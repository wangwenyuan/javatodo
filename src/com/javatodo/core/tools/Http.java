package com.javatodo.core.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class Http {
	public Integer code = null;
	public String html = null;

	public String get(String url) {
		try {
			URL httpurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10 * 1000);
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

	public String get(String url, Map<String, String> header) {
		try {
			URL httpurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10 * 1000);
			for (String key : header.keySet()) {
				conn.setRequestProperty(key, header.get(key));
			}
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
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			String data = param;
			conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(data.getBytes("UTF-8"));
			this.code = conn.getResponseCode();
			if (code == 200) {
				InputStream inputStream = conn.getInputStream();
				this.html = this.stremToString(inputStream, "UTF-8");
				return this.html;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String post(String url, Map<String, String> header, String param) {
		try {
			URL httpurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
			conn.setRequestMethod("POST");
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			for (String key : header.keySet()) {
				conn.setRequestProperty(key, header.get(key));
			}
			String data = param;
			conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(data.getBytes("UTF-8"));
			this.code = conn.getResponseCode();
			if (code == 200) {
				InputStream inputStream = conn.getInputStream();
				this.html = this.stremToString(inputStream, "UTF-8");
				return this.html;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String post(String url, Map<String, String> header, Map<String, String> param) {
		String paramString = "";
		for (String key : param.keySet()) {
			if (paramString.equals("")) {
				paramString = key + "=" + param.get(key);
			} else {
				paramString = paramString + "&" + key + "=" + param.get(key);
			}
		}
		return post(url, header, paramString);
	}

	public String post(String url, Map<String, String> param) {
		String paramString = "";
		for (String key : param.keySet()) {
			if (paramString.equals("")) {
				paramString = key + "=" + param.get(key);
			} else {
				paramString = paramString + "&" + key + "=" + param.get(key);
			}
		}
		return post(url, paramString);
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
