package com.example.regclient_newVersion.Model;

import java.io.Serializable;
import java.util.Objects;

public class LocationHierarchyListID implements Serializable {

    private Integer hierarchyLevel;
    private String langCode;

    public LocationHierarchyListID() {
    }

    public LocationHierarchyListID(Integer hierarchyLevel, String langCode) {
        this.hierarchyLevel = hierarchyLevel;
        this.langCode = langCode;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationHierarchyListID that = (LocationHierarchyListID) o;
        return Objects.equals(hierarchyLevel, that.hierarchyLevel) && Objects.equals(langCode, that.langCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hierarchyLevel, langCode);
    }
}
