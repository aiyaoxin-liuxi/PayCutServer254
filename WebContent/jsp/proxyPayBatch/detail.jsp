<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
		<title>批量明细:</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="批量明细">
	<link href="css/css5/jh.css" rel="stylesheet" type="text/css" />
	<link href="css/css5/ccb.css" rel="stylesheet" type="text/css" />
	<link href="css/css5/B.css" rel="stylesheet" type="text/css" />
	<link href="common/pager/pager.css" rel="stylesheet" type="text/css">
	<link type="text/css" rel="stylesheet" href="css/css5/jquery_dialog.css" />
	<script language="JavaScript" src="js/jquery.js"></script>
	<script type="text/javascript" src="common/pager/jquery.pager.js"></script>
	<script type="text/javascript" src="common/pager/list.js"></script>
	<script type="text/javascript" src="js/jquery_dialog.js"></script>
        <script type="text/javascript">
		  $(document).ready(function(){
        		$('#submitButton').click(function(){
        			
        			var reviewStatus=$("input:radio[name='reviewStatus']:checked").val();
        			if('2'==reviewStatus){
        				var reviewComments=$("#reviewComments").val();
        				if(reviewComments.length==0){
        					alert("原因不能为空");
        					return false;
        				}
        			}
        		});
        	}); 
        	/* $(function(){
        		$('#submitButton').bind('click');
        	}); */
        </script>
	</head>
	<body bgcolor='#FFFFFF' text='#000000' leftmargin='0' topmargin='0' marginwidth='0' marginheight='0'>
		
		 <table border="1" class="Table_N" cellspacing="0" cellpadding="5"
				align="center" width="100%">
				<tr class="Table_H">
					<td nowrap colspan="15" class="Table_H" height="30">
						批量代付明细:<font color="red"> </font>
						<input type="button" value="返回" class="button" onclick="javascript:history.go(-1);" />
					</td>
				</tr>
				<tr class="Table_H">
					<td>商户号</td>
					<td>账户名</td>
					<td>付款账号</td>
					<td>付款人开户银行</td>
					<td>联行号</td>
					<td>渠道名</td>
					<td>交易金额</td>
					<td>处理状态</td>
				
				</tr>
				<c:forEach items="${list}" var="proxyPayBatch">
				<tr>
					<td>${proxyPayBatch.merchId}</td>
					<td>${proxyPayBatch.accName}</td>
					<td>${proxyPayBatch.accNo}</td>
					<td align="center">
						${proxyPayBatch.bankName}
					</td>
					<td>${proxyPayBatch.bankcode}</td>
					<td>
					<c:choose>
							<c:when test="${proxyPayBatch.channelId=='1'}">广发</c:when>
						</c:choose>
					</td>	
					<td>${proxyPayBatch.money}</td>	
					<td>
						<c:choose>
							<c:when test="${proxyPayBatch.reviewStatus=='0'}">已提交</c:when>
							<c:when test="${proxyPayBatch.reviewStatus=='1'}">已复核</c:when>
							<c:when test="${proxyPayBatch.reviewStatus=='2'}">已驳回</c:when>
						</c:choose>
					</td>
				
				</tr>
				</c:forEach>	
			</table>
			<br/>
			<c:choose>
			<c:when test="${proxyBatch.reviewStatus=='0'}">
			
				<sf:form modelAttribute="proxyBatch" method="POST" action="proxyPayBatch/audit">
					 <table border="1" class="Table_N" cellspacing="0" cellpadding="5"
						align="center" width="100%">
						
						<tr class="Table_H">
							<td nowrap colspan="2" class="Table_H" height="30">
								批量代付信息:
								<font color="red"> </font>
								<sf:input  type="hidden" path="batchId" />
							</td>
						</tr>
						 <tr>
		                     <td width="15%">复核</td>
							<td>
								通过:<sf:radiobutton path="reviewStatus" value='1' checked="true"/>
								驳回:<sf:radiobutton path="reviewStatus" value='2' />
							</td>
		                 </tr>
						<tr >
			                 <td width="15%">驳回原因</td>
							 <td>
									<sf:textarea path="reviewComments"/>
									<sf:errors path="reviewComments" cssClass="error"></sf:errors>
							</td>
		                 	</tr>
		                 	<tr>
		                 		<td>
		                 			<input id="submitButton" type="submit" value="确定" class="button"/>
		                 		</td>
		                 	</tr>
						</table>
				</sf:form>
		</c:when>
		</c:choose>
	</body>
</html>
