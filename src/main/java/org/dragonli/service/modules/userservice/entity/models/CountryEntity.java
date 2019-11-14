package org.dragonli.service.modules.userservice.entity.models;


import org.dragonli.jpatools.store.AbstractEntity;
import org.dragonli.service.modules.userservice.entity.enums.CountryStatusEnum;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@Table(name = "country")
@Proxy(lazy = false)
public class CountryEntity extends AbstractEntity {

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String nameZhs;
    @Column(nullable = false)
    private String nameZht;
    @Column(nullable = false)
    private String code2;
    @Column(nullable = false)
    private String code3;

    @Column(name="mobile_prefix",nullable = false)
    private String mobilePrefix;

    @Enumerated(EnumType.STRING)
    private CountryStatusEnum status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameZhs() {
        return nameZhs;
    }

    public void setNameZhs(String nameZhs) {
        this.nameZhs = nameZhs;
    }

    public String getNameZht() {
        return nameZht;
    }

    public void setNameZht(String nameZht) {
        this.nameZht = nameZht;
    }

    public String getCode2() {
        return code2;
    }

    public void setCode2(String code2) {
        this.code2 = code2;
    }

    public String getCode3() {
        return code3;
    }

    public void setCode3(String code3) {
        this.code3 = code3;
    }

    public String getMobilePrefix() {
        return mobilePrefix;
    }

    public void setMobilePrefix(String mobilePrefix) {
        this.mobilePrefix = mobilePrefix;
    }

    public CountryStatusEnum getStatus() {
        return status;
    }

    public void setStatus(CountryStatusEnum status) {
        this.status = status;
    }
}