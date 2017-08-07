/*
* jQuery pager plugin
* Version 1.0 (12/22/2008)
* @requires jQuery v1.2.6 or later
*
* Example at: http://jonpauldavies.github.com/JQuery/Pager/PagerDemo.html
*
* Copyright (c) 2008-2009 Jon Paul Davies
* Dual licensed under the MIT and GPL licenses:
* http://www.opensource.org/licenses/mit-license.php
* http://www.gnu.org/licenses/gpl.html
* 
* Read the related blog post and contact the author at http://www.j-dee.com/2008/12/22/jquery-pager-plugin/
*
* This version is far from perfect and doesn't manage it's own state, therefore contributions are more than welcome!
*
* Usage: .pager({ pagenumber: 1, pagecount: 15, buttonClickCallback: PagerClickTest });
*
* Where pagenumber is the visible page number
*       pagecount is the total number of pages to display
*       buttonClickCallback is the method to fire when a pager button is clicked.
*
* buttonClickCallback signiture is PagerClickTest = function(pageclickednumber) 
* Where pageclickednumber is the number of the page clicked in the control.
*
* The included Pager.CSS file is a dependancy but can obviously tweaked to your wishes
* Tested in IE6 IE7 Firefox & Safari. Any browser strangeness, please report.
*/
(function($) {

    $.fn.pager = function(options) {

        return this.each(function() {
        // empty out the destination element and then render out the pager with the supplied options
            $(this).empty().append(renderpager(parseInt(options.pageSize),parseInt(options.totalCount), parseInt(options.pagenumber), parseInt(options.pagecount), options.buttonClickCallback));
            
        });
    };
    // 2015/05/28 lizhenhe add pageSize param
    // render and return the pager with the supplied options
    function renderpager(pageSize,totalCount, pagenumber, pagecount, buttonClickCallback) {

        // setup $pager to hold render
        var $pager = $('<ul class="pages"></ul>');   
        
        //  2015/05/28 22:32 lizhenhe add jump
        $pager.append(renderPageNo(pagenumber,pagecount,buttonClickCallback));
        
        // add in the previous and next buttons
        var labeltext = $('<li class="page-text">共'+pagecount+'页/'+totalCount+'条记录</li>');
        labeltext.appendTo($pager);

        // add in the previous and next buttons
        $pager.append(renderButton('首页', pagenumber, pagecount, buttonClickCallback)).append(renderButton('上一页', pagenumber, pagecount, buttonClickCallback));

        // pager currently only handles 10 viewable pages ( could be easily parameterized, maybe in next version ) so handle edge cases
        var startPoint = 1;
        var endPoint = 5;

        if (pagenumber > 2) {
            startPoint = pagenumber - 2;
            endPoint = pagenumber + 2;
        }

        if (endPoint > pagecount) {
            startPoint = pagecount - 4;
            endPoint = pagecount;
        }

        if (startPoint < 1) {
            startPoint = 1;
        }

        // loop thru visible pages and render buttons
        for (var page = startPoint; page <= endPoint; page++) {

            var currentButton = $('<li class="page-number">' + (page) + '</li>');

            page == pagenumber ? currentButton.addClass('pgCurrent') : currentButton.click(function() { buttonClickCallback(this.firstChild.data); });
            currentButton.appendTo($pager);
        }

        // render in the next and last buttons before returning the whole rendered control back.
        $pager.append(renderButton('下一页', pagenumber, pagecount, buttonClickCallback)).append(renderButton('末页', pagenumber, pagecount, buttonClickCallback));
        //  2015/05/28 lizhenhe add render pageSize
        $pager.append(renderPageSize(pageSize));
        
        return $pager;
    }

    // renders and returns a 'specialized' button, ie 'next', 'previous' etc. rather than a page number button
    function renderButton(buttonLabel, pagenumber, pagecount, buttonClickCallback) {

        var $Button = $('<li class="pgNext">' + buttonLabel + '</li>');

        var destPage = 1;

        // work out destination page for required button type
        switch (buttonLabel) {
            case "首页":
                destPage = 1;
                break;
            case "上一页":
                destPage = pagenumber - 1;
                break;
            case "下一页":
                destPage = pagenumber + 1;
                break;
            case "末页":
                destPage = pagecount;
                break;
        }

        
        // disable and 'grey' out buttons if not needed.
        if (buttonLabel == "首页" || buttonLabel == "上一页") {
            pagenumber <= 1 ? $Button.addClass('pgEmpty') : $Button.click(function() { buttonClickCallback(destPage); });
        }
        else {
            pagenumber >= pagecount ? $Button.addClass('pgEmpty') : $Button.click(function() { buttonClickCallback(destPage); });
        }

        return $Button;
    }
    //  2015/05/28 lizhenhe add render pageSize
    function renderPageSize(pageSize){
    	
    	var $pageSizeSelect = $('<select id="pageSizeSelect">'
    		+'<option value="10">10</option>'
    		+'<option value="20">20</option>'
    		+'<option value="30">30</option>'
    		+'<option value="50">50</option>'
    		+'</select>s');
    	$pageSizeSelect.find('option[value="'+pageSize+'"]').attr("selected",true);
    	
    	$pageSizeSelect.change(function(){
    		//快速跳转页数将重置为第1页，防止选择较大页数，同时又选择较大pageSize时出现找不到记录的问题
    		$("#pageNumber").val(1);
    		$("#pageSize").val($(this).val());
    		$(".listForm").submit();
    	});
    	
    	return $pageSizeSelect;
    }
    
    //  2015/05/28 22:32 lizhenhe add jump
    function renderPageNo(pagenumber,pagecount,buttonClickCallback){
    	
    	var $jumpInput = $("<li style='border:none;padding-top:0px'>转到<input type='text' id='pageNoInput' size='5' value='"+pagenumber+"' />页 </li>");
    	var $pageNoJumpBtn = $("<input id='pageNoJumpBtn' type='button' value='确定' />");
    	$jumpInput.append($pageNoJumpBtn);
    	$pageNoJumpBtn.click(function(){
    		if(new Number($("#pageNoInput").val()) > new Number($("#pageTotal").val()) ){
    			alert("输入的页数已经超过最大总页数，页数将自动更正为最大页数"+$("#pageTotal").val());
    			$("#pageNoInput").val($("#pageTotal").val());
    			
    		}
    		buttonClickCallback($("#pageNoInput").val());
    	});
    	return $jumpInput;
    }

    // pager defaults. hardly worth bothering with in this case but used as placeholder for expansion in the next version
    $.fn.pager.defaults = {
        pagenumber: 1,
        pagecount: 1
    };

})(jQuery);





