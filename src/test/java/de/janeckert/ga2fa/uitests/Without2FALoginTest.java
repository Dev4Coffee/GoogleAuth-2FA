package de.janeckert.ga2fa.uitests;

import java.io.File;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT,
properties = {"server.port=9998", "app.mfa-strategy=off"})
public class Without2FALoginTest {
	public final static String BASE_URL = "http://localhost:9998";
	
	@BeforeAll
	static void init() {
		System.setProperty("webdriver.edge.driver", TestInfo.PATH_TO_EDGE_DRIVER);
		
		File driverFile = new File(TestInfo.PATH_TO_EDGE_DRIVER);
		Assumptions.assumeThat(driverFile).exists();
	}
	
	@Test
	public void whenUsingWrongCredentials_shouldRejectLogin() {
		EdgeOptions options = new EdgeOptions();
		options.setHeadless(true);
		WebDriver driver = new EdgeDriver(options);
		
		driver.get(BASE_URL);
		
		WebElement un = driver.findElement(By.id("inputUsername"));
		WebElement pw = driver.findElement(By.id("inputPassword"));
		WebElement submit = driver.findElement(By.id("inputSubmit"));
		
		un.sendKeys(TestInfo.INVALID_USERNAME);
		pw.sendKeys(TestInfo.INVALID_PASSWORD);
		submit.click();
		
		Assertions.assertThat(driver.getPageSource()).contains(TestInfo.ERROR_INVALID_CREDENTIALS_UI);
		
		driver.quit();
		
	}
	
	@Test
	public void whenUsingCorrectCredentials_shouldAcceptLogin() {
		EdgeOptions options = new EdgeOptions();
		options.setHeadless(true);
		WebDriver driver = new EdgeDriver(options);
		
		driver.get(BASE_URL);
		
		WebElement un = driver.findElement(By.id("inputUsername"));
		WebElement pw = driver.findElement(By.id("inputPassword"));
		WebElement submit = driver.findElement(By.id("inputSubmit"));
		
		un.sendKeys(TestInfo.VALID_USERNAME);
		pw.sendKeys(TestInfo.VALID_PASSWORD);
		submit.click();
		
		Assertions.assertThat(driver.getPageSource()).contains(TestInfo.SUCCESS_VALID_CREDENTIALS_UI);
				
		driver.quit();
		
	}

	
	@Test
	public void whenUsingCorrectCredentials_shouldReceiveAuthNCookie() {
		EdgeOptions options = new EdgeOptions();
		options.setHeadless(true);
		WebDriver driver = new EdgeDriver(options);
		
		driver.get(BASE_URL);
		
		WebElement un = driver.findElement(By.id("inputUsername"));
		WebElement pw = driver.findElement(By.id("inputPassword"));
		WebElement submit = driver.findElement(By.id("inputSubmit"));
		
		un.sendKeys(TestInfo.VALID_USERNAME);
		pw.sendKeys(TestInfo.VALID_PASSWORD);
		submit.click();
		
		driver.manage().getCookies().forEach(c -> {
			System.out.println(c.getName() +" " + c.getValue());
		});
		
		Assertions.assertThat(driver.manage().getCookieNamed("Authorization")).isNotNull();
		
		driver.quit();
		
	}

	
	@Test
	@DisplayName("WHEN you visit the index page without any Authentication THEN you should see the login screen.")
	public void whenVisitingLoginPageUnAuthenticated_TheninputElementsShouldBePresent() {
		EdgeOptions options = new EdgeOptions();
		options.setHeadless(true);
		WebDriver driver = new EdgeDriver(options);
		
		driver.get(BASE_URL);
		
		Assertions.assertThat(driver.findElement(By.id("inputUsername"))).isNotNull();
		Assertions.assertThat(driver.findElement(By.id("inputPassword"))).isNotNull();
		Assertions.assertThat(driver.findElement(By.id("inputSubmit"))).isNotNull();
		driver.quit();
	}

}
