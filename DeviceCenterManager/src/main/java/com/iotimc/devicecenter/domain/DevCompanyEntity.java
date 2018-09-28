package com.iotimc.devicecenter.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "dev_company", schema = "elsi-trunk", catalog = "")
public class DevCompanyEntity {
    private int id;
    private String name;
    private Timestamp cretime;
    private String alias;
    private String baseurl;
    private String systemname;
    private String status;
    private String username;
    private String password;
    private String apikey;
    private String token;

    @Id
    @Column(name = "id")
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
    @Column(name = "cretime")
    public Timestamp getCretime() {
        return cretime;
    }

    public void setCretime(Timestamp cretime) {
        this.cretime = cretime;
    }

    @Basic
    @Column(name = "alias")
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DevCompanyEntity that = (DevCompanyEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (cretime != null ? !cretime.equals(that.cretime) : that.cretime != null) return false;
        if (alias != null ? !alias.equals(that.alias) : that.alias != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (cretime != null ? cretime.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }

    @Basic
    @Column(name = "baseurl")
    public String getBaseurl() {
        return baseurl;
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    @Basic
    @Column(name = "systemname")
    public String getSystemname() {
        return systemname;
    }

    public void setSystemname(String systemname) {
        this.systemname = systemname;
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
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
