/**
 * Copyright (c) 2017, Wang Wenyuan 王文渊 (827287829@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.javatodo.core.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.javatodo.config.C;

public class T {
	/**
	 * 以yyyy-MM-dd HH:mm:ss格式获取当前时间
	 * 
	 * @return
	 */
	public static String now() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}

	/**
	 * 获取当前时间
	 * 
	 * @param format
	 *            String 时间格式
	 * @return
	 */
	public static String now(String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(new Date());
	}

	/**
	 * 时间转成时间戳
	 * 
	 * @param date
	 *            String 时间字符串
	 * @param format
	 *            String 时间格式
	 * @return 时间戳
	 * @throws ParseException
	 * 
	 */
	public static Integer strtotime(String date, String format) throws ParseException {
		if (format.trim().equals("")) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date dateFormat = simpleDateFormat.parse(date);
		long ts = dateFormat.getTime();
		Integer timestamp = (int) (ts / 1000);
		return timestamp;
	}

	/**
	 * 时间转日期
	 * 
	 * @param format
	 *            String 时间格式
	 * @param time
	 *            Integer 时间戳
	 * @return 时间字符串
	 */
	public static String date(String format, Integer time) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		String timestamp = time.toString() + "000";
		Date date = new Date(Long.valueOf(timestamp));
		return simpleDateFormat.format(date);
	}

	/**
	 * 时间转日期
	 * 
	 * @param format
	 *            String 时间格式
	 * @param time
	 *            Integer 时间戳
	 * @return 时间字符串
	 */
	public static String date(String format, Long time) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date date = new Date(time);
		return simpleDateFormat.format(date);
	}

	/**
	 * 获取当前时间戳
	 * 
	 * @return 当前时间戳
	 */
	public static Integer time() {
		long ts = System.currentTimeMillis();
		Integer timestamp = (int) (ts / 1000);
		return timestamp;
	}

	/**
	 * 对html特殊字符进行转义
	 * 
	 * @param string
	 *            获取转义后的html内容
	 * @return
	 */
	public static String htmlspecialchars(String string) {
		string = string.replaceAll("&", "&amp;");
		string = string.replaceAll("<", "&lt;");
		string = string.replaceAll(">", "&gt;");
		string = string.replaceAll("\"", "&quot;");
		string = string.replaceAll("'", "&apos;");
		return string;
	}

	/**
	 * 对使用T.htmlspecialchars(String string)进行转义后的内容再进行反转义
	 * 
	 * @param string
	 *            使用T.htmlspecialchars(String string)转义后的内容
	 * @return 反转义后的内容
	 */
	public static String htmlspecialchars_decode(String string) {
		string = string.replaceAll("&amp;", "&");
		string = string.replaceAll("&lt;", "<");
		string = string.replaceAll("&gt;", ">");
		string = string.replaceAll("&quot;", "\"");
		string = string.replaceAll("&apos;", "'");
		return string;
	}

	/**
	 * 对数据的格式进行检测
	 * 
	 * @param value
	 *            Object 数据
	 * @param type
	 *            String 格式（mobile、email、require）
	 * @return boolean true:说明数据格式正确；false:说明数据格式不正确
	 */
	public static boolean detect(Object value, String type) {
		boolean ret = false;
		if (value == null) {
			return ret;
		}
		String regExp = "";
		Pattern p = null;
		Matcher m = null;
		switch (type) {
		case "mobile":
			regExp = "^((13[0-9]|15[0-9]|17[0-9]|18[0-9])+\\d{8})$";
			break;
		case "email":
			regExp = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			break;
		case "require":
			if (value.toString().length() > 0) {
				return true;
			} else {
				return false;
			}
		default:
			regExp = type;
			break;
		}
		if (value.toString().length() == 0) {
			return false;
		}
		if (regExp.length() > 0) {
			p = Pattern.compile(regExp);
			m = p.matcher(value.toString());
			ret = m.find();
		}
		return ret;
	}

	/**
	 * 生成字符串的md5值
	 * 
	 * @param string
	 *            String 要进行md5加密的字符串
	 * @return String md5加密以后的字符串
	 */
	public static String md5(String string) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = string.getBytes("utf-8");
			// 使用MD5创建MessageDigest对象
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b = md[i];
				str[k++] = hexDigits[b >> 4 & 0xf];
				str[k++] = hexDigits[b & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 生成url地址
	 * 
	 * @param path
	 *            [模块名/控制器名/操作] 或 [控制器名/操作] 或 [操作]
	 * @return String 对应的url链接
	 */
	public static String U(String path, String entrance) {
		String url = "./" + entrance;
		String[] paths = path.split("/");
		if (paths.length == 3) {
			url = url + "?m=" + paths[0] + "&c=" + paths[1] + "&a=" + paths[2];
		}
		return url;
	}

	/**
	 * 生成url地址
	 * 
	 * @param map
	 *            Map<String 参数名,String 参数值> url中的参数（m：标识模块名；c：表示控制器名；a：表示操作名）
	 * @return String 对应的url链接
	 */
	public static String U(Map<String, String> map, String entrance) {
		String url = "./" + entrance;
		Integer i = 0;
		for (Entry<String, String> entry : map.entrySet()) {
			if (i == 0) {
				url = url + "?" + entry.getKey() + "=" + entry.getValue().toString();
			} else {
				url = url + "&" + entry.getKey() + "=" + entry.getValue().toString();
			}
			i = i + 1;
		}
		return url;
	}

	/**
	 * 生成url地址
	 * 
	 * @param path
	 *            [模块名/控制器名/操作] 或 [控制器名/操作] 或 [操作]
	 * @param param
	 *            map的json结构
	 * @return String 对应的url链接
	 */
	public static String UJ(String path, String param, String entrance) {
		String url = "./" + entrance;
		Map<String, Object> map = JSON.parseObject(param);
		String[] paths = path.split("/");
		if (paths.length == 3) {
			url = url + "?m=" + paths[0] + "&c=" + paths[1] + "&a=" + paths[2];
		}
		for (Entry<String, Object> entry : map.entrySet()) {
			url = url + "&" + entry.getKey() + "=" + entry.getValue().toString();
		}
		return url;
	}

	/**
	 * 生成url地址
	 * 
	 * @param path
	 *            [模块名/控制器名/操作] 或 [控制器名/操作] 或 [操作]
	 * @param param
	 *            参数，形如：“fra=javatodo&v=3.0”
	 * @return String 对应的url链接
	 */
	public static String U(String path, String param, String entrance) {
		String url = "./" + entrance;
		String[] paths = path.split("/");
		if (paths.length == 3) {
			url = url + "?m=" + paths[0] + "&c=" + paths[1] + "&a=" + paths[2];
		}
		url = url + "&" + param;
		return url;
	}

	/**
	 * 生成url地址
	 * 
	 * @param path
	 *            String [模块名/控制器名/操作] 或 [控制器名/操作] 或 [操作]
	 * @param map
	 *            Map<String 参数名,String 参数值> url中的参数
	 * @return String 对应的url链接
	 */
	public static String U(String path, Map<String, Object> map, String entrance) {
		String url = "./" + entrance;
		String[] paths = path.split("/");
		if (paths.length == 3) {
			url = url + "?m=" + paths[0] + "&c=" + paths[1] + "&a=" + paths[2];
		}
		for (Entry<String, Object> entry : map.entrySet()) {
			url = url + "&" + entry.getKey() + "=" + entry.getValue().toString();
		}
		return url;
	}

	public static Integer toInt(String string) {
		Integer integer = 0;
		try {
			integer = Integer.parseInt(string.trim());
		} catch (Exception e) {
			integer = 0;
		}
		return integer;
	}

	public static String toString(Object object) {
		if (object == null) {
			return "";
		} else {
			return object.toString();
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(Object object) {
		Map<String, Object> map = new HashMap<>();
		if (object == null) {
			return map;
		} else {
			return (Map<String, Object>) object;
		}
	}

	public static String json_encode(Object object) {
		String json = JSON.toJSONString(object);
		return json;
	}

	public static List<String> str_to_list(String string, String regex) {
		String[] strings = string.split(regex);
		List<String> list = new ArrayList<>();
		for (Integer i = 0; i < strings.length; i = i + 1) {
			list.add(strings[i]);
		}
		return list;
	}

	// 读取文件
	public static String readFile(String filePath, String charset) {
		try {
			String pathname = filePath; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
			File filename = new File(pathname); // 要读取以上路径的input。txt文件
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), charset); // 建立一个输入流对象reader
			BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String content = "";
			String line = "";
			line = br.readLine();
			if (line != null) {
				content = content + line;
			}
			while (line != null) {
				line = br.readLine(); // 一次读入一行数据
				if (line != null) {
					content = content + line;
				}
			}
			return content;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}

	// 覆盖文件内容
	public static void coverFile(String file_path, String content) {
		FileOutputStream fop = null;
		try {
			File file = new File(file_path);
			if (!file.getParentFile().isDirectory()) {
				new File(file.getParent()).mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			fop = new FileOutputStream(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			byte[] contentInBytes = content.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 向文件追加内容
	public static void writeFile(String file_path, String content) {
		FileOutputStream fop = null;
		try {
			File file = new File(file_path);
			if (!file.getParentFile().isDirectory()) {
				new File(file.getParent()).mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			fop = new FileOutputStream(file, true);
			if (!file.exists()) {
				file.createNewFile();
			}
			byte[] contentInBytes = content.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 记录日志
	public static void create_log(String log_file, String content) {
		writeFile(C.log_file_path + log_file, content + "--------" + T.now() + "\n\r");
	}

	// 删除目录（遍历删除目录内的文件以及子目录）
	public static void deleteDir(String dirPath) {
		File file = new File(dirPath);
		if (file.isFile()) {
			file.delete();
		} else {
			File[] files = file.listFiles();
			if (files == null) {
				file.delete();
			} else {
				for (int i = 0; i < files.length; i++) {
					deleteDir(files[i].getAbsolutePath());
				}
				file.delete();
			}
		}
	}

	// 删除文件
	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.isFile()) {
			file.delete();
		} else {
			deleteDir(filePath);
		}
	}

	// 获取ip地址
	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	// 生成主键
	public static String getPriKey() {
		String[] arr = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
		BigDecimal seed = new BigDecimal(System.currentTimeMillis());
		BigDecimal hexadecimal = new BigDecimal(36);
		List<String> retList = new ArrayList<>();
		while (seed.compareTo(hexadecimal) > 0) {
			BigDecimal[] res = seed.divideAndRemainder(hexadecimal);
			seed = res[0];
			retList.add(0, arr[res[1].intValue()]);
		}
		retList.add(0, arr[seed.intValue()]);
		String ret = "";
		for (Integer i = 0; i < retList.size(); i = i + 1) {
			ret = ret + retList.get(i).toString();
		}
		return ret;
	}

	// 数字转36进制
	public static String int36Hash(Integer n) {
		String[] arr = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
		BigDecimal seed = new BigDecimal(n);
		BigDecimal hexadecimal = new BigDecimal(36);
		List<String> retList = new ArrayList<>();
		while (seed.compareTo(hexadecimal) > 0) {
			BigDecimal[] res = seed.divideAndRemainder(hexadecimal);
			seed = res[0];
			retList.add(0, arr[res[1].intValue()]);
		}
		retList.add(0, arr[seed.intValue()]);
		String ret = "";
		for (Integer i = 0; i < retList.size(); i = i + 1) {
			ret = ret + retList.get(i).toString();
		}
		return ret;
	}

	// 获取map中的内容
	public static String getString(String key, Map<String, Object> info) {
		if (info == null) {
			return "";
		}
		if (info.containsKey(key)) {
			return info.get(key).toString();
		} else {
			return "";
		}
	}

	// 获取jsonObject中的内容
	public static String getString(String key, JSONObject info) {
		if (info == null) {
			return "";
		}
		if (info.containsKey(key) && info.get(key) != null) {
			return info.getString(key);
		} else {
			return "";
		}
	}

	// 获取配置文件中的内容
	public static String getProperties(String PropertiesFile, String Key) {
		String value = "";
		try {
			Properties properties = new Properties();
			InputStream in = new FileInputStream(PropertiesFile);
			properties.load(in);
			value = properties.getProperty(Key);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value.trim();
	}

	// 获取默认配置文件中的内容
	public static String getDefaultProperties(String Key) {
		return T.getProperties("config.properties", Key);
	}

	// 判断是否是手机浏览器
	public static boolean isMobile(HttpServletRequest request) {
		List<String> mobile_agents = Arrays.asList("240x320", "acer", "acoon", "acs-", "abacho", "ahong", "airness", "alcatel", "amoi", "android", "anywhereyougo.com", "applewebkit/525", "applewebkit/532", "asus", "audio", "au-mic", "avantogo", "becker", "benq", "bilbo", "bird", "blackberry", "blazer", "bleu", "cdm-", "compal", "coolpad", "danger", "dbtel", "dopod", "elaine", "eric", "etouch", "fly ", "fly_", "fly-", "go.web", "goodaccess", "gradiente", "grundig", "haier", "hedy", "hitachi", "htc", "huawei", "hutchison", "inno", "ipad", "ipaq", "iphone", "ipod", "jbrowser", "kddi", "kgt", "kwc", "lenovo", "lg ", "lg2", "lg3", "lg4", "lg5", "lg7", "lg8", "lg9", "lg-", "lge-", "lge9", "longcos", "maemo", "mercator", "meridian", "micromax", "midp", "mini", "mitsu", "mmm", "mmp", "mobi", "mot-", "moto", "nec-", "netfront", "newgen", "nexian", "nf-browser", "nintendo", "nitro", "nokia", "nook", "novarra", "obigo", "palm", "panasonic", "pantech", "philips", "phone", "pg-", "playstation", "pocket", "pt-", "qc-", "qtek", "rover", "sagem", "sama", "samu", "sanyo", "samsung", "sch-", "scooter", "sec-", "sendo", "sgh-", "sharp", "siemens", "sie-", "softbank", "sony", "spice", "sprint", "spv", "symbian", "tablet", "talkabout", "tcl-", "teleca", "telit", "tianyu", "tim-", "toshiba", "tsm", "up.browser", "utec", "utstar", "verykool", "virgin", "vk-", "voda", "voxtel", "vx", "wap", "wellco", "wig browser", "wii", "windows ce", "wireless", "xda", "xde", "zte");
		String user_agent = request.getHeader("User-Agent").toLowerCase();
		for (Integer i = 0; i < mobile_agents.size(); i = i + 1) {
			if (user_agent.contains(mobile_agents.get(i))) {
				return true;
			}
		}
		return false;
	}

	public static void javatodo_error_log(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw, true));
		String str = sw.toString();
		str = "\n==========================" + T.date("yyyy-MM-dd HH:mm:ss", T.time()) + "============================\n" + str;
		T.create_log("javatodo_error_" + T.date("yyyy-MM-dd", T.time()) + ".log", str);
	}

	public static void javatodo_sql_log(Connection connection, String sql) {
		String string = "\n" + "connect_id：" + connection.hashCode() + "\n";
		string = string + "sql语句：" + sql + "\n";
		T.create_log("javatodo_error_" + T.date("yyyy-MM-dd", T.time()) + ".log", string);
	}

	// 判断是否是微信浏览器
	public static boolean isWeixin(HttpServletRequest request) {
		String user_agent = request.getHeader("User-Agent").toLowerCase();
		if (user_agent.contains("micromessenger")) {
			return true;
		} else {
			return false;
		}
	}
}
