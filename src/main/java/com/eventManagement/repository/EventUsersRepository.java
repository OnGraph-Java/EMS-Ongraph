package com.eventManagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eventManagement.model.EventUsers;

@Repository
public interface EventUsersRepository extends PagingAndSortingRepository<EventUsers, Long> {

	@Query("SELECT e FROM  EventUsers e WHERE e.userId = :userId AND e.eventId = :eventId")
	EventUsers findByEventByUserId(@Param("userId") Long userId, @Param("eventId") Long eventId);

	@Query("SELECT e FROM  EventUsers e WHERE e.eventId = :eventId")
	List<EventUsers> findAllEventByEventId(@Param("eventId") Long eventId);

	@Query("SELECT e FROM  EventUsers e WHERE e.eventId = :eventId")
	Page<EventUsers> findAllEventByEventIdPage(Long eventId, PageRequest pageReq);
}
