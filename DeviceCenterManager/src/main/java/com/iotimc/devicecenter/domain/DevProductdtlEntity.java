package com.iotimc.devicecenter.domain;

import javax.persistence.*;

@Entity
@Table(name = "dev_productdtl", schema = "elsi-trunk", catalog = "")
public class DevProductdtlEntity {
    private int id;
    private Integer objid;
    private String name;
    private String type;
    private String alias;
    private String notes;
    private Integer resid;
    private Byte insid;
    private Integer productfk;
    private String status;
    private Byte writeable;
    private Byte ishex;
    private String correct;

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
    @Column(name = "objid")
    public Integer getObjid() {
        return objid;
    }

    public void setObjid(Integer objid) {
        this.objid = objid;
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
    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
    @Column(name = "resid")
    public Integer getResid() {
        return resid;
    }

    public void setResid(Integer resid) {
        this.resid = resid;
    }

    @Basic
    @Column(name = "insid")
    public Byte getInsid() {
        return insid;
    }

    public void setInsid(Byte insid) {
        this.insid = insid;
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
    @Column(name = "writeable")
    public Byte getWriteable() {
        return writeable;
    }

    public void setWriteable(Byte writeable) {
        this.writeable = writeable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DevProductdtlEntity that = (DevProductdtlEntity) o;

        if (id != that.id) return false;
        if (objid != null ? !objid.equals(that.objid) : that.objid != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (alias != null ? !alias.equals(that.alias) : that.alias != null) return false;
        if (notes != null ? !notes.equals(that.notes) : that.notes != null) return false;
        if (resid != null ? !resid.equals(that.resid) : that.resid != null) return false;
        if (insid != null ? !insid.equals(that.insid) : that.insid != null) return false;
        if (productfk != null ? !productfk.equals(that.productfk) : that.productfk != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (writeable != null ? !writeable.equals(that.writeable) : that.writeable != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (objid != null ? objid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (resid != null ? resid.hashCode() : 0);
        result = 31 * result + (insid != null ? insid.hashCode() : 0);
        result = 31 * result + (productfk != null ? productfk.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (writeable != null ? writeable.hashCode() : 0);
        return result;
    }

    @Basic
    @Column(name = "ishex")
    public Byte getIshex() {
        return ishex;
    }

    public void setIshex(Byte ishex) {
        this.ishex = ishex;
    }

    @Basic
    @Column(name = "correct")
    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }
}
