package de.janeckert.ga2fa.uitests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT,
properties = "server.port=9999")
public class LoginTest {
	
	private final String BASE_URL = "http://localhost:9999";
	
	private final static String INVALID_USERNAME = "Whatever";
	private final static String INVALID_PASSWORD = "Whatever";
	private final static String ERROR_INVALID_CREDENTIALS = "Credentials Incorrect";
	private final static String SUCCESS_VALID_CREDENTIALS = "You are authenticated!";
	private final static String VALID_USERNAME = "Will";
	private final static String VALID_PASSWORD = "Hireme";
	
	@BeforeAll
	static void init() {
		System.setProperty("webdriver.edge.driver", "D:\\Selenium\\msedgedriver.exe");
	}
	
	@Test
	public void whenUsingWrongCredentials_shouldRejectLogin() {
		EdgeOptions options = new EdgeOptions();
		WebDriver driver = new EdgeDriver(options);
		
		driver.get(BASE_URL);
		
		WebElement un = driver.findElement(By.id("inputUsername"));
		WebElement pw = driver.findElement(By.id("inputPassword"));
		WebElement submit = driver.findElement(By.id("inputSubmit"));
		
		un.sendKeys(INVALID_USERNAME);
		pw.sendKeys(INVALID_PASSWORD);
		submit.click();
		
		Assertions.assertThat(driver.getPageSource()).contains(ERROR_INVALID_CREDENTIALS);
		
		driver.quit();
		
	}
	
	@Test
	public void whenUsingCorrectCredentials_shouldAcceptLogin() {
		EdgeOptions options = new EdgeOptions();
		WebDriver driver = new EdgeDriver(options);
		
		driver.get(BASE_URL);
		
		WebElement un = driver.findElement(By.id("inputUsername"));
		WebElement pw = driver.findElement(By.id("inputPassword"));
		WebElement submit = driver.findElement(By.id("inputSubmit"));
		
		un.sendKeys(VALID_USERNAME);
		pw.sendKeys(VALID_PASSWORD);
		submit.click();
		
		Assertions.assertThat(driver.getPageSource()).contains(SUCCESS_VALID_CREDENTIALS);
		
		Assertions.assertThat(driver.manage().getCookieNamed("authorization")).isNotNull();
		
		driver.quit();
		
	}


	
	@Test
	public void whenVisitingLoginPage_TheninputElementsShouldBePresent() {
		EdgeOptions options = new EdgeOptions();
		WebDriver driver = new EdgeDriver(options);
		
		driver.get(BASE_URL);
		
		Assertions.assertThat(driver.findElement(By.id("inputUsername"))).isNotNull();
		Assertions.assertThat(driver.findElement(By.id("inputPassword"))).isNotNull();
		Assertions.assertThat(driver.findElement(By.id("inputSubmit"))).isNotNull();
		driver.quit();
	}

}
