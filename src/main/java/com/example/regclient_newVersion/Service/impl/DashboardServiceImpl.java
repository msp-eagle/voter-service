package com.example.regclient_newVersion.Service.impl;

import com.example.regclient_newVersion.Model.Registration;
import com.example.regclient_newVersion.Service.DashboardService;
import com.example.regclient_newVersion.Service.RegistrationService;
import com.example.regclient_newVersion.dto.DashboardDTO;
import com.example.regclient_newVersion.dto.RegistrationResponseDTO;
import com.example.regclient_newVersion.repository.BiometricDetailsRepository;
import com.example.regclient_newVersion.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final RegistrationRepository registrationRepository;
    private final BiometricDetailsRepository biometricDetailsRepository;
    private final RegistrationService registrationService;

    @Autowired
    public DashboardServiceImpl(RegistrationRepository registrationRepository,
                                 BiometricDetailsRepository biometricDetailsRepository,
                                 RegistrationService registrationService) {
        this.registrationRepository = registrationRepository;
        this.biometricDetailsRepository = biometricDetailsRepository;
        this.registrationService = registrationService;
    }

    @Override
    public DashboardDTO getDashboardMetrics() {
        DashboardDTO dto = new DashboardDTO();

        long totalRegs = registrationRepository.count();
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        long todayRegs = registrationRepository.countByCreatedAtAfter(todayStart);

        long biometricsCount = biometricDetailsRepository.count();
        long faceCount = biometricDetailsRepository.countByFaceIsNotNull();
        long irisCount = biometricDetailsRepository.countByIrisIsNotNull();
        long fingerprintCount = biometricDetailsRepository.countByFingerprintsIsNotNull();

        List<Registration> allRegs = registrationRepository.findAll();
        long updatesCount = 0;
        long replacementCount = 0;
        for (Registration reg : allRegs) {
            String demoData = reg.getDemographicData();
            if (demoData != null) {
                if (demoData.contains("UPDATE_VOTER_INFO")) {
                    updatesCount++;
                }
                if (demoData.contains("LOST_VOTER")) {
                    replacementCount++;
                }
            }
        }

        dto.setTotalRegistrations(totalRegs);
        dto.setTodayRegistrations(todayRegs);
        dto.setInformationUpdates(updatesCount);
        dto.setReplacementIdsIssued(replacementCount);
        dto.setTotalVoters(totalRegs);
        dto.setActiveRegistrations(totalRegs);
        dto.setPendingVerifications(0);

        dto.setBiometricsCapturedCount(biometricsCount);
        dto.setFaceCapturedCount(faceCount);
        dto.setFingerprintsCapturedCount(fingerprintCount);
        dto.setIrisCapturedCount(irisCount);

        List<Registration> recentRegEntities = registrationRepository.findTop10ByOrderByCreatedAtDesc();
        List<RegistrationResponseDTO> recentDtos = new ArrayList<>();
        for (Registration reg : recentRegEntities) {
            registrationService.getRegistrationById(reg.getRegistrationId()).ifPresent(recentDtos::add);
        }
        dto.setRecentRegistrations(recentDtos);

        return dto;
    }
}
