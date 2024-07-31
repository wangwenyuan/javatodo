<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.javatodo.core.JavaTodo" %>
<%@ page import="com.javatodo.config.C" %>
<%
JavaTodo javaTodo=new JavaTodo();
request.setCharacterEncoding(C.defaultEncoding);
response.setCharacterEncoding(C.defaultEncoding);
response.setHeader("Content-type", "text/html;charset="+C.defaultEncoding);
javaTodo.setRequestAndResponse(request, response,this);
out.clear();
out = pageContext.pushBody();
%>