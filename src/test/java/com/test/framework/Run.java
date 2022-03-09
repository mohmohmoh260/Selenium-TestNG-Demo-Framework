package com.test.framework;

import java.awt.Desktop;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.test.scenarios.*;
import com.test.utilities.*;

import io.appium.java_client.AppiumDriver;

public class Run{
	
	//============================================== Classes ===================================================
	
	Excel_Setup Excel = new Excel_Setup();
	Driver_Setup Setup = new Driver_Setup();
	GetTestData GetTestData = new GetTestData();
	RunActions RunActions;
	
	//========================================== Test Suite Excel ==============================================
	
	List<String[]> TestSuites = Excel_Setup.GetDataFromExcel(System.getProperty("user.dir")+"\\Excel\\Test Scenario.xlsx", "Test Suite");
	List<String[]> TestCases = Excel_Setup.GetDataFromExcel(System.getProperty("user.dir")+"\\Excel\\Test Scenario.xlsx", "Test Case");
		
	//======================================= Browser & Mobile Excel ===========================================
	
	List<String[]> BrowserConfig = Excel_Setup.GetDataFromExcel(System.getProperty("user.dir")+"\\Excel\\Browser Modules.xlsx", "Configuration");
	List<String[]> BrowserAllModules = Excel_Setup.GetModulesFromExcel(System.getProperty("user.dir")+"\\Excel\\Browser Modules.xlsx");
	
	
	List<String[]> MobileConfig = Excel_Setup.GetDataFromExcel(System.getProperty("user.dir")+"\\Excel\\Mobile Modules.xlsx", "Configuration");
	List<String[]> MobileAllModules =  Excel_Setup.GetModulesFromExcel(System.getProperty("user.dir")+"\\Excel\\Mobile Modules.xlsx");

	//============================================== Report ====================================================
	
	ExtentReports Extent;
	ExtentSparkReporter Reporter;
	ExtentTest Test;
	
	//=============================================== Driver ===================================================
	
	WebDriver BrowserDriver;
	AppiumDriver<WebElement> MobileDriver;
	
	//======================================== Other Global Variables ==========================================
	
	int CurrentExecution = 0;
	String Time;
	String Date;
	String FilePath;
	
	//==========================================================================================================
	
	@BeforeSuite
	public void preparingTestScenarios() {
		
		// To kill all driver instances
		try {
			Runtime.getRuntime().exec("taskkill /F /IM ChromeDriver.exe");
		} catch (IOException e) {
		}
		try {
			Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe");
		} catch (IOException e) {
		}
		try {
			Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
		} catch (IOException e) {
		}
		
	}
	
	@BeforeTest
	public void InitReport() throws IOException {
		
		// Get current time & date
		TimeAndDate TimeAndDate = new TimeAndDate();
		Time = TimeAndDate.Time();
		Date = TimeAndDate.Date();
		FilePath = "Reports\\"+Date+"\\"+Time;	
		
		Extent = new ExtentReports();
		Reporter = new ExtentSparkReporter(FilePath+"\\HTML Report");
		Reporter.loadXMLConfig(new File("extentconfig.xml"));
		Reporter.config().setTimeStampFormat("hh:mm:ss");
		Extent.attachReporter(Reporter);
		
		// Initializing CSV log
		CreateExlFile create = new CreateExlFile(FilePath);
		create.CreateLog();
	}
	
	@BeforeMethod
	public void OpenDriver() {
		
		List<String[]>  GetPlatform= GetTestData.getPlatform(TestSuites);
		
		String[] Platform = GetPlatform.get(CurrentExecution);
		
		this.CurrentExecution = CurrentExecution + 1;
	
		// Check the setup
		if(Platform[0].equalsIgnoreCase("Browser")) {
			
			List<String[]> GetConfig = GetTestData.BowserConfig(BrowserConfig);
			
			try {
				BrowserDriver = Setup.Browser_Setup(GetConfig.get(0));
			}catch (Exception e) {
				this.Test = Extent.createTest("Fail to start mobile driver").assignAuthor("Hakim").assignDevice("Browser");
				Test.log(Status.FAIL, "Please check the Configuration sheet in Browser Modules Excel \n"+e.getLocalizedMessage());
				return;
			}
			RunActions = new RunActions(BrowserDriver);
			
		}else {
			
			List<String[]> GetConfig = GetTestData.MobileConfig(MobileConfig);
		
			try {
				MobileDriver = Setup.Mobile_Setup(GetConfig.get(0));
			} catch (Exception e) {
				this.Test = Extent.createTest("Fail to start mobile driver").assignAuthor("Hakim").assignDevice("Mobile");
				Test.log(Status.FAIL, "Please check the Configuration sheet in Mobile Modules Excel \n"+e.getLocalizedMessage());
				return;
			}
			RunActions = new RunActions(MobileDriver);
		}
	}
	
	@DataProvider 
	public Iterator<String[]> TestScenario() {
		
		// calling method to filter enabled Test Suites
		List<String[]>  NoOfTesTSuites= GetTestData.NumberOfTestSuite(TestSuites);
		
		return NoOfTesTSuites.iterator();
	}
	
	@Test (dataProvider="TestScenario")
	public void Driver(String TestSuite, String Platform) {
	
		this.Test = Extent.createTest(TestSuite).assignAuthor("Hakim").assignDevice("Chrome");
		
		// Fetch Test Scenarios
		HashMap<String, List<String>> TestCase = GetTestData.TestCase(TestSuites);
		HashMap<String, List<String>> TestProcedure = GetTestData.TestProcedure(TestCases);
		HashMap<String, List<String>> Module;

		// Fetch Modules
		if(Platform.equalsIgnoreCase("Browser")) {
			Module = GetTestData.Modules(BrowserAllModules);
		}else {
			Module = GetTestData.Modules(MobileAllModules);
		}

		// loop for TestCase from TestSuite ID
		for(int i=0; i<TestCase.get(TestSuite).size(); i++) {
			
			String tempTestCase = null;
			
			// Initializing test case
			try {
				tempTestCase = TestCase.get(TestSuite).get(i);
			}catch (Exception e) {
				Test.log(Status.FAIL, "Please check if the Test Case is linked with the correct Test Suite ["+TestSuite+"]");
				return;
			}
			
			Test.log(Status.INFO, tempTestCase);
			
			for(int j=0; j<TestProcedure.get(tempTestCase).size(); j++) {
				
				String TempTestProcedure = null;
				
				// Initializing test procedure
				try {
					TempTestProcedure = TestProcedure.get(tempTestCase).get(j);
				}catch (Exception e) {
					Test.log(Status.FAIL, "Please check if the Test Procedure is linked with the correct Test Case ["+tempTestCase+"]");
					return;
				}
				
				List<String> tempModule;
				// Initializing test procedure details
				try {
					tempModule = Module.get(TempTestProcedure);
				}catch (Exception e) {
					Test.log(Status.FAIL, "Please check if the Modules Sheet is contain with the correct Test Procedure ["+TempTestProcedure+"]");
					return;
				}
				
				try{
					
					RunActions.DoAction(tempModule, TempTestProcedure, Test, FilePath, Platform);
					
				}catch(Exception e) {
					
					String ScreenCapturePath = null; 
					
					if(Platform.equalsIgnoreCase("Browser")) {
						
						ScreenCapturePath = RunActions.BrowserScreenshot(TempTestProcedure, Test, FilePath);
						
					}else {
						
						try {
							ScreenCapturePath = RunActions.MobileScreenshot(TempTestProcedure, Test, FilePath);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					Test.log(Status.FAIL, TempTestProcedure+" ERROR: "+e.getMessage(), MediaEntityBuilder.createScreenCaptureFromPath(ScreenCapturePath).build());
					Assert.fail(e.getMessage());
				}
			}
		}
		
	}

	@AfterMethod
	public void logout() {
		
		try {
			Setup.closeBrowser();
		}catch (Exception e) {	
		}
		try {
			Setup.closeMobileApplication();
		}catch(Exception e) {
			
		}
	}
	
	@AfterSuite
	public void Exit() {
		
		Extent.flush();
		
		try {
			Setup.exitBrowser();
		}catch (Exception e) {	
		}
		try {
			Setup.exitMobileDriver();
		}catch (Exception e) {
		}
		
		try {
			
			Desktop.getDesktop().browse(new File(FilePath+"\\HTML Report.html").toURI());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.getLocalizedMessage();
		}
	}

}
