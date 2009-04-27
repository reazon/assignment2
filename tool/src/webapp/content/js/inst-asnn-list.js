var asnn2 = asnn2 || {};

asnn2.livedata = true;

/*
 * Returns an list of Assignment Objects that can be viewed.
 */
asnn2.getAsnnCompData = function () {
  var designSampleData = [
    {
      id: "1",
      title: "Audio Scriptwriting",
      editlink: {
        target: "/whatever/edit/1",
        linktext: "Edit"
      },
      duplink: {
        target: "/whatever/duplicate/1",
        linktext: "Duplicate"
      },
      gradelink: {
        target: "/whatever/grade/1",
        linktext: "Grade"
      },
      opentext: "Open: May 6, 2008 3:00 PM",
      duetext: "Due: May 13, 2008 3:00 PM",
      grouptext: "Restricted To: Red Cohort, Yellow Cohort",
      inAndNew: "8/4"
    },
    {
      id: "2",
      title: "Read Chapter 16 of Friedman",
      editlink: {
        target: "/whatever/edit/2",
        linktext: "Edit"
      },
      duplink: {
        target: "/whatever/duplicate/2",
        linktext: "Duplicate"
      },
      opentext: "Open: Apr 29, 2008 3:00 PM",
      duetext: "Due: May 6, 2008 3:00 PM",
      inAndNew: "N/A"
    },
    {
      id: "3",
      title: "Grant Writing",
      editlink: {
        target: "/whatever/edit/3",
        linktext: "Edit"
      },
      duplink: {
        target: "/whatever/duplicate/3",
        linktext: "Duplicate"
      },
      gradelink: {
        target: "/whatever/grade/3",
        linktext: "Grade"
      },
      opentext: "Open: Apr 22, 2008 3:00 PM",
      duetext: "Due: Apr 29, 2008 3:00 PM",
      inAndNew: "8/0"
    },
    {
      id: "4",
      title: "Interactive Storytelling",
      editlink: {
        target: "/whatever/edit/4",
        linktext: "Edit"
      },
      duplink: {
        target: "/whatever/duplicate/4",
        linktext: "Duplicate"
      },
      gradelink: {
        target: "/whatever/grade/4",
        linktext: "Grade"
      },
      opentext: "Open: Apr 15, 2008 3:00 PM",
      duetext: "Due: Apr 22, 2008 3:00 PM",
      inAndNew: "8/2"
    },
    {
      id: "5",
      title: "Professional Writing for Visual Media",
      editlink: {
        target: "/whatever/edit/5",
        linktext: "Edit"
      },
      duplink: {
        target: "/whatever/duplicate/5",
        linktext: "Duplicate"
      },
      gradelink: {
        target: "/whatever/grade/5",
        linktext: "Grade"
      },
      opentext: "Open: Apr 8, 2008 3:00 PM",
      duetext: "Due: Apr 15, 2008 3:00 PM",
      inAndNew: "8/0"
    }
  ];

  var dataFromEntity = function (obj, index) {
    return obj.data; 
  };

  var renderFromData = function (obj, index) {
    var ditto = ['id','title', 'inAndNew', 'sortIndex', 'openDate', 'dueDate'];
    var togo = {};
    for (var i in ditto) {
      togo[ditto[i]] = obj[ditto[i]];
    } 
    if (obj.openDate) {
      togo.opentext = "Open: " + new Date(obj.openDate).toLocaleString();
    }
    if (obj.dueDate) {
      togo.duetext = "Due: " + new Date(obj.dueDate).toLocaleString();
    }
    togo.editlink = { 
      target: '/portal/tool/'+sakai.curPlacement+'/assignment/'+obj.id,
      linktext: "Edit" 
    };
    togo.duplink = {
      target: '/portal/tool/'+sakai.curPlacement+'/assignment?duplicatedAssignmentId='+obj.id,
      linktext: "Duplicate"
    }; 
    if (obj.graded === true) {
        togo.gradelink = {
            target: '/portal/tool/'+sakai.curPlacement+'/viewSubmissions/'+obj.id,
            linktext: "Grade"
        }
    }
    return togo;
  };

  var togo = []
  if (asnn2.livedata === true) {
    jQuery.ajax({
      type: "GET",	
      url: "/direct/assignment2/sitelist.json",
      data: "siteid="+sakai.curContext,
      async: false, 
      success: function (payload) {
        var data = JSON.parse(payload);
        togo = fluid.transform(data.assignment2_collection, dataFromEntity, renderFromData);
      }
    });
  }
  else {
    togo = designSampleData;
  }

  return togo;
}

asnn2.selectorMap = [
  { selector: ".row", id: "row:" },
  { selector: ".asnnid", id: "id" },
  { selector: ".asnntitle", id: "title" },
  { selector: ".gradelink", id: "gradelink"},
  { selector: ".editlink", id: "editlink" },
  { selector: ".duplink", id: "duplink" },
  { selector: ".opendate", id: "opentext" },
  { selector: ".duedate", id: "duetext" },
  { selector: ".groups", id: "grouptext" },
  { selector: ".inAndNew", id: "inAndNew" },
];

asnn2.setupReordering = function () {
  fluid.reorderList("#asnn-list", {
    selectors : {
      movables: ".row",
      grabHandle: ".movehandle"
    }
  });
};

asnn2.renderAsnnList = function(treedata) {
  if (asnn2.asnnListTemplate) {
    fluid.reRender(asnn2.asnnListTemplate, jQuery("#asnn-list"), treedata, {cutpoints: asnn2.selectorMap})
  }
  else {
    asnn2.asnnListTemplate = fluid.selfRender(jQuery("#asnn-list"), treedata, {cutpoints: asnn2.selectorMap});
  }
};

asnn2.setupRemoveCheckboxes = function () {
  $("#checkall").bind("change", function(e) {
    $(".asnncheck").each(function (i) {
      this.checked = e.currentTarget.checked;
    });
  });
};

/**
 * Refresh all the actions and listeners on the asnn list table that need to be
 * setup each time it is rendered.
 */
asnn2.refreshAsnnListEvents = function () {
  asnn2.setupRemoveCheckboxes(); 
  asnn2.setupReordering();

};

asnn2.initAsnnList = function () {
  var treedata = {
    "row:": asnn2.getAsnnCompData()
  };

  asnn2.renderAsnnList(treedata);

  /*
   *  Set up sorting events
   */
  var sortMap = [
      { selector: "#titlesort", property: "title" },
      { selector: "#opendatesort", property: "openDate" },
      { selector: "#duedatesort", property: "dueDate" },
      { selector: "#instsort", property: "sortIndex" }
  ];
  
  var sortDir = 1;
  for (var i in sortMap) {
    var item = sortMap[i];
    $(item.selector).bind("click", function(sortby) {
      return function (e) {
        var newdata = asnn2.getAsnnCompData();
        newdata.sort(function (arec,brec) {
          var a = arec[sortby];
          var b = brec[sortby];
          return a === b? 0 : ( a > b? sortDir : -sortDir); 
        });

        sortDir = sortDir * -1;

        asnn2.renderAsnnList({ "row:": newdata });
        asnn2.refreshAsnnListEvents();
      };
    }(item.property));
  }

  /*
   *  Set up inline edits
   */
  var titleEdits = fluid.inlineEdits("#asnn-list", {
    selectors : {
      text: ".asnntitle",
      editables: "p"
    },
    listeners: {
      onFinishEdit: function (newValue, oldValue, editNode, viewNode) {
        var asnnid = $(".asnnid", viewNode.parentNode).text();
        jQuery.ajax({
          type: "POST",
          url: "/direct/assignment2/"+asnnid+"/edit",
          data: {
            id: asnnid,
            title: newValue
          }
        });
      }
    }
  });

  /*
   * Set up reordering
   */
  asnn2.setupReordering();

  /*
   * Set up check all/none control and Remove Button
   */
  asnn2.setupRemoveCheckboxes();

  $("#removebutton").bind("click", function(e) {
    var toremove = [];

    $(".asnncheck").each(function (i) {
      if (this.checked) {
        var asnnid = $(".asnnid", this.parentNode.parentNode).text();
        toremove.push(asnnid);
        // TODO: Bulk these delete commands together
        jQuery.ajax({
          type: "DELETE",
          url: "/direct/assignment2/"+asnnid+"/delete"
        });
        
      }
    });
  });


  /*
   * Set up the pagers
   */
  var pager = fluid.pager(".portletBody", {
    listeners: {
      onModelChange: function (newModel, oldModel) {
        var something = "cool";
        //alert(newModel.toString());
      }
    },
    dataModel: [1,2,3,4,5],

    pageBar: {
      type: "fluid.pager.pagerBar",
      dataModel: {length: 5},
      options: {
        pageList: {
          type: "fluid.pager.renderedPageList",
          options: {
            linkBody: "a"
          }
        }
      }
    }
  });

/*
  var newModel = fluid.copy(pager.model);
  newModel.totalRange = treedata.length;
  newModel.pageCount = Math.max(1, Math.floor((newModel.totalRange - 1)/ newModel.pageSize) + 1);
  //newModel.sortKey = that.state.sortKey;
  //newModel.sortDir = that.state.sortDir;
  that.pager.events.onModelChange.fire(newModel, pager.model, pager);
  fluid.model.copyModel(pager.model, newModel);
*/
}
