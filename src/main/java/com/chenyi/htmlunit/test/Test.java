package com.chenyi.htmlunit.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.LogFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.InteractivePage;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;

public class Test {
	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");

		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

		System.out.println("1\t" + new Date());
		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME, "127.0.0.1", 8580)) {
			System.out.println("2\t" + new Date());
			String url = "http://htmlunit.sourceforge.net";
			url = "https://www.youtube.com/watch?v=QEzlsjAqADA";
			// webClient.waitForBackgroundJavaScript(10000);
			// webClient.setThrowExceptionOnScriptError(false);
			webClient.getCookieManager().setCookiesEnabled(false);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setCssEnabled(false);

			webClient.setIncorrectnessListener(new IncorrectnessListener() {
				@Override
				public void notify(String arg0, Object arg1) {
					System.out.println("test notify");

				}
			});
			webClient.setJavaScriptErrorListener(new ErrorListener());

			System.out.println("3\t" + new Date());
			new InterceptWebConnection(webClient);
			// webClient.getOptions().setJavaScriptEnabled(false);

			getLink(webClient, url);
		}
	}

	static String getLink(WebClient webClient, String url) {
		try {
			final HtmlPage page = webClient.getPage(url);
			System.out.println("4\t" + new Date());
			// Assert.assertEquals("HtmlUnit - Welcome to HtmlUnit",
			// page.getTitleText());
			System.out.println(page.getTitleText());
			ScriptResult result = page.executeJavaScript("yt.config_.TTS_URL");
			System.out.println(result.getJavaScriptResult());
			System.out.println(result.toString());

			// final String pageAsXml = page.asXml();
			// Assert.assertTrue(pageAsXml.contains("<body class=\"composite\">"));
			// System.out.println(pageAsXml.contains("<body class=\"composite\">"));

			// final String pageAsText = page.asText();
			// Assert.assertTrue(pageAsText.contains("Support for the HTTP and HTTPS protocols"));
			// System.out.println(pageAsText.contains("Support for the HTTP and HTTPS protocols"));
		} catch (Exception e) {
			System.out.println("exception" + e.getMessage());
		}
		return null;
	}

	static class ErrorListener implements JavaScriptErrorListener {

		@Override
		public void scriptException(InteractivePage page, ScriptException scriptException) {
			System.err.println(scriptException.getMessage());
			// System.out.println("test error listener");
		}

		@Override
		public void timeoutError(InteractivePage page, long allowedTime, long executionTime) {

		}

		@Override
		public void malformedScriptURL(InteractivePage page, String url, MalformedURLException malformedURLException) {

		}

		@Override
		public void loadScriptError(InteractivePage page, URL scriptUrl, Exception exception) {

		}
	}

	static class InterceptWebConnection extends FalsifyingWebConnection {

		public boolean match(String line, String pattern) {
			// line = "www-en_UsS-vflmvrsdfsdkJC/base.js";
			// pattern = "www-en_US-.*/base.js";

			// Create a Pattern object
			Pattern r = Pattern.compile(pattern);

			// Now create matcher object.
			Matcher m = r.matcher(line);
			if (m.find()) {
				// System.out.println("Found value: " + m.group(0));
				return true;
			}
			return false;
		}

		public InterceptWebConnection(WebClient webClient) throws IllegalArgumentException {
			super(webClient);
		}

		String[] siArr = new String[] { "https://www.youtube.com/watch?v=", "spf.js3" };
		// reg pattern
		String[] piArr = new String[] { "www-en_US-.*/base.js" };

		@Override
		public WebResponse getResponse(WebRequest request) throws IOException {
			// WebResponse response = super.getResponse(request);
			// String includeUrl = "https://www.youtube.com/watch?v=";
			String reqUrl = request.getUrl().toString();
			boolean include = false;
			for (String includeUrl : siArr) {
				if (reqUrl.indexOf(includeUrl) > -1) {
					include = true;
					break;
				}
			}
			if (!include) {
				for (String pattern : piArr) {
					if (match(reqUrl, pattern)) {
						include = true;
						break;
					}
				}
			}
			if (!include) {
				// System.out.println(reqUrl);
				// return createWebResponse(response.getWebRequest(), "",
				// "application/javascript", 200, "Ok");
				return createWebResponse(request, "", "application/javascript");
			} else {
				System.out.println(reqUrl);
			}
			return super.getResponse(request);
		}
	}
}
