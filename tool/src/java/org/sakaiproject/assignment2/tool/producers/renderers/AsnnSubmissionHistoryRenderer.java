package org.sakaiproject.assignment2.tool.producers.renderers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.sakaiproject.assignment2.logic.AssignmentSubmissionLogic;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.AssignmentSubmission;
import org.sakaiproject.assignment2.model.AssignmentSubmissionVersion;

import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIJointContainer;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.producers.BasicProducer;

/**
 * This renders the aggregated view of all the submissions from a student for
 * a particular assignment. It does this by using collapsable drop downs for 
 * each version. For each one of those it uses the 
 * {@link AsnnSubmissionVersionRenderer} to render that drop downs content.
 * 
 * This also needs to show some nifty notifications on the title bars for
 * each version if that version has any feedback that has not been read. Also,
 * if any of the aggregated versions have new feedback, an overall notification
 * should be present somewhere at the top of the widget.
 * 
 * Currently this renderer is marking the submissions as viewed, so only use it
 * for a student who is actually viewing a submission.
 * 
 * @author sgithens
 *
 */
public class AsnnSubmissionHistoryRenderer implements BasicProducer {

    // Dependency
    private AsnnSubmissionVersionRenderer asnnSubmissionVersionRenderer;
    public void setAsnnSubmissionVersionRenderer(AsnnSubmissionVersionRenderer asnnSubmissionVersionRenderer) {
        this.asnnSubmissionVersionRenderer = asnnSubmissionVersionRenderer;
    }

    // Dependency
    private AssignmentSubmissionLogic submissionLogic;
    public void setSubmissionLogic(AssignmentSubmissionLogic submissionLogic) {
        this.submissionLogic = submissionLogic;
    }

    public void fillComponents(UIContainer parent, String clientID, AssignmentSubmission assignmentSubmission) {
        UIJointContainer joint = new UIJointContainer(parent, clientID, "asnn2-submission-history-widget:");       

        Assignment2 assignment = assignmentSubmission.getAssignment();
        List<AssignmentSubmissionVersion> versionHistory = new ArrayList<AssignmentSubmissionVersion>();
        versionHistory = submissionLogic.getVersionHistoryForSubmission(assignmentSubmission);

        if (versionHistory.size() == 1) {
            AssignmentSubmissionVersion curVersion = versionHistory.get(0);
            asnnSubmissionVersionRenderer.fillComponents(joint, "single-submission-version:", curVersion);
            List<Long>versionIds = new ArrayList<Long>();
            versionIds.add(curVersion.getId());
            submissionLogic.markFeedbackAsViewed(assignmentSubmission.getId(), versionIds);
        }

        //TODO FIXME We'll have to think about the VERSION 0 logic here
        UIOutput.make(joint, "multiple-submissions");
        
        for (AssignmentSubmissionVersion version: versionHistory) {
            UIBranchContainer versionDiv = UIBranchContainer.make(joint, "submission-version:");
            UIOutput.make(versionDiv, "header-text", version.getSubmittedDate().toLocaleString());
            asnnSubmissionVersionRenderer.fillComponents(versionDiv, "submission-entry:", version);
        }
    }

    public void fillComponents(UIContainer parent, String clientID) {

    }

}
