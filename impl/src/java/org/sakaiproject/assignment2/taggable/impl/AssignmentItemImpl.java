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

package org.sakaiproject.assignment2.taggable.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment2.model.AssignmentSubmission;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.taggable.api.TaggableActivity;
import org.sakaiproject.taggable.api.TaggableItem;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ResourceLoader;

public class AssignmentItemImpl implements TaggableItem {

	private static final Log logger = LogFactory
			.getLog(AssignmentItemImpl.class);

	private static ResourceLoader rb = new ResourceLoader("assignment");

	protected static final String ITEM_REF_SEPARATOR = "@";

	protected AssignmentSubmission submission;

	protected String userId;

	protected TaggableActivity activity;

	public AssignmentItemImpl(AssignmentSubmission submission, String userId,
			TaggableActivity activity) {
		this.submission = submission;
		this.userId = userId;
		this.activity = activity;
	}

	public TaggableActivity getActivity() {
		return activity;
	}

	public String getContent() {
		return submission.getCurrentSubmissionVersion().getSubmittedText();
	}

	public Object getObject() {
		return submission;
	}

	public String getReference() {
		StringBuilder sb = new StringBuilder();
		sb.append(submission.getReference());
		sb.append(ITEM_REF_SEPARATOR);
		sb.append(userId);
		return sb.toString();
	}

	public String getTitle() {
		StringBuilder sb = new StringBuilder();
		try {
			User user = UserDirectoryService.getUser(userId);
			sb.append(user.getFirstName());
			sb.append(' ');
			sb.append(user.getLastName());
			sb.append(' ');
			sb.append(rb.getString("gen.submission"));
		} catch (UserNotDefinedException unde) {
			logger.error(unde.getMessage(), unde);
		}
		return sb.toString();
	}

	public String getUserId() {
		return userId;
	}

	public String getItemDetailUrl()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getIconUrl()
	{
		String url = ServerConfigurationService.getServerUrl() + "/library/image/silk/page_edit.png";
		return url;
	}
	
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AssignmentItemImpl))
			return false;
		else if (!((TaggableItem) obj).getReference().equals(this.getReference()))
			return false;
		
		return true;
	}
	
	public int hashCode()
	{
		return this.getReference().hashCode();
	}
}