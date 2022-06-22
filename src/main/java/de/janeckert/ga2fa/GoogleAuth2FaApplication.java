package de.janeckert.ga2fa;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GoogleAuth2FaApplication {

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
}
