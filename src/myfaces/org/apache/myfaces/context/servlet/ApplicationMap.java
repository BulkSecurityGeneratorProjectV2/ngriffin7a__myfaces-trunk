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
package net.sourceforge.myfaces.context.servlet;

import java.util.Enumeration;

import javax.servlet.ServletContext;


/**
 * ServletContext attributes as a Map.
 * 
 * @author Anton Koinov (latest modification by $Author$)
 * @version $Revision$ $Date$
 * 
 * $Log$
 * Revision 1.8  2004/03/30 07:43:33  dave0000
 * add $Log
 *
 */
public class ApplicationMap extends AbstractAttributeMap
{
    final ServletContext _servletContext;

    ApplicationMap(ServletContext servletContext)
    {
        _servletContext = servletContext;
    }

    protected Object getAttribute(String key)
    {
        return _servletContext.getAttribute(key);
    }

    protected void setAttribute(String key, Object value)
    {
        _servletContext.setAttribute(key, value);
    }

    protected void removeAttribute(String key)
    {
        _servletContext.removeAttribute(key);
    }

    protected Enumeration getAttributeNames()
    {
        return _servletContext.getAttributeNames();
    }
}
