/*
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
package net.sourceforge.myfaces.renderkit.html;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * @author Manfred Geiler (latest modification by $Author$)
 * @author Thomas Spiegl
 * @version $Revision$ $Date$
 * $Log$
 * Revision 1.14  2004/03/31 14:51:46  manolito
 * summaryFormat and detailFormat support
 *
 * Revision 1.13  2004/03/30 17:47:34  manolito
 * Message and Messages refactored
 *
 * Revision 1.12  2004/03/30 13:24:57  manolito
 * refactoring: HtmlComponentTag moved to share and renamed to HtmlComponentTagBase
 *
 */
public class HtmlMessageRenderer
        extends HtmlMessageRendererBase
{
    //private static final Log log = LogFactory.getLog(HtmlMessageRenderer.class);

    public void encodeEnd(FacesContext facesContext, UIComponent component)
            throws IOException
    {
        super.encodeEnd(facesContext, component);   //check for NP
        renderMessage(facesContext, component);
    }

    protected String getSummary(FacesContext facesContext,
                                UIComponent message,
                                FacesMessage facesMessage,
                                String msgClientId)
    {
        return facesMessage.getSummary();
    }

    protected String getDetail(FacesContext facesContext,
                               UIComponent message,
                               FacesMessage facesMessage,
                               String msgClientId)
    {
        return facesMessage.getDetail();
    }
}
