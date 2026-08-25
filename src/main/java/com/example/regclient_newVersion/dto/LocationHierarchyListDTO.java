package com.example.regclient_newVersion.dto;

public class LocationHierarchyListDTO {

    private Integer hierarchyLevel;
    private String hierarchyName;
    private String langCode;
    private Boolean isActive;

    public LocationHierarchyListDTO() {
    }

    public LocationHierarchyListDTO(Integer hierarchyLevel, String hierarchyName, String langCode, Boolean isActive) {
        this.hierarchyLevel = hierarchyLevel;
        this.hierarchyName = hierarchyName;
        this.langCode = langCode;
        this.isActive = isActive;
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
