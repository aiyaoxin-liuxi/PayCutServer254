<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
<base href="<%=basePath%>">
<TITLE>商户操作系统</TITLE>
<META http-equiv=Content-Type content="text/html; charset=utf-8">
<META content="MSHTML 6.00.2900.3492" name=GENERATOR>
</head>

<frameset rows="80,*">	
	<frame name=top src="top.jsp" frameBorder=0 noResize scrolling=no>
	<frameset  cols="220,*">
		<frame name="menu" src="menu" frameBorder=0 scrolling="auto" noResize>
		<frame name=frame_main src="welcome.jsp" frameBorder=0>
	</frameset>
	<noframes>
		<p>This page requires frames, but your browser does not support them.
		</p>
	</noframes>
</frameset>
</html>
