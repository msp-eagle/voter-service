package com.example.regclient_newVersion.dto;

import javax.validation.constraints.NotBlank;

public class DemographicDTO {

    private String registrationType;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String suffix;
    private String dobDay;
    private String dobMonth;
    private String dobYear;
    private Integer calculatedAge;

    private String pobCountry;
    private String pobProvince;
    private String pobCity;

    private String gender;
    private String residenceStatus;

    private String permanentCountry;
    private String permanentAddressLine1;
    private String permanentProvince;
    private String permanentCity;
    private String permanentBarangay;
    private String permanentZipcode;

    private Boolean addressCopy;

    private String presentCountry;
    private String presentAddressLine1;
    private String presentProvince;
    private String presentCity;
    private String presentBarangay;
    private String presentZipcode;
    private String presentPrecinct;



    public DemographicDTO() {
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getDobDay() {
        return dobDay;
    }

    public void setDobDay(String dobDay) {
        this.dobDay = dobDay;
    }

    public String getDobMonth() {
        return dobMonth;
    }

    public void setDobMonth(String dobMonth) {
        this.dobMonth = dobMonth;
    }

    public String getDobYear() {
        return dobYear;
    }

    public void setDobYear(String dobYear) {
        this.dobYear = dobYear;
    }

    public Integer getCalculatedAge() {
        return calculatedAge;
    }

    public void setCalculatedAge(Integer calculatedAge) {
        this.calculatedAge = calculatedAge;
    }

    public String getPobCountry() {
        return pobCountry;
    }

    public void setPobCountry(String pobCountry) {
        this.pobCountry = pobCountry;
    }

    public String getPobProvince() {
        return pobProvince;
    }

    public void setPobProvince(String pobProvince) {
        this.pobProvince = pobProvince;
    }

    public String getPobCity() {
        return pobCity;
    }

    public void setPobCity(String pobCity) {
        this.pobCity = pobCity;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getResidenceStatus() {
        return residenceStatus;
    }

    public void setResidenceStatus(String residenceStatus) {
        this.residenceStatus = residenceStatus;
    }

    public String getPermanentCountry() {
        return permanentCountry;
    }

    public void setPermanentCountry(String permanentCountry) {
        this.permanentCountry = permanentCountry;
    }

    public String getPermanentAddressLine1() {
        return permanentAddressLine1;
    }

    public void setPermanentAddressLine1(String permanentAddressLine1) {
        this.permanentAddressLine1 = permanentAddressLine1;
    }

    public String getPermanentProvince() {
        return permanentProvince;
    }

    public void setPermanentProvince(String permanentProvince) {
        this.permanentProvince = permanentProvince;
    }

    public String getPermanentCity() {
        return permanentCity;
    }

    public void setPermanentCity(String permanentCity) {
        this.permanentCity = permanentCity;
    }

    public String getPermanentBarangay() {
        return permanentBarangay;
    }

    public void setPermanentBarangay(String permanentBarangay) {
        this.permanentBarangay = permanentBarangay;
    }

    public String getPermanentZipcode() {
        return permanentZipcode;
    }

    public void setPermanentZipcode(String permanentZipcode) {
        this.permanentZipcode = permanentZipcode;
    }

    public Boolean getAddressCopy() {
        return addressCopy;
    }

    public void setAddressCopy(Boolean addressCopy) {
        this.addressCopy = addressCopy;
    }

    public String getPresentCountry() {
        return presentCountry;
    }

    public void setPresentCountry(String presentCountry) {
        this.presentCountry = presentCountry;
    }

    public String getPresentAddressLine1() {
        return presentAddressLine1;
    }

    public void setPresentAddressLine1(String presentAddressLine1) {
        this.presentAddressLine1 = presentAddressLine1;
    }

    public String getPresentProvince() {
        return presentProvince;
    }

    public void setPresentProvince(String presentProvince) {
        this.presentProvince = presentProvince;
    }

    public String getPresentCity() {
        return presentCity;
    }

    public void setPresentCity(String presentCity) {
        this.presentCity = presentCity;
    }

    public String getPresentBarangay() {
        return presentBarangay;
    }

    public void setPresentBarangay(String presentBarangay) {
        this.presentBarangay = presentBarangay;
    }

    public String getPresentZipcode() {
        return presentZipcode;
    }

    public void setPresentZipcode(String presentZipcode) {
        this.presentZipcode = presentZipcode;
    }

    public String getPresentPrecinct() {
        return presentPrecinct;
    }

    public void setPresentPrecinct(String presentPrecinct) {
        this.presentPrecinct = presentPrecinct;
    }

}
