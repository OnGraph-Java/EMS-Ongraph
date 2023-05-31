package com.eventManagement.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.eventManagement.dto.EventDto;
import com.eventManagement.dto.EventUsersDto;
import com.eventManagement.model.Event;
import com.eventManagement.model.EventUsers;

public interface EventService {

	String createEvent(MultipartFile[] files, EventDto event);
	
	String updateEvent(Long id, EventDto updatedEvent, MultipartFile[] files) throws Exception;
	
	List<Event> getAllEvent(Long adminId,String eventCategory,String eventType,String eventDate, boolean isDashboard, String title);
	
	List<Event> searchEvent(String searchText);

	Event getEvent(Long eventId);

	String registerEventUser(@Valid EventUsersDto eventUsersDto);

	Page<EventUsers> getEventRegisterUsers(Long eventId, int page, int size);

	List<EventUsers> getExportEventRegisterUsers(Long eventId);
	
	String deleteEvent(Long eventId);
	
}
