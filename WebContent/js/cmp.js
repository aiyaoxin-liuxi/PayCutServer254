if(document.getElementById&&!document.all){
	HTMLElement.prototype.__defineGetter__("children",function () {
			 
             var returnValue = new Object();
             var number = 0;
             for (var i=0; i<this.childNodes.length; i++) {
                 if (this.childNodes[i].nodeType == 1) {
                     returnValue[number] = this.childNodes[i];
                     number++;
                 }
             }
             returnValue.length = number;			
             return returnValue;
			 
         });

}
	
	function scrollLine(){
		  if (document.getElementById("switchPoint").className=='MainCMP_CenterLine_R2L'){
  			document.getElementById("switchPoint").className='MainCMP_CenterLine_L2R'
			document.getElementById("scrollLine").className='MainCMP_CenterLineMin'
			document.getElementById("MainCMP_Left").style.display='none';
		}
		else{
			document.getElementById("switchPoint").className='MainCMP_CenterLine_R2L'
			document.getElementById("scrollLine").className='MainCMP_CenterLine'
			document.getElementById("MainCMP_Left").style.display='';
		}
	}
function change(obj,str,ind){		

		/* 控制菜单下子节点是隐藏或显示  */
		if(document.getElementById('show'+ind).style.display=='')
			document.getElementById('show'+ind).style.display='none';
		else
			document.getElementById('show'+ind).style.display='';
		/* 控制 菜单上的 + - 号样式的变化  */
		if(obj.children[0].className.indexOf('left_menu_less') != -1)
			obj.children[0].className=obj.children[0].className.replace('less','more');	
		else
			obj.children[0].className=obj.children[0].className.replace('more','less');
}

var SindexL2=1;
function changeL2(obj,str,ind){		
	document.getElementById('numL2'+SindexL2).style.display='';	
	if(SindexL2==ind){
		if(document.getElementById('showL2'+ind).style.display=='')
			document.getElementById('showL2'+SindexL2).style.display='none';
		else
			document.getElementById('showL2'+SindexL2).style.display='';
		
		if(obj.children[0].className=='left_menu_less2')
			obj.children[0].className='left_menu_more2';	
		else
			obj.children[0].className='left_menu_less2';	
	}
	else
	{	obj.children[0].className='left_menu_less2';
		document.getElementById('numL2'+SindexL2).children[0].className='left_menu_more2';
		document.getElementById('showL2'+ind).style.display='';
		document.getElementById('showL2'+SindexL2).style.display='none';
	}
	SindexL2=ind;
	
}				

function submitOutTX(flag_str){	
	try{
		window.parent.frames['frame_main'].document.body.innerHTML="<div style='padding-top:100px;font-size:40px;text-align: center;'>请稍候......</div>";
		window.parent.frames['frame_main'].document.location.href=flag_str;	
	}catch(e){
		window.parent.location.reload();
	}
	
	
}

function submitOutTX4mian(flag_str){
	document.frames['frame_main'].location.href="BigIframe.html?"+flag_str;
}

//鼠标经过改变表中一行的颜色
function tdOver(tr){
	tr.className="table_select_bg";
}
//鼠标经过还原表中行的颜色
function tdOut(tr){
	tr.className="";
}
