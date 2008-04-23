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

/**
 * The AssignmentGroup object.  AssignmentGroups are used when the 
 * assignment is restricted to select group(s).
 * 
 * @author <a href="mailto:wagnermr@iupui.edu">michelle wagner</a>
 */
public class AssignmentGroup {

	private Long id;
	private Assignment2 assignment;
	private String groupId;
	private int revisionVersion;
	
	public AssignmentGroup() {
		
	}
	
	public AssignmentGroup(Assignment2 assignment, String groupId) {
		this.assignment = assignment;
		this.groupId = groupId;
	}
	
	/**
	 * 
	 * @return id
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * set the id
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * 
	 * @return the assignment that is restricted to this AssignmentGroup
	 */
	public Assignment2 getAssignment() {
		return assignment;
	}
	
	/**
	 * the assignment that is restricted to this AssignmentGroup
	 * @param assignment
	 */
	public void setAssignment(Assignment2 assignment) {
		this.assignment = assignment;
	}

	/**
	 * 
	 * @return the realm id for this group/section that is allowed
	 * access to the associated group-restricted assignment
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * access to the assignment will be allowed for this realm id
	 * @param groupId
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
	
	@Override
	public boolean equals(Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof AssignmentGroup)) return false;
		else {
			AssignmentGroup compGroup = (AssignmentGroup) obj;
			if (this.id == null || compGroup.id == null) {
				return false;
			}
			if (null == this.id || null == compGroup.id) return false;
			else return (
					this.id.equals(compGroup.id)
			);
		}
	}
	
	@Override
	public int hashCode() {
		if (null == this.id) return super.hashCode();
		String hashStr = this.getClass().getName() + ":" + this.id.hashCode();
		return hashStr.hashCode();
	}
	
}
