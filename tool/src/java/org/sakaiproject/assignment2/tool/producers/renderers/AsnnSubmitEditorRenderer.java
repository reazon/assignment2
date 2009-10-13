package org.sakaiproject.assignment2.tool.producers.renderers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.assignment2.logic.AssignmentSubmissionLogic;
import org.sakaiproject.assignment2.logic.ExternalContentReviewLogic;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.AssignmentSubmission;
import org.sakaiproject.assignment2.model.AssignmentSubmissionVersion;
import org.sakaiproject.assignment2.model.constants.AssignmentConstants;
import org.sakaiproject.assignment2.tool.beans.StudentSubmissionVersionFlowBean;
import org.sakaiproject.assignment2.tool.params.FilePickerHelperViewParams;
import org.sakaiproject.assignment2.tool.producers.AddAttachmentHelperProducer;
import org.sakaiproject.assignment2.tool.producers.evolvers.AttachmentInputEvolver;

import uk.org.ponder.beanutil.entity.EntityBeanLocator;
import uk.org.ponder.messageutil.MessageLocator;
import uk.org.ponder.rsf.components.UIBoundBoolean;
import uk.org.ponder.rsf.components.UIBoundString;
import uk.org.ponder.rsf.components.UICommand;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIELBinding;
import uk.org.ponder.rsf.components.UIForm;
import uk.org.ponder.rsf.components.UIInput;
import uk.org.ponder.rsf.components.UIInputMany;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIJointContainer;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.components.decorators.DecoratorList;
import uk.org.ponder.rsf.components.decorators.UIFreeAttributeDecorator;
import uk.org.ponder.rsf.evolvers.TextInputEvolver;
import uk.org.ponder.rsf.producers.BasicProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;

/**
 * Renders the area of the Student Submit pages where the student does the 
 * actual work of putting in the submission text and uploading any attachments
 * that are part of the submission.
 * 
 * This does have to detect the type of assignment: Non-electronic, text,
 * text and attachments, attachments only.  In the future this may be 
 * modularized to allow new pluggable assignment types.
 * 
 * In the non-electronic or non-submission assignment type, this will really
 * just be a check box that says you've completed it and a "Save and Return"
 * button.
 * 
 * @author sgithens
 *
 */
public class AsnnSubmitEditorRenderer implements BasicProducer {

    // Dependency
    private MessageLocator messageLocator;
    public void setMessageLocator(MessageLocator messageLocator) {
        this.messageLocator = messageLocator;
    }

    // Dependency
    private TextInputEvolver richTextEvolver;
    public void setRichTextEvolver(TextInputEvolver richTextEvolver) {
        this.richTextEvolver = richTextEvolver;
    }

    // Dependency
    private AttachmentInputEvolver attachmentInputEvolver;
    public void setAttachmentInputEvolver(AttachmentInputEvolver attachmentInputEvolver){
        this.attachmentInputEvolver = attachmentInputEvolver;
    }

    // Dependency
    private AttachmentListRenderer attachmentListRenderer;
    public void setAttachmentListRenderer (AttachmentListRenderer attachmentListRenderer) {
        this.attachmentListRenderer = attachmentListRenderer;
    }

    // Dependency
    private ViewParameters viewParameters;
    public void setViewParameters(ViewParameters viewParameters) {
        this.viewParameters = viewParameters;
    }

    // Dependency
    private AssignmentSubmissionLogic submissionLogic;
    public void setSubmissionLogic(AssignmentSubmissionLogic submissionLogic) {
        this.submissionLogic = submissionLogic;
    }
    
    private ExternalContentReviewLogic contentReviewLogic;
    public void setExternalContentReviewLogic(ExternalContentReviewLogic contentReviewLogic) {
        this.contentReviewLogic = contentReviewLogic;
    }

    // Flow Scope Bean for Student Submission
    private StudentSubmissionVersionFlowBean studentSubmissionVersionFlowBean;
    public void setStudentSubmissionVersionFlowBean(StudentSubmissionVersionFlowBean studentSubmissionVersionFlowBean) {
        this.studentSubmissionVersionFlowBean = studentSubmissionVersionFlowBean;
    }

    /**
     * Renders the actual editing area for the Assignment Submission.  The 
     * beginning of the method signature is fairly self explanatory, but gets
     * a bit cabberwonky near the end there.  This is something that we might
     * want to rethink in the future ( or just leave if everything is going to
     * be rewritten in JavaScript anyways ).
     * 
     * The first boolean, preview, indicates (if true), that this is view is 
     * actually an Instructor who is authoring the assignment, and just wants to
     * see what it would look like if the student was completing it.
     * 
     * The second boolean, studentPreviewSubmission, indicates (if true), that 
     * this is a Student actually completing an assignment, but are previewing
     * their work before submitting (or editing some more).
     *  
     *  
     * @param parent
     * @param clientID
     * @param assignmentSubmission
     * @param preview
     * @param asvOTP
     */
    public void fillComponents(UIContainer parent, String clientID, AssignmentSubmission assignmentSubmission, boolean preview, boolean studentPreviewSubmission) {

        Assignment2 assignment = assignmentSubmission.getAssignment();

        UIJointContainer joint = new UIJointContainer(parent, clientID, "asnn2-submit-editor-widget:");
        String asOTP = "AssignmentSubmission.";
        String asOTPKey = "";
        if (assignmentSubmission != null && assignmentSubmission.getId() != null) {
            asOTPKey += assignmentSubmission.getId();
        } else {
            asOTPKey += EntityBeanLocator.NEW_PREFIX + "1";
        }
        asOTP = asOTP + asOTPKey;

        String asvOTP = null;
        if (!preview) {
            asvOTP = "StudentSubmissionVersionFlowBean.";
        }
        else {
            asvOTP = "AssignmentSubmissionVersion.";
        }
        String asvOTPKey = "";
        if (assignmentSubmission != null && assignmentSubmission.getCurrentSubmissionVersion() != null 
                && assignmentSubmission.getCurrentSubmissionVersion().isDraft()) {
            asvOTPKey += assignmentSubmission.getCurrentSubmissionVersion().getId();
        } else {
            asvOTPKey += EntityBeanLocator.NEW_PREFIX + "1";
        }
        asvOTP = asvOTP + asvOTPKey;

        //For preview, get a decorated list of disabled="disabled"
        Map<String, String> disabledAttr = new HashMap<String, String>();
        disabledAttr.put("disabled", "disabled");
        DecoratorList disabledDecoratorList = new DecoratorList(new UIFreeAttributeDecorator(disabledAttr));

        UIForm form = UIForm.make(joint, "form");

        // Fill in with submission type specific instructions
        // If this is a Student Preview, we dont' want these instruction headers
        // per the design spec.
        if (!studentPreviewSubmission) {
            UIOutput.make(form, "submission_instructions", messageLocator.getMessage("assignment2.student-submit.instructions." + assignment.getSubmissionType())); 
        }

        if (assignment.isHonorPledge()) {
            UIVerbatim.make(form, "required", messageLocator.getMessage("assignment2.student-submit.required"));
        }

        // Because the flow might not be starting on the initial view, the
        // studentSubmissionPreviewVersion should always use the flow bean 
        // unless it is null.
        AssignmentSubmissionVersion studentSubmissionPreviewVersion = 
            (AssignmentSubmissionVersion) studentSubmissionVersionFlowBean.locateBean(asvOTPKey);

        //Rich Text Input
        if (assignment.getSubmissionType() == AssignmentConstants.SUBMIT_INLINE_ONLY || 
                assignment.getSubmissionType() == AssignmentConstants.SUBMIT_INLINE_AND_ATTACH){

            UIOutput.make(form, "submit_text");

            if (studentPreviewSubmission) {
                // TODO FIXME This is being duplicated
                UIVerbatim make = UIVerbatim.make(form, "text:", studentSubmissionPreviewVersion.getSubmittedText());
            }
            else if (!preview) {
                UIInput text = UIInput.make(form, "text:", asvOTP + ".submittedText");
                text.mustapply = Boolean.TRUE;
                richTextEvolver.evolveTextInput(text);
            } 
            else {
                //disable textarea
                UIInput text = UIInput.make(form, "text:", asvOTP + ".submittedText");
                UIInput text_disabled = UIInput.make(form, "text_disabled",asvOTP + ".submittedText");
                text_disabled.decorators = disabledDecoratorList;
            }


        }

        // Attachment Stuff
        // the editor will only display attachments for the current version if
        // it is a draft. otherwise, the user is working on a new submission
        if (assignment.getSubmissionType() == AssignmentConstants.SUBMIT_ATTACH_ONLY ||
                assignment.getSubmissionType() == AssignmentConstants.SUBMIT_INLINE_AND_ATTACH){
            UIOutput attachSection = UIOutput.make(form, "submit_attachments");
            if (assignment.isContentReviewEnabled()) {
                attachSection.decorate(new UIFreeAttributeDecorator("class", "messageConfirmation"));
            }

            if (studentPreviewSubmission || !preview) {
                String[] attachmentRefs = 
                    studentSubmissionPreviewVersion.getSubmittedAttachmentRefs();

                renderSubmittedAttachments(studentPreviewSubmission, asvOTP,
                        asvOTPKey, form, attachmentRefs);
            }
        }

        // attachment only situations will not return any values in the OTP map; thus,
        // we won't enter the processing loop in processActionSubmit (and nothing will be saved)
        // this will force rsf to bind the otp mapping
        if (assignment.getSubmissionType() == AssignmentConstants.SUBMIT_ATTACH_ONLY) {
            UIInput.make(form, "submitted_text_for_attach_only", asvOTP + ".submittedText");
        }

        if (assignment.isHonorPledge()) {
            UIOutput.make(joint, "honor_pledge_fieldset");
            UIMessage.make(joint, "honor_pledge_label", "assignment2.student-submit.honor_pledge_text");
            UIBoundBoolean.make(form, "honor_pledge", "#{StudentSubmissionBean.honorPledge}");
        }
        
        // display plagiarism check warning
        if (assignment.isContentReviewEnabled() && contentReviewLogic.isContentReviewAvailable()) {
            if (assignment.getProperties().containsKey("s_view_report") && (Boolean)assignment.getProperties().get("s_view_report")) {
                UIMessage.make(joint, "plagiarism_check", "assignment2.turnitin.submit.warning.inst_and_student");
            } else {
                UIMessage.make(joint, "plagiarism_check", "assignment2.turnitin.submit.warning.inst_only");
            }
        }

        form.parameters.add( new UIELBinding("StudentSubmissionBean.ASOTPKey", asOTPKey));
        form.parameters.add( new UIELBinding("StudentSubmissionBean.assignmentId", assignment.getId()));

        /*
         * According to the spec, if a student is editing a submision they will
         * see the Submit,Preview, and Save&Exit buttons.  If they are previewing
         * a submission they will see Submit,Edit, and Save&Exit.
         * Don't display the buttons at all if this is the instructor preview
         */
        if (!preview) {
            UIOutput.make(form, "submit_section");
        }

        if (preview) {
            // don't display the buttons
        } else if (studentPreviewSubmission) {
            UICommand.make(form, "submit_button", UIMessage.make("assignment2.student-submit.submit"), 
            "StudentSubmissionBean.processActionSubmit");
            UICommand.make(form, "save_draft_button", UIMessage.make("assignment2.student-submit.save_draft"), 
            "StudentSubmissionBean.processActionSaveDraft");
            UICommand edit_button = UICommand.make(form, "back_to_edit_button", UIMessage.make("assignment2.student-submit.back_to_edit"),
            "StudentSubmissionBean.processActionBackToEdit");
            //edit_button.addParameter(new UIELBinding(asvOTP + ".submittedText", hackSubmissionText));
        } else {
            UICommand.make(form, "submit_button", UIMessage.make("assignment2.student-submit.submit"), 
            "StudentSubmissionBean.processActionSubmit");
            UICommand.make(form, "preview_button", UIMessage.make("assignment2.student-submit.preview"), 
            "StudentSubmissionBean.processActionPreview");
            UICommand.make(form, "save_draft_button", UIMessage.make("assignment2.student-submit.save_draft"), 
            "StudentSubmissionBean.processActionSaveDraft");
            UICommand.make(form, "cancel_button", UIMessage.make("assignment2.student-submit.cancel"), 
            "StudentSubmissionBean.processActionCancel");
        }

        /* 
         * Render the Instructor's Feedback Materials
         */
        if (!preview && !studentPreviewSubmission) {
            AssignmentSubmissionVersion currVersion = assignmentSubmission.getCurrentSubmissionVersion();
            if (currVersion.isDraft() && currVersion.isFeedbackReleased()) {
                UIOutput.make(joint, "draft-feedback");

                String feedbackComment = currVersion.getFeedbackNotes();
                if (feedbackComment == null || feedbackComment.trim().equals("")) {
                    feedbackComment = messageLocator.getMessage("assignment2.student-submission.feedback.none");
                }
                UIVerbatim.make(joint, "draft-feedback-text", feedbackComment);

                if (assignmentSubmission.getCurrentSubmissionVersion().getFeedbackAttachSet() != null && 
                        assignmentSubmission.getCurrentSubmissionVersion().getFeedbackAttachSet().size() > 0) {
                    UIMessage.make(joint, "draft-feedback-attachments-header", "assignment2.student-submission.feedback.materials.header");
                    attachmentListRenderer.makeAttachmentFromFeedbackAttachmentSet(joint, 
                            "draft-feedback-attachment-list:", viewParameters.viewID, 
                            currVersion.getFeedbackAttachSet());
                }

                // mark this feedback as viewed
                if (!currVersion.isFeedbackRead()) {
                    List<Long> versionIdList = new ArrayList<Long>();
                    versionIdList.add(currVersion.getId());
                    submissionLogic.markFeedbackAsViewed(assignmentSubmission.getId(), versionIdList);
                }
            }
        }

    }

    /**
     * @param studentPreviewSubmission
     * @param asvOTP
     * @param asvOTPKey
     * @param form
     * @param attachmentRefs
     */
    private void renderSubmittedAttachments(boolean studentPreviewSubmission,
            String asvOTP, String asvOTPKey, UIForm form,
            String[] attachmentRefs) {
        UIInputMany attachmentInput = UIInputMany.make(form, "attachment_list:", asvOTP + ".submittedAttachmentRefs", 
                attachmentRefs);
        attachmentInputEvolver.evolveAttachment(attachmentInput, !studentPreviewSubmission);

        if (!studentPreviewSubmission) {
            UIInternalLink.make(form, "add_submission_attachments", UIMessage.make("assignment2.student-submit.add_attachments"),
                    new FilePickerHelperViewParams(AddAttachmentHelperProducer.VIEWID, Boolean.TRUE, 
                            Boolean.TRUE, 500, 700, asvOTPKey, true));
        }

        UIOutput.make(form, "no_attachments_yet", messageLocator.getMessage("assignment2.student-submit.no_attachments"));
    }

    public void fillComponents(UIContainer parent, String clientID) {

    }

}
