package de.janeckert.ga2fa;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebController {

	@GetMapping("/home")
	@ResponseBody
	public String home() {
		return "I am alive!";
	}
}
