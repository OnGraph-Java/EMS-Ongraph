package com.eventManagement.controller;

import static com.eventManagement.utils.StringUtils.getNotNullString;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eventManagement.dto.GiftDto;
import com.eventManagement.model.EventUsers;
import com.eventManagement.model.Gift;
import com.eventManagement.service.GiftService;
import com.opencsv.CSVWriter;

@RestController
@RequestMapping("/gift")
@Api(tags = "Gift Controller")
public class GiftController {

	@Autowired
	GiftService giftService;

	@PostMapping(value = "/addGift", produces = MediaType.APPLICATION_JSON_VALUE, consumes = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
	@ApiOperation("Add a gift")
	@ApiResponses({ @ApiResponse(code = 200, message = "Gift added successfully"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<HashMap<String, String>> addGift(
			@ApiParam(value = "Gift file") @RequestPart("file") MultipartFile file,
			@ApiParam(value = "Gift data") @RequestPart("data") @Valid GiftDto giftDto, BindingResult result) {
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
			response = giftService.addGift(giftDto, file);
			res.put("response", response);
		} catch (Exception ex) {
			res.put("response", ex.getMessage());
			return ResponseEntity.badRequest().body(res);
		}
		return ResponseEntity.ok().body(res);
	}

	@PostMapping("/updateGift/{giftId}")
	@ApiOperation("Update a gift")
	@ApiResponses({ @ApiResponse(code = 200, message = "Gift updated successfully"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<HashMap<String, String>> updateGift(
			@ApiParam(value = "Gift file") @RequestPart(value = "files", required = false) MultipartFile file,
			@ApiParam(value = "Gift data") @RequestPart("data") @Valid GiftDto giftDto, BindingResult result,
			@ApiParam(value = "Gift ID", example = "123") @PathVariable("giftId") Long giftId) {
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
			response = giftService.updateGift(giftId, giftDto, file);
			res.put("response", response);

		} catch (Exception ex) {
			res.put("response", ex.getMessage());
			return ResponseEntity.badRequest().body(res);
		}
		return ResponseEntity.ok().body(res);
	}

	@GetMapping("/findGift/{giftId}")
	@ApiOperation("Find a gift by ID")
	@ApiResponses({ @ApiResponse(code = 200, message = "Gift found"), @ApiResponse(code = 400, message = "Bad request"),
			@ApiResponse(code = 404, message = "Gift not found") })
	public ResponseEntity<?> findGift(
			@ApiParam(value = "Gift ID", example = "123") @PathVariable("giftId") Long giftId) {
		HashMap<String, String> res = new HashMap<>();

		Gift gift = null;
		try {
			gift = giftService.findGift(giftId);
		} catch (Exception ex) {
			res.put("response", ex.getMessage());
			return ResponseEntity.badRequest().body(res);
		}

		return ResponseEntity.ok(gift);
	}

	@GetMapping("/getAllGift/{adminId}")
	@ApiOperation("Get all gifts")
	@ApiResponses({ @ApiResponse(code = 200, message = "Gifts found"),
			@ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 404, message = "No gifts found") })
	public ResponseEntity<?> getAllGift(
			@ApiParam(value = "Admin ID", example = "123") @PathVariable("adminId") Long adminId,
			@ApiParam(value = "Title") @RequestParam(name = "title", defaultValue = "") String title,
			@ApiParam(value = "Sort by", allowableValues = "createdOn, giftTitle, points") @RequestParam(name = "sortBy", defaultValue = "createdOn") String sortBy,
			@ApiParam(value = "Is dashboard") @RequestParam(value = "isDashboard", required = false) boolean isDashboard,
			@ApiParam(value = "From Date") @RequestParam(value = "fromDate", required = false) String fromDate,
			@ApiParam(value = "End Date") @RequestParam(value = "endDate", required = false) String endDate) {

		List<Gift> giftList = null;
		try {
			giftList = giftService.findAllGift(adminId, sortBy, title, isDashboard, fromDate, endDate);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}

		return ResponseEntity.ok(giftList);
	}

	@GetMapping("/exportGift/{adminId}")
	@ApiOperation("Export gift register users")
	@ApiResponses({ @ApiResponse(code = 200, message = "Export file generated successfully"),
			@ApiResponse(code = 200, message = "No Gifts found"), @ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<?> exportGiftRegisterUsers(
			@ApiParam(value = "Admin Id", example = "123") @PathVariable("adminId") Long adminId,
			HttpServletResponse response) {
		try {
			List<Gift> giftList = giftService.findAllGift(adminId, "createdOn", "", false, null, null);
			if (giftList != null && giftList.size() > 0) {
				generateExportFile(giftList, response);
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

	private void generateExportFile(List<Gift> giftList, HttpServletResponse response) throws IOException {

		response.setHeader("Content-Disposition", "attachment; filename=\"GiftHistory.csv\"");
		response.setContentType("text/csv");

		CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));

		generateEventsExportSheet(csvWriter, giftList);
		csvWriter.flush();
		csvWriter.close();
	}

	private void generateEventsExportSheet(CSVWriter csvWriter, List<Gift> giftList) {

		String[] header = { "Gift Title", "Gift Detail", "Available-For", "redeemRequirePoints", "Image Name" };
		csvWriter.writeNext(header);

		for (Gift gift : giftList) {
			String[] data = { String.valueOf(gift.getGiftTitle()), getNotNullString(gift.getGiftDetail()),
					gift.getAvailableFor().toString(), gift.getRedeemRequirePoints().toString(),
					getNotNullString(gift.getImageName()) };
			csvWriter.writeNext(data);
		}

	}

}
