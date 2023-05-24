package com.eventManagement.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.eventManagement.dto.EventDto;
import com.eventManagement.dto.EventUsersDto;
import com.eventManagement.model.Event;
import com.eventManagement.model.EventUsers;
import com.eventManagement.repository.EventRepository;
import com.eventManagement.repository.EventUsersRepository;

@Service
public class EventServiceImpl implements EventService {

	private Logger logger = LoggerFactory.getLogger(EventServiceImpl.class.getName());
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	public static String projectlocalPath = System.getProperty("user.dir");

	@Autowired
	EventRepository eventRepository;

	@Autowired
	EventUsersRepository eventUsersRepository;

	@Autowired
	EventFilter eventFilter;

	@Override
	public String createEvent(MultipartFile[] files, EventDto eventDto) {
		// need a method to validate eventDto
		try {
			Event event = new Event();

			event = parseEvent(event, eventDto);
			List<String> imageNames = saveFileInSystem(files);

			StringBuilder sb = new StringBuilder();
			for (String s : imageNames) {
				sb.append(s).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);

			event.setImageName(sb.toString());
			event.setCreatedOn(sdf.parse(sdf.format(new Date())));

			eventRepository.save(event);
		} catch (Exception ex) {
			logger.error("Error occurred while saving event : " + ex.getMessage());
			return "Error got while saving event : " + ex.getMessage();
		}
		return "Successfully saved event";
	}

	@Override
	public String updateEvent(Long id, EventDto eventDto, MultipartFile[] files) throws Exception {

		try {
			Event event = eventRepository.findById(id).get();
			if (event == null) {
				throw new Exception("Event not found");
			}
			event = parseEvent(event, eventDto);
			List<String> imageNames = saveFileInSystem(files);

			StringBuilder sb = new StringBuilder();
			for (String s : imageNames) {
				sb.append(s).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			event.setImageName(sb.toString());
			event.setLastUpdated(sdf.parse(sdf.format(new Date())));
			eventRepository.save(event);

		} catch (Exception ex) {
			logger.error("Error occurred while saving event : " + ex.getMessage());
			return "Error got while updating event : " + ex.getMessage();
		}
		return "Successfully updated event";

	}

	private Event parseEvent(Event event, EventDto eventDto) throws ParseException {

		event.setAdminId(Long.parseLong(eventDto.getAdminId()));
		event.setAddress(eventDto.getAddress());
		event.setStartDate(sdf.parse(sdf.format((sdf.parse(eventDto.getStartDate())))));
		event.setEndDate(sdf.parse(sdf.format((sdf.parse(eventDto.getEndDate())))));
		event.setEventCategory(eventDto.getEventCategory());
		event.setEventDetails(eventDto.getEventDetails());
		event.setEventTitle(eventDto.getEventTitle());
		event.setEventType(eventDto.getEventType());
		event.setLink(eventDto.getLink());

		event.setLocation(eventDto.getLocation());
		event.setUserType(eventDto.getUserType());
		return event;
	}

	@Override
	public List<Event> getAllEvent(Long adminId, String eventCategory, String eventType, String eventDate,
			boolean isDashboard, int page, int size) {
		
		List<Event> eventList = new ArrayList<>();

		PageRequest pageReq = PageRequest.of(page, size, Sort.by("startDate")); 
		if(isDashboard || !eventDate.equals("")) {
			Date dateOfEvent = getEventDate(eventDate, isDashboard);
			eventList = eventRepository.filterEventsDashboards(adminId, eventCategory.toLowerCase(), eventType.toLowerCase(), dateOfEvent, pageReq);
		} else {
			eventList = eventRepository.filterEvents(adminId, eventCategory.toLowerCase(), eventType.toLowerCase(), pageReq);
		}

		if (eventList != null) {
			int i = 0;
			for (Event event : eventList) {
				String[] images = event.getImageName().split(",");
				for (String str : images) {
					str = projectlocalPath + "\\" + str;
					images[i] = str;
					i++;
				}
				i = 0;
				StringBuilder sb = new StringBuilder();
				for (String s : images) {
					sb.append(s).append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				event.setImageName(sb.toString());
			}
		}

		return eventList;
	}

	private Date getEventDate(String eventDate, boolean isDashboard) {
		Date date = null;

		try {
			if (isDashboard && eventDate.equals("")) {
				return sdf.parse(sdf.format(new Date()));
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			date = sdf.parse(eventDate);
		} catch (ParseException pe) {
			logger.error("Error occure while parsing date: "+pe.getMessage());
		} catch (Exception ex) {
			logger.error("Error occure while parsing date: "+ex.getMessage());
		}
		return date;
	}


	@Override
	public List<Event> searchEvent(String title) {
		List<Event> eventList = new ArrayList<>();
		title = "%" + title.toLowerCase() + "%";
		try {
			eventList = eventRepository.findEventByTitle(title);
		} catch (Exception e) {
			logger.error("Error occurred while fetching event");
		}
		return eventList;
	}

	public List<String> saveFileInSystem(MultipartFile[] files) throws Exception {

		List<String> fileNames = new ArrayList<>();
		String UPLOAD_DIR = "event\\images\\";
		for (MultipartFile file : files) {
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			try {
				Path path = Paths.get(UPLOAD_DIR + fileName);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				fileNames.add(UPLOAD_DIR + fileName);
			} catch (IOException e) {
				throw new Exception("Exception got while saving files : " + e.getMessage());
			} catch (UncheckedIOException e) {
				logger.error("An UncheckedIOException occurred: " + e.getMessage());
			}
		}
		return fileNames;

	}

	@Override
	public Event getEvent(Long eventId) {

		Event event = null;
		try {
			event = eventRepository.findById(eventId).get();
			String str = event.getImageName();
			str = projectlocalPath + "\\" + str;
			event.setImageName(str);
			return event;
		} catch (Exception ex) {
			logger.error("Exception got fetching event by id : " + eventId);
		}
		return null;
	}

	@Override
	public String registerEventUser(@Valid EventUsersDto eventUsersDto) {
		String response = "";

		try {
			Optional<Event> event = eventRepository.findById(eventUsersDto.getEventId());
			if (event.isPresent()) {
				EventUsers eventUser = eventUsersRepository.findByEventByUserId(eventUsersDto.getUserId(),
						eventUsersDto.getEventId());
				if (eventUser != null && eventUser.getUserId() == eventUsersDto.getUserId()) {
					response = "User already registered in event";
					return response;
				}
				eventUser = parseEventUser(eventUsersDto);
				eventUser.setCreatedOn(sdf.parse(sdf.format(new Date())));
				eventUsersRepository.save(eventUser);
				response = "Successfully register user for event";
			} else {
				response = "no such event exists";
				return response;
			}
		} catch (Exception ex) {
			response = ex.getMessage();
			logger.error("Exception got while saving event user : " + ex.getMessage());
			return response;
		}
		return response;
	}

	@Override
	public List<EventUsers> getEventRegisterUsers(Long eventId) {
		List<EventUsers> eventUserList = new ArrayList<>();
		Optional<Event> event = eventRepository.findById(eventId);
		if (event.isPresent()) {
			eventUserList = eventUsersRepository.findAllEventByEventId(eventId);
		} else {
			logger.error("No such event Exists");
		}
		return eventUserList;
	}

	public EventUsers parseEventUser(EventUsersDto eventUsersDto) {

		EventUsers eventUsers = new EventUsers();
		eventUsers.setAdminId(eventUsersDto.getAdminId());
		eventUsers.setEmail(eventUsersDto.getEmail());
		eventUsers.setEventId(eventUsersDto.getEventId());
		eventUsers.setName(eventUsersDto.getName());
		eventUsers.setPhoneNo(eventUsersDto.getName());
		eventUsers.setRegistrationDate(eventUsersDto.getRegistrationDate());
		eventUsers.setRegistrationId(eventUsersDto.getRegistrationId());
		eventUsers.setUserId(eventUsersDto.getUserId());
		eventUsers.setUserType(eventUsersDto.getUserType());

		return eventUsers;
	}
}
