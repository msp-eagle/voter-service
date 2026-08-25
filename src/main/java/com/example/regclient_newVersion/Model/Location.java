package com.example.regclient_newVersion.Model;

import javax.persistence.*;

@Entity
@Table(schema = "master", name = "location")
public class Location {

    @Id
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "hierarchy_level")
    private Integer hierarchyLevel;

    @Column(name = "hierarchy_level_name")
    private String hierarchyName;

    @Column(name = "parent_loc_code")
    private String parentLocCode;

    @Column(name = "lang_code")
    private String langCode;

    @Column(name = "is_active")
    private Boolean isActive;

    public Location() {
    }

    public Location(String code, String name, Integer hierarchyLevel, String hierarchyName, String parentLocCode, String langCode, Boolean isActive) {
        this.code = code;
        this.name = name;
        this.hierarchyLevel = hierarchyLevel;
        this.hierarchyName = hierarchyName;
        this.parentLocCode = parentLocCode;
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

    public Integer getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(Integer hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    public String getParentLocCode() {
        return parentLocCode;
    }

    public void setParentLocCode(String parentLocCode) {
        this.parentLocCode = parentLocCode;
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
