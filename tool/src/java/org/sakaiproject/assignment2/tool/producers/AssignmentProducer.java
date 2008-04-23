/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2007, 2008 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.assignment2.tool.producers;

import org.sakaiproject.assignment2.tool.params.AssignmentViewParams;
import org.sakaiproject.assignment2.tool.params.FilePickerHelperViewParams;
import org.sakaiproject.assignment2.tool.producers.renderers.AttachmentListRenderer;
import org.sakaiproject.assignment2.tool.producers.fragments.FragmentAttachmentsProducer;
import org.sakaiproject.assignment2.tool.producers.fragments.FragmentAssignment2SelectProducer;
import org.sakaiproject.assignment2.tool.producers.fragments.FragmentAssignmentPreviewProducer;
import org.sakaiproject.assignment2.logic.ExternalLogic;
import org.sakaiproject.assignment2.logic.ExternalGradebookLogic;
import org.sakaiproject.assignment2.logic.GradebookItem;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.constants.AssignmentConstants;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.lang.String;

import uk.org.ponder.beanutil.entity.EntityBeanLocator;
import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UIBoundBoolean;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UISelect;
import uk.org.ponder.rsf.components.UISelectChoice;
import uk.org.ponder.rsf.components.UISelectLabel;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UIFreeAttributeDecorator;
import uk.org.ponder.rsf.components.decorators.UILabelTargetDecorator;
import uk.org.ponder.rsf.evolvers.TextInputEvolver;
import uk.org.ponder.rsf.evolvers.FormatAwareDateInputEvolver;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCase;
import uk.org.ponder.rsf.flow.jsfnav.NavigationCaseReporter;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

import org.sakaiproject.site.api.Group;

public class AssignmentProducer implements ViewComponentProducer, NavigationCaseReporter, ViewParamsReporter {

    public static final String VIEW_ID = "assignment";
    public String getViewID() {
        return VIEW_ID;
    }

    String reqStar = "<span class=\"reqStar\">*</span>";

    private TextInputEvolver richTextEvolver;
    private MessageLocator messageLocator;
    private ExternalLogic externalLogic;
    private ExternalGradebookLogic externalGradebookLogic;
    private Locale locale;
    private EntityBeanLocator assignment2BeanLocator;
    private AttachmentListRenderer attachmentListRenderer;
    
	/*
	 * You can change the date input to accept time as well by uncommenting the lines like this:
	 * dateevolver.setStyle(FormatAwareDateInputEvolver.DATE_TIME_INPUT);
	 * and commenting out lines like this:
	 * dateevolver.setStyle(FormatAwareDateInputEvolver.DATE_INPUT);
	 * -AZ
	 * And vice versa - RWE
	 */
	private FormatAwareDateInputEvolver dateEvolver;
	public void setDateEvolver(FormatAwareDateInputEvolver dateEvolver) {
		this.dateEvolver = dateEvolver;
	}

    @SuppressWarnings("unchecked")
	public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {

    	//Get View Params
    	AssignmentViewParams params = (AssignmentViewParams) viewparams;
    	
    	String currentContextId = externalLogic.getCurrentContextId();
 	

    	
    	//get Passed assignmentId to pull in for editing if any
    	Long assignmentId = params.assignmentId;
    	
    	// use a date which is related to the current users locale
    	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
        
        //Breadcrumbs
        UIInternalLink.make(tofill, "breadcrumb", 
        		messageLocator.getMessage("assignment2.assignment_list-sortview.heading"),
        		new SimpleViewParameters(AssignmentListSortViewProducer.VIEW_ID));
        if (params.assignmentId != null) {
        	UIMessage.make(tofill, "last_breadcrumb", "assignment2.assignment_add.edit_heading");
        } else {
        	UIMessage.make(tofill, "last_breadcrumb", "assignment2.assignment_add.add_heading");
        }
        
        //Heading messages
        UIMessage.make(tofill, "page-title", "assignment2.assignment_add.title");
        //navBarRenderer.makeNavBar(tofill, "navIntraTool:", VIEW_ID);
        //UIMessage.make(tofill, "heading", "assignment2.assignment_add.heading");
        UIVerbatim.make(tofill, "instructions", messageLocator.getMessage("assignment2.assignment_add.instructions", 
        		new Object[]{ reqStar }));
        
        String assignment2OTP = "Assignment2.";
        String OTPKey = "";
        if (assignmentId != null) {
        	OTPKey = assignmentId.toString();
        } else {
        	//create new
        	OTPKey = EntityBeanLocator.NEW_PREFIX + "1";
        }
        assignment2OTP += OTPKey;
        Assignment2 assignment = (Assignment2)assignment2BeanLocator.locateBean(OTPKey);
        
    	//Initialize js otpkey
    	UIVerbatim.make(tofill, "attachment-ajax-init", "otpkey=\"" + org.sakaiproject.util.Web.escapeUrl(OTPKey) + "\";\n" +
    			"fragAttachPath=\"" + externalLogic.getAssignmentViewUrl(FragmentAttachmentsProducer.VIEW_ID) + "\";\n" +
    					"fragGBPath=\"" + externalLogic.getAssignmentViewUrl(FragmentAssignment2SelectProducer.VIEW_ID) + "\";");
    	
        UIForm form = UIForm.make(tofill, "assignment_form");
        
    	//Setting up Dates
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.HOUR_OF_DAY, 12);
    	cal.set(Calendar.MINUTE, 0);
    	Date openDate = cal.getTime();
    	cal.add(Calendar.DAY_OF_YEAR, 7);
    	cal.set(Calendar.HOUR_OF_DAY, 17);
    	Date closeDate = cal.getTime();
        
        //set dateEvolver
        dateEvolver.setStyle(FormatAwareDateInputEvolver.DATE_TIME_INPUT);
        
        UIVerbatim title_label = UIVerbatim.make(form, "title_label", messageLocator.getMessage("assignment2.assignment_add.assignment_title",
        		new Object[]{ reqStar }));
        UIInput title = UIInput.make(form, "title", assignment2OTP + ".title");
        UILabelTargetDecorator.targetLabel(title_label, title);
        
        UIVerbatim.make(form, "open_date_label", messageLocator.getMessage("assignment2.assignment_add.open_date",
        		new Object[]{ reqStar }));
        UIInput openDateField = UIInput.make(form, "open_date:", assignment2OTP + ".openTime");
		dateEvolver.evolveDateInput(openDateField, null);
		UIMessage.make(form, "open_date_instruction", "assignment2.assignment_add.open_date_instruction");
        
		//Display None Decorator list
		Map attrmap = new HashMap();
		attrmap.put("style", "display:none");
		DecoratorList display_none_list =  new DecoratorList(new UIFreeAttributeDecorator(attrmap));
		
		
		Boolean require_due_date = (assignment.getDueDate() != null);
		UIBoundBoolean require_due = UIBoundBoolean.make(form, "require_due_date", "#{Assignment2Bean.requireDueDate}", require_due_date);
		require_due.mustapply = true;
		UIMessage require_due_label = UIMessage.make(form, "require_due_date_label", "assignment2.assignment_add.require_due_date");
		UILabelTargetDecorator.targetLabel(require_due_label, require_due);
		
		UIOutput require_due_container = UIOutput.make(form, "require_due_date_container");
		UIInput dueDateField = UIInput.make(form, "due_date:", assignment2OTP + ".dueDate");
		dateEvolver.evolveDateInput(dueDateField, (assignment.getDueDate() != null ? assignment.getDueDate() : closeDate));
		
		if (!require_due_date){
			require_due_container.decorators = display_none_list;
		}
		
		
		Boolean require_date = (assignment.getAcceptUntilTime() != null);
		UIBoundBoolean require = UIBoundBoolean.make(form, "require_accept_until", "#{Assignment2Bean.requireAcceptUntil}", require_date);
		require.mustapply = true;
		UIMessage require_label = UIMessage.make(form, "require_accept_until_label", "assignment2.assignment_add.require_accept_until");
		UILabelTargetDecorator.targetLabel(require_label, require);
		
		UIOutput require_container = UIOutput.make(form, "accept_until_container");
		UIInput acceptUntilTimeField = UIInput.make(form, "accept_until:", assignment2OTP + ".acceptUntilTime");
        dateEvolver.evolveDateInput(acceptUntilTimeField, (assignment.getAcceptUntilTime() != null ? assignment.getAcceptUntilTime() : closeDate));
        
        /*** Out
        //Resubmit until until date
        UIOutput accept_until_until_fieldset = UIOutput.make(form, "accept_until_until_fieldset");
        UIMessage accept_label = UIMessage.make(form, "accept_until_until_label", "assignment2.assignment_add.accept_until_until");
        UIBoundBoolean accept = UIBoundBoolean.make(form, "accept_until_until", assignment2OTP + ".allowResubmit");
        UILabelTargetDecorator.targetLabel(accept_label, accept);
		**/
        
        if (!require_date){
        	require_container.decorators = display_none_list;
        	//accept_until_until_fieldset.decorators = display_none_list;
        }
        
        //Assignment Count for How many Submissions
        Integer current_num_submissions = 1;
        if (assignment != null && assignment.getNumSubmissionsAllowed() != null) {
        	current_num_submissions = assignment.getNumSubmissionsAllowed();
        }
        int size = 20;
        String[] number_submissions_options = new String[size+1];
        String[] number_submissions_values = new String[size+1];
        number_submissions_values[0] = "-1";
        number_submissions_options[0] = messageLocator.getMessage("assignment2.indefinite_resubmit");
        for (int i=0; i < size; i++){
        	number_submissions_values[i + 1] = Integer.valueOf(i+1).toString();
        	number_submissions_options[i + 1] = Integer.valueOf(i+1).toString();
        }
        UISelect.make(form, "number_submissions", number_submissions_values, number_submissions_options, 
        		assignment2OTP + ".numSubmissionsAllowed", current_num_submissions.toString());
        
        
        //Submission Types
        String[] submission_type_values = new String[] {
        		String.valueOf(AssignmentConstants.SUBMIT_INLINE_ONLY),
        		String.valueOf(AssignmentConstants.SUBMIT_ATTACH_ONLY),
        		String.valueOf(AssignmentConstants.SUBMIT_INLINE_AND_ATTACH),
        		String.valueOf(AssignmentConstants.SUBMIT_NON_ELECTRONIC)
        };
        String[] submisison_type_labels = new String[] {
        		"assignment2.submission_type." + String.valueOf(AssignmentConstants.SUBMIT_INLINE_ONLY),
        		"assignment2.submission_type." + String.valueOf(AssignmentConstants.SUBMIT_ATTACH_ONLY),
        		"assignment2.submission_type." + String.valueOf(AssignmentConstants.SUBMIT_INLINE_AND_ATTACH),
        		"assignment2.submission_type." + String.valueOf(AssignmentConstants.SUBMIT_NON_ELECTRONIC)
        };
        UISelect.make(form, "submission_type", submission_type_values,
        		submisison_type_labels, assignment2OTP + ".submissionType").setMessageKeys();
        
        //Rich Text Input
        UIInput instructions = UIInput.make(form, "instructions:", assignment2OTP + ".instructions");
        instructions.mustapply = Boolean.TRUE;
        richTextEvolver.evolveTextInput(instructions);
        
        
        //Calendar Due Date
        //Announcement -  only display if site has the announcements tool
        if (externalLogic.siteHasTool(currentContextId, ExternalLogic.TOOL_ID_ANNC)) {
        	UIMessage announcement_label = UIMessage.make(form, "announcement_label", "assignment2.assignment_add.announcement");
        	UIBoundBoolean announcement = UIBoundBoolean.make(form, "announcement", assignment2OTP + ".hasAnnouncement");
        	UILabelTargetDecorator.targetLabel(announcement_label, announcement);
        }
        
        //Honor Pledge
        String[] honor_pledge_labels = new String[]{
        		"assignment2.no",
        		"assignment2.yes"
        };
        String[] honor_pledge_values = new String[] {
        		Boolean.FALSE.toString(),
        		Boolean.TRUE.toString()
        };
        UISelect.make(form, "honor_pledge", honor_pledge_values, honor_pledge_labels, assignment2OTP + ".honorPledge").setMessageKeys();
        
        //Attachments
        attachmentListRenderer.makeAttachmentFromAssignment2OTPAttachmentSet(form, "attachment_list:", 
        		params.viewID, OTPKey, Boolean.TRUE);
        UIInternalLink.make(form, "add_attachments", UIMessage.make("assignment2.assignment_add.add_attachments"),
        		new FilePickerHelperViewParams(AddAttachmentHelperProducer.VIEWID, Boolean.TRUE, 
        				Boolean.TRUE, 500, 700, OTPKey));
        
        /********
         *Grading
         */  
        //Get Gradebook Items
        List<GradebookItem> gradebook_items = externalGradebookLogic.getAllGradebookItems(currentContextId);
        //Get an Assignment for currently selected from the select box
        // by default this the first item on the list returned from the externalGradebookLogic
        // this will be overwritten if we have a pre-existing assignment with an assigned
        // item
        GradebookItem currentSelected = null;
        for (GradebookItem gi : gradebook_items){
        	if (gi.getGradableObjectId().equals(assignment.getGradableObjectId())){
        		currentSelected = gi;
        	}
        }
        
        String[] gradebook_item_labels = new String[gradebook_items.size()+1];
        String[] gradebook_item_values = new String[gradebook_items.size()+1];
        gradebook_item_values[0] = "0";
        gradebook_item_labels[0] = messageLocator.getMessage("assignment2.assignment_add.gradebook_item_select");
        String js_gradebook_items_data = "var gradebook_items_date = {\n";
        js_gradebook_items_data += "0: \"" + messageLocator.getMessage("assignment2.assignment_add.gradebook_item_not_selected") + "\"\n";
        for (int i=1; i <= gradebook_items.size(); i++) {
        	//Fill out select options
        	gradebook_item_labels[i] = gradebook_items.get(i-1).getTitle();
        	gradebook_item_values[i] = gradebook_items.get(i-1).getGradableObjectId().toString();
        	
        	//store js hash of id => due_date string
        	js_gradebook_items_data += "," + gradebook_items.get(i-1).getGradableObjectId().toString();
        	if(gradebook_items.get(i-1).getDueDate() != null){
        		js_gradebook_items_data += ":\"" + df.format(gradebook_items.get(i-1).getDueDate()) + "\"\n";
        	}else{
        		js_gradebook_items_data += ":\"" + messageLocator.getMessage("assignment2.assignment_add.gradebook_item_no_due_date") + "\"\n";
        	}
        }
        js_gradebook_items_data += "}";
        UISelect.make(form, "gradebook_item",gradebook_item_values, gradebook_item_labels, assignment2OTP + ".gradableObjectId"); 
        
        //Radio Buttons for Grading
        UISelect grading_select = UISelect.make(form, "ungraded", 
        		new String[]{Boolean.FALSE.toString(), Boolean.TRUE.toString()}, new String[]{"", ""}, assignment2OTP + ".ungraded");
        String grading_select_id = grading_select.getFullID();
        UISelectChoice graded = UISelectChoice.make(form, "select_graded", grading_select_id, 0);
        UIMessage graded_label = UIMessage.make(form, "select_graded_label", "assignment2.assignment_add.assignment_graded");
        UILabelTargetDecorator.targetLabel(graded_label, graded);
        
        UISelectChoice ungraded = UISelectChoice.make(form, "select_ungraded", grading_select_id, 1);
        UIMessage ungraded_label = UIMessage.make(form, "select_ungraded_label", "assignment2.assignment_add.assignment_ungraded");
        UILabelTargetDecorator.targetLabel(ungraded_label, ungraded);

        //Output the JS vars
        UIVerbatim.make(tofill, "gradebook_items_data", js_gradebook_items_data);
        
        
        //Links to gradebook Helper
        String url = externalLogic.getUrlForGradebookItemHelper(null, FinishedHelperProducer.VIEWID);
        UILink.make(form, "gradebook_item_new_helper",
        		UIMessage.make("assignment2.assignment_add.gradebook_item_new_helper"),
        		url);

        
        /******
         * Access
         */
        UIMessage.make(form, "access_legend", "assignment2.assignment_add.access_legend");
        String[] access_values = new String[] {
        		Boolean.FALSE.toString(),
        		Boolean.TRUE.toString()
        };
        String[] access_labels = new String[] {
        		"assignment2.assignment_add.access.not_restricted",
        		"assignment2.assignment_add.access.restricted"
        };
        Boolean restrictedToGroups = (assignment.getAssignmentGroupSet() != null && !assignment.getAssignmentGroupSet().isEmpty());
        UISelect access = UISelect.make(form, "access_select", access_values, access_labels,
        		"#{Assignment2Bean.restrictedToGroups}", restrictedToGroups.toString()).setMessageKeys();
        //((UIBoundString) access.selection).setValue(assignment.isRestrictedToGroups().toString());
        
        String accessId = access.getFullID();

        for (int i=0; i < access_values.length; i++) {
        	UIBranchContainer access_row = UIBranchContainer.make(form, "access_row:");
        	UISelectChoice radio = UISelectChoice.make(access_row, "access_choice", accessId, i);

        	UISelectLabel label = UISelectLabel.make(access_row, "access_label", accessId, i);
        	UILabelTargetDecorator.targetLabel(label, radio);
        }
        
        /**
         * Groups
         */
        Collection<Group> groups = externalLogic.getSiteGroups(currentContextId);
        List<String> currentGroups = assignment.getListOfAssociatedGroupReferences();
        for (Group g : groups){
        	//Update OTP
	        UIBranchContainer groups_row = UIBranchContainer.make(form, "groups_row:");
	        UIBoundBoolean checkbox = UIBoundBoolean.make(groups_row, "group_check",  
	        		"Assignment2Bean.selectedIds." + g.getId(), 
	        		(currentGroups == null || !currentGroups.contains(g.getId()) ? Boolean.FALSE : Boolean.TRUE));
	        UIOutput.make(groups_row, "group_label", g.getTitle());
	        UIOutput.make(groups_row, "group_description", g.getDescription());
        }
        
        //Notifications
        UIMessage.make(form, "notification_legend", "assignment2.assignment_add.notification_legend");
        String[] notification_type_values = new String[] {
        		String.valueOf(AssignmentConstants.NOTIFY_NONE),
        		String.valueOf(AssignmentConstants.NOTIFY_FOR_EACH),
        		String.valueOf(AssignmentConstants.NOTIFY_DAILY_SUMMARY)
        };
        String[] notification_type_labels = new String[] {
        		"assignment2.assignment_add.notification_type.notify_none",
        		"assignment2.assignment_add.notification_type.notify_each",
        		"assignment2.assignment_add.notification_type.notify_daily"
        };
        UISelect notifications = UISelect.make(form, "notifications_select", notification_type_values,
        		notification_type_labels, assignment2OTP + ".notificationType").setMessageKeys();
        //((UIBoundString) notifications.selection).setValue(String.valueOf(assignment.getNotificationType()));
        String notificationSelectId = notifications.getFullID();
        for (int i = 0; i < notification_type_values.length; i++){
        	UIBranchContainer notification_row = UIBranchContainer.make(form, "notification_row:");
        	UISelectChoice.make(notification_row, "notification_choice", notificationSelectId, i);
        	UISelectLabel.make(notification_row, "notification_label", notificationSelectId, i);
        }
        
        
        
        //Post Buttons
        UICommand.make(form, "post_assignment", UIMessage.make("assignment2.assignment_add.post"), "#{Assignment2Bean.processActionPost}");
        UICommand.make(form, "preview_assignment", UIMessage.make("assignment2.assignment_add.preview"), "#{Assignment2Bean.processActionPreview}");
        
        if (assignment == null || assignment.getId() == null || assignment.isDraft()){
        	UICommand.make(form, "save_draft", UIMessage.make("assignment2.assignment_add.save_draft"), "#{Assignment2Bean.processActionSaveDraft}");
        }
        UICommand.make(form, "cancel_assignment", UIMessage.make("assignment2.assignment_add.cancel_assignment"), "#{Assignment2Bean.processActionCancel}");
        
    }

	public List<NavigationCase> reportNavigationCases() {
    	List<NavigationCase> nav= new ArrayList<NavigationCase>();
        nav.add(new NavigationCase("post", new SimpleViewParameters(
            AssignmentListSortViewProducer.VIEW_ID)));
        nav.add(new NavigationCase("preview", new AssignmentViewParams(
        	FragmentAssignmentPreviewProducer.VIEW_ID, null)));
        nav.add(new NavigationCase("refresh", new AssignmentViewParams(
        	AssignmentProducer.VIEW_ID, null)));
        nav.add(new NavigationCase("save_draft", new SimpleViewParameters(
        	AssignmentListSortViewProducer.VIEW_ID)));
        nav.add(new NavigationCase("cancel", new SimpleViewParameters(
        	AssignmentListSortViewProducer.VIEW_ID)));
        return nav;
    }
	
	
    public ViewParameters getViewParameters() {
        return new AssignmentViewParams();
    }
    
    public void setMessageLocator(MessageLocator messageLocator) {
        this.messageLocator = messageLocator;
    }
    
    public void setRichTextEvolver(TextInputEvolver richTextEvolver) {
        this.richTextEvolver = richTextEvolver;
    }
    
    
    public void setExternalLogic(ExternalLogic externalLogic) {
    	this.externalLogic = externalLogic;
    }
    
    public void setExternalGradebookLogic(ExternalGradebookLogic externalGradebookLogic) {
    	this.externalGradebookLogic = externalGradebookLogic;
    }
    
    public void setLocale(Locale locale) {
    	this.locale = locale;
    }
	
	public void setAssignment2EntityBeanLocator(EntityBeanLocator entityBeanLocator) {
		this.assignment2BeanLocator = entityBeanLocator;
	}
	
	public void setAttachmentListRenderer(AttachmentListRenderer attachmentListRenderer){
		this.attachmentListRenderer = attachmentListRenderer;
	}
}