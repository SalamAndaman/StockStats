package com.salam.repository;

import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.salam.entity.MonthlyData;
import com.salam.entity.QMonthlyData;

@Repository
public class MonthlyDataRepositoryImpl implements MonthlyDataRepository {

	public static final QMonthlyData qMonthlyData = QMonthlyData.monthlyData;

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

	@Override
	public Optional<MonthlyData> getMonthlyData(final LocalDate today, final String dataSource,
			final String stockname) {
		final Optional<MonthlyData> fetchedRecord = Optional
				.ofNullable(factory.selectFrom(qMonthlyData).where(getSearchPredicate(today, dataSource, stockname))
						.orderBy(qMonthlyData.smsMonthEndDate.desc()).fetchFirst());
		fetchedRecord.ifPresent(em::detach);
		return fetchedRecord;
	}

	private Predicate getSearchPredicate(final LocalDate today, final String dataSource, final String stockname) {
		return new BooleanBuilder(qMonthlyData.smsStockName.eq(stockname))
				.and(qMonthlyData.smsStockSource.eq(dataSource)).and(qMonthlyData.smsMonthEndDate.year()
						.eq(today.getYear()).and(qMonthlyData.smsMonthEndDate.month().eq(today.getMonthValue())));
	}

}
