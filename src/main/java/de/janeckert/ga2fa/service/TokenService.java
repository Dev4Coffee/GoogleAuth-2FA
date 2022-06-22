package de.janeckert.ga2fa.service;

public interface TokenService {
	public String createToken(String seed, String username);
	public String returnClaim(String token, String claimname);
}
