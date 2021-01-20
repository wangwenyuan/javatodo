package com.javatodo.app.index;

import java.io.IOException;

import javax.servlet.ServletException;

import com.javatodo.core.controller.Controller;

import freemarker.template.TemplateException;

public class IndexController extends Controller {
	public void indexPage() throws IOException, ServletException, TemplateException {
		this.assign("welcome", "Hello JavaToDo!");
		this.display();
	}
}
