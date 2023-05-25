package com.eventManagement.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventManagement.dto.RewardPoints;
import com.eventManagement.model.UserRewardsHistory;
import com.eventManagement.model.PageEntity;

@Service
public class RewardFilter {

	Logger logger = LoggerFactory.getLogger(RewardFilter.class);

	DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	@Autowired
	EntityManager entityManager;

	public List<RewardPoints> getRewardsPoints(Long adminId) {
		logger.info("Getting reward points. Admin ID: " + adminId);
		List<RewardPoints> rewardPointsList = new ArrayList<>();

		String query = "SELECT  Count(*) AS 'allUserCount', "
				+ "  SUM(CASE WHEN ur.user_role = '0' THEN 1 ELSE 0 END) AS 'allUniversityStudents', "
				+ "  SUM(CASE WHEN ur.user_role = '1' THEN 1 ELSE 0 END) AS 'allScholorshipStudents', "
				+ "  SUM(CASE WHEN ur.user_role = '2' THEN 1 ELSE 0 END) AS 'allEmployees'"
				+ "  FROM user_rewards ur WHERE ur.admin_id = :adminId";

		try {
			rewardPointsList = entityManager.createNativeQuery(query, "RewardPointsMapping")
					.setParameter("adminId", adminId).getResultList();

		} catch (Exception ex) {
			logger.error("Exception while getting statList: " + ex.getMessage());
		}
		logger.info("Retrieved reward points successfully. Admin ID: " + adminId);
		return rewardPointsList;

	}

	public PageEntity<UserRewardsHistory> getUserRewardsHistory(Long userId, String activityType, String fromDate,
			String endDate, int page, int size) {
		logger.info("Fetching user rewards history. User ID: " + userId);
		List<UserRewardsHistory> userRewardList = new ArrayList<>();
		LocalDate localStartdate = null;
		LocalDate localEnddate = null;

		String query = "Select user from UserRewardsHistory user where user.userId = :userId";

		if (activityType != null && !activityType.equals("all")) {
			query = query + " AND LOWER(user.activityType) like :activityType";
		}
		if (fromDate != null && !fromDate.equals("all")) {
			localStartdate = LocalDate.parse(fromDate, df);
			query = query + " AND user.createdOn >= :localStartdate";
		}
		if (endDate != null && !endDate.equals("all")) {
			localEnddate = LocalDate.parse(endDate, df);
			query = query + " AND user.createdOn <= :localEnddate";
		}

		TypedQuery<UserRewardsHistory> rewardQuery = entityManager.createQuery(query, UserRewardsHistory.class);

		rewardQuery.setParameter("userId", userId);

		if (activityType != null && !activityType.equals("all")) {
			activityType = activityType.toLowerCase();
			activityType = "%" + activityType + "%";
			rewardQuery.setParameter("activityType", activityType);
		}
		if (fromDate != null && !fromDate.equals("all")) {
			rewardQuery.setParameter("localStartdate", localStartdate);
		}
		if (endDate != null && !endDate.equals("all")) {
			rewardQuery.setParameter("localEnddate", localEnddate);
		}

		try {
			if(page  <= 0) {
				page = 1;
			}
			int offset = (page - 1) * size;
			rewardQuery.setFirstResult(offset);
			rewardQuery.setMaxResults(size);
			userRewardList = rewardQuery.getResultList();

			CriteriaQuery<Long> countQuery = entityManager.getCriteriaBuilder().createQuery(Long.class);
			countQuery.select(entityManager.getCriteriaBuilder().count(countQuery.from(UserRewardsHistory.class)));
			long totalElements = entityManager.createQuery(countQuery).getSingleResult();

			PageEntity<UserRewardsHistory> pageObj = new PageEntity<UserRewardsHistory>(userRewardList, page, size,
					totalElements);
			return pageObj;
		} catch (Exception ex) {
			logger.error("Exception while getting statList: " + ex.getMessage());
		}
		logger.info("User rewards history fetched successfully. User ID: " + userId);
		return null;
	}

}
