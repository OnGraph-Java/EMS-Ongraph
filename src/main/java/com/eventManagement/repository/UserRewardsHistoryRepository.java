package com.eventManagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eventManagement.model.UserRewardsHistory;

@Repository
public interface UserRewardsHistoryRepository extends PagingAndSortingRepository<UserRewardsHistory, Long> {

	@Query("Select user from UserRewardsHistory user where user.userId = :userId ORDER BY user.createdOn desc")
	List<UserRewardsHistory> findByUserId(@Param("userId") Long userId);

	
	@Query("Select user from UserRewardsHistory user where user.userId = :userId AND LOWER(user.activityType) LIKE %:activityType% ORDER BY user.createdOn desc")
	List<UserRewardsHistory> findByActivityType(@Param("userId") Long userId, @Param("activityType")String activityType);
	
	@Query("Select user from UserRewardsHistory user where user.userId = :userId AND LOWER(user.activityType) LIKE %:activityType% ORDER BY user.createdOn desc")
	Page<UserRewardsHistory> filterUserRewardsHistory(@Param("userId") Long userId, @Param("activityType") String  activityType,  Pageable pageable);

	@Query("Select user from UserRewardsHistory user where user.userId = :userId AND LOWER(user.activityType) LIKE %:activityType% AND user.createdOn >= :localStartdate AND user.createdOn <= :localEnddate ORDER BY user.createdOn desc")
	Page<UserRewardsHistory> filterUserRewardsHistoryWithDates(@Param("userId") Long userId, @Param("activityType") String  activityType,
			@Param("localStartdate") LocalDate localStartdate, @Param("localEnddate") LocalDate localEnddate, Pageable pageable);

	@Query("Select user from UserRewardsHistory user where user.userId = :userId AND LOWER(user.activityType) LIKE %:activityType%  AND user.createdOn >= :localStartdate ORDER BY user.createdOn desc")
	Page<UserRewardsHistory> filterUserRewardsHistoryWithStartDate(@Param("userId") Long userId, @Param("activityType") String  activityType,
			@Param("localStartdate")	LocalDate localStartdate, Pageable pageable);

	@Query("Select user from UserRewardsHistory user where user.userId = :userId AND LOWER(user.activityType) LIKE %:activityType%  AND user.createdOn <= :localEnddate ORDER BY user.createdOn desc")
	Page<UserRewardsHistory> filterUserRewardsHistoryWithEndDate(@Param("userId") Long userId, @Param("activityType") String  activityType,
			@Param("localEnddate")	LocalDate localEnddate, Pageable pageable);

}
