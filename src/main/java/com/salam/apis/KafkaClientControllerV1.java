package com.salam.apis;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salam.dataloader.RestClientMonthlyStock;
import com.salam.util.InputProperties;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class KafkaClientControllerV1 {

//	@Value("${arrayOfStrings}")
//	@Value("#{'${listOfStocks}'.split(',')}")

//	@Autowired
	private final RestClientMonthlyStock stockMonthlyStats;

	private final InputProperties stockPropertiesYml;

	@GetMapping("/monthlystats")
	public void monthlyStats() throws ClientProtocolException, IOException {
//		System.out.println("Hi Salam :" + stockPropertiesYml.getStockNames());

		for (String sourceapi : stockPropertiesYml.getStocksourceapi()) {
//			stockMonthlyStats.loadMonthly(stockName);
			for (String stockName : stockPropertiesYml.getStocknames()) {
				System.out.println("api name " + sourceapi +" stock " + stockName);
				stockMonthlyStats.loadMonthly(stockName, sourceapi);
			}
		}
		
		System.out.println("Data Loaded!");
	}
	
	@GetMapping("/monthlygrowth")
	public void monthlygrowth() throws ClientProtocolException, IOException {
//		System.out.println("Hi Salam :" + stockPropertiesYml.getStockNames());

		for (String sourceapi : stockPropertiesYml.getStocksourceapi()) {
//			stockMonthlyStats.loadMonthly(stockName);
			for (String stockName : stockPropertiesYml.getStocknames()) {
				System.out.println("Mothly Growth " + sourceapi +" stock " + stockName);
//				stockMonthlyStats.loadMonthly(stockName, sourceapi);
			}
		}
		
		System.out.println("Data Loaded!");
	}
}