/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/assignment2/trunk/api/logic/src/java/org/sakaiproject/assignment2/logic/ExternalGradebookLogic.java $
 * $Id: ExternalGradebookLogic.java 12544 2006-05-03 15:06:26Z wagnermr@iupui.edu $
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

import java.util.List;
import java.util.Map;

import org.sakaiproject.assignment2.model.Assignment2;

/**
 * This is the interface for logic which is related to the integration
 * with the Gradebook tool
 * 
 * @author <a href="mailto:wagnermr@iupui.edu">michelle wagner</a>
 */
public interface ExternalGradebookLogic {

    /**
     * Given a list of graded Assignment2 objects, filter out the objects
     * that the user is not authorized to view according to the gradebook
     * permissions and return a list of graded assignments that the user is
     * authorized to view. Also populates the gb-specific fields for each
     * Assignment2 object
     * @param gradedAssignments
     * @param contextId
     * @return
     */
	public List<Assignment2> getViewableAssignmentsWithGbData(List<Assignment2> gradedAssignments, String contextId);
	
	/**
	 * The Assignment2 tool stores all grading information in the gradebook. Thus,
	 * the gradebook backend must exist for the Assignment2 tool to work. This
	 * method will check to see if the gradebook data exists and, if not, will
	 * add it
	 * @param contextId
	 */
	public void createGradebookDataIfNecessary(String contextId);
	
	/**
	 * 
	 * @param contextId
	 * @return a map of gradable object id to title for all of the gradebook
	 * items that the current user may view or grade.
	 */
	public Map getViewableGradableObjectIdTitleMap(String contextId);
	
	/**
	 * returns a list of GradebookItem objects that represent all of
	 * the gradebook items currently defined in the gradebook tool
	 * @param contextId
	 * @return
	 */
	public List<GradebookItem> getAllGradebookItems(String contextId);
	
	/**
	 * returns a map of the group id to name for
	 * all of the groups/sections that the current user is authorized to view
	 * according to the gradebook grader permissions
	 * @param contextId
	 * @return
	 */
	public Map<String, String> getViewableGroupIdToTitleMap(String contextId);
	
	/**
	 * Using the grader permissions, returns a map of all of the student ids of 
	 * the students that the current user is allowed to view or grade
	 * to the actual function (grade/view)
	 * @param contextId
	 * @param gradableObjectId
	 * @return
	 */
	public Map<String, String> getViewableStudentsForGradedItemMap(String contextId, Long gradableObjectId);
	
	/**
	 * 
	 * @param assignment
	 * @return a list of studentIds that the current user is allowed to view for
	 * the given ungraded assignment. If the user may grade all, all students
	 * are returned.  If user may grade section, only students in his/her section(s)
	 * will be returned.  Otherwise, the user is not authorized to view any
	 * students.
	 */
	//public List getViewableStudentsForUngradedAssignment(Assignment2 assignment);
	
	/**
	 * @param contextId
	 * @return true if the current user is authorized to edit the gradebook
	 */
	public boolean isCurrentUserAbleToEdit(String contextId);
	
	/**
	 * @param contextId
	 * @return true if the current user is authorized to grade all in gradebook
	 */
	public boolean isCurrentUserAbleToGradeAll(String contextId);
	
	/**
	 * @param contextId
	 * @return true if the current user is authorized to grade in some 
	 * capacity in the gradebook.  (ie they may grade all or grade
	 * section)
	 */
	public boolean isCurrentUserAbleToGrade(String contextId);
	
	/**
	 * @param contextId
	 * @return true if current user is authorized to view their own
	 * grades in the gradebook
	 */
	public boolean isCurrentUserAbleToViewOwnGrades(String contextId);
	
	/**
	 * 
	 * @param contextId
	 * @return true if the current user does not have grading or editing 
	 * privileges in the gradebook but does have the view own grades perm
	 */
	public boolean isCurrentUserAStudentInGb(String contextId);
	
	public String getGradebookItemHelperUrl(String contextId);
}
