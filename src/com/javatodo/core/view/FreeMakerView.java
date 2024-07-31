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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.javatodo.config.C;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class FreeMakerView extends View {
	private Configuration configuration = new Configuration();
	private Map<String, Object>data=new HashMap<>();
	
	public FreeMakerView(){
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		configuration.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
		configuration.setDefaultEncoding(C.defaultEncoding);
		configuration.setOutputEncoding(C.defaultEncoding);
		configuration.setLocalizedLookup(false);
		configuration.setNumberFormat("#0.#####");
		configuration.setDateFormat("yyyy-MM-dd");
		configuration.setTimeFormat("HH:mm:ss");
		configuration.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	@Override
	public void assign(String name, Object value) {
		// TODO Auto-generated method stub
		this.data.put(name, value);
	}
	@Override
	public void flush(HttpServletRequest request,HttpServletResponse response,HttpServlet servlet,String view_path) throws IOException, TemplateException{
		view_path=view_path.replace ("/", "\\");
		String[] path_arr=view_path.split("\\\\");
		String template_file=path_arr[path_arr.length-1];
		String template_floder="";
		for(Integer integer=0; integer<path_arr.length-1; integer=integer+1){
			if(integer==0){
				template_floder=path_arr[0];
			}else{
				template_floder=template_floder+"\\"+path_arr[integer];
			}
		}
		configuration.setDirectoryForTemplateLoading(new File(template_floder));
		Template template=configuration.getTemplate(template_file);
		response.setContentType(request.getContentType());
		PrintWriter writer=response.getWriter();
		template.process(this.data, writer);
	}
	@Override
	public String parseString(String view_path,String logName) throws IOException, TemplateException{
		view_path=view_path.replace ("/", "\\");
		String[] path_arr=view_path.split("\\\\");
		String template_file=path_arr[path_arr.length-1];
		String template_floder="";
		for(Integer integer=0; integer<path_arr.length-1; integer=integer+1){
			if(integer==0){
				template_floder=path_arr[0];
			}else{
				template_floder=template_floder+"\\"+path_arr[integer];
			}
		}
		configuration.setDirectoryForTemplateLoading(new File(template_floder));
		StringWriter writer=new StringWriter();
		Template template=configuration.getTemplate(template_file);
		template.process(this.data, writer);
		return writer.toString();
	}
}
