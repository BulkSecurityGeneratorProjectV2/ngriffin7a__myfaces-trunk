package net.sourceforge.myfaces.webapp;

import javax.servlet.ServletContext;

/**
 * TODO: description
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public interface ServletMappingFactory
{
    public ServletMapping getServletMapping(ServletContext servletContext);
}
