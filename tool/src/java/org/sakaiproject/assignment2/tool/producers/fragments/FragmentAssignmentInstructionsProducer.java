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

package org.sakaiproject.assignment2.tool.producers.fragments;

import org.sakaiproject.assignment2.logic.AssignmentLogic;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.tool.params.AssignmentViewParams;
import org.sakaiproject.assignment2.tool.producers.renderers.AttachmentListRenderer;

import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.content.ContentTypeReporter;
import uk.org.ponder.rsf.content.ContentTypeInfoRegistry;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

public class FragmentAssignmentInstructionsProducer implements ViewComponentProducer, ViewParamsReporter, ContentTypeReporter {

    public static final String VIEW_ID = "fragment-assignment-instructions";
    public String getViewID() {
        return VIEW_ID;
    }
    
    private AssignmentLogic assignmentLogic;
    private AttachmentListRenderer attachmentListRenderer;

    public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
    	AssignmentViewParams params = (AssignmentViewParams) viewparams;

    	Assignment2 assignment = assignmentLogic.getAssignmentByIdWithAssociatedData(params.assignmentId);
    	
    	UIVerbatim.make(tofill, "assignment_instructions", assignment.getInstructions());
        attachmentListRenderer.makeAttachmentFromAssignmentAttachmentSet(tofill, "assignment_attachment_list:", params.viewID, 
        		assignment.getAttachmentSet());


    }
    
    public ViewParameters getViewParameters() {
        return new AssignmentViewParams();
    }
	
	public String getContentType() {
		return ContentTypeInfoRegistry.HTML_FRAGMENT;
	}

	public void setAssignmentLogic(AssignmentLogic assignmentLogic) {
		this.assignmentLogic = assignmentLogic;
	}

	public void setAttachmentListRenderer(
			AttachmentListRenderer attachmentListRenderer) {
		this.attachmentListRenderer = attachmentListRenderer;
	}
}