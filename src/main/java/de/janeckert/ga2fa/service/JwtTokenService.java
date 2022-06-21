package de.janeckert.ga2fa.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.UUID;

import javax.swing.JWindow;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtTokenService implements TokenService {

	@Override
	public String createToken(String seed, String username) {
		log.warn("Skipping signing.");
		Algorithm algorithm = Algorithm.none();
		
		Date expiry = Date.from(Instant.now().plus(10, ChronoUnit.MINUTES));
		
		String token = JWT.create()
				.withIssuer("GoogleAuth-2FA-App")
				.withExpiresAt(expiry)
				.withSubject(username)
				.sign(algorithm);
		return token;
	}

}
