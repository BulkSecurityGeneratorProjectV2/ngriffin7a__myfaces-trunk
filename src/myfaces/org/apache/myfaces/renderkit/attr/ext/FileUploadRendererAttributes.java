/**
 * MyFaces - the free JSF implementation
 * Copyright (C) 2003, 2004  The MyFaces Team (http://myfaces.sourceforge.net)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package net.sourceforge.myfaces.renderkit.attr.ext;

import net.sourceforge.myfaces.renderkit.JSFAttr;

/**
 * Constant definitions for the specified render dependent attributes of the
 * "FileUpload" renderer type.
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public interface FileUploadRendererAttributes
{
    public static final String ACCEPT_ATTR = "accept";
    public static final String MAX_LENGTH_ATTR = "maxlength";

    public static final String[] FILE_UPLOAD_ATTRIBUTES =
    {
        JSFAttr.INPUT_CLASS_ATTR,
        ACCEPT_ATTR,
        MAX_LENGTH_ATTR
    };

}
