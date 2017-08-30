package framework;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.IOSMobileCapabilityType;

public abstract class BaseTest {

	//General
	protected static String buildId = System.getenv("BUILD_NUMBER");
	
	//Grid
    private static final String GRID_USERNAME = System.getenv("SeeTestCloud_Username");
    private static final String GRID_PASSWORD = System.getenv("SeeTestCloud_Password");
    private static final String GRID_PROJECT_NAME = System.getenv("SeeTestCloud_Project");
    private static final String GRID_URL = System.getenv("SeeTestCloud_Server_URL");
    
    //Client and Device
    protected AppiumDriver<WebElement> driver;
    protected MobileOS deviceOS;

    private static String getTestNGParam(ITestContext context, String key) {
    	return context.getCurrentXmlTest().getParameter(key);
    }
    
    /*@BeforeSuite
    public void beforeSuite(ITestContext context) {
    	buildId = System.getenv("BUILD_NUMBER");
    }*/
    
    private String getQueryFromUdid(String udid) {
    	return "@serialnumber='" + udid + "'";
    }

    @BeforeClass
    public void beforeClass(ITestContext context) {
    	setBrowserOS(getTestNGParam(context,"device.os"));
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
    			driver = new AndroidDriver<WebElement>(seeTestServer, caps); 
    			break;
    		case IOS: 
    			addSeeTestGridCaps(caps, context);
    			//System.out.println(caps);
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
    	caps.setCapability("deviceQuery", getQueryFromUdid(getUdidFromTestName(context)));
    	caps.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "com.experitest.ExperiBank");
    }
    
    private String getUdidFromTestName(ITestContext context) {
    	String testName = context.getCurrentXmlTest().getName();
    	String deviceNum = testName.split(" ")[1];
    	StringBuilder envVariable = new StringBuilder();
    	envVariable.append("Device").append(deviceNum).append("_UDID");
    	return System.getenv(envVariable.toString());
    }
    
    private void setBrowserOS(String deviceOS) {
    	if (deviceOS.equalsIgnoreCase("android")) {
    		this.deviceOS = MobileOS.ANDROID;
    	} else if (deviceOS.equalsIgnoreCase("ios")) {
    		this.deviceOS = MobileOS.IOS;
    	}
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