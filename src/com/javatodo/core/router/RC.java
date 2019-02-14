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

import java.awt.event.MouseAdapter;
import java.util.HashMap;
import java.util.Map;

public class RC {
	private static Map<String, String> C= new HashMap<>();
	
	/*RC.C:
	 * C["m"]="com.javatodo.app.index"
	 * C["m!--javatodo--!c"]="Index"
	 * C["m!--javatodo--!c!--javatodo--!a"]="index"
	 * */
	
	public RC(String package_name,String module_name){
		if(!("".equals(package_name.trim())||"".equals(module_name.trim()))){
			RC.C.put(module_name, package_name);
		}
	}
	public RC(String package_name,String module_name,String class_name,String controller_name){
		if(!("".equals(package_name.trim())||"".equals(module_name.trim())||"".equals(class_name.trim())||"".equals(controller_name.trim()))){
			RC.C.put(module_name, package_name);
			RC.C.put(module_name+"!--javatodo--!"+controller_name, class_name);
		}
	}
	public RC(String package_name,String module_name,String class_name,String controller_name,String function_name,String action_name){
		if(!("".equals(package_name.trim())||"".equals(module_name.trim())||"".equals(class_name.trim())||"".equals(controller_name.trim())||"".equals(function_name.trim())||"".equals(action_name))){
			RC.C.put(module_name, package_name);
			RC.C.put(module_name+"!--javatodo--!"+controller_name, class_name);
			RC.C.put(module_name+"!--javatodo--!"+controller_name+"!--javatodo--!"+action_name, function_name);
		}
	}
	
	/*RC.C:
	 * C["m"]="com.javatodo.app.index"
	 * C["m!--javatodo--!c"]="Index"
	 * C["m!--javatodo--!c!--javatodo--!a"]="index"
	 * */
	
	/*获得真实的包名、类名、函数名
	 * */
	public static String getRC(String module_name){
		if(RC.C.containsKey(module_name)){
			return RC.C.get(module_name);
		}else{
			return module_name;
		}
	}
	public static String getRC(String module_name, String controller_name) {
		if(RC.C.containsKey(module_name+"!--javatodo--!"+controller_name)){
			return RC.C.get(module_name+"!--javatodo--!"+controller_name);
		}else{
			return controller_name;
		}
		
	}
	public static String getRC(String module_name, String controller_name, String action_name) {
		if(RC.C.containsKey(module_name+"!--javatodo--!"+controller_name+"!--javatodo--!"+action_name)){
			return RC.C.get(module_name+"!--javatodo--!"+controller_name+"!--javatodo--!"+action_name);
		}else{
			return action_name;
		}
	}
}
