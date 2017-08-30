package framework;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class SeeTestHelpers {
	
	public synchronized static String getLocalBuildId() {
		Properties props = new Properties();
		File file = null;
		try {
			file = new File("buildid.properties");
			props.load(new FileInputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String buildId = props.getProperty("build.id");
		Integer updated = Integer.parseInt(buildId) + 1;
		props.setProperty("build.id", updated.toString());
		OutputStream out;
		try {
			out = new FileOutputStream(file);
			props.store(out,null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buildId;
	}
	
	public static void threadSleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static URL getUrl(String host, int port, boolean secured) {
		StringBuilder sb = new StringBuilder();
		sb.append("http").append(secured ? "s" : "").append("://").append(host).append(":").append(port).append("/wd/hub");
		URL server = null;
		try {
			server = new URL(sb.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return server;
	}

}
