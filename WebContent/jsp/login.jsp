<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
		<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
		<title>login</title>		
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bmw.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/jquery-ui.css" type="text/css" media="screen"/>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.0.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.form.js" ></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.js"></script>
		<script  type="text/javascript">
		function create_button(){
			$('.button').each(function(){
				var icon = $(this).attr('icon');
				var settings = {};
				var text = !($(this).hasClass('notext'));
				if (typeof(icon) != "undefined")
				{
					settings = {icons:{primary:"ui-icon-"+icon},text:text};
				}
				$(this).button(settings);
			});
		}
		$(document).ready(function(){
			create_button();
			$('#btnLogin').click(function() {
				if($('#frmLogin').valid()){
					$('#frmLogin').submit();
				}else{
					alert("填写字段有误，请检查字段后的错误提示！");
				}
			});

			$('#frmLogin').validate({	
				rules: {
					userName:{required:true},
					password:{required:true}
				},
				messages: {
					userName:{required:"请输入登录账号！"},
					password:{required:"请输入密码!"}, 
				}
			});
       });
</script>
</head>
<body>
<div class="container">
	<div class="grid">
		<div id="loginHeader">
			<div id="loginLogo"></div>
		</div>
	</div>	
    <div class="rightBigPad leftBigPad divContent">
    	<sf:form modelAttribute="user" method="POST" >
    		<div id="loginDialog">
				<div id="loginTitle">请登录</div>
					<div class="table-write">
						<table width=90% cellspacing=1 cellpadding=1>
							<tr valign=top><td>
								<p>
									<label>用户名:</label>
									<span><sf:input path="userName" /></span>
								</p>
							</td></tr>
							<tr valign=top><td>
								<p>
									<label>密码:</label>
									<span><sf:input path="password" type="password"/></span>
								</p> 
							</td></tr>
							<tr><td align=right>
								<a id="btnLogin" class="button" icon="key">登录</a>
							</td></tr>
						</table>
					</div>
			</div>
			<div>
				<label class="error">${error }</label>
			</div>
    	</sf:form>		
	</div>
	<div id="footer"></div>
</div>	
</body>

</html>