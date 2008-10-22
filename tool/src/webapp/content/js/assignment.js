/** BEGIN jQuery Plugin **/

/* Copyright (c) 2007 Paul Bakaus (paul.bakaus@googlemail.com) and Brandon Aaron (brandon.aaron@gmail.com || http://brandonaaron.net)
 * Dual licensed under the MIT (http://www.opensource.org/licenses/mit-license.php)
 * and GPL (http://www.opensource.org/licenses/gpl-license.php) licenses.
 *
 * $LastChangedDate: 2007-08-17 13:14:11 -0500 (Fri, 17 Aug 2007) $
 * $Rev: 2759 $
 *
 * Version: 1.1.2
 *
 * Requires: jQuery 1.1.3+
 */
(function($){var height=$.fn.height,width=$.fn.width;$.fn.extend({height:function(){if(!this[0])error();if(this[0]==window)if($.browser.opera||($.browser.safari&&parseInt($.browser.version)>520))return self.innerHeight-(($(document).height()>self.innerHeight)?getScrollbarWidth():0);else if($.browser.safari)return self.innerHeight;else
return $.boxModel&&document.documentElement.clientHeight||document.body.clientHeight;if(this[0]==document)return Math.max(($.boxModel&&document.documentElement.scrollHeight||document.body.scrollHeight),document.body.offsetHeight);return height.apply(this,arguments);},width:function(){if(!this[0])error();if(this[0]==window)if($.browser.opera||($.browser.safari&&parseInt($.browser.version)>520))return self.innerWidth-(($(document).width()>self.innerWidth)?getScrollbarWidth():0);else if($.browser.safari)return self.innerWidth;else
return $.boxModel&&document.documentElement.clientWidth||document.body.clientWidth;if(this[0]==document)if($.browser.mozilla){var scrollLeft=self.pageXOffset;self.scrollTo(99999999,self.pageYOffset);var scrollWidth=self.pageXOffset;self.scrollTo(scrollLeft,self.pageYOffset);return document.body.offsetWidth+scrollWidth;}else
return Math.max((($.boxModel&&!$.browser.safari)&&document.documentElement.scrollWidth||document.body.scrollWidth),document.body.offsetWidth);return width.apply(this,arguments);},innerHeight:function(){if(!this[0])error();return this[0]==window||this[0]==document?this.height():this.is(':visible')?this[0].offsetHeight-num(this,'borderTopWidth')-num(this,'borderBottomWidth'):this.height()+num(this,'paddingTop')+num(this,'paddingBottom');},innerWidth:function(){if(!this[0])error();return this[0]==window||this[0]==document?this.width():this.is(':visible')?this[0].offsetWidth-num(this,'borderLeftWidth')-num(this,'borderRightWidth'):this.width()+num(this,'paddingLeft')+num(this,'paddingRight');},outerHeight:function(options){if(!this[0])error();options=$.extend({margin:false},options||{});return this[0]==window||this[0]==document?this.height():this.is(':visible')?this[0].offsetHeight+(options.margin?(num(this,'marginTop')+num(this,'marginBottom')):0):this.height()+num(this,'borderTopWidth')+num(this,'borderBottomWidth')+num(this,'paddingTop')+num(this,'paddingBottom')+(options.margin?(num(this,'marginTop')+num(this,'marginBottom')):0);},outerWidth:function(options){if(!this[0])error();options=$.extend({margin:false},options||{});return this[0]==window||this[0]==document?this.width():this.is(':visible')?this[0].offsetWidth+(options.margin?(num(this,'marginLeft')+num(this,'marginRight')):0):this.width()+num(this,'borderLeftWidth')+num(this,'borderRightWidth')+num(this,'paddingLeft')+num(this,'paddingRight')+(options.margin?(num(this,'marginLeft')+num(this,'marginRight')):0);},scrollLeft:function(val){if(!this[0])error();if(val!=undefined)return this.each(function(){if(this==window||this==document)window.scrollTo(val,$(window).scrollTop());else
this.scrollLeft=val;});if(this[0]==window||this[0]==document)return self.pageXOffset||$.boxModel&&document.documentElement.scrollLeft||document.body.scrollLeft;return this[0].scrollLeft;},scrollTop:function(val){if(!this[0])error();if(val!=undefined)return this.each(function(){if(this==window||this==document)window.scrollTo($(window).scrollLeft(),val);else
this.scrollTop=val;});if(this[0]==window||this[0]==document)return self.pageYOffset||$.boxModel&&document.documentElement.scrollTop||document.body.scrollTop;return this[0].scrollTop;},position:function(returnObject){return this.offset({margin:false,scroll:false,relativeTo:this.offsetParent()},returnObject);},offset:function(options,returnObject){if(!this[0])error();var x=0,y=0,sl=0,st=0,elem=this[0],parent=this[0],op,parPos,elemPos=$.css(elem,'position'),mo=$.browser.mozilla,ie=$.browser.msie,oa=$.browser.opera,sf=$.browser.safari,sf3=$.browser.safari&&parseInt($.browser.version)>520,absparent=false,relparent=false,options=$.extend({margin:true,border:false,padding:false,scroll:true,lite:false,relativeTo:document.body},options||{});if(options.lite)return this.offsetLite(options,returnObject);if(options.relativeTo.jquery)options.relativeTo=options.relativeTo[0];if(elem.tagName=='BODY'){x=elem.offsetLeft;y=elem.offsetTop;if(mo){x+=num(elem,'marginLeft')+(num(elem,'borderLeftWidth')*2);y+=num(elem,'marginTop')+(num(elem,'borderTopWidth')*2);}else
if(oa){x+=num(elem,'marginLeft');y+=num(elem,'marginTop');}else
if((ie&&jQuery.boxModel)){x+=num(elem,'borderLeftWidth');y+=num(elem,'borderTopWidth');}else
if(sf3){x+=num(elem,'marginLeft')+num(elem,'borderLeftWidth');y+=num(elem,'marginTop')+num(elem,'borderTopWidth');}}else{do{parPos=$.css(parent,'position');x+=parent.offsetLeft;y+=parent.offsetTop;if((mo&&!parent.tagName.match(/^t[d|h]$/i))||ie||sf3){x+=num(parent,'borderLeftWidth');y+=num(parent,'borderTopWidth');if(mo&&parPos=='absolute')absparent=true;if(ie&&parPos=='relative')relparent=true;}op=parent.offsetParent||document.body;if(options.scroll||mo){do{if(options.scroll){sl+=parent.scrollLeft;st+=parent.scrollTop;}if(oa&&($.css(parent,'display')||'').match(/table-row|inline/)){sl=sl-((parent.scrollLeft==parent.offsetLeft)?parent.scrollLeft:0);st=st-((parent.scrollTop==parent.offsetTop)?parent.scrollTop:0);}if(mo&&parent!=elem&&$.css(parent,'overflow')!='visible'){x+=num(parent,'borderLeftWidth');y+=num(parent,'borderTopWidth');}parent=parent.parentNode;}while(parent!=op);}parent=op;if(parent==options.relativeTo&&!(parent.tagName=='BODY'||parent.tagName=='HTML')){if(mo&&parent!=elem&&$.css(parent,'overflow')!='visible'){x+=num(parent,'borderLeftWidth');y+=num(parent,'borderTopWidth');}if(((sf&&!sf3)||oa)&&parPos!='static'){x-=num(op,'borderLeftWidth');y-=num(op,'borderTopWidth');}break;}if(parent.tagName=='BODY'||parent.tagName=='HTML'){if(((sf&&!sf3)||(ie&&$.boxModel))&&elemPos!='absolute'&&elemPos!='fixed'){x+=num(parent,'marginLeft');y+=num(parent,'marginTop');}if(sf3||(mo&&!absparent&&elemPos!='fixed')||(ie&&elemPos=='static'&&!relparent)){x+=num(parent,'borderLeftWidth');y+=num(parent,'borderTopWidth');}break;}}while(parent);}var returnValue=handleOffsetReturn(elem,options,x,y,sl,st);if(returnObject){$.extend(returnObject,returnValue);return this;}else{return returnValue;}},offsetLite:function(options,returnObject){if(!this[0])error();var x=0,y=0,sl=0,st=0,parent=this[0],offsetParent,options=$.extend({margin:true,border:false,padding:false,scroll:true,relativeTo:document.body},options||{});if(options.relativeTo.jquery)options.relativeTo=options.relativeTo[0];do{x+=parent.offsetLeft;y+=parent.offsetTop;offsetParent=parent.offsetParent||document.body;if(options.scroll){do{sl+=parent.scrollLeft;st+=parent.scrollTop;parent=parent.parentNode;}while(parent!=offsetParent);}parent=offsetParent;}while(parent&&parent.tagName!='BODY'&&parent.tagName!='HTML'&&parent!=options.relativeTo);var returnValue=handleOffsetReturn(this[0],options,x,y,sl,st);if(returnObject){$.extend(returnObject,returnValue);return this;}else{return returnValue;}},offsetParent:function(){if(!this[0])error();var offsetParent=this[0].offsetParent;while(offsetParent&&(offsetParent.tagName!='BODY'&&$.css(offsetParent,'position')=='static'))offsetParent=offsetParent.offsetParent;return $(offsetParent);}});var error=function(){throw"Dimensions: jQuery collection is empty";};var num=function(el,prop){return parseInt($.css(el.jquery?el[0]:el,prop))||0;};var handleOffsetReturn=function(elem,options,x,y,sl,st){if(!options.margin){x-=num(elem,'marginLeft');y-=num(elem,'marginTop');}if(options.border&&(($.browser.safari&&parseInt($.browser.version)<520)||$.browser.opera)){x+=num(elem,'borderLeftWidth');y+=num(elem,'borderTopWidth');}else if(!options.border&&!(($.browser.safari&&parseInt($.browser.version)<520)||$.browser.opera)){x-=num(elem,'borderLeftWidth');y-=num(elem,'borderTopWidth');}if(options.padding){x+=num(elem,'paddingLeft');y+=num(elem,'paddingTop');}if(options.scroll&&(!$.browser.opera||elem.offsetLeft!=elem.scrollLeft&&elem.offsetTop!=elem.scrollLeft)){sl-=elem.scrollLeft;st-=elem.scrollTop;}return options.scroll?{top:y-st,left:x-sl,scrollTop:st,scrollLeft:sl}:{top:y,left:x};};var scrollbarWidth=0;var getScrollbarWidth=function(){if(!scrollbarWidth){var testEl=$('<div>').css({width:100,height:100,overflow:'auto',position:'absolute',top:-1000,left:-1000}).appendTo('body');scrollbarWidth=100-testEl.append('<div>').find('div').css({width:'100%',height:200}).width();testEl.remove();}return scrollbarWidth;};})(jQuery);


//Let jQuery play nicely with other libs
jQuery.noConflict();

/** END jQuery Plugin **/
function a2SetMainFrameHeight(){
	if (iframeId != ""){
		if(arguments[0] != null){
			height = arguments[0];
		} else {
			height = jQuery(document).height();// + 10;
		}
		jQuery("#" + iframeId, parent.document).height(height);
	}
}
newValue = "";
function useValue(value){
   newValue = value;
}
function changeValue(){   
    el = jQuery("select[name='page-replace\:\:gradebook_item-selection']").get(0);
    if(el){
        for(i=0;i<el.length;i++){
            if(el.options[i].text == newValue){
                el.selectedIndex = i;  
            }
        }
    }

    selectGraded()
}

function selectGraded() {
    el = jQuery("select[name='page-replace\:\:gradebook_item-selection']").get(0);
    if (el.selectedIndex != 0){
        jQuery("input[type='radio'][id='page-replace\:\:select_graded']").get(0).checked=true;
    } else {
        jQuery("input[type='radio'][id='page-replace\:\:select_ungraded']").get(0).checked=true;
    }
}


groups_toggle = function(){
	el = jQuery("input[type='radio'][value='false'][name='page-replace\:\:access_select-selection']").get(0);
	if (el && el.checked) {
		jQuery('li#groups_table_li').hide();
	} else {
		jQuery('li#groups_table_li').show();
	}
}

function toggle_group_checkboxes(check_all_box){
	if (check_all_box.checked){
		jQuery('table#groupTable :checkbox').attr('checked', 'checked');
	} else {
		jQuery('table#groupTable :checkbox').removeAttr('checked');
	}
}

function show_due_date(){
	el = jQuery("input:checkbox[name='page-replace\:\:require_due_date']").get(0);
	if (el) {
		if (el.checked) {
			jQuery(el).nextAll('span:first').show();
		} else {
			jQuery(el).nextAll('span:first').hide();
		}
	}
}

function show_accept_until(){
	el = jQuery("input:checkbox[name='page-replace\:\:require_accept_until']").get(0);
	if (el){
		if(el.checked){
			jQuery(el).nextAll('span:first').show();
		}else {
			jQuery(el).nextAll('span:first').hide();
		}
		
	}
}

function update_resubmit_until(){
	el = jQuery("input:checkbox[@name='page-replace\:\:resubmit_until']").get(0);
	if (el){
	if (el.checked) {
		jQuery(".resubmit_until_toggle").show();
	} else {
		jQuery(".resubmit_until_toggle").hide();
	}
	}
}

function override_submission_settings(){
    override_checkbox = jQuery("input:checkbox[@name='page-replace\:\:override_settings']").get(0);
    
    if (override_checkbox){
        if (override_checkbox.checked) {
            // change text back to normal
            jQuery("#override_settings_container").removeClass("inactive");
            
            // enable all of the form elements
            jQuery("select[@name='page-replace\:\:resubmission_additional-selection']").removeAttr("disabled");
            jQuery("input:checkbox[@name='page-replace\:\:require_accept_until']").removeAttr("disabled");
            // TODO - get the rsf date widget to work when these are disabled -it
            // is throwing syntax error upon submission
            //jQuery("input[@name='page-replace\:\:accept_until\:1\:date-field']").removeAttr("disabled");
           //jQuery("input[@name='page-replace\:\:accept_until\:1\:time-field']").removeAttr("disabled");
        } else {
            // gray out the text
            jQuery("#override_settings_container").addClass("inactive");
            
            // disable all form elements
            jQuery("select[@name='page-replace\:\:resubmission_additional-selection']").attr("disabled","disabled");
            jQuery("input:checkbox[@name='page-replace\:\:require_accept_until']").attr("disabled","disabled");
           // jQuery("input[@name='page-replace\:\:accept_until\:1\:date-field']").attr("disabled","disabled");
            //jQuery("input[@name='page-replace\:\:accept_until\:1\:time-field']").attr("disabled","disabled");
        }
    }
}

function set_accept_until_on_submission_level(){
    el = jQuery("input:checkbox[@name='page-replace\:\:require_accept_until']").get(0);
    if (el){
        if (el.checked) {
            jQuery("#accept_until_container").show();
        } else {
            jQuery("#accept_until_container").hide();
        }
    }
}

function update_new_gb_item_helper_url() {
    var gbUrlWithoutName = jQuery("a[id='page-replace\:\:gradebook_url_without_name']").attr("href");
    var new_title = jQuery("input[name='page-replace\:\:title']").get(0).value
    
    // encode unsafe characters that may be in the assignment title
   
    var escaped_title = "";
    if (new_title) {
        escaped_title = escape(new_title);
    }
    
    var modifiedUrl = gbUrlWithoutName + "&name=" + escaped_title;
    
    jQuery("a[id='page-replace\:\:gradebook_item_new_helper']").attr("href", modifiedUrl);
}

jQuery(document).ready(function(){
	update_resubmit_until();
});

function slide_submission(img){
	jQuery(img).parent('h4').next('div').toggle();
	flip_image(img)
}
function slideFieldset(img) {
	jQuery(img).parent('legend').next('ol').toggle();
	flip_image(img);
	a2SetMainFrameHeight();
}
function flip_image(img){
	if(img.src.match('collapse')){
		img.src=img.src.replace(/collapse/, 'expand');
	}else{
		img.src=img.src.replace(/expand/, 'collapse');
	}
}
function updateDisplayNoAttachments(){
	var li = jQuery("#fragment-attachments-container li span.no_attachments_yet").get(0);
	if (jQuery("#fragment-attachments-container li").size() > 1) {
		jQuery("span.no_attachments_yet").parent("li").hide();
	} else {
		jQuery("span.no_attachments_yet").parent("li").show();
	}
}

//Sorting functions
var sortBy; var sortDir; var pStart=0; var pLength=5;
function sortPageRows(b,d) {
   pLength = jQuery("select[name='page-replace\:\:pagerDiv:1:pager_select_box-selection']").val();
   sortBy=b; sortDir=d;
   var trs = jQuery.makeArray(jQuery("table#sortable tr:gt(0)"));
   trs.sort(function(a,b){
      return (jQuery("." + sortBy, a).get(0).innerHTML.toLowerCase() < jQuery("." + sortBy, b).get(0).innerHTML.toLowerCase()
      ? -1 : (jQuery("." + sortBy, a).get(0).innerHTML.toLowerCase()> jQuery("." + sortBy, b).get(0).innerHTML.toLowerCase()
         ? 1 : 0));
   });
   if (sortDir == 'desc') {trs.reverse();}
   jQuery(trs).appendTo(jQuery("table#sortable tbody"));
   jQuery("a img", "table#sortable tr:first").remove();
   imgSrc = "<img src=\"/sakai-assignment2-tool/content/images/bullet_arrow_" + (d == 'asc' ? 'up' : 'down') + ".png\" />";
   jQuery("a." + b, "table#sortable tr:first").append(imgSrc);
   //Now paging
   jQuery("table#sortable tr:gt(0)").hide();
   jQuery("table#sortable tr:gt(" + pStart + "):lt(" + pLength +")").show();
   trsLength = jQuery("table#sortable tr:gt(0)").size();
   if (pStart == 0){
     jQuery("div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_first_page'], div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_prev_page']").attr('disabled', 'disabled');
   } else {
      jQuery("div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_first_page'], div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_prev_page']").removeAttr('disabled');
   }
   if (Number(pStart) + Number(pLength) >= trsLength) {
      jQuery("div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_next_page'], div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_last_page']").attr('disabled', 'disabled');
   } else {
      jQuery("div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_next_page'], div.pagerDiv input[name='page-replace\:\:pagerDiv\:1\:pager_last_page']").removeAttr('disabled');  
   }
   //now parse the date
   // TODO FIXME SWG commenting out temporarily so the table will show up.
   //format = jQuery("div.pagerDiv div.format").get(0).innerHTML;
   //format = format.replace(/\{0\}/, Number(pStart) + 1);
   //last = Number(pStart) + Number(pLength) > trsLength ? trsLength : Number(pStart) + Number(pLength);
   //format = format.replace(/\{1\}/, last);
   //format = format.replace(/\{2\}/, jQuery("table#sortable tr:gt(0)").size());
   //jQuery("div.pagerDiv div.instruction").html(format);
}
jQuery(document).ready(function(){
	if (jQuery("table#sortable").get(0)) {
 		sortPageRows(defaultSortBy,'asc');
 		pStart=0;
	}
});
function changeSort(newBy){
	sortPageRows(newBy, (newBy!=sortBy ? 'asc' : (sortDir == 'asc' ? 'desc' : 'asc')));
}
function changePage(newPStart){
	total = jQuery("table#sortable tr:gt(0)").size();
	if ("first" == newPStart) {
		pStart = 0;
	} else if ("prev" == newPStart) {
		pStart = pStart - pLength;
		if (pStart < 0) pStart =0;
	} else if ("next" == newPStart) {
		pStart = Number(pStart) + Number(pLength);
		if (pStart > total) {
			pStart = total - (total % pLength);
		}
	} else if ("last" == newPStart) {
		if (total > pLength) {
			pStart = total - (total % pLength);
		} else {
			pStart = 0;
		}
	}
   sortPageRows(sortBy, sortDir);   
}
function updateAttachments(imgsrc, filename, link, ref, filesize){
	//Check if current last row is "demo"
   if (jQuery("#attachmentsFieldset ol:first li:last").hasClass("skip")){
   	newRow = jQuery("#attachmentsFieldset ol:first li:last").get(0);
   	jQuery(newRow).removeClass("skip");
   } else {
    newRow = jQuery("#attachmentsFieldset ol:first li:last").clone(true).appendTo("#attachmentsFieldset ol:first").get(0);
   }
   jQuery("img", newRow).attr("src",imgsrc);
   jQuery("a:first", newRow).attr("href", link);
   jQuery("a:first", newRow).html(filename);
   jQuery("input", newRow).attr("value", ref);
   jQuery("span:first", newRow).html(filesize);
}

/* New Asnn2 functions that are namespaced. Will need to go back
 * and namespace others eventually.
 */
var asnn2 = asnn2 || {};

(function (jQuery, asnn2) {
    var EXPAND_IMAGE = "/sakai-assignment2-tool/content/images/expand.png";
    var COLLAPSE_IMAGE = "/sakai-assignment2-tool/content/images/collapse.png";
    var NEW_FEEDBACK_IMAGE = "/library/image/silk/email.png";
    var READ_FEEDBACK_IMAGE = "/library/image/silk/email_open.png";
    
    asnn2.toggle_hideshow_by_id = function (arrowImgId, toggledId) {
        toggle_hideshow(jQuery('#'+arrowImgId.replace(/:/g, "\\:")),
                        jQuery('#'+toggledId.replace(/:/g, "\\:")));
    }
    
    function toggle_hideshow(arrowImg, toggled) {
        if (arrowImg.attr('src') == EXPAND_IMAGE) {
            arrowImg.attr('src', COLLAPSE_IMAGE);
            toggled.show();
        }
        else {
            arrowImg.attr('src', EXPAND_IMAGE);
            toggled.hide();
        }
    };
    
    function mark_feedback(submissionId, versionId) {
        var queries = new Array();
        queries.push(RSF.renderBinding("MarkFeedbackAsReadAction.asnnSubId",submissionId));
        queries.push(RSF.renderBinding("MarkFeedbackAsReadAction.asnnSubVersionId",versionId));
        queries.push(RSF.renderActionBinding("MarkFeedbackAsReadAction.execute"))
        var body = queries.join("&");
        jQuery.post(document.URL, body);
    };
    
    /**
     * Custom javascript for the Assignment Add/Edit page. When the "Require
     * Submissions" box is checked or unchecked, the Submission Details below it
     * need to show/hide. It does this by hiding/removing all siblings at the
     * same level (ie. the rest of the <li/> in the current list).
     * 
     * @param element The jQuery element whose downstream siblings will be 
     * shown or hidden
     * @param show Whether or not to show them.
     */
    asnn2.showHideSiblings = function(element, show) {
    	if (show == true) {
    		jQuery(element).siblings("li").show();
    	}
    	else {
    		jQuery(element).siblings("li").hide();
    	}
    },

    /**
     * Setup the element for a Assignment Submission Version. This includes
     * hooking up the (un)collapse actions, as well as the Ajax used to mark
     * feedback as read when the div is expanded.
     *
     * If the markup changes, this will need to change as well as it depends
     * on the structure.
     */
    asnn2.assnSubVersionDiv = function (elementId, feedbackRead, submissionId, versionId) {
        var escElemId = elementId.replace(/:/g, "\\:");
        var versionHeader = jQuery('#'+escElemId+ ' h2');
        var arrow = versionHeader.find("img:first");
        var toggled = jQuery('#'+escElemId+ ' div')
        var envelope = versionHeader.find("img:last");
        versionHeader.click(function() {
            toggle_hideshow(arrow, toggled);
            if (envelope.attr('src') == NEW_FEEDBACK_IMAGE) {
                envelope.attr('src', READ_FEEDBACK_IMAGE);
                mark_feedback(submissionId, versionId);
            }
        });
    };
})(jQuery, asnn2);
