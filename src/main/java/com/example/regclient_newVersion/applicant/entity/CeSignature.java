package com.example.regclient_newVersion.applicant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "ce_signature", schema = "public")
public class CeSignature {

    @Id
    @Column(name = "ID", length = 40, nullable = false)
    private String id;

    @Lob
    @Column(name = "IMAGE", nullable = false)
    private byte[] image;


    // =========================
    // Getters and Setters
    // =========================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}