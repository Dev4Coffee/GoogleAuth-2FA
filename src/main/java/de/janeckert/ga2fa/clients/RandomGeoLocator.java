package de.janeckert.ga2fa.clients;

import java.util.Random;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import de.janeckert.ga2fa.geo.GeoCoordinate;
import lombok.extern.slf4j.Slf4j;

@Profile("MockedGeoLocation")
@Service
@Slf4j
public class RandomGeoLocator implements GeoLocatorClient {

	@Override
	public GeoCoordinate resolveGeoLocation(String ipAdress) {
		log.warn("Mocked Geolocation activated; providing random longitude/latitude.");
		Random r = new Random();
		Double lat = (r.nextDouble() - 0.5) * 90;
		Double lon = (r.nextDouble() - 0.5) * 180;
		return GeoCoordinate.builder().latitude(lat).longitude(lon).build();
	}

}
