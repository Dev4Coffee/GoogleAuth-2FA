package de.janeckert.ga2fa;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.janeckert.ga2fa.entities.Identity;
import de.janeckert.ga2fa.entities.Login;
import de.janeckert.ga2fa.service.IdentityService;
import de.janeckert.ga2fa.service.TokenService;

@Controller
public class LoginController {
	
	IdentityService identityService;
	TokenService tokenService;

	public LoginController(IdentityService identityService, TokenService tokenService) {
		super();
		this.identityService = identityService;
		this.tokenService = tokenService;
	}

	@GetMapping("/login")
	public String showLogin() {
		return "login";
	}
	
	//Check for Credentials
	@PostMapping("/login")
	public String login(@ModelAttribute(name="loginForm") Login login, Model m, HttpServletResponse response) {
		Identity subject = this.identityService.retrieveIdentity(login.getUsername());

		String pass = login.getPassword();
		if( (subject != null) && pass.equals(subject.getPassword())) {
			m.addAttribute("uname", subject.getName());
			m.addAttribute("pass", subject.getPassword());
			
			// create a cookie
		    Cookie cookie = new Cookie("authorization", this.tokenService.createToken("secret", subject.getName()));

		    //add cookie to response
		    response.addCookie(cookie);
			
			return "welcome";
		}
		m.addAttribute("error", "Credentials Incorrect");
		return "index";

	}
}