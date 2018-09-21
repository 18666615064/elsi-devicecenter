package com.iotimc.devicecenter.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "dev_device", schema = "elsi-trunk", catalog = "")
public class DevDeviceEntity {
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
    @Column(name = "imei")
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    @Basic
    @Column(name = "imsi")
    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    @Basic
    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "productfk")
    public Integer getProductfk() {
        return productfk;
    }

    public void setProductfk(Integer productfk) {
        this.productfk = productfk;
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
    @Column(name = "cretime")
    public Timestamp getCretime() {
        return cretime;
    }

    public void setCretime(Timestamp cretime) {
        this.cretime = cretime;
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
    @Column(name = "createuser")
    public Integer getCreateuser() {
        return createuser;
    }

    public void setCreateuser(Integer createuser) {
        this.createuser = createuser;
    }

    @Basic
    @Column(name = "companyfk")
    public Integer getCompanyfk() {
        return companyfk;
    }

    public void setCompanyfk(Integer companyfk) {
        this.companyfk = companyfk;
    }

    @Basic
    @Column(name = "platformid")
    public String getPlatformid() {
        return platformid;
    }

    public void setPlatformid(String platformid) {
        this.platformid = platformid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DevDeviceEntity that = (DevDeviceEntity) o;

        if (id != that.id) return false;
        if (imei != null ? !imei.equals(that.imei) : that.imei != null) return false;
        if (imsi != null ? !imsi.equals(that.imsi) : that.imsi != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (productfk != null ? !productfk.equals(that.productfk) : that.productfk != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (cretime != null ? !cretime.equals(that.cretime) : that.cretime != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (createuser != null ? !createuser.equals(that.createuser) : that.createuser != null) return false;
        if (companyfk != null ? !companyfk.equals(that.companyfk) : that.companyfk != null) return false;
        if (platformid != null ? !platformid.equals(that.platformid) : that.platformid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (imei != null ? imei.hashCode() : 0);
        result = 31 * result + (imsi != null ? imsi.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (productfk != null ? productfk.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (cretime != null ? cretime.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (createuser != null ? createuser.hashCode() : 0);
        result = 31 * result + (companyfk != null ? companyfk.hashCode() : 0);
        result = 31 * result + (platformid != null ? platformid.hashCode() : 0);
        return result;
    }
}
