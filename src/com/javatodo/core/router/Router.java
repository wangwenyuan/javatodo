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
package com.javatodo.core.router;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.javatodo.config.C;

public class Router {
	HttpServletRequest request = null;
	public String APP_NAME = "";
	public String MODULE_NAME = C.defaultModule;
	public String CONTROLLER_NAME = C.defaultController;
	public String ACTION_NAME = C.defaultAction;
	public String PACKAGE_NAME = "";
	public String CLASS_NAME = RC.getRC(C.defaultModule, C.defaultController) + "Controller";
	public String FUNCTION_NAME = RC.getRC(C.defaultModule, C.defaultController, C.defaultAction) + "Page";

	public Map<String, String> map = new HashMap<>();

	public Router(HttpServletRequest request, String appName) {
		this.request = request;
		this.APP_NAME = appName;
		if (!"".equals(appName)) {
			this.PACKAGE_NAME = appName + "." + RC.getRC(C.defaultModule);
		} else {
			this.PACKAGE_NAME = RC.getRC(C.defaultModule);
		}
		Map<String, String[]> queryMap = request.getParameterMap();
		for (Map.Entry<String, String[]> entry : queryMap.entrySet()) {
			String[] value_arr = entry.getValue();
			if (value_arr == null) {
				continue;
			} else {
				if (value_arr.length == 0) {
					continue;
				}
			}
			map.put(entry.getKey(), value_arr[0]);
		}

		if (map.containsKey("m") && map.containsKey("c") && map.containsKey("a")) {
			if (map.get("m").equals("com.javatodo.core.controller") && map.get("c").equals("Controller") && map.get("a").equals("jump")) {
				this.PACKAGE_NAME = "com.javatodo.core.controller";
				this.CLASS_NAME = "Controller";
				this.FUNCTION_NAME = "jumpPage";
				this.MODULE_NAME = "com.javatodo.core.controller";
				this.CONTROLLER_NAME = "Controller";
				this.ACTION_NAME = "jump";
				return;
			}
		}

		if (!map.containsKey("m")) {
			map.put("m", this.MODULE_NAME);
		} else {
			this.MODULE_NAME = map.get("m");
			if (!"".equals(this.APP_NAME)) {
				this.PACKAGE_NAME = this.APP_NAME + "." + RC.getRC(map.get("m"));
			} else {
				this.PACKAGE_NAME = RC.getRC(map.get("m"));
			}

		}
		if (!map.containsKey("c")) {
			map.put("c", this.CONTROLLER_NAME);
		} else {
			this.CONTROLLER_NAME = map.get("c");
			this.CLASS_NAME = RC.getRC(map.get("m"), map.get("c")) + "Controller";
		}
		if (!map.containsKey("a")) {
			map.put("a", this.ACTION_NAME);
		} else {
			this.ACTION_NAME = map.get("a");
			this.FUNCTION_NAME = RC.getRC(map.get("m"), map.get("c"), map.get("a")) + "Page";
		}
	}
}
