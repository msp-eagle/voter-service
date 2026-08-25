package com.example.regclient_newVersion.Model;

import org.hibernate.annotations.Type;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voter_reg_details")
public class Registration {

    @Id
    @Column(name = "registration_id", nullable = false, unique = true)
    private String registrationId;

    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "demographic_data", columnDefinition = "TEXT")
    private String demographicData;

    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "documents_data", columnDefinition = "TEXT")
    private String documentsData;
    @Column(name = "vid")
    private String vid;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public Registration() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getDemographicData() {
        return demographicData;
    }

    public void setDemographicData(String demographicData) {
        this.demographicData = demographicData;
    }

    public String getDocumentsData() {
        return documentsData;
    }

    public void setDocumentsData(String documentsData) {
        this.documentsData = documentsData;
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
