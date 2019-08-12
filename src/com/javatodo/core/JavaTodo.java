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
package com.javatodo.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.javatodo.config.C;
import com.javatodo.core.router.RC;
import com.javatodo.core.router.Router;

public class JavaTodo {
	static boolean is_init = false;

	public JavaTodo() {
		if (!JavaTodo.is_init) {
			C.set_router();
			JavaTodo.is_init = true;
		}
	}

	public synchronized void setRequestAndResponse(HttpServletRequest request, HttpServletResponse response,
			HttpServlet servlet) {
		Router router = new Router(request);
		Map<String, String> routerMap = router.parse();
		String package_name = RC.getRC(routerMap.get("m"));
		String class_name = RC.getRC(routerMap.get("m"), routerMap.get("c"));
		String function_name = RC.getRC(routerMap.get("m"), routerMap.get("c"), routerMap.get("a"));
		try {
			Class<?> javatodo_class = Class.forName(package_name + "." + class_name);
			Object javatodo_object = javatodo_class.newInstance();
			Class<?>[] javatodo_args_class = { HttpServletRequest.class, HttpServletResponse.class, HttpServlet.class };
			Object[] javatodo_args = { request, response, servlet };
			Method javatodo_set_parameter = javatodo_class.getMethod("setRequestAndResponse", javatodo_args_class);
			javatodo_set_parameter.invoke(javatodo_object, javatodo_args);
			Method javatodo_init = javatodo_class.getMethod("init");
			Method javatodo_before = javatodo_class.getMethod("_before");
			Method javatodo_after = javatodo_class.getMethod("_after");
			Method javatodo_method = javatodo_class.getMethod(function_name);
			Boolean initRet = (Boolean) javatodo_init.invoke(javatodo_object);
			if (initRet != true) {
				return;
			}
			javatodo_before.invoke(javatodo_object);
			if (Modifier.isStatic(javatodo_method.getModifiers())) {
				javatodo_method.invoke(null);
			} else {
				javatodo_method.invoke(javatodo_object);
			}
			javatodo_after.invoke(javatodo_object);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
