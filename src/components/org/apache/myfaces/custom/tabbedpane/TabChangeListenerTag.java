/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.myfaces.custom.tabbedpane;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import net.sourceforge.myfaces.custom.tree.HtmlTree;
import net.sourceforge.myfaces.custom.tree.event.TreeSelectionListener;
import net.sourceforge.myfaces.util.ClassUtils;


/**
 * Tag to add a tab change listeners to a {@link net.sourceforge.myfaces.custom.tabbedpane.HtmlPanelTabbedPane]
 *
 * @author <a href="mailto:oliver@rossmueller.com">Oliver Rossmueller</a>
 * @version $Revision$ $Date$
 *          $Log$
 *          Revision 1.1  2004/08/09 22:01:38  o_rossmueller
 *          tagChangeListener tag
 *
 *          Revision 1.2  2004/07/01 21:53:06  mwessendorf
 *          ASF switch
 *
 *          Revision 1.1  2004/04/22 21:14:55  o_rossmueller
 *          TreeSelectionListener support
 *
 */
public class TabChangeListenerTag extends TagSupport
{

    private String type = null;


    public TabChangeListenerTag()
    {
    }


    public void setType(String type)
    {
        this.type = type;
    }


    public int doStartTag() throws JspException
    {
        if (type == null)
        {
            throw new JspException("type attribute not set");
        }

        //Find parent UIComponentTag
        UIComponentTag componentTag = UIComponentTag.getParentUIComponentTag(pageContext);
        if (componentTag == null)
        {
            throw new JspException("TabChangeListenerTag has no UIComponentTag ancestor");
        }

        if (componentTag.getCreated())
        {
            //Component was just created, so we add the Listener
            UIComponent component = componentTag.getComponentInstance();
            if (component instanceof HtmlPanelTabbedPane)
            {
                String className;
                if (UIComponentTag.isValueReference(type))
                {
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    ValueBinding valueBinding = facesContext.getApplication().createValueBinding(type);
                    className = (String) valueBinding.getValue(facesContext);
                } else
                {
                    className = type;
                }
                TabChangeListener listener = (TabChangeListener) ClassUtils.newInstance(className);
                ((HtmlPanelTabbedPane) component).addTabChangeListener(listener);
            } else
            {
                throw new JspException("Component " + component.getId() + " is no HtmlPanelTabbedPane");
            }
        }

        return Tag.SKIP_BODY;
    }
}
