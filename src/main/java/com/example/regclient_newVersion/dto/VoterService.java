package com.example.regclient_newVersion.dto;

import com.example.regclient_newVersion.applicant.entity.AppDemo;
import com.example.regclient_newVersion.applicant.entity.AppPhoto;
import com.example.regclient_newVersion.applicant.entity.DocTable;
import com.example.regclient_newVersion.applicant.repository.AppDemoRepo;
import com.example.regclient_newVersion.applicant.repository.AppPhotoRepo;


import com.example.regclient_newVersion.applicant.repository.DocTableRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class VoterService {

    private final AppDemoRepo appDemoRepository;
    private final AppPhotoRepo appPhotoRepository;
    private final DocTableRepo docTableRepo;

    public VoterService(
            AppDemoRepo appDemoRepository,
            AppPhotoRepo appPhotoRepository, DocTableRepo docTableRepo) {

        this.appDemoRepository = appDemoRepository;
        this.appPhotoRepository = appPhotoRepository;
        this.docTableRepo = docTableRepo;
    }

    public VoterSearchResponse searchVoter(String id) {

        AppDemo appDemo = appDemoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Voter not found: " + id)
                );

        VoterSearchResponse response = new VoterSearchResponse();

        response.setVoterId(appDemo.getId());

        response.setFirstName(appDemo.getFirstname());
        response.setLastName(appDemo.getLastname());
        response.setMaternalName(appDemo.getMaternalName());
        response.setSuffix(appDemo.getSuffix());

        response.setSex(appDemo.getSex());
        response.setCivilStatus(appDemo.getCivilStatus());

        response.setDateOfBirth(
                buildDateOfBirth(
                        appDemo.getDobYear(),
                        appDemo.getDobMonth(),
                        appDemo.getDobDay()
                )
        );

        response.setAddress(appDemo.getResStreet());

        response.setPrecinct(appDemo.getResPrecinct());
        response.setBarangay(appDemo.getResBarangay());
        response.setCity(appDemo.getResCity());
        response.setProvince(appDemo.getResProvince());

        // Get photo
        AppPhoto photo = appPhotoRepository
                .findById(id)
                .orElse(null);

        if (photo != null && photo.getP1() != null) {

            response.setPhoto(
                    Base64.getEncoder()
                            .encodeToString(photo.getP1())
            );
        }

        return response;
    }


    private String buildDateOfBirth(
            String year,
            String month,
            String day) {

        if (year == null || month == null || day == null) {
            return null;
        }

        return year + "-" + month + "-" + day;
    }

    public ResponseEntity<DocTable> getById(String id) {
        DocTable docTable = docTableRepo.findById(id).orElseThrow(
                ()-> new RuntimeException("Entity Not Found")
        );

        return ResponseEntity.ok(docTable);
    }

    public ResponseEntity<AppDemo> getAppById(String id) {
        AppDemo appDemo = appDemoRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Entity Not Found")
        );

        return ResponseEntity.ok(appDemo);
    }

    public ResponseEntity<List<AppDemo>> findAll() {
        return ResponseEntity.ok(appDemoRepository.findAll());
    }
}