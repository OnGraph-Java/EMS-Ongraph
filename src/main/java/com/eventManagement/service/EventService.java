package com.eventManagement.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.multipart.MultipartFile;

import com.eventManagement.dto.EventDto;
import com.eventManagement.dto.EventUsersDto;
import com.eventManagement.model.Event;
import com.eventManagement.model.EventUsers;

public interface EventService {

	public String createEvent(MultipartFile[] files, EventDto event);
	
	public String updateEvent(Long id, EventDto updatedEvent, MultipartFile[] files) throws Exception;
	
	List<Event> getAllEvent(Long adminId,String eventCategory,String eventType,String eventDate, boolean isDashboard, int page, int size);
	
	List<Event> searchEvent(String searchText);

	public Event getEvent(Long eventId);

	public String registerEventUser(@Valid EventUsersDto eventUsersDto);

	public List<EventUsers> getEventRegisterUsers(Long eventId);
	
}
