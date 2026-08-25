package com.example.regclient_newVersion.Controller;

import com.example.regclient_newVersion.dto.ConnectionHealthDTO;
import com.example.regclient_newVersion.dto.TableInfoDTO;
import com.example.regclient_newVersion.dto.TransferRequestDTO;
import com.example.regclient_newVersion.dto.TransferResponseDTO;
import com.example.regclient_newVersion.Service.WorkstationTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transfer")
@CrossOrigin(origins = "*")
public class WorkstationTransferController {

    private final WorkstationTransferService workstationTransferService;

    @Autowired
    public WorkstationTransferController(WorkstationTransferService workstationTransferService) {
        this.workstationTransferService = workstationTransferService;
    }

    @GetMapping("/health")
    public ResponseEntity<ConnectionHealthDTO> checkHealth() {
        ConnectionHealthDTO health = workstationTransferService.checkHealth();
        if ("UP".equalsIgnoreCase(health.getStatus())) {
            return ResponseEntity.ok(health);
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
    }

    @GetMapping("/tables")
    public ResponseEntity<List<TableInfoDTO>> getAvailableTables() {
        List<TableInfoDTO> tables = workstationTransferService.getAvailableTables();
        return ResponseEntity.ok(tables);
    }

    @PostMapping("/upload")
    public ResponseEntity<TransferResponseDTO> processUpload(@RequestBody TransferRequestDTO uploadRequest) {
        try {
            TransferResponseDTO response = workstationTransferService.processUpload(uploadRequest);
            if ("SUCCESS".equalsIgnoreCase(response.getStatus())) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            TransferResponseDTO err = new TransferResponseDTO("FAILED", "Workstation upload error: " + e.getMessage(), 0);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @GetMapping("/download/{table}")
    public ResponseEntity<TransferRequestDTO> getDownloadData(@PathVariable("table") String tableName) {
        try {
            TransferRequestDTO data = workstationTransferService.getDownloadData(tableName);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/records/{table}")
    public ResponseEntity<List<java.util.Map<String, Object>>> getTableRecords(@PathVariable("table") String tableName) {
        try {
            TransferRequestDTO data = workstationTransferService.getDownloadData(tableName);
            return ResponseEntity.ok(data != null ? data.getRecords() : java.util.Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
