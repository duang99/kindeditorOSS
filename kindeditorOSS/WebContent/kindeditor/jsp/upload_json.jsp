<%@page import="com.oss.upload.KindUpload"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	response.setContentType("text/html; charset=UTF-8");
	KindUpload upload=new KindUpload(request,response);
	//kindeditor为阿里云OSS Object文件夹，程序会自动创建，OSS Object管理中无须手动创建
	out.print(upload.uploadFile("kindeditor/"));
%>