package com.example.regclient_newVersion.dto;

import java.util.ArrayList;
import java.util.List;

public class LocationHierarchyDTO {

    private String code;
    private String name;
    private Integer hierarchyLevel;
    private String hierarchyName;
    private String parentLocCode;
    private String langCode;
    private Boolean isActive;

    private List<LocationHierarchyDTO> children = new ArrayList<>();

    public LocationHierarchyDTO() {
    }

    public LocationHierarchyDTO(String code, String name, Integer hierarchyLevel, String hierarchyName, String parentLocCode, String langCode, Boolean isActive) {
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

    public List<LocationHierarchyDTO> getChildren() {
        return children;
    }

    public void setChildren(List<LocationHierarchyDTO> children) {
        this.children = children;
    }

    public void addChild(LocationHierarchyDTO child) {
        this.children.add(child);
    }
}
