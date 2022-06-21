package de.janeckert.ga2fa;

import java.time.Instant;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.janeckert.ga2fa.clients.GeoLocatorClient;
import de.janeckert.ga2fa.entities.Authentication;
import de.janeckert.ga2fa.entities.Identity;
import de.janeckert.ga2fa.entities.LoginCredentials;
import de.janeckert.ga2fa.geo.GeoCoordinate;
import de.janeckert.ga2fa.geo.GeoCoordinateUtils;
import de.janeckert.ga2fa.service.AuthenticationEventService;
import de.janeckert.ga2fa.service.IdentityService;
import de.janeckert.ga2fa.service.TokenService;

@Controller
public class LoginController {
	
	private IdentityService identityService;
	private AuthenticationEventService authenticationService;
	private TokenService tokenService;
	private GeoLocatorClient geoclient;

	



	public LoginController(IdentityService identityService, AuthenticationEventService authenticationService,
			TokenService tokenService, GeoLocatorClient geoclient) {
		super();
		this.identityService = identityService;
		this.authenticationService = authenticationService;
		this.tokenService = tokenService;
		this.geoclient = geoclient;
	}

	@GetMapping("/login")
	public String showLogin() {
		return "login";
	}
	
	@PostMapping("/login")
	public String login(@ModelAttribute(name="loginForm") LoginCredentials login, Model m, HttpServletRequest request, HttpServletResponse response) {
		Identity subject = this.identityService.retrieveIdentity(login.getUsername());
		
		String remoteIP = request.getRemoteAddr();
		System.out.println(remoteIP);

		String pass = login.getPassword();
		if( (subject != null) && pass.equals(subject.getPassword())) {
			m.addAttribute("uname", subject.getName());
			m.addAttribute("pass", subject.getPassword());
			// create a cookie and add it to the response
		    Cookie cookie = new Cookie("authorization", this.tokenService.createToken("secret", subject.getName()));
		    response.addCookie(cookie);
		    
		    GeoCoordinate coord = this.geoclient.resolveGeoLocation(remoteIP);
		    // Save AuthN
		    Authentication auth = Authentication.builder()
		    		.principal(subject)
		    		.latitude(coord == null ? 0 : coord.getLatitude())
		    		.longitude(coord == null ? 0 : coord.getLongitude())
		    		.timestamp(Instant.now())
		    		.build();
		    
		    this.authenticationService.saveAuthentication(auth);
			
			return "welcome";
		}
		m.addAttribute("error", "Credentials Incorrect");
		return "index";

	}
}