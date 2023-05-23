package com.eventManagement.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Reward {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long rewardId;
	
	@Column
	private Long pointPerUser;
	
	@Column
	private String activityType;
	
	@Column
	private String comments;
	
	@Column
	private Long adminId;
	
	@Column
	private Long numberOfUser;
	
	@Column
	private LocalDate rewardDate;
	
	@Column
	private String status;
	
	public Reward() {}

	public Long getRewardId() {
		return rewardId;
	}

	public void setRewardId(Long rewardId) {
		this.rewardId = rewardId;
	}

	public Long getPointPerUser() {
		return pointPerUser;
	}

	public void setPointPerUser(Long pointPerUser) {
		this.pointPerUser = pointPerUser;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	

	public Long getNumberOfUser() {
		return numberOfUser;
	}

	public void setNumberOfUser(Long numberOfUser) {
		this.numberOfUser = numberOfUser;
	}

	public LocalDate getRewardDate() {
		return rewardDate;
	}

	public void setRewardDate(LocalDate rewardDate) {
		this.rewardDate = rewardDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
