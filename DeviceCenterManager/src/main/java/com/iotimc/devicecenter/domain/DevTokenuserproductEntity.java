package com.iotimc.devicecenter.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "dev_tokenuserproduct", schema = "elsi-trunk", catalog = "")
public class DevTokenuserproductEntity {
    private int id;
    private Integer tokenuserfk;
    private Integer productfk;
    private Timestamp cretime;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "tokenuserfk")
    public Integer getTokenuserfk() {
        return tokenuserfk;
    }

    public void setTokenuserfk(Integer tokenuserfk) {
        this.tokenuserfk = tokenuserfk;
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

        DevTokenuserproductEntity that = (DevTokenuserproductEntity) o;

        if (id != that.id) return false;
        if (tokenuserfk != null ? !tokenuserfk.equals(that.tokenuserfk) : that.tokenuserfk != null) return false;
        if (productfk != null ? !productfk.equals(that.productfk) : that.productfk != null) return false;
        if (cretime != null ? !cretime.equals(that.cretime) : that.cretime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (tokenuserfk != null ? tokenuserfk.hashCode() : 0);
        result = 31 * result + (productfk != null ? productfk.hashCode() : 0);
        result = 31 * result + (cretime != null ? cretime.hashCode() : 0);
        return result;
    }
}
