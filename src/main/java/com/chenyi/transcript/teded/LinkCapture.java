package com.chenyi.transcript.teded;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import com.chenyi.transcript.CaptureUtil;
import com.gargoylesoftware.htmlunit.WebClient;

public class LinkCapture {
	private static String dirPath = "E:/langeasy/lucene/podcast/ted-ed/";
	private static JSONArray episodeList;
	private static ArrayList<JSONObject> downloadLst = new ArrayList<>();
	private static int[] jobStatus;

	public static void main(String[] args) throws Exception {
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");

		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

		File sFile = new File(dirPath + "episode-list.json");
		// sFile = new File(dirPath + "test.json");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");

		// String sResult = new String(result, "utf-8");
		// sResult = StringEscapeUtils.unescapeJava(sResult);
		// System.out.println(sResult);
		episodeList = new JSONArray(sResult);
		// System.out.println(episodeList);

		for (int i = 0; i < episodeList.length(); i++) {
			JSONObject episode = episodeList.getJSONObject(i);

			String videoid = episode.getString("videoid");
			String saveFilePath = dirPath + "transcript/" + videoid + ".xml";
			File saveFile = new File(saveFilePath);
			if (saveFile.exists()) {
				continue;
			}
			System.out.println(videoid);
			episode.put("index", i);// easy for matching later
			downloadLst.add(episode);
		}

		int total = downloadLst.size();
		System.out.println(total);
		int count = 2;
		step = total / count;
		if (total % step != 0) {
			count += 3;
		}
		step = 1;
		count = total;
		System.out.println(step + "\t" + count);
		if (total > -1) {
			return;
		}

		jobStatus = new int[count];
		int start = 0;
		LinkCapture downloader = new LinkCapture();
		for (int i = start; i < count; i++) {
			Job job = downloader.new Job(i);
			jobStatus[i] = 0;
			job.start();
			if (i > 1) {
				// return;
			}
		}
		for (int i = 0; i < 200; i++) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			boolean allFinished = true;
			for (int j = start; j < count; j++) {
				if (jobStatus[j] == 0) {
					allFinished = false;
					break;
				}
			}
			System.out.println(new JSONArray(jobStatus));
			if (allFinished) {
				// FileUtils.writeStringToFile(sFile, episodeList.toString(3),
				// StandardCharsets.UTF_8);
				break;
			}
		}

	}

	private static int step = 30;

	class Job implements Runnable {
		private Thread t;
		private int jobIndex;

		Job(int jobIndex) {
			this.jobIndex = jobIndex;
			System.out.println("Creating job " + jobIndex);
		}

		public void run() {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			WebClient webClient = LinkHelper.getWebClient();
			int start = jobIndex * step;
			int end = start + step;
			if (end > downloadLst.size()) {
				end = downloadLst.size();
			}
			System.out.println(start + "\t" + end);

			List<JSONObject> subLst = downloadLst.subList(start, end);
			int count = 0;
			for (JSONObject episode : subLst) {
				count++;
				System.err.println("job" + jobIndex + " download seq : " + count);
				try {
					String videoid = episode.getString("videoid");
					parse(httpclient, webClient, videoid);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (count > 3) {
					break;
				}
			}
			try {
				httpclient.close();
				webClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			jobStatus[jobIndex] = 1;
			System.out.println("job" + jobIndex + " last time is : " + new Date());
		}

		public void start() {
			System.out.println("Starting job " + jobIndex);
			if (t == null) {
				t = new Thread(this, "job" + jobIndex);
				t.start();
			}
		}

	}

	private static void parse(CloseableHttpClient httpclient, WebClient webClient, String videoid) throws Exception {
		String saveFilePath = dirPath + "transcript/" + videoid + ".xml";
		System.out.println(saveFilePath + ", start time is : " + new Date());
		long start = System.currentTimeMillis();

		File saveFile = new File(saveFilePath);
		String url = "https://www.youtube.com/watch?v=" + videoid;
		System.out.println(url);

		String turl = LinkHelper.getLink(webClient, url);

		HttpGet httpget = new HttpGet(turl);
		HttpResponse response = CaptureUtil.timeoutRequest(httpclient, httpget, 500, 7);
		if (response == null) {
			return;
		}
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			long startTime = System.currentTimeMillis();
			System.out.println(turl + " download begin");
			InputStream inputStream = entity.getContent();
			OutputStream outputStream = new FileOutputStream(saveFile);
			IOUtils.copy(inputStream, outputStream);
			outputStream.close();
			System.out.println(turl + " download success");
			long endTime = System.currentTimeMillis();
			System.out.println("download time elapsed: " + (endTime - startTime));
		}
		long end = System.currentTimeMillis();
		System.out.println(saveFilePath + ", consuming seconds : " + (end - start));
	}

}
