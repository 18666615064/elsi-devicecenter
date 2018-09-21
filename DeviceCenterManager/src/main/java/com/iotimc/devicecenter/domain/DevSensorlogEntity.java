package com.iotimc.devicecenter.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "dev_sensorlog", schema = "elsi-trunk", catalog = "")
public class DevSensorlogEntity {
    private int id;
    private Integer productdtlfk;
    private Integer devicefk;
    private String value;
    private String dsid;
    private String name;
    private Timestamp cretime;
    private String imei;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "productdtlfk")
    public Integer getProductdtlfk() {
        return productdtlfk;
    }

    public void setProductdtlfk(Integer productdtlfk) {
        this.productdtlfk = productdtlfk;
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
    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Basic
    @Column(name = "dsid")
    public String getDsid() {
        return dsid;
    }

    public void setDsid(String dsid) {
        this.dsid = dsid;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DevSensorlogEntity that = (DevSensorlogEntity) o;

        if (id != that.id) return false;
        if (productdtlfk != null ? !productdtlfk.equals(that.productdtlfk) : that.productdtlfk != null) return false;
        if (devicefk != null ? !devicefk.equals(that.devicefk) : that.devicefk != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (dsid != null ? !dsid.equals(that.dsid) : that.dsid != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (cretime != null ? !cretime.equals(that.cretime) : that.cretime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (productdtlfk != null ? productdtlfk.hashCode() : 0);
        result = 31 * result + (devicefk != null ? devicefk.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (dsid != null ? dsid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (cretime != null ? cretime.hashCode() : 0);
        return result;
    }

    @Basic
    @Column(name = "imei")
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
