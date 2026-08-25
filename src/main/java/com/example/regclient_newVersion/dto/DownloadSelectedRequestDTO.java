package com.example.regclient_newVersion.dto;

import java.util.List;

public class DownloadSelectedRequestDTO {

    private String workstationIp;
    private String tableName;
    private List<String> recordIds;
    private boolean selectAll;

    public DownloadSelectedRequestDTO() {
    }

    public DownloadSelectedRequestDTO(String workstationIp, String tableName, List<String> recordIds, boolean selectAll) {
        this.workstationIp = workstationIp;
        this.tableName = tableName;
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
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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
