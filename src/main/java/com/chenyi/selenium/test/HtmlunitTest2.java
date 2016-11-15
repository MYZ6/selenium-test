package com.chenyi.selenium.test;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class HtmlunitTest2 {
	public static void main(String[] args) {
		String PROXY = "localhost:8580";

		// start the proxy
		BrowserMobProxy proxy = new BrowserMobProxyServer();
		proxy.start(0);

		// get the Selenium proxy object
		Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

		// configure it as a desired capability
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);

		HtmlUnitDriver driver = new HtmlUnitDriver(capabilities);
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
