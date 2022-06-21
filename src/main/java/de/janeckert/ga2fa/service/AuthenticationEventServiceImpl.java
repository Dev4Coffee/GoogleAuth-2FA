package de.janeckert.ga2fa.service;

import org.springframework.stereotype.Service;

import de.janeckert.ga2fa.entities.Authentication;
import de.janeckert.ga2fa.repositories.AuthenticationRepository;

@Service
public class AuthenticationEventServiceImpl implements AuthenticationEventService {
	private AuthenticationRepository repository;
	
	

	public AuthenticationEventServiceImpl(AuthenticationRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public void saveAuthentication(Authentication auth) {
		this.repository.save(auth);
	}

}
