package com.eventManagement.controller;

import java.util.List;

import javax.validation.Valid;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.eventManagement.model.Gift;
import com.eventManagement.service.GiftService;

@RestController
@RequestMapping("/gift")
@Api(tags = "Gift Controller")
public class GiftController {

	@Autowired
	GiftService giftService;

	@PostMapping("/addGift")
	@ApiOperation("Add a gift")
	@ApiResponses({ @ApiResponse(code = 200, message = "Gift added successfully"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<String> addGift(@ApiParam(value = "Gift file") @RequestPart("file") MultipartFile file,
			@ApiParam(value = "Gift data") @RequestPart("data") @Valid GiftDto giftDto, BindingResult result) {

		if (result.hasErrors()) {
			StringBuilder errorMessage = new StringBuilder();
			result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(". "));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
		}
		String response = "";
		try {
			response = giftService.addGift(giftDto, file);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/updateGift/{giftId}")
	@ApiOperation("Update a gift")
	@ApiResponses({ @ApiResponse(code = 200, message = "Gift updated successfully"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<String> updateGift(@ApiParam(value = "Gift file") @RequestPart("file") MultipartFile file,
			@ApiParam(value = "Gift data") @RequestPart("data") @Valid GiftDto giftDto, BindingResult result,
			@ApiParam(value = "Gift ID", example = "123") @PathVariable("giftId") Long giftId) {

		if (result.hasErrors()) {
			StringBuilder errorMessage = new StringBuilder();
			result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(". "));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
		}
		String response = "";
		try {
			response = giftService.updateGift(giftId, giftDto, file);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/findGift/{giftId}")
	@ApiOperation("Find a gift by ID")
	@ApiResponses({ @ApiResponse(code = 200, message = "Gift found"), @ApiResponse(code = 400, message = "Bad request"),
			@ApiResponse(code = 404, message = "Gift not found") })
	public ResponseEntity<?> findGift(
			@ApiParam(value = "Gift ID", example = "123") @PathVariable("giftId") Long giftId) {

		Gift gift = null;
		try {
			gift = giftService.findGift(giftId);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		if (gift == null) {
			return ResponseEntity.ok("no such gift exist");
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
			@ApiParam(value = "Sort by", allowableValues = "createdOn, title") @RequestParam(name = "sortBy", defaultValue = "createdOn") String sortBy) {

		List<Gift> giftList = null;
		try {
			giftList = giftService.findAllGift(adminId, sortBy, title);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		if (giftList == null) {
			return ResponseEntity.ok("no such gift exist");
		}
		return ResponseEntity.ok(giftList);
	}
}
