package com.example.regclient_newVersion.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

public class RegistrationRequestDTO {

    @NotBlank(message = "Registration ID is required")
    private String registrationId= "1234567890";

    @Valid
    private DemographicDTO demographic;

    @Valid
    private List<DocumentDTO> documents;

    @Valid
    private List<BiometricsDto> biometrics;

    public RegistrationRequestDTO() {
    }

    public RegistrationRequestDTO(String registrationId, DemographicDTO demographic, List<DocumentDTO> documents, List<BiometricsDto> biometrics) {
        this.registrationId = registrationId;
        this.demographic = demographic;
        this.documents = documents;
        this.biometrics = biometrics;
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
}
