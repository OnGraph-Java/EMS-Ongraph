package com.eventManagement.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eventManagement.controller.RewardsController;
import com.eventManagement.dto.RewardsDto;
import com.eventManagement.model.Reward;
import com.eventManagement.model.UserRewards;
import com.eventManagement.model.UserRewardsHistory;
import com.eventManagement.repository.RewardRepository;
import com.eventManagement.repository.UserRewardsHistoryRepository;
import com.eventManagement.repository.UserRewardsRepository;

@Service
public class RewardServiceImpl implements RewardService {

	private Logger logger = LoggerFactory.getLogger(RewardsController.class.getName());

	DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Autowired
	RewardRepository rewardRepository;

	@Autowired
	UserRewardsRepository userRewardsRepository;

	@Autowired
	UserRewardsHistoryRepository userRewardsHistoryRepository;

	@Override
	@Transactional
	public String saveReward(@Valid RewardsDto rewardsDto) {
		logger.info("Saving Reward with details :"+rewardsDto.getActivityType());
		String response = "";
		Reward reward = new Reward();
		try {
			reward = parseReward(reward, rewardsDto);
			rewardRepository.save(reward);
			saveUserRewards(reward, rewardsDto.getRewardUserId());
			// saveRewardHistory(reward, rewardsDto.getRewardUserId().size());
			response = "Successfully Saved Reward";
		} catch (Exception ex) {
			logger.error("Exception got while saving reward: " + ex.getMessage());
			response = "Exception got while saving reward: " + ex.getMessage();
			return response;
		}

		return response;
	}


	private void saveUserRewards(Reward reward, List<String> rewardUserId) {
		logger.info("Saving User Reward with Id's :"+rewardUserId.toString());

		UserRewards updatedUserRewards = new UserRewards();
		for (String userId : rewardUserId) {
			LocalDate currentDate = LocalDate.now();

			try {
				Optional<UserRewards> userRewards = userRewardsRepository.findById(Long.parseLong(userId));
				if (userRewards.isEmpty()) {
					UserRewards newUserReward = new UserRewards();
					newUserReward.setRewardPoints(reward.getPointPerUser());
					newUserReward.setUserId(Long.parseLong(userId));
					newUserReward.setAdminId(reward.getAdminId());
					newUserReward.setCreatedOn(LocalDate.parse(currentDate.format(df), df));
					updatedUserRewards = userRewardsRepository.save(newUserReward);
				} else {
					UserRewards oldUser = userRewards.get();
					// added user points
					oldUser.setRewardPoints(oldUser.getRewardPoints() + reward.getPointPerUser());
					oldUser.setUpdatedOn(LocalDate.parse(currentDate.format(df), df));
					updatedUserRewards = userRewardsRepository.save(oldUser);
				}
				saveUserRewardHistory(updatedUserRewards, reward);
			} catch (Exception ex) {
				logger.error("Exception got while saving user rewards");
			}
		}

	}

	private void saveUserRewardHistory(UserRewards userRewardsHistory, Reward reward) {
		LocalDate currentDate = LocalDate.now();
		logger.info("Saving User Reward History with Id's :"+userRewardsHistory.getUserId());

		UserRewardsHistory userRewardHistory = new UserRewardsHistory();
		try {
			userRewardHistory.setRewardId(reward.getRewardId());
			userRewardHistory.setUserId(userRewardsHistory.getUserId());
			userRewardHistory.setPoints(reward.getPointPerUser());
			userRewardHistory.setCreatedOn(LocalDate.parse(currentDate.format(df), df));
			userRewardHistory.setActivityType(reward.getActivityType());
			userRewardsHistoryRepository.save(userRewardHistory);
		} catch (Exception ex) {
			logger.error("Exception got while saving userRewardHistory : " + ex.getMessage());
		}

	}

	public Reward parseReward(Reward reward, RewardsDto rewardsDto) throws ParseException {
		LocalDate currentDate = LocalDate.now();
		reward.setNumberOfUser(Long.valueOf(rewardsDto.getRewardUserId().size()));
		reward.setActivityType(rewardsDto.getActivityType());
		reward.setComments(rewardsDto.getComments());
		reward.setPointPerUser(rewardsDto.getPointPerUser());
		reward.setAdminId(rewardsDto.getAdminId());
		reward.setRewardDate(LocalDate.parse(currentDate.format(df), df));
		reward.setStatus("Completed");
		return reward;
	}

	@Override
	public List<UserRewards> getAllUserRewardsList(Long adminId, Long rewardRange, int page, int size, String sortBy, String username) {
		if(username.equals("all")) {
        	username = "";
        }
        logger.info("Getting user rewards with AdminId :"+adminId+" & username : "+username);

		List<UserRewards> userRewardsList = null;
		try {
			PageRequest pageReq = PageRequest.of(page, size, Sort.by("createdOn")); 
			if (rewardRange > 0) {
				userRewardsList = userRewardsRepository.findByAdminIdAndReward(adminId, rewardRange, pageReq, username);
			} else {
				userRewardsList = userRewardsRepository.findByAdminId(adminId, pageReq, username);
			}
		} catch (Exception ex) {
			logger.error("Exception got while fetching User Rewards from DB : " + ex.getMessage());
		}
		return userRewardsList;
	}

	@Override
	public List<UserRewardsHistory> getAllUserRewardsHistory(Long userId) {
		logger.info("getting User Reward History for userID : "+userId);
		List<UserRewardsHistory> userRewardsHistoryList = null;
		try {
			userRewardsHistoryList = userRewardsHistoryRepository.findByUserId(userId);
		} catch (Exception ex) {
			logger.error("Exception got while fetching User Rewards History from DB : " + ex.getMessage());
		}
		return userRewardsHistoryList;
	}

	@Override
	public List<Reward> getRewardsList(Long adminId, int page, int size) {
		logger.info("getting Rewards List for adminId : "+adminId);

		List<Reward> rewardsList = null;

		try {
			if (page == 0 && size == 0) {
				rewardsList = rewardRepository.findByAdminId(adminId);
			} else {
				Pageable pageable = PageRequest.of(page, size);

				rewardsList = rewardRepository.findByAdminId(adminId, pageable);
			}
			for (Reward reward : rewardsList) {
				reward.setStatus("Completed");
			}
		} catch (Exception ex) {
			logger.error("Exception got while fetching Rewards History from DB : " + ex.getMessage());
		}
		return rewardsList;
	}

	@Override
	public List<UserRewards> searchRewardsUserList(Long adminId, String username) {
		List<UserRewards> userRewardsList = null;
		username = username.toLowerCase();
		username = "%" + username + "%";
		try {
			userRewardsList = userRewardsRepository.findByAdminIdAndUserName(adminId, username);

		} catch (Exception ex) {
			logger.error("Exception got while fetching User Rewards from DB : " + ex.getMessage());
		}
		return userRewardsList;
	}

	@Override
	public String getUserRewardsPoints(Long userId) {
        logger.info("Getting user rewards points for userId :"+userId);
		Optional<UserRewards> userReward = null;
		try {
			userReward = userRewardsRepository.findById(userId);
		} catch (Exception ex) {
			logger.error("Exception got while fetching user " + ex.getMessage());
			return "Exception got while fetching user " + ex.getMessage();
		}
		if (userReward.isEmpty()) {
			return "No such user exist";
		}
		return userReward.get().getRewardPoints().toString();
	}

	@Override
	public List<UserRewardsHistory> searchUserRewardsList(Long userId, String activityType) {
        logger.info("Searching user rewards for userId :"+userId+" with activityType :"+activityType);

		List<UserRewardsHistory> userRewardList = null;
		try {
			activityType = activityType.toLowerCase();
			activityType = "%" + activityType + "%";
			userRewardList = userRewardsHistoryRepository.findByActivityType(userId, activityType);
		} catch (Exception ex) {
			logger.error("Exception got while Searching user reward with name :" + activityType);
		}
		return userRewardList;
	}

	@Override
	public List<UserRewardsHistory> getUserRewardsList(Long userId) {
		logger.info("getting user rewards history for userId :"+userId);
		List<UserRewardsHistory> history = null;
		try {
			history = userRewardsHistoryRepository.findByUserId(userId);
		} catch (Exception ex) {
			logger.error("Exception got while fetching user reward with id userId : " + userId);
		}
		return history;
	}
}
