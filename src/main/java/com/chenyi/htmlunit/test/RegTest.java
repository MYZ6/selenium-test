package com.chenyi.htmlunit.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegTest {
	public static void main(String[] args) {
		match("", "");
	}

	public static boolean match(String line, String pattern) {
		line = "https://www.youtube.com/watch?v=";
		pattern = "https://www.youtube.com/watch?v=";

		// Create a Pattern object
		Pattern r = Pattern.compile(pattern);

		// Now create matcher object.
		Matcher m = r.matcher(line);
		if (m.find()) {
			System.out.println("Found value: " + m.group(0));
			return true;
		}
		return false;
	}
}
