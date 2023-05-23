
package com.eventManagement.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.eventManagement.dto.RewardPoints;

@Entity
@Table
@SqlResultSetMapping(
	    name = "RewardPointsMapping",
	    classes = @ConstructorResult(
	        targetClass = RewardPoints.class,
	        columns = {
	            @ColumnResult(name = "allUserCount", type = Long.class),
	            @ColumnResult(name = "allUniversityStudents", type = Long.class),
	            @ColumnResult(name = "allScholorshipStudents", type = Long.class),
	            @ColumnResult(name = "allEmployees", type = Long.class)
	        }
	    )
	)
public class UserRewards {
	
	@Id
	private Long userId;
	
	@Column
	private Long rewardPoints;
	
	//need to save it in db manually
	@Column
	private String userRole;
	
	@Column
	private Long adminId;

	@Column
	private LocalDate createdOn;
	
	@Column
	private LocalDate updatedOn;
	
	@Column
	private String username;
	
	public UserRewards() {}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRewardPoints() {
		return rewardPoints;
	}

	public void setRewardPoints(Long rewardPoints) {
		this.rewardPoints = rewardPoints;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}
	
	public LocalDate getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDate createdOn) {
		this.createdOn = createdOn;
	}

	public LocalDate getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(LocalDate updatedOn) {
		this.updatedOn = updatedOn;
	}
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
}
