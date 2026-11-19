<%-- 
    Document   : usuarios
    Created on : 20 oct 2026, 12:50:25 p.m.
    Author     : jimen
--%>

<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%
    response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
    String jsonResponse = "[{\"id\": 1, \"nombre\": \"Ana\"}, {\"id\": 2, \"nombre\": \"Carlos\"}]";
    out.print(jsonResponse);
    out.flush();
%>
