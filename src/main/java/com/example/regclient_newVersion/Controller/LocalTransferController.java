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

    @PostMapping("/download")
    public ResponseEntity<TransferResponseDTO> performDownload(@RequestParam(value = "ip", required = false) String workstationIp,
                                                              @RequestParam(value = "table", required = false) String tableName) {
        TransferResponseDTO response = localTransferService.performDownload(workstationIp, tableName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransferHistory>> getTransferHistory() {
        List<TransferHistory> history = localTransferService.getTransferHistory();
        return ResponseEntity.ok(history);
    }
}
