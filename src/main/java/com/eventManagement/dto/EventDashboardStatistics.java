package com.eventManagement.dto;

import java.util.List;

public class EventDashboardStatistics {

	private List<Statistics> statisticsList;
	
	private PeopleInvited peopleInvited;

	public EventDashboardStatistics() {}
	
	public EventDashboardStatistics(List<Statistics> statisticsList, PeopleInvited peopleInvited) {
		super();
		this.statisticsList = statisticsList;
		this.peopleInvited = peopleInvited;
	}

	public List<Statistics> getStatList() {
		return statisticsList;
	}

	public void setStatList(List<Statistics> statList) {
		this.statisticsList = statList;
	}

	public PeopleInvited getPeopleInvited() {
		return peopleInvited;
	}

	public void setPeopleInvited(PeopleInvited peopleInvited) {
		this.peopleInvited = peopleInvited;
	}
	
}
