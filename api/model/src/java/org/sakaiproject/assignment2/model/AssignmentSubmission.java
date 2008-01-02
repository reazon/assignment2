/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/assignment2/trunk/api/model/src/java/org/sakaiproject/assignment2/model/AssignmentSubmission.java $
 * $Id: AssignmentSubmission.java 12544 2006-05-03 15:06:26Z wagnermr@iupui.edu $
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

import java.util.Set;
import java.util.Date;

/**
 * The AssignmentSubmission object
 * 
 * @author <a href="mailto:wagnermr@iupui.edu">michelle wagner</a>
 */
public class AssignmentSubmission {
	
	private Long submissionId;
	private Assignment2 assignment;
	private String userId;
	private Boolean allowResubmit;
	private Date resubmitCloseTime;
	private Set submissionHistorySet;
	
	// fields populated with gradebook data
	private String gradebookGrade;
	private String gradebookComment;
	
	// the current submission version must be populated manually b/c we want
	// to retrieve the version rec with the highest id
	private AssignmentSubmissionVersion currentSubmissionVersion;
	
	private boolean currentVersionIsDraft;

	public AssignmentSubmission() {
	}
	
	public AssignmentSubmission(Assignment2 assignment, String userId) {
		this.assignment = assignment;
		this.userId = userId;
	}
	
	/**
	 * 
	 * @return assignment submission id
	 */
	public Long getSubmissionId() {
		return submissionId;
	}
	
	/**
	 * set assignment submission id
	 * @param submissionId
	 */
	public void setSubmissionId(Long submissionId) {
		this.submissionId = submissionId;
	}
	
	/**
	 * 
	 * @return	the parent assignment
	 */
	public Assignment2 getAssignment() {
		return assignment;
	}

	/**
	 * set the parent assignment
	 * @param assignment
	 */
	public void setAssignment(Assignment2 assignment) {
		this.assignment = assignment;
	}
	
	/**
	 * 
	 * @return user id associated with this submission
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * set the user id associated with this submission
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * 
	 * @return a set of AssignmentSubmissionVersion recs that represent
	 * the submission history for this user
	 */
	public Set getSubmissionHistorySet() {
		return submissionHistorySet;
	}

	/**
	 * the set of AssignmentSubmissionVersion recs that represent
	 * the submission history for this user
	 * @param submissionHistorySet
	 */
	public void setSubmissionHistorySet(Set submissionHistorySet) {
		this.submissionHistorySet = submissionHistorySet;
	}

	
	// fields populated with data from the gradebook
	
	/**
	 * 
	 * @return the grade for this submission from the associated gb item. This
	 * grade will be returned in converted form according to the gradebook's
	 * grade entry type (ie letter grade, percentage, etc)
	 */
	public String getGradebookGrade() {
		return gradebookGrade;
	}
	
	/**
	 * set the grade for this submission to be stored in the associated gb item.
	 * This grade must be in the correct form according to the gradebook's
	 * grade entry type (ie letter grade, percentage, etc)
	 * @param gradebookGrade
	 */
	public void setGradebookGrade(String gradebookGrade) {
		this.gradebookGrade = gradebookGrade;
	}
	
	/**
	 * 
	 * @return the gradebook comment from the associated gb item for this student
	 */
	public String getGradebookComment() {
		return gradebookComment;
	}
	
	/**
	 * set the comment to be stored in the gradebook
	 * @param gradebookComment
	 */
	public void setGradebookComment(String gradebookComment) {
		this.gradebookComment = gradebookComment;
	}

	/**
	 * 
	 * @return true if the submitter is allowed to resubmit this assignment
	 */
	public Boolean isAllowResubmit() {
		return allowResubmit;
	}

	/**
	 * set whether or not the submitter is allowed to resubmit this assignment
	 * @param allowResubmit
	 */
	public void setAllowResubmit(Boolean allowResubmit) {
		this.allowResubmit = allowResubmit;
	}
	
	/**
	 * 
	 * @return time after which the submitter may no longer submit this assignment
	 */
	public Date getResubmitCloseTime() {
		return resubmitCloseTime;
	}

	/**
	 * set the time after which no more submissions will be accepted
	 * @param resubmitCloseTime
	 */
	public void setResubmitCloseTime(Date resubmitCloseTime) {
		this.resubmitCloseTime = resubmitCloseTime;
	}

	// non-persisted fields
	
	// not persisted but convenient here for UI
	/**
	 * <b>Note</b> This is not a persisted field but must be handled specially
	 * when you want to retrieve or update this information
	 * @return The current AssignmentSubmissionVersion for this submission. Each
	 * modification to the submission will result in a new AssignmentSubmissionVersion
	 * record so we maintain a history.
	 */
	public AssignmentSubmissionVersion getCurrentSubmissionVersion() {
		return currentSubmissionVersion;
	}

	/**
	 * <b>Note</b> This is not a persisted field but must be handled specially
	 * when you want to retrieve or update this information
	 * 
	 * Set the current AssignmentSubmissionVersion for this submission. Each
	 * modification to the submission will result in a new AssignmentSubmissionVersion
	 * record so we maintain a history.
	 * @param currentSubmissionVersion
	 */
	public void setCurrentSubmissionVersion(AssignmentSubmissionVersion currentSubmissionVersion) {
		this.currentSubmissionVersion = currentSubmissionVersion;
	}
	
	/**
	 * 
	 * @return true if the most recent submission version is in draft status
	 */
	public boolean isCurrentVersionIsDraft() {
		return currentVersionIsDraft;
	}
	
	/**
	 * true if the most recent submission version is in draft status
	 * @param currentVersionIsDraft
	 */
	public void setCurrentVersionIsDraft(boolean currentVersionIsDraft) {
		this.currentVersionIsDraft = currentVersionIsDraft;
	}
}
