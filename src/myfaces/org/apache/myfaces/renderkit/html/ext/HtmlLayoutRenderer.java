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
package net.sourceforge.myfaces.renderkit.html.ext;

import net.sourceforge.myfaces.component.ext.HtmlPanelLayout;
import net.sourceforge.myfaces.renderkit.RendererUtils;
import net.sourceforge.myfaces.renderkit.html.HTML;
import net.sourceforge.myfaces.renderkit.html.HtmlRenderer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class HtmlLayoutRenderer
        extends HtmlRenderer
{
    private static final Log log = LogFactory.getLog(HtmlLayoutRenderer.class);

    public static final String CLASSIC_LAYOUT = "classic";
    public static final String NAV_RIGHT_LAYOUT = "navigationRight";
    public static final String UPSIDE_DOWN_LAYOUT = "upsideDown";

    public boolean getRendersChildren()
    {
        return true;
    }

    public void decode(FacesContext context, UIComponent component)
    {
    }

    public void encodeBegin(FacesContext context, UIComponent component) throws IOException
    {
    }

    public void encodeChildren(FacesContext context, UIComponent component) throws IOException
    {
    }

    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException
    {
        RendererUtils.checkParamValidity(facesContext, component, HtmlPanelLayout.class);

        HtmlPanelLayout panelLayout = (HtmlPanelLayout)component;

        String layout = panelLayout.getLayout();
        if (layout == null || layout.equals(CLASSIC_LAYOUT))
        {
            renderClassic(facesContext, panelLayout);
        }
        else if (layout.equals(NAV_RIGHT_LAYOUT))
        {

        }
        else if (layout.equals(UPSIDE_DOWN_LAYOUT))
        {

        }
        else
        {
            log.error("Unknown panel layout '" + layout + "'!");
        }
    }

    protected void renderClassic(FacesContext facesContext, HtmlPanelLayout panelLayout)
            throws IOException
    {
        ResponseWriter writer = facesContext.getResponseWriter();
        writer.startElement(HTML.TABLE_ELEM, null);
        HtmlPanelGroup header = panelLayout.getHeader();
        if (header != null)
        {
            writer.startElement(HTML.TR_ELEM, null);
            writer.startElement(HTML.TD_ELEM, null);
            renderChild(facesContext, header);
            writer.endElement(HTML.TD_ELEM);
            writer.endElement(HTML.TR_ELEM);
        }

        //HtmlPanelGroup header = panelLayout.getHeader();

        writer.endElement(HTML.TABLE_ELEM);
    }


    protected void renderChild(FacesContext facesContext, UIComponent component)
            throws IOException
    {
        component.encodeBegin(facesContext);
        if (component.getRendersChildren())
        {
            component.encodeChildren(facesContext);
        }
        else if (component.getChildCount() > 0)
        {
            for (Iterator it = component.getChildren().iterator(); it.hasNext(); )
            {
                UIComponent child = (UIComponent)it.next();
                renderChild(facesContext, child);
            }
        }
        component.encodeEnd(facesContext);
    }

}
