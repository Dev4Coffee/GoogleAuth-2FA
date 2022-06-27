package de.janeckert.ga2fa.service;

import java.time.Instant;

import de.janeckert.ga2fa.entities.Authentication;

public interface AuthenticationEventService {
	public void saveAuthentication(Authentication auth);
	public Instant lastTimeUserUsedDevice(String username, String device);
	public Boolean isMfaHappening (String username, String device, Double latitude, Double longitude);
	public Authentication retrieveLastAuthentication (String username);
}
