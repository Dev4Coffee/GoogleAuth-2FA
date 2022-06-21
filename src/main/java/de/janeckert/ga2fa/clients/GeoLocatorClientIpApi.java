package de.janeckert.ga2fa.clients;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import de.janeckert.ga2fa.geo.GeoCoordinate;
import lombok.extern.slf4j.Slf4j;

/**
 * Uses the ipapi API to query the geographic location of an IP.
 * 
 * @author User
 *
 */
@Service
@Slf4j
@Profile("!MockedGeoLocation")
public class GeoLocatorClientIpApi implements GeoLocatorClient {
	private static final String URL_TEMPLATE_FULL = "https://ipapi.co/%s/json";
	private static final String URL_TEMPLATE_LONGITUDE = "https://ipapi.co/%s/longitude";
	private static final String URL_TEMPLATE_LATITUDE = "https://ipapi.co/%s/latitude";

	@Override
	public @Nullable GeoCoordinate resolveGeoLocation(String ipAdress) {
		RestTemplate template = new RestTemplate();
		
		ResponseEntity<String> resultLongitude = null;
		ResponseEntity<String> resultLatitude = null;
		try {
			resultLongitude = template.getForEntity(new URI(String.format(URL_TEMPLATE_LONGITUDE, ipAdress)), String.class);
			resultLatitude = template.getForEntity(new URI(String.format(URL_TEMPLATE_LATITUDE, ipAdress)), String.class);
		} catch (RestClientException | URISyntaxException e) {
			e.printStackTrace();
		}
		
		if ((null == resultLatitude) || (null == resultLongitude)) {
			log.warn("Geocoordinates could not be resolved!");
			return null;
		}
		
		return GeoCoordinate.builder()
				.latitude(Double.valueOf(resultLatitude.getBody()))
				.longitude(Double.valueOf(resultLongitude.getBody()))				
				.build();
	}

}
