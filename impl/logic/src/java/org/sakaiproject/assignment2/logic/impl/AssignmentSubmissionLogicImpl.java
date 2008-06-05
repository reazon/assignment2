/**********************************************************************************
 * $URL:https://source.sakaiproject.org/contrib/assignment2/trunk/impl/logic/src/java/org/sakaiproject/assignment2/logic/impl/AssignmentSubmissionLogicImpl.java $
 * $Id:AssignmentSubmissionLogicImpl.java 48274 2008-04-23 20:07:00Z wagnermr@iupui.edu $
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

package org.sakaiproject.assignment2.logic.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.StaleObjectStateException;
import org.sakaiproject.assignment2.dao.AssignmentDao;
import org.sakaiproject.assignment2.exception.AssignmentNotFoundException;
import org.sakaiproject.assignment2.exception.StaleObjectModificationException;
import org.sakaiproject.assignment2.exception.SubmissionNotFoundException;
import org.sakaiproject.assignment2.exception.VersionNotFoundException;
import org.sakaiproject.assignment2.logic.AssignmentPermissionLogic;
import org.sakaiproject.assignment2.logic.AssignmentSubmissionLogic;
import org.sakaiproject.assignment2.logic.ExternalGradebookLogic;
import org.sakaiproject.assignment2.logic.ExternalLogic;
import org.sakaiproject.assignment2.logic.utils.ComparatorsUtils;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.AssignmentSubmission;
import org.sakaiproject.assignment2.model.AssignmentSubmissionVersion;
import org.sakaiproject.assignment2.model.FeedbackAttachment;
import org.sakaiproject.assignment2.model.FeedbackVersion;
import org.sakaiproject.assignment2.model.SubmissionAttachment;
import org.sakaiproject.assignment2.model.SubmissionAttachmentBase;
import org.sakaiproject.assignment2.model.constants.AssignmentConstants;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;

/**
 * This is the interface for interaction with the AssignmentSubmission object
 * 
 * @author <a href="mailto:wagnermr@iupui.edu">michelle wagner</a>
 */
public class AssignmentSubmissionLogicImpl implements AssignmentSubmissionLogic{
	
	private static Log log = LogFactory.getLog(AssignmentSubmissionLogicImpl.class);
	
	private ExternalLogic externalLogic;
	public void setExternalLogic(ExternalLogic externalLogic) {
		this.externalLogic = externalLogic;
	}

	private AssignmentDao dao;
	public void setDao(AssignmentDao dao) {
		this.dao = dao;
	}

	private ExternalGradebookLogic gradebookLogic;
	public void setExternalGradebookLogic(ExternalGradebookLogic gradebookLogic) {
		this.gradebookLogic = gradebookLogic;
	}

	private AssignmentPermissionLogic permissionLogic;
	public void setPermissionLogic(AssignmentPermissionLogic permissionLogic) {
		this.permissionLogic = permissionLogic;
	}

	public void init(){
		if (log.isDebugEnabled()) log.debug("init");
	}
	
	public AssignmentSubmission getAssignmentSubmissionById(Long submissionId){
		if (submissionId == null) {
			throw new IllegalArgumentException("Null submissionId passed to getAssignmentSubmissionById");
		}

		AssignmentSubmission submission =  (AssignmentSubmission) dao.findById(AssignmentSubmission.class, submissionId);
		if (submission == null) {
			throw new SubmissionNotFoundException("No submission found with id: " + submissionId);
		}
		
		String currentUserId = externalLogic.getCurrentUserId();

		// if the submission rec exists, we need to grab the most current version

		Assignment2 assignment = submission.getAssignment();

		if (!permissionLogic.isUserAbleToViewStudentSubmissionForAssignment(submission.getUserId(), assignment)) {
			throw new SecurityException("user" + currentUserId + " attempted to view submission with id " + submissionId + " but is not authorized");
		}

		AssignmentSubmissionVersion currentVersion = dao.getCurrentSubmissionVersionWithAttachments(submission);

		if (currentVersion != null) {
			filterOutRestrictedVersionInfo(currentVersion, currentUserId);
		}

		submission.setCurrentSubmissionVersion(currentVersion);

		return submission;
	}
	
	public AssignmentSubmissionVersion getSubmissionVersionById(Long submissionVersionId) {
		if (submissionVersionId == null) {
			throw new IllegalArgumentException("null submissionVersionId passed to getSubmissionVersionById");
		}
		
		AssignmentSubmissionVersion version = dao.getAssignmentSubmissionVersionByIdWithAttachments(submissionVersionId);
		
		if (version == null) {
			throw new VersionNotFoundException("No AssignmentSubmissionVersion exists with id: " + submissionVersionId);
		}
		
		String currentUserId = externalLogic.getCurrentUserId();
		
		if (version != null) {		
			AssignmentSubmission submission = version.getAssignmentSubmission();
			Assignment2 assignment = submission.getAssignment();
			
			// ensure that the current user is authorized to view this user for this assignment
			if (!permissionLogic.isUserAbleToViewStudentSubmissionForAssignment(submission.getUserId(), assignment)) {
				throw new SecurityException("User " + currentUserId + " attempted to access the version " + 
						submissionVersionId + " for student " + submission.getUserId() + " without authorization");
			}
			
			filterOutRestrictedVersionInfo(version, currentUserId);
			
			// populate gradebook information, if appropriate
			/*if (!assignment.isUngraded() && assignment.getGradableObjectId() != null) {
				gradebookLogic.populateAllGradeInfoForSubmission(externalLogic.getCurrentContextId(), 
						currentUserId, submission);
			}*/
		}
		
		return version;
	}
	
	public AssignmentSubmission getCurrentSubmissionByAssignmentIdAndStudentId(Long assignmentId, String studentId) {
		if (assignmentId == null || studentId == null) {
			throw new IllegalArgumentException("Null assignmentId or userId passed to getCurrentSubmissionByAssignmentAndUser");
		}

		String currentUserId = externalLogic.getCurrentUserId();
		
		Assignment2 assignment = dao.getAssignmentByIdWithGroups(assignmentId);
		
		if (assignment == null) {
			throw new AssignmentNotFoundException("No assignment found with id: " + assignmentId);
		}

		if (!permissionLogic.isUserAbleToViewStudentSubmissionForAssignment(studentId, assignment)) {
			throw new SecurityException("Current user " + currentUserId + " is not allowed to view submission for " + studentId + " for assignment " + assignment.getId());
		}

		AssignmentSubmission submission = dao.getSubmissionWithVersionHistoryForStudentAndAssignment(studentId, assignment);

		if (submission == null) {
			// return an "empty" submission
			submission = new AssignmentSubmission(assignment, studentId);
		} else {
			filterOutRestrictedInfo(submission, currentUserId, true);
		} 

		return submission;
	}
	
	public void saveStudentSubmission(String userId, Assignment2 assignment, Boolean draft, 
			String submittedText, Set<SubmissionAttachment> subAttachSet) {
		if (userId == null || assignment == null || draft == null) {
			throw new IllegalArgumentException("null userId, assignment, or draft passed to saveAssignmentSubmission");
		}
		
		if (assignment.getId() == null) {
			throw new IllegalArgumentException("assignment without an id passed to saveStudentSubmission");
		}
		
		Date currentTime = new Date();
		String currentUserId = externalLogic.getCurrentUserId();
		String contextId = externalLogic.getCurrentContextId();
		
		if (!currentUserId.equals(userId)) {
			throw new SecurityException("User " + currentUserId + " attempted to save a submission for " +
					userId + ". You may only make a submission for yourself!");
		}
		
		if (!permissionLogic.isUserAbleToMakeSubmissionForAssignment(contextId, assignment)) {
			log.warn("User " + currentUserId + " attempted to make a submission " +
					"without authorization for assignment " + assignment.getId());
			throw new SecurityException("User " + currentUserId + " attempted to make a submission " +
					"without authorization for assignment " + assignment.getId());
		}
		
		if (!submissionIsOpenForStudentForAssignment(currentUserId, assignment.getId())) {
			log.warn("User " + currentUserId + " attempted to make a submission " +
					"but submission for this user for assignment " + assignment.getId() + " is closed.");
			throw new SecurityException("User " + currentUserId + " attempted to make a submission " +
					"for closed assignment " + assignment.getId());
		}
		
		// if there is no current version or the most recent version was submitted, we will need
		// to create a new version. If the current version is draft, we will continue to update
		// this version until it is submitted
		
		// retrieve the latest submission for this user and assignment
		AssignmentSubmission submission = getAssignmentSubmissionForStudentAndAssignment(assignment, userId);
		AssignmentSubmissionVersion version = null;

		boolean isAnUpdate = false;
		
		if (submission == null) {
			// there is no submission for this user yet
			submission = new AssignmentSubmission(assignment, userId);
			
			// we need to create a new version for this submission
			version = new AssignmentSubmissionVersion();
		} else {
			// the submission exists, so we need to check the current version
			version = dao.getCurrentSubmissionVersionWithAttachments(submission);
			
			// if the current version hasn't been submitted yet, we will
			// update that one. otherwise, create a new version
			if (version != null && version.getSubmittedTime() == null) {
				
				isAnUpdate = true;
				
			} else {
				// we need to create a new version for this submission
				version = new AssignmentSubmissionVersion();
			}
		}

		// now let's set the new data
		version.setAssignmentSubmission(submission);
		version.setDraft(draft);
		version.setSubmittedText(submittedText);

		if (isAnUpdate) {
			version.setModifiedBy(currentUserId);
			version.setModifiedTime(currentTime);
		} else {
			version.setCreatedBy(currentUserId);
			version.setCreatedTime(currentTime);
		}

		if (!version.isDraft()) {
			version.setSubmittedTime(currentTime);
			
			// if this isn't a draft, set the annotated text to be the submitted text
			// to allow instructor to provide inline comments for submitted text
			version.setAnnotatedText(submittedText);
		}

		// identify any attachments that were deleted or need to be created
		// - we don't update attachments
		Set<SubmissionAttachmentBase> attachToDelete = identifyAttachmentsToDelete(version.getSubmissionAttachSet(), subAttachSet);
		Set<SubmissionAttachmentBase> attachToCreate = identifyAttachmentsToCreate(subAttachSet);

		// make sure the version was populated on the FeedbackAttachments
		populateVersionForAttachmentSet(attachToCreate, version);
		populateVersionForAttachmentSet(attachToDelete, version);

		try {

			Set<AssignmentSubmissionVersion> versionSet = new HashSet<AssignmentSubmissionVersion>();
			versionSet.add(version);

			Set<AssignmentSubmission> submissionSet = new HashSet<AssignmentSubmission>();
			submissionSet.add(submission);
			
			if (attachToCreate == null) {
				attachToCreate = new HashSet<SubmissionAttachmentBase>();
			}

			dao.saveMixedSet(new Set[] {submissionSet, versionSet, attachToCreate});
			if (log.isDebugEnabled()) log.debug("Updated/Added student submission version " + 
					version.getId() + " for user " + submission.getUserId() + " for assignment " + 
					submission.getAssignment().getTitle()+ " ID: " + submission.getAssignment().getId());
			if (log.isDebugEnabled()) log.debug("New submission attachments created: " + attachToCreate.size());

			if (attachToDelete != null && !attachToDelete.isEmpty()) {
				dao.deleteSet(attachToDelete);
				if (log.isDebugEnabled()) log.debug("Removed " + attachToDelete.size() + 
						"sub attachments deleted for updated version " + 
						version.getId() + " by user " + currentUserId);
			}
		} catch (HibernateOptimisticLockingFailureException holfe) {
			if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while attempting to update submission version" + version.getId());
			throw new StaleObjectModificationException("An optimistic locking failure occurred while attempting to update submission version" + version.getId(), holfe);
		} catch (StaleObjectStateException sose) {
			if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while attempting to update submission version" + version.getId());
			throw new StaleObjectModificationException("An optimistic locking failure occurred while attempting to update submission version" + version.getId(), sose);
		}
	}

	public void saveInstructorFeedback(Long versionId, String studentId, Assignment2 assignment, 
			Integer numSubmissionsAllowed, Date resubmitCloseTime, String annotatedText, 
			String feedbackNotes, Date releasedTime, Set<FeedbackAttachment> feedbackAttachSet) {
		
		if (studentId == null || assignment == null) {
			throw new IllegalArgumentException("Null studentId or assignment passed" +
					"to saveInstructorFeedback");
		}
		
		Date currentTime = new Date();
		String currentUserId = externalLogic.getCurrentUserId();
		
		if (!permissionLogic.isUserAbleToProvideFeedbackForStudentForAssignment(studentId, assignment)) {
			throw new SecurityException("User " + currentUserId + 
					" attempted to submit feedback for student " + studentId + " without authorization");
		}
		
		// let's retrieve the student's submission
		AssignmentSubmission submission = getAssignmentSubmissionForStudentAndAssignment(assignment, studentId);
		
		if (submission != null && versionId == null) {
			throw new IllegalArgumentException("Null versionId passed to saveInstructorFeedback " +
					"even though a submission exists for this student");
		}
		
		if (submission == null) {
			submission = new AssignmentSubmission(assignment, studentId);
		}
		
		AssignmentSubmissionVersion version = null;
		boolean newVersion = false;
		
		// let's try to retrieve the version to update
		if (versionId != null) {
			version = dao.getAssignmentSubmissionVersionByIdWithAttachments(versionId);
			if (version != null) {
				// double check that this version matches the student and assignment
				if (!(version.getAssignmentSubmission().getUserId().equals(studentId) &&
						version.getAssignmentSubmission().getAssignment().getId().equals(assignment.getId()))) {
					throw new IllegalArgumentException("The versionId passed is not" +
							" associated with the given student and assignment");
				}
				
			} else {
				throw new IllegalArgumentException("No version exists with the given versionId: " + versionId);
			}
		} else {
			// if null, we should be creating a new version b/c the instructor
			// wants to provide feedback but the student has not made a submission
			version = new AssignmentSubmissionVersion();
			newVersion = true;
		}
		
		submission.setResubmitCloseTime(resubmitCloseTime);
		submission.setNumSubmissionsAllowed(numSubmissionsAllowed);
		
		version.setAssignmentSubmission(submission);
		version.setAnnotatedText(annotatedText);
		version.setFeedbackNotes(feedbackNotes);
		version.setReleasedTime(releasedTime);
		version.setLastFeedbackSubmittedBy(currentUserId);
		version.setLastFeedbackTime(currentTime);
		
		if (newVersion) {
			version.setCreatedBy(currentUserId);
			version.setCreatedTime(currentTime);
			version.setDraft(false);
		}
		
		// identify any attachments that were deleted
		Set<SubmissionAttachmentBase> attachToDelete = identifyAttachmentsToDelete(version.getFeedbackAttachSet(), feedbackAttachSet);
		Set<SubmissionAttachmentBase> attachToCreate = identifyAttachmentsToCreate(feedbackAttachSet);
		
		// make sure the version was populated on the FeedbackAttachments
		populateVersionForAttachmentSet(attachToCreate, version);
		populateVersionForAttachmentSet(attachToDelete, version);

		try {

			Set<AssignmentSubmissionVersion> versionSet = new HashSet<AssignmentSubmissionVersion>();
			versionSet.add(version);

			Set<AssignmentSubmission> submissionSet = new HashSet<AssignmentSubmission>();
			submissionSet.add(submission);
			
			if (attachToCreate == null) {
				attachToCreate = new HashSet<SubmissionAttachmentBase>();
			}

			dao.saveMixedSet(new Set[] {submissionSet, versionSet, attachToCreate});
			if (log.isDebugEnabled()) log.debug("Updated/Added feedback for version " + 
					version.getId() + " for user " + submission.getUserId() + " for assignment " + 
					submission.getAssignment().getTitle()+ " ID: " + submission.getAssignment().getId());
			if (log.isDebugEnabled()) log.debug("Created feedbackAttachments: " + attachToCreate.size());

			if (attachToDelete != null && !attachToDelete.isEmpty()) {
				dao.deleteSet(attachToDelete);
				if (log.isDebugEnabled()) log.debug("Removed feedback attachments deleted for updated version " + version.getId() + " by user " + currentUserId);
			}

		} catch (HibernateOptimisticLockingFailureException holfe) {
			if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while attempting to update submission version" + version.getId());
			throw new StaleObjectModificationException("An optimistic locking failure occurred while attempting to update submission version" + version.getId(), holfe);
		} catch (StaleObjectStateException sose) {
			if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while attempting to update submission version" + version.getId());
			throw new StaleObjectModificationException("An optimistic locking failure occurred while attempting to update submission version" + version.getId(), sose);
		}
	}
	
	public List<AssignmentSubmission> getViewableSubmissionsForAssignmentId(Long assignmentId) {
		if (assignmentId == null) {
			throw new IllegalArgumentException("null assignmentId passed to getViewableSubmissionsForAssignmentId");
		}

		List<AssignmentSubmission> viewableSubmissions = new ArrayList<AssignmentSubmission>();

		Assignment2 assignment = (Assignment2)dao.findById(Assignment2.class, assignmentId);
		if (assignment == null) {
			throw new AssignmentNotFoundException("No assignment found with id: " + assignmentId);
		}

		// get a list of all the students that the current user may view for the given assignment
		List<String> viewableStudents = permissionLogic.getViewableStudentsForUserForItem(assignment);

		if (viewableStudents != null && !viewableStudents.isEmpty()) {
			
			// get the submissions for these students
			Set<AssignmentSubmission> existingSubmissions = dao.getCurrentSubmissionsForStudentsForAssignment(viewableStudents, assignment);

			Map<String, AssignmentSubmission> studentIdSubmissionMap = new HashMap<String, AssignmentSubmission>();
			if (existingSubmissions != null) {
				for (AssignmentSubmission submission : existingSubmissions) {
					if (submission != null) {
						studentIdSubmissionMap.put(submission.getUserId(), submission);
					}
				}
			}

			// now, iterate through the students and create empty AssignmentSubmission recs
			// if no submission exists yet
			for (String studentId : viewableStudents) {
				if (studentId != null) {
					AssignmentSubmission thisSubmission = 
						(AssignmentSubmission)studentIdSubmissionMap.get(studentId);

					if (thisSubmission == null) {
						// no submission exists for this student yet, so just
						// add an empty rec to the returned list
						thisSubmission = new AssignmentSubmission(assignment, studentId);
					} else {
						// we need to filter restricted info from instructor
						// if this is draft
						filterOutRestrictedInfo(thisSubmission, externalLogic.getCurrentUserId(), false);
					}

					viewableSubmissions.add(thisSubmission);
				}
			}
		}

		return viewableSubmissions;
	}
	
	public Map<Assignment2, Integer> getSubmissionStatusConstantForAssignments(List<Assignment2> assignments, String studentId) {
		if (studentId == null) {
			throw new IllegalArgumentException("Null studentId passed to setSubmissionStatusForAssignments");
		}
		
		Map<Assignment2, Integer> assignToStatusMap = new HashMap<Assignment2, Integer>();
		
		if (assignments != null) {
			// retrieve the associated submission recs with current version data populated
			List<AssignmentSubmission> submissions = dao.getCurrentAssignmentSubmissionsForStudent(assignments, studentId);
			Map<Long, AssignmentSubmission> assignmentIdToSubmissionMap = new HashMap<Long, AssignmentSubmission>();
			if (submissions != null) {
				for (AssignmentSubmission submission : submissions) {
					if (submission != null) {
						Assignment2 assign = submission.getAssignment();
						if (assign != null) {
							assignmentIdToSubmissionMap.put(assign.getId(), submission);
						}
					}
				}
			}
			
			for (Assignment2 assign : assignments) {
				if (assign != null) {
					AssignmentSubmission currSubmission = (AssignmentSubmission)assignmentIdToSubmissionMap.get(assign.getId());
					AssignmentSubmissionVersion currVersion = currSubmission != null ? 
							currSubmission.getCurrentSubmissionVersion() : null;
					
					Integer status = getSubmissionStatusConstantForCurrentVersion(currVersion, assign.getDueDate());
					assignToStatusMap.put(assign, status);
				}
			}
		}
		
		return assignToStatusMap;
	}
	
	public Integer getSubmissionStatusConstantForCurrentVersion(AssignmentSubmissionVersion currentVersion,
			Date dueDate) {
		int status = AssignmentConstants.SUBMISSION_NOT_STARTED;
		
		if (currentVersion == null) {
			status = AssignmentConstants.SUBMISSION_NOT_STARTED;
		} else if (currentVersion.getId() == null) {
			status = AssignmentConstants.SUBMISSION_NOT_STARTED;
		} else if (currentVersion.isDraft()) {
			status = AssignmentConstants.SUBMISSION_IN_PROGRESS;
		} else if (currentVersion.getSubmittedTime() != null) {
			if (dueDate != null && dueDate.before(currentVersion.getSubmittedTime())) {
				status = AssignmentConstants.SUBMISSION_LATE;
			} else {
				status = AssignmentConstants.SUBMISSION_SUBMITTED;
			}
		}
		
		return status;
	}
	
	private void populateVersionForAttachmentSet(Set<? extends SubmissionAttachmentBase> attachSet,
			AssignmentSubmissionVersion version)
	{
		if (attachSet != null && !attachSet.isEmpty())
			for (SubmissionAttachmentBase attach : attachSet)
				if (attach != null)
					attach.setSubmissionVersion(version);
	}
	
	private Set<SubmissionAttachmentBase> identifyAttachmentsToDelete(
			Set<? extends SubmissionAttachmentBase> existingAttachSet,
			Set<? extends SubmissionAttachmentBase> updatedAttachSet)
	{
		Set<SubmissionAttachmentBase> attachToRemove = new HashSet<SubmissionAttachmentBase>();

		if (existingAttachSet != null)
			for (SubmissionAttachmentBase attach : existingAttachSet)
				if (attach != null)
					if (updatedAttachSet == null || !updatedAttachSet.contains(attach))
						// we need to delete this attachment
						attachToRemove.add(attach);

		return attachToRemove;
	}
	
	private Set<SubmissionAttachmentBase> identifyAttachmentsToCreate(
			Set<? extends SubmissionAttachmentBase> updatedAttachSet)
	{
		Set<SubmissionAttachmentBase> attachToCreate = new HashSet<SubmissionAttachmentBase>();

		if (updatedAttachSet != null)
			for (SubmissionAttachmentBase attach : updatedAttachSet)
				if (attach != null)
					if (attach.getId() == null)
						attachToCreate.add(attach);

		return attachToCreate;
	}
	
	public boolean submissionIsOpenForStudentForAssignment(String studentId, Long assignmentId) {
		if (studentId == null || assignmentId == null) {
			throw new IllegalArgumentException("null parameter passed to studentAbleToSubmit");
		} 

		Assignment2 assignment = (Assignment2)dao.findById(Assignment2.class, assignmentId);
		if (assignment == null) {
			throw new AssignmentNotFoundException("No assignment exists with id " + assignmentId);
		}
		
		// retrieve the submission history for this student for this assignment
		AssignmentSubmission submission = dao.getSubmissionWithVersionHistoryForStudentAndAssignment(studentId, assignment);
		Set<AssignmentSubmissionVersion> versionHistory = null;
		if (submission != null) {
			versionHistory = dao.getVersionHistoryForSubmission(submission);
		}
		
		// we need to determine if this is the first submission for the student
		int currNumSubmissions = 0;
		if (submission == null) {
			currNumSubmissions = 0;
		} else if (versionHistory == null) {
			currNumSubmissions = 0;
		} else {
			// we need to look at the submission history to determine if there
			// are any submission by the student (not drafts and not versions
			// created by instructor feedback when no submission)
			for (AssignmentSubmissionVersion version : versionHistory) {
				if (version != null) {
					if (version.getSubmittedTime() != null) {
						currNumSubmissions++;
					}
				}
			}
		}
		
		// there are several factors into whether a student may make a submission or not
		// 1) if student has not already made a submission:
		//   a) assignment is open AND if applicable, accept until date has not passed
		// 2) student does have a submission
		//	 a) assignment is open AND if applicable, accept until date has not passed
		//		AND instructor has allowed resubmission until accept until date and
		// 		max num submissions has not been reached
		//   OR
		//   b) instructor has allowed resubmission for this student and the
		//		resubmit until date has not passed and max num submissions has
		//		not been reached
		
		boolean studentAbleToSubmit = false;
		boolean assignmentIsOpen = assignment.getOpenTime().before(new Date()) && 
		(assignment.getAcceptUntilTime() == null ||
					(assignment.getAcceptUntilTime() != null && 
							assignment.getAcceptUntilTime().after(new Date())));
		
		if (currNumSubmissions == 0) {
			if (assignmentIsOpen) {
				studentAbleToSubmit = true;
			} else if(submission != null){
				// in this scenario, the instructor took some action on this student without there
				// being a submission, such as adding feedback or allowing this student to resubmit.
				// thus, there is a submission record, but the student hasn't submitted anything yet
				// in this case, we need to check if the student is allowed to resubmit

				if (submission.getResubmitCloseTime() == null ||
						submission.getResubmitCloseTime().after(new Date())) {
					if (submission.getNumSubmissionsAllowed() != null && 
							(submission.getNumSubmissionsAllowed() == null || 
							submission.getNumSubmissionsAllowed().intValue() > currNumSubmissions)) {
						studentAbleToSubmit = true;
					}
				}
			}
		} else {
			// this is a resubmission, so we need to check for resubmission privileges
			// first, check for resubmission on the assignment level
			if (assignmentIsOpen) { 
				if (assignment.getNumSubmissionsAllowed() != null &&
						(assignment.getNumSubmissionsAllowed().equals(-1) ||
						assignment.getNumSubmissionsAllowed() > currNumSubmissions)) {
					studentAbleToSubmit = true;
				}
			} 
			
			// if they still can't submit, check the student level
			if (!studentAbleToSubmit) {
				if (submission.getResubmitCloseTime() == null || 
						submission.getResubmitCloseTime().after(new Date()))
				{
					if (submission.getNumSubmissionsAllowed() != null && 
							(submission.getNumSubmissionsAllowed().equals(-1) || 
							submission.getNumSubmissionsAllowed().intValue() > currNumSubmissions)) {
						studentAbleToSubmit = true;
					}
				}
			}
		}
		
		return studentAbleToSubmit;
	}

	public boolean isMostRecentVersionDraft(AssignmentSubmission submission) {
		if (submission == null) {
			throw new IllegalArgumentException("null submission passed to currentVersionIsDraft");
		}

		boolean currVersionIsDraft = false;
		
		// if the id is null, there is no current version
		if (submission.getId() != null) {
			AssignmentSubmissionVersion version = dao.getCurrentSubmissionVersionWithAttachments(submission);
	
			if (version != null && version.isDraft()) {
				currVersionIsDraft = true;
			}
		}

		return currVersionIsDraft;
	}
	
	public void releaseAllFeedbackForAssignment(Long assignmentId) {
		if (assignmentId == null) {
			throw new IllegalArgumentException("null assignmentId passed to releaseAllFeedbackForAssignment");
		}

		String contextId = externalLogic.getCurrentContextId();
		if (!gradebookLogic.isCurrentUserAbleToGrade(contextId)) {
			throw new SecurityException("User attempted to release feedback for assignment " + assignmentId + " without authorization");
		}
		
		Assignment2 assignment = (Assignment2) dao.findById(Assignment2.class, assignmentId);
		if (assignment == null) {
			throw new AssignmentNotFoundException("Assignment with id " + assignmentId + " does not exist");
		}
		
		List<String> gradableStudents = permissionLogic.getGradableStudentsForUserForItem(assignment);
		if (gradableStudents != null && !gradableStudents.isEmpty()) {
			Set<AssignmentSubmission> submissionList = dao.getSubmissionsWithVersionHistoryForStudentListAndAssignment(
					gradableStudents, assignment);
			
			Date releasedTime = new Date();
			
			if (submissionList != null && !submissionList.isEmpty()) {
				
				Set<AssignmentSubmissionVersion> versionsToUpdate = new HashSet<AssignmentSubmissionVersion>();
				
				for (AssignmentSubmission submission : submissionList) {
					if (submission != null) {
						if (submission.getSubmissionHistorySet() != null &&
								!submission.getSubmissionHistorySet().isEmpty()) {
							// we need to iterate through all of the versions and
							// release them
							for (AssignmentSubmissionVersion version : submission.getSubmissionHistorySet())
							{
								if (version != null) {
									version.setReleasedTime(releasedTime);
									versionsToUpdate.add(version);
								}
							}
						}
					}
				}
				
				try {
					dao.saveMixedSet(new Set[] { versionsToUpdate });
					if (log.isDebugEnabled()) log.debug("All versions for assignment " + assignmentId + " released by " + externalLogic.getCurrentUserId());
				} catch (HibernateOptimisticLockingFailureException holfe) {
					if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while attempting to update submission versions for assignment " + assignmentId);
		            throw new StaleObjectModificationException("An optimistic locking " +
		            		"failure occurred while attempting to update submission " +
		            		"versions for assignment " + assignmentId, holfe);
				} catch (StaleObjectStateException sose) {
					if(log.isInfoEnabled()) log.info("An optimistic locking failure " +
							"occurred while attempting to update submission versions for assignment " + assignmentId);
					throw new StaleObjectModificationException("An optimistic locking " +
							"failure occurred while attempting to update submission versions for assignment " + assignmentId, sose);
				}
			}
		}
	}
	
	public void releaseAllFeedbackForSubmission(Long submissionId) {
		if (submissionId == null) {
			throw new IllegalArgumentException("null submissionId passed to releaseAllFeedbackForSubmission");
		}

		AssignmentSubmission subWithHistory = dao.getSubmissionWithVersionHistoryById(submissionId);

		if (subWithHistory == null) {
			throw new SubmissionNotFoundException("No submission exists with id " + submissionId);
		}

		if (!permissionLogic.isUserAbleToProvideFeedbackForStudentForAssignment(subWithHistory.getUserId(), subWithHistory.getAssignment())) {
			throw new SecurityException("User " + externalLogic.getCurrentUserId() + " attempted to release feedback" +
					" for student " + subWithHistory.getUserId() + " and assignment " + 
					subWithHistory.getAssignment().getId() + "without authorization");
		}

		if (subWithHistory.getSubmissionHistorySet() != null &&
				!subWithHistory.getSubmissionHistorySet().isEmpty()) {
			// we need to iterate through all of the versions and
			// release them
			Date releasedTime = new Date();
			Set<AssignmentSubmissionVersion> updatedVersions = new HashSet<AssignmentSubmissionVersion>();
			for (AssignmentSubmissionVersion version : subWithHistory.getSubmissionHistorySet()) {
				if (version != null) {
					version.setReleasedTime(releasedTime);
					updatedVersions.add(version);
				}
			}
			
			try {
				dao.saveMixedSet(new Set[] { updatedVersions });
				if (log.isDebugEnabled()) log.debug("All submission versions for submission " + submissionId + " released by " + externalLogic.getCurrentUserId());
			} catch (HibernateOptimisticLockingFailureException holfe) {
				if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while attempting to update release all version for submission " + submissionId);
	            
				throw new StaleObjectModificationException("An optimistic locking " +
						"failure occurred while attempting to update release all version for submission " + submissionId, holfe);
			}
		}
	}
	
	public void releaseFeedbackForVersion(Long submissionVersionId) {
		if (submissionVersionId == null) {
			throw new IllegalArgumentException("Null submissionVersionId passed to releaseFeedbackForVersion");
		}
		
		AssignmentSubmissionVersion version = (AssignmentSubmissionVersion)dao.findById(
				AssignmentSubmissionVersion.class, submissionVersionId);
		if (version == null) {
			throw new VersionNotFoundException("No version " + submissionVersionId + " exists");
		}
		
		AssignmentSubmission submission = version.getAssignmentSubmission();
		
		if (!permissionLogic.isUserAbleToProvideFeedbackForStudentForAssignment(submission.getUserId(), submission.getAssignment())) {
			throw new SecurityException("User " + externalLogic.getCurrentUserId() + " attempted to release feedback" +
					" for student " + submission.getUserId() + " and assignment " + 
					submission.getAssignment().getId() + "without authorization");
		}
		
		version.setReleasedTime(new Date());
		
		try {
			dao.update(version);
			if (log.isDebugEnabled()) log.debug("Version " + version.getId() + " released by " + externalLogic.getCurrentUserId());
		} catch (HibernateOptimisticLockingFailureException holfe) {
			if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while attempting to update submission version" + version.getId());
            throw new StaleObjectModificationException("An optimistic locking failure occurred " +
            		"while attempting to update submission version" + version.getId(), holfe);
		}
	}
	
	/**
	 * when retrieving a submission and/or version, some fields may be restricted
	 * for the curr user. If curr user is the submitter and feedback has not been
	 * released, we do not want to return feedback. If curr user is not the submitter
	 * and the version is draft, we do not want to return the submission text and attach
	 * @param submission - do not pass the persistent object since we do not
	 * want to save the changes we are making
	 * @param currentUserId
	 * @param includeHistory - true if the submissionHistorySet was populated and
	 * needs to be filtered, as well
	 */
	private void filterOutRestrictedInfo(AssignmentSubmission submission, String currentUserId, boolean includeHistory) {
		// if the current user is the submitter and feedback has not been 
		// released, do not return the feedback info
		// if the current user is not the submitter and a version is draft, 
		// do not return any of the submission info

		// check the version history
		if (includeHistory) {
			if (submission.getSubmissionHistorySet() != null && !submission.getSubmissionHistorySet().isEmpty()) {
				for (AssignmentSubmissionVersion version : submission.getSubmissionHistorySet()) {
					filterOutRestrictedVersionInfo(version, currentUserId);
				}
			}
		}
		
		// also check the currentVersion
		if (submission.getCurrentSubmissionVersion() != null) {
			filterOutRestrictedVersionInfo(submission.getCurrentSubmissionVersion(), currentUserId);
		}
	}
	
	/**
	 * when retrieving a submission and/or version, some fields may be restricted
	 * for the curr user. If curr user is the submitter and feedback has not been
	 * released, we do not want to return feedback. If curr user is not the submitter
	 * and the version is draft, we do not want to return the submission text and attach
	 * @param version - do not pass the persistent object since we do not
	 * want to save the changes we are making
	 * @param currentUserId
	 */
	private void filterOutRestrictedVersionInfo(AssignmentSubmissionVersion version, String currentUserId) {
		if (version != null) {
			// if the current user is the submitter
			if (version.getAssignmentSubmission().getUserId().equals(currentUserId)) {
				if (version.getReleasedTime() == null || version.getReleasedTime().after(new Date())) {
					// do not populate the feedback since not released 
					version.setFeedbackAttachSet(new HashSet<FeedbackAttachment>());
					version.setFeedbackNotes("");
					version.setAnnotatedText("");
					if (log.isDebugEnabled()) log.debug("Not populating feedback-specific info b/c curr user is submitter and feedback not released");
				}
			} else {
				// do not populate submission info if still draft
				if (version.isDraft()) {
					version.setSubmittedText("");
					version.setSubmissionAttachSet(new HashSet<SubmissionAttachment>());
					if (log.isDebugEnabled()) log.debug("Not populating submission-specific info b/c draft status and current user is not submitter");
				}
			}
		}
	}
	
	public List<AssignmentSubmissionVersion> getVersionHistoryForSubmission(AssignmentSubmission submission) {
		if (submission == null) {
			throw new IllegalArgumentException("Null submission passed to getVersionHistoryForSubmission");
		}
		
		List<AssignmentSubmissionVersion> filteredVersionHistory = new ArrayList<AssignmentSubmissionVersion>();
		
		if (!permissionLogic.isUserAbleToViewStudentSubmissionForAssignment(submission.getUserId(), submission.getAssignment())) {
			throw new SecurityException("User " + externalLogic.getCurrentUserId() +
					" attempted to access version history for student " + submission.getUserId() +
					" without authorization!");
		}

		// if id is null, this submission does not exist yet - will return empty
		// version history
		if (submission.getId() != null) {
			String currentUserId = externalLogic.getCurrentUserId();

			Set<AssignmentSubmissionVersion> historySet = dao.getVersionHistoryForSubmission(submission);
			if (historySet != null && !historySet.isEmpty()) {
				for (AssignmentSubmissionVersion version : historySet) {
					if (version != null) {
						filterOutRestrictedVersionInfo(version, currentUserId);
						filteredVersionHistory.add(version);
					}

				}}
		} else {
			if (log.isDebugEnabled()) log.debug("Submission does not exist so no version history retrieved");
		}
		
		Collections.sort(filteredVersionHistory, new ComparatorsUtils.VersionCreatedDateComparatorDesc());
		
		return filteredVersionHistory;
	}
	
	private AssignmentSubmission getAssignmentSubmissionForStudentAndAssignment(Assignment2 assignment, String studentId) {
		AssignmentSubmission submission = null;
		
		List<AssignmentSubmission> subList = dao.findByProperties(AssignmentSubmission.class, 
				new String[] {"assignment", "userId"}, new Object[] {assignment, studentId});
		
		if (subList != null && subList.size() > 0) {
			submission = (AssignmentSubmission) subList.get(0);
		}
		
		return submission;
	}

	public FeedbackVersion getFeedbackByUserIdAndSubmittedTime(String userId, Date submittedTime)
	{
		AssignmentSubmissionVersion v = dao.getVersionByUserIdAndSubmittedTime(userId,
				submittedTime);
		return v;
	}

	public void updateFeedbackForVersion(FeedbackVersion feedback)
	{
		if (feedback == null)
			throw new IllegalArgumentException("Feedback cannot be null.");
		if (feedback.getId() == null)
			throw new IllegalArgumentException("Cannot update feedback without ID.  Please create a submission version first.");

		AssignmentSubmissionVersion asv = (AssignmentSubmissionVersion) dao.findById(
				AssignmentSubmissionVersion.class, feedback.getId());

		if (asv == null)
			throw new IllegalArgumentException("Cannot find submission version with id = " + feedback.getId());

		if (feedback.getAnnotatedText() != null)
			asv.setAnnotatedText(feedback.getAnnotatedText());

		if (feedback.getFeedbackAttachSet() != null)
		{
			for (FeedbackAttachment fa : feedback.getFeedbackAttachSet())
				fa.setSubmissionVersion(asv);
			dao.saveSet(feedback.getFeedbackAttachSet());
			asv.setFeedbackAttachSet(feedback.getFeedbackAttachSet());
		}

		if (feedback.getFeedbackNotes() != null)
			asv.setFeedbackNotes(feedback.getFeedbackNotes());

		if (feedback.getLastFeedbackTime() != null)
			asv.setLastFeedbackTime(feedback.getLastFeedbackTime());
		else
			asv.setLastFeedbackTime(new Date());

		if (feedback.getLastFeedbackSubmittedBy() != null)
			asv.setLastFeedbackSubmittedBy(feedback.getLastFeedbackSubmittedBy());
		else
			asv.setLastFeedbackSubmittedBy(externalLogic.getCurrentUserId());

		dao.update(asv);
	}

	public List<AssignmentSubmissionVersion> getLatestSubmissionsForAssignment(Long assignmentId)
	{
		List<AssignmentSubmissionVersion> l = dao.getLatestSubmissionsForAssignment(assignmentId);
		return l;
	}

	public int getNumSubmittedVersions(String studentId, Long assignmentId) {
		if (studentId == null || assignmentId == null) {
			throw new IllegalArgumentException("Null studentId or assignmentId passed " +
			"to getTotalNumSubmissionsForStudentForAssignment");
		}

		return dao.getNumSubmittedVersions(studentId, assignmentId);
	}
}