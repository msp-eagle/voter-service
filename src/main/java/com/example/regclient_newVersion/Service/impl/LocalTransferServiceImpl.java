package com.example.regclient_newVersion.Service.impl;

import com.example.regclient_newVersion.Model.BiometricDetails;
import com.example.regclient_newVersion.Model.Registration;
import com.example.regclient_newVersion.dataMigration.entity.BiometricDetailsServer;
import com.example.regclient_newVersion.dataMigration.entity.VoterRegDetailsServer;
import com.example.regclient_newVersion.dataMigration.repository.BiometricDetailsServerRepo;
import com.example.regclient_newVersion.dataMigration.repository.VoterRegDetailsServerRepo;
import com.example.regclient_newVersion.dto.ConnectionHealthDTO;
import com.example.regclient_newVersion.dto.TableInfoDTO;
import com.example.regclient_newVersion.dto.TransferRequestDTO;
import com.example.regclient_newVersion.dto.TransferResponseDTO;
import com.example.regclient_newVersion.Model.TransferHistory;
import com.example.regclient_newVersion.repository.BiometricDetailsRepository;
import com.example.regclient_newVersion.repository.RegistrationRepository;
import com.example.regclient_newVersion.repository.TransferHistoryRepository;
import com.example.regclient_newVersion.Service.LocalTransferService;
import com.example.regclient_newVersion.Service.WorkstationApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
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

    @Value("${spring.datasource.url2:jdbc:postgresql://192.168.1.232:5432/applicants}")
    private String workstationUrl2;

    @Value("${spring.datasource.username2:postgres}")
    private String workstationUsername2;

    @Value("${spring.datasource.password2:Msp@321#}")
    private String workstationPassword2;

    @Value("${spring.datasource.url3:jdbc:postgresql://localhost:5433/applicants}")
    private String localApplicantsUrl;

    @Value("${spring.datasource.username3:postgres}")
    private String localApplicantsUsername;

    @Value("${spring.datasource.password3:root}")
    private String localApplicantsPassword;

    @Autowired
    private BiometricDetailsRepository biometricDetailsRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private BiometricDetailsServerRepo biometricDetailsServerRepo;

    @Autowired
    private VoterRegDetailsServerRepo voterRegDetailsServerRepo;



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

    private Connection getLocalDestinationConnection(String tableName) throws SQLException {
        if (localApplicantsUrl != null && !localApplicantsUrl.trim().isEmpty()) {
            String user = (localApplicantsUsername != null && !localApplicantsUsername.trim().isEmpty())
                    ? localApplicantsUsername.trim() : "postgres";
            String pass = (localApplicantsPassword != null && !localApplicantsPassword.trim().isEmpty())
                    ? localApplicantsPassword.trim() : "root";
            try {
                return DriverManager.getConnection(localApplicantsUrl.trim(), user, pass);
            } catch (SQLException e) {
                System.err.println("Note: Connecting to local applicants DB (" + localApplicantsUrl + ") failed: " + e.getMessage() + ". Falling back to default DataSource.");
            }
        }
        return dataSource.getConnection();
    }

    private Connection getWorkstationConnection(String workstationIp) throws SQLException {
        String user = (workstationUsername2 != null && !workstationUsername2.trim().isEmpty())
                ? workstationUsername2.trim()
                : (workstationUsername1 != null ? workstationUsername1.trim() : "postgres");
        String pass = (workstationPassword2 != null && !workstationPassword2.trim().isEmpty())
                ? workstationPassword2.trim()
                : (workstationPassword1 != null ? workstationPassword1.trim() : "Msp@321#");

        // 1. Direct use of workstationUrl2 if configured
        if (workstationUrl2 != null && !workstationUrl2.trim().isEmpty()) {
            if (workstationIp == null || workstationIp.trim().isEmpty() || "192.168.1.232".equalsIgnoreCase(workstationIp.trim())) {
                try {
                    return DriverManager.getConnection(workstationUrl2.trim(), user, pass);
                } catch (SQLException e) {
                    System.err.println("Note: connecting directly with workstationUrl2 (" + workstationUrl2 + ") failed: " + e.getMessage());
                }
            }
        }

        String host = "192.168.1.232";
        int port = 5432;
        String targetDb = "applicants";

        // Prioritize spring.datasource.url2 (applicants database) for workstation data
        String configUrl = (workstationUrl2 != null && !workstationUrl2.trim().isEmpty()) ? workstationUrl2 : workstationUrl1;
        if (configUrl != null && configUrl.contains("://")) {
            String afterScheme = configUrl.substring(configUrl.indexOf("://") + 3);
            if (afterScheme.contains("/")) {
                String hostPort = afterScheme.substring(0, afterScheme.indexOf('/'));
                if (hostPort.contains(":")) {
                    host = hostPort.split(":")[0];
                    try {
                        port = Integer.parseInt(hostPort.split(":")[1]);
                    } catch (NumberFormatException ignored) {}
                } else {
                    host = hostPort;
                }
                String dbPart = afterScheme.substring(afterScheme.indexOf('/') + 1);
                if (dbPart.contains("?")) {
                    dbPart = dbPart.substring(0, dbPart.indexOf('?'));
                }
                if (!dbPart.trim().isEmpty()) {
                    targetDb = dbPart.trim();
                }
            }
        }

        if (workstationIp != null && !workstationIp.trim().isEmpty()) {
            String raw = workstationIp.trim();
            if (raw.startsWith("http://")) raw = raw.substring(7);
            if (raw.startsWith("https://")) raw = raw.substring(8);
            if (raw.endsWith("/")) raw = raw.substring(0, raw.length() - 1);
            if (raw.contains(":")) {
                String[] parts = raw.split(":");
                host = parts[0];
                try {
                    port = Integer.parseInt(parts[1]);
                } catch (NumberFormatException ignored) {}
            } else {
                host = raw;
            }
        }

        if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equalsIgnoreCase(host)) {
            user = localDbUsername != null ? localDbUsername : "postgres";
            pass = localDbPassword != null ? localDbPassword : "root";
        }

        String primaryUrl = "jdbc:postgresql://" + host + ":" + port + "/" + targetDb + "?currentSchema=public";
        try {
            return DriverManager.getConnection(primaryUrl, user, pass);
        } catch (SQLException e) {
            String fallbackDb = "applicants".equalsIgnoreCase(targetDb) ? "voter_reg" : "applicants";
            String fallbackUrl = "jdbc:postgresql://" + host + ":" + port + "/" + fallbackDb;
            try {
                return DriverManager.getConnection(fallbackUrl, user, pass);
            } catch (SQLException ex) {
                if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equalsIgnoreCase(host)) {
                    String localFallback = "jdbc:postgresql://" + host + ":" + port + "/backup_votater";
                    try {
                        return DriverManager.getConnection(localFallback, user, pass);
                    } catch (SQLException ex2) {
                        return DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/postgres", user, pass);
                    }
                }
                throw e;
            }
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

    public BiometricDetailsServer convertToServerBioEntity(BiometricDetails source) {

        if (source == null) {
            return null;
        }

        BiometricDetailsServer target = new BiometricDetailsServer();

        target.setRegistrationId(source.getRegistrationId());

        // Face
        target.setFace(source.getFace());

        // Iris
        target.setLeftIris(source.getLeftIris());
        target.setRightIris(source.getRightIris());

        // Left hand fingerprints
        target.setLeftThumb(source.getLeftThumb());
        target.setLeftIndexFinger(source.getLeftIndexFinger());
        target.setLeftMiddleFinger(source.getLeftMiddleFinger());
        target.setLeftRingFinger(source.getLeftRingFinger());
        target.setLeftLittleFinger(source.getLeftLittleFinger());

        // Right hand fingerprints
        target.setRightThumb(source.getRightThumb());
        target.setRightIndexFinger(source.getRightIndexFinger());
        target.setRightMiddleFinger(source.getRightMiddleFinger());
        target.setRightRingFinger(source.getRightRingFinger());
        target.setRightLittleFinger(source.getRightLittleFinger());

        // Other fields
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());
        target.setVoterId(source.getVoterId());
        target.setStatus(source.getStatus());

        return target;
    }

    private VoterRegDetailsServer convertToServerDemoEntity(Registration source) {

        if (source == null) {
            return null;
        }

        VoterRegDetailsServer target = new VoterRegDetailsServer();

        // Registration ID
        target.setRegistrationId(source.getRegistrationId());

        // Demographic data
        target.setDemographicData(source.getDemographicData());

        // Documents data
        target.setDocumentsData(source.getDocumentsData());

        // Voter ID
        target.setVid(source.getVid());

        // Created timestamp
        target.setCreatedAt(source.getCreatedAt());

        // Updated timestamp
        target.setUpdatedAt(source.getUpdatedAt());

        return target;
    }
    @Modifying
    @Transactional
    @Override
    public TransferResponseDTO performUpload(String workstationIp, String tableName) {

       List<BiometricDetails> bioList =  biometricDetailsRepository.findAllByStatus("NEW");
       List<Registration> voterList =  registrationRepository.findAllByStatus("NEW");

       List<BiometricDetailsServer> copyBio = new ArrayList<>();
        List<VoterRegDetailsServer> copyDemo = new ArrayList<>();

        bioList.forEach(e-> {
            copyBio.add(convertToServerBioEntity(e));
       });
        voterList.forEach(e-> {
            copyDemo.add(convertToServerDemoEntity(e));
        });

        biometricDetailsServerRepo.saveAll(copyBio);


        voterRegDetailsServerRepo.saveAll(copyDemo);

        bioList.forEach(e -> e.setStatus("UPLOADED"));


        biometricDetailsRepository.saveAll(bioList);

        biometricDetailsServerRepo.flush();
        voterRegDetailsServerRepo.flush();





     /*   if (tableName == null || tableName.trim().isEmpty() || "ALL".equalsIgnoreCase(tableName.trim())) {
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
        return performUploadForSingleTable(workstationIp, tableName);*/
        return new TransferResponseDTO("SUCCESS","Uploaded voter data to server",bioList.size());
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

        // Determine Workstation schema and ensure table exists on Workstation DB
        String wsSchema = "public";
        try (Connection checkConn = getWorkstationConnection(workstationIp)) {
            try {
                DatabaseMetaData wsMeta = checkConn.getMetaData();
                try (ResultSet rs = wsMeta.getTables(null, null, "%", new String[]{"TABLE"})) {
                    while (rs.next()) {
                        String tName = rs.getString("TABLE_NAME");
                        if (tName != null && tName.equalsIgnoreCase(tableName)) {
                            wsSchema = rs.getString("TABLE_SCHEM");
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {}
            if (wsSchema == null) wsSchema = "public";

            String wsFullTableName = getFullTableName(wsSchema, tableName);
            if (!localRecords.isEmpty()) {
                StringBuilder createSql = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(wsFullTableName).append(" (");
                List<String> colDefs = new ArrayList<>();
                Map<String, Object> firstRow = localRecords.get(0);
                for (String col : firstRow.keySet()) {
                    colDefs.add(col.toLowerCase() + " TEXT");
                }
                createSql.append(String.join(", ", colDefs)).append(")");
                try (Statement stmt = checkConn.createStatement()) {
                    stmt.execute(createSql.toString());
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        String wsFullTableName = getFullTableName(wsSchema, tableName);

        int transferred = 0;
        try (Connection wsConn = getWorkstationConnection(workstationIp)) {
            wsConn.setAutoCommit(false);
            try {
                List<String> primaryKeys = getPrimaryKeysForConnection(wsConn, wsSchema, tableName);
                Map<String, Integer> columnTypes = getColumnTypesForConnection(wsConn, wsSchema, tableName);

                for (Map<String, Object> record : localRecords) {
                    upsertRecordToConnection(wsConn, wsFullTableName, record, primaryKeys, columnTypes);
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

        String targetTable = (tableName != null && !tableName.trim().isEmpty()) ? tableName.trim() : "doctable";
        String schemaName = "public";
        String actualTable = targetTable;

        String querySql;
        if ("doctable".equalsIgnoreCase(targetTable) || "public.doctable".equalsIgnoreCase(targetTable)) {
            querySql = "select \"ID\",\"FIRSTNAME\",\"LASTNAME\",\"SEX\",\"DOBYEAR\",\"DOBMONTH\",\"DOBDAY\" from public.doctable";
            actualTable = "doctable";
        } else {
            List<TableInfoDTO> wsTables = getWorkstationTables(workstationIp);
            for (TableInfoDTO t : wsTables) {
                if (t.getTableName().equalsIgnoreCase(targetTable)) {
                    schemaName = t.getSchemaName();
                    actualTable = t.getTableName();
                    break;
                }
            }
            querySql = "SELECT * FROM " + getFullTableName(schemaName, actualTable);
        }
        String fullTableName = "doctable".equalsIgnoreCase(actualTable) ? "doctable" : getFullTableName(schemaName, actualTable);

        List<Map<String, Object>> wsRecords = new ArrayList<>();
        try (Connection wsConn = getWorkstationConnection(workstationIp);
             Statement stmt = wsConn.createStatement();
             ResultSet rs = stmt.executeQuery(querySql)) {

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
            recordAudit("WORKSTATION", "LOCAL", workstationIp, "DOWNLOAD", actualTable, "FAILED", 0, errorMsg);
            return new TransferResponseDTO("FAILED", errorMsg, 0);
        }

        if (wsRecords.isEmpty()) {
            recordAudit("WORKSTATION", "LOCAL", workstationIp, "DOWNLOAD", actualTable, "SUCCESS", 0, "No records found on Workstation");
            return new TransferResponseDTO("SUCCESS", "No records found on Workstation PostgreSQL for " + actualTable, 0);
        }

        List<String> primaryKeys = getPrimaryKeysForTable(schemaName, actualTable);
        try {
            int recordsSaved = saveDownloadedRecordsToLocalDb(workstationIp, schemaName, actualTable, fullTableName, wsRecords, primaryKeys);
            recordAudit("WORKSTATION", "LOCAL", workstationIp, "DOWNLOAD", actualTable, "SUCCESS", recordsSaved, "Download successful");
            return new TransferResponseDTO("SUCCESS", "Data downloaded from Workstation PostgreSQL into Local DB successfully", recordsSaved, 0, actualTable);
        } catch (Exception e) {
            String errorMsg = "Failed to store downloaded records into Local DB: " + e.getMessage();
            recordAudit("WORKSTATION", "LOCAL", workstationIp, "DOWNLOAD", actualTable, "FAILED", 0, errorMsg);
            return new TransferResponseDTO("FAILED", errorMsg, 0);
        }
    }

    @Override
    public List<Map<String, Object>> getWorkstationRecords(String workstationIp, String tableName) {
        String targetTable = (tableName != null && !tableName.trim().isEmpty()) ? tableName.trim() : "doctable";
        String sql;
        if ("doctable".equalsIgnoreCase(targetTable) || "public.doctable".equalsIgnoreCase(targetTable)) {
            sql = "select \"ID\",\"FIRSTNAME\",\"LASTNAME\",\"SEX\",\"DOBYEAR\",\"DOBMONTH\",\"DOBDAY\" from public.doctable";
        } else {
            String schemaName = "public";
            String actualTable = targetTable;
            List<TableInfoDTO> wsTables = getWorkstationTables(workstationIp);
            for (TableInfoDTO t : wsTables) {
                if (t.getTableName().equalsIgnoreCase(targetTable)) {
                    schemaName = t.getSchemaName();
                    actualTable = t.getTableName();
                    break;
                }
            }
            sql = "SELECT * FROM " + getFullTableName(schemaName, actualTable);
        }

        List<Map<String, Object>> records = new ArrayList<>();
        try (Connection wsConn = getWorkstationConnection(workstationIp);
             Statement stmt = wsConn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    String colName = meta.getColumnName(i);
                    Object val = rs.getObject(i);
                    if (val instanceof byte[]) {
                        row.put(colName, Base64.getEncoder().encodeToString((byte[]) val));
                    } else {
                        row.put(colName, val);
                    }
                }
                records.add(row);
            }
        } catch (Exception e) {
            System.err.println("Error fetching records from Workstation PostgreSQL (" + workstationIp + ", query " + sql + "): " + e.getMessage());
            throw new RuntimeException("Failed to fetch records from Workstation PostgreSQL: " + e.getMessage(), e);
        }

        return records;
    }

    @Override
    public TransferResponseDTO performDownloadSelected(String workstationIp, String tableName, List<String> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return performDownload(workstationIp, tableName);
        }

        ConnectionHealthDTO health = testConnection(workstationIp);
        if (!"CONNECTED".equalsIgnoreCase(health.getStatus()) && !"UP".equalsIgnoreCase(health.getStatus())) {
            recordAudit("WORKSTATION", "LOCAL", workstationIp, "DOWNLOAD_SELECTED", tableName, "FAILED", 0, "Connection check failed: " + health.getMessage());
            return new TransferResponseDTO("FAILED", "Workstation PostgreSQL unreachable: " + health.getMessage(), 0);
        }

        String targetTable = (tableName != null && !tableName.trim().isEmpty()) ? tableName.trim() : "doctable";
        String schemaName = "public";
        String actualTable = targetTable;

        String querySql;
        if ("doctable".equalsIgnoreCase(targetTable) || "public.doctable".equalsIgnoreCase(targetTable)) {
            querySql = "select \"ID\",\"FIRSTNAME\",\"LASTNAME\",\"SEX\",\"DOBYEAR\",\"DOBMONTH\",\"DOBDAY\" from public.doctable";
            actualTable = "doctable";
        } else {
            List<TableInfoDTO> wsTables = getWorkstationTables(workstationIp);
            for (TableInfoDTO t : wsTables) {
                if (t.getTableName().equalsIgnoreCase(targetTable)) {
                    schemaName = t.getSchemaName();
                    actualTable = t.getTableName();
                    break;
                }
            }
            querySql = "SELECT * FROM " + getFullTableName(schemaName, actualTable);
        }
        String fullTableName = "doctable".equalsIgnoreCase(actualTable) ? "doctable" : getFullTableName(schemaName, actualTable);
        List<String> primaryKeys = getPrimaryKeysForTable(schemaName, actualTable);

        List<Map<String, Object>> wsRecords = new ArrayList<>();
        try (Connection wsConn = getWorkstationConnection(workstationIp);
             Statement stmt = wsConn.createStatement();
             ResultSet rs = stmt.executeQuery(querySql)) {

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
            recordAudit("WORKSTATION", "LOCAL", workstationIp, "DOWNLOAD_SELECTED", actualTable, "FAILED", 0, errorMsg);
            return new TransferResponseDTO("FAILED", errorMsg, 0);
        }

        if (wsRecords.isEmpty()) {
            recordAudit("WORKSTATION", "LOCAL", workstationIp, "DOWNLOAD_SELECTED", actualTable, "SUCCESS", 0, "No records found on Workstation");
            return new TransferResponseDTO("SUCCESS", "No records found on Workstation PostgreSQL for " + actualTable, 0);
        }

        // Filter for matching records based on selected recordIds
        Set<String> selectedIdSet = recordIds.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<Map<String, Object>> selectedRecords = new ArrayList<>();
        if (selectedIdSet.contains("all") || selectedIdSet.isEmpty()) {
            selectedRecords.addAll(wsRecords);
        } else {
            for (Map<String, Object> row : wsRecords) {
                boolean isMatch = false;

                // 1. Check primary key values
                for (String pk : primaryKeys) {
                    Object pkVal = getCaseInsensitiveValue(row, pk);
                    if (pkVal != null && selectedIdSet.contains(String.valueOf(pkVal).trim().toLowerCase())) {
                        isMatch = true;
                        break;
                    }
                }

                // 2. Check common identifier column names ("ID", "registration_id", "code", "uin")
                if (!isMatch) {
                    for (Map.Entry<String, Object> entry : row.entrySet()) {
                        String col = entry.getKey().toLowerCase();
                        if (col.equals("id") || col.equals("registration_id") || col.equals("code") || col.equals("uin")) {
                            Object val = entry.getValue();
                            if (val != null && selectedIdSet.contains(String.valueOf(val).trim().toLowerCase())) {
                                isMatch = true;
                                break;
                            }
                        }
                    }
                }

                if (isMatch) {
                    selectedRecords.add(row);
                }
            }
        }

        if (selectedRecords.isEmpty()) {
            return new TransferResponseDTO("FAILED", "None of the selected record IDs matched records in table " + actualTable, 0);
        }

        try {
            int recordsSaved = saveDownloadedRecordsToLocalDb(workstationIp, schemaName, actualTable, fullTableName, selectedRecords, primaryKeys);
            recordAudit("WORKSTATION", "LOCAL", workstationIp, "DOWNLOAD_SELECTED", actualTable, "SUCCESS", recordsSaved, "Selected download successful (" + recordsSaved + " records)");
            return new TransferResponseDTO("SUCCESS", "Selected " + recordsSaved + " records downloaded from Workstation PostgreSQL into Local DB successfully", recordsSaved, 0, actualTable);
        } catch (Exception e) {
            String errorMsg = "Failed to store selected records into Local DB: " + e.getMessage();
            recordAudit("WORKSTATION", "LOCAL", workstationIp, "DOWNLOAD_SELECTED", actualTable, "FAILED", 0, errorMsg);
            return new TransferResponseDTO("FAILED", errorMsg, 0);
        }
    }

    @Override
    public TransferResponseDTO performDownloadSelectedMulti(String workstationIp, List<String> tableNames, List<String> recordIds) {
        if (tableNames == null || tableNames.isEmpty()) {
            return performDownloadSelected(workstationIp, "doctable", recordIds);
        }
        if (tableNames.size() == 1) {
            return performDownloadSelected(workstationIp, tableNames.get(0), recordIds);
        }

        int totalTransferred = 0;
        int totalFailed = 0;
        List<String> successTables = new ArrayList<>();
        List<String> failedTables = new ArrayList<>();
        List<String> details = new ArrayList<>();

        for (String table : tableNames) {
            if (table == null || table.trim().isEmpty()) continue;
            String cleanTable = table.trim();
            try {
                TransferResponseDTO result = performDownloadSelected(workstationIp, cleanTable, recordIds);
                if ("SUCCESS".equalsIgnoreCase(result.getStatus())) {
                    totalTransferred += result.getRecordsTransferred();
                    successTables.add(cleanTable);
                    details.add(cleanTable + " (" + result.getRecordsTransferred() + " records)");
                } else {
                    totalFailed++;
                    failedTables.add(cleanTable + ": " + result.getMessage());
                }
            } catch (Exception e) {
                totalFailed++;
                failedTables.add(cleanTable + ": " + e.getMessage());
            }
        }

        String overallStatus = failedTables.isEmpty() ? "SUCCESS" : (successTables.isEmpty() ? "FAILED" : "PARTIAL_SUCCESS");
        String message = "Downloaded " + successTables.size() + " tables: " + String.join(", ", details);
        if (!failedTables.isEmpty()) {
            message += ". Failures: " + String.join("; ", failedTables);
        }

        return new TransferResponseDTO(overallStatus, message, totalTransferred, totalFailed, String.join(", ", tableNames));
    }

    private Object getCaseInsensitiveValue(Map<String, Object> map, String key) {
        if (map == null || key == null) return null;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public TransferResponseDTO clearAllLocalData() {
        return clearLocalTableData("ALL");
    }

    @Override
    public TransferResponseDTO clearLocalTableData(String tableName) {
        String target = (tableName != null && !tableName.trim().isEmpty()) ? tableName.trim() : "ALL";
        int totalCleared = 0;
        List<String> clearedTables = new ArrayList<>();

        List<String> tablesToClear = new ArrayList<>();
        if ("ALL".equalsIgnoreCase(target)) {
            tablesToClear.addAll(Arrays.asList(
                    "doctable", "app_demo", "app_photo", "voter_reg_details", "biometric_details", "doc_type", "location", "loc_hierarchy_list"));
            try (Connection conn = getLocalDestinationConnection(null)) {
                DatabaseMetaData meta = conn.getMetaData();
                try (ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"})) {
                    while (rs.next()) {
                        String t = rs.getString("TABLE_NAME");
                        if (t != null && !t.startsWith("pg_") && !t.startsWith("sql_") && !t.equalsIgnoreCase("transfer_history") && !tablesToClear.contains(t.toLowerCase())) {
                            tablesToClear.add(t);
                        }
                    }
                }
            } catch (Exception ignored) {}
        } else {
            tablesToClear.add(target);
        }

        for (String table : tablesToClear) {
            boolean cleared = false;
            try (Connection conn = getLocalDestinationConnection(table);
                 Statement stmt = conn.createStatement()) {
                int rows = 0;
                try {
                    rows = stmt.executeUpdate("DELETE FROM \"" + table + "\"");
                    cleared = true;
                } catch (Exception ignored) {
                    try {
                        rows = stmt.executeUpdate("DELETE FROM " + table);
                        cleared = true;
                    } catch (Exception ignored2) {}
                }

                if (cleared) {
                    totalCleared += rows;
                    if (!clearedTables.contains(table)) {
                        clearedTables.add(table);
                    }
                }
            } catch (Exception e) {
                System.err.println("Note: clearing table " + table + " in local DB: " + e.getMessage());
            }

            try {
                int rows = jdbcTemplate.update("DELETE FROM " + table);
                totalCleared += rows;
                if (!clearedTables.contains(table)) {
                    clearedTables.add(table);
                }
            } catch (Exception ignored) {}
        }

        String msg = clearedTables.isEmpty()
                ? "No tables found or cleared for target: " + target
                : "Cleared data from " + clearedTables.size() + " local table(s): " + clearedTables + " (Total rows removed: " + totalCleared + ")";

        recordAudit("LOCAL", "LOCAL", "127.0.0.1", "CLEAR", target, "SUCCESS", totalCleared, msg);
        return new TransferResponseDTO("SUCCESS", msg, totalCleared, 0, target);
    }
    private String quoteTableName(String tableName) {

        String[] parts = tableName.trim().split("\\.", 2);

        if (parts.length == 2) {
            return "\"" + parts[0] + "\".\"" + parts[1] + "\"";
        }

        return "\"" + tableName.trim() + "\"";
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
    public void deleteLocalTransferredData(String schema, String table, List<Map<String, Object>> records) {
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

    private void ensureLocalTableExists(Connection localConn, String workstationIp, String schema, String table, List<Map<String, Object>> sampleRecords) {
        if (table == null || table.trim().isEmpty()) return;
        String cleanTable = table.trim();

        try (Statement stmt = localConn.createStatement()) {
            try {
                stmt.execute("CREATE SCHEMA IF NOT EXISTS applicants;");
            } catch (Exception ignored) {}

            boolean exists = false;
            try {
                DatabaseMetaData meta = localConn.getMetaData();
                try (ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"})) {
                    while (rs.next()) {
                        String tName = rs.getString("TABLE_NAME");
                        if (cleanTable.equalsIgnoreCase(tName)) {
                            exists = true;
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {}

            if (exists) return;

            // Fetch columns and data types from Workstation DB to recreate table locally
            List<String> colDefs = new ArrayList<>();
            try (Connection wsConn = getWorkstationConnection(workstationIp)) {
                DatabaseMetaData wsMeta = wsConn.getMetaData();
                try (ResultSet rs = wsMeta.getColumns(null, null, "%", null)) {
                    while (rs.next()) {
                        String tName = rs.getString("TABLE_NAME");
                        if (cleanTable.equalsIgnoreCase(tName)) {
                            String colName = rs.getString("COLUMN_NAME");
                            String typeName = rs.getString("TYPE_NAME");
                            int colSize = rs.getInt("COLUMN_SIZE");
                            if ("varchar".equalsIgnoreCase(typeName) || "character varying".equalsIgnoreCase(typeName)) {
                                colDefs.add("\"" + colName + "\" VARCHAR(" + (colSize > 0 ? colSize : 255) + ")");
                            } else if ("bytea".equalsIgnoreCase(typeName) || "blob".equalsIgnoreCase(typeName)) {
                                colDefs.add("\"" + colName + "\" BYTEA");
                            } else if ("text".equalsIgnoreCase(typeName)) {
                                colDefs.add("\"" + colName + "\" TEXT");
                            } else if ("int4".equalsIgnoreCase(typeName) || "integer".equalsIgnoreCase(typeName)) {
                                colDefs.add("\"" + colName + "\" INTEGER");
                            } else if ("int8".equalsIgnoreCase(typeName) || "bigint".equalsIgnoreCase(typeName)) {
                                colDefs.add("\"" + colName + "\" BIGINT");
                            } else if ("bool".equalsIgnoreCase(typeName) || "boolean".equalsIgnoreCase(typeName)) {
                                colDefs.add("\"" + colName + "\" BOOLEAN");
                            } else if ("timestamp".equalsIgnoreCase(typeName) || "timestamptz".equalsIgnoreCase(typeName)) {
                                colDefs.add("\"" + colName + "\" TIMESTAMP");
                            } else if ("date".equalsIgnoreCase(typeName)) {
                                colDefs.add("\"" + colName + "\" DATE");
                            } else {
                                colDefs.add("\"" + colName + "\" " + (typeName != null ? typeName : "TEXT"));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Note: could not read workstation metadata for " + cleanTable + ": " + e.getMessage());
            }

            // Fallback from sample record columns if workstation metadata was not available
            if (colDefs.isEmpty() && sampleRecords != null && !sampleRecords.isEmpty()) {
                Map<String, Object> firstRow = sampleRecords.get(0);
                for (Map.Entry<String, Object> entry : firstRow.entrySet()) {
                    String col = entry.getKey();
                    Object val = entry.getValue();
                    if (val instanceof byte[]) {
                        colDefs.add("\"" + col + "\" BYTEA");
                    } else if (val instanceof Integer) {
                        colDefs.add("\"" + col + "\" INTEGER");
                    } else if (val instanceof Long) {
                        colDefs.add("\"" + col + "\" BIGINT");
                    } else if (val instanceof Boolean) {
                        colDefs.add("\"" + col + "\" BOOLEAN");
                    } else {
                        colDefs.add("\"" + col + "\" TEXT");
                    }
                }
            }

            if (!colDefs.isEmpty()) {
                String createSql = "CREATE TABLE IF NOT EXISTS \"" + cleanTable + "\" (" + String.join(", ", colDefs) + ")";
                stmt.execute(createSql);
                System.out.println("Auto-created table locally: " + cleanTable);
            }
        } catch (Exception e) {
            System.err.println("Note: auto-create table " + cleanTable + " in local DB: " + e.getMessage());
        }
    }

    protected int saveDownloadedRecordsToLocalDb(String schemaName, String tableName, String fullTableName, List<Map<String, Object>> records, List<String> primaryKeys) {
        return saveDownloadedRecordsToLocalDb(null, schemaName, tableName, fullTableName, records, primaryKeys);
    }

    protected int saveDownloadedRecordsToLocalDb(String workstationIp, String schemaName, String tableName, String fullTableName, List<Map<String, Object>> records, List<String> primaryKeys) {
        if (records == null || records.isEmpty()) return 0;

        int count = 0;
        try (Connection localConn = getLocalDestinationConnection(tableName)) {
            ensureLocalTableExists(localConn, workstationIp, schemaName, tableName, records);

            String destTable = "\"" + tableName + "\"";
            Map<String, Integer> columnTypes = getColumnTypesForConnection(localConn, schemaName, tableName);

            List<String> basePKs = (primaryKeys != null && !primaryKeys.isEmpty())
                    ? primaryKeys
                    : ("doctable".equalsIgnoreCase(tableName) ? Collections.singletonList("ID") : Collections.emptyList());

            for (Map<String, Object> record : records) {
                Map<String, Object> cleanRecord = new LinkedHashMap<>();
                for (Map.Entry<String, Object> entry : record.entrySet()) {
                    cleanRecord.put(entry.getKey(), entry.getValue());
                }

                List<String> rawColumns = new ArrayList<>(cleanRecord.keySet());
                List<String> quotedColumns = rawColumns.stream().map(c -> "\"" + c + "\"").collect(Collectors.toList());
                List<Object> values = new ArrayList<>();
                for (String col : rawColumns) {
                    Object rawVal = cleanRecord.get(col);
                    Integer dataType = columnTypes.get(col.toLowerCase());
                    values.add(convertValueForJdbc(rawVal, dataType));
                }

                List<String> matchedPKs = new ArrayList<>();
                for (String col : rawColumns) {
                    for (String pk : basePKs) {
                        if (pk.equalsIgnoreCase(col)) {
                            matchedPKs.add(col);
                            break;
                        }
                    }
                }
                if (matchedPKs.isEmpty()) {
                    for (String col : rawColumns) {
                        String lc = col.toLowerCase();
                        if (lc.equals("id") || lc.equals("app_id") || lc.equals("photo_id") || lc.equals("applicant_id") || lc.equals("registration_id")) {
                            matchedPKs.add(col);
                            break;
                        }
                    }
                }

                if (!matchedPKs.isEmpty()) {
                    // Delete existing row with same ID/PK to ensure idempotency without requiring explicit DB unique constraints
                    StringBuilder delSql = new StringBuilder("DELETE FROM ").append(destTable).append(" WHERE ");
                    List<String> delConditions = new ArrayList<>();
                    List<Object> delValues = new ArrayList<>();
                    for (String pk : matchedPKs) {
                        delConditions.add("\"" + pk + "\" = ?");
                        Object pkVal = cleanRecord.get(pk);
                        Integer dataType = columnTypes.get(pk.toLowerCase());
                        delValues.add(convertValueForJdbc(pkVal, dataType));
                    }
                    delSql.append(String.join(" AND ", delConditions));

                    try (PreparedStatement delStmt = localConn.prepareStatement(delSql.toString())) {
                        for (int i = 0; i < delValues.size(); i++) {
                            delStmt.setObject(i + 1, delValues.get(i));
                        }
                        delStmt.executeUpdate();
                    } catch (Exception ignored) {
                    }
                }

                StringBuilder sql = new StringBuilder();
                sql.append("INSERT INTO ").append(destTable).append(" (");
                sql.append(String.join(", ", quotedColumns));
                sql.append(") VALUES (");
                sql.append(quotedColumns.stream().map(c -> "?").collect(Collectors.joining(", ")));
                sql.append(")");

                try (PreparedStatement pstmt = localConn.prepareStatement(sql.toString())) {
                    for (int i = 0; i < values.size(); i++) {
                        pstmt.setObject(i + 1, values.get(i));
                    }
                    pstmt.executeUpdate();
                }
                count++;
            }
        } catch (SQLException e) {
            System.err.println("Error saving downloaded records to local destination DB: " + e.getMessage());
            throw new RuntimeException("Failed to save records to local destination database: " + e.getMessage(), e);
        }
        return count;
    }



    private Map<String, Integer> getColumnTypes(String schema, String table) {
        try (Connection conn = dataSource.getConnection()) {
            return getColumnTypesForConnection(conn, schema, table);
        } catch (Exception e) {
            System.err.println("Error getting column types from default DataSource for " + schema + "." + table + ": " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    private Object convertValueForJdbc(Object val, Integer dataType) {
        if (val == null) {
            return null;
        }

        if (val instanceof byte[]) {
            return val;
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
        if ("doctable".equalsIgnoreCase(table)) {
            pkColumns.add("ID");
            return pkColumns;
        }
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getPrimaryKeys(null, schema, table)) {
                while (rs.next()) {
                    pkColumns.add(rs.getString("COLUMN_NAME"));
                }
            }
        } catch (Exception ignored) {
        }

        if (pkColumns.isEmpty()) {
            String lowerTable = table != null ? table.toLowerCase() : "";
            if (lowerTable.equals("doctable")) {
                pkColumns.add("ID");
            } else if (lowerTable.contains("demo") || lowerTable.contains("photo") || lowerTable.contains("applicant")) {
                pkColumns.add("app_id");
                pkColumns.add("id");
                pkColumns.add("photo_id");
                pkColumns.add("registration_id");
            } else if (lowerTable.equals("voter_reg_details") || lowerTable.equals("biometric_details")) {
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
