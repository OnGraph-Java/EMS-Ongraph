package com.eventManagement.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eventManagement.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

	@Query("SELECT e FROM Event e WHERE LOWER(e.eventTitle) LIKE :eventTitle OR LOWER(e.location) LIKE :eventTitle")
	List<Event> findEventByTitle(@Param("eventTitle") String eventTitle);

	@Query("SELECT e FROM Event e WHERE ( LOWER(e.eventTitle) LIKE %:eventTitle% OR LOWER(e.location) LIKE %:eventTitle%) AND e.adminId = :adminId and LOWER(e.eventCategory) LIKE %:eventCategory% AND LOWER(e.eventType) LIKE %:eventType%"
			+ " AND e.startDate >= :eventDate ORDER BY e.startDate asc")
	List<Event> findFirst5Event(@Param("adminId") Long adminId, @Param("eventCategory") String eventCategory,
			@Param("eventType") String eventType, @Param("eventDate") LocalDateTime eventDate,
			@Param("eventTitle") String eventTitle, Pageable pageable);

	@Query("SELECT e FROM Event e WHERE ( LOWER(e.eventTitle) LIKE %:eventTitle% OR LOWER(e.location) LIKE %:eventTitle%) AND e.adminId = :adminId and LOWER(e.eventCategory) LIKE %:eventCategory% AND LOWER(e.eventType) LIKE %:eventType% ORDER BY e.startDate desc")
	List<Event> filterEvents(@Param("adminId") Long adminId, @Param("eventCategory") String eventCategory,
			@Param("eventType") String eventType, @Param("eventTitle") String eventTitle);

	@Query("SELECT e FROM Event e WHERE ( LOWER(e.eventTitle) LIKE %:eventTitle% OR LOWER(e.location) LIKE %:eventTitle%) AND e.adminId = :adminId and LOWER(e.eventCategory) LIKE %:eventCategory% AND LOWER(e.eventType) LIKE %:eventType% AND e.startDate >= :eventDate ORDER BY e.startDate desc")
	List<Event> filterEventsWithDate(@Param("adminId") Long adminId, @Param("eventCategory") String eventCategory,
			@Param("eventType") String eventType, @Param("eventTitle") String eventTitle, LocalDateTime eventDate);

}
