<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bmw.css" type="text/css" media="screen" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/jquery-ui.css" type="text/css" media="screen"/>
<script type="text/javascript"
	src="http://app.mapabc.com/apis?t=javascriptmap&v=3&key=9212b51b6021d6172100acb6437a412befdc072df334fd408930cf6213c6eef85aaa9470b462cd61"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-ui.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/tools.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/map.js"></script>
<script type="text/javascript">
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
	function fnOpenNormalDialog() {
	    	    $("#dialog-confirm").dialog({
	    	        resizable: true,
	    	        modal: false,
	    	        title: "Modal",
	    	        height: 750,
	    	        width: 500,
	    	    });
	    	}
	$(document).ready(function() {
		create_map();
		$( "#tabs" ).tabs();
		$('.accordionPDiv').live('click',function() {
		$(this).next('.accordionCDiv').toggle();
		var icon = $(this).find("a");
		icon.toggleClass("ui-icon-circle-arrow-s ui-icon-circle-arrow-n");
		return false;
	});
		create_button();
		//$("#tabs-1").css('display','none');
		$("#dialog").bind('click',function(){
			$("#dialog-confirm").load('.css.html');
			fnOpenNormalDialog()
		});
		$("#getPsap").bind("click",function(){
			var lat =$("#lat").val();
			var lon =$("#lon").val();
			setBDPoint(lon,lat);
		});
	});
</script>
</head>
<body>
	<div id="dialog-confirm"></div>
	<div id="wrapper">
		<div id="head">	
			<div style="float:left;width:50%;padding-top:5px;" id="loginLogo">				
			</div>	
			<div style="float:right;width:50%">
				<div id="headMenu" class="right padded">
					
					<a href="#" id="dialog">管理员</a> |
					
					<a href="@routes.ServiceAction.toList()">案件列表</a> |
					<a href="@routes.ServiceAction.requestService(-1)">请求案件</a> |
					<a href="@routes.UserSettingAction.profile()">用户设置</a> |
					<a href="@routes.UserAction.logout()">注销</a> 
				</div>
				<div class="clear"></div>
				<div id="headSubInfo" class="right padded">@username  登录在AllianzAssistance </div>
			</div>				
		</div>
		<div class="clear"></div>
		<div id="contentCerter">
			<div id="contentLeft" class="contentLeft">
				<div id="contentLeftMainTab"
					class="ui-tabs ui-widget ui-widget-content ui-corner-all">
				<div id="tabs">
					<ul>
						<li><a href="#tabs-1">psap</a></li>
						<li><a href="#tabs-2">POI</a></li>
					</ul>
					<div id="leftdiv" style="height: 750px;overflow:auto;">
						<div id="tabs-1">
	    		   		   <div class="clear spacer_5"></div>
							<div class="accordionPDiv">
								<table>
									<tr>
										<td><a class="ui-icon ui-icon-circle-arrow-s right" href="#"></a></td>
										<td><img src="${pageContext.request.contextPath}/images/car_repair.png"/></td>
										<td>psap 信息</td>
									</tr>
								</table>	
							</div>
							<div class="accordionCDiv">
								<p>
									<label>经度(值大的):</label>
									<input id="lon" name="lon" value=""/>
								</p>
								<p>
									<label>纬度(值小的):</label>
									<input id="lat" name="lat" value=""/>
								</p>
								<a id="getPsap" class="button right" icon="arrowthick-1-e"  href="#">获取</a>
								<div id="psapt"></div>
					        </div>
	  					</div>
	 					<div id="tabs-2">
	   						 <p>POI,.</p>
	  					</div>
  					</div>
				</div>
					
				</div>

			</div>			
			<div id="contentRight" class="contentRight">
				<div id="contentRightMainTab"
					class="ui-tabs ui-widget ui-widget-content ui-corner-all">

					<div class="tabContent">
						<div id="mapObj" style="height: 790px;"></div>
					</div>
				</div>
			</div>
		</div>

	<div id="footer">
	</div>
	</div>
</body>
</html>