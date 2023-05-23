package com.eventManagement.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eventManagement.model.Event;

public interface EventRepository extends JpaRepository<Event, Long>{

	@Query("SELECT e FROM Event e WHERE LOWER(e.eventTitle) LIKE :eventTitle OR LOWER(e.location) LIKE :eventTitle")
	List<Event> findEventByTitle(@Param("eventTitle") String eventTitle);
	
	@Query("SELECT e FROM Event e WHERE e.adminId = :adminId and LOWER(e.eventCategory) LIKE %:eventCategory% AND LOWER(e.eventType) LIKE %:eventType%"
			+ " AND e.startDate >= :eventDate")
	List<Event> filterEventsDashboards(@Param("adminId") Long adminId, 
							           @Param("eventCategory") String eventCategory, 
							           @Param("eventType") String eventType, 
							           @Param("eventDate") Date eventDate,
							           Pageable page);
	
	@Query("SELECT e FROM Event e WHERE e.adminId = :adminId and LOWER(e.eventCategory) LIKE %:eventCategory% AND LOWER(e.eventType) LIKE %:eventType%")
	List<Event> filterEvents(@Param("adminId") Long adminId, 
            				 @Param("eventCategory") String eventCategory, 
            				 @Param("eventType") String eventType, 
							 Pageable page);
}
