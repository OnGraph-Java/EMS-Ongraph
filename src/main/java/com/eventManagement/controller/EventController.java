package com.eventManagement.controller;

import static com.eventManagement.utils.StringUtils.getNotNullString;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eventManagement.dto.EventDashboardStatistics;
import com.eventManagement.dto.EventDto;
import com.eventManagement.dto.EventUsersDto;
import com.eventManagement.dto.PeopleInvited;
import com.eventManagement.dto.Statistics;
import com.eventManagement.model.Event;
import com.eventManagement.model.EventUsers;
import com.eventManagement.service.EventService;
import com.eventManagement.service.StatisticsService;
import com.opencsv.CSVWriter;

@RestController
@RequestMapping("/event")
public class EventController {

	@Autowired
	EventService eventService;

	@Autowired
	StatisticsService statisticsService;
	
	//completed apart of event start end date/time
	@PostMapping("/createEvent")
	public ResponseEntity<String> createEvent(@RequestPart("files") MultipartFile[] files,
			@RequestPart("data") @Valid EventDto event, BindingResult result) {
		
		if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(". "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
		String response = "";
		try {
			response = eventService.createEvent(files, event);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/updateEvent/{id}")
	public ResponseEntity<String> updateEvent(@PathVariable("id") Long id, @RequestPart("files") MultipartFile[] files,
			@RequestPart("data") @Valid EventDto event){
		
		String response = "";
		try {
			response = eventService.updateEvent(id, event, files);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/getAllEvent/{adminId}")
	public ResponseEntity<List<Event>> getAllEvent(@PathVariable("adminId") Long adminId,
			@RequestParam(value = "eventCategory", defaultValue = "", required = false) String eventCategory,
			@RequestParam(value = "eventType", defaultValue = "", required = false) String eventType,
			@RequestParam(value = "eventDate", defaultValue = "", required = false) String eventDate,
			@RequestParam(value = "isDashboard", required = false) boolean isDashboard,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "8") int size,
			@RequestParam(value = "title", defaultValue = "all") String title) {

		List<Event> eventList = eventService.getAllEvent(adminId, eventCategory, eventType, eventDate, isDashboard, page, size, title);
		if (eventList != null) {
			return ResponseEntity.ok().body(eventList);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/getEvent/{eventId}")
	public ResponseEntity<?> getAllEvent(@PathVariable("eventId") Long eventId ){
		Event event = eventService.getEvent(eventId);
		if (event != null) {
			return ResponseEntity.ok().body(event);
		} else {
			return ResponseEntity.ok().body("no such event exist");
		}
	}
	
//
//	@GetMapping("/searchEvent/{title}")
//	public ResponseEntity<?> searchEvent(@PathVariable("title") String title) {
//		List<Event> eventList = eventService.searchEvent(title);
//		if (eventList != null) {
//			return ResponseEntity.ok().body(eventList);
//		} else {
//			return ResponseEntity.ok().body("no such event exist");
//		}
//	}
	
	@PostMapping("/registerEventUser")
	public ResponseEntity<String> registerEventUser(@RequestBody @Valid EventUsersDto eventUsersDto,  BindingResult result){
		String respose = "";
		if (result.hasErrors()) {
            // Build error message and return bad request response
            StringBuilder errorMessage = new StringBuilder();
            result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(". "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
		try {
			respose = eventService.registerEventUser(eventUsersDto);
		}catch(Exception ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		return ResponseEntity.ok().body(respose);
	}
	
	@GetMapping("/getEventUsers/{eventId}")
	public ResponseEntity<?> getEventRegisterUsers(@PathVariable("eventId") Long eventId){
		List<EventUsers> eventUserList = eventService.getEventRegisterUsers(eventId);
		if (eventUserList != null && eventUserList.size() > 0) {
			return ResponseEntity.ok().body(eventUserList);
		} else {
			return ResponseEntity.ok().body("no event users exist");
		}
	}
	
	@GetMapping("/exportEventUsers/{eventId}")
	public ResponseEntity<?> exportEventRegisterUsers(@PathVariable("eventId") Long eventId, HttpServletResponse response){
		try {
			List<EventUsers> eventUserList = eventService.getEventRegisterUsers(eventId);
			if (eventUserList != null && eventUserList.size() > 0) {
				generateExportFile(eventUserList, response);
				return ResponseEntity.ok().body(eventUserList);
			} else {
				return ResponseEntity.ok().body("no events found");
			}
		} catch(Exception ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
	}
	
	@GetMapping("/getEventStatictics/{adminId}")
	public ResponseEntity<?> getEventStatictics(@PathVariable("adminId") Long adminId){
		
		List<Statistics> statisticsList = statisticsService.getDashboardStatistics(adminId);
		PeopleInvited peopleInvited = new PeopleInvited(Long.valueOf(1000), Long.valueOf(200), Long.valueOf(1200));
		EventDashboardStatistics stats  = new EventDashboardStatistics(statisticsList, peopleInvited);
		return ResponseEntity.ok().body(stats);
	}
	
	private void generateExportFile(List<EventUsers> eventList, HttpServletResponse response) throws IOException {

		response.setHeader("Content-Disposition", "attachment; filename=\"EventUsersHistory.csv\"");
		response.setContentType("text/csv");

		CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));

		generateEventsExportSheet(csvWriter, eventList);
		csvWriter.flush();
		csvWriter.close();
	}

	private void generateEventsExportSheet(CSVWriter csvWriter, List<EventUsers> eventList) {

		String[] header = { "Registration ID", "Name", "User Type", "Contact Number", "Email", "Registration Date" };
		csvWriter.writeNext(header);

		for (EventUsers event : eventList) {
			String[] data = { String.valueOf(event.getRegistrationId()), getNotNullString(event.getName()), getNotNullString(event.getUserType()),
							  getNotNullString(event.getPhoneNo()), getNotNullString(event.getEmail()), getNotNullString(event.getRegistrationDate()) };
			csvWriter.writeNext(data);
		}

	}

}
