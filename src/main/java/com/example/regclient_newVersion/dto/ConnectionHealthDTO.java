package com.example.regclient_newVersion.dto;

import java.time.LocalDateTime;

public class ConnectionHealthDTO {

    private String status; // UP, CONNECTED, DISCONNECTED
    private String message;
    private boolean databaseAvailable;
    private LocalDateTime timestamp;

    public ConnectionHealthDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ConnectionHealthDTO(String status, String message, boolean databaseAvailable) {
        this.status = status;
        this.message = message;
        this.databaseAvailable = databaseAvailable;
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

    public boolean isDatabaseAvailable() {
        return databaseAvailable;
    }

    public void setDatabaseAvailable(boolean databaseAvailable) {
        this.databaseAvailable = databaseAvailable;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
