package com.example.regclient_newVersion.Service;

import com.example.regclient_newVersion.dto.BiometricsDto;
import com.example.regclient_newVersion.dto.DocumentDTO;
import com.example.regclient_newVersion.dto.RegistrationRequestDTO;
import com.example.regclient_newVersion.dto.RegistrationResponseDTO;

import java.util.List;
import java.util.Optional;

public interface RegistrationService {
    RegistrationResponseDTO saveRegistration(RegistrationRequestDTO requestDTO);
    Optional<RegistrationResponseDTO> getRegistrationById(String registrationId);
    List<RegistrationResponseDTO> getAllRegistrations();
    RegistrationResponseDTO addSignatureDocument(String registrationId, DocumentDTO signatureDocument);
    RegistrationResponseDTO saveBiometrics(String registrationId, List<BiometricsDto> biometricsList);
}
