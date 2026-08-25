package com.example.regclient_newVersion.dto;

public class TableInfoDTO {

    private String tableName;
    private String schemaName;
    private long recordCount;

    public TableInfoDTO() {
    }

    public TableInfoDTO(String tableName, String schemaName, long recordCount) {
        this.tableName = tableName;
        this.schemaName = schemaName;
        this.recordCount = recordCount;
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

    public long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }
}
