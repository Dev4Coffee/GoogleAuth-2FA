package de.janeckert.ga2fa.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import de.janeckert.ga2fa.configuration.ApplicationConfiguration;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtTokenService implements TokenService {
	private ApplicationConfiguration cfg;
	
	

	public JwtTokenService(ApplicationConfiguration cfg) {
		super();
		this.cfg = cfg;
	}



	@Override
	public String createToken(String seed, String username) {
		log.warn("Skipping signing.");
		Algorithm algorithm = Algorithm.none();
		
		Date expiry = Date.from(Instant.now().plus(cfg.getTokenLifetime(), ChronoUnit.SECONDS));
		
		String token = JWT.create()
				.withIssuer("GoogleAuth-2FA-App")
				.withExpiresAt(expiry)
				.withSubject(username)
				.sign(algorithm);
		return token;
	}



	@Override
	public String returnClaim(String token, String claimname) {
		DecodedJWT decodedJWT = JWT.decode(token);
		return decodedJWT.getClaim(claimname).asString();
	}

	
}
