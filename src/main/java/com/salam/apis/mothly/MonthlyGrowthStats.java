package com.salam.apis.mothly;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.salam.dataloader.helper.PercentCalculator;
import com.salam.entity.MonthlyData;
import com.salam.repository.MonthlyDataRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonthlyGrowthStats {

	@Autowired
	private MonthlyDataRepository monthlyDataRep;
	
	@Autowired
	private PercentCalculator percentCalc;

	public void getMonthlyGrowth(String stockName, String stockSource, int reps) {
		System.out.println("Stock is " + stockName);
		LocalDate today = LocalDate.now();
		Optional<MonthlyData> monthlyData = Optional
				.ofNullable(monthlyDataRep.getMonthlyData(today, stockSource, stockName).orElse(null));
		if (monthlyData.isPresent()) {
			System.out.println(monthlyData);
			for (int i = 0; i < reps; i++) {
				MonthlyData latestMonth = monthlyData.get();
				Period oneMonth = Period.of(0, 1, 0);
				LocalDate prevMonthDate = latestMonth.getSmsMonthEndDate().minus(oneMonth);
				
				monthlyData = Optional
						.ofNullable(monthlyDataRep.getMonthlyData(prevMonthDate, stockSource, stockName).orElse(null));
				if (monthlyData.isPresent()) {
					System.out.println(monthlyData);
					percentCalc.updateMonthYTD(monthlyData.get(), latestMonth);
				}
			}
		}

	}

}
