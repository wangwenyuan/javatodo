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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.javatodo.config.C;
import com.javatodo.core.tools.T;

public class VelocityView extends View {
	public VelocityContext context = new VelocityContext();
	private static final Properties properties = new Properties();
	private static boolean is_init = false;

	private static void init() {
		properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, "");
		properties.setProperty(Velocity.ENCODING_DEFAULT, C.defaultEncoding);
		properties.setProperty(Velocity.INPUT_ENCODING, C.defaultEncoding);
		properties.setProperty(Velocity.OUTPUT_ENCODING, C.defaultEncoding);
		VelocityView.is_init = true;
	}

	public VelocityView() {
		// TODO Auto-generated constructor stub
		if (!VelocityView.is_init) {
			VelocityView.init();
			Velocity.init(properties);
		}
	}

	@Override
	public void assign(String name, Object value) {
		// TODO Auto-generated method stub
		this.context.put(name, value);
	}

	@Override
	public void flush(HttpServletRequest request, HttpServletResponse response, HttpServlet servlet, String view_path) throws IOException {
		Template template = Velocity.getTemplate(view_path);
		response.setContentType(request.getContentType());
		PrintWriter writer = response.getWriter();
		template.merge(context, writer);
		writer.flush();
	}

	@Override
	public String parseString(String view_path, String logName) throws IOException {
		StringWriter stringWriter = new StringWriter();
		String content = T.readFile(view_path, C.defaultEncoding);
		Velocity.evaluate(context, stringWriter, logName, content);
		return stringWriter.getBuffer().toString();
	}
}
