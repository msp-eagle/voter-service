package com.example.regclient_newVersion.Controller;

import com.example.regclient_newVersion.dto.BiometricsDto;
import com.example.regclient_newVersion.dto.DocumentDTO;
import com.example.regclient_newVersion.dto.RegistrationRequestDTO;
import com.example.regclient_newVersion.dto.RegistrationResponseDTO;
import com.example.regclient_newVersion.Service.RegistrationService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<RegistrationResponseDTO> saveRegistration(@Valid @RequestBody RegistrationRequestDTO requestDTO) {
        RegistrationResponseDTO response = registrationService.saveRegistration(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{registrationId}")
    public ResponseEntity<RegistrationResponseDTO> getRegistrationById(@PathVariable String registrationId) {
        return registrationService.getRegistrationById(registrationId)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<RegistrationResponseDTO>> getAllRegistrations() {
        List<RegistrationResponseDTO> list = registrationService.getAllRegistrations();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/{registrationId}/signature")
    public ResponseEntity<RegistrationResponseDTO> addSignature(
            @PathVariable String registrationId,
            @Valid @RequestBody DocumentDTO signatureDocument) {
        RegistrationResponseDTO response = registrationService.addSignatureDocument(registrationId, signatureDocument);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{registrationId}/biometrics")
    public ResponseEntity<RegistrationResponseDTO> saveBiometrics(
            @PathVariable String registrationId,
            @RequestBody List<BiometricsDto> biometrics) {
        RegistrationResponseDTO response = registrationService.saveBiometrics(registrationId, biometrics);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
