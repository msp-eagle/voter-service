package com.example.regclient_newVersion.Model;

import javax.persistence.*;

@Entity
@Table(schema = "master", name = "loc_hierarchy_list")
@IdClass(LocationHierarchyListID.class)
public class LocationHierarchyList {

    @Id
    @Column(name = "hierarchy_level", nullable = false)
    private Integer hierarchyLevel;

    @Id
    @Column(name = "lang_code", nullable = false)
    private String langCode;

    @Column(name = "hierarchy_level_name")
    private String hierarchyName;

    @Column(name = "is_active")
    private Boolean isActive;

    public LocationHierarchyList() {
    }

    public LocationHierarchyList(Integer hierarchyLevel, String langCode, String hierarchyName, Boolean isActive) {
        this.hierarchyLevel = hierarchyLevel;
        this.langCode = langCode;
        this.hierarchyName = hierarchyName;
        this.isActive = isActive;
    }

    public Integer getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(Integer hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
