package com.example.regclient_newVersion.applicant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "doctable")
public class DocTable {

    @Id
    @Column(name = "ID", length = 40)
    private String id;

    @Column(name = "APPLICATION_ID", length = 11)
    private String applicationId;

    @Column(name = "FORM_ID", length = 75)
    private String formId;

    @Column(name = "APP_TYPE", length = 1)
    private String appType;

    @Column(name = "ABSENTEE", length = 1)
    private String absentee;

    @Column(name = "REGISTRATION", length = 1)
    private String registration;

    @Column(name = "LASTNAME", length = 100)
    private String lastname;

    @Column(name = "FIRSTNAME", length = 100)
    private String firstname;

    @Column(name = "MATERNALNAME", length = 100)
    private String maternalName;

    @Column(name = "SEX", length = 1)
    private String sex;

    @Column(name = "CIVILSTATUS", length = 1)
    private String civilStatus;

    @Column(name = "SPOUSENAME", length = 100)
    private String spouseName;

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

    @Column(name = "ABROADSTREET", length = 80)
    private String abroadStreet;

    @Column(name = "ABROADZIP", length = 50)
    private String abroadZip;

    @Column(name = "ABSENTIA", length = 1)
    private String absentia;

    @Column(name = "ABROADCITY", length = 20)
    private String abroadCity;

    @Column(name = "ABROADCOUNTRY", length = 2)
    private String abroadCountry;

    @Column(name = "ABROADPERIOD", length = 2)
    private String abroadPeriod;

    @Column(name = "ABROADRESCONT", length = 1)
    private String abroadResCont;

    @Column(name = "REGCOUNTRY", length = 2)
    private String regCountry;

    @Column(name = "REGEMBASSY", length = 2)
    private String regEmbassy;

    @Column(name = "MAILSTREET", length = 80)
    private String mailStreet;

    @Column(name = "MAILZIP", length = 10)
    private String mailZip;

    @Column(name = "MAILCITY", length = 20)
    private String mailCity;

    @Column(name = "MAILCOUNTRY", length = 2)
    private String mailCountry;

    @Column(name = "MAILEMBASSY", length = 2)
    private String mailEmbassy;

    @Column(name = "REPSTREET", length = 80)
    private String repStreet;

    @Column(name = "REPBARANGAY", length = 3)
    private String repBarangay;

    @Column(name = "REPCITY", length = 2)
    private String repCity;

    @Column(name = "REPPROVINCE", length = 2)
    private String repProvince;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "ABROADSTATUS", length = 1)
    private String abroadStatus;

    @Column(name = "ABROADSTATUSSPECIF", length = 30)
    private String abroadStatusSpecif;

    @Column(name = "LASTENTRYDATE", length = 40)
    private String lastEntryDate;

    @Column(name = "ABSREGISTERED", length = 1)
    private String absRegistered;

    @Column(name = "OLDPRECINCT", length = 5)
    private String oldPrecinct;

    @Column(name = "OLDREGBARANGAY", length = 3)
    private String oldRegBarangay;

    @Column(name = "OLDREGCITY", length = 2)
    private String oldRegCity;

    @Column(name = "OLDREGPROVINCE", length = 2)
    private String oldRegProvince;

    @Column(name = "OLDREGDATE", length = 40)
    private String oldRegDate;

    @Column(name = "FLASTNAME", length = 100)
    private String flastname;

    @Column(name = "FFIRSTNAME", length = 100)
    private String ffirstname;

    @Column(name = "FMATERNALNAME", length = 100)
    private String fmaternalName;

    @Column(name = "MLASTNAME", length = 100)
    private String mlastname;

    @Column(name = "MFIRSTNAME", length = 100)
    private String mfirstname;

    @Column(name = "MMATERNALNAME", length = 100)
    private String mmaternalName;

    @Column(name = "REPLASTNAME", length = 40)
    private String replastname;

    @Column(name = "REPFIRSTNAME", length = 40)
    private String repfirstname;

    @Column(name = "REPMATERNALNAME", length = 40)
    private String repmaternalName;

    @Column(name = "DOBYEAR", length = 100)
    private String dobYear;

    @Column(name = "DOBMONTH", length = 100)
    private String dobMonth;

    @Column(name = "DOBDAY", length = 100)
    private String dobDay;

    @Column(name = "BIRTHCITY", length = 2)
    private String birthCity;

    @Column(name = "BIRTHPROVINCE", length = 2)
    private String birthProvince;

    @Column(name = "CITIZENSHIP", length = 1)
    private String citizenship;

    @Column(name = "NATURALIZATIONDATE", length = 40)
    private String naturalizationDate;

    @Column(name = "CERTIFICATENB", length = 10)
    private String certificateNb;

    @Column(name = "COUNTRYRES", length = 3)
    private String countryRes;

    @Column(name = "CITYRESYEAR", length = 3)
    private String cityResYear;

    @Column(name = "CITYRESMONTH", length = 2)
    private String cityResMonth;

    @Column(name = "PROFESSION", length = 6)
    private String profession;

    @Column(name = "SECTOR", length = 3)
    private String sector;

    @Column(name = "HEIGHT", length = 4)
    private String height;

    @Column(name = "WEIGHT", length = 3)
    private String weight;

    @Column(name = "MARKS", length = 2)
    private String marks;

    @Column(name = "DISABLED", length = 1)
    private String disabled;

    @Column(name = "ASSISTEDBY", length = 40)
    private String assistedBy;

    @Column(name = "OLD_VIN", length = 21)
    private String oldVin;

    @Column(name = "VINP1", length = 100)
    private String vinp1;

    @Column(name = "VINP2", length = 100)
    private String vinp2;

    @Column(name = "VINP3", length = 100)
    private String vinp3;

    @Column(name = "VINCONTROLCODE", length = 1)
    private String vinControlCode;

    @Column(name = "TIN", length = 100)
    private String tin;

    @Column(name = "PASSPORTLOST", length = 1)
    private String passportLost;

    @Column(name = "PASSPORTNB", length = 10)
    private String passportNb;

    @Column(name = "PASSPORTPLACE", length = 20)
    private String passportPlace;

    @Column(name = "PASSYEAR", length = 4)
    private String passYear;

    @Column(name = "PASSMONTH", length = 2)
    private String passMonth;

    @Column(name = "PASSDAY", length = 2)
    private String passDay;

    @Column(name = "REGBARANGAY", length = 3)
    private String regBarangay;

    @Column(name = "REGCITY", length = 2)
    private String regCity;

    @Column(name = "REGPROVINCE", length = 2)
    private String regProvince;

    @Column(name = "REG_DATE", length = 40)
    private String regDate;

    @Column(name = "INTERNAME", length = 40)
    private String interName;

    @Column(name = "OFFICERNAME", length = 40)
    private String officerName;

    @Column(name = "OPERNAME", length = 20)
    private String operName;

    @Column(name = "STATIONID", length = 5)
    private String stationId;

    @Column(name = "CDID", length = 20)
    private String cdid;

    @Column(name = "SETID", length = 20)
    private String setId;

    @Column(name = "PRINT_FLAG", length = 19)
    private String printFlag;

    @Column(name = "FINGER_INFO", length = 19)
    private String fingerInfo;

    @Column(name = "FINGER_TOPO_COORD", length = 73)
    private String fingerTopoCoord;

    @Column(name = "QUALITY", length = 21)
    private String quality;

    @Column(name = "MATCHING_FINGER", length = 1)
    private String matchingFinger;

    @Column(name = "TRANSFER_STATUS", length = 50)
    private String transferStatus;

    @Column(name = "TRANSFER_UPDATE_TIME", length = 40)
    private String transferUpdateTime;

    @Column(name = "PAGES_DESCR", length = 50)
    private String pagesDescr;

    @Column(name = "LOCAL_ID", length = 50)
    private String localId;

    @Column(name = "CREATE_TIME", length = 40)
    private String createTime;

    @Column(name = "UPDATE_TIME", length = 40)
    private String updateTime;

    @Column(name = "LOCK_USER", length = 22)
    private String lockUser;

    @Column(name = "LOCK_TIME", length = 40)
    private String lockTime;

    @Column(name = "PROCESSING", length = 1)
    private String processing;

    @Column(name = "IS_CURRENT", length = 1)
    private String isCurrent;

    @Column(name = "DOC_VERSION", length = 10)
    private String docVersion;

    @Column(name = "CD_STAT_ENTY", length = 3)
    private String cdStatEnty;

    @Column(name = "DISAPPROVED", length = 1)
    private String disapproved;

    @Column(name = "VOTING_HIST1", length = 20)
    private String votingHist1;

    @Column(name = "VOTING_HIST2", length = 20)
    private String votingHist2;

    @Column(name = "OP_CODE", length = 3)
    private String opCode;

    @Column(name = "OP_DATE", length = 40)
    private String opDate;

    @Column(name = "SENIOR")
    private Double senior;

    @Column(name = "DETAINEE")
    private Double detainee;

    @Column(name = "RAP")
    private Double rap;

    @Column(name = "C_PAGES_DESC", length = 50)
    private String cPagesDesc;

    @Column(name = "SUFFIX", length = 50)
    private String suffix;

    @Column(name = "EXTRA", length = 50)
    private String extra;

    @Column(name = "REG_HIST", length = 50)
    private String regHist;


    // Getters and Setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
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

    public String getAbsentee() {
        return absentee;
    }

    public void setAbsentee(String absentee) {
        this.absentee = absentee;
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

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
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

    public String getAbroadStreet() {
        return abroadStreet;
    }

    public void setAbroadStreet(String abroadStreet) {
        this.abroadStreet = abroadStreet;
    }

    public String getAbroadZip() {
        return abroadZip;
    }

    public void setAbroadZip(String abroadZip) {
        this.abroadZip = abroadZip;
    }

    public String getAbsentia() {
        return absentia;
    }

    public void setAbsentia(String absentia) {
        this.absentia = absentia;
    }

    public String getAbroadCity() {
        return abroadCity;
    }

    public void setAbroadCity(String abroadCity) {
        this.abroadCity = abroadCity;
    }

    public String getAbroadCountry() {
        return abroadCountry;
    }

    public void setAbroadCountry(String abroadCountry) {
        this.abroadCountry = abroadCountry;
    }

    public String getAbroadPeriod() {
        return abroadPeriod;
    }

    public void setAbroadPeriod(String abroadPeriod) {
        this.abroadPeriod = abroadPeriod;
    }

    public String getAbroadResCont() {
        return abroadResCont;
    }

    public void setAbroadResCont(String abroadResCont) {
        this.abroadResCont = abroadResCont;
    }

    public String getRegCountry() {
        return regCountry;
    }

    public void setRegCountry(String regCountry) {
        this.regCountry = regCountry;
    }

    public String getRegEmbassy() {
        return regEmbassy;
    }

    public void setRegEmbassy(String regEmbassy) {
        this.regEmbassy = regEmbassy;
    }

    public String getMailStreet() {
        return mailStreet;
    }

    public void setMailStreet(String mailStreet) {
        this.mailStreet = mailStreet;
    }

    public String getMailZip() {
        return mailZip;
    }

    public void setMailZip(String mailZip) {
        this.mailZip = mailZip;
    }

    public String getMailCity() {
        return mailCity;
    }

    public void setMailCity(String mailCity) {
        this.mailCity = mailCity;
    }

    public String getMailCountry() {
        return mailCountry;
    }

    public void setMailCountry(String mailCountry) {
        this.mailCountry = mailCountry;
    }

    public String getMailEmbassy() {
        return mailEmbassy;
    }

    public void setMailEmbassy(String mailEmbassy) {
        this.mailEmbassy = mailEmbassy;
    }

    public String getRepStreet() {
        return repStreet;
    }

    public void setRepStreet(String repStreet) {
        this.repStreet = repStreet;
    }

    public String getRepBarangay() {
        return repBarangay;
    }

    public void setRepBarangay(String repBarangay) {
        this.repBarangay = repBarangay;
    }

    public String getRepCity() {
        return repCity;
    }

    public void setRepCity(String repCity) {
        this.repCity = repCity;
    }

    public String getRepProvince() {
        return repProvince;
    }

    public void setRepProvince(String repProvince) {
        this.repProvince = repProvince;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAbroadStatus() {
        return abroadStatus;
    }

    public void setAbroadStatus(String abroadStatus) {
        this.abroadStatus = abroadStatus;
    }

    public String getAbroadStatusSpecif() {
        return abroadStatusSpecif;
    }

    public void setAbroadStatusSpecif(String abroadStatusSpecif) {
        this.abroadStatusSpecif = abroadStatusSpecif;
    }

    public String getLastEntryDate() {
        return lastEntryDate;
    }

    public void setLastEntryDate(String lastEntryDate) {
        this.lastEntryDate = lastEntryDate;
    }

    public String getAbsRegistered() {
        return absRegistered;
    }

    public void setAbsRegistered(String absRegistered) {
        this.absRegistered = absRegistered;
    }

    public String getOldPrecinct() {
        return oldPrecinct;
    }

    public void setOldPrecinct(String oldPrecinct) {
        this.oldPrecinct = oldPrecinct;
    }

    public String getOldRegBarangay() {
        return oldRegBarangay;
    }

    public void setOldRegBarangay(String oldRegBarangay) {
        this.oldRegBarangay = oldRegBarangay;
    }

    public String getOldRegCity() {
        return oldRegCity;
    }

    public void setOldRegCity(String oldRegCity) {
        this.oldRegCity = oldRegCity;
    }

    public String getOldRegProvince() {
        return oldRegProvince;
    }

    public void setOldRegProvince(String oldRegProvince) {
        this.oldRegProvince = oldRegProvince;
    }

    public String getOldRegDate() {
        return oldRegDate;
    }

    public void setOldRegDate(String oldRegDate) {
        this.oldRegDate = oldRegDate;
    }

    public String getFlastname() {
        return flastname;
    }

    public void setFlastname(String flastname) {
        this.flastname = flastname;
    }

    public String getFfirstname() {
        return ffirstname;
    }

    public void setFfirstname(String ffirstname) {
        this.ffirstname = ffirstname;
    }

    public String getFmaternalName() {
        return fmaternalName;
    }

    public void setFmaternalName(String fmaternalName) {
        this.fmaternalName = fmaternalName;
    }

    public String getMlastname() {
        return mlastname;
    }

    public void setMlastname(String mlastname) {
        this.mlastname = mlastname;
    }

    public String getMfirstname() {
        return mfirstname;
    }

    public void setMfirstname(String mfirstname) {
        this.mfirstname = mfirstname;
    }

    public String getMmaternalName() {
        return mmaternalName;
    }

    public void setMmaternalName(String mmaternalName) {
        this.mmaternalName = mmaternalName;
    }

    public String getReplastname() {
        return replastname;
    }

    public void setReplastname(String replastname) {
        this.replastname = replastname;
    }

    public String getRepfirstname() {
        return repfirstname;
    }

    public void setRepfirstname(String repfirstname) {
        this.repfirstname = repfirstname;
    }

    public String getRepmaternalName() {
        return repmaternalName;
    }

    public void setRepmaternalName(String repmaternalName) {
        this.repmaternalName = repmaternalName;
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

    public String getBirthCity() {
        return birthCity;
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
    }

    public String getBirthProvince() {
        return birthProvince;
    }

    public void setBirthProvince(String birthProvince) {
        this.birthProvince = birthProvince;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getNaturalizationDate() {
        return naturalizationDate;
    }

    public void setNaturalizationDate(String naturalizationDate) {
        this.naturalizationDate = naturalizationDate;
    }

    public String getCertificateNb() {
        return certificateNb;
    }

    public void setCertificateNb(String certificateNb) {
        this.certificateNb = certificateNb;
    }

    public String getCountryRes() {
        return countryRes;
    }

    public void setCountryRes(String countryRes) {
        this.countryRes = countryRes;
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

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
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

    public String getOldVin() {
        return oldVin;
    }

    public void setOldVin(String oldVin) {
        this.oldVin = oldVin;
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

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getPassportLost() {
        return passportLost;
    }

    public void setPassportLost(String passportLost) {
        this.passportLost = passportLost;
    }

    public String getPassportNb() {
        return passportNb;
    }

    public void setPassportNb(String passportNb) {
        this.passportNb = passportNb;
    }

    public String getPassportPlace() {
        return passportPlace;
    }

    public void setPassportPlace(String passportPlace) {
        this.passportPlace = passportPlace;
    }

    public String getPassYear() {
        return passYear;
    }

    public void setPassYear(String passYear) {
        this.passYear = passYear;
    }

    public String getPassMonth() {
        return passMonth;
    }

    public void setPassMonth(String passMonth) {
        this.passMonth = passMonth;
    }

    public String getPassDay() {
        return passDay;
    }

    public void setPassDay(String passDay) {
        this.passDay = passDay;
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

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
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

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getCdid() {
        return cdid;
    }

    public void setCdid(String cdid) {
        this.cdid = cdid;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
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

    public String getFingerTopoCoord() {
        return fingerTopoCoord;
    }

    public void setFingerTopoCoord(String fingerTopoCoord) {
        this.fingerTopoCoord = fingerTopoCoord;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getMatchingFinger() {
        return matchingFinger;
    }

    public void setMatchingFinger(String matchingFinger) {
        this.matchingFinger = matchingFinger;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public String getTransferUpdateTime() {
        return transferUpdateTime;
    }

    public void setTransferUpdateTime(String transferUpdateTime) {
        this.transferUpdateTime = transferUpdateTime;
    }

    public String getPagesDescr() {
        return pagesDescr;
    }

    public void setPagesDescr(String pagesDescr) {
        this.pagesDescr = pagesDescr;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getLockUser() {
        return lockUser;
    }

    public void setLockUser(String lockUser) {
        this.lockUser = lockUser;
    }

    public String getLockTime() {
        return lockTime;
    }

    public void setLockTime(String lockTime) {
        this.lockTime = lockTime;
    }

    public String getProcessing() {
        return processing;
    }

    public void setProcessing(String processing) {
        this.processing = processing;
    }

    public String getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(String isCurrent) {
        this.isCurrent = isCurrent;
    }

    public String getDocVersion() {
        return docVersion;
    }

    public void setDocVersion(String docVersion) {
        this.docVersion = docVersion;
    }

    public String getCdStatEnty() {
        return cdStatEnty;
    }

    public void setCdStatEnty(String cdStatEnty) {
        this.cdStatEnty = cdStatEnty;
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

    public String getOpDate() {
        return opDate;
    }

    public void setOpDate(String opDate) {
        this.opDate = opDate;
    }

    public Double getSenior() {
        return senior;
    }

    public void setSenior(Double senior) {
        this.senior = senior;
    }

    public Double getDetainee() {
        return detainee;
    }

    public void setDetainee(Double detainee) {
        this.detainee = detainee;
    }

    public Double getRap() {
        return rap;
    }

    public void setRap(Double rap) {
        this.rap = rap;
    }

    public String getcPagesDesc() {
        return cPagesDesc;
    }

    public void setcPagesDesc(String cPagesDesc) {
        this.cPagesDesc = cPagesDesc;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getRegHist() {
        return regHist;
    }

    public void setRegHist(String regHist) {
        this.regHist = regHist;
    }
}