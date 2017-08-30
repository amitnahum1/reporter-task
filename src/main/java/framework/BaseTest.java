package framework;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

public abstract class BaseTest {

	//General
	protected static String buildId = System.getenv("BUILD_NUMBER");
	private static HashMap<String,DeviceForTest> testsMap;
	
	//Grid
    private static final String GRID_USERNAME = System.getenv("SeeTestCloud_Username");
    private static final String GRID_PASSWORD = System.getenv("SeeTestCloud_Password");
    private static final String GRID_PROJECT_NAME = System.getenv("SeeTestCloud_Project");
    private static final String GRID_URL = System.getenv("SeeTestCloud_Server_URL");
    private static final String DEVICE1_QUERY = System.getenv("Device1_Query");
    private static final String DEVICE2_QUERY = System.getenv("Device2_Query");
    private static final String DEVICE3_QUERY = System.getenv("Device3_Query");
    private static final String DEVICE4_QUERY = System.getenv("Device4_Query");
    private static final String DEVICE5_QUERY = System.getenv("Device5_Query");
    
    //Client and Device
    protected AppiumDriver<WebElement> driver;
    private String deviceQuery;
    protected MobileOS deviceOS;

    /*private static String getTestNGParam(ITestContext context, String key) {
    	return context.getCurrentXmlTest().getParameter(key);
    }*/
    
    @BeforeSuite
    public void beforeSuite(ITestContext context) {
    	System.out.println(System.getenv());
    }
    
    @BeforeTest
    public void beforeTest(ITestContext context) {
    	String testName = context.getCurrentXmlTest().getName();
    	String query = getQueryFromTestName(testName);
    	addToTestsMap(testName, query);
    }
    
    private synchronized void addToTestsMap(String testName, String query) {
    	if (testsMap == null) {
    		testsMap = new HashMap<String,DeviceForTest>();
    	}
    	System.out.println("ADDING TO TESTS MAP -> " + testName);
    	testsMap.put(testName, new DeviceForTest(query,getDeviceOSfromQuery(query)));
    }

    @BeforeClass
    public void beforeClass(ITestContext context) {
    	String testName = context.getCurrentXmlTest().getName();
    	this.deviceQuery = testsMap.get(testName).getQuery();
    	this.deviceOS = testsMap.get(testName).getOs();
    	System.out.println("Test Class with -> " + testName + " , " + this.deviceOS + " , " + this.deviceQuery);
    	DesiredCapabilities caps = new DesiredCapabilities();
    	URL seeTestServer = null;
		try {
			seeTestServer = new URL(GRID_URL + "/wd/hub");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    	switch (deviceOS) {
    		case ANDROID:
    			addSeeTestGridCaps(caps, context);
    			//System.out.println(caps);
    			caps.setCapability(MobileCapabilityType.APP, "cloud:com.experitest.ExperiBank/.LoginActivity");  
    			caps.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.experitest.ExperiBank");
    			caps.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, ".LoginActivity");
    			driver = new AndroidDriver<WebElement>(seeTestServer, caps); 
    			break;
    		case IOS: 
    			addSeeTestGridCaps(caps, context);
    			//System.out.println(caps);
    			caps.setCapability(MobileCapabilityType.APP, "cloud:com.experitest.ExperiBank");
    			caps.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "com.experitest.ExperiBank");
    			driver = new IOSDriver<WebElement>(seeTestServer, caps); 
    			break;
    	}
    	driver.setLogLevel(Level.INFO);
    	driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    	driver.context("NATIVE_APP_INSTRUMENTED");
    }
    
    private void addSeeTestGridCaps(DesiredCapabilities caps, ITestContext context) {
    	caps.setCapability("user", GRID_USERNAME);
    	caps.setCapability("password", GRID_PASSWORD);
    	caps.setCapability("project", GRID_PROJECT_NAME);
    	caps.setCapability("reportFormat", "xml");
    	caps.setCapability("testName", this.getClass().getSimpleName());
    	caps.setCapability("suite.type", context.getSuite().getName());
    	caps.setCapability("build.id", buildId);
    	caps.setCapability("deviceQuery", deviceQuery);
    	caps.setCapability("instrumentApp", true);
    }
    
    private String getQueryFromTestName(String testName) {
    	int deviceNum = Integer.parseInt(testName.split(" ")[1]);
    	switch(deviceNum) {
	    	case 1: return DEVICE1_QUERY;
	    	case 2: return DEVICE2_QUERY;
	    	case 3: return DEVICE3_QUERY;
	    	case 4: return DEVICE4_QUERY;
	    	case 5: return DEVICE5_QUERY;
    	}
    	return null;
    }
    
    private MobileOS getDeviceOSfromQuery(String query) {
    	if (query.contains("android")) {
    		return MobileOS.ANDROID;
    	} else if (query.contains("ios")) {
    		return MobileOS.IOS;
    	}
    	return null;
    }
    
    @BeforeMethod
    public void beforeMethod(ITestContext context, Method method) {
    	driver.executeScript("client:client.startStepsGroup('" + method.getName() + "')");
    }

    @AfterMethod
    public void afterMethod() {
    	driver.executeScript("client:client.stopStepsGroup()");
    }

    @AfterClass
    public void afterClass() {
    	driver.quit();
    }
    
}