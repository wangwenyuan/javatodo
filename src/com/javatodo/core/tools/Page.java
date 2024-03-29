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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class Page {
	public String entrance = "";
	public long total = 0;// 总条数
	public int listRows = 0;// 每页条数
	public int current = 1;// 当前页码
	public int firstRow = 0;// 起始条数
	public int allPageNum = 0;// 总页数
	public Map<String, String> map = new HashMap<>();
	public HttpServletRequest request = null;

	public Page(int total, int num, String entrance, Map<String, String> routerMap) {
		this.total = total;
		this.listRows = num;
		if (routerMap.containsKey("p")) {
			current = T.toInt(routerMap.get("p").toString());
			if (current == 0) {
				current = 1;
			}
		} else {
			current = 1;
		}
		this.firstRow = (current - 1) * listRows;
		for (Map.Entry<String, String> entry : routerMap.entrySet()) {
			this.map.put(entry.getKey().toString(), entry.getValue().toString());
		}
		this.entrance = entrance;
	}

	public Page(long total, int num, String entrance, Map<String, String> routerMap) {
		this.total = total;
		this.listRows = num;
		if (routerMap.containsKey("p")) {
			current = T.toInt(routerMap.get("p").toString());
			if (current == 0) {
				current = 1;
			}
		} else {
			current = 1;
		}
		this.firstRow = (current - 1) * listRows;
		for (Map.Entry<String, String> entry : routerMap.entrySet()) {
			this.map.put(entry.getKey().toString(), entry.getValue().toString());
		}
		this.entrance = entrance;
	}

	public Page(int total, int num, String entrance, Map<String, String> routerMap, HttpServletRequest request) {
		this.request = request;
		this.total = total;
		this.listRows = num;
		if (routerMap.containsKey("p")) {
			current = T.toInt(routerMap.get("p").toString());
			if (current == 0) {
				current = 1;
			}
		} else {
			current = 1;
		}
		this.firstRow = (current - 1) * listRows;
		for (Map.Entry<String, String> entry : routerMap.entrySet()) {
			this.map.put(entry.getKey().toString(), entry.getValue().toString());
		}
		this.entrance = entrance;
	}

	public Page(long total, int num, String entrance, Map<String, String> routerMap, HttpServletRequest request) {
		this.request = request;
		this.total = total;
		this.listRows = num;
		if (routerMap.containsKey("p")) {
			current = T.toInt(routerMap.get("p").toString());
			if (current == 0) {
				current = 1;
			}
		} else {
			current = 1;
		}
		this.firstRow = (current - 1) * listRows;
		for (Map.Entry<String, String> entry : routerMap.entrySet()) {
			this.map.put(entry.getKey().toString(), entry.getValue().toString());
		}
		this.entrance = entrance;
	}

	/**
	 * 生成分页的html代码
	 * 
	 * @return String 分页的html代码
	 */
	public String show() {
		double zhi = (double) total / (double) listRows;
		allPageNum = (int) Math.ceil(zhi);
		String page_str = "<span>共" + allPageNum + "页，每页" + listRows + "条，当前第" + this.current + "页</span>";
		if (current == 1) {
			map.put("p", (this.current + 1) + "");
			if (request == null) {
				page_str = page_str + "<span>首页</span><span>上一页</span><a href='" + T.U(map, entrance) + "' >下一页</a>";
			} else {
				page_str = page_str + "<span>首页</span><span>上一页</span><a href='" + T.U(map, entrance, request) + "' >下一页</a>";
			}
			map.put("p", allPageNum + "");
			if (request == null) {
				page_str = page_str + "<a href='" + T.U(map, entrance) + "' >尾页</a>";
			} else {
				page_str = page_str + "<a href='" + T.U(map, entrance, request) + "' >尾页</a>";
			}
		} else if (current == allPageNum) {
			map.put("p", "1");
			if (request == null) {
				page_str = page_str + "<a href='" + T.U(map, entrance) + "'>首页</a>";
			} else {
				page_str = page_str + "<a href='" + T.U(map, entrance, request) + "'>首页</a>";
			}
			map.put("p", (this.current - 1) + "");
			if (request == null) {
				page_str = page_str + "<a href='" + T.U(map, entrance) + "' >上一页</a><span>下一页</span><span>尾页</span>";
			} else {
				page_str = page_str + "<a href='" + T.U(map, entrance, request) + "' >上一页</a><span>下一页</span><span>尾页</span>";
			}
		} else if (current < 1) {
			page_str = "";
		} else if (current > allPageNum) {
			page_str = "";
		} else {
			map.put("p", "1");
			if (request == null) {
				page_str = page_str + "<a href='" + T.U(map, entrance) + "' >首页</a>";
			} else {
				page_str = page_str + "<a href='" + T.U(map, entrance, request) + "' >首页</a>";
			}
			map.put("p", (this.current - 1) + "");
			if (request == null) {
				page_str = page_str + "<a href='" + T.U(map, entrance) + "' >上一页</a>";
			} else {
				page_str = page_str + "<a href='" + T.U(map, entrance, request) + "' >上一页</a>";
			}
			map.put("p", (this.current + 1) + "");
			if (request == null) {
				page_str = page_str + "<a href='" + T.U(map, entrance) + "' >下一页</a>";
			} else {
				page_str = page_str + "<a href='" + T.U(map, entrance, request) + "' >下一页</a>";
			}
			map.put("p", allPageNum + "");
			if (request == null) {
				page_str = page_str + "<a href='" + T.U(map, entrance) + "' >尾页</a>";
			} else {
				page_str = page_str + "<a href='" + T.U(map, entrance, request) + "' >尾页</a>";
			}

		}
		String gotopage = "<span>转到：<select onchange=\"self.location.href=this.options[this.selectedIndex].value\">";
		for (Integer i = 0; i < allPageNum; i = i + 1) {
			map.put("p", (i + 1) + "");
			String selected = "";
			if (this.current == i + 1) {
				selected = "selected=\"selected\"";
			} else {
				selected = "";
			}
			if (request == null) {
				gotopage = gotopage + "<option " + selected + " value=\"" + T.U(map, entrance) + "\">第 " + (i + 1) + " 页</option>";
			} else {
				gotopage = gotopage + "<option " + selected + " value=\"" + T.U(map, entrance, request) + "\">第 " + (i + 1) + " 页</option>";
			}

		}
		gotopage = gotopage + "</select></span>";
		if (zhi < 1) {
			return "";
		} else {
			return "<div>" + page_str + gotopage + "</div>";
		}
	}
}
