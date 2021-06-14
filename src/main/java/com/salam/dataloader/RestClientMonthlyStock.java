package com.salam.dataloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.salam.entity.MetaData;
import com.salam.entity.MonthlyData;
import com.salam.repository.MonthlyDataRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestClientMonthlyStock {

	@Autowired
	private MonthlyDataRepository monthlyDataDao;

	public void loadMonthly() throws ClientProtocolException, IOException
//				 throws ClientProtocolException, IOException
	{
		System.out.println("Hi Salam");

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(
				"https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol=BSE:RELIANCE&apikey=WF4YO1WAOZY8BJD5");
		HttpResponse response = client.execute(request);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = " ";
		final StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null) {
//			jsonString.append(line);
			sb.append(line);
//			System.out.println(line);
		}
		System.out.println("Salam" + sb.toString().length());
		System.out.println("the end");

		JSONObject jsonObj = new JSONObject(sb.toString());

		MetaData stockMetaData = getStockMeta(jsonObj);
		System.out.println(stockMetaData);

		boolean keyPresent = jsonObj.has("Monthly Time Series");

		if (keyPresent) {
			if (jsonObj.get("Monthly Time Series") instanceof JSONObject) {
				System.out.println("Monthly Time in JSON Object");
				JSONObject dataObjList = (JSONObject) jsonObj.get("Monthly Time Series");
				Iterator<?> keys = dataObjList.keys();
				while (keys.hasNext()) {
					String monthEndDate = (String) keys.next();
					JSONObject jsonMonthlyObj = (JSONObject) dataObjList.get(monthEndDate);
					Iterator<?> monthlyKeys = jsonMonthlyObj.keys();
					Map<String, String> rowValue = new HashMap<>();
					while (monthlyKeys.hasNext()) {
						String keyDetail = (String) monthlyKeys.next();
						String keyValue = (String) jsonMonthlyObj.get(keyDetail);
						rowValue.put(keyDetail, keyValue);
					}
					extractDBrecord(stockMetaData, monthEndDate, rowValue);
				}

			}
		} else {
			System.out.println("Key not present");
		}

	}

	private void extractDBrecord(MetaData stockMetaData, String monthEndDate, Map<String, String> rowValue) {
		MonthlyData newDataRow = new MonthlyData();
		newDataRow.setSmsMonthEndDate(LocalDate.parse(monthEndDate));
		newDataRow.setSmsStockName(stockMetaData.getSmdStockName());
		for (final Map.Entry<String, String> mapEntry : rowValue.entrySet()) {
			if (mapEntry.getKey().contains("open")) {
				newDataRow.setSmsOpen(new BigDecimal(mapEntry.getValue()));
			}
			if (mapEntry.getKey().contains("low")) {
				newDataRow.setSmsLow(new BigDecimal(mapEntry.getValue()));
			}
			if (mapEntry.getKey().contains("high")) {
				newDataRow.setSmsHigh(new BigDecimal(mapEntry.getValue()));
			}
			if (mapEntry.getKey().contains("close")) {
				newDataRow.setSmsClose(new BigDecimal(mapEntry.getValue()));
			}
			if (mapEntry.getKey().contains("volume")) {
				newDataRow.setSmsVolume(Long.parseLong(mapEntry.getValue()));
			}
		}
//		System.out.println(newDataRow);
		monthlyDataDao.insert(newDataRow);
	}

	private MetaData getStockMeta(JSONObject jsonObj) {
		MetaData metaData = new MetaData();
		Iterator<?> keys = jsonObj.keys();
		while (keys.hasNext()) {
			String metaDataKey = (String) keys.next();
			if (metaDataKey.contains("Meta Data")) {
				JSONObject dataObjList = (JSONObject) jsonObj.get(metaDataKey);
				loopThruMetaKeys(dataObjList, metaData);
			} else {
				metaData.setSmdDataType(metaDataKey);
			}
		}

		return metaData;
	}

	private void loopThruMetaKeys(JSONObject dataObjList, MetaData metaData) {
		Iterator<?> keys = dataObjList.keys();
		while (keys.hasNext()) {
			String metaDataKey = (String) keys.next();
			if (metaDataKey.contains("Symbol")) {
				String stockName = (String) dataObjList.get(metaDataKey);
				metaData.setSmdStockName(stockName.substring(stockName.lastIndexOf(":") + 1));
			}

			if (metaDataKey.contains("Last Refreshed")) {
				metaData.setSmdLastUpdated(LocalDate.parse((String) dataObjList.get(metaDataKey)));
			}
		}
	}
}
