package com.example.regclient_newVersion;

import com.example.regclient_newVersion.Controller.LocalTransferController;
import com.example.regclient_newVersion.Controller.WorkstationTransferController;
import com.example.regclient_newVersion.dto.ConnectionHealthDTO;
import com.example.regclient_newVersion.dto.TableInfoDTO;
import com.example.regclient_newVersion.dto.TransferRequestDTO;
import com.example.regclient_newVersion.dto.TransferResponseDTO;
import com.example.regclient_newVersion.Model.TransferHistory;
import com.example.regclient_newVersion.Service.LocalTransferService;
import com.example.regclient_newVersion.Service.WorkstationApiClient;
import com.example.regclient_newVersion.Service.WorkstationTransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransferWorkflowTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkstationTransferService workstationTransferService;

    @MockBean
    private LocalTransferService localTransferService;

    @Autowired
    private WorkstationApiClient workstationApiClient;

    @Test
    public void testWorkstationHealthEndpoint() throws Exception {
        ConnectionHealthDTO health = new ConnectionHealthDTO("UP", "Workstation transfer API and Database are active", true);
        when(workstationTransferService.checkHealth()).thenReturn(health);

        mockMvc.perform(get("/api/v1/transfer/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.databaseAvailable").value(true));
    }

    @Test
    public void testWorkstationTablesEndpoint() throws Exception {
        TableInfoDTO table = new TableInfoDTO("voter_reg_details", "public", 10);
        when(workstationTransferService.getAvailableTables()).thenReturn(Collections.singletonList(table));

        mockMvc.perform(get("/api/v1/transfer/tables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tableName").value("voter_reg_details"))
                .andExpect(jsonPath("$[0].recordCount").value(10));
    }

    @Test
    public void testLocalTestConnectionEndpoint() throws Exception {
        ConnectionHealthDTO health = new ConnectionHealthDTO("CONNECTED", "Workstation reachability verified", true);
        when(localTransferService.testConnection(anyString())).thenReturn(health);

        mockMvc.perform(post("/api/v1/local/transfer/test-connection")
                        .param("ip", "192.168.1.50:8080"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONNECTED"));
    }

    @Test
    public void testLocalUploadEndpoint() throws Exception {
        TransferResponseDTO response = new TransferResponseDTO("SUCCESS", "Data transferred successfully", 5, 0, "voter_reg_details");
        when(localTransferService.performUpload(anyString(), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/v1/local/transfer/upload")
                        .param("ip", "192.168.1.50:8080")
                        .param("table", "voter_reg_details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.recordsTransferred").value(5));
    }

    @Test
    public void testLocalDownloadEndpoint() throws Exception {
        TransferResponseDTO response = new TransferResponseDTO("SUCCESS", "Data downloaded successfully", 12, 0, "voter_reg_details");
        when(localTransferService.performDownload(anyString(), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/v1/local/transfer/download")
                        .param("ip", "192.168.1.50:8080")
                        .param("table", "voter_reg_details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.recordsTransferred").value(12));
    }

    @Test
    public void testTransferHistoryEndpoint() throws Exception {
        TransferHistory history = new TransferHistory();
        history.setId(1L);
        history.setOperation("UPLOAD");
        history.setTableName("voter_reg_details");
        history.setStatus("SUCCESS");
        history.setRecordsTransferred(5);

        when(localTransferService.getTransferHistory()).thenReturn(Collections.singletonList(history));

        mockMvc.perform(get("/api/v1/local/transfer/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].operation").value("UPLOAD"))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"));
    }

    @Test
    public void testWorkstationApiClientUrlFormatting() {
        org.junit.jupiter.api.Assertions.assertEquals("http://192.168.1.50:8080", workstationApiClient.formatBaseUrl("192.168.1.50"));
        org.junit.jupiter.api.Assertions.assertEquals("http://192.168.1.50:9090", workstationApiClient.formatBaseUrl("192.168.1.50:9090"));
        org.junit.jupiter.api.Assertions.assertEquals("http://127.0.0.1:8080", workstationApiClient.formatBaseUrl("http://127.0.0.1:8080/"));
    }

    @Test
    public void testGetWorkstationRecordsEndpoint() throws Exception {
        java.util.Map<String, Object> record = new java.util.HashMap<>();
        record.put("registration_id", "REG-9999");
        record.put("name", "Juan Dela Cruz");
        when(localTransferService.getWorkstationRecords(anyString(), anyString()))
                .thenReturn(Collections.singletonList(record));

        mockMvc.perform(get("/api/v1/voter-transfer/workstation-records")
                        .param("ip", "192.168.1.232")
                        .param("table", "voter_reg_details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].registration_id").value("REG-9999"))
                .andExpect(jsonPath("$[0].name").value("Juan Dela Cruz"));
    }

    @Test
    public void testDownloadSelectedEndpoint() throws Exception {
        TransferResponseDTO response = new TransferResponseDTO("SUCCESS", "Selected 2 records downloaded", 2, 0, "voter_reg_details");
        when(localTransferService.performDownloadSelected(anyString(), anyString(), anyList()))
                .thenReturn(response);

        String jsonPayload = "{\"workstationIp\":\"192.168.1.232\",\"tableName\":\"voter_reg_details\",\"recordIds\":[\"REG-101\",\"REG-102\"]}";

        mockMvc.perform(post("/api/v1/voter-transfer/download-selected")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.recordsTransferred").value(2));
    }

    @Test
    public void testLocalDownloadSelectedEndpoint() throws Exception {
        TransferResponseDTO response = new TransferResponseDTO("SUCCESS", "Selected 1 records downloaded", 1, 0, "voter_reg_details");
        when(localTransferService.performDownloadSelected(anyString(), anyString(), anyList()))
                .thenReturn(response);

        String jsonPayload = "{\"workstationIp\":\"192.168.1.232\",\"tableName\":\"voter_reg_details\",\"recordIds\":[\"REG-101\"]}";

        mockMvc.perform(post("/api/v1/local/transfer/download-selected")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.recordsTransferred").value(1));
    }

    @Test
    public void testWorkstationTableRecordsEndpoint() throws Exception {
        java.util.Map<String, Object> record = new java.util.HashMap<>();
        record.put("registration_id", "REG-555");
        record.put("status", "SUBMITTED");
        TransferRequestDTO req = new TransferRequestDTO("voter_reg_details", "public", Collections.singletonList(record), 1, "192.168.1.232", "REG-555");
        when(workstationTransferService.getDownloadData("voter_reg_details")).thenReturn(req);

        mockMvc.perform(get("/api/v1/transfer/records/voter_reg_details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].registration_id").value("REG-555"))
                .andExpect(jsonPath("$[0].status").value("SUBMITTED"));
    }

    @Test
    public void testGetDoctableRecordsDefaultEndpoint() throws Exception {
        java.util.Map<String, Object> docRecord = new java.util.HashMap<>();
        docRecord.put("ID", "DOC-001");
        docRecord.put("FIRSTNAME", "John");
        docRecord.put("LASTNAME", "Doe");
        docRecord.put("SEX", "M");
        docRecord.put("DOBYEAR", "1990");
        docRecord.put("DOBMONTH", "05");
        docRecord.put("DOBDAY", "15");

        when(localTransferService.getWorkstationRecords(anyString(), eq("doctable")))
                .thenReturn(Collections.singletonList(docRecord));

        mockMvc.perform(get("/api/v1/local/transfer/workstation-records")
                        .param("ip", "192.168.1.232"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ID").value("DOC-001"))
                .andExpect(jsonPath("$[0].FIRSTNAME").value("John"))
                .andExpect(jsonPath("$[0].LASTNAME").value("Doe"))
                .andExpect(jsonPath("$[0].SEX").value("M"))
                .andExpect(jsonPath("$[0].DOBYEAR").value("1990"))
                .andExpect(jsonPath("$[0].DOBMONTH").value("05"))
                .andExpect(jsonPath("$[0].DOBDAY").value("15"));
    }

    @Test
    public void testDownloadSelectedDoctableRecords() throws Exception {
        TransferResponseDTO response = new TransferResponseDTO("SUCCESS", "Selected 1 records downloaded", 1, 0, "doctable");
        when(localTransferService.performDownloadSelected(eq("192.168.1.232"), eq("doctable"), anyList()))
                .thenReturn(response);

        String jsonPayload = "{\"workstationIp\":\"192.168.1.232\",\"tableName\":\"doctable\",\"recordIds\":[\"DOC-001\"]}";

        mockMvc.perform(post("/api/v1/local/transfer/download-selected")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.recordsTransferred").value(1))
                .andExpect(jsonPath("$.tableName").value("doctable"));
    }

    @Test
    public void testDownloadSelectedMultiTableRecords() throws Exception {
        TransferResponseDTO response = new TransferResponseDTO("SUCCESS", "Downloaded 3 tables", 234, 0, "app_demo, app_photo, doctable");
        when(localTransferService.performDownloadSelectedMulti(eq("192.168.1.232"), anyList(), anyList()))
                .thenReturn(response);

        String jsonPayload = "{\"workstationIp\":\"192.168.1.232\",\"tableName\":[\"app_demo\",\"app_photo\",\"doctable\"],\"recordIds\":[\"ALL\"]}";

        mockMvc.perform(post("/api/v1/local/transfer/download-selected")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.recordsTransferred").value(234));
    }

    @Test
    public void testClearLocalDataEndpoint() throws Exception {
        TransferResponseDTO response = new TransferResponseDTO("SUCCESS", "Cleared data from 3 local table(s)", 15, 0, "ALL");
        when(localTransferService.clearLocalTableData(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/local/transfer/clear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tableName\":\"ALL\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.recordsTransferred").value(15));
    }
}
