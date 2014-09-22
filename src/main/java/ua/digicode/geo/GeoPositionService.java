/*
    This file is part of GoEuroTest.

    Copyright 2014 Vjacheslav Maslovskij 
*/

package ua.digicode.geo;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.csvreader.CsvWriter;


public class GeoPositionService {
	
	/**
	 * Columns of CSV file
	 */
	List<String> columnsOfCSV = Arrays.asList("_id", "name", "type", "longitude");
	
	
	/**
	 * Getting data from site and write to CSV file
	 * @param city
	 * @throws Exception
	 */
	public void getDataLocationToFile(String city) throws Exception{
		JSONArray geoPositions = getLocation(city);
		if(geoPositions.length() == 0){
			System.out.println("No result data from site for value " + city + ". CSV file will not be be created.");
		}
		else{
			List<HashMap<String, String>> allGeoPositionList = getDataFromJSONToList(geoPositions);
			writeDataToCSV(allGeoPositionList);
		}
	}
	
	/**
	 * Receive a response from the site
	 * @param city
	 * @return
	 * @throws Exception
	 */
	private JSONArray getLocation(String city) throws Exception{
		
		try {
			String stringURL = "http://api.goeuro.com/api/v2/position/suggest/en/" + city;
			URL url = new URL(stringURL);
			
			// make connection, use get mode
			HttpURLConnection connection = null;
//			boolean isProxyUsed = true;
//			if(isProxyUsed){
//				String stringProxy = "proxy.firma.net";
//				Integer intPort = 3128;
//				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(stringProxy, intPort));
//				connection= (HttpURLConnection) url.openConnection(proxy);
//			}
//			else
				connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);

			// Call the service
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			// Extract response
			String str;
			StringBuilder sb = new StringBuilder();
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
			br.close();
			connection.disconnect();
			String JSONStringToObject = sb.toString();
			JSONArray response = new JSONArray(JSONStringToObject);
			
			return response;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Obtain the necessary data to file
	 * @param singleGeoPosition
	 * @return
	 * @throws JSONException
	 */
	private HashMap<String, String> getKeysOfJSONObject(JSONObject singleGeoPosition) throws JSONException{
		
		HashMap<String, String> keysOfGeoPosition= new HashMap<String, String>();
		Iterator<?> keys = singleGeoPosition.keys();
		
        while(keys.hasNext()){
            String key = (String)keys.next();
            if(singleGeoPosition.get(key) instanceof JSONObject){
            	HashMap<String, String> innerKeysOfGeoPosition = getKeysOfJSONObject((JSONObject)singleGeoPosition.get(key));
            	keysOfGeoPosition.putAll(innerKeysOfGeoPosition);
            }
            else if (columnsOfCSV.contains(key)) {
				keysOfGeoPosition.put(key, singleGeoPosition.getString(key));
			}
        }
		return keysOfGeoPosition;
	}
	
	/**
	 * Write data to a file
	 * @param geoPosition
	 * @throws JSONException
	 */
	private List<HashMap<String, String>> getDataFromJSONToList(JSONArray geoPositions) throws JSONException{
		
		List<HashMap<String, String>> allGeoPositionList= new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < geoPositions.length(); i++) {
			JSONObject singleGeoPosition = geoPositions.getJSONObject(i);
			allGeoPositionList.add(getKeysOfJSONObject(singleGeoPosition));
		}
		return allGeoPositionList;
	}
	
	
	
	/**
	 * 
	 * @param allGeoPositionList
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private void writeDataToCSV(List<HashMap<String, String>> allGeoPositionList) throws IOException, InterruptedException{
		
		String filePath = "C:/";
		String outputFile = filePath + "GoEuroTest.csv";
		
		try {
			// use FileWriter constructor that specifies open for appending
			FileWriter fileWriter = new FileWriter(outputFile, false);
			CsvWriter csvOutput = new CsvWriter(fileWriter, ',');
			
			// write the header
			for(String columnHeader : columnsOfCSV) {
				csvOutput.write(columnHeader);
			}	
			csvOutput.endRecord();
			
			// write out records
			for(HashMap<String, String> keysOfSingleGeoPosition : allGeoPositionList){
				for(String columnName : columnsOfCSV){
					csvOutput.write(keysOfSingleGeoPosition.get(columnName));
				}
				csvOutput.endRecord();
			}
				csvOutput.close();
				
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
		
	}
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		if(args.length == 0){
			System.out.println("Please, enter valid value!");
		}
	else{
			GeoPositionService service = new GeoPositionService();
			service.getDataLocationToFile(args[0]);
		}
		
	}
}
