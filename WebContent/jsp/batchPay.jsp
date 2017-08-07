<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>单笔代收列表:</title>
    
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
</head>

<body bgcolor='#FFFFFF' text='#000000' leftmargin='0' topmargin='0'
	marginwidth='0' marginheight='0'>
	<sf:form modelAttribute="form" method="POST" cssClass="listForm"
		name="form1" id="form2">
		<table border="1" class="Table_N" cellspacing="0" cellpadding="5"
			align="center" width="100%">
			<tr class="Table_H">
				<td nowrap colspan="15" class="Table_H" height="40">单笔代收列表:</td>
			</tr>
			<tr class="Table_H">
				<td nowrap colspan="15" height="50" valign="middle">
					开始日期:<sf:input path="startDate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" autocomplete="off"/>
					&nbsp;
					结束日期:<sf:input path="endDate" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" autocomplete="off"/>
					&nbsp;&nbsp;<br/>
					账户名:<sf:input path="acctname"/>
					&nbsp;&nbsp;
					账号:<sf:input path="acctno"/>
					&nbsp;&nbsp;
					处理状态:
					<sf:select path="review_status">
						<sf:option value="">全部</sf:option>
						<sf:option value="0">已提交</sf:option>
						<sf:option value="1">已审核</sf:option>
						<sf:option value="2">已驳回</sf:option>
					</sf:select>
					&nbsp;&nbsp;
					交易结果:
					<sf:select path="handle_status">
						<sf:option value="">全部</sf:option>
						<%-- <sf:option value="0">交易处理中</sf:option> --%>
						<sf:option value="fail">交易失败</sf:option>
						<sf:option value="succ">交易成功</sf:option>
					</sf:select>
					&nbsp;&nbsp;&nbsp;
					<input type="submit" value="查询" name="queryButton" class="button">
						<input	type="button" value="单笔代收" class="button"
						onclick="javascript:JqueryDialog.Open('','<%=basePath%>proxy_acp/new',700,400);" />
				</td>
			</tr>
			<tr class="Table_H">
				<td align="center" nowrap>账户名</td>
				<td align="center">扣款账号</td>
				<td align="center">扣款人开户银行</td>
				<td align="center">业务费项</td>
				<td align="center">交易金额</td>
				<td align="center" nowrap>交易结果</td>
				<td align="center" width="100px">交易结果描述</td>
				<td align="center">时间</td>
				<td align="center" nowrap>处理状态</td>
				<td align="center">操作</td>
			</tr>
			<c:forEach items="${form.pager.datas}" var="proxyAcp" varStatus="i">
				<c:choose>
					<c:when test="${i.count%2==0 }">
						<c:set var="rowClass" value="table_select_bg"></c:set>
					</c:when>
					<c:otherwise>
						<c:set var="rowClass" value=""></c:set>
					</c:otherwise>
				</c:choose>
				<tr class="${rowClass }">
					<td align="center" nowrap>${proxyAcp.accname}</td>
					<td align="center">${proxyAcp.accno }</td>
					<td align="center">
						${proxyAcp.bank_name}
					</td>
					<td align="center">${proxyAcp.feeitem }</td>
					<td align="center">
						<fmt:formatNumber value="${proxyAcp.money}" pattern="#,##0.00#"/>
					</td>
					<td align="center">
						<c:choose>
							<c:when test="${proxyAcp.handle_status == '00'}">
								成功
							</c:when>
							<c:otherwise>
								${proxyAcp.handle_status }
							</c:otherwise>
						</c:choose>	
					</td>
					<td align="center">
						<c:out value="${proxyAcp.handle_remark }" escapeXml="true"/>
					</td>
					<td align="center">${proxyAcp.create_datetime}</td>
					<td align="center">
						<c:choose>
							<c:when test="${proxyAcp.review_status=='0' }">已提交</c:when>
							<c:when test="${proxyAcp.review_status=='1' }">已复核</c:when>
							<c:when test="${proxyAcp.review_status=='2' }">已驳回</c:when>
						</c:choose>
					</td>
					<td>
							
								<input type="button" value="查看" onClick="javascript:JqueryDialog.Open('','<%=basePath%>proxy_acp/view?id=${proxyAcp.id}',700,400);"
									class="button2">
					</td>
					</tr>
			</c:forEach>
		</table>
		<%@include file="/common/pager/pager.jsp"%>
	</sf:form>
</body>

</html>
