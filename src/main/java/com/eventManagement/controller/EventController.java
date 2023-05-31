package com.eventManagement.controller;

import com.eventManagement.dto.*;
import com.eventManagement.model.Event;
import com.eventManagement.model.EventUsers;
import com.eventManagement.service.EventService;
import com.eventManagement.service.StatisticsService;
import com.opencsv.CSVWriter;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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
	@PostMapping(value = "/createEvent", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
	@ApiOperation("Create an event")
	@ApiResponses({ @ApiResponse(code = 200, message = "Event created successfully"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<HashMap<String, String>> createEvent(@RequestPart("files") MultipartFile[] files,
			@RequestPart("data") @Valid EventDto event, BindingResult result) {
		HashMap<String, String> res = new HashMap<>();

		if (result.hasErrors()) {
			StringBuilder errorMessage = new StringBuilder();
			result.getFieldErrors().forEach(error -> {
				String fieldName = error.getField();
				String defaultMessage = error.getDefaultMessage();
				errorMessage.append(fieldName).append(": ").append(defaultMessage).append(". ");
			});
			res.put("response", errorMessage.toString());
			result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(". "));
			res.put("response", errorMessage.toString());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
		}
		String response = "";
		try {

			response = eventService.createEvent(files, event);
			res.put("response", response);

		} catch (Exception ex) {
			res.put("response", ex.getMessage());

			return ResponseEntity.badRequest().body(res);
		}
		return ResponseEntity.ok().body(res);
	}

	@PostMapping(value = "/updateEvent/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
	@ApiOperation(value = "Update an event", consumes = "multipart/form-data")
	@ApiResponses({ @ApiResponse(code = 200, message = "Event updated successfully"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<HashMap<String, String>> updateEvent(@PathVariable("id") Long id,
			@RequestPart(value = "files", required = false) MultipartFile[] files,
			@RequestPart("data") @Valid EventDto event, BindingResult result) {
		HashMap<String, String> res = new HashMap<>();

		if (result.hasErrors()) {
			StringBuilder errorMessage = new StringBuilder();
			result.getFieldErrors().forEach(error -> {
				String fieldName = error.getField();
				String defaultMessage = error.getDefaultMessage();
				errorMessage.append(fieldName).append(": ").append(defaultMessage).append(". ");
			});
			res.put("response", errorMessage.toString());
			result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(". "));
			res.put("response", errorMessage.toString());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
		}
		String response = "";
		try {
			response = eventService.updateEvent(id, event, files);
			res.put("response", response);
		} catch (Exception ex) {
			res.put("response", ex.getMessage());
			return ResponseEntity.badRequest().body(res);
		}
		return ResponseEntity.ok().body(res);
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
			@ApiParam(value = "Event title") @RequestParam(value = "title", defaultValue = "all") String title) {

		List<Event> eventList = eventService.getAllEvent(adminId, eventCategory, eventType, eventDate, isDashboard,
				title);
		if (eventList != null) {
			return ResponseEntity.ok().body(eventList);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/getEvent/{eventId}")
	@ApiOperation(value = "Get Event by ID", notes = "Retrieve an event by its ID")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Event.class),
			@ApiResponse(code = 200, message = "No event found with the given ID", response = String.class) })
	public ResponseEntity<?> getEvent(
			@PathVariable("eventId") @ApiParam(value = "Event ID", example = "123") Long eventId) {
		Event event = eventService.getEvent(eventId);
		return ResponseEntity.ok().body(event);
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
	public ResponseEntity<?> registerEventUser(@RequestBody @Valid EventUsersDto eventUsersDto, BindingResult result) {
		HashMap<String, String> res = new HashMap<>();

		String respose = "";
		if (result.hasErrors()) {
			// Build error message and return bad request response
			StringBuilder errorMessage = new StringBuilder();
			result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(","));
			res.put("response", errorMessage.toString());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
		}
		try {
			respose = eventService.registerEventUser(eventUsersDto);
			res.put("response", respose);
		} catch (Exception ex) {
			res.put("response", ex.getMessage());
			return ResponseEntity.badRequest().body(res);
		}
		return ResponseEntity.ok().body(res);
	}

	@GetMapping("/getEventUsers/{eventId}")
	@ApiOperation("Get event register users")
	@ApiResponses({ @ApiResponse(code = 200, message = "List of event users returned successfully"),
			@ApiResponse(code = 200, message = "No event users exist") })
	public ResponseEntity<?> getEventRegisterUsers(
			@ApiParam(value = "Event ID", example = "123") @PathVariable("eventId") Long eventId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "7") int size) {
		Page<EventUsers> eventUserList = eventService.getEventRegisterUsers(eventId, page, size);
		if (eventUserList != null) {
			return ResponseEntity.ok().body(eventUserList);
		} else {
			HashMap<String, String> res = new HashMap<>();
			res.put("response", "no event users exist");
			return ResponseEntity.ok().body(res);
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
			List<EventUsers> eventUserList = eventService.getExportEventRegisterUsers(eventId);
			if (eventUserList != null && eventUserList.size() > 0) {
				generateExportFile(eventUserList, response);
				return ResponseEntity.ok().body("");
			} else {
				HashMap<String, String> res = new HashMap<>();
				res.put("response", "no events found");
				return ResponseEntity.ok().body(res);
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

	@GetMapping("/images/{imageName}")
	public ResponseEntity<Resource> getImage(@PathVariable String imageName, HttpServletRequest request)
			throws MalformedURLException {

		try {
			Path imagePath = Paths.get("event//images//").resolve(imageName).normalize();
			Resource imageResource = new UrlResource(imagePath.toUri());

			if (imageResource.exists() && imageResource.isReadable()) {
				MediaType contentType = determineContentType(imagePath.toFile());

				return ResponseEntity.ok().contentType(contentType).body(imageResource);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (MalformedURLException e) {
			return ResponseEntity.notFound().build();
		}
	}

	private MediaType determineContentType(File file) {
		String mimeType = new MimetypesFileTypeMap().getContentType(file);
		return MediaType.parseMediaType(mimeType);
	}

	@DeleteMapping("/delete/{eventId}")
	public ResponseEntity<HashMap<String, String>> deleteEvent(@PathVariable Long eventId) {
		String response = "";
		HashMap<String, String> res = new HashMap<>();
		try {
			response = eventService.deleteEvent(eventId);
			res.put("response", response);
		} catch (Exception e) {
			res.put("response", "An error occurred while deleting the event :" + e.getMessage());
		}
		return ResponseEntity.ok().body(res);
	}
}
