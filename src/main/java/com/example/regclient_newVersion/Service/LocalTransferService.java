package com.example.regclient_newVersion.Service;

import com.example.regclient_newVersion.dto.ConnectionHealthDTO;
import com.example.regclient_newVersion.dto.TableInfoDTO;
import com.example.regclient_newVersion.dto.TransferResponseDTO;
import com.example.regclient_newVersion.Model.TransferHistory;

import java.util.List;
import java.util.Map;

public interface LocalTransferService {

//    ConnectionHealthDTO testConnection(String workstationIp);

//    List<TableInfoDTO> getLocalTables();

//    List<TableInfoDTO> getWorkstationTables(String workstationIp);

    TransferResponseDTO performUpload(String workstationIp, String tableName);

//    TransferResponseDTO performDownload(String workstationIp, String tableName);

//    List<Map<String, Object>> getWorkstationRecords(String workstationIp, String tableName);

//    TransferResponseDTO performDownloadSelected(String workstationIp, String tableName, List<String> recordIds);

//    TransferResponseDTO performDownloadSelectedMulti(String workstationIp, List<String> tableNames, List<String> recordIds);

//    TransferResponseDTO clearLocalTableData(String tableName);

//    TransferResponseDTO clearAllLocalData();

//    List<TransferHistory> getTransferHistory();

    TransferResponseDTO clearAllLocalTableData();
}
