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
import com.salam.repository.StockMetaDataRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestClientMonthlyStock {

	@Autowired
	private MonthlyDataRepository monthlyDataDao;

	@Autowired
	private StockMetaDataRepository stockMetaData;

	public void loadMonthly(String stockName, String sourceapi) throws ClientProtocolException, IOException {
		String[] arrOfStr = stockName.split("/", 2);
		stockName = arrOfStr[0];
		JSONObject jsonObj = extracted(arrOfStr[0], sourceapi);
		System.out.println("Stock1" + arrOfStr[0]);

		MetaData newMetaData = getStockMeta(jsonObj);
		if (newMetaData == null) {
			System.out.println("Not found - trying again");
			if (arrOfStr.length > 1) {
				jsonObj = extracted(arrOfStr[1], sourceapi);
				System.out.println("Stock2" + stockName);

				newMetaData = getStockMeta(jsonObj);
				if (newMetaData != null) {
					newMetaData.setSmdStockName(stockName);
				}
			}
		}

		String dataKeyname = getDataKey(sourceapi);

		boolean keyPresent = jsonObj.has(dataKeyname);

		if (keyPresent) {
			long recCount = loadDataToDB(jsonObj, newMetaData, dataKeyname);
			System.out.println("Record Count: " + recCount);
			if (recCount > 0L) {
				try {
					System.out.println("Inserting Meta Data"+newMetaData);
					stockMetaData.insert(newMetaData);
				} catch (RuntimeException e) {
//				System.out.println("Db exception:" + e.getMessage());
				}
			}

		} else {
			System.out.println("Key not present " + dataKeyname);
		}

	}

	private JSONObject extracted(String stockName, String sourceapi) throws IOException, ClientProtocolException {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("https://www.alphavantage.co/query?function=" + sourceapi + "&symbol=BSE:"
				+ stockName + "&apikey=WF4YO1WAOZY8BJD5");
		HttpResponse response = client.execute(request);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = " ";
		final StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null) {
//			jsonString.append(line);
			sb.append(line);
//			System.out.println(line);
		}

		JSONObject jsonObj = new JSONObject(sb.toString());
		return jsonObj;
	}

	private String getDataKey(String sourceapi) {
		// TODO Auto-generated method stub
		if (sourceapi.equalsIgnoreCase("TIME_SERIES_MONTHLY")) {
			return "Monthly Time Series";
		}
		if (sourceapi.equalsIgnoreCase("TIME_SERIES_WEEKLY")) {
			return "Weekly Time Series";
		}
		if (sourceapi.equalsIgnoreCase("TIME_SERIES_DAILY")) {
			return "Daily Time Series";
		}
		return null;
	}

	private long loadDataToDB(JSONObject jsonObj, MetaData newMetaData, String dataKeyname) {
		long recordsInserted = 0L;
		MonthlyData prevMonthly = null;
		if (jsonObj.get(dataKeyname) instanceof JSONObject) {
			System.out.println("Monthly Time in JSON Object");
			JSONObject dataObjList = (JSONObject) jsonObj.get(dataKeyname);
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
				MonthlyData currMonthly = extractDBrecord(newMetaData, monthEndDate, rowValue);
				currMonthly.setSmsStockSource(dataKeyname);
//				percentCalc.updateMonthYTD(prevMonthly,currMonthly);
				prevMonthly = loadToDB(currMonthly);
				if (prevMonthly != null) {
					recordsInserted = recordsInserted + 1L;
				}
			}

		}
		return recordsInserted;
	}

	private MonthlyData extractDBrecord(MetaData stockMetaData, String monthEndDate, Map<String, String> rowValue) {
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

		return newDataRow;
	}

	private MonthlyData loadToDB(MonthlyData newDataRow) {
		try {

			monthlyDataDao.insert(newDataRow);
			return newDataRow;
		} catch (RuntimeException e) {

//			System.out.println("Db exception:" + e.getMessage());
		}
		return null;
	}

	private MetaData getStockMeta(JSONObject jsonObj) {
		MetaData metaData = null;
		Iterator<?> keys = jsonObj.keys();
		String seriesName = null;
		while (keys.hasNext()) {
			String metaDataKey = (String) keys.next();
			if (metaDataKey.contains("Meta Data")) {
				metaData = new MetaData();
				JSONObject dataObjList = (JSONObject) jsonObj.get(metaDataKey);
				loopThruMetaKeys(dataObjList, metaData);
			}
			if (metaDataKey.contains("Series")) {
				seriesName = metaDataKey;				
			}
		}
		if (metaData != null && seriesName != null) {
			metaData.setSmdDataType(seriesName);
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
