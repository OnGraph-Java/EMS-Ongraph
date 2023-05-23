package com.eventManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eventManagement.model.UserRewards;
import com.eventManagement.model.UserRewardsHistory;

@Repository
public interface UserRewardsHistoryRepository extends JpaRepository<UserRewardsHistory, Long> {

	@Query("Select user from UserRewardsHistory user where user.userId = :userId")
	List<UserRewardsHistory> findByUserId(@Param("userId") Long userId);

	
	@Query("Select user from UserRewardsHistory user where user.userId = :userId AND LOWER(user.activityType) LIKE :activityType")
	List<UserRewardsHistory> findByActivityType(@Param("userId") Long userId, @Param("activityType")String activityType);

}
