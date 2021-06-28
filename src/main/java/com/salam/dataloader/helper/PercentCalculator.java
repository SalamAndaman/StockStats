package com.salam.dataloader.helper;

import com.salam.entity.MonthlyData;

public interface PercentCalculator {

	public void updateMonthYTD(MonthlyData prevMonthly, MonthlyData currMonthly);

}
