<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>kindeditor上传图片至aliyunOSS</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" href="kindeditor/themes/default/default.css" />
	<script charset="utf-8" src="kindeditor/kindeditor-min.js"></script>
	<script charset="utf-8" src="kindeditor/lang/zh_CN.js"></script>
    <script type="text/javascript">
    	var editor;
    	KindEditor.ready(function(K) {
			editor = K.create('#editor-text', {
				allowFileManager : false,
				uploadJson : './kindeditor/jsp/upload_json.jsp',
			});
		});
    </script>
  </head>
  
  <body>
    <textarea id="editor-text" style="height:500px; width: 700px;"></textarea>
  </body>
</html>
