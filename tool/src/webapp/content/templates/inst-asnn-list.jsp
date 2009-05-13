<%@ page import="org.sakaiproject.component.cover.ServerConfigurationService" %>
<!DOCTYPE html      PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>Assignment List</title>

<%! // See SakaiRSF class DefaultPortalMatter
String getDefaultPortalMatter() {
  String skin = ServerConfigurationService.getString("skin.default");
  String skinRepo = ServerConfigurationService.getString("skin.repo");
  String headCssToolBase = "<link href=\"" + skinRepo
    + "/tool_base.css\" type=\"text/css\" rel=\"stylesheet\" media=\"all\" />\n";
  String headCssToolSkin = "<link href=\"" + skinRepo + "/" + skin
    + "/tool.css\" type=\"text/css\" rel=\"stylesheet\" media=\"all\" />\n";
  String headCss = headCssToolBase + headCssToolSkin;
  return headCss;
}
%>
<%= getDefaultPortalMatter() %>
<link media="all" href="../css/assignmentsv4.css" type="text/css" rel="stylesheet" />
<link media="all" href="../css/inst-asnn-list.css" type="text/css" rel="stylesheet" />

<!--
    <script type="text/javascript" src="../js/infusion/lib/jquery/core/js/jquery.js"></script>
    <script type="text/javascript" src="../js/infusion/lib/jquery/ui/js/ui.core.js"></script>
    <script type="text/javascript" src="../js/infusion/lib/jquery/ui/js/ui.draggable.js"></script>
    <script type="text/javascript" src="../js/infusion/framework/core/js/jquery.keyboard-a11y.js"></script>
    <script type="text/javascript" src="../js/infusion/lib/jquery/plugins/tooltip/js/jquery.tooltip.js"></script>
    <script type="text/javascript" src="../js/infusion/framework/core/js/Fluid.js"></script>
    <script type="text/javascript" src="../js/infusion/components/inlineEdit/js/InlineEdit.js"></script>
    <script type="text/javascript" src="../js/infusion/framework/core/js/FluidDOMUtilities.js"></script>
    <script type="text/javascript" src="../js/infusion/components/reorderer/js/GeometricManager.js"></script>
    <script type="text/javascript" src="../js/infusion/components/reorderer/js/Reorderer.js"></script>
    <script type="text/javascript" src="../js/infusion/lib/json/js/json2.js"></script>
    <script type="text/javascript" src="../js/infusion/framework/core/js/DataBinding.js"></script>
    <script type="text/javascript" src="../js/infusion/lib/fastXmlPull/js/fastXmlPull.js"></script>
    <script type="text/javascript" src="../js/infusion/framework/renderer/js/fluidParser.js"></script>
    <script type="text/javascript" src="../js/infusion/framework/renderer/js/fluidRenderer.js"></script>
    <script type="text/javascript" src="../js/infusion/components/pager/js/Pager.js"></script>
-->

    <script type="text/javascript" language="JavaScript">
        var sakai = sakai || {};
        sakai.curPlacement = '<%= request.getParameter("placement") %>';
        sakai.curContext = '<%= request.getParameter("context") %>';
        var iframeId = 'Main<%= request.getParameter("placement").replace("-","x") %>';
    </script>

    <!-- Application Code -->
    <script type="text/javascript" language="JavaScript" src="/library/js/headscripts.js"></script>
    <script src="../js/InfusionAll_r7132.js" type="text/javascript"></script>
    <script src="../js/assignment.js" type="text/javascript"></script>
    <script src="../js/inst-asnn-list.js" type="text/javascript"></script>
  </head>
  <body onload="setMainFrameHeight('Main<%= request.getParameter("placement").replace("-","x") %>');">
    <div class="portletBody">

    <ul class="breadCrumb">
      <li class="lastCrumb">Assignment List</li>
    </ul>

<div class="pager-sort-area">
<div id="asnn-list-area">
    <ul id="pager-top" class="fl-pager-top flc-pager-top">
      <li>
        <ul class="pager-links flc-pager-links">
          <li class="fl-pager-pageLink flc-pager-pageLink"><a href="#">1</a></li>
          <li class="flc-pager-pageLink-disabled">2</li>
          <li class="fl-pager-pageLink flc-pager-pageLink"><a href="#">3</a></li>
        </ul>
      </li>
      <li class="fl-pager-previous flc-pager-previous"><a href="#">&lt; previous</a></li>
      <li class="fl-pager-next flc-pager-next"><a href="#">next &gt;</a></li>
      <li>Show <span>
        <select class="flc-pager-page-size">
          <option value="5">5</option>
          <option value="10">10</option>
          <option value="20">20</option>
          <option value="50">50</option>
        </select></span> per page
      </li>
    </ul>
</div>

<div style="margin: 0; padding: 5px; border-top: solid 2px #cccccc;">
    <span>Sorted by:</span>
    <ul class="sort-bar">
      <li class="sort-item"><a href="#" id="titlesort">Title</a> |</li>
      <!-- <li class="sort-item">Restricted To |</li> -->
      <li class="sort-item"><a href="#" id="opendatesort">Open Date</a> |</li>
      <li class="sort-item"><a href="#" id="duedatesort">Due Date</a> |</li>
      <li class="sort-item">
        <a href="#" id="instsort">Instructor Specified Order</a>
        <img src="/library/image/sakai/sortascending.gif" />
      </li>
    </ul>
</div>
</div>

    <table style="display: none; border-collapse: collapse" id="asnn-list">
      <thead>
        <tr>
          <th>
          </th>
          <th class="asnn-decorated-cell">
            <span>Assignments</span>
            <img src="/sakai-assignment2-tool/content/images/add.png" />
            <a href="#" onclick="window.location = '/portal/tool/'+sakai.curPlacement+'/assignment'">
            Add</a>
          </th>
          <th class="asnn-decorated-cell">
            <span>In/New</span>
          </th>
          <th class="asnn-decorated-cell">
            <input id="checkall" type="checkbox" />
          </th>
        </tr>
      </thead>
      <tbody id="asnn-list-body">
        <tr class="row">
          <td style="width: 40px"><img style="display:none" alt="Move Assignment" src="/sakai-assignment2-tool/content/images/4Arrows.png" class="movehandle" /></td>
          <td class="asnn-decorated-cell"><p>
            <span style="display:none" class="asnnid">1</span>
            <span class="asnntitle">Audio Scriptwriting</span>
            <img class="attachments" src="/sakai-assignment2-tool/content/images/attach.png" />
            <span class="draft">draft</span>
            <img class="needsAttention" src="/../../library/image/sakai/warn.gif"
            	alt="There is a problem with this assignment. For additional information, click Edit."
            	title="There is a problem with this assignment. For additional information, click Edit." />
            <br/>
            <a href="" class="editlink">Edit</a>
            <a href="" class="duplink">Duplicate</a>
            <a href="" class="gradelink">Grade</a>
            <br/>
            <span class="opendate">Open: May 6, 2008 3:00 PM</span><br/>
            <span class="duedate">Due: May 13, 2008 3:00 PM</span><br/>
            <span class="groups">Restricted To: Red Cohort, Yellow Cohort</span>
            </p>
          </td>
          <td class="asnn-decorated-cell">
            <span class="inAndNew">8/4</span>
            <a href="" class="inAndNewLink">10/4</a>
          </td>
          <td class="asnn-decorated-cell"><input class="asnncheck" type="checkbox" /></td>

        </tr>
      </tbody>
    </table>

    <input type="button" value="Remove" id="removebutton" />

    <!-- This is the template for the Confirm Asnn Remove Dialog. -->
	<div id="remove-asnn-dialog" style="display:none">
	    <div class="generalErrors">
	       <ul class="ui-dialog-msg-list">
	          <li class="alertMessageInline" >Are you sure you want to remove the following assignments and any associated submissions?</li>
	       </ul>
	    </div>
		<table class="listHier lines" > <!--  rsf:id="assignment-list-table" > -->
			<thead><tr>
				<th>Assignment Title</th>
				<th>Due</th>
				<th>Submissions</th>
			</tr></thead>
			<tbody id="asnn-to-delete"> <!--
				<td id="asnn-to-delete-title">Homework 1</td>
				<td id="asnn-to-delete-due">Mar 26, 2008 5:00 pm</td>
				<td id="asnn-to-delete-numsubmissions">1</td> -->
			</tbody>
		</table>
		<span style="display:none" id="asnn-to-delete-id"></span>
		<fieldset class="submit">
			<input id="remove-asnn-button" accesskey="r" type="button" class="remove-button active" value="Remove" />
			<input id="cancel-remove-asnn-button" class="cancel-button" accesskey="x" type="button" value="Cancel" />
		</fieldset>
	</div>

    <script type="text/javascript">
      asnn2.initAsnnList();
    </script>

    </div>
  </body>
</html>
