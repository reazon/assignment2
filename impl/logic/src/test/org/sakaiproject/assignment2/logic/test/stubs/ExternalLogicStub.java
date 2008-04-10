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

package org.sakaiproject.assignment2.logic.test.stubs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.sakaiproject.assignment2.logic.ExternalLogic;
import org.sakaiproject.assignment2.test.AssignmentTestDataLoad;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.ParticipationRecord;
import org.sakaiproject.tool.gradebook.facades.Authn;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.user.api.User;



/**
 * This is a stub class for testing purposes, it will allow us to test all the classes
 * that depend on it since it has way to many external dependencies to make it worth
 * it to mock them all up<br/>
 *
 * Methods only used by the ui are not implemented
 */
public class ExternalLogicStub implements ExternalLogic {

	private Authn authn;
	private SectionAwareness sectionAwareness;
	
	public void setAuthn(Authn authn) {
		this.authn = authn;
	}
	public void setSectionAwareness(SectionAwareness sectionAwareness) {
		this.sectionAwareness = sectionAwareness;
	}
	/**
     * @return the current sakai user id (not username)
     */
    public String getCurrentUserId() {
    	return authn.getUserUid();
    }

    /**
     * Get the display name for a user by their unique id
     * 
     * @param userId
     *            the current sakai user id (not username)
     * @return display name (probably firstname lastname) or "----------" (10 hyphens) if none found
     */
    public String getUserDisplayName(String userId) {
    	return "Artichoke, Arty";
    }

    /**
     * @return the current location id of the current user
     */
    public String getCurrentLocationId() {
    	return AssignmentTestDataLoad.CONTEXT_ID;
    }
    
    /**
     * 
     * @return the current context for the current user
     */
    public String getCurrentContextId() {
    	return AssignmentTestDataLoad.CONTEXT_ID;
    }

    /**
     * Check if this user has super admin access
     * 
     * @param userId
     *            the internal user id (not username)
     * @return true if the user has admin access, false otherwise
     */
    public boolean isUserAdmin(String userId) {
    	return false; 
    }

    /**
     * Cleans up the users submitted strings to protect us from XSS
     * 
     * @param userSubmittedString any string from the user which could be dangerous
     * @return a cleaned up string which is now safe
     */
    public String cleanupUserStrings(String userSubmittedString) {
    	return null; // used for ui
    }
    
    /**
     * Returns URL to viewId pass in
     * @param viewId of view to build path to
     * @return a url path to the vie
     */
    public String getAssignmentViewUrl(String viewId) {
    	return null; // used for ui
    }

    /**
     * Return a Collection of all Groups
     * @return a collection
     */
    public Collection<Group> getSiteGroups(String contextId) {
    	return null; // used for ui
    }
    
    /**
     * 
     * @return a collection of the groups that the given user is a member of
     */
    public Collection<Group> getUserMemberships(String userId, String contextId) {
    	return null; // used for ui
    }
    
    /**
     * 
     * @return list of the group ids of the groups that the given user is
     * a member of
     */
    public List<String> getUserMembershipGroupIdList(String userId, String contextId) {
    	List<String> groupIdList = new ArrayList<String>();
    	List<CourseSection> sectionList = sectionAwareness.getSections(AssignmentTestDataLoad.CONTEXT_ID);
    	if (sectionList != null && !sectionList.isEmpty()) {
    		for (CourseSection section : sectionList) {
    			if (section != null) {
    				if (sectionAwareness.isSectionMemberInRole(section.getUuid(), userId, Role.STUDENT) || 
    						sectionAwareness.isSectionMemberInRole(section.getUuid(), userId, Role.TA)) {
    					groupIdList.add(section.getUuid());
    				}
    			}
    		}
    		
    	}
    	
    	return groupIdList;
    }
    
    /**
     * 
     * @return a map of group id to group name for all of the sections/groups
     * associated with the current site
     */
    public Map<String, String> getGroupIdToNameMapForSite(String contextId) {
    	return null; //used for ui
    }
    
    /**
     * @param contextId
     * @param toolId
     * @return true if tool with the given toolId exists in the site with the given siteId
     */
    public boolean siteHasTool(String contextId, String toolId) {
    	return false; //used for ui
    }

    /**
     * 
     * @param contentReference
     * @return String of path for <img> tag for resource image type icon
     */
    public String getContentTypeImagePath(ContentResource contentReference) {
    	return null; //used for ui
    }
    
    /**
     * 
     * @param contextId
     * @return a list of the student Ids of all of the students in the given site
     */
    public List<String> getStudentsInSite(String contextId) {
    	if (contextId == null) {
    		throw new IllegalArgumentException("Null contextId passed to getStudentsInSite");
    	}
    	List<String> studentsInSite = new ArrayList<String>();
    	
    	List<ParticipationRecord> participants = sectionAwareness.getSiteMembersInRole(contextId, Role.STUDENT);
    	if (participants != null) {
    		for (ParticipationRecord part : participants) {
    			if (part != null) {
    				String studentId = part.getUser().getUserUid();
    				studentsInSite.add(studentId);
    			}
    		}
    	}
    	
    	return studentsInSite;
    }
    
    /**
     * 
     * @param sectionId
     * @return a list of the student ids of students in the given section  
     */
    public List<String> getStudentsInSection(String sectionId) {
    	if (sectionId == null) {
    		throw new IllegalArgumentException("null sectionId passed to getStudentsInSection");
    		
    	}
    	
    	List<String> studentsInSection = new ArrayList<String>();
    	
    	List<ParticipationRecord> participants = sectionAwareness.getSectionMembersInRole(sectionId, Role.STUDENT);
    	for (ParticipationRecord part : participants) {
			if (part != null) {
				String studentId = part.getUser().getUserUid();
				studentsInSection.add(studentId);
			}
		}
    	
    	return studentsInSection;
    }
    
    /**
     * 
     * @param gradeableObjectId
     * @param returnViewId
     * @return url to helper
     */
    public String getUrlForGradebookItemHelper(Long gradeableObjectId, String returnViewId) {
    	return null; //used for ui
    }
    
    /**
     * 
     * @param gradeableObjectId
     * @param userId
     * @param returnViewId
     * @return url to helper
     */
    public String getUrlForGradeGradebookItemHelper(Long gradeableObjectId, String userId, String returnViewId) {
    	return null; //used for ui
    }
    
	public String getUserFullName(String userId) {
		return getUserDisplayName(userId);
	}
	
	public String getToolTitle() {
		return "Assignment2";
	}
	public User getUser(String userId)
	{
		return null;
	}

}
