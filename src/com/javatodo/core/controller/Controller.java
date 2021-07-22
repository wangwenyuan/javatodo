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
package com.javatodo.core.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import com.javatodo.config.C;
import com.javatodo.core.router.RC;
import com.javatodo.core.router.Router;
import com.javatodo.core.tools.Captcha;
import com.javatodo.core.tools.Page;
import com.javatodo.core.tools.T;
import com.javatodo.core.view.FreeMakerView;
import com.javatodo.core.view.JspView;
import com.javatodo.core.view.VelocityView;
import com.javatodo.core.view.View;

import freemarker.template.TemplateException;

public class Controller {
	public boolean IS_POST = false;
	public HttpServletRequest request;
	public HttpServletResponse response;
	public HttpServlet servlet;
	public View view;
	public Map<String, String> routerMap = null;
	public String APP_NAME = "";// app名称
	public String MODULE_NAME = "";// 显示的模块名
	public String CONTROLLER_NAME = "";// 显示的控制器名
	public String ACTION_NAME = "";// 显示的方法名
	public String PACKAGE_NAME = "";// 真正的模块名
	public String CLASS_NAME = "";// 真正的控制器名
	public String FUNCTION_NAME = "";// 真正的方法名
	public boolean IS_AJAX = false;
	public String templatePath = C.default_template_path;
	public String ROOT = "";
	public String PUBLIC = "";
	private Map<String, Object> assignMap = new HashMap<>();
	private String templateSuffix = ".html";
	private String entrance = "";

	public void setRequestAndResponse(HttpServletRequest request, HttpServletResponse response, HttpServlet servlet, String app) {
		this.APP_NAME = app;
		this.setParam(request, response, servlet);
		this.setRouter(request);
		this.response.setHeader("X-Powered-By", "JavaToDo");
		if (C.is_debug && C.log_file_path.equals("")) {
			C.log_file_path = servlet.getServletContext().getRealPath("/") + "WEB-INF/Runtime/log/";
		}
		if(C.cache_path.equals("")) {
			C.cache_path = servlet.getServletContext().getRealPath("/") + "WEB-INF/Runtime/cache/";
		}
	}

	private void setParam(HttpServletRequest request, HttpServletResponse response, HttpServlet servlet) {
		this.request = request;
		this.response = response;
		this.servlet = servlet;
		if ("POST".equals(request.getMethod())) {
			this.IS_POST = true;
		}
		if (this.request.getHeader("X-Requested-With") == null) {
			this.IS_AJAX = false;
		} else {
			if (this.request.getHeader("X-Requested-With").toString().equals("XMLHttpRequest")) {
				this.IS_AJAX = true;
			} else {
				this.IS_AJAX = false;
			}
		}
		this.ROOT = request.getContextPath();
		this.PUBLIC = ROOT + "/" + C.default_template_public;
		// 设置模版
		if ("velocity".equals(C.template_engines)) {
			view = new VelocityView();
			this.templateSuffix = ".html";
		}
		if ("jsp".equals(C.template_engines)) {
			view = new JspView(request);
			this.templateSuffix = ".jsp";
		}
		if ("freemaker".equals(C.template_engines)) {
			view = new FreeMakerView();
			this.templateSuffix = ".html";
		}
		// 设置入口文件
		String url = request.getRequestURI();
		String[] urlArr = url.split("/");
		if (url.contains(".jsp")) {
			this.entrance = urlArr[urlArr.length - 1];
		} else {
			this.entrance = "index.jsp";
		}
	}

	private void setRouter(HttpServletRequest request) {
		// 设置路由参数
		Router router = new Router(request, this.APP_NAME);
		this.MODULE_NAME = router.MODULE_NAME;
		this.CONTROLLER_NAME = router.CONTROLLER_NAME;
		this.ACTION_NAME = router.ACTION_NAME;

		this.PACKAGE_NAME = router.PACKAGE_NAME;
		this.CLASS_NAME = router.CLASS_NAME;
		this.FUNCTION_NAME = router.FUNCTION_NAME;

		this.routerMap = router.map;
	}

	public Boolean init() {
		return true;
	}

	/**
	 * 前置操作
	 * 
	 * @throws Exception
	 */
	public void _before() throws Exception {

	}

	/**
	 * 后置操作
	 * 
	 * @throws Exception
	 */
	public void _after() throws Exception {

	}

	/**
	 * 获取session的值
	 * 
	 * @param name
	 *            String session的名称
	 * @return session中该名称对应的值
	 */
	public Object session(String name) {
		if (name == null) {
			Enumeration<String> sNames = request.getSession().getAttributeNames();
			while (sNames.hasMoreElements()) {
				request.getSession().removeAttribute(sNames.nextElement().toString());
			}
			return true;
		} else {
			HttpSession session = request.getSession();
			Object value = session.getAttribute(name);
			if (value == null) {
				value = "";
			}
			return value;
		}
	}

	/**
	 * 设置session的值
	 * 
	 * @param name
	 *            String session的名称
	 * @param value
	 *            Object session的值
	 * @return 没有返回值
	 */
	public void session(String name, Object value) {
		if (value == null) {
			HttpSession session = request.getSession();
			session.removeAttribute(name);
		} else {
			HttpSession session = request.getSession();
			session.setAttribute(name, value);
		}
	}

	/**
	 * 获取cookie的值
	 * 
	 * @param name
	 *            String cookie的名称
	 * @return
	 */
	public String cookie(String name) {
		String value = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().toString().equals(name)) {
				value = cookie.getValue();
				break;
			}
		}
		return value;
	}

	/**
	 * 设置cookie的值
	 * 
	 * @param name
	 *            String cookie的名称
	 * @param value
	 *            String cookie的值
	 */
	public void cookie(String name, String value) {
		if (value == null) {
			Cookie cookie = new Cookie(name, value);
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		} else {
			Cookie cookie = new Cookie(name, value);
			cookie.setMaxAge(3600);
			response.addCookie(cookie);
		}
	}

	/**
	 * 设置cookie的值
	 * 
	 * @param name
	 *            String cookie的名称
	 * @param value
	 *            String cookie的值
	 * @param expiry
	 *            int cookie的存活时间，单位(秒)
	 */
	public void cookie(String name, String value, int expiry) {
		if (value == null) {
			Cookie cookie = new Cookie(name, value);
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		} else {
			Cookie cookie = new Cookie(name, value);
			cookie.setMaxAge(expiry);
			response.addCookie(cookie);
		}
	}

	/**
	 * 设置cookie的值
	 * 
	 * @param name
	 *            String cookie的名称
	 * @param value
	 *            String cookie的值
	 * @param domain
	 *            String cookie的作用域
	 */
	public void cookie(String name, String value, String domain) {
		if (value == null) {
			Cookie cookie = new Cookie(name, value);
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		} else {
			Cookie cookie = new Cookie(name, value);
			cookie.setDomain(domain);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
	}

	/**
	 * 设置cookie的值
	 * 
	 * @param name
	 *            String cookie的名称
	 * @param value
	 *            String cookie的值
	 * @param expiry
	 *            int cookie的有效时间
	 * @param domain
	 *            String cookie的作用域
	 */
	public void cookie(String name, String value, int expiry, String domain) {
		if (value == null) {
			Cookie cookie = new Cookie(name, value);
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		} else {
			Cookie cookie = new Cookie(name, value);
			cookie.setMaxAge(expiry);
			cookie.setDomain(domain);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
	}

	/**
	 * 获取系统变量或用户提交的数据
	 * 
	 * @return Map<String 变量名,String 变量值>
	 */
	public Map<String, String> I() {
		Map<String, String> map = this.routerMap;
		Map<String, String> retMap = new HashMap<>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (entry.getKey().toString().equals("m") || entry.getKey().toString().equals("c") || entry.getKey().toString().equals("a")) {
				continue;
			} else {
				retMap.put(entry.getKey().toString(), T.htmlspecialchars(entry.getValue().toString()));
			}
		}
		return retMap;
	}

	/**
	 * 获取系统变量或用户提交的数据
	 * 
	 * @param name
	 *            String 变量名
	 * @return String 变量值
	 */
	public String I(String name) {
		String string = "";
		if (routerMap.containsKey(name)) {
			string = routerMap.get(name);
		} else {
			string = "";
		}
		string = T.htmlspecialchars(string);
		return string;
	}

	/**
	 * 生成url地址
	 * 
	 * @param path
	 *            [模块名/控制器名/操作] 或 [控制器名/操作] 或 [操作]
	 * @return String 对应的url链接
	 */
	public String U(String path) {
		Map<String, String> map = this.routerMap;
		String url = "";
		String[] paths = path.split("/");
		String root_path = request.getRequestURI();
		if (paths.length == 3) {
			url = root_path + "?m=" + paths[0] + "&c=" + paths[1] + "&a=" + paths[2];
		}
		if (paths.length == 2) {
			url = root_path + "?m=" + map.get("m") + "&c=" + paths[0] + "&a=" + paths[1];
		}
		if (paths.length == 1) {
			url = root_path + "?m=" + map.get("m") + "&c=" + map.get("c") + "&a=" + paths[0];
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
	public String U(Map<String, String> map) {
		String url = request.getRequestURI();
		if (map.containsKey("m") && map.containsKey("c") && map.containsKey("a")) {
			String paramUrl = "";
			Integer i = 0;
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (i == 0) {
					paramUrl = "?" + entry.getKey() + "=" + entry.getValue();
				} else {
					paramUrl = paramUrl + "&" + entry.getKey() + "=" + entry.getValue();
				}
				i = i + 1;
			}
			url = url + paramUrl;
		}
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
	public String U(String path, Map<String, Object> map) {
		String url = "";
		String[] paths = path.split("/");
		String root_path = request.getRequestURI();
		if (paths.length == 3) {
			url = root_path + "?m=" + paths[0] + "&c=" + paths[1] + "&a=" + paths[2];
		}
		if (paths.length == 2) {
			url = root_path + "?m=" + this.routerMap.get("m") + "&c=" + paths[0] + "&a=" + paths[1];
		}
		if (paths.length == 1) {
			url = root_path + "?m=" + this.routerMap.get("m") + "&c=" + this.routerMap.get("c") + "&a=" + paths[0];
		}
		for (Entry<String, Object> entry : map.entrySet()) {
			url = url + "&" + entry.getKey() + "=" + entry.getValue().toString();
		}
		return url;
	}

	/**
	 * 页面重定向
	 * 
	 * @param url
	 *            String 页面重定向的链接
	 * @throws IOException
	 */
	public void redirect(String url) throws IOException {
		response.sendRedirect(url);
	}

	/**
	 * 生成验证码
	 * 
	 * @throws IOException
	 */
	public void Verify() throws IOException {
		response.setContentType("image/jpeg");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		Captcha captcha = new Captcha();
		session("verify_code", captcha.getCode());
		session("verify_time", System.currentTimeMillis());
		captcha.write(response.getOutputStream());
	}

	/**
	 * 检测验证码是否正确
	 * 
	 * @param code
	 *            用户输入的验证码的值
	 * @return boolean true:正确；false:错误
	 */
	public boolean check_verify(String code) {
		boolean b = false;
		String verify_code = session("verify_code").toString();
		long dtime = System.currentTimeMillis() - Long.parseLong(session("verify_time").toString());
		if (code.toLowerCase().equals(verify_code.toLowerCase()) && dtime < 1000 * 60 * 30) {
			b = true;
		} else {
			b = false;
			session("verify_code", null);
			session("verify_time", null);
		}
		return b;
	}

	/**
	 * 给模版赋值
	 * 
	 * @param name
	 *            String 模版中变量的名称
	 * @param value
	 *            Object 模版中变量的值
	 */
	public void assign(String name, Object value) {
		this.assignMap.put(name, value);
		this.view.assign(name, value);
	}

	/**
	 * 定义模版常量
	 */
	private void tempConstant() {
		this.view.assign("IS_POST", IS_POST);
		this.view.assign("IS_AJAX", IS_AJAX);
		this.view.assign("MODULE_NAME", MODULE_NAME);
		this.view.assign("CONTROLLER_NAME", CONTROLLER_NAME);
		this.view.assign("ACTION_NAME", ACTION_NAME);
		this.view.assign("ROOT", ROOT);
		this.view.assign("PUBLIC", PUBLIC);
		this.view.assign("controller", this);
		this.view.assign("input", routerMap);
	}

	private String parseJsp(String path) throws ServletException, IOException {
		ServletContext sc = this.servlet.getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher(path);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final ServletOutputStream stream = new ServletOutputStream() {
			public void write(byte[] data, int offset, int length) {
				os.write(data, offset, length);
			}

			public void write(int b) throws IOException {
				os.write(b);
			}

			@Override
			public boolean isReady() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void setWriteListener(WriteListener arg0) {
				// TODO Auto-generated method stub

			}
		};
		final PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
		HttpServletResponse rep = new HttpServletResponseWrapper(response) {
			public ServletOutputStream getOutputStream() {
				return stream;
			}

			public PrintWriter getWriter() {
				return pw;
			}
		};
		rd.include(request, rep);
		pw.flush();
		return os.toString();
	}

	/**
	 * 获取当前模版解析后的内容
	 * 
	 * @return 模版解析后的内容
	 * @throws ServletException
	 * @throws IOException
	 * @throws TemplateException
	 */
	public String parse() throws ServletException, IOException, TemplateException {
		String path = "";
		this.tempConstant();
		if (this.templateSuffix.equals(".jsp")) {
			path = "/" + this.templatePath;
			path = path + "/" + PACKAGE_NAME + "/" + CLASS_NAME + "/" + FUNCTION_NAME + this.templateSuffix;
			return this.parseJsp(path);
		} else {
			path = servlet.getServletContext().getRealPath("/") + this.templatePath;
			path = path + "\\" + PACKAGE_NAME + "\\" + CLASS_NAME + "\\" + FUNCTION_NAME + this.templateSuffix;
			return this.view.parseString(path, routerMap.get("m").toString() + "." + routerMap.get("c").toString() + "." + routerMap.get("a").toString() + ".log");
		}
	}

	/**
	 * 获取对应的模版解析以后的内容
	 * 
	 * @param path
	 * @return String [模块名/控制器名/操作] 或 [控制器名/操作] 或 [操作]
	 * @throws ServletException
	 * @throws IOException
	 * @throws TemplateException
	 */
	public String parse(String path) throws ServletException, IOException, TemplateException {
		String root_path = "";
		if (this.templateSuffix.equals(".jsp")) {
			root_path = "/" + this.templatePath;
		} else {
			root_path = servlet.getServletContext().getRealPath("/") + this.templatePath;
		}
		String[] paths = path.split("/");
		Map<String, String> map = new HashMap<>();

		if (paths.length == 3) {
			map.put("m", RC.getRC(paths[0]));
			map.put("c", RC.getRC(paths[0] + "!--javatodo--!" + paths[1]));
			map.put("a", RC.getRC(paths[0] + "!--javatodo--!" + paths[1] + "!--javatodo--!" + paths[2]));
		}
		if (paths.length == 2) {
			map.put("m", MODULE_NAME);
			map.put("c", RC.getRC(MODULE_NAME + "!--javatodo--!" + paths[0]));
			map.put("a", RC.getRC(MODULE_NAME + "!--javatodo--!" + paths[0] + "!--javatodo--!" + paths[1]));
		}
		if (paths.length == 1) {
			map.put("m", MODULE_NAME);
			map.put("m", CONTROLLER_NAME);
			map.put("a", RC.getRC(MODULE_NAME + "!--javatodo--!" + CONTROLLER_NAME + "!--javatodo--!" + paths[0]));
		}

		path = root_path + "\\" + map.get("m").toString() + "\\" + map.get("c").toString() + "\\" + map.get("a").toString() + this.templateSuffix;
		this.tempConstant();

		if (this.templateSuffix.equals(".jsp")) {
			return this.parseJsp(path);
		} else {
			return this.view.parseString(path, routerMap.get("m").toString() + "." + routerMap.get("c").toString() + "." + routerMap.get("a").toString() + ".log");
		}
	}

	/**
	 * 渲染模版
	 * 
	 * @throws IOException
	 * @throws ServletException
	 * @throws TemplateException
	 */
	public void display() throws IOException, ServletException, TemplateException {
		String path = "";
		if (this.templateSuffix.equals(".jsp")) {
			path = "/" + this.templatePath;
		} else {
			path = servlet.getServletContext().getRealPath("/") + this.templatePath;
		}
		path = path + "\\" + PACKAGE_NAME + "\\" + CLASS_NAME + "\\" + FUNCTION_NAME + this.templateSuffix;
		this.tempConstant();
		this.view.flush(request, response, servlet, path);
	}

	/**
	 * 渲染模版
	 * 
	 * @param path
	 *            String [模块名/控制器名/操作] 或 [控制器名/操作] 或 [操作]
	 * @throws IOException
	 * @throws ServletException
	 * @throws TemplateException
	 */
	public void display(String path) throws IOException, ServletException, TemplateException {
		String root_path = "";
		if (this.templateSuffix.equals(".jsp")) {
			root_path = "/" + this.templatePath;
		} else {
			root_path = servlet.getServletContext().getRealPath("/") + this.templatePath;
		}
		String[] paths = path.split("/");
		Map<String, String> map = new HashMap<>();
		if (paths.length == 3) {
			if ("".equals(this.APP_NAME)) {
				map.put("package_name", RC.getRC(paths[0]));
			} else {
				map.put("package_name", this.APP_NAME + "." + RC.getRC(paths[0]));
			}
			map.put("class_name", RC.getRC(paths[0], paths[1]) + "Controller");
			map.put("function_name", RC.getRC(paths[0], paths[1], paths[2]) + "Page");
		}
		if (paths.length == 2) {
			map.put("package_name", PACKAGE_NAME);
			map.put("class_name", RC.getRC(MODULE_NAME, paths[0]) + "Controller");
			map.put("function_name", RC.getRC(MODULE_NAME, paths[0], paths[1]) + "Page");
		}
		if (paths.length == 1) {
			map.put("package_name", PACKAGE_NAME);
			map.put("class_name", CLASS_NAME);
			map.put("function_name", RC.getRC(MODULE_NAME, CONTROLLER_NAME, paths[0]) + "Page");
		}
		path = root_path + "\\" + map.get("package_name").toString() + "\\" + map.get("class_name").toString() + "\\" + map.get("function_name").toString() + this.templateSuffix;
		this.tempConstant();
		this.view.flush(request, response, servlet, path);
	}

	// 以json方式输出内容
	public void jsonDisplay() {
		String json = JSON.toJSONString(this.assignMap);
		PrintWriter writer = null;
		try {
			this.response.setHeader("Pragma", "no-cache");
			this.response.setHeader("Cache-Control", "no-cache");
			this.response.setDateHeader("Expires", 0);
			this.response.setContentType("application/json; charset=" + C.default_encoding);
			writer = response.getWriter();
			writer.write(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	// 以json方式输出内容
	public void jsonDisplay(String text) {
		String json = text;
		PrintWriter writer = null;
		try {
			this.response.setHeader("Pragma", "no-cache");
			this.response.setHeader("Cache-Control", "no-cache");
			this.response.setDateHeader("Expires", 0);
			this.response.setContentType("application/json; charset=" + C.default_encoding);
			writer = response.getWriter();
			writer.write(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	// 以html方式输出内容
	public void htmlDisplay(String html) {
		PrintWriter writer = null;
		try {
			this.response.setHeader("Pragma", "no-cache");
			this.response.setHeader("Cache-Control", "no-cache");
			this.response.setDateHeader("Expires", 0);
			this.response.setContentType("text/html; charset=" + C.default_encoding);
			writer = response.getWriter();
			writer.write(html);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * 生成分页类
	 * 
	 * @param total
	 *            int 内容总数
	 * @param num
	 *            int 每页条数
	 * @return Pagination 分页类
	 */
	public Page page(int total, int num) {
		return new Page(total, num, this.entrance, this.routerMap);
	}

	/**
	 * 生成分页类
	 * 
	 * @param total
	 *            Long 内容总数
	 * @param num
	 *            int 每页条数
	 * @return Pagination 分页类
	 */
	public Page page(Long total, int num) {
		return new Page(total, num, this.entrance, this.routerMap);
	}

	/**
	 * 带有错误信息的跳转页面，例如“操作错误”，并且自动跳转到另外一个目标页面
	 * 
	 * @param errMsg
	 *            String 要提示给用户的错误信息
	 * @throws IOException
	 */
	public void error(String errMsg) throws IOException {
		if (!IS_AJAX) {
			session("javatodo_jump_type", "error");
			session("javatodo_jump_msg", errMsg);
			session("javatodo_jump_seconds", 3);
			session("javatodo_jump_url", "javascript:history.go(-1)");
			Map<String, String> map = new HashMap<>();
			map.put("m", "com.javatodo.core.controller");
			map.put("c", "Controller");
			map.put("a", "jump");
			this.redirect(this.U(map));
		} else {
			this.assignMap.clear();
			this.assignMap.put("status", 0);
			this.assignMap.put("info", errMsg);
			this.assignMap.put("url", "");
			this.jsonDisplay();
		}
	}

	/**
	 * 带有错误信息的跳转页面，例如“操作错误”，并且自动跳转到另外一个目标页面
	 * 
	 * @param errMsg
	 *            String 要提示给用户的错误信息
	 * @param url
	 *            String 要跳转的目标链接
	 * @throws IOException
	 */
	public void error(String errMsg, String url) throws IOException {
		if (!IS_AJAX) {
			session("javatodo_jump_type", "error");
			session("javatodo_jump_msg", errMsg);
			session("javatodo_jump_seconds", 3);
			session("javatodo_jump_url", url);
			Map<String, String> map = new HashMap<>();
			map.put("m", "com.javatodo.core.controller");
			map.put("c", "Controller");
			map.put("a", "jump");
			this.redirect(this.U(map));
		} else {
			this.assignMap.clear();
			this.assignMap.put("status", 0);
			this.assignMap.put("info", errMsg);
			this.assignMap.put("url", url);
			this.jsonDisplay();
		}
	}

	/**
	 * 带有错误信息的跳转页面，例如“操作错误”，并且自动跳转到另外一个目标页面
	 * 
	 * @param errMsg
	 *            String 要提示给用户的错误信息
	 * @param url
	 *            String 要跳转的目标链接
	 * @param seconds
	 *            Integer 在提示信息页面的停留时间
	 * @throws IOException
	 */
	public void error(String errMsg, String url, Integer seconds) throws IOException {
		if (!IS_AJAX) {
			session("javatodo_jump_type", "error");
			session("javatodo_jump_msg", errMsg);
			session("javatodo_jump_seconds", seconds);
			session("javatodo_jump_url", url);
			Map<String, String> map = new HashMap<>();
			map.put("m", this.MODULE_NAME);
			map.put("c", this.CONTROLLER_NAME);
			map.put("a", "jump");
			this.redirect(this.U(map));
		} else {
			this.assignMap.clear();
			this.assignMap.put("status", 0);
			this.assignMap.put("info", errMsg);
			this.assignMap.put("url", url);
			this.jsonDisplay();
		}
	}

	/**
	 * 带有成功信息的跳转页面，例如“操作成功”，并且自动跳转到另外一个目标页面
	 * 
	 * @param sucMsg
	 *            String 提示给用户的成功信息
	 * @throws IOException
	 */
	public void success(String sucMsg) throws IOException {
		if (!IS_AJAX) {
			session("javatodo_jump_type", "success");
			session("javatodo_jump_msg", sucMsg);
			session("javatodo_jump_seconds", 3);
			session("javatodo_jump_url", request.getHeader("Referer"));
			Map<String, String> map = new HashMap<>();
			map.put("m", this.MODULE_NAME);
			map.put("c", this.CONTROLLER_NAME);
			map.put("a", "jump");
			this.redirect(this.U(map));
		} else {
			this.assignMap.clear();
			this.assignMap.put("status", 1);
			this.assignMap.put("info", sucMsg);
			this.assignMap.put("url", "");
			this.jsonDisplay();
		}
	}

	/**
	 * 带有成功信息的跳转页面，例如“操作成功”，并且自动跳转到另外一个目标页面
	 * 
	 * @param sucMsg
	 *            String 提示给用户的成功信息
	 * @param url
	 *            String 要跳转的目标链接
	 * @throws IOException
	 */
	public void success(String sucMsg, String url) throws IOException {
		if (!IS_AJAX) {
			session("javatodo_jump_type", "success");
			session("javatodo_jump_msg", sucMsg);
			session("javatodo_jump_seconds", 3);
			session("javatodo_jump_url", url);
			Map<String, String> map = new HashMap<>();
			map.put("m", this.MODULE_NAME);
			map.put("c", this.CONTROLLER_NAME);
			map.put("a", "jump");
			this.redirect(this.U(map));
		} else {
			this.assignMap.clear();
			this.assignMap.put("status", 1);
			this.assignMap.put("info", sucMsg);
			this.assignMap.put("url", url);
			this.jsonDisplay();
		}
	}

	/**
	 * 带有成功信息的跳转页面，例如“操作成功”，并且自动跳转到另外一个目标页面
	 * 
	 * @param sucMsg
	 *            String 提示给用户的成功信息
	 * @param url
	 *            String 要跳转的目标链接
	 * @param seconds
	 *            Integer 在提示信息页面的停留时间
	 * @throws IOException
	 */
	public void success(String sucMsg, String url, Integer seconds) throws IOException {
		if (!IS_AJAX) {
			session("javatodo_jump_type", "success");
			session("javatodo_jump_msg", sucMsg);
			session("javatodo_jump_seconds", seconds);
			session("javatodo_jump_url", url);
			Map<String, String> map = new HashMap<>();
			map.put("m", this.MODULE_NAME);
			map.put("c", this.CONTROLLER_NAME);
			map.put("a", "jump");
			this.redirect(this.U(map));
		} else {
			this.assignMap.clear();
			this.assignMap.put("status", 1);
			this.assignMap.put("info", sucMsg);
			this.assignMap.put("url", url);
			this.jsonDisplay();
		}
	}

	/**
	 * 跳转到信息提示页面
	 * 
	 * @throws IOException
	 * @throws ServletException
	 * @throws TemplateException
	 */
	public void jumpPage() throws IOException, ServletException, TemplateException {
		String root_path = "";
		if (this.templateSuffix.equals(".jsp")) {
			root_path = "/" + this.templatePath;
		} else {
			root_path = servlet.getServletContext().getRealPath("/") + this.templatePath;
		}
		String path = root_path + "\\system\\jump" + this.templateSuffix;
		this.assign("type", session("javatodo_jump_type"));
		this.assign("msg", session("javatodo_jump_msg"));
		this.assign("seconds", session("javatodo_jump_seconds"));
		this.assign("url", session("javatodo_jump_url"));
		this.view.flush(request, response, servlet, path);
	}

	/**
	 * 判断是否是javatodo的控制器
	 */
	public boolean check_if_it_is_a_javatodo_controller() {
		return true;
	}
}
