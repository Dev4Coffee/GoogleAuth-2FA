package de.janeckert.ga2fa.entities;

import org.springframework.stereotype.Controller;

import lombok.Data;

@Controller
@Data
public class RegisterForm {
	private String username;
	private String password;
	private String passwordConfirm;

}
