package com.example.regclient_newVersion.Model;

import javax.persistence.*;

@Entity
@Table(schema = "master", name = "doc_type")
public class DocumentType {

    @Id
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "descr")
    private String descr;

    @Column(name = "lang_code")
    private String langCode;

    @Column(name = "is_active")
    private Boolean isActive;

    public DocumentType() {
    }

    public DocumentType(String code, String name, String descr, String langCode, Boolean isActive) {
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
