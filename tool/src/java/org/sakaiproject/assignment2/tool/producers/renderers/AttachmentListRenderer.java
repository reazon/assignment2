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

package org.sakaiproject.assignment2.tool.producers.renderers;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment2.logic.ExternalLogic;
import org.sakaiproject.assignment2.model.Assignment2;
import org.sakaiproject.assignment2.model.AssignmentAttachment;
import org.sakaiproject.assignment2.model.FeedbackAttachment;
import org.sakaiproject.assignment2.model.SubmissionAttachment;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;

import uk.org.ponder.beanutil.entity.EntityBeanLocator;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIJointContainer;
import uk.org.ponder.rsf.components.UILink;
import uk.org.ponder.rsf.components.UIMessage;
import uk.org.ponder.rsf.components.UIOutput;

/**
 * Contains a number of convenience methods for rendering different kinds of
 * Attachments (ie. Supporting Materials). Note that there are different methods
 * to be used depending on whether the attachments are on an Assignment,
 * Submission, Feedback, etc.
 * 
 * TODO FIXME I'm still not sure why some of these methods require a viewid.
 * I'm guessing they might need to generate return URL's or something.
 * It would be cool to have the option of passing null for the viewid, in 
 * which case it would look it up in the context. But that would also allow
 * you to script this in case you were generating markup for future events.
 * 
 * @author rjlowe
 * @author sgithens
 *
 */
public class AttachmentListRenderer {
    private static final Log LOG = LogFactory.getLog(AttachmentListRenderer.class);

    private ContentHostingService contentHostingService;
    public void setContentHostingService(ContentHostingService contentHostingService) {
        this.contentHostingService = contentHostingService;
    }
    
    private ExternalLogic externalLogic;
    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

    private EntityBeanLocator assignment2EntityBeanLocator;
    public void setAssignment2EntityBeanLocator(EntityBeanLocator assignment2EntityBeanLocator) {
        this.assignment2EntityBeanLocator = assignment2EntityBeanLocator;
    }

    /**
     * Use this for rendering attachments from an Assignment2 assignment
     * object. 
     * 
     * @param tofill
     * @param divID
     * @param currentViewID
     * @param aaSet
     */
    public void makeAttachmentFromAssignmentAttachmentSet(UIContainer tofill, String divID, String currentViewID, Set<AssignmentAttachment> aaSet) {
        Set<String> refSet = new HashSet<String>();
        if (aaSet != null){
            for (AssignmentAttachment aa : aaSet) {
                refSet.add(aa.getAttachmentReference());
            }
        }
        makeAttachment(tofill, divID, currentViewID, refSet);
    }

    public void makeAttachmentFromAssignment2OTPAttachmentSet(UIContainer tofill, String divID, String currentViewID, String a2OTPKey) {
        Assignment2 assignment = (Assignment2)assignment2EntityBeanLocator.locateBean(a2OTPKey);
        Set<String> refSet = new HashSet<String>();
        if (assignment != null && assignment.getAttachmentSet() != null){
            for (AssignmentAttachment aa : assignment.getAttachmentSet()) {
                refSet.add(aa.getAttachmentReference());
            }
        }
        makeAttachment(tofill, divID, currentViewID, refSet);
    }

    public void makeAttachmentFromSubmissionAttachmentSet(UIContainer tofill, String divID, String currentViewID,
            Set<SubmissionAttachment> asaSet) {
        Set<String> refSet = new HashSet<String>();
        if (asaSet != null) {
            for (SubmissionAttachment asa : asaSet) {
                refSet.add(asa.getAttachmentReference());
            }
        }
        makeAttachment(tofill, divID, currentViewID, refSet);
    }

    public void makeAttachmentFromFeedbackAttachmentSet(UIContainer tofill, String divID, String currentViewID,
            Set<FeedbackAttachment> afaSet) {
        Set<String> refSet = new HashSet<String>();
        if (afaSet != null) {
            for (FeedbackAttachment afa : afaSet) {
                refSet.add(afa.getAttachmentReference());
            }
        }
        makeAttachment(tofill, divID, currentViewID, refSet);
    }


    public void makeAttachment(UIContainer tofill, String divID, String currentViewID, Set<String> refSet) {


        int i = 1;
        if (refSet.size() == 0) {
            UIJointContainer joint = new UIJointContainer(tofill, divID, "attachments:", ""+1);
            UIMessage.make(joint, "no_attachments_yet", "assignment2.no_attachments_yet");
            return;
        }

        for (String ref : refSet){
            UIJointContainer joint = new UIJointContainer(tofill, divID, "attachments:", ""+(i++));
            try {
                //TODO  put all contentHosting calls in an external Logic
                
                //TODO FIXME For some reason, when there are no attachments, we 
                // still getting a single item in the Set<String> ref that is 
                // just an empty string.  This is on previewing an assignment.
                // To reproduce, just put in a title and hit preview.
                if (ref != null && !ref.equals("")) {
                    ContentResource cr = contentHostingService.getResource(ref);
                    UILink.make(joint, "attachment_image", externalLogic.getContentTypeImagePath(cr));
                    UILink.make(joint, "attachment_link", cr.getProperties().getProperty(cr.getProperties().getNamePropDisplayName()),
                            cr.getUrl());
                    String file_size = externalLogic.getReadableFileSize(cr.getContentLength());
                    UIOutput.make(joint, "attachment_size", file_size);
                }

            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                //do nothing
            }
        } //Ending for loop
    }

}
