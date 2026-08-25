package com.example.regclient_newVersion.dto;

public class DocumentDTO {

    private byte[] document;
    private String value;
    private String type;
    private String category;
    private String owner;
    private String format;
    private String refNumber;

    public DocumentDTO() {
    }

    public DocumentDTO(byte[] document, String value, String type, String category, String owner, String format, String refNumber) {
        this.document = document;
        this.value = value;
        this.type = type;
        this.category = category;
        this.owner = owner;
        this.format = format;
        this.refNumber = refNumber;
    }

    public byte[] getDocument() {
        return document;
    }

    public void setDocument(byte[] document) {
        this.document = document;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }
}
