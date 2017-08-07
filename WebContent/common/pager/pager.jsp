<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>

<script type="text/javascript">
$().ready( function() {
	$("#pager").pager({
		pageSize: ${form.pager.pageSize},
		totalCount: ${form.pager.total},
		pagenumber: ${form.pager.pageNo},
		pagecount: ${form.pager.totalPages},
		buttonClickCallback: $.gotoPage	
	});

})
</script>
<span id="pager"></span>
<sf:hidden path="pager.pageNo" id="pageNumber"/>
<sf:hidden path="pager.pageSize" id="pageSize"/>
<sf:hidden path="pager.totalPages" id="pageTotal"/>
