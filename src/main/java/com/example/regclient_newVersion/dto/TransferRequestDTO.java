package com.example.regclient_newVersion.dto;

import java.util.List;
import java.util.Map;

public class TransferRequestDTO {

    private String tableName;
    private String schemaName;
    private List<Map<String, Object>> records;
    private int recordCount;
    private String sourceIp;
    private String recordIdentifier;

    public TransferRequestDTO() {
    }

    public TransferRequestDTO(String tableName, String schemaName, List<Map<String, Object>> records, int recordCount, String sourceIp, String recordIdentifier) {
        this.tableName = tableName;
        this.schemaName = schemaName;
        this.records = records;
        this.recordCount = recordCount;
        this.sourceIp = sourceIp;
        this.recordIdentifier = recordIdentifier;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public List<Map<String, Object>> getRecords() {
        return records;
    }

    public void setRecords(List<Map<String, Object>> records) {
        this.records = records;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getRecordIdentifier() {
        return recordIdentifier;
    }

    public void setRecordIdentifier(String recordIdentifier) {
        this.recordIdentifier = recordIdentifier;
    }
}
