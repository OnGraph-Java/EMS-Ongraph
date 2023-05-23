package com.eventManagement.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eventManagement.model.Reward;

@Repository
public interface RewardRepository extends PagingAndSortingRepository<Reward,Long> {

	@Query("Select reward from Reward reward where reward.adminId = :adminId")
	List<Reward> findByAdminId(@Param("adminId") Long adminId, Pageable pageable);
	

	@Query("Select reward from Reward reward where reward.adminId = :adminId")
	List<Reward> findByAdminId(@Param("adminId") Long adminId);

}
