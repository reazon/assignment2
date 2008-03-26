package org.sakaiproject.assignment2.tool.producers;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.logic.AssignmentLogic;
import org.sakaiproject.assignment2.logic.ExternalLogic;
import org.sakaiproject.assignment2.tool.params.PagerViewParams;
import org.sakaiproject.assignment2.tool.producers.AssignmentListSortViewProducer;
import org.sakaiproject.assignment2.tool.producers.renderers.PagerRenderer;

import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UIFreeAttributeDecorator;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;
import java.util.Map;
import java.util.HashMap;

public class AssignmentListReorderProducer implements ViewComponentProducer, ViewParamsReporter {

    public static final String VIEW_ID = "assignment_list-reorder";
    public String getViewID() {
        return VIEW_ID;
    }

    private PagerRenderer pagerRenderer;
    private MessageLocator messageLocator;
    private AssignmentLogic assignmentLogic;
    private ExternalLogic externalLogic;
    private Locale locale;


    @SuppressWarnings("unchecked")
	public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
    	PagerViewParams pagerparams = (PagerViewParams) viewparams;
    	String currentUserId = externalLogic.getCurrentUserId();
    	
    	//use a date which is related to the current users locale
    	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
    	
        List<Assignment2> entries = new ArrayList<Assignment2>();
        entries = assignmentLogic.getViewableAssignments();
        
    	//get paging data
        int total_count = entries != null ? entries.size() : 0;
    	
        UIMessage.make(tofill, "page-title", "assignment2.assignment_list-reorder.title");
        pagerRenderer.makePager(tofill, "pagerDiv:", VIEW_ID, pagerparams, total_count);
        UIMessage.make(tofill, "heading", "assignment2.assignment_list-sortview.heading");

        //Links
        UIInternalLink.make(tofill, "assignment_list-sortview-link",
					UIMessage.make("assignment2.assignment_list-sortview.title"),
				new SimpleViewParameters(AssignmentListSortViewProducer.VIEW_ID));
        UIMessage.make(tofill, "current_page", "assignment2.assignment_list-reorder.title");
        
        //Headers
        UIMessage.make(tofill, "reorder_header", "assignment2.assignment_list-reorder.reorder");
        UIMessage.make(tofill, "remove_header", "assignment2.assignment_list-reorder.remove");
        UIMessage.make(tofill, "for_header", "assignment2.assignment_list-reorder.for");
        UIMessage.make(tofill, "status_header", "assignment2.assignment_list-reorder.status");
        UIMessage.make(tofill, "open_header", "assignment2.assignment_list-reorder.open");
        UIMessage.make(tofill, "due_header", "assignment2.assignment_list-reorder.due");
        UIMessage.make(tofill, "in_new_header", "assignment2.assignment_list-reorder.in_new");
        //UIMessage.make(tofill, "scale_header", "assignment2.assignment_list-reorder.scale");
        
        
        if (entries.size() <= 0) {
            UIMessage.make(tofill, "assignment_empty", "assignment2.assignment_list-reorder.assignment_empty");
            return;
        }
        UIOutput td = UIOutput.make(tofill, "td_assignment_li_container");
        Map td_rowspan = new HashMap();
        td_rowspan.put("rowspan", Integer.toString(entries.size() + 1));
        td_rowspan.put("id", "sortable");
        td.decorators = new DecoratorList(new UIFreeAttributeDecorator(td_rowspan));
        
        //Fill out Table
        int i=0;
        for (Assignment2 assignment : entries){
        	UIBranchContainer row = UIBranchContainer.make(tofill, "assignment-row:");
        	/***
        	if (i == 0){
        		UIOutput.make(row, "reorder_cell");
        	}
        	**/
        	
        	//Sorting LI
        	UIBranchContainer li = UIBranchContainer.make(tofill, "assignment_li:");
        	Map attrmap = new HashMap();
        	attrmap.put("id", "li_" + assignment.getId().toString());
        	li.decorators = new DecoratorList(new UIFreeAttributeDecorator(attrmap));
        	UIOutput.make(li, "assignment_row_title", assignment.getTitle());
        	
        	// 	Table Row
        	UIOutput.make(row, "assignment_row_for", "Site");
        	if (assignment.isDraft()){
        		UIOutput.make(row, "assignment_row_draft_td");
        		UIMessage.make(row, "assignment_row_draft", "assignment2.assignment_list-reorder.assignment_row_draft");
        	} else {
        	   	UIMessage.make(row, "assignment_row_open_text", "assignment2.assignment_list-reorder.assignment_row_open");
        	}
        	UIOutput.make(row, "assignment_row_open", df.format(assignment.getOpenTime()));
        	UIOutput.make(row, "assignment_row_due", (assignment.getDueDateForUngraded() != null ? df.format(assignment.getDueDateForUngraded()) : ""));
        	UIInternalLink.make(row, "assignment_row_in_new", "2/2", new SimpleViewParameters(AssignmentListReorderProducer.VIEW_ID));
        	//UIOutput.make(row, "assignment_row_scale", "0-100.0");
        	
        	i++;
        }
        
    }
    public ViewParameters getViewParameters(){
    	return new PagerViewParams();
    }

    public void setMessageLocator(MessageLocator messageLocator) {
        this.messageLocator = messageLocator;
    }
    
    public void setPagerRenderer(PagerRenderer pagerRenderer){
    	this.pagerRenderer = pagerRenderer;
    }
    
    public void setAssignmentLogic(AssignmentLogic assignmentLogic) {
    	this.assignmentLogic = assignmentLogic;
    }
    
    public void setExternalLogic(ExternalLogic externalLogic) {
    	this.externalLogic = externalLogic;
    }
    
    public void setLocale(Locale locale) {
    	this.locale = locale;
    }
}