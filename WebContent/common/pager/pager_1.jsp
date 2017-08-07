<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>



<span id="pager"></span>
<sf:hidden path="pager.pageNo" id="pageNumber"/>
<sf:hidden path="pager.pageSize" id="pageSize"/>
<sf:hidden path="pager.totalPages" id="pageTotal"/>
