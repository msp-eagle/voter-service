package com.example.regclient_newVersion.dto;

public class DocumentTypeDTO {

    private String code;
    private String name;
    private String descr;
    private String langCode;
    private Boolean isActive;

    public DocumentTypeDTO() {
    }

    public DocumentTypeDTO(String code, String name, String descr, String langCode, Boolean isActive) {
        this.code = code;
        this.name = name;
        this.descr = descr;
        this.langCode = langCode;
        this.isActive = isActive;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
