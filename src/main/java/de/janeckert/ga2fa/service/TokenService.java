package de.janeckert.ga2fa.service;

public interface TokenService {
	public String createToken(String seed, String username, Boolean active, String location);
	public String returnClaim(String token, String claimname);
	public Boolean validate (String jwt);
}
