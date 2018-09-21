package com.iotimc.devicecenter.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "dev_loginlog", schema = "elsi-trunk")
public class DevLoginlogEntity {
    private int id;
    private Integer devicefk;
    private String imei;
    private Timestamp cretime;
    private Byte type;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "devicefk")
    public Integer getDevicefk() {
        return devicefk;
    }

    public void setDevicefk(Integer devicefk) {
        this.devicefk = devicefk;
    }

    @Basic
    @Column(name = "imei")
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
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
    @Column(name = "type")
    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DevLoginlogEntity that = (DevLoginlogEntity) o;

        if (id != that.id) return false;
        if (devicefk != null ? !devicefk.equals(that.devicefk) : that.devicefk != null) return false;
        if (imei != null ? !imei.equals(that.imei) : that.imei != null) return false;
        if (cretime != null ? !cretime.equals(that.cretime) : that.cretime != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id ;
        result = 31 * result + (devicefk != null ? devicefk.hashCode() : 0);
        result = 31 * result + (imei != null ? imei.hashCode() : 0);
        result = 31 * result + (cretime != null ? cretime.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
