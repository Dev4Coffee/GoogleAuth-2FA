package de.janeckert.ga2fa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.warrenstrange.googleauth.ICredentialRepository;

import de.janeckert.ga2fa.entities.Identity;

@Repository
public class GoogleAuthRepository implements ICredentialRepository {
	private IdentityRepository identityrepository;
	
	public GoogleAuthRepository(IdentityRepository identityrepository) {
		super();
		this.identityrepository = identityrepository;
	}

	@Override
	public String getSecretKey(String userName) {
		Optional<Identity> subject = this.identityrepository.findByName(userName);
		
		if (subject.isEmpty()) {
			throw new RuntimeException("Username unknown: " + userName);
		}
		
		return subject.get().getGoogleAuthSecret();
	}

	@Override
	public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
		Optional<Identity> subjectOptional = this.identityrepository.findByName(userName);
		
		if (subjectOptional.isEmpty()) {
			throw new RuntimeException("Username unknown: " + userName);
		}
		
		Identity subject = subjectOptional.get();
		subject.setGoogleAuthSecret(secretKey);
		
		this.identityrepository.save(subject);
	}

}
