/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/assignment2/trunk/api/logic/src/java/org/sakaiproject/assignment2/logic/ExternalGradebookLogicImpl.java $
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

package org.sakaiproject.assignment2.logic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment2.logic.ExternalAnnouncementLogic;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.event.cover.NotificationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.util.FormattedText;
import org.sakaiproject.announcement.api.AnnouncementChannel;
import org.sakaiproject.announcement.api.AnnouncementMessage;
import org.sakaiproject.announcement.api.AnnouncementMessageEdit;
import org.sakaiproject.announcement.api.AnnouncementMessageHeaderEdit;
import org.sakaiproject.announcement.api.AnnouncementService;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.AssignmentGroup;
import org.sakaiproject.assignment2.exception.AnnouncementPermissionException;

/**
 * This is the implementation for logic to interact with the Sakai
 * Announcements tool
 * 
 * @author <a href="mailto:wagnermr@iupui.edu">michelle wagner</a>
 */
public class ExternalAnnouncementLogicImpl implements ExternalAnnouncementLogic {

    private static Log log = LogFactory.getLog(ExternalAnnouncementLogic.class);
    
    private AnnouncementService aService;
    private AnnouncementChannel announcementChannel;

    public void init() {
    	log.debug("init");
    }
    
    public String addOpenDateAnnouncement(Assignment2 assignment, String contextId,
    		String announcementSubject, String announcementBody) throws AnnouncementPermissionException {
    	if (assignment == null || contextId == null) {
    		throw new IllegalArgumentException("null assignment or contextId passed to addOpenDateAnnouncement");
    	}
    	
    	initializeAnnouncementServiceData(contextId);
    	if (announcementChannel == null) {
    		log.warn("announcementChannel was null when trying to add announcement so no annc added");
    		return null;
    	}
    	
    	try
		{
			AnnouncementMessageEdit message = announcementChannel.addAnnouncementMessage();
			AnnouncementMessageHeaderEdit header = message.getAnnouncementHeaderEdit();
			header.setDraft(false);
			header.replaceAttachments(EntityManager.newReferenceList());

			header.setSubject(announcementSubject);
			message.setBody(announcementBody);
				
			if (assignment.getAssignmentGroupSet() == null || assignment.getAssignmentGroupSet().isEmpty()) {
				//site announcement
				header.clearGroupAccess();
			} else {
				addGroupRestrictions(assignment, contextId, header);
			}

			announcementChannel.commitMessage(message, NotificationService.NOTI_NONE);
			log.debug("announcement added with id: " + message.getId());
			
			return message.getId();	
			
		} catch (PermissionException pe) {
			throw new AnnouncementPermissionException("The current user does not have permission to access add announcement");
		}
	}
    
	public String updateOpenDateAnnouncement(Assignment2 assignment, String contextId, 
			String announcementSubject, String announcementBody) throws AnnouncementPermissionException {
		if (assignment == null || contextId == null) {
    		throw new IllegalArgumentException("null assignment or contextId passed to addOpenDateAnnouncement");
    	}
    	
    	initializeAnnouncementServiceData(contextId);
    	if (announcementChannel == null) {
    		log.warn("announcementChannel was null when trying to add announcement so no annc added");
    		return null;
    	}
    	
    	if (assignment.getAnnouncementId() == null) {
    		log.warn("there was no announcement assocated with the passed assignment");
    		return null;
    	}
    	
    	try
		{
			AnnouncementMessageEdit message = announcementChannel.editAnnouncementMessage(assignment.getAnnouncementId());
			AnnouncementMessageHeaderEdit header = message.getAnnouncementHeaderEdit();
			header.setDraft(false);
			header.replaceAttachments(EntityManager.newReferenceList());
			header.setSubject(announcementSubject);
			message.setBody(announcementBody);
				
			if (assignment.getAssignmentGroupSet() == null || assignment.getAssignmentGroupSet().isEmpty()) {
				//site announcement
				header.clearGroupAccess();
			} else {
				addGroupRestrictions(assignment, contextId, header);
			}

			announcementChannel.commitMessage(message, NotificationService.NOTI_NONE);
			log.debug("Announcement updated with id: " + assignment.getAnnouncementId());
			
			return message.getId();	
			
		} catch (PermissionException pe) {
			throw new AnnouncementPermissionException("The current user does not have permission to access add announcement");
		} catch (IdUnusedException iue) {
			// the announcement id stored in the assignment is invalid, so add a new announcement
			log.debug("Bad announcementId associated with assignment, so adding new announcement");
			return addOpenDateAnnouncement(assignment, contextId, announcementSubject, announcementBody);
		} catch (InUseException iue) {
			log.error("Announcement " + assignment.getAnnouncementId() + " is locked and cannot be" +
					"updated");
			return null;
		}
	}
	
	public void deleteOpenDateAnnouncement(Assignment2 assignment, String contextId) {
		if (assignment == null || contextId == null) {
    		throw new IllegalArgumentException("null assignment or contextId passed to addOpenDateAnnouncement");
    	}
    	
    	initializeAnnouncementServiceData(contextId);
    	if (announcementChannel == null) {
    		log.warn("announcementChannel was null when trying to add announcement so no annc added");
    		return;
    	}
    	
    	if (assignment.getAnnouncementId() == null) {
    		log.warn("there was no announcement associated with the passed assignment, so announcement was not deleted");
    		return;
    	}
    	
    	try
		{
			announcementChannel.removeMessage(assignment.getAnnouncementId());
			log.debug("Announcement removed with id: " + assignment.getAnnouncementId());
			
		} catch (PermissionException pe) {
			throw new AnnouncementPermissionException("The current user does not have permission to access add announcement");
		} 
	}
    
	private void initializeAnnouncementServiceData(String contextId) {
		aService = org.sakaiproject.announcement.cover.AnnouncementService.getInstance();
    	
    	String channelId = ServerConfigurationService.getString("channel", null);
    	if (channelId == null)
		{
			channelId = aService.channelReference(contextId, SiteService.MAIN_CONTAINER);
			try
			{
				announcementChannel = aService.getAnnouncementChannel(channelId);
			}
			catch (IdUnusedException e)
			{
				log.warn("No announcement channel found");
				announcementChannel = null;
			}
			catch (PermissionException e)
			{
				throw new AnnouncementPermissionException("The current user does not have permission to access the announcement channel");
			}
		}
	}
	
	private void addGroupRestrictions(Assignment2 assignment, String contextId, AnnouncementMessageHeaderEdit header) {
		try
		{
			Set assignmentGroups = assignment.getAssignmentGroupSet();
			if (assignmentGroups != null) {
				List groupIds = new ArrayList();
				for (Iterator aGroupIter = assignmentGroups.iterator(); aGroupIter.hasNext();) {
					AssignmentGroup aGroup = (AssignmentGroup) aGroupIter.next();
					if (aGroup != null && aGroup.getGroupId() != null) {
						groupIds.add(aGroup.getGroupId());
					}
				}

				//make a collection of Group objects from the collection of group ref strings
				Site site = SiteService.getSite(contextId);
				Collection<Group> groups = null;
				for (Iterator groupIdIter = groupIds.iterator(); groupIdIter.hasNext();)
				{
					String groupId = (String) groupIdIter.next();
					groups.add(site.getGroup(groupId));
				}

				// set access
				header.setGroupAccess(groups);
			}
		}
		catch (Exception exception)
		{
			log.warn("There was an error adding the group restrictions to the announcement");
		}
	}
		
}
