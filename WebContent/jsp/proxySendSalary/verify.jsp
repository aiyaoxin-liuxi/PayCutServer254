
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
request.setAttribute("basePath", basePath);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>批量代付复核列表:</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<link href="resources/css5/jh.css" rel="stylesheet" type="text/css" />
	<link href="resources/css5/ccb.css" rel="stylesheet" type="text/css" />
	<link href="resources/css5/B.css" rel="stylesheet" type="text/css" />
	<link href="resources/css5/tx.css" rel="stylesheet" type="text/css" />
	
	<script language="JavaScript" src="resources/js5/jquery/jquery.js"></script>
	<script language="JavaScript" src="resources/js5/jh.js"></script>
	<script type="text/javascript" src="common/pager/jquery.pager.js"></script>
	<script type="text/javascript" src="common/pager/list.js"></script>
	<link href="common/pager/pager.css" rel="stylesheet" type="text/css">
	<link type="text/css" rel="stylesheet" href="resources/css5/jquery_dialog.css" />
	<script type="text/javascript" src="resources/js5/jquery_dialog.js"></script>
</head>

<body bgcolor='#FFFFFF' text='#000000' leftmargin='0' topmargin='0'
	marginwidth='0' marginheight='0'>
	<sf:form modelAttribute="form" method="POST" cssClass="listForm" 
		name="form1" id="form2">
		<table border="1" class="Table_N" cellspacing="0" cellpadding="5"
			align="center" width="100%">
			<tr class="Table_H">
				<td nowrap colspan="15" class="Table_H" height="40">批量代付复核列表:</td>
			</tr>
			<tr class="Table_H">
				<td nowrap colspan="15" height="50" valign="middle">
					开始日期:<sf:input path="startDate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" autocomplete="off"/>
					&nbsp;
					结束日期:<sf:input path="endDate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" autocomplete="off"/>
					&nbsp;&nbsp;&nbsp;
					<input type="submit" value="查询" name="queryButton" class="button">
				</td>
			</tr>
			</table>
			<table border="1" id="table" class="Table_N" cellspacing="0" cellpadding="5"
				align="center" width="100%">
			<tr class="Table_H">
				<td align="center">批次号</td>
				<td align="center">文件名称</td>
				<td align="center">记录数</td>
				<td align="center">业务费项</td>
				<td align="center">总金额</td>
				<td align="center">时间</td>
				<td align="center">操作员</td>
				<td align="center">操作</td>
			</tr>
			<c:forEach items="${form.pager.datas}" var="proxyPayBatch" varStatus="i">
				<c:choose>
					<c:when test="${i.count%2==0 }">
						<c:set var="rowClass" value="table_select_bg"></c:set>
					</c:when>
					<c:otherwise>
						<c:set var="rowClass" value=""></c:set>
					</c:otherwise>
				</c:choose>
				<tr class="${rowClass }">
					<td align="center">${proxyPayBatch.id }</td>
					<td align="center">${proxyPayBatch.filename}</td>
					<td align="center">${proxyPayBatch.total_num}</td>
					<td align="center">${proxyPayBatch.feeitem}</td>
					<td align="center">
						<fmt:formatNumber value="${proxyPayBatch.total_money}" pattern="#,##0.00#"/>	
					</td>
					<td align="center"><fmt:formatDate value="${proxyPayBatch.create_datetime}" type="both"/></td>
					<td align="center">${proxyPayBatch.creator_id}</td>
					<td>
							<sec:authorize url="/proxyPayBatch/audit">
								<input type="button" value="复核" onClick="javascript:JqueryDialog.Open('','<%=basePath%>proxyPayBatch/audit?id=${proxyPayBatch.id}',700,400);"
									class="button2">
							</sec:authorize>
					</td>
					</tr>
			</c:forEach>
		</table>
		<%@include file="/common/pager/pager.jsp"%>
	</sf:form>
</body>
<script language="javascript" type="text/javascript" src="<%=basePath %>resources/js5/My97DatePicker/WdatePicker.js"></script>
</html>
