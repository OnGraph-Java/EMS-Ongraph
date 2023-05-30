package com.eventManagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eventManagement.model.Gift;

@Repository
public interface GiftRepository extends JpaRepository<Gift,Long>{
 
	@Query("SELECT g FROM Gift g WHERE g.adminId = :adminId and lower(g.giftTitle) like %:title%")
	List<Gift> findAllGiftByAdminId(@Param("adminId") Long adminId, @Param("title") String title, Sort sort);

	@Query("SELECT g FROM Gift g WHERE g.adminId = :adminId and lower(giftTitle) like %:title% order by :sortBy desc")
	List<Gift> findAllGiftByAdminIdPage(@Param("adminId") Long adminId, @Param("sortBy") String sortBy, @Param("title") String title, PageRequest pageReq);

	@Query("SELECT g FROM Gift g WHERE g.adminId = :adminId and lower(giftTitle) like %:title% and g.createdOn BETWEEN :localStartDate and :localEndDate order by :sortBy desc")
	List<Gift> findAllGiftByAdminIdPageWithDates(@Param("adminId") Long adminId, @Param("sortBy") String sortBy, @Param("title") String title,@Param("localStartDate") LocalDate localStartDate,@Param("localEndDate") LocalDate localEndDate,  PageRequest pageReq);

	@Query("SELECT g FROM Gift g WHERE g.adminId = :adminId and lower(giftTitle) like %:title% and g.createdOn >= :localStartDate order by :sortBy desc")
	List<Gift> findAllGiftByAdminIdPageWithStartDate(@Param("adminId") Long adminId, @Param("sortBy") String sortBy, @Param("title") String title, @Param("localStartDate") LocalDate localStartDate,  PageRequest pageReq);

	@Query("SELECT g FROM Gift g WHERE g.adminId = :adminId and lower(giftTitle) like %:title% and g.createdOn <= :localEndDate order by :sortBy desc")
	List<Gift> findAllGiftByAdminIdPageWithEndDate(@Param("adminId") Long adminId, @Param("sortBy") String sortBy, @Param("title") String title, @Param("localEndDate") LocalDate localEndDate, PageRequest pageReq);

}
