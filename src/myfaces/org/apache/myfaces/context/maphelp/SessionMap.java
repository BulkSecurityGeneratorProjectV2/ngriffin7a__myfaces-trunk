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
package net.sourceforge.myfaces.context.maphelp;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * Helper class for {@link net.sourceforge.myfaces.context.ExternalContextImpl}
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class SessionMap
    extends AbstractAttributeMap
{
    private HttpSession _session;

    public SessionMap(HttpSession session)
    {
        _session = session;
    }

    protected Object getAttribute(String name)
    {
        return _session.getAttribute(name);
    }

    protected void setAttribute(String name, Object newVal)
    {
        _session.setAttribute(name, newVal);
    }

    protected void removeAttribute(String name)
    {
        _session.removeAttribute(name);
    }

    protected Enumeration getAttributeNames()
    {
        return _session.getAttributeNames();
    }

}
