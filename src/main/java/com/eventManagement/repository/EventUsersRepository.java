package com.eventManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eventManagement.model.EventUsers;

@Repository
public interface EventUsersRepository extends JpaRepository<EventUsers, Long> {

	@Query("SELECT e FROM  EventUsers e WHERE e.userId = :userId AND e.eventId = :eventId")
	EventUsers findByEventByUserId(@Param("userId") Long userId, @Param("eventId") Long eventId);

	@Query("SELECT e FROM  EventUsers e WHERE e.eventId = :eventId")
	List<EventUsers> findAllEventByEventId(@Param("eventId") Long eventId);
}
