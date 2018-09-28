package com.iotimc.devicecenter.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "dev_controllog", schema = "elsi-trunk", catalog = "")
public class DevControllogEntity {
    private int id;
    private Integer devicefk;
    private Integer productdtlfk;
    private String log;
    private Timestamp cretime;
    private String type;
    private String result;
    private Integer usetime;
    private String name;
    private String value;
    private String imei;
    private String asyncuuid;

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
    @Column(name = "productdtlfk")
    public Integer getProductdtlfk() {
        return productdtlfk;
    }

    public void setProductdtlfk(Integer productdtlfk) {
        this.productdtlfk = productdtlfk;
    }

    @Basic
    @Column(name = "log")
    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
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
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DevControllogEntity that = (DevControllogEntity) o;

        if (id != that.id) return false;
        if (devicefk != null ? !devicefk.equals(that.devicefk) : that.devicefk != null) return false;
        if (productdtlfk != null ? !productdtlfk.equals(that.productdtlfk) : that.productdtlfk != null) return false;
        if (log != null ? !log.equals(that.log) : that.log != null) return false;
        if (cretime != null ? !cretime.equals(that.cretime) : that.cretime != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (devicefk != null ? devicefk.hashCode() : 0);
        result = 31 * result + (productdtlfk != null ? productdtlfk.hashCode() : 0);
        result = 31 * result + (log != null ? log.hashCode() : 0);
        result = 31 * result + (cretime != null ? cretime.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Basic
    @Column(name = "result")
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Basic
    @Column(name = "usetime")
    public Integer getUsetime() {
        return usetime;
    }

    public void setUsetime(Integer usetime) {
        this.usetime = usetime;
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
    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
    @Column(name = "asyncuuid")
    public String getAsyncuuid() {
        return asyncuuid;
    }

    public void setAsyncuuid(String asyncuuid) {
        this.asyncuuid = asyncuuid;
    }
}
