/*var mapObj,toolbar,overview,scale,mouseTool; var opt = {
		level:10,//初始地图视野级别
		center:new MMap.LngLat(116.397428,39.90923),//设置地图中心点
		doubleClickZoom:true,//双击放大地图
		scrollwheel:true//鼠标滚轮缩放地图
};
function create_map()
{
	mapObj = new MMap.Map("mapObj",opt);
	mapObj.plugin(["MMap.ToolBar","MMap.Scale"],function()
	{
		toolbar = new MMap.ToolBar();
		mapObj.addControl(toolbar);
		scale = new MMap.Scale(); //加载比例尺
		mapObj.addControl(scale);
	});
	 mapObj.plugin("MMap.MouseTool",function(){  
		  
	        mouseTool = new MMap.MouseTool(mapObj);//构造鼠标工具实例    
	  
	    });  
	 addContentMenu();
}*/
 jQuery.support.cors = true;
	var mapObj, toolbar, overview, scale, mouseTool;
	var opt = {
		level : 10,
		center : new MMap.LngLat(116.397428, 39.90923),
		doubleClickZoom : true,
		scrollwheel : true
	};
	function create_map() {
		mapObj = new MMap.Map("mapObj", opt);
		mapObj.plugin([ "MMap.ToolBar", "MMap.Scale" ], function() {
			toolbar = new MMap.ToolBar({offset:new MMap.Pixel(540,10) });
			mapObj.addControl(toolbar);
			scale = new MMap.Scale(); 
			mapObj.addControl(scale);
		});
	}
	
	
function setBDPoint(longitude,latitude){
	if((!latitude)&&(!longitude)){
		return;
	}
	if(longitude<latitude){
		alert("经度不能小于纬度");
		return ;
	}
	var lon,lat;
	if(!(latitude.indexOf('.')>=2&&longitude.indeOf('.')>=3)){
		if(latitude.length>7&&longitude.length>7){
			 lon = longitude/3600000+"";
			 lat = latitude/3600000+"";
		}
	}else{
		lon = longitude+"";
		lat = latitude+"";
	}
	mapObj.clearOverlays();
	var geocoderOption = {
			range:500,//范围
			crossnum:2,//道路交叉口数
			roadnum	:3,//路线记录数
			poinum:2//POI点数
	};
	var geocoder = new MMap.Geocoder(geocoderOption);
		geocoder.regeocode(new MMap.LngLat(lon,lat),function(data){
			var postalcode = '';	
			var cityName = '';
			var cityCode = '';
			var nearpoiName  = '';
			var distance     = '';
			var provinceName = '';
			var roadName     = '';
			var districtName = '';
			if(data.list){
				 if(data.list[0].district){
		                if(data.list[0].district.code){
		                postalcode = data.list[0].district.code;
		               }
		                 if(data.list[0].district.name){
		               	 districtName = data.list[0].district.name;
		               }
		            }
		            if(data.list[0].city){
		                if(data.list[0].city.name){
		                    cityName = data.list[0].city.name;
		                  }
		                 if(data.list[0].city.citycode){
		                    cityCode = data.list[0].city.citycode;
		                  }
					}
		             if(data.list[0].province){
		                if(provinceName = data.list[0].province.name){
		                    provinceName = data.list[0].province.name;
		                  }
					}
		            if(data.list[0].roadlist){
		                if(data.list[0].roadlist[0].name){
		                    roadName = data.list[0].roadlist[0].name;
		                  }
					}
		            if(data.list[0].poilist){
		                if(data.list[0].poilist[0].name){
		                    nearpoiName = data.list[0].poilist[0].name;
		                  }
		                  if(data.list[0].poilist[0].distance){
		                   distance = data.list[0].poilist[0].distance;
		                  }
					}
			}else{
				alert('qoros 提供的经纬度有误 不能定位');
				return;
			}
			
			var lonstr =lon.substr(0,6);
			var latstr =lat.substr(0,6);
			var carPosition = provinceName+" "+cityName+" "+districtName+" "+roadName+" "+nearpoiName;
			var car1 = cityName+" "+roadName+" "+districtName+" "+nearpoiName;
			var info ="经度: "+lonstr+" 纬度:"+latstr+" 区号:" +cityCode+" <br>"+carPosition;
			var carposition =[{"longitudePoint":lon, "latitudePoint":lat, "info":info}];
			$('#lat').text(latstr);
			$('#lon').text(lonstr);
			$('#districtCode').text(cityCode);
			$('#carPosition').text(carPosition);
			$.each(carposition, function(index, value) {
				add_car(index,value.longitudePoint,value.latitudePoint,value.info);
			});
			//$.post(encodeURI('/poi/injectCityInfo/'+serviceId+'?'+Math.random()),{'postalcode':postalcode,'cityName':provinceName,'nearAddress':car1},function(data){
				setPsap(longitude,latitude,postalcode);
			//});

	   });
}

function requestPsap(longitude,latitude,postalcode){
	$.ajax({
		url:getCurrentContextPath()+'app/psap/'+longitude+'/'+latitude+'/'+postalcode,
		type:'GET',
		cache:'false',
		dataType:'json',
		timeout:'100000',
		error:function(error){
			alert("error:"+error);
		},
		success:function(data){
			 $('#psapt').children().empty();
			//$('#psapt').empty();
			var length = data.length;
			var mapA = new Array();
			if(length>0){
				var pointIndex =0;
				for(var i=0;i<length;i++){
					var psapdata =data[i];
					var name = psapdata.name;	
						$('#psapt').append('<table border=0 width=100% style="background-color:#e9e9e9">'
								+'<tr>'
								+'<td width="15%" style="font-weight:bold;font-size:11px">描述:</td>'
								+'<td colspan="2"><span><p>'+psapdata.description+'</span></p></td>'
								+'</tr>'
								+'<tr>'
								+'<td style="font-weight:bold;font-size:11px">医院名:</td>'
								+'<td colspan="2"><span><p>'+psapdata.name+'</span></p></td>'
								+'</tr>'
								+'<tr>'
								+'<td style="font-weight:bold;font-size:11px">第一联系人:</td>'
								+'<td><span><p>'+psapdata.primaryPhoneNumber+'('+psapdata.callDescription+')</span></p></td>'
								+'<td width="15%"><a class="button btnCall" icon="arrowthick-1-e" href="#">呼出</a></td>'
								+'</tr>'
								+'<tr>'
								+'<td style="font-weight:bold;font-size:11px">第二联系人:</td>'
								+'<td><span><p>'+psapdata.secondaryPhoneNumber+'</span></p></td>'
								+'<td><a class="button btnCall" icon="arrowthick-1-e" href="#">呼出</a></td>'
								+'</tr>'
								+'<tr>'
								+'<td style="font-weight:bold;font-size:11px">地址:</td>'
								+'<td colspan="2"><span><p>'+psapdata.addresses+'</span></p></td>'
								+'</tr>'
								+'<tr>'
								+'<td colspan="3"></td>'
								+'</tr>'
								+'</table><div class="clear spacer_5"></div>');
						if(!(name.indexOf("110")>0||name.indexOf("120")>0||name.indexOf("119")>0||name.indexOf("122")>0)){		
							mapA[pointIndex] ={"longitudePoint":psapdata.longitude, "latitudePoint":psapdata.latitude, "info":psapdata.name+""};
							pointIndex++;
					    }
				}
				create_button();
			}
			
			$.each(mapA, function(index, value) {
				add_mark(index,value.longitudePoint,value.latitudePoint,value.info);
			});
		}
	});
}
function setPsap(latitude,longitude,serviceId){
	 longitude = longitude/3600000;
	 latitude = latitude/3600000;
		var geocoder = new MMap.Geocoder();
		/*geocoder.regeocode(new MMap.LngLat(longitude,latitude),function(data){
		   var districtCode = data.list[0].district.code;	*/
		requestPsap(latitude,longitude,serviceId);
			
}

function addContentMenu(){ 
	  
    //构造 ContextMenu 新实例 
  
    var contextMenu = new MMap.ContextMenu({ 
  
        isCustom:false, 
  
        width:180 
    }); 
  
    //添加菜单项 
    contextMenu.addItem("加点",function(e){ 
    	var lon = xy.lng+"";
    	var lat = xy.lat+"";
    	mapObj.clearOverlays();
    	var geocoder = new MMap.Geocoder();
    	geocoder.regeocode(new MMap.LngLat(lon,lat),function(data){
    		var postalcode = '';	
			var cityName = '';
			var cityCode = '';
			var nearpoiName  = '';
			var distance     = '';
			var provinceName = '';
			var roadName     = '';
			var districtName = '';
			 if(data.list[0].district){
	                if(data.list[0].district.code){
	                postalcode = data.list[0].district.code;
	               }
	                 if(data.list[0].district.name){
	               	 districtName = data.list[0].district.name;
	               }
	            }
	            if(data.list[0].city){
	                if(data.list[0].city.name){
	                    cityName = data.list[0].city.name;
	                  }
	                 if(data.list[0].city.citycode){
	                    cityCode = data.list[0].city.citycode;
	                  }
				}
	             if(data.list[0].province){
	                if(provinceName = data.list[0].province.name){
	                    provinceName = data.list[0].province.name;
	                  }
				}
	            if(data.list[0].roadlist){
	                if(data.list[0].roadlist[0].name){
	                    roadName = data.list[0].roadlist[0].name;
	                  }
				}
	            if(data.list[0].poilist){
	                if(data.list[0].poilist[0].name){
	                    nearpoiName = data.list[0].poilist[0].name;
	                  }
	                  if(data.list[0].poilist[0].distance){
	                   distance = data.list[0].poilist[0].distance;
	                  }
				}
			var car1 = cityName+" "+roadName+" "+districtName+" "+nearpoiName;
			var lonstr =lon.substr(0,6);
			var latstr =lat.substr(0,6);
			var carPosition = provinceName+" "+cityName+" "+districtName+" "+roadName;
			$('#lat').text(latstr);
			$('#lon').text(lonstr);
			$('#districtCode').text(cityCode);
			$('#carPosition').text(carPosition);
			var info ="经度: "+lonstr+" 纬度:"+latstr+" 区号:" +cityCode+" <br>"+carPosition;	
			add_car("加点",lon,lat,info);
			if(postalcode==''){
				postalcode = cityCode;
			}
			$.get(encodeURI('/poi/injectCityInfo/'+postalcode+'/'+provinceName+'/'+car1+'/'+serviceId),function(data){
				if(serviceId!=-1){
					requestPsap(latstr,lonstr,serviceId);
				}
				
			});
	   });
       
  
    },0); 
  
    contextMenu.addItem("测距",function(e){ 
  
    	mouseTool.rule(); 
  
    },1); 
    contextMenu.addItem("取消测距",function(e){ 
    	  
    	mouseTool.close(); 
  
    },2); 
  
    //绑定右键单击事件，打开右键菜单 
  
    mapObj.bind(mapObj,"rightclick",fun5=function(e){ 
  
        contextMenu.open(mapObj,e.lnglat); 
        xy = e.lnglat;
  
    }); 
  
} 
function add_mark(markerID,longitudePoint,latitudePoint,markerContent)
{
	var marker = new MMap.Marker({                     
		   id:markerID, //marker id                    
		   position:new MMap.LngLat(longitudePoint,latitudePoint),   
		   icon:getCurrentContextPath()+"images/lan_1.png", 
		   offset:new MMap.Pixel(-11.5,-32)  
	   });  
	mapObj.addOverlays(marker); 
	mapObj.setFitView();//设置地图合适视野级别 
	
    mapObj.bind(marker,"click",function(e){ 
    	inforWindow = new MMap.InfoWindow({ 
    		  
    	      content:markerContent, 
    	  
    	      offset:new MMap.Pixel(-106,-61) 
    	  
    	    }); 
        inforWindow.open(mapObj,marker.getPosition());   
  
    });  
}

function add_car(markerID,longitudePoint,latitudePoint,markerContent)
{
	var marker = new MMap.Marker({                     
		   id:markerID,                    
		   position:new MMap.LngLat(longitudePoint,latitudePoint),   
		   icon:getCurrentContextPath()+"images/redcar.png", 
		   offset:new MMap.Pixel(-11.5,-32) 
	   });  
	mapObj.addOverlays(marker); 
	mapObj.setFitView();//设置地图合适视野级别 
	
	
    mapObj.bind(marker,"click",function(e){ 
    	inforWindow = new MMap.InfoWindow({ 
    		  
    	      content:markerContent, 
    	  
    	      offset:new MMap.Pixel(-106,-61) 
    	  
    	    }); 
        inforWindow.open(mapObj,marker.getPosition());   
  
    });  
}



function searchPOI(longtitude,latitude,keyword){
	var centXY = new MMap.LngLat(longtitude/3600000,latitude/3600000);//中心点坐标 -- 事故车辆
    var PoiSearchOption = { 
        srctype:"POI",//数据来源 
        type:"",//数据类别 
        number:10,//每页数量,默认10 
        batch:1,//请求页数，默认1 
        range:300000, //查询范围，默认3000米 
        ext:""//扩展字段 
    }; 
    var MSearch = new MMap.PoiSearch(PoiSearchOption); 
    MSearch.byKeywords(keyword,'total',searchPOICallBack); 
}

var poiData ;
function searchPOICallBack(data){ 
	poiData = data;
	if(data.status=='E0'){ 
		if(data.bounds){ 
			var a=data.bounds.split(';'); 
			if(a.length==2){ 
				var b=a[0].split(','),c=a[1].split(','); 
				mapObj.setBounds(new MMap.Bounds(new MMap.LngLat(b[0],b[1]),new MMap.LngLat(c[0],c[1]))); 
			}else{//只返回一条数据时 
				var d=a[0].split(',') ;
				mapObj.setCenter(new MMap.LngLat(d[0],d[1])) ;
			} 
		}
		cleanPOI();
		
		
		for(var i=0,l=data.list.length;i<l;i++){ 
			var poi = data.list[i];
				addPOI(i,poi);
		}
	}else{ 
		alert("未查找到任何结果!");     
	} 
} 


var POImarkers = [];
function addPOI(i,poi){
	var poiItem = $("<li class='ui-state-default'>"+ poi.name + "</li>");
	var marker = new MMap.Marker({
		id : 'marker' + i,
		icon :getCurrentContextPath()+"images/lan_1.png",
		position : new MMap.LngLat(poi.x, poi.y),
		offset : new MMap.Pixel(-10, -34)
	});
	mapObj.addOverlays(marker);
	POImarkers.push('marker'+i);
	
	var infoContent = '<b>' + poi.name + '</b><hr/>'
			+ tipContents(poi.type, poi.address, poi.tel);
	
	mapObj.bind(marker, 'click', function() {
		var infoWindow = new MMap.InfoWindow({content:infoContent,autoMove:true});
		infoWindow.open(mapObj, this.obj.getPosition());
	});

	poiItem.click(function() {
		var infoWindow = new MMap.InfoWindow({content:infoContent,autoMove:true});
		infoWindow.open(mapObj, marker.getPosition());
	});
	
	$("#resPOI>ul").append(poiItem);
}

function cleanPOI(){
	mapObj.removeOverlays(POImarkers);
	$("#resPOI>ul").empty();
}
  
function tipContents(type,address,tel){ 
	if (type == "" || type == "undefined" || type == null || type == " undefined" || typeof type == "undefined") { 
		type = "暂无"; 
	} 
	if (address == "" || address == "undefined" || address == null || address == " undefined" || typeof address == "undefined") { 
		address = "暂无"; 
	} 
	if (tel == "" || tel == "undefined" || tel == null || tel == " undefined" || typeof address == "tel") { 
		tel = "暂无"; 
	} 
	var str ="地址：" + address + "<br/>电话：" + tel + " <br/>类型："+type; 
	return str; 
} 