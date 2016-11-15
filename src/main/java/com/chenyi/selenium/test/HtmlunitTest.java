package com.chenyi.selenium.test;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class HtmlunitTest {
	public static void main(String[] args) {
		String PROXY = "localhost:8580";

		org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
		proxy.setHttpProxy(PROXY).setFtpProxy(PROXY).setSslProxy(PROXY);
		DesiredCapabilities cap = new DesiredCapabilities();
		cap.setCapability(CapabilityType.PROXY, proxy);

		HtmlUnitDriver driver = new HtmlUnitDriver(cap);
		driver.setJavascriptEnabled(true);
		// And now use this to visit Google
		// driver.get("https://www.google.com");
		String url = "https://www.baidu.com";
		url = "https://www.youtube.com/watch?v=QEzlsjAqADA";
		driver.get(url);
		// Alternatively the same thing can be done like this
		// driver.navigate().to("http://www.google.com");

		Object a = ((JavascriptExecutor) driver).executeScript("return document.title;");
		System.out.println(a);
		Object b = ((JavascriptExecutor) driver).executeScript("return yt.config_.TTS_URL.length");
		System.out.println(b);

		// Close the browser
		driver.quit();
	}
}
