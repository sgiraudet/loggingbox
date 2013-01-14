package com.loggingbox.storage.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.loggingbox.storage.model.SqlLog;

@Component
public class SqlLogDao {

	@Autowired
	SessionFactory sessionFactory;

	public void insertLog(SqlLog sqlLog) {
		sessionFactory.getCurrentSession().save(sqlLog);
	}

	@SuppressWarnings("unchecked")
	public List<SqlLog> getLogsPage(String applicationId, String beginLogId,
			int maxNumber, boolean ascendingOrder) {

		Criteria criteria = sessionFactory.getCurrentSession()
				.createCriteria(SqlLog.class)
				.add(Restrictions.eq("applicationId", applicationId));
		if (beginLogId != null) {
			if (ascendingOrder) {
				criteria = criteria.add(Restrictions.le("id", beginLogId));
			} else {
				criteria = criteria.add(Restrictions.ge("id", beginLogId));
			}
		}
		if (ascendingOrder) {
			criteria = criteria.addOrder(Order.desc("id"));
		} else {
			criteria = criteria.addOrder(Order.asc("id"));
		}
		return (List<SqlLog>) criteria.setMaxResults(maxNumber).list();

	}

	@SuppressWarnings("unchecked")
	public List<SqlLog> getLogs(String applicationId, Date fromDate,
			Date toDate, String beginLogId, Integer maxNumber) {

		Criteria criteria = sessionFactory.getCurrentSession()
				.createCriteria(SqlLog.class)
				.add(Restrictions.eq("applicationId", applicationId))
				.add(Restrictions.gt("date", fromDate))
				.add(Restrictions.lt("date", toDate));
		if (beginLogId != null) {
			criteria = criteria.add(Restrictions.le("id", beginLogId));
		}
		return (List<SqlLog>) criteria.addOrder(Order.desc("id"))
				.setMaxResults(maxNumber).list();

	}
}
