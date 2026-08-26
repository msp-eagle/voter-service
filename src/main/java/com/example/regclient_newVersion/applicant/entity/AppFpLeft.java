package com.example.regclient_newVersion.applicant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "app_fp_left", schema = "public")
public class AppFpLeft {

    @Id
    @Column(name = "ID", length = 40, nullable = false)
    private String id;

    @Column(name = "F6")
    private byte[] f6;

    @Column(name = "F7")
    private byte[] f7;

    @Column(name = "F8")
    private byte[] f8;

    @Column(name = "F9")
    private byte[] f9;

    @Column(name = "F10")
    private byte[] f10;

    /*
     * ID is also a foreign key to app_demo.ID.
     *
     * This makes the relationship:
     *
     * app_demo 1 <----> 1 app_fp_left
     */
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

    public byte[] getF6() {
        return f6;
    }

    public void setF6(byte[] f6) {
        this.f6 = f6;
    }

    public byte[] getF7() {
        return f7;
    }

    public void setF7(byte[] f7) {
        this.f7 = f7;
    }

    public byte[] getF8() {
        return f8;
    }

    public void setF8(byte[] f8) {
        this.f8 = f8;
    }

    public byte[] getF9() {
        return f9;
    }

    public void setF9(byte[] f9) {
        this.f9 = f9;
    }

    public byte[] getF10() {
        return f10;
    }

    public void setF10(byte[] f10) {
        this.f10 = f10;
    }

    public AppDemo getAppDemo() {
        return appDemo;
    }

    public void setAppDemo(AppDemo appDemo) {
        this.appDemo = appDemo;
    }
}