package com.test.utilities;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Driver_Setup {

	public WebDriver browserDriver;

	public WebDriver Browser_Setup(String[] browser) {
		
		// Index -- 0=Status 1=Browser 2=URL
		if(browser[1].equals("Chrome")) {
			
			WebDriverManager.chromedriver().setup();
			browserDriver = new ChromeDriver();
		
		}else if(browser[1].equals("Firefox")) {
			
			WebDriverManager.firefoxdriver().setup();
			browserDriver = new FirefoxDriver();
		
		}else if(browser[1].equals("Chromium")) {
			
			WebDriverManager.edgedriver().setup();
			browserDriver = new EdgeDriver();
		}
		
		browserDriver.get(browser[2]);
		browserDriver.manage().window().maximize();
		
		return browserDriver;
	}
	
	public void closeBrowser() {

		browserDriver.close();
	}

	public void exitBrowser() {

		browserDriver.quit();
	}

	public AppiumDriver<WebElement> mobileDriver;

	public AppiumDriver<WebElement> Mobile_Setup(String[] mobile) throws MalformedURLException {

		DesiredCapabilities dc = new DesiredCapabilities();

		dc.setCapability(MobileCapabilityType.UDID, mobile[1]);
		
		dc.setCapability(MobileCapabilityType.PLATFORM_NAME, mobile[2]);

		dc.setCapability(MobileCapabilityType.APP, mobile[3]);

		dc.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, mobile[4]);
		
		dc.setCapability(MobileCapabilityType.APP, System.getProperty("user.dir") + "\\Apk\\" +mobile[5]+".apk");

		dc.setCapability(MobileCapabilityType.FULL_RESET, false);

		mobileDriver = new AndroidDriver<WebElement>(new URL("http://127.0.0.1:4723/wd/hub"), dc);
		
		return mobileDriver;
	}

	public void closeMobileApplication() {

		mobileDriver.resetApp();
	}

	public void exitMobileDriver() {

		mobileDriver.quit();
	}

}
