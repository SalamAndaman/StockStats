package com.salam.repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.salam.entity.MonthlyData;

@Repository
public class MonthlyDataRepositoryImpl implements MonthlyDataRepository {

//	public static final QMonthlyData qMonthlyData = QMonthlyData.monthlyData;

	@Value("${spring.jpa.database-timeout}")
	private int timeout;

	private EntityManager em;
	private JPAQueryFactory factory;

	@Autowired
	public MonthlyDataRepositoryImpl(EntityManager em) {
		this.em = em;
		factory = new JPAQueryFactory(em);
	}

	@Override
	@Transactional
	public void insert(MonthlyData monthlyData) {

		em.persist(monthlyData);
		em.flush();
		em.clear();
	}

}
