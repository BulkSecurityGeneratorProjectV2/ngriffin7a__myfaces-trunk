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
package net.sourceforge.myfaces.renderkit.html.util;

import net.sourceforge.myfaces.renderkit.JSFAttr;
import net.sourceforge.myfaces.renderkit.RendererUtils;
import net.sourceforge.myfaces.renderkit.html.HTML;
import net.sourceforge.myfaces.renderkit.html.ListboxRenderer;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;


/**
 * Utility methods for rendering HTML tags.
 * @author Thomas Spiegl (latest modification by $Author$)
 * @author Anton Koinov
 * @version $Revision$ $Date$
 */
public class HTMLUtil
{
    //~ Constructors -------------------------------------------------------------------------------

    private HTMLUtil()
    {
    }

    //~ Methods ------------------------------------------------------------------------------------

    public static int getColspan(UIComponent component)
    {
        Object value = component.getAttributes().get(JSFAttr.COLSPAN_ATTR);
        int    count;

        count = ((value != null) && (value instanceof Integer)) ? ((Integer) value).intValue() : 1;

        return count;
    }

    public static int getColumns(UIComponent component)
    {
        Object value = component.getAttributes().get(JSFAttr.COLUMNS_ATTR);
        int    count;

        count =
            ((value != null) && (value instanceof Integer)) ? ((Integer) value).intValue()
                                                            : Integer.MAX_VALUE;

        return count;
    }

    public static void encodeChildrenRecursively(FacesContext context, UIComponent component)
    throws IOException
    {
        component.encodeBegin(context);

        if (component.getRendersChildren())
        {
            component.encodeChildren(context);
        }
        else
        {
            UIComponent child;

            for (
                Iterator kids = component.getChildren().iterator(); kids.hasNext();
                        encodeChildrenRecursively(context, child))
            {
                child = (UIComponent) kids.next();
            }
        }

        component.encodeEnd(context);
    }


    /**
     * @return true, if the attribute was written
     * @throws IOException
     *
     * @deprecated styleClass is now contained in HTML.UNIVERSAL_ATTRIBUTES
     */
    public static boolean renderStyleClass(ResponseWriter writer, UIComponent uiComponent)
        throws IOException
    {
        String styleClass = (String) uiComponent.getAttributes().get(JSFAttr.STYLE_CLASS_ATTR);
        if (styleClass != null && styleClass.length() > 0)
        {
            writer.writeAttribute("class", styleClass, JSFAttr.STYLE_CLASS_ATTR);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * @return true, if the attribute was written
     * @throws IOException
     */
    public static boolean renderHTMLAttribute(ResponseWriter writer,
                                              UIComponent component,
                                              String rendererAttrName,
                                              String htmlAttrName)
        throws IOException
    {
        Object value = component.getAttributes().get(rendererAttrName);
        if (!RendererUtils.isDefaultAttributeValue(value))
        {
            writer.writeAttribute(htmlAttrName, value, rendererAttrName);
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * @return true, if an attribute was written
     * @throws IOException
     */
    public static boolean renderHTMLAttributes(ResponseWriter writer,
                                               UIComponent component,
                                               String[] attributes)
        throws IOException
    {
        boolean somethingDone = false;
        for (int i = 0; i < attributes.length; i++)
        {
            String attrName = attributes[i];
            if (attrName.equals(HTML.STYLE_CLASS_ATTR))
            {
                //render JSF "styleClass" attribute as "class"
                if (renderHTMLAttribute(writer, component, attrName, "class"))
                {
                    somethingDone = true;
                }
            }
            else
            {
                if (renderHTMLAttribute(writer, component, attrName, attrName))
                {
                    somethingDone = true;
                }
            }
        }
        return somethingDone;
    }


    public static void renderSelect(
        FacesContext facesContext, UIComponent uiComponent, String rendererType, int size)
    throws IOException
    {
        ResponseWriter writer     = facesContext.getResponseWriter();

        boolean        selectMany = (uiComponent instanceof UISelectMany);

        writer.write("<select");
        writer.write(" name=\"");
        writer.write(uiComponent.getClientId(facesContext));
        writer.write('"');

        if (rendererType.equals(ListboxRenderer.TYPE))
        {
            writer.write(" size=\"");
            writer.write(Integer.toString(size));
            writer.write('"');
        }

        renderStyleClass(writer, uiComponent);
        renderHTMLAttributes(writer, uiComponent, HTML.SELECT_PASSTHROUGH_ATTRIBUTES);
        renderDisabledOnUserRole(facesContext, uiComponent);

        if (selectMany)
        {
            writer.write(" multiple ");
        }

        writer.write(">\n");

        Iterator it = SelectItemUtil.getSelectItems(facesContext, uiComponent);

        if (it.hasNext())
        {
            String currentStrValue   = null;
            Set    selectedValuesSet = null;

            if (selectMany)
            {
                selectedValuesSet =
                    SelectItemUtil.getSelectedValuesAsStringSet(
                        facesContext, (UISelectMany) uiComponent);
            }
            else
            {
                //FIXME
                //Object currentValue = ((UIInput) uiComponent).currentValue(facesContext);
                Object currentValue = null;

                /*
                currentStrValue = ConverterUtils.getComponentValueAsString(facesContext,
                                                                           uiComponent,
                                                                           currentValue);
                                                                           */
                currentStrValue = ((currentValue != null) ? currentValue.toString() : null);
            }

            while (it.hasNext())
            {
                SelectItem item = (SelectItem) it.next();
                writer.write("\t\t<option");

                Object itemObjValue = item.getValue();

                if (itemObjValue != null)
                {
                    String itemStrValue = itemObjValue.toString();
                    writer.write(" value=\"");
                    writer.write(HTMLEncoder.encode(itemStrValue, false, false));
                    writer.write('"');

                    if (
                        (selectMany && selectedValuesSet.contains(itemStrValue))
                                || ((currentStrValue != null)
                                && itemStrValue.equals(currentStrValue)))
                    {
                        writer.write(" selected=\"selected\"");
                    }
                }

                writer.write('>');
                writer.write(HTMLEncoder.encode(
                        item.getLabel(),
                        true,
                        true));

                writer.write("</option>\n");
            }
        }

        writer.write("</select>");
    }


    /**@deprecated use {@link #renderDisabledOnUserRole(javax.faces.context.ResponseWriter, javax.faces.component.UIComponent, javax.faces.context.FacesContext)} instead*/
    public static void renderDisabledOnUserRole(FacesContext facesContext, UIComponent uiComponent)
        throws IOException
    {
        if (!RendererUtils.isEnabledOnUserRole(facesContext, uiComponent))
        {
            ResponseWriter writer = facesContext.getResponseWriter();
            writer.write(" disabled");
        }
    }


    public static void renderDisabledOnUserRole(ResponseWriter writer,
                                                UIComponent uiComponent,
                                                FacesContext facesContext)
        throws IOException
    {
        if (!RendererUtils.isEnabledOnUserRole(facesContext, uiComponent))
        {
            writer.writeAttribute(HTML.DISABLED_ATTR, Boolean.TRUE, JSFAttr.ENABLED_ON_USER_ROLE_ATTR);
        }
    }



    public static void renderTableRowOfOneCell(
        FacesContext context, UIComponent component, int columns, String rowClass,
        String[] columnClasses, String cellHtmlTag, String rowGroupTag)
    throws IOException
    {
        ResponseWriter writer           = context.getResponseWriter();
        int            columnClassCount = (columnClasses == null) ? 0 : columnClasses.length;

        if (rowGroupTag != null)
        {
            writer.write("\t<");
            writer.write(rowGroupTag);
            writer.write(">\n");
        }

        writer.write("\t\t<tr");

        if (rowClass != null)
        {
            writer.write(" class=\"");
            writer.write(rowClass);
            writer.write('"');
        }

        writer.write(">\n\t\t\t<");
        writer.write(cellHtmlTag);

        int colspan = getColspan(component);

        if (colspan > 1)
        {
            writer.write(" colspan=\"");
            writer.write(colspan);
            writer.write('"');
        }

        if (columnClassCount > 0)
        {
            writer.write(" class=\"");
            writer.write(columnClasses[0]);
            writer.write('"');
        }

        writer.write('>');

        encodeChildrenRecursively(context, component);

        writer.write("</");
        writer.write(cellHtmlTag);
        writer.write(">\n");

        // REVISIT: should we fill in with empty cells or not?
//        for (int i = 1; i < columns; i++)
//        {
//            writer.write("\t\t\t<");
//            writer.write(cellHtmlTag);
//
//            if (columnClassCount > 0)
//            {
//                writer.write(" class=\"");
//                writer.write(columnClasses[i % columnClassCount]);
//                writer.write('"');
//            }
//
//            writer.write(" />\n");
//        }
        writer.write("\t\t</tr>\n");

        if (rowGroupTag != null)
        {
            writer.write("\t</");
            writer.write(rowGroupTag);
            writer.write(">\n");
        }
    }

    public static int renderTableRows(
        FacesContext context, Iterator fields, int columns, String[] rowClasses,
        String[] columnClasses, String cellHtmlTag, String rowGroupTag, int row)
    throws IOException
    {
        ResponseWriter writer = context.getResponseWriter();

        if (rowGroupTag != null)
        {
            writer.write("\t<");
            writer.write(rowGroupTag);
            writer.write(">\n");
        }

        int     rowClassCount    = (rowClasses == null) ? 0 : rowClasses.length;
        int     columnClassCount = (columnClasses == null) ? 0 : columnClasses.length;

        boolean closeRowTag      = false;

        for (
            int col = Integer.MAX_VALUE, colspan = 1, colClassIndex = 0; fields.hasNext();
                    col += colspan)
        {
            if (col >= columns)
            {
                col = 0;

                if (closeRowTag)
                {
                    writer.write("\t\t</tr>\n");
                }

                writer.write("\t\t<tr");

                if (rowClassCount > 0)
                {
                    writer.write(" class=\"");
                    writer.write(rowClasses[row++ % rowClassCount]);
                    writer.write('"');
                }

                writer.write(">\n");
                closeRowTag       = true;
                colClassIndex     = 0;
            }

            writer.write("\t\t\t<");
            writer.write(cellHtmlTag);

            UIComponent child = (UIComponent) fields.next();
            colspan = getColspan(child);

            if (colspan > 1)
            {
                writer.write(" colspan=\"");
                writer.write(colspan);
                writer.write('"');
            }

            if (columnClassCount > 0)
            {
                writer.write(" class=\"");
                writer.write(columnClasses[colClassIndex++]);
                writer.write('"');

                if (colClassIndex >= columnClassCount)
                {
                    colClassIndex = 0;
                }
            }

            writer.write('>');

            encodeChildrenRecursively(context, child);

            writer.write("</");
            writer.write(cellHtmlTag);
            writer.write(">\n");
        }

        if (closeRowTag)
        {
            writer.write("\t\t</tr>\n");
        }

        if (rowGroupTag != null)
        {
            writer.write("\t</");
            writer.write(rowGroupTag);
            writer.write(">\n");
        }

        return row;
    }


}
