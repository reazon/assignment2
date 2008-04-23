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

package org.sakaiproject.assignment2.tool.producers;

import org.sakaiproject.assignment2.tool.params.FinishedHelperViewParameters;

import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;
import uk.org.ponder.htmlutil.HTMLUtil;

import org.sakaiproject.content.api.FilePickerHelper;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;

import java.util.List;

public class FinishedHelperProducer implements ViewComponentProducer, ViewParamsReporter
{
	  public static final String VIEWID = "FinishedHelper";
	  
	  public String getViewID() {
	    return VIEWID;
	  }
	  
	  private SessionManager sessionManager;
	  public void setSessionManager(SessionManager sessionManager) {
		  this.sessionManager = sessionManager;
	  }
	
	  public void fillComponents(UIContainer tofill, ViewParameters viewparams, ComponentChecker checker) {
		  
		  FinishedHelperViewParameters params = (FinishedHelperViewParameters) viewparams;
		  
		  //Really do nothing, let the JS do it all, call thickbox close window and Ajax call
		  // except call frame resize because the parent document window may have changed
		  
		  UIVerbatim.make(tofill, "sizeFrame",
				  HTMLUtil.emitJavascriptCall("parent.a2SetMainFrameHeight",
						  new String[] {org.sakaiproject.util.Web.escapeJavascript(
								  "Main" + org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement().getId())
				  			}
				  )
		  );
		  
		  //check session for attachment refs returned from a file picker helper
		  ToolSession toolSession = sessionManager.getCurrentToolSession();
		  if (toolSession.getAttribute(FilePickerHelper.FILE_PICKER_CANCEL) == null &&
				  toolSession.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS) != null) 
	      {
			  List<Reference> refs = (List)toolSession.getAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
			  String[] attachmentRefs = new String[refs.size()];
			  int i=0;
			  for (Reference ref : refs) {
				  attachmentRefs[i] = org.sakaiproject.util.Web.escapeUrl(ref.getId());
				  i++;
			  }
			  UIVerbatim.make(tofill, "updateAttachments",
				  HTMLUtil.emitJavascriptCall("parent.updateAttachments", attachmentRefs));
				  //Here are my references... now emit a JS call to add these references to the UI
				  //Then remove the FilePickerBean
				  //Then make the UI just duplicate or create a row for attachments
				  //Then do it for all attachments on the site
			  toolSession.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS);
			  toolSession.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);
	      }
		  
		  if (params.value != null && !params.value.equals("")) {
			  UIVerbatim.make(tofill, "useValue", 
					  HTMLUtil.emitJavascriptCall("parent.useValue", params.value));
		  }
	  }

	public ViewParameters getViewParameters() {
		return new FinishedHelperViewParameters();
	}
	
}