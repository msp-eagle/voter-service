package com.example.regclient_newVersion.dto;

import java.util.List;

public class DashboardDTO {

    private long totalRegistrations;
    private long todayRegistrations;
    private long informationUpdates;
    private long replacementIdsIssued;
    private long totalVoters;
    private long activeRegistrations;
    private long pendingVerifications;
    private long biometricsCapturedCount;
    private long faceCapturedCount;
    private long fingerprintsCapturedCount;
    private long irisCapturedCount;
    private List<RegistrationResponseDTO> recentRegistrations;

    public DashboardDTO() {
    }

    public long getInformationUpdates() {
        return informationUpdates;
    }

    public void setInformationUpdates(long informationUpdates) {
        this.informationUpdates = informationUpdates;
    }

    public long getReplacementIdsIssued() {
        return replacementIdsIssued;
    }

    public void setReplacementIdsIssued(long replacementIdsIssued) {
        this.replacementIdsIssued = replacementIdsIssued;
    }

    public long getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(long totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
    }

    public long getTodayRegistrations() {
        return todayRegistrations;
    }

    public void setTodayRegistrations(long todayRegistrations) {
        this.todayRegistrations = todayRegistrations;
    }

    public long getTotalVoters() {
        return totalVoters;
    }

    public void setTotalVoters(long totalVoters) {
        this.totalVoters = totalVoters;
    }

    public long getActiveRegistrations() {
        return activeRegistrations;
    }

    public void setActiveRegistrations(long activeRegistrations) {
        this.activeRegistrations = activeRegistrations;
    }

    public long getPendingVerifications() {
        return pendingVerifications;
    }

    public void setPendingVerifications(long pendingVerifications) {
        this.pendingVerifications = pendingVerifications;
    }

    public long getBiometricsCapturedCount() {
        return biometricsCapturedCount;
    }

    public void setBiometricsCapturedCount(long biometricsCapturedCount) {
        this.biometricsCapturedCount = biometricsCapturedCount;
    }

    public long getFaceCapturedCount() {
        return faceCapturedCount;
    }

    public void setFaceCapturedCount(long faceCapturedCount) {
        this.faceCapturedCount = faceCapturedCount;
    }

    public long getFingerprintsCapturedCount() {
        return fingerprintsCapturedCount;
    }

    public void setFingerprintsCapturedCount(long fingerprintsCapturedCount) {
        this.fingerprintsCapturedCount = fingerprintsCapturedCount;
    }

    public long getIrisCapturedCount() {
        return irisCapturedCount;
    }

    public void setIrisCapturedCount(long irisCapturedCount) {
        this.irisCapturedCount = irisCapturedCount;
    }

    public List<RegistrationResponseDTO> getRecentRegistrations() {
        return recentRegistrations;
    }

    public void setRecentRegistrations(List<RegistrationResponseDTO> recentRegistrations) {
        this.recentRegistrations = recentRegistrations;
    }
}
