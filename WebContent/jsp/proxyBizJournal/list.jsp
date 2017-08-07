
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
		
	</script>
</head>

 <body bgcolor='#FFFFFF' text='#000000' leftmargin='0' topmargin='0' marginwidth='0' marginheight='0' >
		<sf:form modelAttribute="form" method="POST" cssClass="listForm" name="form1" id="form1">
			<table border="1" class="Table_N" cellspacing="0" cellpadding="5"
				align="center" width="100%">
				<tr class="Table_H">
					<td nowrap colspan="25" class="Table_H" height="40">
						交易查询
					</td>
				</tr>
				<tr class="Table_H">
					<td nowrap colspan="25" height="50" valign="middle">
						开始时间:
					    <sf:input path="startDate" onClick="WdatePicker({dateFmt:'yyyyMMdd'})" autocomplete="off"/>
					             结束时间:
					    <sf:input path="endDate" onClick="WdatePicker({dateFmt:'yyyyMMdd'})" autocomplete="off"/>
					             交易类型:
					    <sf:select path="bizType">
								<sf:option value="" label="-- 全部 --"></sf:option>
								<sf:option value="2" label="批量代付"></sf:option>
								<sf:option value="3" label="代付工资"></sf:option>
						</sf:select><br/><br/>
						<sf:input path="merchId" type="hidden"/>
					
						付款人名
						<sf:input path="fromAccName" maxlength="15" size="20"/>						
						
						付款卡号
						<sf:input path="fromAccNo" maxlength="30" size="35"/>										
						  
						收款人名
						<sf:input path="toAccName" maxlength="15" size="20"/><br/><br/>						
						
						收款卡号
						<sf:input path="toAccNo" maxlength="30" size="35"/>										
						 
						<input type="submit" value="查询" name="queryButton" class="button">	
					</td>
				</tr>
				<tr class="Table_H">
					<td nowrap align="center">
						交易类型
					</td>
					<td nowrap align="center">
						交易时间
					</td>
					<td nowrap align="center">
						商户号
					</td>
					<td nowrap align="center">
						金额
					</td>
					<td nowrap align="center">
						扣款人名
					</td>
					<td nowrap align="center">
						扣款账号
					</td>					
					<td nowrap align="center">
						收款人名
					</td>
					<td nowrap align="center">
						收款账号
					</td>
					<td nowrap align="center">
						交易批次号
					</td>
					<td nowrap align="center">
						交易流水号
					</td>
					<td nowrap align="center">
						交易状态
					</td>
					<td nowrap align="center">
						状态描述
					</td>
					<td nowrap align="center">
						备注
					</td>
				</tr>
		
				<c:forEach items="${form.pager.datas}" var="journal" varStatus="i"> 
					<c:choose>
						<c:when test="${i.count%2==0 }">
							<c:set var="rowClass" value="table_select_bg"></c:set>
						</c:when>
						<c:otherwise>
							<c:set var="rowClass" value=""></c:set>
						</c:otherwise>
					</c:choose>
					
					<tr class="${rowClass }">
						<td align="center">
							<c:choose>
								<c:when test="${journal.bizType=='2'}">批量代付</c:when>
								<c:when test="${journal.bizType=='3'}">代发工资</c:when>
							</c:choose>
							
						</td>
						<td align="center" nowrap>
							${journal.createTime }
						</td>
						<td align="center" nowrap>
							${journal.merchId}
						</td>
						<td align="center" nowrap>
							
							<fmt:formatNumber value="${journal.money}" pattern="#,##0.00#"/>
						</td>
							<td align="center" nowrap>
							${journal.fromUserName }
						</td>
							<td align="center" nowrap>
							${journal.fromBankCardNo }
						</td>
							<td align="center" nowrap>
							${journal.toUserName }
						</td>
							<td align="center" nowrap>
							${journal.toBankCardNo }
						</td>
							<td align="center" nowrap>
							${journal.batchId }
						</td>
						<td align="center" nowrap>
							${journal.id }
						</td>
						<td align="center" nowrap>
							${journal.handleStatus }
						</td>
						<td align="center" nowrap>
							${journal.handleRemark }
						</td>
							<td align="center" nowrap>
							${journal.memo }
						</td>
					</tr>	
				</c:forEach>			
			</table>		
			<%@include file="/common/pager/pager.jsp"%>
		</sf:form>
	</body>
</html>
