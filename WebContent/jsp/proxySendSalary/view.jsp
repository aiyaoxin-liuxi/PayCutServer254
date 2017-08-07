
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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

<title>单笔代付信息:</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="单笔代付信息:">
<link href="css/css5/jh.css" rel="stylesheet" type="text/css" />
<link href="css/css5/ccb.css" rel="stylesheet" type="text/css" />
<link href="css/css5/B.css" rel="stylesheet" type="text/css" />
<link type="text/css" rel="stylesheet" href="css/css5/jquery_dialog.css" />
<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/jquery_dialog.js"></script>
</head>

<body>
<body bgcolor='#FFFFFF' text='#000000' leftmargin='0' topmargin='0'
	marginwidth='0' marginheight='0'>
	<table border="1" class="Table_N" cellspacing="0" cellpadding="5"
		align="center" width="100%">
		<tr class="Table_H">
			<td nowrap colspan="2" class="Table_H" height="30">单笔代付信息:<font
				color="red"> </font></td>
		</tr>
		<tr>
			<td align="left" style="vertical-align: middle" width="15%">收款人账户名</td>
			<td>${proxyPay.accname}</td>
		</tr>
		<tr>
			<td align="left" style="vertical-align: middle" width="15%">收款人银行账号</td>
			<td>${proxyPay.accno}</td>
		</tr>
		<tr>
			<td align="left" style="vertical-align: middle" width="15%">收款人开户行名称</td>
			<td>
				<c:set value="${proxyPay.bankno}" var="bankno"/>
		<%-- 		<%
					try{
						ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
						BaseDao<BankSwiftCode> bankSwiftCodeDao = (BaseDao<BankSwiftCode>)ctx.getBean("bankSwiftCodeDao");
						BankSwiftCode bankSwiftCode = bankSwiftCodeDao.get(pageContext.getAttribute("bankno")+"");
						pageContext.setAttribute("bank_name", bankSwiftCode.getBank_name());
					}catch(Exception e){
						
					}
				%> --%>
				${bank_name}
			</td>
		</tr>
		<tr>
			<td align="left" style="vertical-align: middle" width="15%">业务费项</td>
				<td>${proxyPay.feeitem}</td>
		</tr>
		<tr>
			<td align="left" style="vertical-align: middle" width="15%">金额(单位:元)</td>
			<td>${proxyPay.money}</td>
		</tr>
		<tr>
			<td align="left" style="vertical-align: middle" width="15%">时间</td>
			<td>${proxyPay.create_datetime}</td>
		</tr>
		<tr>
			<td align="left" style="vertical-align: middle" width="15%">操作员</td>
			<td>${proxyPay.creator_id}</td>
		</tr>
		<tr>
			<td align="left" style="vertical-align: middle" width="15%">复核时间</td>
			<td>${proxyPay.review_datetime}</td>
		</tr>
		<tr>
			<td align="left" style="vertical-align: middle" width="15%">复核操作员</td>
			<td>${proxyPay.reviewer_id}</td>
		</tr>
		<tr>
			<td align="left" style="vertical-align: middle" width="15%">处理状态</td>
			<td>
				<c:choose>
					<c:when test="${proxyPay.review_status=='0'}">已提交</c:when>
					<c:when test="${proxyPay.review_status=='1'}">已复核</c:when>
					<c:when test="${proxyPay.review_status=='2'}">已驳回</c:when>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td align="left" style="vertical-align: middle" width="15%">驳回原因</td>
			<td>${proxyPay.review_comments}</td>
		</tr>
		<tr>
			<td align="left" style="vertical-align: middle" width="15%">交易结果</td>
			<td>${proxyPay.handle_status}</td>
		</tr>
		<tr>
			<td align="left" style="vertical-align: middle" width="15%">交易描述</td>
			<td>${proxyPay.handle_remark}</td>
		</tr>
	</table>
			<input type="button" value="<spring:message code="global.button.back"/>" class="button"
				onclick="javascript:parent.window.JqueryDialog.Close();" />
</body>
</html>
