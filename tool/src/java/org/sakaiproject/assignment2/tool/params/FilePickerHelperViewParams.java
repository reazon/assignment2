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

package org.sakaiproject.assignment2.tool.params;

/**
 * These view parameters are for the Add Attachments / File Picker helper. 
 * See the {@link ThickboxHelperViewParams} for information about some of the
 * inherited members.
 * 
 * The one important key of this one is the public otpkey member.  This appears
 * to be binding to FilePickerBean.  From debugging it's value usually seems to
 * be 'new 1'. I'm not sure if it's ever used with an existing ID.  We might be
 * able to simplify this. TODO FIXME
 * 
 * @author rjlowe
 * @author sgithens
 *
 */
public class FilePickerHelperViewParams extends ThickboxHelperViewParams {

    public String otpkey;

    public FilePickerHelperViewParams() {}

    public FilePickerHelperViewParams(String viewId, String otpkey){
        super(viewId);
        this.otpkey = otpkey;
    }

    public FilePickerHelperViewParams(String viewId, Boolean KeepThis, Boolean TB_iframe, int height, int width, String otpkey){
        super(viewId, KeepThis, TB_iframe, height, width);
        this.otpkey = otpkey;
    }

    public String getParseSpec() {
        // include a comma delimited list of the public properties in this class
        return super.getParseSpec() + ",otpkey";
    }
}