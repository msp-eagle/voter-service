package com.example.regclient_newVersion.Service.impl;

import com.example.regclient_newVersion.dto.ConnectionHealthDTO;
import com.example.regclient_newVersion.dto.TableInfoDTO;
import com.example.regclient_newVersion.dto.TransferRequestDTO;
import com.example.regclient_newVersion.dto.TransferResponseDTO;
import com.example.regclient_newVersion.Service.WorkstationTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkstationTransferServiceImpl implements WorkstationTransferService {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Autowired
    public WorkstationTransferServiceImpl(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    @Override
    public ConnectionHealthDTO checkHealth() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result != null && result == 1) {
                return new ConnectionHealthDTO("UP", "Workstation transfer API and Database are active", true);
            }
        } catch (Exception e) {
            return new ConnectionHealthDTO("DOWN", "Database error: " + e.getMessage(), false);
        }
        return new ConnectionHealthDTO("DOWN", "Workstation database check failed", false);
    }

    @Override
    public List<TableInfoDTO> getAvailableTables() {
        List<TableInfoDTO> tables = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String[] types = {"TABLE"};
            try (ResultSet rs = metaData.getTables(null, null, "%", types)) {
                while (rs.next()) {
                    String schema = rs.getString("TABLE_SCHEM");
                    String tableName = rs.getString("TABLE_NAME");

                    // Filter for application relevant schemas/tables
                    if (schema != null && (schema.equalsIgnoreCase("public") || schema.equalsIgnoreCase("master"))) {
                        if (!tableName.startsWith("pg_") && !tableName.startsWith("sql_") && !tableName.startsWith("archived_")) {
                            long count = 0;
                            try {
                                String fullTable = getFullTableName(schema, tableName);
                                Long cnt = jdbcTemplate.queryForObject("SELECT count(*) FROM " + fullTable, Long.class);
                                count = cnt != null ? cnt : 0;
                            } catch (Exception ignored) {
                            }
                            tables.add(new TableInfoDTO(tableName, schema, count));
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Fallback to explicit query if metadata extraction fails
            try {
                List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                        "SELECT table_schema, table_name FROM information_schema.tables WHERE table_schema IN ('public', 'master') AND table_type = 'BASE TABLE'");
                for (Map<String, Object> row : rows) {
                    String schema = (String) row.get("table_schema");
                    String tableName = (String) row.get("table_name");
                    long count = 0;
                    try {
                        String fullTable = getFullTableName(schema, tableName);
                        Long cnt = jdbcTemplate.queryForObject("SELECT count(*) FROM " + fullTable, Long.class);
                        count = cnt != null ? cnt : 0;
                    } catch (Exception ignored) {
                    }
                    tables.add(new TableInfoDTO(tableName, schema, count));
                }
            } catch (Exception ex) {
                System.err.println("Error listing tables: " + ex.getMessage());
            }
        }

        // Sort tables by schema then name
        tables.sort(Comparator.comparing(TableInfoDTO::getSchemaName).thenComparing(TableInfoDTO::getTableName));
        return tables;
    }

    @Override
    @Transactional
    public TransferResponseDTO processUpload(TransferRequestDTO uploadRequest) {
        if (uploadRequest == null || uploadRequest.getTableName() == null) {
            return new TransferResponseDTO("FAILED", "Invalid upload payload: table name missing", 0);
        }

        String tableName = uploadRequest.getTableName();
        String schemaName = uploadRequest.getSchemaName() != null ? uploadRequest.getSchemaName() : "public";
        List<Map<String, Object>> records = uploadRequest.getRecords();

        if (records == null || records.isEmpty()) {
            return new TransferResponseDTO("SUCCESS", "No records to transfer for table " + tableName, 0);
        }

        String fullTableName = getFullTableName(schemaName, tableName);
        List<String> primaryKeys = getPrimaryKeysForTable(schemaName, tableName);
        Map<String, Integer> columnTypes = getColumnTypes(schemaName, tableName);

        int count = 0;
        for (Map<String, Object> record : records) {
            upsertRecord(fullTableName, record, primaryKeys, columnTypes);
            count++;
        }

        return new TransferResponseDTO("SUCCESS", "Data transferred successfully", count, 0, tableName);
    }

    @Override
    public TransferRequestDTO getDownloadData(String tableName) {
        String schemaName = "public";
        String actualTable = tableName;

        if (tableName != null && tableName.contains(".")) {
            String[] parts = tableName.split("\\.");
            schemaName = parts[0];
            actualTable = parts[1];
        } else {
            // Find schema for table
            List<TableInfoDTO> available = getAvailableTables();
            for (TableInfoDTO t : available) {
                if (t.getTableName().equalsIgnoreCase(tableName)) {
                    schemaName = t.getSchemaName();
                    actualTable = t.getTableName();
                    break;
                }
            }
        }

        String fullTableName = getFullTableName(schemaName, actualTable);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM " + fullTableName);

        // Process byte arrays / binary fields into base64 if needed for JSON safety
        List<Map<String, Object>> processedRows = rows.stream().map(this::processRowForOutput).collect(Collectors.toList());

        return new TransferRequestDTO(actualTable, schemaName, processedRows, processedRows.size(), "WORKSTATION", actualTable);
    }

    private void upsertRecord(String fullTableName, Map<String, Object> record, List<String> primaryKeys, Map<String, Integer> columnTypes) {
        if (record == null || record.isEmpty()) {
            return;
        }

        // Clean up column names and values
        Map<String, Object> cleanRecord = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : record.entrySet()) {
            cleanRecord.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        List<String> columns = new ArrayList<>(cleanRecord.keySet());
        List<Object> values = new ArrayList<>();
        for (String col : columns) {
            Object rawVal = cleanRecord.get(col);
            Integer dataType = columnTypes.get(col.toLowerCase());
            values.add(convertValueForJdbc(rawVal, dataType));
        }

        if (primaryKeys != null && !primaryKeys.isEmpty()) {
            // Build PostgreSQL INSERT ... ON CONFLICT DO UPDATE
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ").append(fullTableName).append(" (");
            sql.append(String.join(", ", columns));
            sql.append(") VALUES (");
            sql.append(columns.stream().map(c -> "?").collect(Collectors.joining(", ")));
            sql.append(") ON CONFLICT (");
            sql.append(String.join(", ", primaryKeys));
            sql.append(") DO UPDATE SET ");

            List<String> updateClause = new ArrayList<>();
            for (String col : columns) {
                if (!primaryKeys.contains(col)) {
                    updateClause.add(col + " = EXCLUDED." + col);
                }
            }

            if (updateClause.isEmpty()) {
                // If table only has primary keys, do update on primary key column to ensure idempotency
                sql.append(columns.get(0)).append(" = EXCLUDED.").append(columns.get(0));
            } else {
                sql.append(String.join(", ", updateClause));
            }

            jdbcTemplate.update(sql.toString(), values.toArray());
        } else {
            // Fallback plain insert
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ").append(fullTableName).append(" (");
            sql.append(String.join(", ", columns));
            sql.append(") VALUES (");
            sql.append(columns.stream().map(c -> "?").collect(Collectors.joining(", ")));
            sql.append(")");

            jdbcTemplate.update(sql.toString(), values.toArray());
        }
    }

    private Map<String, Integer> getColumnTypes(String schema, String table) {
        Map<String, Integer> columnTypes = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getColumns(null, schema, table, null)) {
                while (rs.next()) {
                    String colName = rs.getString("COLUMN_NAME").toLowerCase();
                    int dataType = rs.getInt("DATA_TYPE");
                    columnTypes.put(colName, dataType);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting column types for " + schema + "." + table + ": " + e.getMessage());
        }
        return columnTypes;
    }

    private Object convertValueForJdbc(Object val, Integer dataType) {
        if (val == null) {
            return null;
        }

        if (dataType != null && (dataType == java.sql.Types.TIMESTAMP || dataType == java.sql.Types.TIMESTAMP_WITH_TIMEZONE || dataType == java.sql.Types.DATE)) {
            if (val instanceof String) {
                String str = (String) val;
                String cleanStr = str.replace("T", " ").replace("Z", "");
                try {
                    if (cleanStr.length() == 10) {
                        return java.sql.Date.valueOf(cleanStr);
                    } else if (cleanStr.length() >= 19) {
                        return java.sql.Timestamp.valueOf(cleanStr.substring(0, 19));
                    }
                } catch (Exception ignored) {
                }
            } else if (val instanceof Number) {
                return new java.sql.Timestamp(((Number) val).longValue());
            }
        }

        if (val instanceof String) {
            String str = (String) val;
            if (str.length() >= 19 && str.contains("-") && (str.contains("T") || str.contains(" ")) && str.contains(":")) {
                try {
                    String cleanStr = str.replace("T", " ").replace("Z", "");
                    if (cleanStr.length() >= 19) {
                        return java.sql.Timestamp.valueOf(cleanStr.substring(0, 19));
                    }
                } catch (Exception ignored) {
                }
            }
        }

        if (dataType != null && (dataType == java.sql.Types.BINARY || dataType == java.sql.Types.VARBINARY || dataType == java.sql.Types.LONGVARBINARY)) {
            if (val instanceof String) {
                try {
                    return Base64.getDecoder().decode((String) val);
                } catch (Exception ignored) {
                    return ((String) val).getBytes();
                }
            }
        }

        if (dataType != null && (dataType == java.sql.Types.BOOLEAN || dataType == java.sql.Types.BIT)) {
            if (val instanceof String) {
                return Boolean.parseBoolean((String) val);
            }
        }

        if (dataType != null && (dataType == java.sql.Types.INTEGER || dataType == java.sql.Types.SMALLINT || dataType == java.sql.Types.TINYINT)) {
            if (val instanceof Number) {
                return ((Number) val).intValue();
            } else if (val instanceof String) {
                try {
                    return Integer.parseInt((String) val);
                } catch (Exception ignored) {}
            }
        }

        if (dataType != null && dataType == java.sql.Types.BIGINT) {
            if (val instanceof Number) {
                return ((Number) val).longValue();
            } else if (val instanceof String) {
                try {
                    return Long.parseLong((String) val);
                } catch (Exception ignored) {}
            }
        }

        return val;
    }

    private List<String> getPrimaryKeysForTable(String schema, String table) {
        List<String> pkColumns = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getPrimaryKeys(null, schema, table)) {
                while (rs.next()) {
                    pkColumns.add(rs.getString("COLUMN_NAME").toLowerCase());
                }
            }
        } catch (Exception ignored) {
        }

        if (pkColumns.isEmpty()) {
            // Hardcode known primary keys for domain models if metadata PK search missed
            String lowerTable = table.toLowerCase();
            if (lowerTable.equals("voter_reg_details") || lowerTable.equals("biometric_details")) {
                pkColumns.add("registration_id");
            } else if (lowerTable.equals("doc_type") || lowerTable.equals("location")) {
                pkColumns.add("code");
            } else if (lowerTable.equals("loc_hierarchy_list")) {
                pkColumns.add("hierarchy_level");
                pkColumns.add("lang_code");
            } else if (lowerTable.equals("transfer_history")) {
                pkColumns.add("id");
            }
        }
        return pkColumns;
    }

    private String getFullTableName(String schema, String table) {
        if (schema != null && !schema.trim().isEmpty() && !"public".equalsIgnoreCase(schema)) {
            return schema + "." + table;
        }
        return table;
    }

    private Map<String, Object> processRowForOutput(Map<String, Object> row) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            Object val = entry.getValue();
            if (val instanceof byte[]) {
                // Encode byte[] to Base64 string for JSON safety
                map.put(entry.getKey(), Base64.getEncoder().encodeToString((byte[]) val));
            } else {
                map.put(entry.getKey(), val);
            }
        }
        return map;
    }
}
