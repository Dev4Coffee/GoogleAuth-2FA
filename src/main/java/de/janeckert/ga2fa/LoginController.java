package de.janeckert.ga2fa;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Base64;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.janeckert.ga2fa.clients.GeoLocatorClient;
import de.janeckert.ga2fa.configuration.ApplicationConfiguration;
import de.janeckert.ga2fa.entities.Authentication;
import de.janeckert.ga2fa.entities.Identity;
import de.janeckert.ga2fa.entities.LoginCredentials;
import de.janeckert.ga2fa.geo.GeoCoordinate;
import de.janeckert.ga2fa.geo.GeoCoordinateUtils;
import de.janeckert.ga2fa.service.AuthenticationEventService;
import de.janeckert.ga2fa.service.IdentityService;
import de.janeckert.ga2fa.service.TokenService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LoginController {

	private IdentityService identityService;
	private AuthenticationEventService authenticationService;
	private TokenService tokenService;
	private GeoLocatorClient geoclient;
	private ApplicationConfiguration cfg;




	public LoginController(IdentityService identityService, AuthenticationEventService authenticationService,
			TokenService tokenService, GeoLocatorClient geoclient, ApplicationConfiguration cfg) {
		super();
		this.identityService = identityService;
		this.authenticationService = authenticationService;
		this.tokenService = tokenService;
		this.geoclient = geoclient;
		this.cfg = cfg;
	}

	@GetMapping("/")
	public String showLogin(Model m, HttpServletRequest request, HttpServletResponse response) {
		log.info("Reached root page.");

		Cookie[] cookies = request.getCookies();

		String authValue = GoogleAuth2FaApplication.getAuthorizationCookieValue(request);
		
		if (null == authValue) {
			log.info("No authentication information found --> login");
			return "auth";
		}
		if (authValue.isBlank()) {
			log.info("Empty authentication information found --> removal --> login");
			removeAuthCookie(response);
			return "auth";
		}
		
		log.info("User authenticated --> welcome");
		return "welcome";
	}

	@PostMapping("/login")
	public String login(@ModelAttribute(name="loginForm") LoginCredentials login, Model m, HttpServletRequest request, HttpServletResponse response) {
		Identity subject = this.identityService.retrieveIdentity(login.getUsername());

		String remoteIP = request.getRemoteAddr();

		String pass = login.getPassword();
		if( (subject != null) && pass.equals(subject.getPassword())) {
			m.addAttribute("uname", subject.getName());
			m.addAttribute("pass", subject.getPassword());
			// create a cookie and add it to the response
			String authToken = this.tokenService.createToken("secret", subject.getName());
			Cookie cookie = new Cookie("Authorization", authToken);
			response.addCookie(cookie);

			GeoCoordinate coord = this.geoclient.resolveGeoLocation(remoteIP);
			String device = Base64.getEncoder().encodeToString(request.getHeader("User-Agent").getBytes());
			// Save AuthN
			Authentication auth = Authentication.builder()
					.principal(subject)
					.latitude(coord == null ? 0 : coord.getLatitude())
					.longitude(coord == null ? 0 : coord.getLongitude())
					.device(device)
					.deviceExpiry(Instant.now().plusSeconds(cfg.getDeviceRetentionTime()))
					.timestamp(Instant.now())
					.build();

			this.authenticationService.saveAuthentication(auth);
			subject.setToken(authToken);
			this.identityService.saveIdentity(subject);

			return "welcome";
		}
		m.addAttribute("error", "Credentials Incorrect");
		return "auth";
	}
	
	
	@PostMapping("/logout")
	public String handleLogout(HttpServletRequest request, HttpServletResponse response) {
		
		// retrieve identity of user and remove the token
		String token = GoogleAuth2FaApplication.getAuthorizationCookieValue(request);
		String principalname = this.tokenService.returnClaim(token, "sub");
		Identity principal = this.identityService.retrieveIdentity(principalname);
		principal.setToken("");
		this.identityService.saveIdentity(principal);
		
		// remove the token cookie
		removeAuthCookie(response);
		return "auth";
		
	}
	
	
	private static void removeAuthCookie(HttpServletResponse response) {
		log.info("Authorization cookie is being removed.");
		Cookie cookieRemove = new Cookie("Authorization", null);
		cookieRemove.setMaxAge(0);
		response.addCookie(cookieRemove);
	}
}