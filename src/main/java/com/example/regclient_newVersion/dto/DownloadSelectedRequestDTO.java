package com.example.regclient_newVersion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DownloadSelectedRequestDTO {

    private String workstationIp;
    private List<String> tableNames = new ArrayList<>();
    private List<String> recordIds;
    private boolean selectAll;

    public DownloadSelectedRequestDTO() {
    }

    public DownloadSelectedRequestDTO(String workstationIp, String tableName, List<String> recordIds, boolean selectAll) {
        this.workstationIp = workstationIp;
        if (tableName != null) {
            this.tableNames = new ArrayList<>(Collections.singletonList(tableName));
        }
        this.recordIds = recordIds;
        this.selectAll = selectAll;
    }

    public String getWorkstationIp() {
        return workstationIp;
    }

    public void setWorkstationIp(String workstationIp) {
        this.workstationIp = workstationIp;
    }

    public String getTableName() {
        return (tableNames != null && !tableNames.isEmpty()) ? tableNames.get(0) : null;
    }

    @JsonProperty("tableName")
    public void setTableName(Object tableName) {
        setTablesFromObject(tableName);
    }

    @JsonProperty("tables")
    public void setTables(Object tables) {
        setTablesFromObject(tables);
    }

    @JsonProperty("tableNames")
    public void setTableNames(Object tableNames) {
        setTablesFromObject(tableNames);
    }

    private void setTablesFromObject(Object obj) {
        if (obj == null) return;
        if (this.tableNames == null) this.tableNames = new ArrayList<>();
        if (obj instanceof List) {
            for (Object item : (List<?>) obj) {
                if (item != null) {
                    String s = String.valueOf(item).trim();
                    if (!s.isEmpty() && !this.tableNames.contains(s)) {
                        this.tableNames.add(s);
                    }
                }
            }
        } else if (obj instanceof String[]) {
            for (String item : (String[]) obj) {
                if (item != null) {
                    String s = item.trim();
                    if (!s.isEmpty() && !this.tableNames.contains(s)) {
                        this.tableNames.add(s);
                    }
                }
            }
        } else {
            String str = String.valueOf(obj).trim();
            if (str.startsWith("[") && str.endsWith("]")) {
                str = str.substring(1, str.length() - 1);
                for (String part : str.split(",")) {
                    String clean = part.replace("\"", "").replace("'", "").trim();
                    if (!clean.isEmpty() && !this.tableNames.contains(clean)) {
                        this.tableNames.add(clean);
                    }
                }
            } else if (!str.isEmpty() && !this.tableNames.contains(str)) {
                this.tableNames.add(str);
            }
        }
    }

    public List<String> getTableList() {
        return (tableNames != null && !tableNames.isEmpty()) ? tableNames : Collections.singletonList("doctable");
    }

    public List<String> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<String> recordIds) {
        this.recordIds = recordIds;
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }
}
