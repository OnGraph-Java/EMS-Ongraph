package com.eventManagement.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	
	DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static String projectlocalPath = System.getProperty("user.dir");

	@Autowired
	EventRepository eventRepository;

	@Autowired
	EventUsersRepository eventUsersRepository;

	@Override
	public String createEvent(MultipartFile[] files, EventDto eventDto) {
		// need a method to validate eventDto
		LocalDateTime currentDate = LocalDateTime.now();

		logger.info("Started creating event with name :" + eventDto.getEventTitle());
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
			event.setCreatedOn(LocalDateTime.parse(currentDate.format(df), df));

			eventRepository.save(event);
		} catch (Exception ex) {
			logger.error("Error occurred while saving event : " + ex.getMessage());
			return "Error got while saving event : " + ex.getMessage();
		}
		return "Successfully saved event";
	}

	@Override
	public String updateEvent(Long id, EventDto eventDto, MultipartFile[] files) throws Exception {
		logger.info("Upadting Event With info : " + eventDto.getEventTitle());
		LocalDateTime currentDate = LocalDateTime.now();

		try {
			Event event = eventRepository.findById(id).get();
			if (event == null) {
				throw new Exception("Event not found");
			}
			event = parseEvent(event, eventDto);
			if (files != null && files.length > 0) {
				List<String> imageNames = saveFileInSystem(files);

				StringBuilder sb = new StringBuilder();
				for (String s : imageNames) {
					sb.append(s).append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				event.setImageName(sb.toString());
			}
			event.setLastUpdated(LocalDateTime.parse(currentDate.format(df), df));
			eventRepository.save(event);

		} catch (Exception ex) {
			logger.error("Error occurred updating saving event : " + ex.getMessage());
			return "Error got while updating event : " + ex.getMessage();
		}
		return "Successfully updated event";

	}

	private Event parseEvent(Event event, EventDto eventDto) throws ParseException {

		event.setAdminId(Long.parseLong(eventDto.getAdminId()));
		event.setAddress(eventDto.getAddress());
		event.setStartDate(LocalDateTime.parse(eventDto.getStartDate(), df));
		event.setEndDate(LocalDateTime.parse(eventDto.getEndDate(), df));
		event.setEventCategory(eventDto.getEventCategory());
		event.setEventDetails(eventDto.getEventDetails());
		event.setEventTitle(eventDto.getEventTitle());
		event.setEventType(eventDto.getEventType());
		event.setLink(eventDto.getLink());
		event.setEventRewardPoints(eventDto.getEventRewardPoints());
		event.setLocation(eventDto.getLocation());
		event.setUserType(eventDto.getUserType());
		return event;
	}

	@Override
	public List<Event> getAllEvent(Long adminId, String eventCategory, String eventType, String eventDate,
			boolean isDashboard, String title) {
		if (title.equals("all")) {
			title = "";
		}
		logger.info("Getting event for Admin : " + adminId + " and Eventtitle : " + title);
		List<Event> eventList = null;

		if (isDashboard) {
			Pageable pageable = PageRequest.of(0, 5);
			LocalDateTime dateOfEvent = getEventDate(eventDate, isDashboard);
            if(dateOfEvent == null) {
            	return new ArrayList<>();
            }
			eventList = eventRepository.findFirst5Event(adminId, eventCategory.toLowerCase(),
					eventType.toLowerCase(), dateOfEvent, title.toLowerCase(), pageable);
			// List<Event> eventListStream =
			// eventList.getContent().stream().limit(5).collect(Collectors.toList());
		} else {
			if (!eventDate.equals("")) {
				LocalDateTime dateOfEvent = getEventDate(eventDate, false);
				eventList = eventRepository.filterEventsWithDate(adminId, eventCategory.toLowerCase(),
						eventType.toLowerCase(), title.toLowerCase(), dateOfEvent);
			} else {
				eventList = eventRepository.filterEvents(adminId, eventCategory.toLowerCase(), eventType.toLowerCase(),
						title.toLowerCase());
			}
		}

		if (eventList != null) {
			int i = 0;
			for (Event event : eventList) {
				String[] images = event.getImageName().split(",");
				for (String str : images) {
					//str = projectlocalPath + "//" + str;
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

	private LocalDateTime getEventDate(String eventDate, boolean isDashboard) {
		LocalDateTime currentDate = LocalDateTime.now();

		try {
			if (isDashboard && eventDate.equals("")) {
				return LocalDateTime.parse(currentDate.format(df), df);
			}
			if (isDashboard && !eventDate.equals("")) {
				LocalDateTime localEventDate = LocalDateTime.parse(eventDate, df);
				currentDate = LocalDateTime.parse(currentDate.format(df), df);
				if(localEventDate.isBefore(currentDate)) {
					return null;
				}else {
					return localEventDate;
				}
			}

			
			currentDate = LocalDateTime.parse(eventDate, df);
		} catch (Exception ex) {
			logger.error("Error occure while parsing date: " + ex.getMessage());
		}
		return currentDate;
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
		//Path imagePath = Paths.get("src", "main", "resources", "static", image.getOriginalFilename());
        //Files.write(imagePath, image.getBytes());
		String UPLOAD_DIR = "src//main//resources//static//img";
		for (MultipartFile file : files) {
			//String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			try {
				//Path path = Paths.get(UPLOAD_DIR + fileName);
				//Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				 String fileName = file.getOriginalFilename();
			        Path imagePath = Path.of("src", "main", "resources", "static","img",fileName);
			        Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

				fileNames.add(fileName);
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
		logger.info("fetching Event With Id : " + eventId);
		Event event = null;
		try {
			event = eventRepository.findById(eventId).get();
			String str = event.getImageName();
			//str = projectlocalPath + "//" + str;
			event.setImageName(str);
			return event;
		} catch (Exception ex) {
			logger.error("Exception got fetching event by id : " + eventId);
		}
		return null;
	}

	@Override
	public String registerEventUser(@Valid EventUsersDto eventUsersDto) {
		logger.info("Registering Event user for event :" + eventUsersDto.getEventId());
		String response = "";
		LocalDate currentDate = LocalDate.now();

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
				eventUser.setCreatedOn(LocalDate.parse(currentDate.format(df), df));
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
	public Page<EventUsers> getEventRegisterUsers(Long eventId, int page, int size) {
		logger.info("Getting Event Registered Users for EventID :" + eventId);
		Page<EventUsers> eventUserList = null;
		Optional<Event> event = eventRepository.findById(eventId);
		if (event.isPresent()) {
			PageRequest pageReq = PageRequest.of(page, size);
			eventUserList = eventUsersRepository.findAllEventByEventIdPage(eventId, pageReq);
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

	@Override
	public List<EventUsers> getExportEventRegisterUsers(Long eventId) {
		logger.info("Getting Event Registered Users Export Data for EventID :" + eventId);
		List<EventUsers> eventUserList = null;
		Optional<Event> event = eventRepository.findById(eventId);
		if (event.isPresent()) {
			eventUserList = eventUsersRepository.findAllEventByEventId(eventId);

		} else {
			logger.error("No such event Exists");
		}
		return eventUserList;
	}
}
