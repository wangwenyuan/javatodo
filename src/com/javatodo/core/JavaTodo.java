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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.javatodo.config.C;
import com.javatodo.core.router.Router;
import com.javatodo.core.tools.T;

public class JavaTodo {
	static boolean is_init = false;
	private String app = "";

	public JavaTodo() {
		if (!JavaTodo.is_init) {
			C.setRouter();
			JavaTodo.is_init = true;
		}
	}

	public JavaTodo(String appName) {
		this.app = appName;
		if (!JavaTodo.is_init) {
			C.setRouter();
			JavaTodo.is_init = true;
		}
	}

	public void setRequestAndResponse(HttpServletRequest request, HttpServletResponse response, HttpServlet servlet) {
		Boolean ControllerCheck = false;
		Router router = new Router(request, app);
		String package_name = router.PACKAGE_NAME;
		String class_name = router.CLASS_NAME;
		String function_name = router.FUNCTION_NAME;
		try {
			Class<?> javatodo_class = Class.forName(package_name + "." + class_name);
			if (null == javatodo_class) {
				return;
			}
			Object javatodo_object = javatodo_class.newInstance();
			Method[] methods = javatodo_class.getMethods();
			for (Integer i = 0; i < methods.length; i = i + 1) {
				if (methods[i].getName().equals("check_if_it_is_a_javatodo_controller")) {
					Method check_if_it_is_a_javatodo_controller = javatodo_class.getMethod("check_if_it_is_a_javatodo_controller");
					ControllerCheck = (Boolean) check_if_it_is_a_javatodo_controller.invoke(javatodo_object);
				}
			}
			if (!ControllerCheck) {
				return;
			}
			Class<?>[] javatodo_args_class = { HttpServletRequest.class, HttpServletResponse.class, HttpServlet.class, String.class };
			Object[] javatodo_args = { request, response, servlet, app };
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
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			C.logFilePath = servlet.getServletContext().getRealPath("/") + "WEB-INF/Runtime/log/";
			T.javatodo_error_log(e);
		}
	}
}
