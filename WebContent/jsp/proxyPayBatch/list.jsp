
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
 
 <title>批量代付复核列表:</title>
 
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<link href="css/css5/jh.css" rel="stylesheet" type="text/css" />
<link href="css/css5/ccb.css" rel="stylesheet" type="text/css" />
<link href="css/css5/B.css" rel="stylesheet" type="text/css" />
<link href="common/pager/pager.css" rel="stylesheet" type="text/css">
<link type="text/css" rel="stylesheet" href="css/css5/jquery_dialog.css" />
<script language="JavaScript" src="js/jquery.js"></script>
<script type="text/javascript" src="common/pager/jquery.pager.js"></script>
<script type="text/javascript" src="common/pager/list.js"></script>
<script type="text/javascript" src="js/jquery_dialog.js"></script>
<script type="text/javascript" src="<%=basePath %>common/My97DatePicker/WdatePicker.js"></script>
		<script type="text/javascript">
		function upload(){
			var channelName = encodeURIComponent(encodeURIComponent($("#channelId :selected").text()));
			var channelId= $("#channelId :selected").val();
			var merchId = $("#merchId").val();
			javascript:JqueryDialog.Open('','<%=basePath%>proxyPayBatch/uploadExcelFile?channelId='+channelId+'&channelName='+channelName+'&merchId='+merchId,900,500);
		}
	</script>
</head>

<body bgcolor='#FFFFFF' text='#000000' leftmargin='0' topmargin='0'
	marginwidth='0' marginheight='0'>
	<sf:form modelAttribute="form" method="POST" cssClass="listForm" name="form1" id="form1">
		<table border="1" class="Table_N" cellspacing="0" cellpadding="5"
			align="center" width="100%">
			<tr class="Table_H">
				<td nowrap colspan="15" class="Table_H" height="40">批量代付复核列表:</td>
			</tr>
			<tr>
					<td>
					 通道名
					   <select name="channelId" id="channelId">
					   		  <option value="1">广发</option>
					        <%--  <c:forEach var="item" items="${feeItems}">
					         
					            <option value="${item.id}">${item.name}</option>
					         </c:forEach> --%>
						</select>
						<sf:input path="merchId" type="hidden"/>
					   <span class="required">*</span>
					   <span id = "acct_typeTip" class="tip"></span>
					</td>
				</tr>
			<tr>
					<td>
					  		
							<input type="button" value="上传excel文件" class="button"
								onclick="upload()" />
					
					  &nbsp;&nbsp;&nbsp;
					  <a href="<%=basePath %>template/proxy_pay.xlsx">下载批量代付模板</a>
					  &nbsp;&nbsp;&nbsp;<input id="submitButton" type="submit" value="查询" class="button"/>
					</td>
			</tr>
			</table>
			<table border="1" id="table" class="Table_N" cellspacing="0" cellpadding="5"
				align="center" width="100%">
			<tr class="Table_H">
				<td align="center">批次号</td>
				<td align="center">交易类型</td>
				<td align="center">文件名称</td>
				<td align="center">记录数</td>
				<td align="center">总金额</td>
				<td align="center">成功记录数</td>
				<td align="center">成功金额</td>
				<td align="center">时间</td>
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
					<td align="center">${proxyPayBatch.batchId}</td>
					<td align="center"><c:choose>
							
							<c:when test="${proxyPayBatch.bizType=='2' }">批量代付</c:when>
							<c:when test="${proxyPayBatch.bizType=='3' }">代发工资</c:when>
						</c:choose></td>
					<td align="center">${proxyPayBatch.filename}</td>
					<td align="center">${proxyPayBatch.totalNum}</td>
					<td align="center">
						<fmt:formatNumber value="${proxyPayBatch.totalMoney}" pattern="#,##0.00#"/>
					</td>
					<td align="center">${proxyPayBatch.totalSuccNum}</td>
					<td align="center">
						<fmt:formatNumber value="${proxyPayBatch.totalSuccMoney}" pattern="#,##0.00#"/>
					</td>
					<td align="center"><fmt:formatDate value="${proxyPayBatch.createTime}" type="both"/></td>
				
					<td align="center">
						<c:choose>
							<c:when test="${proxyPayBatch.reviewStatus=='0' }">已提交</c:when>
							<c:when test="${proxyPayBatch.reviewStatus=='1' }">已复核</c:when>
							<c:when test="${proxyPayBatch.reviewStatus=='2' }">已驳回</c:when>
						</c:choose>
					</td>
					<td>
						<a href="proxyPayBatch/detail?bath_no=${proxyPayBatch.batchId}">
					                  明细
					   </a>
					</td>
					</tr>
			</c:forEach>
		</table>
		<%@include file="/common/pager/pager.jsp"%>
	</sf:form>
</body>
</html>
