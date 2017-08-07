<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<html>
<head>
<base href="<%=basePath%>">
<title>菜单页</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="菜单页">
<script src="js/cmp.js"></script>
<link href="css/leftTree.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
	function turnCl(obj) {
		var all_list = document.getElementsByTagName("li");
		var i = 0, temp, Send, Sleft;
		for (i = 0; i < all_list.length; i++) {
			temp = all_list[i].className;
			if (temp.length > 3) {
				Send = temp.substring(temp.length - 3, temp.length);
				if (Send == "red") {
					Sleft = temp.substring(0, temp.length - 4);
					all_list[i].className = Sleft;
				}
			}
		}
		obj.className = obj.className + "_red";
	}

</script>
</head>
<body style="overflow: auto;">
	<table style="background: #E0DBD6;">
		<tbody>
			<tr>
				<td width="100%" class="MainCMP_Left">
					<div class="TopCMP_SecMenuBG">
						<ul>
							<c:forEach items="${resources}" var="resource">
								<!-- 一级菜单开始 -->
									<%-- TopCmp_Sec --%>
									<li class="TopCMP_SecMenuBG_li1"
										id="num<c:out value='${resource.id }'/>"
										onclick="change(this,null,'<c:out value='${resource.id }'/>');turnCl(this);">
										<div class="left_menu_more">
											<c:out value="${resource.name }" />
										</div>
									</li>
									<!-- 一级菜单结束  -->

									<!-- 二级菜单开始 -->
									<li id="show<c:out value='${ resource.id }'/>"
										style="display: none; overflow: hidden;"
										class="leftMenu_height_hack">
										<% // long start = System.currentTimeMillis(); %>
										<c:forEach items="${resource.children }" var="resource2">
											<c:if test="${resource2.type == '0'}">
											
													<ul>
														<%--${fn:length(rowList)>2 --%>
														<c:set var="expandable" value="false"></c:set>
														
														<c:forEach items="${resource2.children}" var="resource3">
															<c:if test="${!expandable }">
																<c:if test="${resource3.type == '0' }">
																	<c:set var="expandable" value="true"></c:set>
																</c:if>
															</c:if>
														</c:forEach>
														
														<c:choose>
															<c:when test="${!expandable }">
																<%--三级功能项，非菜单 --%>
																<li id="num<c:out value='${resource2.id }'/>"
																	class="TopCMP_SecMenuBG_li2" onClick="turnCl(this);">
																	<div class="left_menu_less2_2"
																		onClick="submitOutTX('<c:url value='${resource2.url }'/>');">
																		<c:out value='${resource2.name}' />
																	</div>
																</li>
															</c:when>
															<c:otherwise>
																<%--三级菜单 --%>
																<li id="num<c:out value='${resource2.id }'/>"
																	class="TopCMP_SecMenuBG_li3"
																	onclick="change(this,null,'<c:out value='${resource2.id }'/>');turnCl(this);">
																	<div class="left_menu_more2">
																		<c:out value='${resource2.name}' />
																	</div>
																</li>															
																
																<li id="show<c:out value='${ resource2.id }'/>"
																	style="display: none; overflow: hidden;"
																	class="leftMenu_height_hack">
																	<c:forEach items="${resource2.children }" var="resource3">																	
																		<c:if test="${resource3.type == 0 }">
																			
																				<ul>
																					<li id="num<c:out value='${resource3.id }'/>"
																						class="TopCMP_SecMenuBG_li3"
																						onClick="turnCl(this);">
																						<div class="left_menu_less3"
																							onClick="submitOutTX('<c:url value='${resource3.url }'/>');">
																							<c:out value='${resource3.name}' />
																						</div>
																					</li>
																				</ul>
																		</c:if>
																		
																	</c:forEach>	
																</li>
															</c:otherwise>
														</c:choose>
													</ul>
											</c:if>
										</c:forEach>
									</li>
									<!-- 二级菜单结束 -->
								
							</c:forEach>
						</ul>
					</div>
				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>
