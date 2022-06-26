package de.janeckert.ga2fa.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.janeckert.ga2fa.configuration.ApplicationConfiguration;
import de.janeckert.ga2fa.entities.Identity;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtTokenService implements TokenService {
	private ApplicationConfiguration cfg;
	private IdentityService identityService;
	
	public JwtTokenService(ApplicationConfiguration cfg, IdentityService identityService) {
		super();
		this.cfg = cfg;
		this.identityService = identityService;
	}

	@Override
	public String createToken(String seed, String username, Boolean active, String location) {
		log.warn("Skipping signing.");
		Algorithm algorithm = Algorithm.none();
		
		Date expiry = Date.from(Instant.now().plus(cfg.getTokenLifetime(), ChronoUnit.SECONDS));
		
		String token = JWT.create()
				.withIssuer("GoogleAuth-2FA-App")
				.withExpiresAt(expiry)
				.withSubject(username)
				.withClaim("active", active)
				.withClaim("location", location)
				.sign(algorithm);
		return token;
	}

	@Override
	public String returnClaim(String token, String claimname) {
		DecodedJWT decodedJWT = JWT.decode(token);
		String result = decodedJWT.getClaim(claimname).asString();
		return result;
	}

	@Override
	public Boolean validate(String jwt) {
		log.info(String.format("Validating token: %s", jwt));
		DecodedJWT decodedJWT = JWT.decode(jwt);
		
		if (!decodedJWT.getClaim("iss").asString().equals("GoogleAuth-2FA-App")  ) {
			log.warn("Presented JWT token was not issued by us.");
			return false;
		}
		
		Date exp = decodedJWT.getExpiresAt();
		
		if (null == exp) {
			return false;
		}
		
		Instant expiry = exp.toInstant();
		if (Instant.now().isAfter(expiry)) {
			log.warn("Token expired.");
			return false;
		}
		
		Identity subject = this.identityService.retrieveIdentity(this.returnClaim(jwt, "sub"));
		if (!jwt.equals(subject.getToken())) {
			log.warn("Token does not match the authorized one.");
		}
		return true;
	}
}
