package com.javatodo.core.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.javatodo.core.tools.Http;

public class ElasticSearch {
	private String ip = "";
	private Integer port = 9200;

	private String log = "";

	public ElasticSearch(String ip) {
		this.ip = ip;
	}

	public ElasticSearch(String ip, Integer port) {
		this.ip = ip;
		this.port = port;
	}

	public String getLog() {
		return log;
	}

	/**
	 * 新增或修改数据
	 * 
	 * @param _index
	 *            String ElasticSearch中的索引相当于数据库
	 * @param _type
	 *            String ElasticSearch中的类型相当于数据表
	 * @param _id
	 *            String 标识某个数据库、某个数据表中的唯一数据ID
	 * @param jsonString
	 *            String 提交的json数据
	 */
	public Boolean save(String _index, String _type, String _id, String jsonString) {
		try {
			URL httpurl = new URL(ip + ":" + port + "/" + _index + "/" + _type + "/" + _id);
			HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
			conn.setRequestMethod("POST");
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			String data = jsonString;
			conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(data.getBytes("UTF-8"));
			Integer code = conn.getResponseCode();
			if (code == 201 || code == 200) {
				InputStream inputStream = conn.getInputStream();
				this.log = this.stremToString(inputStream, "UTF-8");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 新增或修改数据
	 * 
	 * @param _index
	 *            String ElasticSearch中的索引相当于数据库
	 * @param _type
	 *            String ElasticSearch中的类型相当于数据表
	 * @param _id
	 *            String 标识某个数据库、某个数据表中的唯一数据ID
	 * @param map
	 *            Map<String, Object> 提交的数据
	 */
	public Boolean save(String _index, String _type, String _id, Map<String, Object> map) {
		String jsonString = JSON.toJSONString(map);
		return save(_index, _type, _id, jsonString);
	}

	/**
	 * 新增或修改数据
	 * 
	 * @param _index
	 *            String ElasticSearch中的索引相当于数据库
	 * @param _type
	 *            String ElasticSearch中的类型相当于数据表
	 * @param _id
	 *            String 标识某个数据库、某个数据表中的唯一数据ID
	 */
	public Boolean delete(String _index, String _type, String _id) {
		try {
			URL httpurl = new URL(ip + ":" + port + "/" + _index + "/" + _type + "/" + _id);
			HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
			conn.setRequestMethod("DELETE");
			conn.setReadTimeout(5000);
			conn.setDoOutput(true);
			conn.getOutputStream();
			Integer code = conn.getResponseCode();
			if (code == 201 || code == 200) {
				InputStream inputStream = conn.getInputStream();
				this.log = this.stremToString(inputStream, "UTF-8");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public JSONObject sqlQuery(String sql) {
		try {
			URL httpurl = new URL(ip + ":" + port + "/_xpack/sql?format=json");
			HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
			conn.setRequestMethod("POST");
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			JSONObject object = new JSONObject();
			object.put("query", sql);
			String data = object.toJSONString();
			conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(data.getBytes("UTF-8"));
			Integer code = conn.getResponseCode();
			if (code == 200) {
				InputStream inputStream = conn.getInputStream();
				String jsonString = this.stremToString(inputStream, "UTF-8");
				return JSONObject.parseObject(jsonString);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String query(String _index, String _type, String jsonString) {
		try {
			URL httpurl = new URL(ip + ":" + port + "/" + _index + "/" + _type + "/_search");
			HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
			conn.setRequestMethod("POST");
			conn.setReadTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			String data = jsonString;
			conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(data.getBytes("UTF-8"));
			Integer code = conn.getResponseCode();
			if (code == 200) {
				InputStream inputStream = conn.getInputStream();
				return this.stremToString(inputStream, "UTF-8");
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public JSONObject find(String _index, String _type, String _id) {
		Http http = new Http();
		String ret = http.get(ip + ":" + port + "/" + _index + "/" + _type + "/" + _id);
		if (ret == null) {
			return null;
		} else {
			return JSONObject.parseObject(ret);
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
