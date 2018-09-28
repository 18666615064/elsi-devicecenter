package com.iotimc.devicecenter.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

public class ProductConfig implements Serializable{
    private Integer id;
    private String name;
    private String alias;
    private String notes;
    private Timestamp cretime;
    private String status;
    private String token;
    private String apikey;
    private Integer companyfk;
    private Map<String, DevProductdtlEntity> dtllist;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCretime() {
        return cretime;
    }

    public void setCretime(Timestamp cretime) {
        this.cretime = cretime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public Integer getCompanyfk() {
        return companyfk;
    }

    public void setCompanyfk(Integer companyfk) {
        this.companyfk = companyfk;
    }

    public Map<String, DevProductdtlEntity> getDtllist() {
        return dtllist;
    }

    public void setDtllist(Map<String, DevProductdtlEntity> dtllist) {
        this.dtllist = dtllist;
    }
}
