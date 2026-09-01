package com.example.regclient_newVersion.applicant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "app_photo")
public class AppPhoto {

    @Id
    @Column(name = "ID", length = 40, nullable = false)
    private String id;

    @Column(name = "P1")
    private byte[] p1;

    @OneToOne
    @JoinColumn(
        name = "ID",
        referencedColumnName = "ID"
    )
    private AppDemo appDemo;


    // =========================
    // Getters and Setters
    // =========================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getP1() {
        return p1;
    }

    public void setP1(byte[] p1) {
        this.p1 = p1;
    }

    public AppDemo getAppDemo() {
        return appDemo;
    }

    public void setAppDemo(AppDemo appDemo) {
        this.appDemo = appDemo;
    }
}