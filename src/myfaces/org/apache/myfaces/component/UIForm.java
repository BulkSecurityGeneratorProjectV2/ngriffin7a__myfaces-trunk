/**
 * MyFaces - the free JSF implementation
 * Copyright (C) 2003  The MyFaces Team (http://myfaces.sourceforge.net)
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
package net.sourceforge.myfaces.component;

/**
 * Derived class from javax.faces.UIForm.
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class UIForm
        extends javax.faces.component.UIForm
{
    public static final String FORM_NAME_ATTR = "formName";

    public void setAttribute(String s, Object o)
    {
        if (s.equals(FORM_NAME_ATTR))
        {
            setValue(o);
        }
        else
        {
            super.setAttribute(s, o);
        }
    }


    public String getFormName()
    {
        String formName = super.getFormName();
        if (formName == null)
        {
            return getCompoundId();
        }
        return formName;
    }

}
