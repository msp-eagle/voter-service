package com.example.regclient_newVersion.Service;

import com.example.regclient_newVersion.dto.ConnectionHealthDTO;
import com.example.regclient_newVersion.dto.TableInfoDTO;
import com.example.regclient_newVersion.dto.TransferResponseDTO;
import com.example.regclient_newVersion.Model.TransferHistory;

import java.util.List;

public interface LocalTransferService {

    ConnectionHealthDTO testConnection(String workstationIp);

    List<TableInfoDTO> getLocalTables();

    List<TableInfoDTO> getWorkstationTables(String workstationIp);

    TransferResponseDTO performUpload(String workstationIp, String tableName);

    TransferResponseDTO performDownload(String workstationIp, String tableName);

    List<TransferHistory> getTransferHistory();
}
