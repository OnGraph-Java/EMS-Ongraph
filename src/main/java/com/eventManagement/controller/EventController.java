package com.eventManagement.controller;

import com.eventManagement.dto.*;
import com.eventManagement.model.Event;
import com.eventManagement.model.EventUsers;
import com.eventManagement.service.EventService;
import com.eventManagement.service.StatisticsService;
import com.opencsv.CSVWriter;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.eventManagement.utils.StringUtils.getNotNullString;

@RestController
@RequestMapping("/event")
@Api(tags = "Event Controller")
public class EventController {

	@Autowired
	EventService eventService;

	@Autowired
	StatisticsService statisticsService;

	// completed apart of event start end date/time
	@PostMapping("/createEvent")
	@ApiOperation("Create an event")
	@ApiResponses({ @ApiResponse(code = 200, message = "Event created successfully"),
			@ApiResponse(code = 400, message = "Bad request") })
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
	@ApiOperation("Update an event")
	@ApiResponses({ @ApiResponse(code = 200, message = "Event updated successfully"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<String> updateEvent(@PathVariable("id") Long id, @RequestPart("files") MultipartFile[] files,
			@RequestPart("data") @Valid EventDto event) {

		String response = "";
		try {
			response = eventService.updateEvent(id, event, files);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/getAllEvent/{adminId}")
	@ApiOperation("Get all events")
	@ApiResponses({ @ApiResponse(code = 200, message = "List of events returned successfully"),
			@ApiResponse(code = 404, message = "Events not found") })
	public ResponseEntity<List<Event>> getAllEvent(
			@ApiParam(value = "Admin ID", example = "123") @PathVariable("adminId") Long adminId,
			@ApiParam(value = "Event category") @RequestParam(value = "eventCategory", defaultValue = "", required = false) String eventCategory,
			@ApiParam(value = "Event type") @RequestParam(value = "eventType", defaultValue = "", required = false) String eventType,
			@ApiParam(value = "Event date") @RequestParam(value = "eventDate", defaultValue = "", required = false) String eventDate,
			@ApiParam(value = "Is dashboard") @RequestParam(value = "isDashboard", required = false) boolean isDashboard,
			@ApiParam(value = "Page number", example = "0") @RequestParam(value = "page", defaultValue = "0") int page,
			@ApiParam(value = "Page size", example = "8") @RequestParam(value = "size", defaultValue = "8") int size,
			@ApiParam(value = "Event title") @RequestParam(value = "title", defaultValue = "all") String title) {

		List<Event> eventList = eventService.getAllEvent(adminId, eventCategory, eventType, eventDate, isDashboard,
				page, size, title);
		if (eventList != null) {
			return ResponseEntity.ok().body(eventList);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/getEvent/{eventId}")
	@ApiOperation(value = "Get Event by ID", notes = "Retrieve an event by its ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success", response = Event.class),
			@ApiResponse(code = 200, message = "No event found with the given ID", response = String.class)
	})
	public ResponseEntity<?> getAllEvent(@PathVariable("eventId") @ApiParam(value = "Event ID", example = "123") Long eventId) {
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
	@ApiOperation("Register an event user")
	@ApiResponses({ @ApiResponse(code = 200, message = "Event user registered successfully"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<String> registerEventUser(@RequestBody @Valid EventUsersDto eventUsersDto,
			BindingResult result) {
		String respose = "";
		if (result.hasErrors()) {
			// Build error message and return bad request response
			StringBuilder errorMessage = new StringBuilder();
			result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(". "));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
		}
		try {
			respose = eventService.registerEventUser(eventUsersDto);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		return ResponseEntity.ok().body(respose);
	}

	@GetMapping("/getEventUsers/{eventId}")
	@ApiOperation("Get event register users")
	@ApiResponses({ @ApiResponse(code = 200, message = "List of event users returned successfully"),
			@ApiResponse(code = 200, message = "No event users exist") })
	public ResponseEntity<?> getEventRegisterUsers(
			@ApiParam(value = "Event ID", example = "123") @PathVariable("eventId") Long eventId) {
		List<EventUsers> eventUserList = eventService.getEventRegisterUsers(eventId);
		if (eventUserList != null && eventUserList.size() > 0) {
			return ResponseEntity.ok().body(eventUserList);
		} else {
			return ResponseEntity.ok().body("no event users exist");
		}
	}

	@GetMapping("/exportEventUsers/{eventId}")
	@ApiOperation("Export event register users")
	@ApiResponses({ @ApiResponse(code = 200, message = "Export file generated successfully"),
			@ApiResponse(code = 200, message = "No events found"), @ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<?> exportEventRegisterUsers(
			@ApiParam(value = "Event ID", example = "123") @PathVariable("eventId") Long eventId,
			HttpServletResponse response) {
		try {
			List<EventUsers> eventUserList = eventService.getEventRegisterUsers(eventId);
			if (eventUserList != null && eventUserList.size() > 0) {
				generateExportFile(eventUserList, response);
				return ResponseEntity.ok().body(eventUserList);
			} else {
				return ResponseEntity.ok().body("no events found");
			}
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
	}

	@GetMapping("/getEventStatictics/{adminId}")
	@ApiOperation("Get event statistics")
	@ApiResponses({ @ApiResponse(code = 200, message = "Event statistics retrieved successfully"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<?> getEventStatictics(
			@ApiParam(value = "Admin ID", example = "123") @PathVariable("adminId") Long adminId) {

		List<Statistics> statisticsList = statisticsService.getDashboardStatistics(adminId);
		PeopleInvited peopleInvited = new PeopleInvited(Long.valueOf(1000), Long.valueOf(200), Long.valueOf(1200));
		EventDashboardStatistics stats = new EventDashboardStatistics(statisticsList, peopleInvited);
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
			String[] data = { String.valueOf(event.getRegistrationId()), getNotNullString(event.getName()),
					getNotNullString(event.getUserType()), getNotNullString(event.getPhoneNo()),
					getNotNullString(event.getEmail()), getNotNullString(event.getRegistrationDate()) };
			csvWriter.writeNext(data);
		}

	}

}
