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
package com.javatodo.core.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JspView extends View {
	private HttpServletRequest request=null;
	public JspView(HttpServletRequest request) {
		this.request=request;
	}
	@Override
	public void assign(String name, Object value) {
		// TODO Auto-generated method stub
		this.request.setAttribute(name, value);
	}
	@Override
	public void flush(HttpServletRequest request,HttpServletResponse response,HttpServlet servlet,String view_path) throws IOException, ServletException{
		this.request.getRequestDispatcher(view_path).forward(this.request, response);
	}
	@Override
	public String parseString(String view_path,String logName){
		return "";
	}
}
