package com.example.regclient_newVersion.Service.impl;

import com.example.regclient_newVersion.dto.ConnectionHealthDTO;
import com.example.regclient_newVersion.dto.TableInfoDTO;
import com.example.regclient_newVersion.dto.TransferRequestDTO;
import com.example.regclient_newVersion.dto.TransferResponseDTO;
import com.example.regclient_newVersion.Model.TransferHistory;
import com.example.regclient_newVersion.repository.TransferHistoryRepository;
import com.example.regclient_newVersion.Service.LocalTransferService;
import com.example.regclient_newVersion.Service.WorkstationApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocalTransferServiceImpl implements LocalTransferService {

    private final WorkstationApiClient workstationApiClient;
    private final TransferHistoryRepository transferHistoryRepository;
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Value("${spring.datasource.username:postgres}")
    private String localDbUsername;

    @Value("${spring.datasource.password:root}")
    private String localDbPassword;

    @Value("${spring.datasource.url1:jdbc:postgresql://192.168.1.232:5432/voter_reg}")
    private String workstationUrl1;

    @Value("${spring.datasource.username1:postgres}")
    private String workstationUsername1;

    @Value("${spring.datasource.password1:Msp@321#}")
    private String workstationPassword1;

    @Autowired
    public LocalTransferServiceImpl(WorkstationApiClient workstationApiClient,
                                    TransferHistoryRepository transferHistoryRepository,
                                    JdbcTemplate jdbcTemplate,
                                    DataSource dataSource) {
        this.workstationApiClient = workstationApiClient;
        this.transferHistoryRepository = transferHistoryRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    private Connection getWorkstationConnection(String workstationIp) throws SQLException {
        if (workstationIp == null || workstationIp.trim().isEmpty()) {
            throw new IllegalArgumentException("Workstation IP/Host is required");
        }
        String raw = workstationIp.trim();
        if (raw.startsWith("http://")) raw = raw.substring(7);
        if (raw.startsWith("https://")) raw = raw.substring(8);
        if (raw.endsWith("/")) raw = raw.substring(0, raw.length() - 1);

        String host = raw;
        int port = 5432;
        if (raw.contains(":")) {
            String[] parts = raw.split(":");
            host = parts[0];
            try {
                port = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {}
        }

        // Extract target database name from spring.datasource.url1 (default: voter_reg)
        String targetDb = "voter_reg";
        if (workstationUrl1 != null && workstationUrl1.contains("/")) {
            int lastSlash = workstationUrl1.lastIndexOf('/');
            if (lastSlash != -1 && lastSlash < workstationUrl1.length() - 1) {
                targetDb = workstationUrl1.substring(lastSlash + 1);
                if (targetDb.contains("?")) {
                    targetDb = targetDb.substring(0, targetDb.indexOf("?"));
                }
            }
        }

        // Use workstation credentials for remote IPs; fallback to local credentials if localhost
        String user = workstationUsername1 != null ? workstationUsername1 : "postgres";
        String pass = workstationPassword1 != null ? workstationPassword1 : "Msp@321#";
        if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equalsIgnoreCase(host)) {
            user = localDbUsername != null ? localDbUsername : "postgres";
            pass = localDbPassword != null ? localDbPassword : "root";
        }

        String primaryUrl = "jdbc:postgresql://" + host + ":" + port + "/" + targetDb;
        try {
            return DriverManager.getConnection(primaryUrl, user, pass);
        } catch (SQLException e) {
            // Fallback for local dev databases if targetDb does not exist
            if (e.getMessage() != null && e.getMessage().contains("database") && e.getMessage().contains("does not exist")) {
                String fallbackUrl = "jdbc:postgresql://" + host + ":" + port + "/backup_votater";
                try {
                    return DriverManager.getConnection(fallbackUrl, user, pass);
                } catch (SQLException ex) {
                    String defaultUrl = "jdbc:postgresql://" + host + ":" + port + "/postgres";
                    return DriverManager.getConnection(defaultUrl, user, pass);
                }
            }
            throw e;
        }
    }

    @Override
    public ConnectionHealthDTO testConnection(String workstationIp) {
        if (workstationIp == null || workstationIp.trim().isEmpty()) {
            return new ConnectionHealthDTO("DISCONNECTED", "Workstation IP is required", false);
        }
        try (Connection conn = getWorkstationConnection(workstationIp);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {
            if (rs.next()) {
                return new ConnectionHealthDTO("CONNECTED", "Direct PostgreSQL connection active at " + workstationIp, true);
            }
        } catch (Exception e) {
            return new ConnectionHealthDTO("DISCONNECTED", "Workstation PostgreSQL connection failed: " + e.getMessage(), false);
        }
        return new ConnectionHealthDTO("DISCONNECTED", "Workstation PostgreSQL database check failed", false);
    }

    @Override
    public List<TableInfoDTO> getLocalTables() {
        List<TableInfoDTO> tables = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String[] types = {"TABLE"};
            try (ResultSet rs = metaData.getTables(null, null, "%", types)) {
                while (rs.next()) {
                    String schema = rs.getString("TABLE_SCHEM");
                    String tableName = rs.getString("TABLE_NAME");

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
                System.err.println("Error reading local tables: " + ex.getMessage());
            }
        }
        tables.sort(Comparator.comparing(TableInfoDTO::getSchemaName).thenComparing(TableInfoDTO::getTableName));
        return tables;
    }

    @Override
    public List<TableInfoDTO> getWorkstationTables(String workstationIp) {
        List<TableInfoDTO> tables = new ArrayList<>();
        try (Connection conn = getWorkstationConnection(workstationIp)) {
            DatabaseMetaData metaData = conn.getMetaData();
            String[] types = {"TABLE"};
            try (ResultSet rs = metaData.getTables(null, null, "%", types)) {
                while (rs.next()) {
                    String schema = rs.getString("TABLE_SCHEM");
                    String tableName = rs.getString("TABLE_NAME");

                    if (schema != null && (schema.equalsIgnoreCase("public") || schema.equalsIgnoreCase("master"))) {
                        if (!tableName.startsWith("pg_") && !tableName.startsWith("sql_") && !tableName.startsWith("archived_")) {
                            long count = 0;
                            try (Statement stmt = conn.createStatement();
                                 ResultSet countRs = stmt.executeQuery("SELECT count(*) FROM " + getFullTableName(schema, tableName))) {
                                if (countRs.next()) {
                                    count = countRs.getLong(1);
                                }
                            } catch (Exception ignored) {
                            }
                            tables.add(new TableInfoDTO(tableName, schema, count));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error listing workstation PostgreSQL tables: " + e.getMessage());
        }
        tables.sort(Comparator.comparing(TableInfoDTO::getSchemaName).thenComparing(TableInfoDTO::getTableName));
        return tables;
    }

    @Override
    public TransferResponseDTO performUpload(String workstationIp, String tableName) {
        if (tableName == null || tableName.trim().isEmpty() || "ALL".equalsIgnoreCase(tableName.trim())) {
            List<TableInfoDTO> localTables = getLocalTables();
            Set<String> processedTables = new HashSet<>();
            int totalTransferred = 0;
            StringBuilder summaryMsg = new StringBuilder();
            boolean anyProcessed = false;

            for (TableInfoDTO t : localTables) {
                String tbl = t.getTableName();
                if (!processedTables.contains(tbl.toLowerCase())) {
                    processedTables.add(tbl.toLowerCase());
                    TransferResponseDTO res = performUploadForSingleTable(workstationIp, tbl);
                    if ("SUCCESS".equalsIgnoreCase(res.getStatus()) && res.getRecordsTransferred() > 0) {
                        totalTransferred += res.getRecordsTransferred();
                        summaryMsg.append(tbl).append(": ").append(res.getRecordsTransferred()).append(" records; ");
                        anyProcessed = true;
                    }
                }
            }
            if (!anyProcessed) {
                return new TransferResponseDTO("SUCCESS", "No records found in local tables to upload.", 0);
            }
            return new TransferResponseDTO("SUCCESS", "Batch upload completed: " + summaryMsg.toString(), totalTransferred, 0, "ALL_TABLES");
        }
        return performUploadForSingleTable(workstationIp, tableName);
    }

    private TransferResponseDTO performUploadForSingleTable(String workstationIp, String tableName) {
        ConnectionHealthDTO health = testConnection(workstationIp);
        if (!"CONNECTED".equalsIgnoreCase(health.getStatus()) && !"UP".equalsIgnoreCase(health.getStatus())) {
            recordAudit("LOCAL", "WORKSTATION", workstationIp, "UPLOAD", tableName, "FAILED", 0, "Connection check failed: " + health.getMessage());
            return new TransferResponseDTO("FAILED", "Workstation PostgreSQL unreachable: " + health.getMessage(), 0);
        }

        String schemaName = findSchemaForTable(tableName);
        String fullTableName = getFullTableName(schemaName, tableName);

        List<Map<String, Object>> localRecords;
        try {
            localRecords = jdbcTemplate.queryForList("SELECT * FROM " + fullTableName);
        } catch (Exception e) {
            String errorMsg = "Error reading local table " + tableName + ": " + e.getMessage();
            recordAudit("LOCAL", "WORKSTATION", workstationIp, "UPLOAD", tableName, "FAILED", 0, errorMsg);
            return new TransferResponseDTO("FAILED", errorMsg, 0);
        }

        // If local active table is empty, check if archived table has records and restore to active local table
        if (localRecords.isEmpty()) {
            String archiveTableName = getFullTableName(schemaName, "archived_" + tableName);
            try {
                List<Map<String, Object>> archivedRecords = jdbcTemplate.queryForList("SELECT * FROM " + archiveTableName);
                if (!archivedRecords.isEmpty()) {
                    List<String> pks = getPrimaryKeysForTable(schemaName, tableName);
                    saveDownloadedRecordsToLocalDb(schemaName, tableName, fullTableName, archivedRecords, pks);
                    localRecords = jdbcTemplate.queryForList("SELECT * FROM " + fullTableName);
                }
            } catch (Exception ignored) {}
        }

        if (localRecords.isEmpty()) {
            recordAudit("LOCAL", "WORKSTATION", workstationIp, "UPLOAD", tableName, "SUCCESS", 0, "No records to transfer");
            return new TransferResponseDTO("SUCCESS", "No local records found in " + tableName + " to upload.", 0);
        }

        int transferred = 0;
        try (Connection wsConn = getWorkstationConnection(workstationIp)) {
            wsConn.setAutoCommit(false);
            try {
                List<String> primaryKeys = getPrimaryKeysForConnection(wsConn, schemaName, tableName);
                Map<String, Integer> columnTypes = getColumnTypesForConnection(wsConn, schemaName, tableName);

                for (Map<String, Object> record : localRecords) {
                    upsertRecordToConnection(wsConn, fullTableName, record, primaryKeys, columnTypes);
                    transferred++;
                }
                wsConn.commit();
            } catch (Exception ex) {
                wsConn.rollback();
                throw ex;
            }
        } catch (Exception e) {
            String errorMsg = "Direct JDBC upload to Workstation PostgreSQL failed: " + e.getMessage();
            recordAudit("LOCAL", "WORKSTATION", workstationIp, "UPLOAD", tableName, "FAILED", 0, errorMsg);
            return new TransferResponseDTO("FAILED", errorMsg, 0);
        }

        // Local data is retained intact in Local DB as requested
        recordAudit("LOCAL", "WORKSTATION", workstationIp, "UPLOAD", tableName, "SUCCESS", transferred, "Upload successful. Local data retained.");
        return new TransferResponseDTO("SUCCESS", "Data transferred to Workstation PostgreSQL successfully and retained in Local DB.", transferred, 0, tableName);
    }

    @Override
    public TransferResponseDTO performDownload(String workstationIp, String tableName) {
        ConnectionHealthDTO health = testConnection(workstationIp);
        if (!"CONNECTED".equalsIgnoreCase(health.getStatus()) && !"UP".equalsIgnoreCase(health.getStatus())) {
            recordAudit("WORKSTATION", "LOCAL", workstationIp, "DOWNLOAD", tableName, "FAILED", 0, "Connection check failed: " + health.getMessage());
            return new TransferResponseDTO("FAILED", "Workstation PostgreSQL unreachable: " + health.getMessage(), 0);
        }

        String schemaName = "public";
        String actualTable = tableName;

        List<TableInfoDTO> wsTables = getWorkstationTables(workstationIp);
        for (TableInfoDTO t : wsTables) {
            if (t.getTableName().equalsIgnoreCase(tableName)) {
                schemaName = t.getSchemaName();
                actualTable = t.getTableName();
                break;
            }
        }
        String fullTableName = getFullTableName(schemaName, actualTable);

        List<Map<String, Object>> wsRecords = new ArrayList<>();
        try (Connection wsConn = getWorkstationConnection(workstationIp);
             Statement stmt = wsConn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + fullTableName)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                wsRecords.add(row);
            }
        } catch (Exception e) {
            String errorMsg = "Direct JDBC download from Workstation PostgreSQL failed: " + e.getMessage();
            recordAudit("WORKSTATION", "LOCAL", workstationIp, "DOWNLOAD", tableName, "FAILED", 0, errorMsg);
            return new TransferResponseDTO("FAILED", errorMsg, 0);
        }

        if (wsRecords.isEmpty()) {
            recordAudit("WORKSTATION", "LOCAL", workstationIp, "DOWNLOAD", tableName, "SUCCESS", 0, "No records found on Workstation");
            return new TransferResponseDTO("SUCCESS", "No records found on Workstation PostgreSQL for " + tableName, 0);
        }

        List<String> primaryKeys = getPrimaryKeysForTable(schemaName, actualTable);
        try {
            int recordsSaved = saveDownloadedRecordsToLocalDb(schemaName, actualTable, fullTableName, wsRecords, primaryKeys);
            recordAudit("WORKSTATION", "LOCAL", workstationIp, "DOWNLOAD", tableName, "SUCCESS", recordsSaved, "Download successful");
            return new TransferResponseDTO("SUCCESS", "Data downloaded from Workstation PostgreSQL into Local DB successfully", recordsSaved, 0, tableName);
        } catch (Exception e) {
            String errorMsg = "Failed to store downloaded records into Local DB: " + e.getMessage();
            recordAudit("WORKSTATION", "LOCAL", workstationIp, "DOWNLOAD", tableName, "FAILED", 0, errorMsg);
            return new TransferResponseDTO("FAILED", errorMsg, 0);
        }
    }

    @Override
    public List<TransferHistory> getTransferHistory() {
        return transferHistoryRepository.findAllByOrderByCreatedAtDesc();
    }

    private void upsertRecordToConnection(Connection conn, String fullTableName, Map<String, Object> record, List<String> primaryKeys, Map<String, Integer> columnTypes) throws SQLException {
        if (record == null || record.isEmpty()) return;

        Map<String, Object> cleanRecord = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : record.entrySet()) {
            cleanRecord.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        List<String> columns = new ArrayList<>();
        for (String col : cleanRecord.keySet()) {
            if (columnTypes != null && !columnTypes.isEmpty()) {
                if (columnTypes.containsKey(col.toLowerCase())) {
                    columns.add(col.toLowerCase());
                }
            } else {
                columns.add(col.toLowerCase());
            }
        }
        if (columns.isEmpty()) return;

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(fullTableName).append(" (");
        sql.append(String.join(", ", columns));
        sql.append(") VALUES (");
        sql.append(columns.stream().map(c -> "?").collect(Collectors.joining(", ")));

        if (primaryKeys != null && !primaryKeys.isEmpty()) {
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
                sql.append(columns.get(0)).append(" = EXCLUDED.").append(columns.get(0));
            } else {
                sql.append(String.join(", ", updateClause));
            }
        } else {
            sql.append(")");
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < columns.size(); i++) {
                String col = columns.get(i);
                Object rawVal = cleanRecord.get(col);
                Integer dataType = columnTypes.get(col.toLowerCase());
                Object converted = convertValueForJdbc(rawVal, dataType);
                pstmt.setObject(i + 1, converted);
            }
            pstmt.executeUpdate();
        }
    }

    private List<String> getPrimaryKeysForConnection(Connection conn, String schema, String table) {
        List<String> pkColumns = new ArrayList<>();
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getPrimaryKeys(null, schema, table)) {
                while (rs.next()) {
                    pkColumns.add(rs.getString("COLUMN_NAME").toLowerCase());
                }
            }
        } catch (Exception ignored) {}

        if (pkColumns.isEmpty()) {
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

    private Map<String, Integer> getColumnTypesForConnection(Connection conn, String schema, String table) {
        Map<String, Integer> columnTypes = new HashMap<>();
        try {
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

    @Transactional
    protected void deleteLocalTransferredData(String schema, String table, List<Map<String, Object>> records) {
        String fullTableName = getFullTableName(schema, table);
        String archiveTableName = getFullTableName(schema, "archived_" + table);
        List<String> primaryKeys = getPrimaryKeysForTable(schema, table);
        Map<String, Integer> columnTypes = getColumnTypes(schema, table);

        // 1. Ensure Archive Table Exists
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + archiveTableName + " (LIKE " + fullTableName + ")");
        } catch (Exception e) {
            try {
                jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + archiveTableName + " AS SELECT * FROM " + fullTableName + " WHERE 1=0");
            } catch (Exception ignored) {}
        }

        // 2. Save records into Archive Table
        try {
            saveDownloadedRecordsToLocalDb(schema, "archived_" + table, archiveTableName, records, primaryKeys);
        } catch (Exception e) {
            System.err.println("Archiving records to " + archiveTableName + " failed: " + e.getMessage());
        }

        // 3. Delete records from Active Local Table
        if (!primaryKeys.isEmpty()) {
            for (Map<String, Object> record : records) {
                StringBuilder sql = new StringBuilder("DELETE FROM ").append(fullTableName).append(" WHERE ");
                List<Object> params = new ArrayList<>();
                List<String> whereClauses = new ArrayList<>();

                for (String pk : primaryKeys) {
                    Object val = null;
                    for (Map.Entry<String, Object> entry : record.entrySet()) {
                        if (entry.getKey().equalsIgnoreCase(pk)) {
                            val = entry.getValue();
                            break;
                        }
                    }
                    if (val != null) {
                        whereClauses.add(pk + " = ?");
                        Integer dataType = columnTypes.get(pk.toLowerCase());
                        params.add(convertValueForJdbc(val, dataType));
                    }
                }

                if (!whereClauses.isEmpty()) {
                    sql.append(String.join(" AND ", whereClauses));
                    jdbcTemplate.update(sql.toString(), params.toArray());
                }
            }
        } else {
            jdbcTemplate.update("DELETE FROM " + fullTableName);
        }
    }

    @Transactional
    protected int saveDownloadedRecordsToLocalDb(String schemaName, String tableName, String fullTableName, List<Map<String, Object>> records, List<String> primaryKeys) {
        int count = 0;
        Map<String, Integer> columnTypes = getColumnTypes(schemaName, tableName);

        for (Map<String, Object> record : records) {
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
                    sql.append(columns.get(0)).append(" = EXCLUDED.").append(columns.get(0));
                } else {
                    sql.append(String.join(", ", updateClause));
                }

                jdbcTemplate.update(sql.toString(), values.toArray());
            } else {
                StringBuilder sql = new StringBuilder();
                sql.append("INSERT INTO ").append(fullTableName).append(" (");
                sql.append(String.join(", ", columns));
                sql.append(") VALUES (");
                sql.append(columns.stream().map(c -> "?").collect(Collectors.joining(", ")));
                sql.append(")");

                jdbcTemplate.update(sql.toString(), values.toArray());
            }
            count++;
        }
        return count;
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

    private void recordAudit(String source, String destination, String workstationIp, String operation,
                             String tableName, String status, int count, String errorMsg) {
        try {
            TransferHistory audit = new TransferHistory();
            audit.setSource(source);
            audit.setDestination(destination);
            audit.setWorkstationIp(workstationIp);
            audit.setOperation(operation);
            audit.setTableName(tableName);
            audit.setRecordIdentifier(tableName + "-" + System.currentTimeMillis());
            audit.setStatus(status);
            audit.setRecordsTransferred(count);
            audit.setErrorMessage(errorMsg);
            audit.setCompletedAt(LocalDateTime.now());
            transferHistoryRepository.save(audit);
        } catch (Exception e) {
            System.err.println("Failed to record transfer audit log: " + e.getMessage());
        }
    }

    private String findSchemaForTable(String tableName) {
        List<TableInfoDTO> tables = getLocalTables();
        for (TableInfoDTO t : tables) {
            if (t.getTableName().equalsIgnoreCase(tableName)) {
                return t.getSchemaName();
            }
        }
        return "public";
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
}
