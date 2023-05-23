package com.eventManagement.controller;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventManagement.dto.RewardPoints;
import com.eventManagement.dto.RewardsDto;
import com.eventManagement.model.Reward;
import com.eventManagement.model.UserRewards;
import com.eventManagement.model.UserRewardsHistory;
import com.eventManagement.service.RewardFilter;
import com.eventManagement.service.RewardService;

import com.opencsv.CSVWriter;

@RestController
@RequestMapping("/rewards")
public class RewardsController {

	private Logger logger = LoggerFactory.getLogger(RewardsController.class.getName());

	@Autowired
	RewardService rewardService;

	@Autowired
	RewardFilter rewardFilter;

	@PostMapping("/saveReward")
	public ResponseEntity<String> saveReward(@RequestBody @Valid RewardsDto rewardsDto) {

		String response = null;
		try {
			response = rewardService.saveReward(rewardsDto);
		} catch (Exception ex) {
			logger.error("Exception caught while saving reward : " + ex.getMessage());
			return ResponseEntity.ok().body(ex.getMessage());
		}

		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/getAllUserRewards/{adminId}")
	public ResponseEntity<?> getAllUserRewards(@PathVariable("adminId") Long adminId,
			@RequestParam(value = "rewardRange", defaultValue = "0") Long rewardRange,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "8") int size,
			@RequestParam(value = "sortBy", defaultValue = "createdOn") String sortBy) {
		List<UserRewards> list = null;
		try {
			list = rewardService.getAllUserRewardsList(adminId, rewardRange, page, size, sortBy);
		} catch (Exception ex) {
			logger.error("exception got while fetching UserRewards : " + ex.getMessage());
			return ResponseEntity.badRequest().body("exception got while fetching UserRewards : " + ex.getMessage());
		}
		return ResponseEntity.ok().body(list);

	}

	@GetMapping("/searchRewardUsers/{adminId}")
	public ResponseEntity<?> searchRewardUsers(@PathVariable("adminId") Long adminId,
			@RequestParam(value = "username", required = true) String username) {
		List<UserRewards> list = null;
		try {
			list = rewardService.searchRewardsUserList(adminId, username);
		} catch (Exception ex) {
			logger.error("exception got while fetching UserRewards : " + ex.getMessage());
			return ResponseEntity.badRequest().body(
					"exception got while searchind User Rewards with usernmae : " + username + " : " + ex.getMessage());
		}
		return ResponseEntity.ok().body(list);
	}

	@GetMapping("/getUserRewardsHistory/{userId}")
	public ResponseEntity<?> getUserRewardsHistory(@PathVariable("userId") Long userId,
			@RequestParam(value = "activityType", required = false, defaultValue = "all") String activityType,
			@RequestParam(value = "fromDate", required = false, defaultValue = "all") String fromDate,
			@RequestParam(value = "endDate", required = false, defaultValue = "all") String endDate,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "8") int size) {

		List<UserRewardsHistory> list = null;
		try {
			list = rewardFilter.getUserRewardsHistory(userId, activityType, fromDate, endDate, page, size);
		} catch (Exception ex) {
			logger.error("exception got while fetching UserRewards History : " + ex.getMessage());
			return ResponseEntity.badRequest()
					.body("exception got while fetching UserRewards History: " + ex.getMessage());
		}
		return ResponseEntity.ok().body(list);
	}

	@GetMapping("/searchUserReward/{userId}")
	public ResponseEntity<?> searchUserReward(@PathVariable("userId") Long userId,
			@RequestParam(value = "activityType", required = true) String activityType) {
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
	public ResponseEntity<String> getUserRewardsPoints(@PathVariable("userId") Long userId) {
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
	public ResponseEntity<?> getRewardsHistory(@PathVariable("adminId") Long adminId,
			                              @RequestParam(defaultValue = "0") int page,
	                                      @RequestParam(defaultValue = "8") int size) {
		List<Reward> list = null;
		try {
			list = rewardService.getRewardsList(adminId, page, size);
		} catch (Exception ex) {
			logger.error("exception got while fetching Rewards History : " + ex.getMessage());
			return ResponseEntity.badRequest().body("exception got while fetching Rewards History: " + ex.getMessage());
		}
		return ResponseEntity.ok().body(list);
	}

	@GetMapping("/getRewardUserCount/{adminId}")
	public ResponseEntity<?> getRewardUserCount(@PathVariable("adminId") Long adminId) {
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
	public ResponseEntity<?> getRewardExport(@PathVariable("adminId") Long adminId, HttpServletResponse response) {

		List<Reward> rewardlist = null;
		try {
			rewardlist = rewardService.getRewardsList(adminId, 0, 0);
			generateExportFile(rewardlist, response);

		} catch (Exception ex) {
			logger.error("exception got while fetching Rewards History : " + ex.getMessage());
			return ResponseEntity.badRequest().body("exception got while fetching Rewards History: " + ex.getMessage());
		}
		return ResponseEntity.ok().body("");
	}

	@GetMapping("/getUserRewardExport/{userId}")
	public ResponseEntity<?> getUserRewardExport(@PathVariable("userId") Long userId, HttpServletResponse response) {

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
