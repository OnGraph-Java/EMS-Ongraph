package com.eventManagement.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventManagement.dto.Statistics;

@Service
public class StatisticsService {

	Logger logger = LoggerFactory.getLogger(StatisticsService.class);

	@Autowired
	EntityManager entityManager;

	public List<Statistics> getDashboardStatistics(Long adminId) {

		List<Statistics> statisticsList = new ArrayList<>();
		
		String query = "SELECT e.event_id, e.event_title, "
				    + "     SUM(CASE WHEN eu.user_type = '301' THEN 1 ELSE 0 END) AS 'universityStudent',"
				    + "     SUM(CASE WHEN eu.user_type = '302' THEN 1 ELSE 0 END) AS 'employee',"
				    + "     SUM(CASE WHEN eu.user_type = '303' THEN 1 ELSE 0 END) AS 'scholarShipStudents',"
				    + "     SUM(CASE WHEN eu.user_type = '304' THEN 1 ELSE 0 END) AS 'publicUsers' "
				    + "  FROM event_users eu "
				    + "  JOIN event e ON eu.event_id = e.event_id"
				    + "  WHERE eu.admin_id = :adminId "
				    + "  GROUP BY e.event_id, e.event_title";
		try {
			statisticsList = entityManager.createNativeQuery(query, "StatisticsMapping").setParameter("adminId", adminId)
					.getResultList();

		} catch (Exception ex) {
			logger.error("Exception while getting statList: " + ex.getMessage());
		}

		return statisticsList;
	}

}
