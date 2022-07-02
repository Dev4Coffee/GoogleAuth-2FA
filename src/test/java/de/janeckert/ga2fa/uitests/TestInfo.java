package de.janeckert.ga2fa.uitests;

import de.janeckert.ga2fa.entities.Identity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestInfo {
	public final static String PATH_TO_EDGE_DRIVER = "D:\\Selenium\\msedgedriver.exe";
	
	public final static String INVALID_USERNAME = "Whatever";
	public final static String INVALID_PASSWORD = "Whatever";
	public final static String ERROR_INVALID_CREDENTIALS_UI = "Credentials Incorrect";
	public final static String SUCCESS_VALID_CREDENTIALS_UI = "You are authenticated!";
	public final static String VALID_USERNAME = "Will";
	public final static String VALID_PASSWORD = "Smith";
	public final static Identity IDENTITY_2FA_READY;

	public static final CharSequence INVALID_AUTH_CODE_UI = "Authenticator code invalid.";

	public static final CharSequence NOT_READY_FOR_MFA_UI = "2MFA needed but not set up for this account.";
	
	static {
		Identity jb = new Identity();
		jb.setActive(true);
		jb.setName("James");
		jb.setPassword("Bond");
		
		IDENTITY_2FA_READY = jb;
	}

}
