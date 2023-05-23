package com.eventManagement.dto;

public class RewardPoints {
	
	private Long allUserCount;
	
	private Long allUniversityStudents;
	
	private Long allScholorshipStudents;
	
	private Long allEmployees;
	
	public RewardPoints() {}
	
	public RewardPoints(Long allUserCount, Long allUniversityStudents, Long allScholorshipStudents, Long allEmployees) {
		super();
		this.allUserCount = allUserCount;
		this.allUniversityStudents = allUniversityStudents;
		this.allScholorshipStudents = allScholorshipStudents;
		this.allEmployees = allEmployees;
	}

	public Long getAllUserCount() {
		return allUserCount;
	}

	public void setAllUserCount(Long allUserCount) {
		this.allUserCount = allUserCount;
	}

	public Long getAllUniversityStudents() {
		return allUniversityStudents;
	}

	public void setAllUniversityStudents(Long allUniversityStudents) {
		this.allUniversityStudents = allUniversityStudents;
	}

	public Long getAllScholorshipStudents() {
		return allScholorshipStudents;
	}

	public void setAllScholorshipStudents(Long allScholorshipStudents) {
		this.allScholorshipStudents = allScholorshipStudents;
	}

	public Long getAllEmployees() {
		return allEmployees;
	}

	public void setAllEmployees(Long allEmployees) {
		this.allEmployees = allEmployees;
	}
	
}
