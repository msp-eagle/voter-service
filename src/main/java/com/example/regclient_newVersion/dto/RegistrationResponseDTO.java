package com.example.regclient_newVersion.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RegistrationResponseDTO {

    private String registrationId;
    private DemographicDTO demographic;
    private List<DocumentDTO> documents;
    private List<BiometricsDto> biometrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RegistrationResponseDTO() {
    }

    public RegistrationResponseDTO(String registrationId, DemographicDTO demographic, List<DocumentDTO> documents, List<BiometricsDto> biometrics, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.registrationId = registrationId;
        this.demographic = demographic;
        this.documents = documents;
        this.biometrics = biometrics;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public DemographicDTO getDemographic() {
        return demographic;
    }

    public void setDemographic(DemographicDTO demographic) {
        this.demographic = demographic;
    }

    public List<DocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentDTO> documents) {
        this.documents = documents;
    }

    public List<BiometricsDto> getBiometrics() {
        return biometrics;
    }

    public void setBiometrics(List<BiometricsDto> biometrics) {
        this.biometrics = biometrics;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
