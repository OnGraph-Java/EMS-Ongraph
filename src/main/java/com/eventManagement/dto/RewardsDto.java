package com.eventManagement.dto;

import java.util.List;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RewardsDto {

	@NotNull(message = "adminId is null")
	private Long pointPerUser;

	@NotBlank(message = "activityType is null")
    @Size(min = 2, max = 60)
	private String activityType;

	@NotBlank(message = "comments is null")
    @Size(min = 2)
	private String comments;
	
	@NotNull(message = "adminId is null")
	private Long adminId;

	
	private List<String> rewardUserId;


	public RewardsDto() {}

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

	public List<String> getRewardUserId() {
		return rewardUserId;
	}

	public void setRewardUserId(List<String> rewardUserId) {
		this.rewardUserId = rewardUserId;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	
}
