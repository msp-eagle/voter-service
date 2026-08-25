package com.example.regclient_newVersion.Service;

import com.example.regclient_newVersion.dto.ConnectionHealthDTO;
import com.example.regclient_newVersion.dto.TableInfoDTO;
import com.example.regclient_newVersion.dto.TransferRequestDTO;
import com.example.regclient_newVersion.dto.TransferResponseDTO;

import java.util.List;

public interface WorkstationTransferService {

    ConnectionHealthDTO checkHealth();

    List<TableInfoDTO> getAvailableTables();

    TransferResponseDTO processUpload(TransferRequestDTO uploadRequest);

    TransferRequestDTO getDownloadData(String tableName);
}
