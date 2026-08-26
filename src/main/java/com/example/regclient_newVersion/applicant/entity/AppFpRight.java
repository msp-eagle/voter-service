package com.example.regclient_newVersion.applicant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "app_fp_right", schema = "public")
public class AppFpRight {

    @Id
    @Column(name = "ID", length = 40, nullable = false)
    private String id;

    @Column(name = "F1")
    private byte[] f1;

    @Column(name = "F2")
    private byte[] f2;

    @Column(name = "F3")
    private byte[] f3;

    @Column(name = "F4")
    private byte[] f4;

    @Column(name = "F5")
    private byte[] f5;

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

    public byte[] getF1() {
        return f1;
    }

    public void setF1(byte[] f1) {
        this.f1 = f1;
    }

    public byte[] getF2() {
        return f2;
    }

    public void setF2(byte[] f2) {
        this.f2 = f2;
    }

    public byte[] getF3() {
        return f3;
    }

    public void setF3(byte[] f3) {
        this.f3 = f3;
    }

    public byte[] getF4() {
        return f4;
    }

    public void setF4(byte[] f4) {
        this.f4 = f4;
    }

    public byte[] getF5() {
        return f5;
    }

    public void setF5(byte[] f5) {
        this.f5 = f5;
    }

    public AppDemo getAppDemo() {
        return appDemo;
    }

    public void setAppDemo(AppDemo appDemo) {
        this.appDemo = appDemo;
    }
}