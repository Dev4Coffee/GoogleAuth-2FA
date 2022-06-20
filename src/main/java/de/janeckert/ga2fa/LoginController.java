package de.janeckert.ga2fa;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.janeckert.ga2fa.entities.Identity;
import de.janeckert.ga2fa.entities.Login;
import de.janeckert.ga2fa.service.IdentityService;

@Controller
public class LoginController {
	
	IdentityService identityService;
	
	
	
	public LoginController(IdentityService identityService) {
		super();
		this.identityService = identityService;
	}

	@GetMapping("/login")
	public String showLogin() {
		return "login";
	}
	
	//Check for Credentials
	@PostMapping("/login")
	public String login(@ModelAttribute(name="loginForm") Login login, Model m) {
		Identity subject = this.identityService.retrieveIdentity(login.getUsername());

		String pass = login.getPassword();
		if( (subject != null) && pass.equals(subject.getPassword())) {
			m.addAttribute("uname", subject.getName());
			m.addAttribute("pass", subject.getPassword());
			return "welcome";
		}
		m.addAttribute("error", "Credentials Incorrect");
		return "index";

	}
}