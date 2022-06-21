package de.janeckert.ga2fa.servicetests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import de.janeckert.ga2fa.clients.GeoLocatorClient;
import de.janeckert.ga2fa.clients.GeoLocatorClientIpApi;
import de.janeckert.ga2fa.geo.GeoCoordinate;
import de.janeckert.ga2fa.geo.GeoCoordinateUtils;

public class GeoTest {
	private static final String HOME_IP = "2a02:a03f:616a:bb00:9909:129a:4953:7e3a";
	
	@Test
	public void testIpApi() {
		GeoLocatorClient client = new GeoLocatorClientIpApi();
		
		GeoCoordinate mine = client.resolveGeoLocation(HOME_IP);
		
		Assertions.assertThat(mine.getLatitude()).isEqualTo(50.8152);
		Assertions.assertThat(mine.getLongitude()).isEqualTo(4.4376);
	}
	
	@Test
	public void testDistanceCalculation() {
		GeoCoordinate kleve = GeoCoordinate.builder().longitude(51.832386).latitude(6.145384).build();
		GeoCoordinate aachen = GeoCoordinate.builder().longitude(50.757783).latitude(6.087254).build();
		
		Double distance = GeoCoordinateUtils.calculateDistance(kleve, aachen);
		
		Assertions.assertThat(distance).isBetween(100d, 150d);
	}

}
