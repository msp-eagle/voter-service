package com.example.regclient_newVersion.Controller;

import com.example.regclient_newVersion.dto.ConnectionHealthDTO;
import com.example.regclient_newVersion.dto.TableInfoDTO;
import com.example.regclient_newVersion.dto.TransferResponseDTO;
import com.example.regclient_newVersion.Model.TransferHistory;
import com.example.regclient_newVersion.Service.LocalTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/voter-transfer", "/api/v1/data-transfer"})
@CrossOrigin(origins = "*")
public class VoterDataTransferController {

    private final LocalTransferService localTransferService;

    @Value("${spring.datasource.url1:jdbc:postgresql://192.168.1.232:5432/voter_reg}")
    private String workstationUrl1;

    @Autowired
    public VoterDataTransferController(LocalTransferService localTransferService) {
        this.localTransferService = localTransferService;
    }

    private String resolveWorkstationIp(String rawIp) {
        if (rawIp != null && !rawIp.trim().isEmpty()) {
            return rawIp.trim();
        }
        if (workstationUrl1 != null && workstationUrl1.contains("://")) {
            String afterScheme = workstationUrl1.substring(workstationUrl1.indexOf("://") + 3);
            if (afterScheme.contains("/")) {
                String hostPort = afterScheme.substring(0, afterScheme.indexOf('/'));
                if (hostPort.contains(":")) {
                    return hostPort.split(":")[0];
                }
                return hostPort;
            }
        }
        return "192.168.1.232";
    }

    /**
     * Health check endpoint testing connectivity to Workstation PostgreSQL DB (192.168.1.232:5432/voter_reg)
     * GET /api/v1/transfer/health
     */
    @GetMapping("/health")
    public ResponseEntity<ConnectionHealthDTO> checkHealth(@RequestParam(value = "ip", required = false) String workstationIp) {
        String targetIp = resolveWorkstationIp(workstationIp);
        ConnectionHealthDTO health = localTransferService.testConnection(targetIp);
        return ResponseEntity.ok(health);
    }

    /**
     * Connection test endpoint
     * POST /api/v1/transfer/test-connection
     */
    @PostMapping("/test-connection")
    public ResponseEntity<ConnectionHealthDTO> testConnection(@RequestParam(value = "ip", required = false) String workstationIp) {
        String targetIp = resolveWorkstationIp(workstationIp);
        ConnectionHealthDTO result = localTransferService.testConnection(targetIp);
        return ResponseEntity.ok(result);
    }

    /**
     * Upload API: Transfers data from Local PostgreSQL DB (localhost:5433/backup_votater) to Workstation DB (192.168.1.232:5432/voter_reg)
     * POST /api/v1/transfer/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<TransferResponseDTO> uploadData(@RequestParam(value = "ip", required = false) String workstationIp,
                                                           @RequestParam(value = "table", required = false) String tableName) {
        String targetIp = resolveWorkstationIp(workstationIp);
        TransferResponseDTO response = localTransferService.performUpload(targetIp, tableName);
        return ResponseEntity.ok(response);
    }

    /**
     * Download API: Downloads data from Workstation DB to Local DB
     * POST/GET /api/v1/voter-transfer/download
     */
    @RequestMapping(value = "/download", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<TransferResponseDTO> downloadData(
            @RequestParam(value = "ip", required = false) String workstationIp,
            @RequestParam(value = "table", required = false) String tableName,
            @RequestBody(required = false) com.example.regclient_newVersion.dto.DownloadSelectedRequestDTO downloadRequest) {
        String targetIp = (downloadRequest != null && downloadRequest.getWorkstationIp() != null)
                ? downloadRequest.getWorkstationIp() : resolveWorkstationIp(workstationIp);
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

    /**
     * Download Selected API: Downloads specific selected records from Workstation DB to Local DB
     * POST/GET /api/v1/voter-transfer/download-selected
     */
    @RequestMapping(value = "/download-selected", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<TransferResponseDTO> downloadSelectedData(
            @RequestParam(value = "ip", required = false) String workstationIp,
            @RequestParam(value = "table", required = false) String tableName,
            @RequestParam(value = "recordIds", required = false) List<String> queryRecordIds,
            @RequestBody(required = false) com.example.regclient_newVersion.dto.DownloadSelectedRequestDTO downloadRequest) {
        String targetIp = (downloadRequest != null && downloadRequest.getWorkstationIp() != null)
                ? downloadRequest.getWorkstationIp() : resolveWorkstationIp(workstationIp);
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

    /**
     * Records API: Fetch records from Workstation PostgreSQL DB for preview and selection
     * GET /api/v1/transfer/workstation-records
     */
    @GetMapping("/workstation-records")
    public ResponseEntity<List<java.util.Map<String, Object>>> getWorkstationRecords(
            @RequestParam(value = "ip", required = false) String workstationIp,
            @RequestParam(value = "table", required = false, defaultValue = "doctable") String tableName) {
        String targetIp = resolveWorkstationIp(workstationIp);
        List<java.util.Map<String, Object>> records = localTransferService.getWorkstationRecords(targetIp, tableName);
        return ResponseEntity.ok(records);
    }

    /**
     * Tables API: List local tables
     * GET /api/v1/transfer/local-tables
     */
    @GetMapping("/local-tables")
    public ResponseEntity<List<TableInfoDTO>> getLocalTables() {
        List<TableInfoDTO> tables = localTransferService.getLocalTables();
        return ResponseEntity.ok(tables);
    }

    /**
     * Tables API: List workstation tables
     * GET /api/v1/transfer/workstation-tables
     */
    @GetMapping("/workstation-tables")
    public ResponseEntity<List<TableInfoDTO>> getWorkstationTables(@RequestParam(value = "ip", required = false) String workstationIp) {
        String targetIp = resolveWorkstationIp(workstationIp);
        List<TableInfoDTO> tables = localTransferService.getWorkstationTables(targetIp);
        return ResponseEntity.ok(tables);
    }

    /**
     * Clear API: Clears local database table data
     * POST/DELETE/GET /api/v1/voter-transfer/clear
     */
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

    /**
     * History API: Audit trail of transfer operations
     * GET /api/v1/transfer/history
     */
    @GetMapping("/history")
    public ResponseEntity<List<TransferHistory>> getTransferHistory() {
        List<TransferHistory> history = localTransferService.getTransferHistory();
        return ResponseEntity.ok(history);
    }
    
}
