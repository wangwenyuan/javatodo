<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.javatodo.core.JavaTodo" %>
<%@ page import="com.javatodo.config.C" %>
<%
JavaTodo javaTodo=new JavaTodo();
request.setCharacterEncoding(C.default_encoding);
response.setCharacterEncoding(C.default_encoding);
response.setHeader("Content-type", "text/html;charset="+C.default_encoding);
javaTodo.setRequestAndResponse(request, response,this);
out.clear();
out = pageContext.pushBody();
%>