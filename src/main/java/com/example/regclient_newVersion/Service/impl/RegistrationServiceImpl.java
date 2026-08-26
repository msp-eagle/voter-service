package com.example.regclient_newVersion.Service.impl;

import com.example.regclient_newVersion.dto.BiometricsDto;
import com.example.regclient_newVersion.dto.DemographicDTO;
import com.example.regclient_newVersion.dto.DocumentDTO;
import com.example.regclient_newVersion.dto.RegistrationRequestDTO;
import com.example.regclient_newVersion.dto.RegistrationResponseDTO;
import com.example.regclient_newVersion.Model.BiometricDetails;
import com.example.regclient_newVersion.Model.Registration;
import com.example.regclient_newVersion.repository.BiometricDetailsRepository;
import com.example.regclient_newVersion.repository.RegistrationRepository;
import com.example.regclient_newVersion.Service.RegistrationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final BiometricDetailsRepository biometricDetailsRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public RegistrationServiceImpl(RegistrationRepository registrationRepository,
                                   BiometricDetailsRepository biometricDetailsRepository,
                                   ObjectMapper objectMapper) {
        this.registrationRepository = registrationRepository;
        this.biometricDetailsRepository = biometricDetailsRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public RegistrationResponseDTO saveRegistration(RegistrationRequestDTO requestDTO) {
        Registration entity = new Registration();
        entity.setRegistrationId(requestDTO.getRegistrationId());

        try {
            if (requestDTO.getDemographic() != null) {
                entity.setDemographicData(objectMapper.writeValueAsString(requestDTO.getDemographic()));
            }
            if (requestDTO.getDocuments() != null) {
                entity.setDocumentsData(objectMapper.writeValueAsString(requestDTO.getDocuments()));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing registration data to JSON", e);
        }
        String vid = null;
        if("NEW".equalsIgnoreCase(requestDTO.getRegType())) {
            vid = "63" +
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                    );

        }else {
          vid =  requestDTO.getOldRegId();
        }
        entity.setVid(vid);
        entity.setStatus("NEW");
        // Save biometrics into dedicated biometric_details table
        if (requestDTO.getBiometrics() != null && !requestDTO.getBiometrics().isEmpty()) {
            BiometricDetails biometricDetails = mapBiometricsListToEntity(requestDTO.getRegistrationId(), requestDTO.getBiometrics());
            biometricDetails.setVoterId(vid);
            biometricDetails.setStatus("NEW");
            biometricDetailsRepository.save(biometricDetails);
        }

        Registration savedEntity = registrationRepository.save(entity);


        return mapEntityToResponse(savedEntity);
    }

    @Override
    public Optional<RegistrationResponseDTO> getRegistrationById(String registrationId) {
        return registrationRepository.findById(registrationId)
                .map(this::mapEntityToResponse);
    }

    @Override
    public List<RegistrationResponseDTO> getAllRegistrations() {
        return registrationRepository.findAll().stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RegistrationResponseDTO addSignatureDocument(String registrationId, DocumentDTO signatureDocument) {
        Registration entity = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration record not found for ID: " + registrationId));

        List<DocumentDTO> documentsList = new ArrayList<>();
        try {
            if (entity.getDocumentsData() != null && !entity.getDocumentsData().isEmpty()) {
                documentsList = objectMapper.readValue(entity.getDocumentsData(), new TypeReference<List<DocumentDTO>>() {});
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error reading existing documents JSON data", e);
        }

        if (signatureDocument.getType() == null || signatureDocument.getType().isEmpty()) {
            signatureDocument.setType("SIGNATURE");
        }
        if (signatureDocument.getCategory() == null || signatureDocument.getCategory().isEmpty()) {
            signatureDocument.setCategory("SIGNATURE");
        }

        documentsList.removeIf(doc -> "SIGNATURE".equalsIgnoreCase(doc.getType()) || "SIGNATURE".equalsIgnoreCase(doc.getCategory()));
        documentsList.add(signatureDocument);

        try {
            entity.setDocumentsData(objectMapper.writeValueAsString(documentsList));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing updated documents list to JSON", e);
        }

        Registration savedEntity = registrationRepository.save(entity);
        return mapEntityToResponse(savedEntity);
    }

    @Override
    public RegistrationResponseDTO saveBiometrics(String registrationId, List<BiometricsDto> biometricsList) {
        Registration entity = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration record not found for ID: " + registrationId));

        if (biometricsList != null && !biometricsList.isEmpty()) {
            BiometricDetails biometricDetails = mapBiometricsListToEntity(registrationId, biometricsList);
            biometricDetailsRepository.save(biometricDetails);
        }

        return mapEntityToResponse(entity);
    }

    private RegistrationResponseDTO mapEntityToResponse(Registration entity) {
        DemographicDTO demographicDTO = null;
        List<DocumentDTO> documentsList = Collections.emptyList();

        try {
            if (entity.getDemographicData() != null && !entity.getDemographicData().isEmpty()) {
                String strData = entity.getDemographicData().trim();
                if (strData.startsWith("{")) {
                    demographicDTO = objectMapper.readValue(strData, DemographicDTO.class);
                }
            }
            if (entity.getDocumentsData() != null && !entity.getDocumentsData().isEmpty()) {
                String strData = entity.getDocumentsData().trim();
                if (strData.startsWith("[")) {
                    documentsList = objectMapper.readValue(strData, new TypeReference<List<DocumentDTO>>() {});
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: skipping legacy non-JSON string for registration " + entity.getRegistrationId() + ": " + e.getMessage());
        }

        // Fetch biometric details from dedicated biometric_details table
        BiometricDetails biometricDetails = biometricDetailsRepository.findById(entity.getRegistrationId()).orElse(null);
        List<BiometricsDto> biometricsList = mapEntityToBiometricsList(biometricDetails);

        return new RegistrationResponseDTO(
                entity.getRegistrationId(),
                demographicDTO,
                documentsList,
                biometricsList,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private BiometricDetails mapBiometricsListToEntity(String registrationId, List<BiometricsDto> biometricsList) {
        BiometricDetails entity = biometricDetailsRepository.findById(registrationId)
                .orElseGet(() -> new BiometricDetails(registrationId));

        if (biometricsList != null) {
            for (BiometricsDto dto : biometricsList) {
                if (dto.getBioAttribute() == null || dto.getAttributeISO() == null) {
                    continue;
                }
                String attr = dto.getBioAttribute().trim().toLowerCase();
                byte[] data = dto.getAttributeISO();

                // Decode Base64 string if passed as Data URL or ASCII string
                if (data != null && data.length > 0) {
                    try {
                        String strVal = new String(data, java.nio.charset.StandardCharsets.UTF_8);
                        if (strVal.startsWith("data:") || strVal.contains(";base64,")) {
                            String b64 = strVal.contains(",") ? strVal.split(",")[1] : strVal;
                            data = java.util.Base64.getDecoder().decode(b64.trim());
                        }
                    } catch (Exception ignored) {
                        // Keep raw binary data
                    }
                }

                // Convert incoming biometric image (Iris/Finger/Photo) to JPG format
                data = convertToJpgByteArray(data);

                switch (attr) {
                    case "face":
                        entity.setFace(data);
                        break;
                    case "left_iris":
                        entity.setLeftIris(data);
                        break;
                    case "right_iris":
                        entity.setRightIris(data);
                        break;
                    case "left_thumb":
                        entity.setLeftThumb(data);
                        break;
                    case "right_thumb":
                        entity.setRightThumb(data);
                        break;
                    case "left_index_finger":
                        entity.setLeftIndexFinger(data);
                        break;
                    case "right_index_finger":
                        entity.setRightIndexFinger(data);
                        break;
                    case "left_middle_finger":
                        entity.setLeftMiddleFinger(data);
                        break;
                    case "left_ring_finger":
                        entity.setLeftRingFinger(data);
                        break;
                    case "left_little_finger":
                        entity.setLeftLittleFinger(data);
                        break;
                    case "right_middle_finger":
                        entity.setRightMiddleFinger(data);
                        break;
                    case "right_ring_finger":
                        entity.setRightRingFinger(data);
                        break;
                    case "right_little_finger":
                        entity.setRightLittleFinger(data);
                        break;
                }
            }
        }
        return entity;
    }

    private byte[] convertToJpgByteArray(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            BufferedImage bufferedImage = ImageIO.read(bais);
            if (bufferedImage != null) {
                BufferedImage rgbImage = new BufferedImage(
                        bufferedImage.getWidth(),
                        bufferedImage.getHeight(),
                        BufferedImage.TYPE_INT_RGB
                );
                Graphics2D g = rgbImage.createGraphics();
                g.drawImage(bufferedImage, 0, 0, Color.WHITE, null);
                g.dispose();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(rgbImage, "jpg", baos);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            System.err.println("Note: ImageIO conversion to JPG skipped: " + e.getMessage());
        }
        return data;
    }

    private List<BiometricsDto> mapEntityToBiometricsList(BiometricDetails entity) {
        if (entity == null) {
            return Collections.emptyList();
        }
        List<BiometricsDto> list = new ArrayList<>();
        addBiometricsIfNotNull(list, "face", entity.getFace());
        addBiometricsIfNotNull(list, "left_iris", entity.getLeftIris());
        addBiometricsIfNotNull(list, "right_iris", entity.getRightIris());
        addBiometricsIfNotNull(list, "left_thumb", entity.getLeftThumb());
        addBiometricsIfNotNull(list, "left_index_finger", entity.getLeftIndexFinger());
        addBiometricsIfNotNull(list, "left_middle_finger", entity.getLeftMiddleFinger());
        addBiometricsIfNotNull(list, "left_ring_finger", entity.getLeftRingFinger());
        addBiometricsIfNotNull(list, "left_little_finger", entity.getLeftLittleFinger());
        addBiometricsIfNotNull(list, "right_thumb", entity.getRightThumb());
        addBiometricsIfNotNull(list, "right_index_finger", entity.getRightIndexFinger());
        addBiometricsIfNotNull(list, "right_middle_finger", entity.getRightMiddleFinger());
        addBiometricsIfNotNull(list, "right_ring_finger", entity.getRightRingFinger());
        addBiometricsIfNotNull(list, "right_little_finger", entity.getRightLittleFinger());
        return list;
    }

    private void addBiometricsIfNotNull(List<BiometricsDto> list, String attrName, byte[] data) {
        if (data != null && data.length > 0) {
            BiometricsDto dto = new BiometricsDto(attrName, data);
            list.add(dto);
        }
    }
}
