package com.test.scenarios;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.test.utilities.Excel_Setup;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

public class RunActions{
	
	GetTestData GetTestData = new GetTestData();
	
	List<String[]> BrowserObjectRepository = Excel_Setup.GetDataFromExcel(System.getProperty("user.dir")+"\\Excel\\Browser Modules.xlsx", "Object Repository");
	List<String[]> BrowserInputData = Excel_Setup.GetDataFromExcel(System.getProperty("user.dir")+"\\Excel\\Browser Modules.xlsx", "Input Data");
	
	List<String[]> MobileObjectRepository = Excel_Setup.GetDataFromExcel(System.getProperty("user.dir")+"\\Excel\\Mobile Modules.xlsx", "Object Repository");
	List<String[]> MobileInputData = Excel_Setup.GetDataFromExcel(System.getProperty("user.dir")+"\\Excel\\Mobile Modules.xlsx", "Input Data");	
	
	HashMap<String, List<String>> BrowserHashObjectRepository = GetTestData.ObjectRepository(BrowserObjectRepository);
	HashMap<String, List<String>> BrowserHasHInputData = GetTestData.InputData(BrowserInputData);
	
	HashMap<String, List<String>> MobileHashObjectRepository = GetTestData.ObjectRepository(MobileObjectRepository);
	HashMap<String, List<String>> MobileHasHInputData = GetTestData.InputData(MobileInputData);
	
	WebDriver BrowserDriver;
	AppiumDriver<WebElement> MobileDriver;
	
	WebElement element;
	ExtentTest Test;
	
	public RunActions(WebDriver browserDriver) {
		this.BrowserDriver = browserDriver;
	}
	
	public RunActions(AppiumDriver<WebElement> mobileDriver) {
		this.MobileDriver = mobileDriver;		
	}

	public void DoAction(List<String> DoList, String TestProcedure, ExtentTest Test, String FilePath, String Platform) {
		
		// Noted that  Indexes are -- 0=Screenshot 1=Action 2=ObjectType 3=InputData 4=ExpectedValue --
		String Screenshot = DoList.get(0);
		String Action = DoList.get(1);
		String ObjectName = DoList.get(2);
		String ExpectedValue = DoList.get(4);

		String ObjectValue = "";
		String InputData = "";
		String ScreenCapturePath = "";
		
		// =================================================== Browser ===================================================
		if(Platform.equalsIgnoreCase("Browser")) {
		
			WebDriverWait wait = new WebDriverWait(BrowserDriver, 10);
			
			// Wait for element and scroll to the element
			if (ObjectName.isEmpty() == false) {
				
				// Get the Object Repository values
				ObjectValue = BrowserHashObjectRepository.get(ObjectName).get(0);
			
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(ObjectValue)));

				element = BrowserDriver.findElement(By.xpath(ObjectValue));
				
				// Scroll to view
				((JavascriptExecutor) BrowserDriver).executeScript("arguments[0].scrollIntoView(true);", element);
				
				// Highlight element
				((JavascriptExecutor)BrowserDriver).executeScript("arguments[0].style.border='3px solid red'", element);
			}
			
			// Take screenshot
			ScreenCapturePath = BrowserScreenshot(TestProcedure, Test, FilePath);
			
			if(Action.equalsIgnoreCase("Click")) {
				
				try {
					WebElement element = BrowserDriver.findElement(By.xpath(ObjectValue));
					Actions actions = new Actions(BrowserDriver);
					actions.moveToElement(element).click().perform();
				} catch (Exception e) {
					((JavascriptExecutor) BrowserDriver).executeScript("arguments[0].click();", element);
				}
				
			}else if(Action.equalsIgnoreCase("Input")) {
				
				InputData = BrowserHasHInputData.get(DoList.get(3)).get(0);
				BrowserDriver.findElement(By.xpath(ObjectValue)).sendKeys(InputData);
			
			}else if(Action.equalsIgnoreCase("Verify")){
				
				String Actual = BrowserDriver.findElement(By.xpath(ObjectValue)).getText();
				
				if(!Actual.equals(ExpectedValue)) {
					
					Test.log(Status.FAIL, TestProcedure+" The Expected ["+ExpectedValue+"] is NOT EQUALS to Actual ["+Actual+"]" , MediaEntityBuilder.createScreenCaptureFromPath(ScreenCapturePath).build());
					return;
				}
			
			}else if(Action.equalsIgnoreCase("Verify Exist")) {
				
				if(BrowserDriver.findElements(By.xpath(ObjectValue)).size()==0) {
					
					Test.log(Status.FAIL, TestProcedure+" The Element is not Exist", MediaEntityBuilder.createScreenCaptureFromPath(ScreenCapturePath).build());
					return;
				}
			
			}else if(Action.equalsIgnoreCase("Select")) {
				
				InputData = BrowserHasHInputData.get(DoList.get(3)).get(0);
				WebElement element = BrowserDriver.findElement(By.xpath(ObjectValue));
				Select dropdown = new Select(element);
				dropdown.selectByVisibleText(InputData);				
			}
			
			if(Screenshot.equalsIgnoreCase("Y")) {
				
				Test.log(Status.PASS, TestProcedure, MediaEntityBuilder.createScreenCaptureFromPath(ScreenCapturePath).build());
			
			}else {
			
				Test.log(Status.PASS, TestProcedure);
			}
			
			try {
				// Close Highlight
				((JavascriptExecutor)BrowserDriver).executeScript("arguments[0].style.border=''", element);
			}catch (Exception e) {
				
			}
			
		}else {// ============================================== Mobile ==========================================================

			WebDriverWait wait = new WebDriverWait(MobileDriver, 10);
			By Object = null;
			
			// Wait for element and scroll to the element
			if (ObjectName.isEmpty() == false) {
				
				String ObjectType = "";
				
				// Get the Object Repository values
				ObjectType = MobileHashObjectRepository.get(ObjectName).get(0);
				ObjectValue = MobileHashObjectRepository.get(ObjectName).get(1);
			
				// Assigning values using By class
				if(ObjectType.equalsIgnoreCase("xpath")) {
					
					Object = By.xpath(ObjectValue);
				
				}else{// Resource-id
					
					Object = By.id(ObjectValue);
				}
				
				// Highlight focus element
				((JavascriptExecutor)MobileDriver).executeScript("arguments[0].style.border='3px solid red'", MobileDriver.findElement(Object));
				
				// wait for object
				if (!Action.equalsIgnoreCase("Scroll Down To") && !Action.equalsIgnoreCase("Scroll Up To")) {

					wait.until(ExpectedConditions.presenceOfElementLocated(Object));
				}
				
				// Take screenshot
				try {
					ScreenCapturePath = MobileScreenshot(TestProcedure, Test, FilePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(Action.equalsIgnoreCase("Click")) {
					
					MobileDriver.findElement(Object).click();
				
				}else if(Action.equalsIgnoreCase("Input")) {
					
					InputData = MobileHasHInputData.get(DoList.get(3)).get(0);
					
					MobileDriver.findElement(Object).sendKeys(InputData);
					
					try {
						MobileDriver.hideKeyboard();
					} catch (Exception e) {
					}
				
				}else if(Action.equalsIgnoreCase("Scroll Down To")) {
					
					ScrollDown(Object);
				
				}else if(Action.equalsIgnoreCase("Scroll Up To")){
					
					ScrollUp(Object);
				
				}else if(Action.equalsIgnoreCase("Verify")) {
					
					String Actual = MobileDriver.findElement(Object).getText();
					
					if(!Actual.equals(ExpectedValue)) {
						
						Test.log(Status.FAIL, "The Expected ["+ExpectedValue+"] is NOT EQUALS to Actual ["+Actual+"]", MediaEntityBuilder.createScreenCaptureFromPath(ScreenCapturePath).build());
						return;
					}
					
				}else if(Action.equalsIgnoreCase("Verify Exist")) {
					
					if(MobileDriver.findElements(Object).size()==0) {
					
						Test.log(Status.FAIL, TestProcedure+" The Element is not Exist", MediaEntityBuilder.createScreenCaptureFromPath(ScreenCapturePath).build());
						return;
					}
				}
			
			}else {// If Object Name is empty, means these actions requires no object
			
				if(Action.equalsIgnoreCase("Delay")) {
					
					long val = Long.valueOf("3");
					
					try {
						TimeUnit.SECONDS.sleep(val);
					} catch (InterruptedException e) {
					}
					
				}else if(Action.equalsIgnoreCase("Hide Keyboard")) {
				
					MobileDriver.hideKeyboard();
					
				}else if(Action.equalsIgnoreCase("Swipe Up")) {
					
					SwipeUp();
				
				}else if(Action.equalsIgnoreCase("Swipe Down")) {
	
					SwipeDown();
					
				}else if(Action.equalsIgnoreCase("Swipe Left")) {
					
					SwipeLeft();
				
				}else if(Action.equalsIgnoreCase("Swipe Right")) {
					
					SwipeRight();
					
				}else if(Action.equalsIgnoreCase("Back")) {
					
					MobileDriver.navigate().back();
				}
			}
		
			// Take screenshot if enable
			if(Screenshot.equalsIgnoreCase("Y")) {
				
				Test.log(Status.PASS, TestProcedure, MediaEntityBuilder.createScreenCaptureFromPath(ScreenCapturePath).build());
			
			}else {
			
				Test.log(Status.PASS, TestProcedure);
			}
			
			try {
				// Close highlight
				((JavascriptExecutor)MobileDriver).executeScript("arguments[0].style.border=''", Object);
			}catch (Exception e) {
				
			}
		}
	}
	
	public String BrowserScreenshot(String ProcedureID, ExtentTest Test, String Path){
		
		String FinalPath;
		File TargetFile;
		File ScreenShotFile = ((TakesScreenshot)BrowserDriver).getScreenshotAs(OutputType.FILE);
		
		int i = 1;
		
		TargetFile = new File(Path+"\\Screenshot\\"+ProcedureID+".png");
		FinalPath = "Screenshot/"+ProcedureID+".png";
		
		while(TargetFile.isFile()) {
				
			TargetFile = new File(Path+"/Screenshot/"+ProcedureID+"("+i+").png");
			FinalPath = "Screenshot/"+ProcedureID+"("+i+").png";
			i++;
			}
		
		try {
			FileUtils.copyFile(ScreenShotFile, TargetFile);
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		return FinalPath;
	}
	
	public String MobileScreenshot(String ProcedureID, ExtentTest Test, String Path) throws Exception{
		
		String FinalPath;
		File TargetFile;
	
		File ScreenShotFile = ((TakesScreenshot)MobileDriver).getScreenshotAs(OutputType.FILE);
	
		int i = 1;
		
		TargetFile = new File(Path+"\\Screenshot\\"+ProcedureID+".png");
		FinalPath = "Screenshot/"+ProcedureID+".png";
		
		while(TargetFile.isFile()) {
				
			TargetFile = new File(Path+"/Screenshot/"+ProcedureID+"("+i+").png");
			FinalPath = "Screenshot/"+ProcedureID+"("+i+").png";
			i++;
			}
		
		try {
			FileUtils.copyFile(ScreenShotFile, TargetFile);
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}
		
		return FinalPath;
	}
	
	@SuppressWarnings("rawtypes")
	public void SwipeDown(){
	
		int swipeSpeed = 2;
		
		Dimension dimension = MobileDriver.manage().window().getSize();
		int startHeight = (int) (dimension.getHeight() * 0.55);
		int endHeight = (int) (dimension.getHeight() * 0.35);
		
		new TouchAction(MobileDriver)
		.press(PointOption.point(0, startHeight))
		.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(swipeSpeed)))
		.moveTo(PointOption.point(0, endHeight))
		.release().perform();
	    }
	
	@SuppressWarnings("rawtypes")
	public void SwipeUp(){

		int swipeSpeed = 2;
		
		Dimension dimension = MobileDriver.manage().window().getSize();
		int startHeight = (int) (dimension.getHeight() * 0.35);
		int endHeight = (int) (dimension.getHeight() * 0.55);
		 
		new TouchAction(MobileDriver)
		.press(PointOption.point(0, startHeight))
		.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(swipeSpeed)))
		.moveTo(PointOption.point(0, endHeight))
		.release().perform();
	    }
	
	@SuppressWarnings("rawtypes")
	public void SwipeLeft(){

		int swipeSpeed = 2;
		
		Dimension dimension = MobileDriver.manage().window().getSize();
		int startWidth = (int) (dimension.getWidth() * 0.35);
		int endWidth = (int) (dimension.getHeight() * 0.55);
		
		new TouchAction(MobileDriver)
		.press(PointOption.point(0, startWidth))
		.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(swipeSpeed)))
		.moveTo(PointOption.point(0, endWidth))
		.release().perform();
	    }
	
	@SuppressWarnings("rawtypes")
	public void SwipeRight(){
		
		int swipeSpeed = 2;
		
		Dimension dimension = MobileDriver.manage().window().getSize();
		int startWidth = (int) (dimension.getWidth() * 0.55);
		int endWidth = (int) (dimension.getHeight() * 0.35);
		 
		new TouchAction(MobileDriver)
		.press(PointOption.point(0, startWidth))
		.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(swipeSpeed)))
		.moveTo(PointOption.point(0, endWidth))
		.release().perform();
	  }
	
	@SuppressWarnings("rawtypes")
	public void ScrollDown(By element){
		
		// get the dimension of x and y axis
		Dimension dimension = MobileDriver.manage().window().getSize();
		int startHeight = (int) (dimension.getHeight() * 0.60);
		int endHeight = (int) (dimension.getHeight() * 0.30);
		
		// list of element to check
		List<WebElement> isDisplay;
		
		do {
			isDisplay = MobileDriver.findElements(element);
			
			//scroll action
			try {
				new TouchAction(MobileDriver)
				.press(PointOption.point(0, startHeight))
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000)))
				.moveTo(PointOption.point(0, endHeight))
				.release().perform();
			}catch (Exception e) {
			}
			
		}while(isDisplay.isEmpty()==true);
	}
	
	@SuppressWarnings("rawtypes")
	public void ScrollUp(By element){
		
		// get the dimension of x and y axis
		Dimension dimension = MobileDriver.manage().window().getSize();
		int startHeight = (int) (dimension.getHeight() * 0.35);
		int endHeight = (int) (dimension.getHeight() * 0.55);
		
		// list of element to check
		List<WebElement> isDisplay;
		
		do {
			isDisplay = MobileDriver.findElements(element);
			
			//scroll action
			try {
				new TouchAction(MobileDriver)
				.press(PointOption.point(0, startHeight))
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000)))
				.moveTo(PointOption.point(0, endHeight))
				.release().perform();
			}catch (Exception e) {
			}
			
		}while(isDisplay.isEmpty()==true);
	}

}
