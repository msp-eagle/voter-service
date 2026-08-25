package com.example.regclient_newVersion.dto;

public class BiometricsDto {

    private String bioAttribute;
    private byte[] attributeISO;

    public BiometricsDto() {
    }

    public BiometricsDto(String bioAttribute, byte[] attributeISO) {
        this.bioAttribute = bioAttribute;
        this.attributeISO = attributeISO;
    }

    public String getBioAttribute() {
        return bioAttribute;
    }

    public void setBioAttribute(String bioAttribute) {
        this.bioAttribute = bioAttribute;
    }

    public byte[] getAttributeISO() {
        return attributeISO;
    }

    public void setAttributeISO(Object rawISO) {
        if (rawISO == null) {
            this.attributeISO = null;
            return;
        }
        if (rawISO instanceof byte[]) {
            this.attributeISO = (byte[]) rawISO;
        } else if (rawISO instanceof String) {
            String str = ((String) rawISO).trim();
            if (str.startsWith("data:") || str.contains(";base64,")) {
                str = str.contains(",") ? str.split(",")[1] : str;
            }
            try {
                this.attributeISO = java.util.Base64.getDecoder().decode(str.trim());
            } catch (Exception e) {
                // If non-Base64 string or image path is passed, convert to UTF-8 bytes safely without Jackson deserialization crash
                this.attributeISO = str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            }
        }
    }
}
