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

package org.sakaiproject.assignment2.tool.beans.locallogic;

import org.sakaiproject.assignment2.logic.AssignmentPermissionLogic;
import org.sakaiproject.assignment2.logic.ExternalLogic;
import org.sakaiproject.assignment2.logic.AssignmentSubmissionLogic;
import org.sakaiproject.assignment2.tool.producers.*;
import org.sakaiproject.assignment2.tool.producers.dev.*;
import org.sakaiproject.assignment2.tool.producers.fragments.*;
import org.sakaiproject.assignment2.tool.params.SimpleAssignmentViewParams;

import uk.org.ponder.rsf.builtin.UVBProducer;

public class LocalPermissionLogic {
	
	private AssignmentPermissionLogic permissionLogic;
	public void setPermissionLogic(AssignmentPermissionLogic permissionLogic) {
		this.permissionLogic = permissionLogic;
	}
	
	private ExternalLogic externalLogic;
	public void setExternalLogic(ExternalLogic externalLogic) {
		this.externalLogic = externalLogic;
	}
	
	private AssignmentSubmissionLogic submissionLogic;
	public void setSubmissionLogic(AssignmentSubmissionLogic submissionLogic) {
		this.submissionLogic = submissionLogic;
	}
	
	public Boolean checkCurrentUserHasViewPermission(String viewId) {
		String contextId = externalLogic.getCurrentContextId();
		
		if (AddAttachmentHelperProducer.VIEWID.equals(viewId)) {
			return Boolean.TRUE;
			
		} else if (AssignmentDetailProducer.VIEW_ID.equals(viewId)) {
			return Boolean.TRUE;
			
		} else if (AssignmentListSortViewProducer.VIEW_ID.equals(viewId)) {
			return permissionLogic.isUserAbleToAccessInstructorView(contextId);
			
		} else if (AssignmentProducer.VIEW_ID.equals(viewId) || AssignmentDevProducer.VIEW_ID.equals(viewId)) {
			return permissionLogic.isCurrentUserAbleToEditAssignments(contextId);
			
		} else if (FinishedHelperProducer.VIEWID.equals(viewId)) {
			return Boolean.TRUE;
			
		} else if (GradeProducer.VIEW_ID.equals(viewId) || GradeDevProducer.VIEW_ID.equals(viewId)) {
			return permissionLogic.isUserAbleToAccessInstructorView(contextId);
			
		} else if (StudentAssignmentListProducer.VIEW_ID.equals(viewId)) {
			return permissionLogic.isCurrentUserAbleToSubmit(contextId);
			
		} else if (StudentSubmitProducer.VIEW_ID.equals(viewId)) {
			return permissionLogic.isCurrentUserAbleToSubmit(contextId);
			
		} else if (ViewSubmissionsProducer.VIEW_ID.equals(viewId)) {
			return permissionLogic.isUserAbleToAccessInstructorView(contextId);
			
		} else if (AjaxCallbackProducer.VIEW_ID.equals(viewId)) {
			return Boolean.TRUE;
			
		} else if (FragmentAssignment2SelectProducer.VIEW_ID.equals(viewId)) {
			return permissionLogic.isUserAbleToAccessInstructorView(contextId);
			
		} else if (FragmentAssignmentPreviewProducer.VIEW_ID.equals(viewId)) {
			return permissionLogic.isUserAbleToAccessInstructorView(contextId);
			
		} else if (FragmentGradebookDetailsProducer.VIEW_ID.equals(viewId)) {
			return permissionLogic.isUserAbleToAccessInstructorView(contextId);
			
		} else if (FragmentSubmissionGradePreviewProducer.VIEW_ID.equals(viewId)) {
			return permissionLogic.isUserAbleToAccessInstructorView(contextId);
			
		} else if (FragmentSubmissionPreviewProducer.VIEW_ID.equals(viewId)) {
			return permissionLogic.isCurrentUserAbleToSubmit(contextId);
		
		} else if (FragmentViewSubmissionProducer.VIEW_ID.equals(viewId)) {
			return Boolean.TRUE;
		
		} else if (UploadAllProducer.VIEW_ID.equals(viewId)) {
			return permissionLogic.isUserAbleToAccessInstructorView(contextId);
			
		} else if (FragmentAssignmentInstructionsProducer.VIEW_ID.equals(viewId)) {
			return permissionLogic.isUserAbleToAccessInstructorView(contextId);
			
		} else if ("zipSubmissions".equals(viewId)) {
			return permissionLogic.isUserAbleToAccessInstructorView(contextId);
			
		} else if (TaggableHelperProducer.VIEWID.equals(viewId)) {
			return permissionLogic.isUserAbleToAccessInstructorView(contextId);
		}
		
		//Here are some RSF Generic always true viewIds
		
		else if (UVBProducer.VIEW_ID.equals(viewId)) {
			return Boolean.TRUE;
		}
		
		//else just say No
		return Boolean.FALSE;
	}
	
	public String filterViewIdForStudentSubmission(SimpleAssignmentViewParams incoming) {
		String userId = externalLogic.getCurrentUserId();
		if(submissionLogic.submissionIsOpenForStudentForAssignment(userId, incoming.assignmentId)){
			return StudentSubmitProducer.VIEW_ID;
		} else {
			return StudentSubmitSummaryProducer.VIEW_ID;
		}
	}
	
}
