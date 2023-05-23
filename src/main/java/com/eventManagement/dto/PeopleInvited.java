package com.eventManagement.dto;

public class PeopleInvited {

	private Long registeredUsers;
	
	private Long unregisteredUsers;
	
	private Long totalUsers;
	
	public PeopleInvited() {}

	public PeopleInvited(Long registeredUsers, Long unregisteredUsers, Long totalUsers) {
		super();
		this.registeredUsers = registeredUsers;
		this.unregisteredUsers = unregisteredUsers;
		this.totalUsers = totalUsers;
	}

	public Long getRegisteredUsers() {
		return registeredUsers;
	}

	public void setRegisteredUsers(Long registeredUsers) {
		this.registeredUsers = registeredUsers;
	}

	public Long getUnregisteredUsers() {
		return unregisteredUsers;
	}

	public void setUnregisteredUsers(Long unregisteredUsers) {
		this.unregisteredUsers = unregisteredUsers;
	}

	public Long getTotalUsers() {
		return totalUsers;
	}

	public void setTotalUsers(Long totalUsers) {
		this.totalUsers = totalUsers;
	}
	
	
}
