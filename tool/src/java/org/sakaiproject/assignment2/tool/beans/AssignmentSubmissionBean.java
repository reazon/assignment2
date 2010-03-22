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

package org.sakaiproject.assignment2.tool.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.assignment2.logic.AssignmentLogic;
import org.sakaiproject.assignment2.logic.AssignmentSubmissionLogic;
import org.sakaiproject.assignment2.logic.ExternalGradebookLogic;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.AssignmentSubmission;
import org.sakaiproject.assignment2.model.AssignmentSubmissionVersion;
import org.sakaiproject.assignment2.tool.StudentAction;
import org.sakaiproject.util.FormattedText;

import uk.org.ponder.beanutil.entity.EntityBeanLocator;
import uk.org.ponder.messageutil.TargettedMessage;
import uk.org.ponder.messageutil.TargettedMessageList;

/**
 * This bean is for binding the Assignment Submissions for various pages and
 * acting on them.
 * 
 * @author sgithens
 *
 */
public class AssignmentSubmissionBean {

    public static final String SUBMIT = "submit";
    public static final String SUBMIT_PREV = "submit_prev";
    public static final String SUBMIT_NEXT = "submit_next";
    public static final String RELEASE_PREV = "release_prev";
    public static final String RELEASE_NEXT = "release_next";
    public static final String SUBMIT_RETURNTOLIST = "submit_returnToList";
    public static final String RELEASE_RETURNTOLIST = "release_returnToList";
    public static final String PREVIEW = "preview";
    public static final String CANCEL = "cancel";
    public static final String SAVE_AND_EDIT_PREFIX = "SAVE_FEEDBACK_AND_EDIT_VERSION_";
    private static final String FAILURE = "failure";

    public Map<String, Boolean> selectedIds = new HashMap<String, Boolean>();
    public Long assignmentId;
    public String ASOTPKey;
    public String userId;
    public Boolean releaseFeedback;

    private TargettedMessageList messages;
    public void setMessages(TargettedMessageList messages) {
        this.messages = messages;
    }

    private AssignmentLogic assignmentLogic;
    public void setAssignmentLogic(AssignmentLogic assignmentLogic) {
        this.assignmentLogic = assignmentLogic;
    }

    private AssignmentSubmissionLogic submissionLogic;
    public void setSubmissionLogic(AssignmentSubmissionLogic submissionLogic) {
        this.submissionLogic = submissionLogic;
    }
    
    private ExternalGradebookLogic gradebookLogic;
    public void setExternalGradebookLogic(ExternalGradebookLogic gradebookLogic) {
        this.gradebookLogic = gradebookLogic;
    }

    private Map<String, AssignmentSubmission> OTPMap;
    private EntityBeanLocator asEntityBeanLocator;
    @SuppressWarnings("unchecked")
    public void setAssignmentSubmissionEntityBeanLocator(EntityBeanLocator entityBeanLocator) {
        this.OTPMap = entityBeanLocator.getDeliveredBeans();
        this.asEntityBeanLocator = entityBeanLocator;
    }

    private Map<String, AssignmentSubmissionVersion> asvOTPMap;
    public void setAsvEntityBeanLocator(EntityBeanLocator entityBeanLocator) {
        this.asvOTPMap = entityBeanLocator.getDeliveredBeans();
    }

    private Boolean honorPledge;
    public void setHonorPledge(Boolean honorPledge) {
        this.honorPledge = honorPledge;
    }

    private NotificationBean notificationBean;
    public void setNotificationBean(NotificationBean notificationBean) {
        this.notificationBean = notificationBean;
    }

    private Boolean overrideResubmissionSettings;
    public void setOverrideResubmissionSettings(Boolean overrideResubmissionSettings) {
        this.overrideResubmissionSettings = overrideResubmissionSettings;
    }

    private Boolean resubmitUntil;
    public void setResubmitUntil(Boolean resubmitUntil) {
        this.resubmitUntil = resubmitUntil;
    }
    
    private String grade;
    public void setGrade(String grade) {
        this.grade = grade;
    }
    
    private String gradeComment;
    public void setGradeComment(String gradeComment) {
        this.gradeComment = gradeComment;
    }

    /**
     * This property is used primarily with the 
     * {@link AssignmentSubmissionBean.processActionGradeSubmitAndEditAnotherVersion}
     * to set the next version to edit.
     */
    private Long nextVersionIdToEdit;

    public void setNextVersionIdToEdit(Long nextVersionIdToEdit) {
        this.nextVersionIdToEdit = nextVersionIdToEdit;
    }

    public Long getNextVersionIdToEdit() {
        return nextVersionIdToEdit;
    }

    private String submitOption = "submitOption";
    public void setSubmitOption (String submitOption)
    {
    	this.submitOption = submitOption;
    }


    /*
     * INSTRUCTOR FUNCTIONS
     */

    public String processActionSaveAndReleaseFeedbackForSubmission(){
        this.releaseFeedback = true;
        processActionGradeSubmit();

        return SUBMIT;
    }
    
    public String processActionGradeSubmitOption()
    {
    	String rv = "";
    	if (RELEASE_NEXT.equals(submitOption) || RELEASE_PREV.equals(submitOption) || RELEASE_RETURNTOLIST.equals(submitOption))
    	{
    	    rv = processActionSaveAndReleaseFeedbackForSubmission();
    	}
    	else
    	{
    		rv = processActionGradeSubmit();
    	}
    	return submitOption;
    }

    /**
     * This action method is primarily used for the "Edit" links on the Grade
     * page under the History section, where you want to go edit a previous 
     * version.  This method exists because we are supposed to save any changes
     * in the feedback on the current screen before going back to edit another
     * version.  This will do the usual work involved in saving (but NOT 
     * releasing) the feedback, and then return a formatted string whose first
     * half is the identifier demarcating this action, and the second part is 
     * ID of the Version we are going to edit next. This way the version id can
     * be used in the ActionResultInterceptor.
     * 
     * The format will look like this:
     * 
     * SAVE_FEEDBACK_AND_EDIT_VERSION_14
     * 
     * where 14 is the ID of the next version to edit.  Currently in our DB 
     * schema the version ID will always be Long.
     * 
     * @return
     */
    public String processActionGradeSubmitAndEditAnotherVersion() {
        String saveResult = processActionGradeSubmit();
        if (FAILURE.equals(saveResult)) {
            return FAILURE;
        }

        return SAVE_AND_EDIT_PREFIX + getNextVersionIdToEdit();
    }

    public String processActionGradeSubmit(){
        if (assignmentId == null || userId == null){
            return FAILURE;
        }
        Assignment2 assignment = assignmentLogic.getAssignmentById(assignmentId);
        AssignmentSubmission assignmentSubmission = new AssignmentSubmission(assignment, userId);
        
        // validate the grade entry first
        boolean allowedToGradeInGB = false;
        if (assignment.isGraded() && gradebookLogic.gradebookItemExists(assignment.getGradebookItemId())) {
            allowedToGradeInGB = gradebookLogic.isCurrentUserAbleToGradeStudentForItem(assignment.getContextId(), 
                    assignmentSubmission.getUserId(), assignment.getGradebookItemId());
        }
        
        if (allowedToGradeInGB) {
            boolean gradeValid = gradebookLogic.isGradeValid(assignment.getContextId(), grade);
            if (!gradeValid) {
                int gradeEntryType = gradebookLogic.getGradebookGradeEntryType(assignment.getContextId());
                String errorMessageRef;
                if (gradeEntryType == ExternalGradebookLogic.ENTRY_BY_POINTS) {
                    errorMessageRef = "assignment2.gradebook.grading.points.error";
                } else if (gradeEntryType == ExternalGradebookLogic.ENTRY_BY_PERCENT) {
                    errorMessageRef = "assignment2.gradebook.grading.percent.error";
                } else if (gradeEntryType == ExternalGradebookLogic.ENTRY_BY_LETTER) {
                    errorMessageRef = "assignment2.gradebook.grading.letter.error";
                } else {
                    errorMessageRef = "assignment2.gradebook.grading.unknown.error";
                }
                
                messages.addMessage(new TargettedMessage(errorMessageRef, new Object[] {}, TargettedMessage.SEVERITY_ERROR));
                return FAILURE;
            }
        }

        for (String key : OTPMap.keySet()){
            assignmentSubmission = OTPMap.get(key);
            assignmentSubmission.setAssignment(assignment);
            assignmentSubmission.setUserId(userId);

            if (resubmitUntil == null || Boolean.FALSE.equals(resubmitUntil)) {
                assignmentSubmission.setResubmitCloseDate(null);
            }

            if (overrideResubmissionSettings == null || !overrideResubmissionSettings) {
                assignmentSubmission.setNumSubmissionsAllowed(null);
                assignmentSubmission.setResubmitCloseDate(null);
            }
        }
        for (String key : asvOTPMap.keySet()){

            AssignmentSubmissionVersion asv = asvOTPMap.get(key);
            
            // validate the input text from the WYSIWYG editors
            if (asv.getFeedbackNotes() != null) {
                StringBuilder alertMsg = new StringBuilder();
                asv.setFeedbackNotes(FormattedText.processFormattedText(asv.getFeedbackNotes(), 
                        alertMsg, true, true));
                
                if (alertMsg != null && alertMsg.length() > 0) {
                    messages.addMessage(new TargettedMessage("assignment2.assignment_grade.error.feedback_notes", 
                            new Object[] {alertMsg.toString()}));
                    return FAILURE;
                }
            }
            if (asv.getAnnotatedText() != null) {
                StringBuilder alertMsg = new StringBuilder();
                asv.setAnnotatedText(FormattedText.processFormattedText(asv.getAnnotatedText(), 
                        alertMsg, true, true));
                
                if (alertMsg != null && alertMsg.length() > 0) {
                    messages.addMessage(new TargettedMessage("assignment2.assignment_grade.error.annotated_text", 
                            new Object[] {alertMsg.toString()}));
                    return FAILURE;
                }
            }

            asv.setAssignmentSubmission(assignmentSubmission);
            if (this.releaseFeedback != null && asv.getFeedbackReleasedDate() == null) {
                asv.setFeedbackReleasedDate(new Date());
            }

            submissionLogic.saveInstructorFeedback(asv.getId(), assignmentSubmission.getUserId(),
                    assignmentSubmission.getAssignment(), asv.getAnnotatedText(),
                    asv.getFeedbackNotes(), asv.getFeedbackReleasedDate(), asv.getFeedbackAttachSet());

            List<String> studentUids = new ArrayList<String>();
            studentUids.add(assignmentSubmission.getUserId());
            submissionLogic.updateStudentResubmissionOptions(studentUids, assignmentSubmission.getAssignment(), 
                    assignmentSubmission.getNumSubmissionsAllowed(), assignmentSubmission.getResubmitCloseDate());
        }
        
        if (allowedToGradeInGB) {
            // save the grade and comments
            gradebookLogic.saveGradeAndCommentForStudent(assignment.getContextId(), 
                    assignment.getGradebookItemId(), assignmentSubmission.getUserId(), grade, gradeComment);
        }
        
        return SUBMIT;
    }

    public String processActionGradePreview(){
        for (String key : OTPMap.keySet()){
            AssignmentSubmission assignmentSubmission = OTPMap.get(key);
            Assignment2 assignment = assignmentLogic.getAssignmentByIdWithAssociatedData(assignmentId);
            assignmentSubmission.setAssignment(assignment);
            // previewAssignmentSubmissionBean.setAssignmentSubmission(assignmentSubmission);
        }
        for (String key : asvOTPMap.keySet()){
            AssignmentSubmissionVersion asv = asvOTPMap.get(key);
            // previewAssignmentSubmissionBean.setAssignmentSubmissionVersion(asv);
        }
        return PREVIEW;
    }

    public String processActionCancel() {
        return CANCEL;
    }

    public StudentAction determineStudentAction(String studentId, Long assignmentId) {
        boolean isOpenForSubmission = submissionLogic.isSubmissionOpenForStudentForAssignment(
                studentId, assignmentId);

        int numSubmittedVersions = submissionLogic.getNumSubmittedVersions(studentId, assignmentId);

        StudentAction action = StudentAction.VIEW_DETAILS;

        // 1. View Details and Submit
        if (isOpenForSubmission && numSubmittedVersions < 1) {
            action = StudentAction.VIEW_AND_SUBMIT;
        }
        // 3. Resubmit
        else if (isOpenForSubmission && numSubmittedVersions >= 1) {
            action = StudentAction.VIEW_AND_RESUBMIT;
        }
        // 4a View Submission
        else if (numSubmittedVersions == 1) {
            action = StudentAction.VIEW_SUB;
        }
        // 4b View Submissions
        else if (numSubmittedVersions > 1) {
            action = StudentAction.VIEW_ALL_SUB;
        }
        // 2 View Details
        else {
            action = StudentAction.VIEW_DETAILS;
        }

        return action;
    }

}