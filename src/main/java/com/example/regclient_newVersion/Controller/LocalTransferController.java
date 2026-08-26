package com.example.regclient_newVersion.Controller;

import com.example.regclient_newVersion.dto.ConnectionHealthDTO;
import com.example.regclient_newVersion.dto.TableInfoDTO;
import com.example.regclient_newVersion.dto.TransferResponseDTO;
import com.example.regclient_newVersion.Model.TransferHistory;
import com.example.regclient_newVersion.Service.LocalTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/local/transfer")
@CrossOrigin(origins = "*")
public class LocalTransferController {

    private final LocalTransferService localTransferService;

    @Autowired
    public LocalTransferController(LocalTransferService localTransferService) {
        this.localTransferService = localTransferService;
    }

    @PostMapping("/test-connection")
    public ResponseEntity<ConnectionHealthDTO> testConnection(@RequestParam(value = "ip", required = false) String workstationIp) {
        ConnectionHealthDTO result = localTransferService.testConnection(workstationIp);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/local-tables")
    public ResponseEntity<List<TableInfoDTO>> getLocalTables() {
        List<TableInfoDTO> tables = localTransferService.getLocalTables();
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/workstation-tables")
    public ResponseEntity<List<TableInfoDTO>> getWorkstationTables(@RequestParam(value = "ip", required = false) String workstationIp) {
        try {
            List<TableInfoDTO> tables = localTransferService.getWorkstationTables(workstationIp);
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<TransferResponseDTO> performUpload(@RequestParam(value = "ip", required = false) String workstationIp,
                                                            @RequestParam(value = "table", required = false) String tableName) {
        TransferResponseDTO response = localTransferService.performUpload(workstationIp, tableName);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/download", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<TransferResponseDTO> performDownload(
            @RequestParam(value = "ip", required = false) String workstationIp,
            @RequestParam(value = "table", required = false) String tableName,
            @RequestBody(required = false) com.example.regclient_newVersion.dto.DownloadSelectedRequestDTO downloadRequest) {
        String targetIp = (downloadRequest != null && downloadRequest.getWorkstationIp() != null) ? downloadRequest.getWorkstationIp() : workstationIp;
        List<String> targetTables = (downloadRequest != null && downloadRequest.getTableList() != null && !downloadRequest.getTableList().isEmpty())
                ? downloadRequest.getTableList() : (tableName != null ? java.util.Collections.singletonList(tableName) : java.util.Collections.singletonList("doctable"));
        List<String> recordIds = (downloadRequest != null) ? downloadRequest.getRecordIds() : null;

        TransferResponseDTO response;
        if (targetTables.size() > 1) {
            response = localTransferService.performDownloadSelectedMulti(targetIp, targetTables, recordIds);
        } else {
            String singleTable = !targetTables.isEmpty() ? targetTables.get(0) : "doctable";
            if (recordIds != null && !recordIds.isEmpty()) {
                response = localTransferService.performDownloadSelected(targetIp, singleTable, recordIds);
            } else {
                response = localTransferService.performDownload(targetIp, singleTable);
            }
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/workstation-records")
    public ResponseEntity<List<java.util.Map<String, Object>>> getWorkstationRecords(
            @RequestParam(value = "ip", required = false) String workstationIp,
            @RequestParam(value = "table", required = false, defaultValue = "doctable") String tableName) {
        try {
            List<java.util.Map<String, Object>> records = localTransferService.getWorkstationRecords(workstationIp, tableName);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/download-selected", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<TransferResponseDTO> performDownloadSelected(
            @RequestParam(value = "ip", required = false) String workstationIp,
            @RequestParam(value = "table", required = false) String tableName,
            @RequestParam(value = "recordIds", required = false) List<String> queryRecordIds,
            @RequestBody(required = false) com.example.regclient_newVersion.dto.DownloadSelectedRequestDTO downloadRequest) {
        String targetIp = (downloadRequest != null && downloadRequest.getWorkstationIp() != null) ? downloadRequest.getWorkstationIp() : workstationIp;
        List<String> targetTables = (downloadRequest != null && downloadRequest.getTableList() != null && !downloadRequest.getTableList().isEmpty())
                ? downloadRequest.getTableList() : (tableName != null ? java.util.Collections.singletonList(tableName) : java.util.Collections.singletonList("doctable"));
        List<String> recordIds = (downloadRequest != null && downloadRequest.getRecordIds() != null)
                ? downloadRequest.getRecordIds() : queryRecordIds;

        TransferResponseDTO response;
        if (targetTables.size() > 1) {
            response = localTransferService.performDownloadSelectedMulti(targetIp, targetTables, recordIds);
        } else {
            String singleTable = !targetTables.isEmpty() ? targetTables.get(0) : "doctable";
            if (recordIds != null && !recordIds.isEmpty()) {
                response = localTransferService.performDownloadSelected(targetIp, singleTable, recordIds);
            } else {
                response = localTransferService.performDownload(targetIp, singleTable);
            }
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/clear", method = {RequestMethod.POST, RequestMethod.DELETE, RequestMethod.GET})
    public ResponseEntity<TransferResponseDTO> clearLocalData(
            @RequestParam(value = "table", required = false) String tableName,
            @RequestBody(required = false) java.util.Map<String, String> requestBody) {
        String targetTable = tableName;
        if (requestBody != null && requestBody.containsKey("tableName")) {
            targetTable = requestBody.get("tableName");
        } else if (requestBody != null && requestBody.containsKey("table")) {
            targetTable = requestBody.get("table");
        }
        TransferResponseDTO response = localTransferService.clearLocalTableData(targetTable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransferHistory>> getTransferHistory() {
        List<TransferHistory> history = localTransferService.getTransferHistory();
        return ResponseEntity.ok(history);
    }
}
