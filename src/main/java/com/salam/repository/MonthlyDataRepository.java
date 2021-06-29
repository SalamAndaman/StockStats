package com.salam.repository;

import java.time.LocalDate;
import java.util.Optional;

import com.salam.entity.MonthlyData;

public interface MonthlyDataRepository
{

   void insert(MonthlyData monthlyData);

	Optional<MonthlyData> getMonthlyData(LocalDate today, String dataSource, String stockname);

}
