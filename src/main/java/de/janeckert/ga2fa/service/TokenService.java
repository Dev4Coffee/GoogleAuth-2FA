package de.janeckert.ga2fa.service;

public interface TokenService {
	public String createToken(String seed, String username, Boolean active, String location);
	public String returnSubject(String token);
	public String returnExpiry(String token);
	public String returnIssuer(String token);
	public String returnActive(String token);
	public String returnLocation(String token);
	public Boolean validate (String jwt);
}
