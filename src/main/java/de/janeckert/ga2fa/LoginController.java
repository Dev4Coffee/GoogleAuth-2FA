package de.janeckert.ga2fa;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.beust.jcommander.Strings;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import de.janeckert.ga2fa.clients.GeoLocatorClient;
import de.janeckert.ga2fa.configuration.ApplicationConfiguration;
import de.janeckert.ga2fa.entities.Authentication;
import de.janeckert.ga2fa.entities.Identity;
import de.janeckert.ga2fa.entities.Login2faCredentials;
import de.janeckert.ga2fa.entities.LoginCredentials;
import de.janeckert.ga2fa.entities.RegisterForm;
import de.janeckert.ga2fa.geo.GeoCoordinate;
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
	private GoogleAuthenticator googleAuthenticator;




	public LoginController(IdentityService identityService, AuthenticationEventService authenticationService,
			TokenService tokenService, GeoLocatorClient geoclient, ApplicationConfiguration cfg,
			GoogleAuthenticator googleAuthenticator) {
		super();
		this.identityService = identityService;
		this.authenticationService = authenticationService;
		this.tokenService = tokenService;
		this.geoclient = geoclient;
		this.cfg = cfg;
		this.googleAuthenticator = googleAuthenticator;
	}

	@GetMapping("/")
	public String handleEntry(Model m, HttpServletRequest request, HttpServletResponse response) {
		log.info("Reached root page.");

		String authValue = GoogleAuth2FaApplication.getAuthorizationCookieValue(request);
		
		
		// Scenario "There is no authorization cookie"
		if (null == authValue) {
			log.info("No authentication information found --> login");
			return "auth";
		}
		
		// Scenario "There is an empty cookie" (weird edge scenario)
		if (authValue.isEmpty()) {
			log.info("Empty authentication information found --> removal --> login");
			removeAuthCookie(response);
			return "auth";
		}
		
		// Senario "There is an invalid token present" (it outdated ...)
		if (!this.tokenService.validate(authValue)) {
			log.info("Non-empty but invalid token found --> removal --> login");
			removeAuthCookie(response);
			return "auth";
		}
		
		log.info("User authenticated --> welcome");
		m.addAttribute("uname", this.tokenService.returnClaim(authValue, "sub"));
		m.addAttribute("token", authValue);
		m.addAttribute("active", this.tokenService.returnClaim(authValue, "active"));
		m.addAttribute("location", this.tokenService.returnClaim(authValue, "location"));
		
		return "welcome";
	}

	@PostMapping("/login")
	public String login(@ModelAttribute(name="loginForm") LoginCredentials login, Model m, HttpServletRequest request, HttpServletResponse response) {
		Identity subject = this.identityService.retrieveIdentity(login.getUsername());

		String pass = login.getPassword();
		if( (subject != null) && pass.equals(subject.getPassword())) {
			
			if (!subject.getActive()) {
				m.addAttribute("error", "Account known but not active (missing MFA configuration?).");
				return "auth";
			}
			
			GeoCoordinate coord = this.geoclient.resolveGeoLocation(request.getRemoteAddr());
			String device = Base64.getEncoder().encodeToString(request.getHeader("User-Agent").getBytes());
			// Save AuthN
			Double latitude = (coord == null) ? 0 : coord.getLatitude();
			Double longitude = coord == null ? 0 : coord.getLongitude();
			
			Authentication auth = Authentication.builder()
					.principal(subject)
					.latitude(latitude)
					.longitude(longitude)
					.device(device)
					.deviceExpiry(Instant.now().plusSeconds(cfg.getDeviceRetentionTime()))
					.timestamp(Instant.now())
					.build();

			this.authenticationService.saveAuthentication(auth);
			
			// create a cookie and add it to the response
						String authToken = null;
						
						if(!Strings.isStringEmpty(subject.getToken())) {
							if (this.tokenService.validate(subject.getToken())) {
								authToken = subject.getToken();
							}
							authToken = this.tokenService.createToken("secret", subject.getName(), subject.getActive(), "(" + latitude + " , " + longitude + ")");
						} else {
							authToken = this.tokenService.createToken("secret", subject.getName(), subject.getActive(), "(" + latitude + " , " + longitude + ")");
						}
						
						Cookie cookie = new Cookie("Authorization", authToken);
						response.addCookie(cookie);
			
			subject.setToken(authToken);
			this.identityService.saveIdentity(subject);


			m.addAttribute("uname", subject.getName());
			m.addAttribute("token", authToken);
			m.addAttribute("active", subject.getActive());
			m.addAttribute("location", "(" + latitude + " , " + longitude + ")");
			
			if (this.authenticationService.isMfaHappening(subject.getName(), device, latitude, longitude)) {
				return "authmfa";
			}
			
			return "welcome";
		}
		
		
		m.addAttribute("error", "Credentials Incorrect");
		return "auth";
	}
	
	@PostMapping("/login2fa")
	public String login2fa(@ModelAttribute(name="login2faForm") Login2faCredentials login, Model m, HttpServletRequest request, HttpServletResponse response) {
		
		log.info(String.format("Submitted credentials: totp: %s", login.getTotp()) );
		
		String token = GoogleAuth2FaApplication.getAuthorizationCookieValue(request);
		this.tokenService.validate(GoogleAuth2FaApplication.getAuthorizationCookieValue(request));
		
		String subject = this.tokenService.returnClaim(token, "sub");
		Boolean isAuthorized = this.googleAuthenticator.authorizeUser(subject, login.getTotp());
		
		if (isAuthorized) {
			m.addAttribute("uname", subject);
			m.addAttribute("token", token);
			m.addAttribute("active", this.tokenService.returnClaim(token, "active"));
			m.addAttribute("location", this.tokenService.returnClaim(token, "location"));
			return "welcome";
		}
		else {
			m.addAttribute("uname", subject);
			return "authmfa";
		}
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
	
	@PostMapping("/register")
	public String prepareRegister(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(name="registerForm") LoginCredentials login) {
		log.info("Reached first registration step to provide username and password.");
		return "register";
	}
	
	@PostMapping("/processregister")
	public String processRegister(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(name="registerForm") RegisterForm data, Model model) throws WriterException, IOException {
		log.info("Received username and password.");
		
		log.info(String.format("Registration data: Username: %s, Password: %s, Password confirm: %s", data.getUsername(), data.getPassword(), data.getPasswordConfirm()));
		
		if (!data.getPassword().equals(data.getPasswordConfirm())) {
			model.addAttribute("error", "Passwords do not match");
			return "register";
		}
		
		Identity subject = this.identityService.retrieveIdentity(data.getUsername());
		if (null != subject) {
			model.addAttribute("error", "Username already taken.");
			return "register";			
		}
		
		Identity newIdentity = new Identity();
		newIdentity.setName(data.getUsername());
		newIdentity.setPassword(data.getPassword());
		newIdentity.setActive(true);
		this.identityService.saveIdentity(newIdentity);
		
		GoogleAuthenticatorKey key = this.googleAuthenticator.createCredentials(data.getUsername());
		model.addAttribute("uname", data.getUsername());
		model.addAttribute("key", key.getKey());
		
		QRCodeWriter qrCodeWriter = new QRCodeWriter();

        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("GoogleAuth-2FA-App", data.getUsername(), key);

        BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);

        ServletOutputStream outputStream = response.getOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        outputStream.close();
        
		throw new RuntimeException("Pic is on its way!");
	}
	
	@PostMapping("/registermfa")
	public String handleRegisterMfa(HttpServletRequest request, HttpServletResponse response) {
		log.info("Reached second registration step to add TOTP support to account.");
		return "registermfa";
	}
	
	@PostMapping("/processmfa")
	public String processRegisterMfa(HttpServletRequest request, HttpServletResponse response) {
		log.info("Mfa information submitted.");
		return "redirect:/";
	}
	

	
	private static void removeAuthCookie(HttpServletResponse response) {
		log.info("Authorization cookie is being removed.");
		Cookie cookieRemove = new Cookie("Authorization", null);
		cookieRemove.setMaxAge(0);
		response.addCookie(cookieRemove);
	}
}