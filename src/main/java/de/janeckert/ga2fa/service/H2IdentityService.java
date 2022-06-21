package de.janeckert.ga2fa.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.janeckert.ga2fa.entities.Identity;
import de.janeckert.ga2fa.repositories.IdentityRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class H2IdentityService implements IdentityService {
	
	private IdentityRepository identityRepository;
	
	

	public H2IdentityService(IdentityRepository identityRepository) {
		super();
		this.identityRepository = identityRepository;
	}

	@Override
	public Identity retrieveIdentity(String name) {
		Optional<Identity> identity = this.identityRepository.findByName(name);
		
		if (identity.isPresent()) {
			log.info(String.format("Identity with name %s found in database.", name));
			return identity.get();
		}
		else {
			log.warn(String.format("Identity with name %s unknown.", name));
			return null;
		}
	}

	@Override
	public Collection<Identity> retrieveAllIdentities() {
		throw new RuntimeException("Not yet implemented!");
	}

}
