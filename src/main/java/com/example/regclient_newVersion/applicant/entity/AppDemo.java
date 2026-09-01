package com.example.regclient_newVersion.applicant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_demo")
public class AppDemo {

    @Id
    @Column(name = "ID", length = 40, nullable = false)
    private String id;

    @Column(name = "FORM_ID", length = 75)
    private String formId;

    @Column(name = "APP_TYPE", length = 1, nullable = false)
    private String appType;

    @Column(name = "REGISTRATION", length = 1)
    private String registration;

    @Column(name = "LASTNAME", length = 100)
    private String lastname;

    @Column(name = "FIRSTNAME", length = 100)
    private String firstname;

    @Column(name = "MATERNALNAME", length = 100)
    private String maternalName;

    @Column(name = "SEX", length = 100)
    private String sex;

    @Column(name = "CIVILSTATUS", length = 100)
    private String civilStatus;

    @Column(name = "RESSTREET", length = 200)
    private String resStreet;

    @Column(name = "RESPRECINCT", length = 6)
    private String resPrecinct;

    @Column(name = "RESPRECINCTCODE", length = 2)
    private String resPrecinctCode;

    @Column(name = "RESBARANGAY", length = 3)
    private String resBarangay;

    @Column(name = "RESCITY", length = 2)
    private String resCity;

    @Column(name = "RESPROVINCE", length = 2)
    private String resProvince;

    @Column(name = "ABSENTIA", length = 1)
    private String absentia;

    @Column(name = "DOBYEAR", length = 100)
    private String dobYear;

    @Column(name = "DOBMONTH", length = 100)
    private String dobMonth;

    @Column(name = "DOBDAY", length = 100)
    private String dobDay;

    @Column(name = "CITIZENSHIP", length = 1)
    private String citizenship;

    @Column(name = "CITYRESYEAR", length = 3)
    private String cityResYear;

    @Column(name = "CITYRESMONTH", length = 2)
    private String cityResMonth;

    @Column(name = "SECTOR", length = 3)
    private String sector;

    @Column(name = "MARKS", length = 2)
    private String marks;

    @Column(name = "DISABLED", length = 1)
    private String disabled;

    @Column(name = "ASSISTEDBY", length = 40)
    private String assistedBy;

    @Column(name = "VINP1", length = 100)
    private String vinp1;

    @Column(name = "VINP2", length = 100)
    private String vinp2;

    @Column(name = "VINP3", length = 100)
    private String vinp3;

    @Column(name = "VINCONTROLCODE", length = 1)
    private String vinControlCode;

    @Column(name = "REGBARANGAY", length = 3)
    private String regBarangay;

    @Column(name = "REGCITY", length = 2)
    private String regCity;

    @Column(name = "REGPROVINCE", length = 2)
    private String regProvince;

    @Column(name = "REG_DATE")
    private LocalDateTime regDate;

    @Column(name = "INTERNAME", length = 40)
    private String interName;

    @Column(name = "OFFICERNAME", length = 40)
    private String officerName;

    @Column(name = "OPERNAME", length = 20)
    private String operName;

    @Column(name = "CDID", length = 15)
    private String cdid;

    @Column(name = "PRINT_FLAG", length = 19)
    private String printFlag;

    @Column(name = "FINGER_INFO", length = 19)
    private String fingerInfo;

    @Column(name = "PAGES_DESCR", length = 50)
    private String pagesDescr;

    @Column(name = "TRANSFER_UPDATE_TIME")
    private LocalDateTime transferUpdateTime;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

    @Column(name = "DISAPPROVED", length = 1)
    private String disapproved;

    @Column(name = "VOTING_HIST1", length = 20)
    private String votingHist1;

    @Column(name = "VOTING_HIST2", length = 20)
    private String votingHist2;

    @Column(name = "OP_CODE", length = 3)
    private String opCode;

    @Column(name = "OP_DATE")
    private LocalDateTime opDate;

    @Column(name = "TYPE_ASSISTANCE", length = 10)
    private String typeAssistance;

    @Column(name = "DETAINEE")
    private Integer detainee;

    @Column(name = "ID_ON_HAND")
    private Integer idOnHand;

    @Column(name = "SENIOR")
    private Integer senior;

    @Column(name = "SUFFIX", length = 50)
    private String suffix;

    @Column(name = "RAP", length = 1)
    private String rap;

    @Column(name = "EXTRA", length = 50)
    private String extra;

    @Column(name = "MDISABLED", length = 50)
    private String mdisabled;

    @Column(name = "BARMM1", length = 21)
    private String barmm1;

    @Column(name = "BARMM2", length = 22)
    private String barmm2;

    @Column(name = "EXTRA2", length = 10)
    private String extra2;


    // =========================
    // Getters and Setters
    // =========================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMaternalName() {
        return maternalName;
    }

    public void setMaternalName(String maternalName) {
        this.maternalName = maternalName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(String civilStatus) {
        this.civilStatus = civilStatus;
    }

    public String getResStreet() {
        return resStreet;
    }

    public void setResStreet(String resStreet) {
        this.resStreet = resStreet;
    }

    public String getResPrecinct() {
        return resPrecinct;
    }

    public void setResPrecinct(String resPrecinct) {
        this.resPrecinct = resPrecinct;
    }

    public String getResPrecinctCode() {
        return resPrecinctCode;
    }

    public void setResPrecinctCode(String resPrecinctCode) {
        this.resPrecinctCode = resPrecinctCode;
    }

    public String getResBarangay() {
        return resBarangay;
    }

    public void setResBarangay(String resBarangay) {
        this.resBarangay = resBarangay;
    }

    public String getResCity() {
        return resCity;
    }

    public void setResCity(String resCity) {
        this.resCity = resCity;
    }

    public String getResProvince() {
        return resProvince;
    }

    public void setResProvince(String resProvince) {
        this.resProvince = resProvince;
    }

    public String getAbsentia() {
        return absentia;
    }

    public void setAbsentia(String absentia) {
        this.absentia = absentia;
    }

    public String getDobYear() {
        return dobYear;
    }

    public void setDobYear(String dobYear) {
        this.dobYear = dobYear;
    }

    public String getDobMonth() {
        return dobMonth;
    }

    public void setDobMonth(String dobMonth) {
        this.dobMonth = dobMonth;
    }

    public String getDobDay() {
        return dobDay;
    }

    public void setDobDay(String dobDay) {
        this.dobDay = dobDay;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getCityResYear() {
        return cityResYear;
    }

    public void setCityResYear(String cityResYear) {
        this.cityResYear = cityResYear;
    }

    public String getCityResMonth() {
        return cityResMonth;
    }

    public void setCityResMonth(String cityResMonth) {
        this.cityResMonth = cityResMonth;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getDisabled() {
        return disabled;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public String getAssistedBy() {
        return assistedBy;
    }

    public void setAssistedBy(String assistedBy) {
        this.assistedBy = assistedBy;
    }

    public String getVinp1() {
        return vinp1;
    }

    public void setVinp1(String vinp1) {
        this.vinp1 = vinp1;
    }

    public String getVinp2() {
        return vinp2;
    }

    public void setVinp2(String vinp2) {
        this.vinp2 = vinp2;
    }

    public String getVinp3() {
        return vinp3;
    }

    public void setVinp3(String vinp3) {
        this.vinp3 = vinp3;
    }

    public String getVinControlCode() {
        return vinControlCode;
    }

    public void setVinControlCode(String vinControlCode) {
        this.vinControlCode = vinControlCode;
    }

    public String getRegBarangay() {
        return regBarangay;
    }

    public void setRegBarangay(String regBarangay) {
        this.regBarangay = regBarangay;
    }

    public String getRegCity() {
        return regCity;
    }

    public void setRegCity(String regCity) {
        this.regCity = regCity;
    }

    public String getRegProvince() {
        return regProvince;
    }

    public void setRegProvince(String regProvince) {
        this.regProvince = regProvince;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }

    public void setRegDate(LocalDateTime regDate) {
        this.regDate = regDate;
    }

    public String getInterName() {
        return interName;
    }

    public void setInterName(String interName) {
        this.interName = interName;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public String getCdid() {
        return cdid;
    }

    public void setCdid(String cdid) {
        this.cdid = cdid;
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag;
    }

    public String getFingerInfo() {
        return fingerInfo;
    }

    public void setFingerInfo(String fingerInfo) {
        this.fingerInfo = fingerInfo;
    }

    public String getPagesDescr() {
        return pagesDescr;
    }

    public void setPagesDescr(String pagesDescr) {
        this.pagesDescr = pagesDescr;
    }

    public LocalDateTime getTransferUpdateTime() {
        return transferUpdateTime;
    }

    public void setTransferUpdateTime(LocalDateTime transferUpdateTime) {
        this.transferUpdateTime = transferUpdateTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getDisapproved() {
        return disapproved;
    }

    public void setDisapproved(String disapproved) {
        this.disapproved = disapproved;
    }

    public String getVotingHist1() {
        return votingHist1;
    }

    public void setVotingHist1(String votingHist1) {
        this.votingHist1 = votingHist1;
    }

    public String getVotingHist2() {
        return votingHist2;
    }

    public void setVotingHist2(String votingHist2) {
        this.votingHist2 = votingHist2;
    }

    public String getOpCode() {
        return opCode;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public LocalDateTime getOpDate() {
        return opDate;
    }

    public void setOpDate(LocalDateTime opDate) {
        this.opDate = opDate;
    }

    public String getTypeAssistance() {
        return typeAssistance;
    }

    public void setTypeAssistance(String typeAssistance) {
        this.typeAssistance = typeAssistance;
    }

    public Integer getDetainee() {
        return detainee;
    }

    public void setDetainee(Integer detainee) {
        this.detainee = detainee;
    }

    public Integer getIdOnHand() {
        return idOnHand;
    }

    public void setIdOnHand(Integer idOnHand) {
        this.idOnHand = idOnHand;
    }

    public Integer getSenior() {
        return senior;
    }

    public void setSenior(Integer senior) {
        this.senior = senior;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getRap() {
        return rap;
    }

    public void setRap(String rap) {
        this.rap = rap;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getMdisabled() {
        return mdisabled;
    }

    public void setMdisabled(String mdisabled) {
        this.mdisabled = mdisabled;
    }

    public String getBarmm1() {
        return barmm1;
    }

    public void setBarmm1(String barmm1) {
        this.barmm1 = barmm1;
    }

    public String getBarmm2() {
        return barmm2;
    }

    public void setBarmm2(String barmm2) {
        this.barmm2 = barmm2;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }
}