package org.dragonli.service.modules.userservice.entity.models;

import org.dragonli.jpatools.store.AbstractEntity;
import org.dragonli.service.modules.userservice.entity.enums.UserStatus;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@Table(name="user")
@Proxy(lazy = false)
public class UserEntity extends AbstractEntity {
    private static final long serialVersionUID = 7092023432129832562L;

    /**
     *
     */
    @Column
    private String phone;

    /**
     *
     */
    @Column(nullable = false)
    private String passwd;

    @Column(name="passwd_code",nullable = false)
    private String passwdCode;

    @Column(name="country_id",nullable = false)
    private Long countryId;

    /**
     *
     */
    @Column(nullable = false)
    private String username;


    /**
     *
     */
    @Column(name="regist_time",nullable = false)
    private Long registTime;

    /**
     *
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    
    @Column
    private String email;
    
    
    @Column(nullable = false)
    private String nickname;

    @Column(name="recommend_code")
    private String recommendCode;

    @Column(name="validata_code")
    private String validataCode;

    @Column(name="phone_code")
    private String phoneCode;

    @Column(name="email_code")
    private String emailCode;

    @Column(name="login_code")
    private String loginCode;

    @Column(name="phone_validated",nullable = false)
    private Boolean phoneValidated;

    @Column(name="email_validated",nullable = false)
    private Boolean emailValidated;

    public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getRegistTime() {
        return registTime;
    }

    public void setRegistTime(Long registTime) {
        this.registTime = registTime;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getRecommendCode() {
        return recommendCode;
    }

    public void setRecommendCode(String recommendCode) {
        this.recommendCode = recommendCode;
    }

    public String getValidataCode() {
        return validataCode;
    }

    public void setValidataCode(String validataCode) {
        this.validataCode = validataCode;
    }

    public String getPasswdCode() {
        return passwdCode;
    }

    public void setPasswdCode(String passwdCode) {
        this.passwdCode = passwdCode;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }

    public Boolean getPhoneValidated() {
        return phoneValidated;
    }

    public void setPhoneValidated(Boolean phoneValidated) {
        this.phoneValidated = phoneValidated;
    }

    public Boolean getEmailValidated() {
        return emailValidated;
    }

    public void setEmailValidated(Boolean emailValidated) {
        this.emailValidated = emailValidated;
    }

    public String getLoginCode() {
        return loginCode;
    }

    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }
}
