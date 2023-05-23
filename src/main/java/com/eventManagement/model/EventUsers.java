package com.eventManagement.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.eventManagement.dto.Statistics;


@Entity
@Table
@SqlResultSetMapping(
	    name = "StatisticsMapping",
	    classes = @ConstructorResult(
	        targetClass = Statistics.class,
	        columns = {
	            @ColumnResult(name = "event_id", type = Long.class),
	            @ColumnResult(name = "event_title", type = String.class),
	            @ColumnResult(name = "universityStudent", type = String.class),
	            @ColumnResult(name = "employee", type = String.class),
	            @ColumnResult(name = "scholarShipStudents", type = String.class),
	            @ColumnResult(name = "publicUsers", type = String.class)
	        }
	    )
	)
public class EventUsers {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Long userId;
	
	@Column
	private Long eventId;
	
	@Column
	private Long registrationId;
	
	@Column
	private Long adminId;
	
	@Column
	private String name;
	
	@Column
	private String userType;
	
	@Column
	private String phoneNo;

	@Column
	private String email;
	
	@Column
	private String registrationDate;

	@Column
	private Date createdOn;
	
	public EventUsers() {}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(Long registrationId) {
		this.registrationId = registrationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

}
