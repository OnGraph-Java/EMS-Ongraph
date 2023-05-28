package com.eventManagement.controller;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventManagement.dto.RewardPoints;
import com.eventManagement.dto.RewardsDto;
import com.eventManagement.model.PageEntity;
import com.eventManagement.model.Reward;
import com.eventManagement.model.UserRewards;
import com.eventManagement.model.UserRewardsHistory;
import com.eventManagement.service.RewardFilter;
import com.eventManagement.service.RewardService;

import com.opencsv.CSVWriter;

@RestController
@RequestMapping("/rewards")
@Api(tags = "Reward Controller")
public class RewardsController {

	private Logger logger = LoggerFactory.getLogger(RewardsController.class.getName());

	@Autowired
	RewardService rewardService;

	@Autowired
	RewardFilter rewardFilter;

	@PostMapping("/saveReward")
	@ApiOperation("Save a reward")
	@ApiResponses({ @ApiResponse(code = 200, message = "Reward saved successfully"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<HashMap<String,String>> saveReward(@RequestBody @Valid RewardsDto rewardsDto, BindingResult result) {
		HashMap<String, String> res = new HashMap<>();
		String response = null;
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
		try {
			response = rewardService.saveReward(rewardsDto);
			res.put("response", response);
		} catch (Exception ex) {
			logger.error("Exception caught while saving reward : " + ex.getMessage());
			res.put("response", ex.getMessage());

			return ResponseEntity.badRequest().body(res);
		}

		return ResponseEntity.ok().body(res);
	}

	@GetMapping("/getAllUserRewards/{adminId}")
	@ApiOperation("Get all user rewards")
	@ApiResponses({ @ApiResponse(code = 200, message = "User rewards found"),
			            @ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<?> getAllUserRewards(
			@ApiParam(value = "Admin ID", example = "123") @PathVariable("adminId") Long adminId,
			@ApiParam(value = "Reward range", example = "0") @RequestParam(value = "rewardRange", defaultValue = "0") Long rewardRange,
			@ApiParam(value = "Page number") @RequestParam(value = "page", defaultValue = "0") int page,
			@ApiParam(value = "Page size") @RequestParam(value = "size", defaultValue = "5") int size,
			@ApiParam(value = "Sort by") @RequestParam(value = "sortBy", defaultValue = "createdOn") String sortBy,
			@ApiParam(value = "Username", defaultValue = "all") @RequestParam(value = "username", defaultValue = "all") String username) {
		Page<UserRewards> list = null;
		try {
			list = rewardService.getAllUserRewardsList(adminId, rewardRange, page, size, sortBy, username);
		} catch (Exception ex) {
			logger.error("exception got while fetching UserRewards : " + ex.getMessage());
			return ResponseEntity.badRequest().body("exception got while fetching UserRewards : " + ex.getMessage());
		}
		return ResponseEntity.ok().body(list);

	}

	@GetMapping("/getUserRewardsHistory/{userId}")
	@ApiOperation("Get user rewards history")
	@ApiResponses({ @ApiResponse(code = 200, message = "User rewards history found"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<?> getUserRewardsHistory(
			@ApiParam(value = "User ID", example = "123") @PathVariable("userId") Long userId,
			@ApiParam(value = "Activity type", defaultValue = "all") @RequestParam(value = "activityType", required = false, defaultValue = "all") String activityType,
			@ApiParam(value = "From date", defaultValue = "all") @RequestParam(value = "fromDate", required = false, defaultValue = "all") String fromDate,
			@ApiParam(value = "End date", defaultValue = "all") @RequestParam(value = "endDate", required = false, defaultValue = "all") String endDate,
			@ApiParam(value = "Page number", defaultValue = "0") @RequestParam(value = "page", defaultValue = "0") int page,
			@ApiParam(value = "Page size", defaultValue = "8") @RequestParam(value = "size", defaultValue = "8") int size) {

		Page<UserRewardsHistory> list = null;
		try {
			list = rewardService.getUserRewardsHistory(userId, activityType, fromDate, endDate, page, size);
		} catch (Exception ex) {
			logger.error("exception got while fetching UserRewards History : " + ex.getMessage());
			return ResponseEntity.badRequest()
					.body("exception got while fetching UserRewards History: " + ex.getMessage());
		}
		return ResponseEntity.ok().body(list);
	}

	@GetMapping("/searchUserReward/{userId}")
	@ApiOperation("Search user rewards")
	@ApiResponses({ @ApiResponse(code = 200, message = "User rewards found"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<?> searchUserReward(
			@ApiParam(value = "User ID", example = "123") @PathVariable("userId") Long userId,
			@ApiParam(value = "Activity type", required = true) @RequestParam(value = "activityType", required = true) String activityType) {
		List<UserRewardsHistory> list = null;
		try {
			list = rewardService.searchUserRewardsList(userId, activityType);
		} catch (Exception ex) {
			logger.error("exception got while fetching UserRewards : " + ex.getMessage());
			return ResponseEntity.badRequest()
					.body("exception got while searchind User Rewards with ID : " + userId + " : " + ex.getMessage());
		}
		return ResponseEntity.ok().body(list);
	}

	@GetMapping("/getUserRewardsPoints/{userId}")
	@ApiOperation("Get user rewards points")
	@ApiResponses({ @ApiResponse(code = 200, message = "User rewards points retrieved"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<String> getUserRewardsPoints(
			@ApiParam(value = "User ID", example = "123") @PathVariable("userId") Long userId) {
		String rewardPoints;
		try {
			rewardPoints = rewardService.getUserRewardsPoints(userId);
		} catch (Exception ex) {
			logger.error("exception got while fetching UserRewards Points : " + ex.getMessage());
			return ResponseEntity.badRequest()
					.body("exception got while fetching UserRewards Points: " + ex.getMessage());
		}
		return ResponseEntity.ok().body(rewardPoints);

	}

	@GetMapping("/getRewardsHistory/{adminId}")
	@ApiOperation("Get rewards history")
	@ApiResponses({ @ApiResponse(code = 200, message = "Rewards history retrieved"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<?> getRewardsHistory(
			@ApiParam(value = "Admin ID", example = "123") @PathVariable("adminId") Long adminId,
			@ApiParam(value = "Page number", example = "0") @RequestParam(defaultValue = "0") int page,
			@ApiParam(value = "Page size", example = "8") @RequestParam(defaultValue = "8") int size) {
		Page<Reward> list = null;

		try {
			list = rewardService.getRewardsListPage(adminId, page, size);
		} catch (Exception ex) {
			logger.error("exception got while fetching Rewards History : " + ex.getMessage());
			return ResponseEntity.badRequest().body("exception got while fetching Rewards History: " + ex.getMessage());
		}
		return ResponseEntity.ok().body(list);
	}

	@GetMapping("/getRewardUserCount/{adminId}")
	@ApiOperation("Get reward user count")
	@ApiResponses({ @ApiResponse(code = 200, message = "Reward user count retrieved"),
			@ApiResponse(code = 400, message = "Bad request") })

	public ResponseEntity<?> getRewardUserCount(
			@ApiParam(value = "Admin ID", example = "123") @PathVariable("adminId") Long adminId) {
		List<RewardPoints> list = null;
		try {
			list = rewardFilter.getRewardsPoints(adminId);
		} catch (Exception ex) {
			logger.error("exception got while fetching Rewards History : " + ex.getMessage());
			return ResponseEntity.badRequest().body("exception got while fetching Rewards History: " + ex.getMessage());
		}
		return ResponseEntity.ok().body(list);
	}

	@GetMapping("/getRewardExport/{adminId}")
	@ApiOperation("Export rewards")
	@ApiResponses({ @ApiResponse(code = 200, message = "Rewards exported successfully"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<?> getRewardExport(
			@ApiParam(value = "Admin ID", example = "123") @PathVariable("adminId") Long adminId,
			HttpServletResponse response) {

		List<Reward> rewardlist = null;
		try {
			rewardlist = rewardService.getRewardsListExport(adminId);
			generateExportFile(rewardlist, response);

		} catch (Exception ex) {
			logger.error("exception got while fetching Rewards History : " + ex.getMessage());
			return ResponseEntity.badRequest().body("exception got while fetching Rewards History: " + ex.getMessage());
		}
		return ResponseEntity.ok().body("");
	}

	@GetMapping("/getUserRewardExport/{userId}")
	@ApiOperation("Export user rewards")
	@ApiResponses({ @ApiResponse(code = 200, message = "User rewards exported successfully"),
			@ApiResponse(code = 400, message = "Bad request") })
	public ResponseEntity<?> getUserRewardExport(
			@ApiParam(value = "User ID", example = "123") @PathVariable("userId") Long userId,
			HttpServletResponse response) {

		List<UserRewardsHistory> userRewardList = null;
		try {
			userRewardList = rewardService.getUserRewardsList(userId);
			generateUserRewardExportFile(userRewardList, response);

		} catch (Exception ex) {
			logger.error("exception got while fetching Rewards History : " + ex.getMessage());
			return ResponseEntity.badRequest().body("exception got while fetching Rewards History: " + ex.getMessage());
		}
		return ResponseEntity.ok().body("");
	}

	private void generateUserRewardExportFile(List<UserRewardsHistory> userRewardList, HttpServletResponse response)
			throws IOException {

		response.setHeader("Content-Disposition", "attachment; filename=\"UserRewardHistory.csv\"");
		response.setContentType("text/csv");

		CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));

		generateUserRewardsExportSheet(csvWriter, userRewardList);
		csvWriter.flush();
		csvWriter.close();

	}

	private void generateUserRewardsExportSheet(CSVWriter csvWriter, List<UserRewardsHistory> userRewardList) {

		String[] header = { "ActivityType", "Points", "RewardDate" };
		csvWriter.writeNext(header);

		for (UserRewardsHistory reward : userRewardList) {
			String[] data = { reward.getActivityType(), String.valueOf(reward.getPoints()),
					String.valueOf(reward.getCreatedOn()) };
			csvWriter.writeNext(data);
		}

	}

	private void generateExportFile(List<Reward> rewardList, HttpServletResponse response) throws IOException {

		response.setHeader("Content-Disposition", "attachment; filename=\"RewardHistory.csv\"");
		response.setContentType("text/csv");

		CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));

		generateRewardsExportSheet(csvWriter, rewardList);
		csvWriter.flush();
		csvWriter.close();
	}

	private void generateRewardsExportSheet(CSVWriter csvWriter, List<Reward> rewardList) {

		String[] header = { "ActivityType", "PointPerUser", "NumberOfUser", "RewardDate", "Status" };
		csvWriter.writeNext(header);

		for (Reward reward : rewardList) {
			String[] data = { reward.getActivityType(), String.valueOf(reward.getPointPerUser()),
					String.valueOf(reward.getNumberOfUser()), String.valueOf(reward.getRewardDate()),
					reward.getStatus() };
			csvWriter.writeNext(data);
		}

	}

}
