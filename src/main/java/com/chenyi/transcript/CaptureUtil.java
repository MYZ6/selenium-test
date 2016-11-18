package com.chenyi.transcript;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CaptureUtil {

	static final String DB_URL = "jdbc:mysql://localhost:3306/langeasy";
	static final String USER = "root";
	static final String PASS = "";

	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return conn;
	}

	public static void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Document timeoutRequest(String url) {
		Document doc = null;
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36";
		try {
			doc = Jsoup.connect(url).userAgent(userAgent).get();
		} catch (SocketTimeoutException ex) {
			System.out.println("url " + url + " read timeout");
			ex.printStackTrace();
			doc = timeoutRequest(url);// try again recursively
			// throw ex;
		} catch (HttpStatusException ex) {
			if (404 == ex.getStatusCode()) {
				System.out.println("url " + url + " 404");
				ex.printStackTrace();
			}
			if (403 == ex.getStatusCode()) {
				System.err.println("url " + url + " 403");
				ex.printStackTrace();
			}
			// throw ex;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	public static Document timeoutRequest(String url, int interval) {
		Document doc = null;
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36";
		try {
			doc = Jsoup.connect(url).userAgent(userAgent).get();
		} catch (SocketTimeoutException ex) {
			System.out.println("url " + url + " read timeout");
			ex.printStackTrace();
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			doc = timeoutRequest(url, interval);// try again recursively
			// throw ex;
		} catch (HttpStatusException ex) {
			if (404 == ex.getStatusCode()) {
				System.out.println("url " + url + " 404");
				ex.printStackTrace();
			}
			if (403 == ex.getStatusCode()) {
				System.err.println("url " + url + " 403");
				ex.printStackTrace();
			}
			// throw ex;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	public static HttpResponse timeoutRequest(CloseableHttpClient httpclient, HttpGet httpget, int interval,
			int retryTime) {
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (ClientProtocolException ex) {
			ex.printStackTrace();
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (retryTime > 0) {
				// try again recursively
				response = timeoutRequest(httpclient, httpget, interval, retryTime - 1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

}
