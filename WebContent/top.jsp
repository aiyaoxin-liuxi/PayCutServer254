<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//Dtd HTML 4.01 transitional//EN" "http://www.w3c.org/tr/1999/REC-html401-19991224/loose.dtd">
<html>
<head>
<title>顶部</title>
<meta http-equiv=Content-Type content="text/html; charset=utf-8">
<style type=text/css>
body {
	PaDDING-RIGHT: 0px;
	PaDDING-LEFT: 0px;
	PaDDING-BOTTOM: 0px;
	MaRGIN: 0px;
	PaDDING-TOP: 0px;
	BaCKGROUND-COLOR: #C7BFE6;
	font-SIZE: 12px;
	COLOR: #003366;
	font-FaMILY: Verdana, arial, Helvetica, sans-serif;
}
td {
	font-SIZE: 12px;
	COLOR: #003366;
	font-FaMILY: Verdana, arial, Helvetica, sans-serif
}

div {
	font-SIZE: 12px;
	COLOR: #003366;
	font-FaMILY: Verdana, arial, Helvetica, sans-serif
}

p {
	font-SIZE: 12px;
	COLOR: #003366;
	font-FaMILY: Verdana, arial, Helvetica, sans-serif
}
</style>
</head>
<body>
	<table cellSpacing=0 cellPadding=0 width="100%" border=0>
		<tbody>
			<tr>
				<td width=10></td>

				<td>
					<table cellSpacing=0 cellPadding=0 width="100%" border=0>
						<tbody>
							<tr>
								<td align=left height=35>
								<font size=4><B></B></font></td>
							</tr>
							<tr>
								<td height=35>
								</td>
							</tr>
						</tbody>
					</table>

				</td>
				<td>
					<table cellSpacing=0 cellPadding=0 width="100%" border=0>
						<tbody>
							<tr>
								<td align=right height=35></td>
							</tr>
							<tr>
								<td height=35><a href="<%=basePath%>logout"
									target="_parent"><font color=red><B>安全退出</B></font></a></td>
							</tr>
						</tbody>
					</table>
				</td>
				<td width=10></td>
			</tr>
		</tbody>
	</table>
</body>
</html>
