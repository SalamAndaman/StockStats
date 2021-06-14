package com.salam.apis;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salam.dataloader.RestClientMonthlyStock;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class KafkaClientControllerV1 {

	private final RestClientMonthlyStock stockMonthlyStats;

	@GetMapping("/monthlystats")
	public void monthlyStats() throws ClientProtocolException, IOException {
		stockMonthlyStats.loadMonthly();
		System.out.println("Data Loaded!");
	}
}