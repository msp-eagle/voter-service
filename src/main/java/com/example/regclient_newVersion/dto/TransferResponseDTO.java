package com.example.regclient_newVersion.dto;

import java.time.LocalDateTime;

public class TransferResponseDTO {

    private String status; // SUCCESS or FAILED
    private String message;
    private int recordsTransferred;
    private int failedRecords;
    private String tableName;
    private LocalDateTime timestamp;

    public TransferResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public TransferResponseDTO(String status, String message, int recordsTransferred) {
        this.status = status;
        this.message = message;
        this.recordsTransferred = recordsTransferred;
        this.failedRecords = 0;
        this.timestamp = LocalDateTime.now();
    }

    public TransferResponseDTO(String status, String message, int recordsTransferred, int failedRecords, String tableName) {
        this.status = status;
        this.message = message;
        this.recordsTransferred = recordsTransferred;
        this.failedRecords = failedRecords;
        this.tableName = tableName;
        this.timestamp = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRecordsTransferred() {
        return recordsTransferred;
    }

    public void setRecordsTransferred(int recordsTransferred) {
        this.recordsTransferred = recordsTransferred;
    }

    public int getFailedRecords() {
        return failedRecords;
    }

    public void setFailedRecords(int failedRecords) {
        this.failedRecords = failedRecords;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
