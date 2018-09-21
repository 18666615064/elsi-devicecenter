package com.iotimc.devicecenter.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class DeviceCache implements Serializable {
    private int id;
    private String imei;
    private String imsi;
    private String code;
    private Integer productfk;
    private String status;
    private Timestamp cretime;
    private String name;
    private Integer createuser;
    private Integer companyfk;
    private String platformid;
    private Integer onlinestatus;
    private Timestamp updatetime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getProductfk() {
        return productfk;
    }

    public void setProductfk(Integer productfk) {
        this.productfk = productfk;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCretime() {
        return cretime;
    }

    public void setCretime(Timestamp cretime) {
        this.cretime = cretime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCreateuser() {
        return createuser;
    }

    public void setCreateuser(Integer createuser) {
        this.createuser = createuser;
    }

    public Integer getCompanyfk() {
        return companyfk;
    }

    public void setCompanyfk(Integer companyfk) {
        this.companyfk = companyfk;
    }

    public String getPlatformid() {
        return platformid;
    }

    public void setPlatformid(String platformid) {
        this.platformid = platformid;
    }

    public Integer getOnlinestatus() {
        return onlinestatus;
    }

    public void setOnlinestatus(Integer onlinestatus) {
        this.onlinestatus = onlinestatus;
    }

    public Timestamp getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Timestamp updatetime) {
        this.updatetime = updatetime;
    }
}
