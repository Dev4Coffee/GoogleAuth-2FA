package de.janeckert.ga2fa;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import de.janeckert.ga2fa.repositories.GoogleAuthRepository;
import validation.ApplicationConfigurationValidator;

@SpringBootApplication
public class GoogleAuth2FaApplication {
	private GoogleAuthRepository repo;
	
	

	public GoogleAuth2FaApplication(GoogleAuthRepository repo) {
		super();
		this.repo = repo;
	}


	public static void main(String[] args) {
		SpringApplication.run(GoogleAuth2FaApplication.class, args);
	}

	
	public static String getAuthorizationCookieValue (HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("Authorization")) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
	
	public static boolean isNumeric(String string) {
	    int intValue;
			
	    System.out.println(String.format("Parsing string: \"%s\"", string));
			
	    if(string == null || string.equals("")) {
	        System.out.println("String cannot be parsed, it is null or empty.");
	        return false;
	    }
	    
	    try {
	        intValue = Integer.parseInt(string);
	        return true;
	    } catch (NumberFormatException e) {
	        System.out.println("Input String cannot be parsed to Integer.");
	    }
	    return false;
	}
	
	
	@Bean
	public GoogleAuthenticator createGoogleAuthenticator() {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        googleAuthenticator.setCredentialRepository(this.repo);
        return googleAuthenticator;
	}
	
	@Bean
	public static ApplicationConfigurationValidator configurationPropertiesValidator() {
		return new ApplicationConfigurationValidator();
	}
}
