package com.eventManagement.service;

import java.util.List;

import javax.validation.Valid;

import com.eventManagement.dto.RewardsDto;
import com.eventManagement.model.Reward;
import com.eventManagement.model.UserRewards;
import com.eventManagement.model.UserRewardsHistory;

public interface RewardService {

	String saveReward(@Valid RewardsDto rewardsDto);

	List<UserRewards> getAllUserRewardsList(Long adminId, Long rewardRange, int page, int size, String sortBy, String username);

	List<UserRewardsHistory> getAllUserRewardsHistory(Long userId);

	List<Reward> getRewardsList(Long adminId, int pageNo, int size);

	List<UserRewards> searchRewardsUserList(Long adminId, String username);

	String getUserRewardsPoints(Long userId);

	List<UserRewardsHistory> searchUserRewardsList(Long userId, String activityType);

	List<UserRewardsHistory> getUserRewardsList(Long userId);

}
