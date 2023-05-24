package com.eventManagement.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventManagement.model.Event;

@Service
public class EventFilter {

	private Logger logger = LoggerFactory.getLogger(EventFilter.class.getName());

	@Autowired
	EntityManager entityManager;

	public List<Event> filterEvents(Long adminId, String eventCategory, String eventType, Date eventDate,
			boolean isDashboard, int page, int size) {
		List<Event> eventList = null;
		logger.info("Filtering events. Admin ID: " + adminId);
		try {
			String query = "SELECT e FROM Event e where e.adminId = " + adminId;

			if (!eventType.equals("all")) {
				query = query + " AND e.eventType = :eventType";
			}
			if (!eventCategory.equals("all")) {
				query = query + " AND e.eventCategory = :eventCategory";
			}

			if (isDashboard) {
				query = query + " and e.startDate > :eventDate";
			}

			TypedQuery<Event> eventQuery = entityManager.createQuery(query, Event.class);
			if (!eventType.equals("all")) {
				eventQuery.setParameter("eventType", eventType);
			}
			if (!eventCategory.equals("all")) {
				eventQuery.setParameter("eventCategory", eventCategory);
			}
			if (isDashboard) {
				eventQuery.setParameter("eventDate", eventDate);
			}

			if (isDashboard) {
				eventQuery.setMaxResults(5);
			}

			eventList = eventQuery.getResultList();

		} catch (Exception ex) {
			logger.error("Exception caught while getting event List :" + ex.getMessage());
		}

		if (page == 0 && size == 0) {
			return eventList;
		}
		int startIndex = page * size;
		int endIndex = Math.min(startIndex + size, eventList.size());
		if (startIndex >= eventList.size()) {
			return Collections.emptyList();
		}
		logger.info("Events filtered successfully. Admin ID: " + adminId);
		return eventList.subList(startIndex, endIndex);
	}

	public List<Event> filterEventsCriteria(Long adminId, String eventCategory, String eventType, Date eventDate,
			boolean isDashboard, int page, int size) {
		logger.info("Filtering events using criteria. Admin ID: " + adminId);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Event> query = cb.createQuery(Event.class);
		Root<Event> root = query.from(Event.class);

		List<Predicate> predicates = new ArrayList<>();

		// Add condition for start time
		if (isDashboard) {
			predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), eventDate));
		}

		// Add conditions for optional filters if provided
		if (!eventCategory.equals("all")) {
			predicates.add(cb.like(root.get("eventCategory"), "%" + eventCategory + "%"));
		}

		if (!eventType.equals("all")) {
			predicates.add(cb.like(root.get("eventType"), "%" + eventType + "%"));
		}

		if (adminId != null) {
			predicates.add(cb.equal(root.get("adminId"), adminId));
		}

		Predicate finalPredicate = cb.and(predicates.toArray(new Predicate[0]));

		query.select(root).where(finalPredicate);

		// Add sorting
//	    if (isDashboard) {
//	        query.orderBy(
//	                cb.desc(root.get("startDate")),
//	                cb.asc(root.get("endDate"))
//	        );
//	    }

		TypedQuery<Event> typedQuery = entityManager.createQuery(query);

		// Add paging
		typedQuery.setFirstResult((page) * size);
		typedQuery.setMaxResults(size);
		logger.info("Events filtered successfully using criteria. Admin ID: " + adminId);
		return typedQuery.getResultList();
		
	}

}
