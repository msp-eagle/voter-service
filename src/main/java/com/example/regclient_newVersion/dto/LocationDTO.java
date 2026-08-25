package com.example.regclient_newVersion.dto;

public class LocationDTO {

    private String code;
    private String name;
    private Integer hierarchyLevel;
    private String hierarchyName;
    private String parentLocCode;
    private String langCode;
    private Boolean isActive;

    public LocationDTO() {
    }

    public LocationDTO(String code, String name, Integer hierarchyLevel, String hierarchyName, String parentLocCode, String langCode, Boolean isActive) {
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
