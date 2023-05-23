package com.eventManagement.dto;

public class Statistics {
	
	private Long eventId;
	
	private String eventTitle;
	
    private String universityStudent;
    
    private String employee;

    private String scholarShipStudents;

    private String publicUsers;
    
    public Statistics() {}
    
	public Statistics(Long eventId, String eventTitle, String universityStudent, String employee,
			String scholarShipStudents, String publicUsers) {
		super();
		this.eventId = eventId;
		this.eventTitle = eventTitle;
		this.universityStudent = universityStudent;
		this.employee = employee;
		this.scholarShipStudents = scholarShipStudents;
		this.publicUsers = publicUsers;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public String getUniversityStudent() {
		return universityStudent;
	}

	public void setUniversityStudent(String universityStudent) {
		this.universityStudent = universityStudent;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public String getScholarShipStudents() {
		return scholarShipStudents;
	}

	public void setScholarShipStudents(String scholarShipStudents) {
		this.scholarShipStudents = scholarShipStudents;
	}

	public String getPublicUsers() {
		return publicUsers;
	}

	public void setPublicUsers(String publicUsers) {
		this.publicUsers = publicUsers;
	}

}
