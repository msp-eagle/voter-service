package com.example.regclient_newVersion.Model;

import org.hibernate.annotations.Type;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "biometric_details")
public class BiometricDetails {

    @Id
    @Column(name = "registration_id", nullable = false, unique = true)
    private String registrationId;

    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "face", columnDefinition = "BYTEA")
    private byte[] face;

    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "left_iris", columnDefinition = "BYTEA")
    private byte[] leftIris;

    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "right_iris", columnDefinition = "BYTEA")
    private byte[] rightIris;

    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "left_thumb", columnDefinition = "BYTEA")
    private byte[] leftThumb;

    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "left_index_finger", columnDefinition = "BYTEA")
    private byte[] leftIndexFinger;

    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "left_middle_finger", columnDefinition = "BYTEA")
    private byte[] leftMiddleFinger;

    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "left_ring_finger", columnDefinition = "BYTEA")
    private byte[] leftRingFinger;

    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "left_little_finger", columnDefinition = "BYTEA")
    private byte[] leftLittleFinger;

    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "right_thumb", columnDefinition = "BYTEA")
    private byte[] rightThumb;

    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "right_index_finger", columnDefinition = "BYTEA")
    private byte[] rightIndexFinger;

    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "right_middle_finger", columnDefinition = "BYTEA")
    private byte[] rightMiddleFinger;

    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "right_ring_finger", columnDefinition = "BYTEA")
    private byte[] rightRingFinger;

    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "right_little_finger", columnDefinition = "BYTEA")
    private byte[] rightLittleFinger;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BiometricDetails() {
    }

    public BiometricDetails(String registrationId) {
        this.registrationId = registrationId;
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

    // Getters and Setters

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public byte[] getFace() {
        return face;
    }

    public void setFace(byte[] face) {
        this.face = face;
    }

    public byte[] getLeftIris() {
        return leftIris;
    }

    public void setLeftIris(byte[] leftIris) {
        this.leftIris = leftIris;
    }

    public byte[] getRightIris() {
        return rightIris;
    }

    public void setRightIris(byte[] rightIris) {
        this.rightIris = rightIris;
    }

    public byte[] getLeftThumb() {
        return leftThumb;
    }

    public void setLeftThumb(byte[] leftThumb) {
        this.leftThumb = leftThumb;
    }

    public byte[] getLeftIndexFinger() {
        return leftIndexFinger;
    }

    public void setLeftIndexFinger(byte[] leftIndexFinger) {
        this.leftIndexFinger = leftIndexFinger;
    }

    public byte[] getLeftMiddleFinger() {
        return leftMiddleFinger;
    }

    public void setLeftMiddleFinger(byte[] leftMiddleFinger) {
        this.leftMiddleFinger = leftMiddleFinger;
    }

    public byte[] getLeftRingFinger() {
        return leftRingFinger;
    }

    public void setLeftRingFinger(byte[] leftRingFinger) {
        this.leftRingFinger = leftRingFinger;
    }

    public byte[] getLeftLittleFinger() {
        return leftLittleFinger;
    }

    public void setLeftLittleFinger(byte[] leftLittleFinger) {
        this.leftLittleFinger = leftLittleFinger;
    }

    public byte[] getRightThumb() {
        return rightThumb;
    }

    public void setRightThumb(byte[] rightThumb) {
        this.rightThumb = rightThumb;
    }

    public byte[] getRightIndexFinger() {
        return rightIndexFinger;
    }

    public void setRightIndexFinger(byte[] rightIndexFinger) {
        this.rightIndexFinger = rightIndexFinger;
    }

    public byte[] getRightMiddleFinger() {
        return rightMiddleFinger;
    }

    public void setRightMiddleFinger(byte[] rightMiddleFinger) {
        this.rightMiddleFinger = rightMiddleFinger;
    }

    public byte[] getRightRingFinger() {
        return rightRingFinger;
    }

    public void setRightRingFinger(byte[] rightRingFinger) {
        this.rightRingFinger = rightRingFinger;
    }

    public byte[] getRightLittleFinger() {
        return rightLittleFinger;
    }

    public void setRightLittleFinger(byte[] rightLittleFinger) {
        this.rightLittleFinger = rightLittleFinger;
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
