package com.example.regclient_newVersion.dto;
public class SignatureResponse {

    private boolean success;
    private String message;
    private String signatureBase64;

    public SignatureResponse() {
    }

    public SignatureResponse(
            boolean success,
            String message,
            String signatureBase64) {

        this.success = success;
        this.message = message;
        this.signatureBase64 = signatureBase64;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSignatureBase64() {
        return signatureBase64;
    }

    public void setSignatureBase64(String signatureBase64) {
        this.signatureBase64 = signatureBase64;
    }
}

