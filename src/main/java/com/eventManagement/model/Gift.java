package com.eventManagement.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
@Entity
public class Gift {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long giftId;
	
	@Column
	private String giftTitle;
	
	@Column
	private String giftDetail;
	
	@Column
	private Integer availableFor;
	
	@Column
	private Long redeemRequirePoints;

	@Column
	private Long adminId;
	
	@Column
	private String imageName;
	
	@Column
	private Date createdOn;
	
	@Column
	private Date lastUpdated;

	public Long getGiftId() {
		return giftId;
	}

	public void setGiftId(Long giftId) {
		this.giftId = giftId;
	}

	public String getGiftTitle() {
		return giftTitle;
	}

	public void setGiftTitle(String giftTitle) {
		this.giftTitle = giftTitle;
	}

	public String getGiftDetail() {
		return giftDetail;
	}

	public void setGiftDetail(String giftDetail) {
		this.giftDetail = giftDetail;
	}

	public Integer getAvailableFor() {
		return availableFor;
	}

	public void setAvailableFor(Integer availableFor) {
		this.availableFor = availableFor;
	}

	public Long getRedeemRequirePoints() {
		return redeemRequirePoints;
	}

	public void setRedeemRequirePoints(Long redeemRequirePoints) {
		this.redeemRequirePoints = redeemRequirePoints;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	
}
