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
	HttpServletRequest request=null;
	public String MODULE_NAME=C.default_module;
	public String CONTROLLER_NAME=C.default_controller;
	public String ACTION_NAME=C.default_action;
	public String PACKAGE_NAME=RC.getRC(C.default_module);
	public String CLASS_NAME=RC.getRC(C.default_module, C.default_controller);
	public String FUNCTION_NAME=RC.getRC(C.default_module, C.default_controller, C.default_action);
	public Router(HttpServletRequest request){
		this.request=request;
	}
	public Map<String, String> parse(){
		Map<String, String>map=new HashMap<>();
		Map<String, String[]>queryMap=request.getParameterMap();
		for(Map.Entry<String, String[]> entry:queryMap.entrySet()){
			String[] value_arr=entry.getValue();
			if(value_arr==null){
				continue;
			}else{
				if(value_arr.length==0){
					continue;
				}
			}
			map.put(entry.getKey(), value_arr[0]);
		}
		if(!map.containsKey("m")) {
			map.put("m", this.MODULE_NAME);
		}else {
			this.MODULE_NAME = map.get("m");
			this.PACKAGE_NAME = RC.getRC(map.get("m"));
		}
		if(!map.containsKey("c")) {
			map.put("c", this.CONTROLLER_NAME);
		}else {
			this.CONTROLLER_NAME = map.get("c");
			this.CLASS_NAME = RC.getRC(map.get("m"), map.get("c"));
		}
		if(!map.containsKey("a")) {
			map.put("a", this.ACTION_NAME);
		}else {
			this.ACTION_NAME = map.get("a");
			this.FUNCTION_NAME = RC.getRC(map.get("m"), map.get("c"), map.get("a"));
		}
		return map;
	}
}
