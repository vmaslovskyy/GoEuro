/*
    This file is part of GoEuroTest.

    Copyright 2014 Vjacheslav Maslovskij 
*/

package ua.digicode.geo;


import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class GeoPositionServiceTest {

	/**
	 * @throws Exception 
	 * 
	 */
	@Test
	public void testGetLocation() throws Exception{
		
		// Arrange
		String city = "Potsdam";
		
		// Act
		GeoPositionService service = new GeoPositionService();
		service.getDataLocationToFile(city);
		String filePath = "C:/";
		String outputFile = filePath + "GoEuroTest.csv";
		
		// Assert
		assertEquals(new File(outputFile).exists(), true);
	}
}
