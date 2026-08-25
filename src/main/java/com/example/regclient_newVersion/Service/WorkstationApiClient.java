package com.example.regclient_newVersion.Service;

import com.example.regclient_newVersion.dto.ConnectionHealthDTO;
import com.example.regclient_newVersion.dto.TableInfoDTO;
import com.example.regclient_newVersion.dto.TransferRequestDTO;
import com.example.regclient_newVersion.dto.TransferResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Component
public class WorkstationApiClient {

    private final RestTemplate restTemplate;

    @Autowired
    public WorkstationApiClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }

    public ConnectionHealthDTO checkConnection(String workstationIp) {
        try {
            String baseUrl = formatBaseUrl(workstationIp);
            String url = baseUrl + "/api/v1/transfer/health";
            return restTemplate.getForObject(url, ConnectionHealthDTO.class);
        } catch (IllegalArgumentException e) {
            return new ConnectionHealthDTO("DISCONNECTED", e.getMessage(), false);
        } catch (Exception e) {
            return new ConnectionHealthDTO("DISCONNECTED", "Failed to reach Workstation API: " + e.getMessage(), false);
        }
    }

    public List<TableInfoDTO> getWorkstationTables(String workstationIp) {
        String baseUrl = formatBaseUrl(workstationIp);
        String url = baseUrl + "/api/v1/transfer/tables";
        try {
            ResponseEntity<List<TableInfoDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<TableInfoDTO>>() {}
            );
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching tables from Workstation: " + e.getMessage(), e);
        }
    }

    public TransferResponseDTO uploadData(String workstationIp, TransferRequestDTO uploadRequest) {
        String baseUrl = formatBaseUrl(workstationIp);
        String url = baseUrl + "/api/v1/transfer/upload";
        try {
            return restTemplate.postForObject(url, uploadRequest, TransferResponseDTO.class);
        } catch (Exception e) {
            return new TransferResponseDTO("FAILED", "Error sending upload payload to Workstation: " + e.getMessage(), 0);
        }
    }

    public TransferRequestDTO downloadData(String workstationIp, String tableName) {
        String baseUrl = formatBaseUrl(workstationIp);
        String url = baseUrl + "/api/v1/transfer/download/" + tableName;
        try {
            return restTemplate.getForObject(url, TransferRequestDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error downloading data from Workstation: " + e.getMessage(), e);
        }
    }

    public String formatBaseUrl(String rawIp) {
        if (rawIp == null || rawIp.trim().isEmpty()) {
            throw new IllegalArgumentException("Workstation IP address cannot be empty");
        }
        String ip = rawIp.trim();
        if (!ip.startsWith("http://") && !ip.startsWith("https://")) {
            ip = "http://" + ip;
        }

        String addressPart = ip.substring(ip.indexOf("://") + 3);
        if (addressPart.endsWith("/")) {
            addressPart = addressPart.substring(0, addressPart.length() - 1);
        }

        // Detect database port misconfiguration
        if (addressPart.endsWith(":5432") || addressPart.endsWith(":5433")) {
            throw new IllegalArgumentException("Port 5432 is a PostgreSQL database port. Enter the Workstation HTTP API port (e.g. 8080) instead.");
        }

        // Append default HTTP API port 8080 if no port specified
        if (!addressPart.contains(":")) {
            ip = "http://" + addressPart + ":8080";
        } else {
            ip = "http://" + addressPart;
        }

        return ip;
    }
}
