package com.eventManagement.repository;

import com.eventManagement.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

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


    @Query("SELECT e FROM Event e WHERE e.eventId = :eventId")
    Event findByIdAndFlag(@Param("eventId") Long eventId);

    @Modifying
    @Query("UPDATE Event e SET e.isActive = :status WHERE e.eventId = :eventId")
    void updateEventStatus(@Param("eventId") Long eventId, @Param("status") boolean status);

    @Modifying
    @Query("DELETE FROM Event e WHERE e.eventId = :eventId AND e.isActive = false")
    void deleteEvent(@Param("eventId") Long eventId);
}
