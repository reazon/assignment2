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

package org.sakaiproject.assignment2.exception;

/**
 * Indicates that the assignment that the user attempted to save was marked as
 * graded, but no gradableObjectId was defined
 *
 * @author <a href="mailto:wagnermr@iupui.edu">michelle wagner</a>
 */
public class NoGradebookItemForGradedAssignmentException extends AssignmentException {

	private static final long serialVersionUID = 1L;

	public NoGradebookItemForGradedAssignmentException(String message) {
        super(message);
    }
	
	public NoGradebookItemForGradedAssignmentException(String message, Throwable t) {
        super(message, t);
    }
}
