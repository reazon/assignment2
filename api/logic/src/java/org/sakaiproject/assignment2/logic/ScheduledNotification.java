/**********************************************************************************
 * $URL: 
 * $Id: 
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
package org.sakaiproject.assignment2.logic;

import org.sakaiproject.api.app.scheduler.ScheduledInvocationCommand;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.AssignmentSubmission;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.user.api.UserNotDefinedException;

public interface ScheduledNotification extends ScheduledInvocationCommand
{

	void notifyStudentThatSubmissionWasAccepted(AssignmentSubmission submission)
			throws IdUnusedException, UserNotDefinedException, PermissionException,
			TypeException;

	void notifyInstructorsOfSubmission(AssignmentSubmission submission,
			Assignment2 assignment) throws IdUnusedException, UserNotDefinedException,
			PermissionException, IdUnusedException, TypeException;

}
