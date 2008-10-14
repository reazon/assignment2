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

package org.sakaiproject.assignment2.tool.beans;

import org.sakaiproject.assignment2.model.Assignment2;

import uk.org.ponder.beanutil.entity.EntityBeanLocator;

public class PreviewAssignmentBean {
	
	private Assignment2 assignment;
	private String OTPKey = EntityBeanLocator.NEW_PREFIX + "1";
	
	public void setAssignment(Assignment2 assignment){
		this.assignment = assignment;
	}
	
	public Assignment2 getAssignment(){
		return this.assignment;
	}
	
	public void setOTPKey(String key) {
		this.OTPKey = key;
	}
	
	public String getOTPKey() {
		return this.OTPKey;
	}
}