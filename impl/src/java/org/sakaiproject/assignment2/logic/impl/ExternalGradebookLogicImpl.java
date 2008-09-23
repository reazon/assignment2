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

package org.sakaiproject.assignment2.logic.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment2.exception.GradebookItemNotFoundException;
import org.sakaiproject.assignment2.exception.InvalidGradeForAssignmentException;
import org.sakaiproject.assignment2.exception.NoGradebookDataExistsException;
import org.sakaiproject.assignment2.logic.ExternalGradebookLogic;
import org.sakaiproject.assignment2.logic.GradeInformation;
import org.sakaiproject.assignment2.logic.GradebookItem;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.AssignmentSubmission;
import org.sakaiproject.assignment2.model.constants.AssignmentConstants;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.service.gradebook.shared.AssessmentNotFoundException;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.service.gradebook.shared.CommentDefinition;
import org.sakaiproject.service.gradebook.shared.GradeDefinition;
import org.sakaiproject.service.gradebook.shared.GradebookFrameworkService;
import org.sakaiproject.service.gradebook.shared.GradebookNotFoundException;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.service.gradebook.shared.InvalidGradeException;


/**
 * This is the implementation for logic to interact with the Sakai
 * Gradebook tool
 * 
 * @author <a href="mailto:wagnermr@iupui.edu">michelle wagner</a>
 */
public class ExternalGradebookLogicImpl implements ExternalGradebookLogic {

    private static Log log = LogFactory.getLog(ExternalGradebookLogicImpl.class);

    public void init() {
    	if (log.isDebugEnabled()) log.debug("init");
    }
    
    private GradebookService gradebookService;
    public void setGradebookService(GradebookService gradebookService) {
    	this.gradebookService = gradebookService;
    }

    public List<Assignment2> getViewableGradedAssignments(List<Assignment2> gradedAssignments, String contextId) {
    	if (contextId == null) {
    		throw new IllegalArgumentException("contextId is null in getViewableAssignmentsWithGbData");
    	}
    	
    	List<Assignment2> viewableGradedAssignments = new ArrayList<Assignment2>();
    	if (gradedAssignments == null || gradedAssignments.isEmpty()) {
    		return viewableGradedAssignments;
    	}
    	
    	List<Assignment> gbAssignments = gradebookService.getViewableAssignmentsForCurrentUser(contextId);
		
		Map<Long, Assignment> goIdGbAssignmentMap = new HashMap<Long, Assignment>();
		if (gbAssignments != null) {
			for (Assignment gbObject : gbAssignments) {
				if (gbObject != null) {
					goIdGbAssignmentMap.put(gbObject.getId(), gbObject);
				}
			}
		}
		
		boolean userIsStudent = isCurrentUserAStudentInGb(contextId);
		
		// there are 2 situations in which the gradable object associated with the assignment
		// is not included in the list returned from the gradebook:
		// 1) the user does not have permission to view that GO - if the user is a student,
		//		we still want to display the assignment but don't want to display the
		//		associated grade info b/c not released to students yet
		// 2) the GO was deleted from the gb
		
		for (Assignment2 gradedAssignment : gradedAssignments) {
			if (gradedAssignment != null && gradedAssignment.isGraded()) {
				Long goId = gradedAssignment.getGradableObjectId();
				if (goId != null) {
					Assignment gbItem =	(Assignment)goIdGbAssignmentMap.get(goId);
					if (gbItem != null) {
						viewableGradedAssignments.add(gradedAssignment);
					} else {
						// check to see if this gradable object exists anymore
						if (!gradebookService.isGradableObjectDefined(goId)) {
							viewableGradedAssignments.add(gradedAssignment);
						} else {
							// if it exists, then this user does not have perm to view it in the gb
							if (userIsStudent) {
								// if a student, we still need to return an assignment with gb item info
								// this just means the grade info has not been released yet - we still
								// let students view the assignment
								viewableGradedAssignments.add(gradedAssignment);
							}
						}
					}
				} else {
					// there is no gradableObjectId set for this assignment!
					viewableGradedAssignments.add(gradedAssignment);
				}
			}
		}
		
		return viewableGradedAssignments;
    }

    public void createGradebookDataIfNecessary(String contextId) {
    	// we need to check if there is gradebook data defined for this site. if not,
        // create it (but will not actually add the tool, just the backend)
    	
    	GradebookFrameworkService frameworkService = (org.sakaiproject.service.gradebook.shared.GradebookFrameworkService) 
        ComponentManager.get("org.sakaiproject.service.gradebook.GradebookFrameworkService");
        if (!frameworkService.isGradebookDefined(contextId)) {
        	if (log.isInfoEnabled()) 
        		log.info("Gradebook data being added to context " + contextId + " by Assignment2 tool");
        	frameworkService.addGradebook(contextId, contextId);
        }
    }

    public Map<Long, String> getViewableGradableObjectIdTitleMap(String contextId) {
    	if (contextId == null) {
    		throw new IllegalArgumentException("Null contextId passed to getViewableGradableObjectIdTitleMap");
    	}
    	
    	List<Assignment> viewableGbItems = gradebookService.getViewableAssignmentsForCurrentUser(contextId);
    	
    	Map<Long, String> idTitleMap = new HashMap<Long, String>();
    	if (viewableGbItems == null || viewableGbItems.isEmpty()) {
    		return idTitleMap;
    	}
    	
    	for (Assignment assign : viewableGbItems) {
    		if (assign != null) {
    			idTitleMap.put(assign.getId(), assign.getName());
    		}
    	}
    	
    	return idTitleMap;
    }

    public List<GradebookItem> getAllGradebookItems(String contextId) {
    	if (contextId == null) {
    		throw new IllegalArgumentException("null contextId passed to getAllGradebookItems");
    	}

    	List<GradebookItem> gradebookItems = new ArrayList<GradebookItem>();

    	try {
    		List<Assignment> allGbItems = gradebookService.getAssignments(contextId);

    		if (allGbItems != null) {

    			for (Assignment assign : allGbItems) {
    				if (assign != null) {
    					GradebookItem item = 
    						new GradebookItem(assign.getId(), assign.getName(), assign.getPoints(), assign.getDueDate());
    					
    					if (assign.isExternallyMaintained()) {
    						item.setExternalId(assign.getExternalId());
    					}
    					
    					gradebookItems.add(item);
    				}
    			}
    		}
    	} catch (SecurityException se) {
    		throw new SecurityException("User without edit or grade perm attempted to access the list of all gb items.", se);
    	} catch (GradebookNotFoundException gnfe) {
    		throw new NoGradebookDataExistsException("No gradebook exists for the given contextId: " + contextId, gnfe);
    	}

    	return gradebookItems;
    }

    public Map<String, String> getViewableGroupIdToTitleMap(String contextId) {
    	if (contextId == null) {
    		throw new IllegalArgumentException("Null contextId passed to getViewableGroupIdToTitleMap");
    	}
    	
    	return gradebookService.getViewableSectionUuidToNameMap(contextId);
    }

    public Map<String, String> getViewableStudentsForGradedItemMap(String contextId, Long gradableObjectId) {
    	if (contextId == null) {
    		throw new IllegalArgumentException("Null contextId passed to getViewableGroupIdToTitleMap");
    	}
    	
    	Map<String, String> studentIdAssnFunctionMap = new HashMap<String, String>();

    	try {
    		Map<String, String> studentIdGbFunctionMap = gradebookService.getViewableStudentsForItemForCurrentUser(contextId, gradableObjectId);


    		if (studentIdGbFunctionMap != null) {
    			for (Map.Entry<String, String> entry : studentIdGbFunctionMap.entrySet()) {
    				String studentId = entry.getKey();
    				String function = entry.getValue();
    				if (studentId != null && function != null) {
    					if (function != null) {
    						if (function.equals(GradebookService.gradePermission)) {
    							studentIdAssnFunctionMap.put(studentId, AssignmentConstants.GRADE);
    						} else {
    							studentIdAssnFunctionMap.put(studentId, AssignmentConstants.VIEW);
    						}
    					}
    				}
    			}
    		}
    	} catch (SecurityException se) {
    		throw new SecurityException("User is not authorized to retrieve student " +
    				"list for gb item in context: " + contextId, se);
    	}
    	
    	return studentIdAssnFunctionMap;
    }
    
    public boolean isCurrentUserAbleToEdit(String contextId) {
    	return gradebookService.currentUserHasEditPerm(contextId);
    }
	
	public boolean isCurrentUserAbleToGradeAll(String contextId) {
    	return gradebookService.currentUserHasGradeAllPerm(contextId);
	}
	
	public boolean isCurrentUserAbleToGrade(String contextId) {
    	return gradebookService.currentUserHasGradingPerm(contextId);
	}
	
	public boolean isCurrentUserAbleToViewOwnGrades(String contextId) {
    	return gradebookService.currentUserHasViewOwnGradesPerm(contextId);
	}
	
	public boolean isCurrentUserAStudentInGb(String contextId) {
		boolean userIsAStudentInGb = false;
		
		if (isCurrentUserAbleToViewOwnGrades(contextId) &&
				!isCurrentUserAbleToGrade(contextId) &&
				!isCurrentUserAbleToEdit(contextId)) {
			userIsAStudentInGb = true;
		}
		
		return userIsAStudentInGb;
	}
    
    public String getGradeViewPermissionForCurrentUserForStudentForItem(String contextId, String studentId, Long gbItemId) {
    	if (contextId == null || studentId == null || gbItemId == null) {
    		throw new IllegalArgumentException("Null contextId or studentId or itemId passed to getGradeViewPermissionForCurrentUserForStudentForItem");
    	}
    	
    	String viewOrGrade = null;
    	
    	String function =
    		gradebookService.getGradeViewFunctionForUserForStudentForItem(contextId, gbItemId, studentId);
    	
    	if (function == null) {
    		viewOrGrade = null;
    	} else if (function.equals(GradebookService.gradePermission)) {
    		viewOrGrade = AssignmentConstants.GRADE;
    	} else if (function.equals(GradebookService.viewPermission)) {
    		viewOrGrade = AssignmentConstants.VIEW;
    	}
    	
    	return viewOrGrade;
    }
    
    public String getStudentGradeForItem(String contextId, String studentId, Long gbItemId) {
    	if (contextId == null || studentId == null || gbItemId == null) {
    		throw new IllegalArgumentException("Null contextId or studentId or gbItemId passed to getStudentGradeForItem");
    	}
    	
    	String grade = null;
    	
    	try {
    		GradeDefinition gradeDef = gradebookService.getGradeDefinitionForStudentForItem(contextId, gbItemId, studentId);
    		if (gradeDef != null) {
    			grade = gradeDef.getGrade();
    		}
    	} catch (AssessmentNotFoundException anfe) {
    		// this gradebook item no longer exists, so return a null grade
    		grade = null;
    	} catch (GradebookNotFoundException gnfe) {
    		throw new NoGradebookDataExistsException("No gradebook exists for the given contextId: " + contextId, gnfe);
    	} catch (SecurityException se) {
    		throw new SecurityException("User attempted to access the grade for student : " + 
    				studentId + " for gbItemId: " + gbItemId + " without authorization in gb", se);
    	}
    	
    	return grade;
    }
    
    public String getStudentGradeCommentForItem(String contextId, String studentId, Long gbItemId) {
    	if (contextId == null || studentId == null || gbItemId == null) {
    		throw new IllegalArgumentException("Null contextId or studentId or gbItemId passed to getStudentGradeCommentForItem");
    	}
    	
    	String comment = null;
    	
    	try {
    		CommentDefinition commentDef = gradebookService.getAssignmentScoreComment(contextId, gbItemId, studentId);
    		if (commentDef != null) {
    			comment = commentDef.getCommentText();
    		}
    	} catch (AssessmentNotFoundException anfe) {
    		// this gradebook item no longer exists, so return a null comment
    		comment = null;
    	} catch (GradebookNotFoundException gnfe) {
    		throw new NoGradebookDataExistsException(
					"No gradebook exists for the given contextId: " + contextId, gnfe);
    	}
    	
    	return comment;
    }
    
    public boolean isCurrentUserAbleToGradeStudentForItem(String contextId, String studentId, Long gradableObjectId) {
    	if (contextId == null || studentId == null || gradableObjectId == null) {
    		throw new IllegalArgumentException("null contextId, studentId or gradableObjectId passed to isCurrentUserAbleToGradeStudentForItem");
    	}
    	
    	return gradebookService.isUserAbleToGradeItemForStudent(contextId, gradableObjectId, studentId);
    }
    
    public GradeInformation getGradeInformationForSubmission(String contextId, AssignmentSubmission submission) {
    	if (contextId == null || submission == null) {
    		throw new IllegalArgumentException("null contextId or submission passed to populateAllGradeInfoForSubmission");
    	}
    	
    	Assignment2 assignment = submission.getAssignment();
    	if (assignment == null) {
    		throw new IllegalArgumentException("Null assignment associated with submission passed to getGradeInfoForSubmission");
    	}
    	
    	GradeInformation gradeInfo = new GradeInformation();
    	gradeInfo.setStudentId(submission.getUserId());
    	
    	if (assignment.isGraded() && assignment.getGradableObjectId() != null) {
    		try {
				GradeDefinition gradeDef = gradebookService.getGradeDefinitionForStudentForItem(contextId, assignment.getGradableObjectId(), submission.getUserId());
				
				if (gradeDef != null) {
					gradeInfo.setGradebookGrade(gradeDef.getGrade());
					gradeInfo.setGradebookGradeReleased(gradeDef.isGradeReleased());
					gradeInfo.setGradebookComment(gradeDef.getGradeComment());
				}
				
			} catch (AssessmentNotFoundException anfe) {
				// this gb item no longer exists, so there is no information to populate
				if (log.isDebugEnabled()) log.debug("gb item with id " + assignment.getGradableObjectId() + " no longer exists, so returning null grade info");
			} catch (SecurityException se) {
				throw new SecurityException("User does not have authorization to access " +
						"grade information for " + submission.getUserId() + " for gb item " + 
						assignment.getGradableObjectId(), se);
			}
    	}
    	
    	return gradeInfo;
    }
	
	public Long createGbItemInGradebook(String contextId, String title, Double pointsPossible, Date dueDate,
			boolean releasedToStudents, boolean countedInCourseGrade) {
		if (contextId == null || title == null) {
			throw new IllegalArgumentException("Null contextId or title passed to createGbItemInGradebook");
		}
		
		if (countedInCourseGrade && !releasedToStudents) {
			throw new IllegalArgumentException("You may not count an item in course" +
					" grade without releasing to students.");
		}
		
		Long gradableObjectId = null;
		Assignment newItem = new Assignment();
		newItem.setCounted(countedInCourseGrade);
		newItem.setDueDate(dueDate);
		newItem.setName(title);
		newItem.setPoints(pointsPossible);
		newItem.setReleased(releasedToStudents);
		
		gradebookService.addAssignment(contextId, newItem);
		if (log.isDebugEnabled()) log.debug("New gradebook item added to gb via assignment2 tool");
		
		// now let's retrieve the id of this newly created item
		Assignment newlyCreatedAssign = gradebookService.getAssignment(contextId, title);
		if (newlyCreatedAssign != null) {
			gradableObjectId = newlyCreatedAssign.getId();
		}
		
		return gradableObjectId;
	}
	
	public GradebookItem getGradebookItemById(String contextId, Long gradableObjectId) {
		if (contextId == null || gradableObjectId == null) {
			throw new IllegalArgumentException ("Null contextId or gradableObjectId passed to getGradebookItemById");
		}
		
		GradebookItem gradebookItem = new GradebookItem();
		
		try {
			Assignment assign = gradebookService.getAssignment(contextId, gradableObjectId);
			gradebookItem = new GradebookItem(assign.getId(), assign.getName(), assign.getPoints(), assign.getDueDate());
		} catch (AssessmentNotFoundException anfe) {
			throw new GradebookItemNotFoundException ("No gradebook item exists with gradableObjectId " 
					+ gradableObjectId + " in context " + contextId, anfe);
		}
		
		return gradebookItem;
	}
	
	public void saveGradeAndCommentForStudent(String contextId, Long gradableObjectId, String studentId, String grade, String comment) {
		if (contextId == null || gradableObjectId == null || studentId == null) {
			throw new IllegalArgumentException("Null contextId or gradableObjectId " +
					"or studentId passed to saveGradeAndCommentForStudent");
		}
		
		try {
			gradebookService.saveGradeAndCommentForStudent(contextId, gradableObjectId, studentId, grade, comment);
			if(log.isDebugEnabled()) log.debug("Grade and comment for student " + studentId + 
					" for gbItem " + gradableObjectId + "updated successfully");
		} catch (GradebookNotFoundException gnfe) {
			throw new NoGradebookDataExistsException("No gradebook exists in the given context "
					+ contextId, gnfe);
		} catch (AssessmentNotFoundException anfe) {
			throw new GradebookItemNotFoundException("No gradebook item exists with the given id "
					+ gradableObjectId, anfe);
		} catch (InvalidGradeException ige) {
			throw new InvalidGradeForAssignmentException("The grade: " + grade + 
					" for gradebook " + contextId + " is invalid.", ige);
		} catch (SecurityException se) {
			throw new SecurityException(
					"The current user attempted to saveGradeAndCommentForStudent "
							+ "without authorization. Error: " + se.getMessage(), se);
		}
	}

	public Map<String, GradeInformation> getGradeInformationForStudents(String contextId, List<String> studentIdList, Assignment2 assignment) {
		if (contextId == null || assignment == null) {
			throw new IllegalArgumentException("null contextId or assignment passed to populateStudentGradeInformation");
		}

		// throw an error if this is called for an ungraded assignment
		if (!assignment.isGraded() || assignment.getGradableObjectId() == null) {
			throw new IllegalArgumentException("Ungraded assignment was passed to " +
			"getGradeInformationForStudents. This method may only be used for graded assignments.");
		}

		Map<String, GradeInformation> studentIdToGradeInfoMap = new HashMap<String, GradeInformation>();

		if (studentIdList != null && !studentIdList.isEmpty()) {
			// let's retrieve a map of the students and their grades
			Map<String, GradeDefinition> studentIdToGradeDefMap = new HashMap<String, GradeDefinition>();

			try {
				List<GradeDefinition>gradeDefs = gradebookService.getGradesForStudentsForItem(contextId, 
						assignment.getGradableObjectId(), studentIdList);

				if (gradeDefs != null) {
					for (GradeDefinition gradeDef : gradeDefs) {
						studentIdToGradeDefMap.put(gradeDef.getStudentUid(), gradeDef);
					}
				}
			} catch (SecurityException se) {
				throw new SecurityException("User attempted to access a list of student grades" +
						" that he/she did not have authorization for in the gb.", se);
			}

			for (String studentId : studentIdList) {

				GradeInformation gradeInfo = new GradeInformation();
				gradeInfo.setStudentId(studentId);
				gradeInfo.setGradableObjectId(assignment.getGradableObjectId());

				// get the GradeDefinition for this student and convert it to
				// our local GradeInformation object
				GradeDefinition gradeDef = studentIdToGradeDefMap.get(studentId);
				if (gradeDef != null) {
					gradeInfo.setGradebookComment(gradeDef.getGradeComment());
					gradeInfo.setGradebookGrade(gradeDef.getGrade());
					gradeInfo.setGradebookGradeReleased(gradeDef.isGradeReleased());
				} 

				studentIdToGradeInfoMap.put(studentId, gradeInfo);
			}
		}

		return studentIdToGradeInfoMap;
	}
	
	public void saveGradesAndComments(String contextId, Long gradableObjectId, 
			List<GradeInformation> gradeInfoList) {
		if (contextId == null || gradableObjectId == null) {
			throw new IllegalArgumentException("Null contextId or gradableObjectId " +
					"passed to saveGradesAndCommentsForSubmissions");
		}
		
		List<GradeDefinition> gradeDefList = new ArrayList<GradeDefinition>();
		
		if (gradeInfoList != null) {
			for (GradeInformation gradeInfo : gradeInfoList) {
				if (gradeInfo != null) {
					// double check that we weren't passed submissions for 
					// different assignments - we don't want to update the wrong grades!
					Long assignGo = gradeInfo.getGradableObjectId();
					if (assignGo == null ||	!assignGo.equals(gradableObjectId)) {
						throw new IllegalArgumentException("The given submission's gradableObjectId " + assignGo +
							" does not match the goId passed to saveGradesAndCommentsForSubmissions: " + gradableObjectId);
					}
					
					// convert the info into a GradeDefinition
					GradeDefinition gradeDef = new GradeDefinition();
					gradeDef.setGrade(gradeInfo.getGradebookGrade());
					gradeDef.setGradeComment(gradeInfo.getGradebookComment());
					gradeDef.setStudentUid(gradeInfo.getStudentId());
					
					gradeDefList.add(gradeDef);
				} 
			}
			
			// now try to save the new information
			try {
				gradebookService.saveGradesAndComments(contextId, gradableObjectId, gradeDefList);
				if(log.isDebugEnabled()) log.debug("Grades and comments for contextId " + contextId + 
						" for gbItem " + gradableObjectId + "updated successfully for " + gradeDefList.size() + " students");
			} catch (GradebookNotFoundException gnfe) {
				throw new NoGradebookDataExistsException(
						"No gradebook exists in the given context " + contextId, gnfe);
			} catch (AssessmentNotFoundException anfe) {
				throw new GradebookItemNotFoundException(
						"No gradebook item exists with the given id " + gradableObjectId, anfe);
			} catch (InvalidGradeException ige) {
				throw new InvalidGradeForAssignmentException("Attempted to save an invalid grade in gradebook " 
						+ contextId + " via saveGradesAndCommentsForSubmissions. " +
								"No grade information was updated.", ige);
			} catch (SecurityException se) {
				throw new SecurityException(
						"The current user attempted to saveGradeAndCommentForStudent "
								+ "without authorization. Error: " + se.getMessage(), se);
			}
		}
	}

	public boolean gradebookDataExistsInSite(String contextId) {
		if (contextId == null) {
			throw new IllegalArgumentException("Null contextId passed to gradebookDataExistsInSite");
		}
		
		return gradebookService.isGradebookDefined(contextId);
	}
	
	public boolean isGradeValid(String contextId, String grade) {
		if (contextId == null) {
			throw new IllegalArgumentException("Null contextId passed to isGradeValid");
		}
		
		boolean valid = false;
		
		try {
			valid = gradebookService.isGradeValid(contextId, grade);
		} catch (GradebookNotFoundException gnfe) {
			throw new NoGradebookDataExistsException("No gradebook exists in the given context: "
					+ contextId, gnfe);
		}
		
		return valid;
	}
	
	public List<String> identifyStudentsWithInvalidGrades(String contextId, Map<String, String> studentIdToGradeMap) {
		if (contextId == null) {
			throw new IllegalArgumentException("Null contextId passed to getStudentsWithInvalidGrades");
		}
		
		List<String> studentsWithInvalidGrades = new ArrayList<String>();
		if (studentIdToGradeMap != null) {
			try {
				studentsWithInvalidGrades = gradebookService.identifyStudentsWithInvalidGrades(contextId, studentIdToGradeMap);
			} catch (GradebookNotFoundException gnfe) {
				throw new NoGradebookDataExistsException(
						"No gradebook exists in the given context: " + contextId, gnfe);
			}
		}
		
		return studentsWithInvalidGrades;
	}
	
	public boolean isCurrentUserAbleToViewGradebookItem(String contextId, Long gradableObjectId) {
		if (contextId == null || gradableObjectId == null) {
			throw new IllegalArgumentException ("Null parameter passed to " +
					"sCurrentUserAbleToViewGradebookItem - contextId: " + contextId + 
					", gradableObjectId: " + gradableObjectId);
		}
		
		boolean allowed = false;
		
		if (isCurrentUserAbleToGradeAll(contextId)) {
			allowed = true;
    	} else if (isCurrentUserAbleToGrade(contextId) || isCurrentUserAStudentInGb(contextId)) {
    		// check to see if this assignment is among the viewable assign for this user
    		Map<Long, String> gbItemIdToTitleMap = getViewableGradableObjectIdTitleMap(contextId);
    		if (gbItemIdToTitleMap.containsKey(gradableObjectId)) {
    			allowed = true;
    		}
    	} 
		
		return allowed;
	}
}