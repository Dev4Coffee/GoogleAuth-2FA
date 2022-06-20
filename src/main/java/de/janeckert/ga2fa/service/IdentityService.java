package de.janeckert.ga2fa.service;

import java.util.Collection;

import de.janeckert.ga2fa.entities.Identity;

public interface IdentityService {
	public Identity retrieveIdentity(String name);
	public Collection<Identity> retrieveAllIdentities();
}
