package de.janeckert.ga2fa.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import de.janeckert.ga2fa.configuration.ApplicationConfiguration;
import de.janeckert.ga2fa.entities.Authentication;
import de.janeckert.ga2fa.geo.GeoCoordinate;
import de.janeckert.ga2fa.geo.GeoCoordinateUtils;
import de.janeckert.ga2fa.repositories.AuthenticationRepository;

@Service
public class AuthenticationEventServiceImpl implements AuthenticationEventService {
	private AuthenticationRepository repository;
	private ApplicationConfiguration cfg;

	public AuthenticationEventServiceImpl(AuthenticationRepository repository, ApplicationConfiguration cfg) {
		super();
		this.repository = repository;
		this.cfg = cfg;
	}

	@Override
	public void saveAuthentication(Authentication auth) {
		this.repository.save(auth);
	}

	/**
	 * returns null if the username has never logged in with the device, else last timestamp it happened
	 * 
	 * device needs to be BASE-encoded.
	 *
	 */
	@Override
	public Instant lastTimeUserUsedDevice(String username, String device) {
		Iterable<Authentication> auths = this.repository.findByDevice(device);
		
		List<Authentication> rest = StreamSupport.stream(auths.spliterator(), false)
				.filter(t -> username.equals(t.getPrincipal().getName()))
				.collect(Collectors.toList());
		if (rest.isEmpty()) {
			return null;
		}
		
		Instant last = Instant.MIN;
		for (Authentication authn : rest) {
			if (device.equals(authn.getDevice())) {
				Instant current = authn.getTimestamp();
				if (last.isBefore(current)) {
					last = current;
				}
			}
		}
		return last;
	}
	
	public Boolean isMfaHappening (String username, String device, Double latitude, Double longitude) {
		if ("on".equals(cfg.getMfaStrategy())) {
			return true;
		}
		if ("off".equals(cfg.getMfaStrategy())) {
			return false;
		}
		
		if ("adaptive".equals(cfg.getMfaStrategy())) {
			Instant lastTimeWithDevice = this.lastTimeUserUsedDevice(username, device);
			
			// new device
			if (null == lastTimeWithDevice) {
				return true;
			}
			
			// device is forgotten
			if (lastTimeWithDevice.plus(cfg.getDeviceRetentionTime(), ChronoUnit.SECONDS).isBefore(Instant.now())) {
				return true;
			}
			
			// Impossible travel
			Authentication lastAuth = this.retrieveLastAuthentication(username);
			GeoCoordinate nowLocation = GeoCoordinate.builder().latitude(latitude).longitude(longitude).build();
			GeoCoordinate lastLocation = GeoCoordinate.builder().latitude(lastAuth.getLatitude()).longitude(lastAuth.getLongitude()).build();
			Double distance = GeoCoordinateUtils.calculateDistance(nowLocation, lastLocation);
			
			Long timeDelta = lastAuth.getTimestamp().until(Instant.now(), ChronoUnit.HOURS);
			Double travelspeed = distance / timeDelta;
			
			if (travelspeed > this.cfg.getMaximumTravelSpeed()) {
				return true;
			}
		}
		
		// In case of doubt, use mfa
		return true;
	}

	@Override
	public Authentication retrieveLastAuthentication(String username) {
		Iterable<Authentication> all = this.repository.findAll();
		List<Authentication> auths = StreamSupport.stream(all.spliterator(), false)
				.filter(t -> username.equals(t.getPrincipal().getName()))
				.collect(Collectors.toList());
		
		if (auths.isEmpty()) {
			return null;
		}
		
		Authentication lastAuth = null;
		
		for (Authentication authn : auths) {
			if ((null == lastAuth) || (authn.getTimestamp().isAfter(lastAuth.getTimestamp()))) {
				lastAuth = authn;
			}
		}

		return lastAuth;
	}
}
