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
properties = {"server.port=9999", "app.mfa-strategy=on"})
public class With2FALoginTest {
	public final static String BASE_URL = "http://localhost:9999";

	@BeforeAll
	static void init() {
		System.setProperty("webdriver.edge.driver", TestInfo.PATH_TO_EDGE_DRIVER);
		
		File driverFile = new File(TestInfo.PATH_TO_EDGE_DRIVER);
		Assumptions.assumeThat(driverFile).exists();
	}
	
	@Test
	@DisplayName("WHEN using correct credentials THEN should see the 2FA login screen")
	public void whenUsingCorrectCredentials_shouldSeeMfaLogin() {
		EdgeOptions options = new EdgeOptions();
		options.setHeadless(true);
		WebDriver driver = new EdgeDriver(options);
		
		driver.get(BASE_URL);
		
		WebElement un = driver.findElement(By.id("inputUsername"));
		WebElement pw = driver.findElement(By.id("inputPassword"));
		WebElement submit = driver.findElement(By.id("inputSubmit"));
		
		un.sendKeys(TestInfo.IDENTITY_2FA_READY.getName());
		pw.sendKeys(TestInfo.IDENTITY_2FA_READY.getPassword());
		submit.click();
		
		WebElement code = driver.findElement(By.id("inputTotp"));
		Assertions.assertThat(code).isNotNull();
		Assertions.assertThat(code.isDisplayed()).isTrue();

		WebElement submit2FA = driver.findElement(By.id("inputSubmit"));
		Assertions.assertThat(submit2FA).isNotNull();
		Assertions.assertThat(submit2FA.isDisplayed()).isTrue();

		driver.quit();
	}
	
	@Test
	@DisplayName("WHEN using correct credentials and WHEN using wrong authenticator code THEN should see error message.")
	public void whenUsingCorrectCredentialsButWrongCode_shouldSeeError() {
		EdgeOptions options = new EdgeOptions();
		options.setHeadless(true);
		WebDriver driver = new EdgeDriver(options);
		
		driver.get(BASE_URL);
		
		WebElement un = driver.findElement(By.id("inputUsername"));
		WebElement pw = driver.findElement(By.id("inputPassword"));
		WebElement submit = driver.findElement(By.id("inputSubmit"));
		
		un.sendKeys(TestInfo.IDENTITY_2FA_READY.getName());
		pw.sendKeys(TestInfo.IDENTITY_2FA_READY.getPassword());
		submit.click();
		
		WebElement submit2FA = driver.findElement(By.id("inputSubmit"));
		WebElement code = driver.findElement(By.id("inputTotp"));
		code.sendKeys("1");
		submit2FA.click();
		
		WebElement submit2FABack = driver.findElement(By.id("inputSubmit"));
		WebElement usernameBack = driver.findElement(By.id("inputUsername"));
		WebElement codeBack = driver.findElement(By.id("inputTotp"));
		Assertions.assertThat(codeBack.isDisplayed()).isTrue();
		Assertions.assertThat(codeBack.isEnabled()).isTrue();
		Assertions.assertThat(usernameBack.isDisplayed()).isTrue();
		Assertions.assertThat(usernameBack.isEnabled()).isFalse();
		//Assertions.assertThat(usernameBack.getText()).isEqualTo(TestInfo.IDENTITY_2FA_READY.getName());
		Assertions.assertThat(submit2FABack.isDisplayed()).isTrue();
		Assertions.assertThat(submit2FABack.isEnabled()).isTrue();
		
		Assertions.assertThat(driver.getPageSource()).contains(TestInfo.INVALID_AUTH_CODE_UI);
				
		driver.quit();
	}
	
	@Test
	@DisplayName("WHEN using correct credentials and WHEN user is not set up for MFA THEN should see error message.")
	public void whenUsingCorrectCredentialsButUserAccountNot2faReady_shouldSeeError() {
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
		
		WebElement unBack = driver.findElement(By.id("inputUsername"));
		WebElement pwBack = driver.findElement(By.id("inputPassword"));
		
		Assertions.assertThat(unBack.isDisplayed()).isTrue();
		Assertions.assertThat(unBack.isEnabled()).isTrue();
		Assertions.assertThat(pwBack.isDisplayed()).isTrue();
		Assertions.assertThat(pwBack.isEnabled()).isTrue();
		
		Assertions.assertThat(driver.getPageSource()).contains(TestInfo.NOT_READY_FOR_MFA_UI);
				
		driver.quit();
	}
	
	@Test
	@DisplayName("WHEN using correct credentials and WHEN using invalid authenticator code THEN should see error message.")
	public void whenUsingCorrectCredentialsButInvalidCode_shouldSeeError() {
		EdgeOptions options = new EdgeOptions();
		options.setHeadless(true);
		WebDriver driver = new EdgeDriver(options);
		
		driver.get(BASE_URL);
		
		WebElement un = driver.findElement(By.id("inputUsername"));
		WebElement pw = driver.findElement(By.id("inputPassword"));
		WebElement submit = driver.findElement(By.id("inputSubmit"));
		
		un.sendKeys(TestInfo.IDENTITY_2FA_READY.getName());
		pw.sendKeys(TestInfo.IDENTITY_2FA_READY.getPassword());
		submit.click();
		
		WebElement submit2FA = driver.findElement(By.id("inputSubmit"));
		WebElement code = driver.findElement(By.id("inputTotp"));
		code.sendKeys("wrong");
		submit2FA.click();
		
		Assertions.assertThat(driver.getPageSource()).contains(TestInfo.INVALID_AUTH_CODE_UI);
				
		driver.quit();
	}
}
