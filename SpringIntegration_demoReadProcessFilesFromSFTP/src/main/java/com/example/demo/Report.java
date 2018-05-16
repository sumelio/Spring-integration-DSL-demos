package com.example.demo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.example.demo.SftpJavaApplicationOK.Result;

public class Report implements Serializable {

    
    private String account;
    private String campaign;
    private String send;
    private String newSletter;
    private String subject;
    private String kindProfile;	
    private String profile; 
    private String email;
    private Date date;	
    private String geolocation;
    private String device;
    private String browser;
    private String action; 
    private String url;	
    private String rebound;
    private String viral;	
    private String share;
    private String description;
    private Date sendDate;
    private Result result;
    
    
    

    

    // setters and getters...



	public Result getResult() {
		return result;
	}



	public void setResult(Result result) {
		this.result = result;
	}



	public String getAccount() {
		return account;
	}



	public void setAccount(String account) {
		this.account = account;
	}



	public String getCampaign() {
		return campaign;
	}



	public void setCampaign(String campagn) {
		this.campaign = campagn;
	}



	public String getSend() {
		return send;
	}



	public void setSend(String send) {
		this.send = send;
	}



	public String getNewSletter() {
		return newSletter;
	}



	public void setNewSletter(String newSletter) {
		this.newSletter = newSletter;
	}



	public String getSubject() {
		return subject;
	}



	public void setSubject(String subject) {
		this.subject = subject;
	}



	public String getKindProfile() {
		return kindProfile;
	}



	public void setKindProfile(String kindProfile) {
		this.kindProfile = kindProfile;
	}



	public String getProfile() {
		return profile;
	}



	public void setProfile(String profile) {
		this.profile = profile;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public Date getDate() {
		return date;
	}



	public void setDate(Date date) {
		this.date = date;
	}



	public String getGeolocation() {
		return geolocation;
	}



	public void setGeolocation(String geolocation) {
		this.geolocation = geolocation;
	}



	public String getDevice() {
		return device;
	}



	public void setDevice(String device) {
		this.device = device;
	}



	public String getBrowser() {
		return browser;
	}



	public void setBrowser(String browser) {
		this.browser = browser;
	}



	public String getAction() {
		return action;
	}



	public void setAction(String action) {
		this.action = action;
	}



	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}



	public String getRebound() {
		return rebound;
	}



	public void setRebound(String rebound) {
		this.rebound = rebound;
	}



	public String getViral() {
		return viral;
	}



	public void setViral(String viral) {
		this.viral = viral;
	}



	public String getShare() {
		return share;
	}



	public void setShare(String share) {
		this.share = share;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public Date getSendDate() {
		return sendDate;
	}



	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
    
    
    
    
}




