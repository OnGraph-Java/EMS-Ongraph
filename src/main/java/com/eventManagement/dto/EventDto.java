package com.eventManagement.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


public class EventDto {

	@NotBlank(message = "adminId is null")
    @Size(min = 2, max = 60)
	private String adminId;
	
	@NotBlank(message = "eventTitle is null")
    @Size(min = 2, max = 60)
	private String eventTitle;
	
	@NotBlank(message = "startDate is null")
    @Size(min = 2, max = 60)
	private String startDate;
	
	@NotBlank(message = "endDate is null")
    @Size(min = 2, max = 60)
    private String endDate;
	
	@NotBlank(message = "eventCategory is null")
    @Size(min = 2, max = 60)
	private String eventCategory;
	
	@NotBlank(message = "eventType is null")
    @Size(min = 2, max = 60)
	private String eventType;
	
	@NotBlank(message = "userType is null")
    @Size(min = 2, max = 60)
	private String userType;
	
	@NotBlank(message = "location is null")
    @Size(min = 2, max = 60)
	private String location;
	
	@NotBlank(message = "address is null")
    @Size(min = 2, max = 60)
	private String address;
	
	@NotBlank(message = "link is null")
    @Size(min = 2, max = 60)
	private String link;
	
	@NotBlank(message = "eventDetails is null")
	private String eventDetails;
	
	public String getAdminId() {
		return adminId;
	}
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}
	public String getEventTitle() {
		return eventTitle;
	}
	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getEventCategory() {
		return eventCategory;
	}
	public void setEventCategory(String eventCategory) {
		this.eventCategory = eventCategory;
	}
	
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getEventDetails() {
		return eventDetails;
	}
	public void setEventDetails(String eventDetails) {
		this.eventDetails = eventDetails;
	}

	
}
