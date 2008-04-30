/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2007 The Sakai Foundation.
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

package org.sakaiproject.assignment2.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The AssignmentSubmissionVersion object
 * 
 * @author <a href="mailto:wagnermr@iupui.edu">michelle wagner</a>
 */
public class AssignmentSubmissionVersion implements FeedbackVersion {
	
	private Long id;
	private AssignmentSubmission assignmentSubmission;
	private Date submittedTime;
	private Date releasedTime;
	private String annotatedText;
	private String feedbackNotes;
	private String submittedText;
	private Boolean draft;
	private String reviewReportUrl;
	private int reviewReportScore;
	private String reviewStatus;
	private String reviewIconUrl;
	private String createdBy;
	private Date createdTime;
	private String modifiedBy;
	private Date modifiedTime;
	private String lastFeedbackSubmittedBy;
	private Date lastFeedbackTime;
	private Set<FeedbackAttachment> feedbackAttachSet;
	private Set<SubmissionAttachment> submissionAttachSet;
	private int revisionVersion;

	public AssignmentSubmissionVersion() {
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @return time this assignment was submitted. If null, assignment has not
	 * been submitted for this user.
	 */
	public Date getSubmittedTime() {
		return submittedTime;
	}
	
	/**
	 * Set the time the assignment was submitted. Null if no submission yet.
	 * @param submittedTime
	 */
	public void setSubmittedTime(Date submittedTime) {
		this.submittedTime = submittedTime;
	}
	
	/**
	 * 
	 * @return text composed of the submission with grader-added annotation
	 */
	public String getAnnotatedText() {
		return annotatedText;
	}
	
	/**
	 * 
	 * @return formatted text composed of the inline submission with grader-added annotation
	 */
	public String getAnnotatedTextFormatted() {	
		if (annotatedText == null) {
			return "";
		}
    	Pattern p = Pattern.compile("\\{\\{([^\\}]+|\\}(?!\\}))\\}\\}");
    	Matcher m = p.matcher(annotatedText);
    	StringBuffer sb = new StringBuffer();
    	while(m.find()){
    		m.appendReplacement(sb, "<span class=\"highlight\">$1</span>");
    	}
    	m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * set the text composed of the inline submission with grader-added annotation
	 * @param annotatedText
	 */
	public void setAnnotatedText(String annotatedText) {
		this.annotatedText = annotatedText;
	}
	
	/**
	 * 
	 * @return the text of the submission
	 */
	public String getSubmittedText() {
		return submittedText;
	}

	/**
	 * set the text of the submission
	 * @param submittedText
	 */
	public void setSubmittedText(String submittedText) {
		this.submittedText = submittedText;
	}
	
	/**
	 * 
	 * @return true if the submitter has started working on the submission
	 * but has not yet submitted it for review
	 */
	public Boolean isDraft() {
		return draft;
	}

	/**
	 * set the draft status
	 * @param draft
	 */
	public void setDraft(Boolean draft) {
		this.draft = draft;
	}
	
	/**
	 * 
	 * @return the URL of the content review report (ie from turnitin)
	 */
	public String getReviewReportUrl() {
		return reviewReportUrl;
	}

	/**
	 * set the URL of the content review report (ie from turnitin)
	 * @param reviewReportUrl
	 */
	public void setReviewReportUrl(String reviewReportUrl) {
		this.reviewReportUrl = reviewReportUrl;
	}
	
	/**
	 * 
	 * @return the score from the content review service (ie from turnitin)
	 */
	public int getReviewReportScore() {
		return reviewReportScore;
	}

	/**
	 * set the score from the content review service (ie from turnitin)
	 * @param reviewReportScore
	 */
	public void setReviewReportScore(int reviewReportScore) {
		this.reviewReportScore = reviewReportScore;
	}
	
	/**
	 * 
	 * @return the status of the content review (ie from turnitin)
	 */
	public String getReviewStatus() {
		return reviewStatus;
	}

	/**
	 * set the status of the content review (ie from turnitin)
	 * @param reviewStatus
	 */
	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}
	
	/**
	 * 
	 * @return the URL of the content review icon associated with 
	 * this submission (ie from turnitin)
	 */
	public String getReviewIconUrl() {
		return reviewIconUrl;
	}

	/**
	 * set the URL of the content review icon associated with this 
	 * submission (ie from turnitin)
	 * @param reviewIconUrl
	 */
	public void setReviewIconUrl(String reviewIconUrl) {
		this.reviewIconUrl = reviewIconUrl;
	}

	/**
	 * Additional feedback comments provided by the "grader"
	 * @return feedbackNotes
	 */
	public String getFeedbackNotes() {
		return feedbackNotes;
	}

	/**
	 * Additional feedback comments provided by the "grader"
	 * @param feedbackNotes
	 */
	public void setFeedbackNotes(String feedbackNotes) {
		this.feedbackNotes = feedbackNotes;
	}

	/**
	 * 
	 * @return the Date feedback for this version was released to the submitter.
	 */
	public Date getReleasedTime() {
		return releasedTime;
	}

	/**
	 * set the Date feedback for this version was released to the submitter. 
	 * @param releasedTime
	 */
	public void setReleasedTime(Date releasedTime) {
		this.releasedTime = releasedTime;
	}

	/**
	 * 
	 * @return the userId of the person who created this version
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * set the userId of the person who created this version
	 * @param createdBy
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	/**
	 * 
	 * @return the date this version was created
	 */
	public Date getCreatedTime() {
		return createdTime;
	}

	/**
	 * the date this version was created
	 * @param createdTime
	 */
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	
	/**
	 * 
	 * @return the userId of the person who made the last modification to
	 * the submission
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * set the userId of the person who made the last modification to
	 * the submission
	 * @param modifiedBy
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * 
	 * @return the date this version was last modified
	 */
	public Date getModifiedTime() {
		return modifiedTime;
	}

	/**
	 * set the date this version was last modified
	 * @param modifiedTime
	 */
	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	/**
	 * 
	 * @return the parent submission record associated with this version
	 */
	public AssignmentSubmission getAssignmentSubmission() {
		return assignmentSubmission;
	}

	/**
	 * set the parent submission record associated with this version
	 * @param assignmentSubmission
	 */
	public void setAssignmentSubmission(AssignmentSubmission assignmentSubmission) {
		this.assignmentSubmission = assignmentSubmission;
	}

	/**
	 * 
	 * @return the userid who last submitted feedback on this version
	 */
	public String getLastFeedbackSubmittedBy() {
		return lastFeedbackSubmittedBy;
	}

	/**
	 *  the userid who last submitted feedback on this version
	 * @param lastFeedbackSubmittedBy
	 */
	public void setLastFeedbackSubmittedBy(String lastFeedbackSubmittedBy) {
		this.lastFeedbackSubmittedBy = lastFeedbackSubmittedBy;
	}

	/**
	 * 
	 * @return the date that the feedback for this version was last updated
	 */
	public Date getLastFeedbackTime() {
		return lastFeedbackTime;
	}

	/**
	 * the date that the feedback for this version was last updated
	 * @param lastFeedbackTime
	 */
	public void setLastFeedbackTime(Date lastFeedbackTime) {
		this.lastFeedbackTime = lastFeedbackTime;
	}

	/**
	 * 
	 * @return the FeedbackAttachments associated with this submission
	 * version
	 */
	public Set<FeedbackAttachment> getFeedbackAttachSet() {
		return feedbackAttachSet;
	}

	/**
	 * 
	 * @param feedbackAttachSet
	 * the FeedbackAttachments associated with this submission version
	 */
	public void setFeedbackAttachSet(
			Set<FeedbackAttachment> feedbackAttachSet) {
		this.feedbackAttachSet = feedbackAttachSet;
	}
	
	/**
	 * 
	 * @return the SubmissionAttachments associated with this
	 * submission version
	 */
	public Set<SubmissionAttachment> getSubmissionAttachSet() {
		return submissionAttachSet;
	}

	/**
	 * 
	 * @param submissionAttachSet
	 * the SubmissionAttachments associated with this
	 * submission version
	 */
	public void setSubmissionAttachSet(
			Set<SubmissionAttachment> submissionAttachSet) {
		this.submissionAttachSet = submissionAttachSet;
	}
	
	/**
	 * the int value of the version number for this assignment. not
     * to be confused with submission version.
	 * @return
	 */
	public int getRevisionVersion() {
		return revisionVersion;
	}

	/**
	 * the int value of the version number for this assignment. not
     * to be confused with submission version.
	 * @param revisionVersion
	 */
	public void setRevisionVersion(int revisionVersion) {
		this.revisionVersion = revisionVersion;
	}
	
	public String[] getFeedbackAttachmentRefs()
	{
		if (feedbackAttachSet == null) { return new String[]{}; }
		String[] refs = new String[feedbackAttachSet.size()];   /// HEY FOR FUTURE RYAN _ THIS IS NULL, PROLLY THE OTP GETTER
		int i = 0;
		for (FeedbackAttachment fa : feedbackAttachSet) {
			refs[i++] = fa.getAttachmentReference();
		}
		return refs;
	}

	public void setFeedbackAttachmentRefs(String[] attachmentRefs)
	{
		Set<FeedbackAttachment> set = new HashSet<FeedbackAttachment>();
		for (int i = 0; i < attachmentRefs.length; i++) {
			FeedbackAttachment fa = new FeedbackAttachment();
			fa.setSubmissionVersion(this);
			fa.setAttachmentReference(attachmentRefs[i]);
			set.add(fa);
		}
		this.feedbackAttachSet = set;
	}
	
	public String[] getSubmittedAttachmentRefs()
	{
		if (submissionAttachSet == null) { return new String[]{}; }
		String[] refs = new String[submissionAttachSet.size()];
		int i = 0;
		for (SubmissionAttachment sa : submissionAttachSet) {
			refs[i++] = sa.getAttachmentReference();
		}
		return refs;
	}

	public void setSubmittedAttachmentRefs(String[] attachmentRefs)
	{
		Set<SubmissionAttachment> set = new HashSet<SubmissionAttachment>();
		for (int i = 0; i < attachmentRefs.length; i++) {
			SubmissionAttachment sa = new SubmissionAttachment();
			sa.setSubmissionVersion(this);
			sa.setAttachmentReference(attachmentRefs[i]);
			set.add(sa);
		}
		this.submissionAttachSet = set;
	}
}
