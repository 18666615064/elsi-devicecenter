package com.iotimc.devicecenter.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "dev_product", schema = "elsi-trunk", catalog = "")
public class DevProductEntity {
    private int id;
    private String name;
    private String alias;
    private String notes;
    private Timestamp cretime;
    private String status;
    private String token;
    private String apikey;
    private Integer companyfk;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "alias")
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Basic
    @Column(name = "notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Basic
    @Column(name = "cretime")
    public Timestamp getCretime() {
        return cretime;
    }

    public void setCretime(Timestamp cretime) {
        this.cretime = cretime;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Basic
    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Basic
    @Column(name = "apikey")
    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    @Basic
    @Column(name = "companyfk")
    public Integer getCompanyfk() {
        return companyfk;
    }

    public void setCompanyfk(Integer companyfk) {
        this.companyfk = companyfk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DevProductEntity that = (DevProductEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (alias != null ? !alias.equals(that.alias) : that.alias != null) return false;
        if (notes != null ? !notes.equals(that.notes) : that.notes != null) return false;
        if (cretime != null ? !cretime.equals(that.cretime) : that.cretime != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        if (apikey != null ? !apikey.equals(that.apikey) : that.apikey != null) return false;
        if (companyfk != null ? !companyfk.equals(that.companyfk) : that.companyfk != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (cretime != null ? cretime.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (apikey != null ? apikey.hashCode() : 0);
        result = 31 * result + (companyfk != null ? companyfk.hashCode() : 0);
        return result;
    }
}
