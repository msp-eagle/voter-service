package com.example.regclient_newVersion.applicant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "app_sign")
public class AppSign {

    @Id
    @Column(name = "ID", length = 40, nullable = false)
    private String id;

    @Column(name = "S1")
    private byte[] s1;

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

    public byte[] getS1() {
        return s1;
    }

    public void setS1(byte[] s1) {
        this.s1 = s1;
    }

    public AppDemo getAppDemo() {
        return appDemo;
    }

    public void setAppDemo(AppDemo appDemo) {
        this.appDemo = appDemo;
    }
}