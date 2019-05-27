package com.javatodo.core.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http {
	public Integer code = null;
	public String html = null;

	public String get(String url) {
		try {
			URL httpurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10 * 1000);
			// conn.setRequestProperty("APPTOKEN", "");
			this.code = conn.getResponseCode();
			if (code == 200) {
				InputStream inputStream = conn.getInputStream();
				this.html = this.stremToString(inputStream, "utf-8");
				return html;
			} else {
				return null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public String post(String url, String param) {
		try {
			URL httpurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
			conn.setRequestMethod("POST");
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// conn.setRequestProperty("APPTOKEN", "");
			String data = param;
			conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
			conn.setDoOutput(true);
			conn.getOutputStream().write(data.getBytes());
			this.code = conn.getResponseCode();
			if (code == 200) {
				InputStream inputStream = conn.getInputStream();
				this.html = this.stremToString(inputStream, "utf-8");
				return this.html;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	private String stremToString(InputStream is, String encoding) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString(encoding);
	}
}
