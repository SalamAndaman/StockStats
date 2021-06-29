package com.salam.dataloader.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Component;

import com.salam.entity.MonthlyData;

@Component
public class PercentCalculatorImpl implements PercentCalculator {
	MonthlyData BaseYearBegin;

	public PercentCalculatorImpl() {
		BaseYearBegin = null;
	}

	@Override
	public void updateMonthYTD(MonthlyData prevMonthly, MonthlyData currMonthly) {

		double changePerc = calculatePercentage(prevMonthly.getSmsClose(), currMonthly.getSmsClose());
		System.out.println(prevMonthly.getSmsStockName() + " Curr/" + currMonthly.getSmsMonthEndDate() + "-"
				+ currMonthly.getSmsClose() + " Prev/" + prevMonthly.getSmsMonthEndDate() + "-"
				+ prevMonthly.getSmsClose() + " Chg/" + changePerc);
	}

	private double calculatePercentage(BigDecimal oldPrice, BigDecimal newPrice) {
		BigDecimal changePrice = newPrice.subtract(oldPrice);
		BigDecimal perCent = changePrice.divide(oldPrice, 4, RoundingMode.HALF_UP);
		return perCent.multiply(new BigDecimal(100)).doubleValue();
	}

}
