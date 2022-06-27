package de.janeckert.ga2fa.configuration;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
@Validated
public class ApplicationConfiguration {
	
	/**
	 *  Lifetime in seconds of access tokens issued on a successful login.
	 */
	@NotNull
	private Integer tokenLifetime;
	
	
	/**
	 *   Determines the rough strategy for MFA used.
	 *   - off: No MFA used
	 *   - on: MFA always used
	 *   - adaptive: MFA only used in cases "forgotten/unknown device" and "impossible travel distance"
	 */
	@NotEmpty
	private String mfaStrategy;
	
	
	/**
	 *   number of seconds a device on which a user successfully logged in, will be remembered.
	 */
	@NotNull
	private Integer deviceRetentionTime;
	
	/**
	 *   Maximum travel speed deemed possible in km/h
	 */
	@NotNull
	private Integer maximumTravelSpeed;
}
