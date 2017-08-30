package tests;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import framework.BaseTest;

public class Eribank extends BaseTest {
	
	@Test(priority=1)
	public void login() {
		driver.findElement(By.xpath("//*[@placeholder='Username']")).sendKeys("company");
		driver.findElement(By.xpath("//*[@placeholder='Password']")).sendKeys("company");
		driver.findElement(By.xpath("//*[@text='Login']")).click();	
	}
	
	@Test(priority=2, dependsOnMethods={"login"})
	public void getBalance() {
		//driver.context("WEBVIEW_1");
		System.out.println(driver.findElement(By.tagName("H1")).getText());
	}
	
	@Test(priority=3, dependsOnMethods={"login"})
	public void logout() {
		//driver.context("NATIVE_APP_INSTRUMENTED");
		driver.findElement(By.xpath("//*[@text='Logout']")).click();
	}
	
}