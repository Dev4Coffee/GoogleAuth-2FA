package de.janeckert.ga2fa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.janeckert.ga2fa.configuration.ApplicationConfiguration;

@SpringBootTest
class GoogleAuth2FaApplicationTests {
	@Autowired
	ApplicationConfiguration cfg;
	

	@Test
	public void contextLoads() {
		System.out.println(cfg);
	}

}
