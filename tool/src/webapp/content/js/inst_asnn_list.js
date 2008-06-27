// Add custom parser for assignment status
$.tablesorter.addParser({ 
        // set a unique id 
        id: 'status', 
        is: function(s) { 
            // return false so this parser is not auto detected 
            return false; 
        }, 
        format: function(s) { 
            // format your data for normalization 
            return s.toLowerCase().replace(/closed/,3).replace(/late/,1).replace(/open/,0).replace(/unavailable/,2); 
        }, 
        // set type, either numeric or text 
        type: 'numeric' 
}); 
    
// Populate the test data
var testdata = {
	"context": "",
	"drafts": [
		{ "id": "3",
			"title": "Assignment 3",
			"sections": "A1",
			"openDate": "05/21/2008",
			"dueDate": {
				"abbreviated": "06/21/2008",
				"short": "Mon, Jun 21",
				"long": "Mon, Jun 21, 2008 11:04 AM"
			},
			"state": "unavail"},
		{ "id": "4",
			"title": "Assignment 4",
			"sections": "A2",
			"openDate": "05/22/2008",
			"dueDate": {
				"abbreviated": "06/22/2008",
				"short": "Tue, Jun 22",
				"long": "Tue, Jun 22, 2008 11:04 AM"
			},
			"state": "unavail"}
	],
	"posted": [
		{ "id": "1",
			"title": "Assignment 1",
			"sections": "A3",
			"openDate": "04/21/2008",
			"dueDate": {
				"abbreviated": "05/21/2008",
				"short": "Tue, May 21",
				"long": "Tue, May 21, 2008 11:04 AM"
			},
			"state": "late",
			"due": "past"
		},
		{ "id": "2",
			"title": "Assignment 2",
			"sections": "A4",
			"openDate": "04/22/2008",
			"dueDate": {
				"abbreviated": "05/17/2008",
				"short": "Fri, May 17",
				"long": "Fri, May 17, 2008 11:04 AM"
			},
			"state": "closed",
			"due": "past"
		},
		{ "id": "5",
			"title": "Assignment 5",
			"sections": "A5",
			"openDate": "04/22/2008",
			"dueDate": {
				"abbreviated": "06/22/2008",
				"short": "Wed, Jun 22",
				"long": "Wed, Jun 22, 2008 11:04 AM"
			},
			"state": "open",
			"due": "today"
		},
		{ "id": "6",
			"title": "Assignment 6",
			"sections": "A5",
			"openDate": "08/22/2008",
			"dueDate": {
				"abbreviated": "12/08/2008",
				"short": "Thu, Dec 08",
				"long": "Thu, Dec 08, 2008 11:04 AM"
			},
			"state": "unavailable",
			"due": "later"
		},
		{ "id": "7",
			"title": "Assignment 7",
			"sections": "A5",
			"openDate": "05/27/2008",
			"dueDate": {
				"abbreviated": "06/29/2008",
				"short": "Thu, Jun 29",
				"long": "Thu, Jun 29, 2008 11:04 AM"
			},
			"state": "open",
			"due": "later"
		},
		{ "id": "8",
			"title": "Assignment 8",
			"sections": "A5",
			"openDate": "08/12/2008",
			"dueDate": {
				"abbreviated": "08/30/2008",
				"short": "Sat, Aug 30",
				"long": "Sat, Aug 30, 2008 11:04 AM"
			},
			"state": "unavailable",
			"due": "later"
		}
	]
};

var InstAsnnList = {
	context: null,
	navTemplate: null,
	draftTemplate: null,
	postedTemplate: null,

	show: function(context)
	{
		// if a context is provided, get the data from the server
		if (context)
		{
			InstAsnnList.context = context;

			// update newLink to include context
			jQuery('#newLink').attr('href', 'newassignment1.html?context=' + context + '&KeepThis=true&TB_iframe=true&width=800&height=600&modal=true');

			var url = '/sakai-assignment2-tool/sdata/asnnList?context=' + context;
			jQuery.getJSON(url, function(data)
			{
				InstAsnnList.paintAssignments(data);
			});
		}
		// with no context, use test data
		else
		{
			InstAsnnList.paintAssignments(testdata);
		}
	},

	paintAssignments: function(data)
	{
		jQuery('#nav_out').html(InstAsnnList.navTemplate.process(data));
		jQuery('#draft_out').html(InstAsnnList.draftTemplate.process(data));
		jQuery('#posted_out').html(InstAsnnList.postedTemplate.process(data));

		// set the context for all context fields in forms
		jQuery('input[name="context"]').val(data['context']);

		// add the toggle events to the twisties
		ListCommon.addToggle('#postedTwisty', '#postedList', true);
		ListCommon.addToggle('#draftsTwisty', '#draftsList', true);

		// Make the tables sortable
		jQuery("#draftAssns").tablesorter({headers: {0: {sorter: false}}});
		jQuery("#postedAssns").tablesorter({
				headers: {0: {sorter: false},
				5: {sorter: 'status'}},
				textExtraction: function(node) { 
				// extract data from markup and return it
				if(node.className == "dueDate"){
					//3rd div (abbreviated date) has an index value of 5
					return node.childNodes[5].innerHTML;
				}
				else{
					return node.innerHTML;
				}},
				sortForce: [[4,0]],
				sortList: [[5,0],[4,0]]
		});

		// make sure thickbox is applied
		tb_init('a.thickbox, area.thickbox, input.thickbox');

		// set the iframe to the fit the screen
		if (window.frameElement)
		{
			var height = jQuery("#" + window.frameElement.id, parent.document).height();
			jQuery("#" + window.frameElement.id, parent.document).height(Math.max(700, height));
//			setMainFrameHeight(window.frameElement.id);
		}
	},

	copyAsnn: function(asnnId)
	{
		var url = '/sakai-assignment2-tool/sdata/asnnCopy';
		var data = {context: InstAsnnList.context, asnnId: asnnId};
		jQuery.post(url, data, function(data, textStatus)
		{
			InstAsnnList.show(InstAsnnList.context);
		});
	},

	delAsnn: function(asnnId)
	{
		if (confirm('Are you sure you want to delete this assignment?'))
		{
			var url = '/sakai-assignment2-tool/sdata/asnnDel';
			var data = {context: InstAsnnList.context, asnnId: asnnId};
			jQuery.post(url, data, function(data, textStatus)
			{
				InstAsnnList.show(InstAsnnList.context);
			});
		}
	}
}

jQuery(document).ready(function()
{
	var qs = new Querystring();
	var context = qs.get('context');

	InstAsnnList.navTemplate = TrimPath.parseDOMTemplate('nav_template');
	InstAsnnList.draftTemplate = TrimPath.parseDOMTemplate('draft_template');
	InstAsnnList.postedTemplate = TrimPath.parseDOMTemplate('posted_template');

	InstAsnnList.show(context);

      jQuery("#draftCheckAll").click(function()
      {
	      var checked_status = this.checked;
	      jQuery("input[@id=draftCheckBox]").each(function()
	      {
		      this.checked = checked_status;
	      });
      });

      jQuery("#postedCheckAll").click(function()
      {
	      var checked_status = this.checked;
	      jQuery("input[@id=postedCheckBox]").each(function()
	      {
		      this.checked = checked_status;
	      });
      });
      
      jQuery("tr[@name=asnnRow]").hover(function()
      {
	      jQuery("td div[@name=shortDate]", this).hide();
	      jQuery("td div[@name=longDate]", this).show();
      }, function()
      {
	      jQuery("td div[@name=shortDate]", this).show();
	      jQuery("td div[@name=longDate]", this).hide();
      });

});

