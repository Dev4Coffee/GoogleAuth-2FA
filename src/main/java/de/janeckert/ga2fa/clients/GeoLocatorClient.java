package de.janeckert.ga2fa.clients;

import de.janeckert.ga2fa.geo.GeoCoordinate;

/**
 * A Service that will query some API to translate an IP into a geographic location described as longitude and latitude.
 * 
 * @author User
 *
 */
public interface GeoLocatorClient {
	public GeoCoordinate resolveGeoLocation(String ipAdress);
}
