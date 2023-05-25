package com.eventManagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eventManagement.model.UserRewards;

@Repository
public interface UserRewardsRepository extends PagingAndSortingRepository<UserRewards, Long> {

	@Query("Select user from UserRewards user where user.adminId = :adminId AND LOWER(user.username) LIKE %:username%")
	Page<UserRewards> findByAdminId(@Param("adminId") Long adminId, Pageable page, @Param("username") String username);

	@Query("Select user from UserRewards user where user.adminId = :adminId AND user.rewardPoints <= :rewardRange AND LOWER(user.username) LIKE %:username%")
	Page<UserRewards> findByAdminIdAndReward(@Param("adminId") Long adminId, @Param("rewardRange") Long rewardRange, Pageable page, String username);

	@Query("Select user from UserRewards user where user.adminId = :adminId AND LOWER(user.username) LIKE :username")
	List<UserRewards> findByAdminIdAndUserName(@Param("adminId") Long adminId, @Param("username") String username);

}
