package com.example.regclient_newVersion.applicant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ce_fingerprint_wsq", schema = "public")
public class CeFingerprintWsq {

    @Id
    @Column(name = "ID", length = 40, nullable = false)
    private String id;

    @Column(name = "RT_IMAGE")
    private byte[] rtImage;

    @Column(name = "RI_IMAGE")
    private byte[] riImage;

    @Column(name = "RM_IMAGE")
    private byte[] rmImage;

    @Column(name = "RR_IMAGE")
    private byte[] rrImage;

    @Column(name = "RL_IMAGE")
    private byte[] rlImage;

    @Column(name = "LT_IMAGE")
    private byte[] ltImage;

    @Column(name = "LI_IMAGE")
    private byte[] liImage;

    @Column(name = "LM_IMAGE")
    private byte[] lmImage;

    @Column(name = "LR_IMAGE")
    private byte[] lrImage;

    @Column(name = "LL_IMAGE")
    private byte[] llImage;


    // =========================
    // Getters and Setters
    // =========================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getRtImage() {
        return rtImage;
    }

    public void setRtImage(byte[] rtImage) {
        this.rtImage = rtImage;
    }

    public byte[] getRiImage() {
        return riImage;
    }

    public void setRiImage(byte[] riImage) {
        this.riImage = riImage;
    }

    public byte[] getRmImage() {
        return rmImage;
    }

    public void setRmImage(byte[] rmImage) {
        this.rmImage = rmImage;
    }

    public byte[] getRrImage() {
        return rrImage;
    }

    public void setRrImage(byte[] rrImage) {
        this.rrImage = rrImage;
    }

    public byte[] getRlImage() {
        return rlImage;
    }

    public void setRlImage(byte[] rlImage) {
        this.rlImage = rlImage;
    }

    public byte[] getLtImage() {
        return ltImage;
    }

    public void setLtImage(byte[] ltImage) {
        this.ltImage = ltImage;
    }

    public byte[] getLiImage() {
        return liImage;
    }

    public void setLiImage(byte[] liImage) {
        this.liImage = liImage;
    }

    public byte[] getLmImage() {
        return lmImage;
    }

    public void setLmImage(byte[] lmImage) {
        this.lmImage = lmImage;
    }

    public byte[] getLrImage() {
        return lrImage;
    }

    public void setLrImage(byte[] lrImage) {
        this.lrImage = lrImage;
    }

    public byte[] getLlImage() {
        return llImage;
    }

    public void setLlImage(byte[] llImage) {
        this.llImage = llImage;
    }
}