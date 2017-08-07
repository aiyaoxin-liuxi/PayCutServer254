<%@page import="java.net.URLDecoder"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%  
String path = request.getContextPath();  
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";  
if (request.getCharacterEncoding() == null) {
	request.setCharacterEncoding("UTF-8");//你的编码格式
}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>EXCEL文件上传</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<link href="css/css5/jh.css" rel="stylesheet" type="text/css" />
<link href="css/css5/ccb.css" rel="stylesheet" type="text/css" />
<link href="css/css5/B.css" rel="stylesheet" type="text/css" />
<link type="text/css" rel="stylesheet" href="css/css5/jquery_dialog.css" />
<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/jquery_dialog.js"></script>
<script type="text/javascript">
	function readFile(obj) {
		document.getElementById("file").value = obj.value;
	}

	$(document).ready(function(){
		$("#upload").submit(function(e){
			if(!$("#file").val()){
				alert("必须选择文件！");
				return false;
			}
			
		});
	});

</script>

</head>

<body>
	<form id="upload" method="POST" action="proxySendSalary/import"
		enctype="multipart/form-data">
		<table border="1" class="Table_N" cellspacing="0" cellpadding="5"
				align="center" width="100%">
				<tr class="Table_H">
					<td nowrap colspan="9" class="Table_H" height="40">
						批量代付excel文件上传
					</td>
				</tr>
		</table>
		<p>
		<!-- <em>Excel文件格式要求为2003版 Excel，文件名后缀为xls.2007及以后版本暂且不支持。</em> -->
		<c:choose>
			<c:when test="${result!=null }">
				${result}
				
				<p>
				
				<input type="button" value="关闭" class="button"
						onclick="javascript:parent.window.JqueryDialog.CloseAndRefresh();" />	
			</c:when>
			<c:otherwise>	
				<p>
				通道名：<%=URLDecoder.decode(request.getParameter("channelName"),"UTF-8") %><p>
				<p>商户号:${param.merchId}<p>			
				<label>请选要导入文件 </label> 
				<input type="hidden" name="channelId" value="${param.channelId }"/>
				<input type="hidden" name="merchId" value="${param.merchId }"/>
				<input type="file" id="file" name="file">
				<p>
				<input type="submit" value="提交">
				<input type="button" value="关闭" class="button"
						onclick="javascript:parent.window.JqueryDialog.CloseAndRefresh();" />			 
			</c:otherwise>
		</c:choose>
	</form>
</body>
</html>
