package com.abciloveu.entities.common;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Audit implements Serializable {
    private static final long serialVersionUID = -1113938061247581206L;
    @CreatedDate
    @Basic(optional = false)
    @Column(name = "create_dt", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDt;

    @CreatedBy
    @Basic(optional = false)
    @Column(name = "create_by", length = 50, nullable = false, updatable = false)
    @Size(max = 50)
    protected String createBy;

    @LastModifiedDate
    @Column(name = "last_upd")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastUpd;

    @LastModifiedBy
    @Column(name = "upd_by", length = 50)
    @Size(max = 50)
    protected String updBy;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Date getCreateDt() {
        return createDt;
    }

    public void setCreateDt(Date createDt) {
        this.createDt = createDt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getLastUpd() {
        return lastUpd;
    }

    public void setLastUpd(Date lastUpd) {
        this.lastUpd = lastUpd;
    }

    public String getUpdBy() {
        return updBy;
    }

    public void setUpdBy(String updBy) {
        this.updBy = updBy;
    }
}
