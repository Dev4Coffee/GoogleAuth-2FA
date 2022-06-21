package de.janeckert.ga2fa.entities;

import org.springframework.stereotype.Controller;

import lombok.Data;

@Controller
@Data
public class Login {
	private String username;
	private String password;
}
