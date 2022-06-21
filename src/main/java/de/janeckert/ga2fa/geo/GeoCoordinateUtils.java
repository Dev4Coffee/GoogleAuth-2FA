package de.janeckert.ga2fa.geo;

import org.geotools.referencing.GeodeticCalculator;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GeoCoordinateUtils {

	/**
	 * Calculated the distance in kilometers between the two provided locations
	 * 
	 * @param location1 point on earth's surface
	 * @param location2 point on earth's surface
	 * @return distance between location1 and location2 in km
	 */
	public static Double calculateDistance(GeoCoordinate location1, GeoCoordinate location2) {
		double distanceInMeters = 0;
		GeodeticCalculator calculator = new GeodeticCalculator();
		calculator.setStartingGeographicPoint(location1.getLongitude(), location1.getLatitude());
		calculator.setDestinationGeographicPoint(location2.getLongitude(), location2.getLatitude());
		
		distanceInMeters = calculator.getOrthodromicDistance();
		return distanceInMeters / 1000;
	}
}
