package com.eventManagement.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EventUsersDto {
	
	@NotNull(message = "userId is null")
	private Long userId;
    
	@NotNull(message = "eventId is null")
	private Long eventId;

	@NotNull(message = "registrationId is null")
	private Long registrationId;
	
	@NotNull(message = "adminId is null")
	private Long adminId;

	@NotBlank(message = "name is null")
    @Size(min = 2, max = 20)
	private String name;

	@NotBlank(message = "userType is null")
    @Size(min = 2, max = 20)
	private String userType;

	@NotBlank(message = "phoneNo is null")
    @Size(min = 9, max = 11)
	private String phoneNo;

	@NotBlank(message = "email is null")
    @Size(min = 4, max = 30)
	@Email
	private String email;

	@NotBlank(message = "registrationDate is null")
    @Size(min = 2, max = 20)
	private String registrationDate;
	
	public EventUsersDto() {}

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
	

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}


	

}
