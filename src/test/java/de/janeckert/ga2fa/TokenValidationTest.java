package de.janeckert.ga2fa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import de.janeckert.ga2fa.configuration.ApplicationConfiguration;
import de.janeckert.ga2fa.entities.Identity;
import de.janeckert.ga2fa.service.IdentityService;
import de.janeckert.ga2fa.service.JwtTokenService;

public class TokenValidationTest {
	private JwtTokenService service;
	
	private final String ISSUER = "GoogleAuth-2FA-App";
	private final String SUBJECT = "Will";
	
	@BeforeEach
	public void init() {
		ApplicationConfiguration cfg = new ApplicationConfiguration();
		cfg.setTokenLifetime(30);
		cfg.setDeviceRetentionTime(60);
		cfg.setMfaStrategy("off");
		
		IdentityService identityService = mock(IdentityService.class);
		Identity will = new Identity();
		will.setName("Will");
		when(identityService.retrieveIdentity(anyString())).thenReturn(will);
		
		this.service = new JwtTokenService(cfg, identityService);

	}

	@Test
	public void whenTokenExpiryValid_ThenItShouldPass() {
				
		Date expiry = Date.from(Instant.now().plus(30, ChronoUnit.SECONDS));
		
		String token = JWT.create()
				.withIssuer(ISSUER)
				.withExpiresAt(expiry)
				.withSubject(SUBJECT)
				.sign(Algorithm.none());
		
		Boolean result = this.service.validate(token);
		
		Assertions.assertThat(result).isTrue();
	}
	
	@Test
	public void whenTokenExpiryInValid_ThenItShouldNotPass() {
				
		Date expiry = Date.from(Instant.now().minus(30, ChronoUnit.SECONDS));
		
		String token = JWT.create()
				.withIssuer(ISSUER)
				.withExpiresAt(expiry)
				.withSubject(SUBJECT)
				.sign(Algorithm.none());
		
		Boolean result = this.service.validate(token);
		
		Assertions.assertThat(result).isFalse();
	}
	
	@Test
	public void testIssuerClaim() {
		Date expiry = Date.from(Instant.now().plus(30, ChronoUnit.SECONDS));
		
		String token = JWT.create()
				.withIssuer(ISSUER)
				.withExpiresAt(expiry)
				.withSubject(SUBJECT)
				.sign(Algorithm.none());
		
		assertThat(this.service.returnIssuer(token)).isEqualTo(ISSUER);
	}

	@Test
	public void testSubjectClaim() {
		Date expiry = Date.from(Instant.now().plus(30, ChronoUnit.SECONDS));
		
		String token = JWT.create()
				.withIssuer(ISSUER)
				.withExpiresAt(expiry)
				.withSubject(SUBJECT)
				.sign(Algorithm.none());
		
		assertThat(this.service.returnSubject(token)).isEqualTo(SUBJECT);
	}

	@Test
	public void testExpiryClaim() {
		Date expiry = Date.from(Instant.now().plus(30, ChronoUnit.SECONDS));
		
		String token = JWT.create()
				.withIssuer(ISSUER)
				.withExpiresAt(expiry)
				.withSubject(SUBJECT)
				.sign(Algorithm.none());
		
		assertThat(this.service.returnExpiry(token)).isNotBlank();
	}
	
	@Test
	public void testActiveClaim() {
		Date expiry = Date.from(Instant.now().plus(30, ChronoUnit.SECONDS));
		
		Boolean active = true;
		
		String token = JWT.create()
				.withIssuer(ISSUER)
				.withExpiresAt(expiry)
				.withClaim("active", active)
				.withSubject(SUBJECT)
				.sign(Algorithm.none());
		
		assertThat(this.service.returnActive(token)).isEqualTo("true");
	}
	
	public void testLocationClaim() {
		Date expiry = Date.from(Instant.now().plus(30, ChronoUnit.SECONDS));
		
		String token = JWT.create()
				.withIssuer(ISSUER)
				.withExpiresAt(expiry)
				.withClaim("location", "myLocation")
				.withSubject(SUBJECT)
				.sign(Algorithm.none());
		
		assertThat(this.service.returnExpiry(token)).isEqualTo("myLocation");
	}


}
