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

package net.sourceforge.myfaces.application;

import net.sourceforge.myfaces.renderkit.html.state.StateRenderer;
import net.sourceforge.myfaces.webapp.webxml.ServletMapping;
import net.sourceforge.myfaces.webapp.webxml.WebXml;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;
import javax.servlet.ServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * DOCUMENT ME!
 * 
 * @author Thomas Spiegl (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class ViewHandlerImpl
    implements ViewHandler
{
    private static final Log log = LogFactory.getLog(ViewHandlerImpl.class);

    private StateManager _stateManager;

    public ViewHandlerImpl()
    {
        _stateManager = new StateManagerImpl();
        if (log.isTraceEnabled()) log.trace("New ViewHanldler instance created");
    }

    public Locale calculateLocale(FacesContext facescontext)
    {
        Enumeration locales = ((ServletRequest)facescontext.getExternalContext().getRequest()).getLocales();
        while (locales.hasMoreElements())
        {
            Locale locale = (Locale) locales.nextElement();
            for (Iterator it = facescontext.getApplication().getSupportedLocales(); it.hasNext();)
            {
                Locale supportLocale = (Locale)it.next();
                // higher priority to a langauage match over an exact match
                // that occures further down (see Jstl Reference 1.0 8.3.1)
                if (locale.getLanguage().equals(supportLocale.getLanguage()) &&
                    (supportLocale.getCountry() == null ||
                    supportLocale.getCountry().length() == 0))
                {
                    return locale;
                }
                else if (supportLocale.equals(locale))
                {
                    return locale;
                }
            }
        }

        Locale locale = facescontext.getApplication().getDefaultLocale();
        return locale != null ? locale : Locale.getDefault();
    }

    public UIViewRoot createView(FacesContext facescontext, String viewId)
    {
        UIViewRoot uiViewRoot = new UIViewRoot();
        uiViewRoot.setViewId(viewId);
        uiViewRoot.setLocale(calculateLocale(facescontext));
        if (log.isTraceEnabled()) log.trace("Created view " + viewId);
        return uiViewRoot;
    }

    public StateManager getStateManager()
    {
        return _stateManager;
    }

    public String getViewIdPath(FacesContext facescontext, String viewId)
    {
        if (viewId == null)
        {
            log.error("ViewId must not be null");
            throw new NullPointerException("ViewId must not be null");
        }
        if (!viewId.startsWith("/"))
        {
            log.error("ViewId must start with '/' (viewId = " + viewId + ")");
            throw new IllegalArgumentException("ViewId must start with '/' (viewId = " + viewId + ")");
        }
        ExternalContext externalContext = facescontext.getExternalContext();
        String path = getPath(externalContext);
        if (path == null)
        {
            return viewId;
        }
        viewId = viewId.substring(1, viewId.length());
        if (path.endsWith("*"))
        {
            // prefix mapping
            return path.substring(0, path.length() - 1) + viewId;
        }
        else if (path.startsWith("*"))
        {
            // extension mapping
            String extensionMapping = path.substring(2, path.length());
            if (viewId.endsWith(extensionMapping))
            {
                return viewId;
            }
            else
            {
                int idx = viewId.lastIndexOf(".");
                return viewId.substring(0, idx + 1) + extensionMapping;
            }
        }
        return viewId;
    }

    private String getPath(ExternalContext externalContext)
    {
        String servletPath = externalContext.getRequestServletPath();
        String requestPathInfo = externalContext.getRequestPathInfo();

        WebXml webxml = WebXml.getWebXml(externalContext);
        List mappings = webxml.getFacesServletMappings();
        String path = null;
        boolean isExtensionMapping = requestPathInfo != null;
        for (int i = 0, size = mappings.size(); i < size; i++)
        {
            ServletMapping servletMapping = (ServletMapping) mappings.get(i);
            String urlpattern = servletMapping.getUrlPattern();
            if (urlpattern.endsWith("*"))
            {
                urlpattern = urlpattern.substring(0, urlpattern.length() - 1);
            }
            else if (urlpattern.startsWith("*"))
            {
                urlpattern = urlpattern.substring(1, urlpattern.length());
            }

            if (isExtensionMapping)
            {
                //extension mapping
                if (servletPath.endsWith(urlpattern))
                {
                    path = urlpattern;
                    break;
                }
            }
            else
            {
                //prefix mapping
                if (servletPath.equals(urlpattern))
                {
                    path = urlpattern;
                    break;
                }
            }
        }
        if (path == null)
        {
            log.error("could not find pathMapping for servletPath = " + servletPath +
                      " requestPathInfo = " + requestPathInfo);
        }
        return path;
    }

    public void renderView(FacesContext facesContext, UIViewRoot viewToRender) throws IOException, FacesException
    {
        if (viewToRender == null)
        {
            log.error("viewToRender must not be null");
            throw new NullPointerException("viewToRender must not be null");
        }

        // TODO: adapt
        ExternalContext externalContext = facesContext.getExternalContext();
        ServletRequest servletRequest = (ServletRequest)externalContext.getRequest();

        //Build component tree from parsed JspInfo so that all components
        //already exist in case a component needs it's children prior to
        //rendering it's body
        /*
        Tree staticTree = JspInfo.getTree(facesContext, tree.getTreeId());
        TreeCopier tc = new TreeCopier(facesContext);
        tc.setOverwriteComponents(false);
        tc.setOverwriteAttributes(false);
        tc.copyTree(staticTree, tree);
        */

        //Look for a StateRenderer and prepare for state saving
        RenderKitFactory rkFactory = (RenderKitFactory)FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = rkFactory.getRenderKit(viewToRender.getRenderKitId());
        Renderer renderer = null;
        try
        {
            renderer = renderKit.getRenderer(StateRenderer.TYPE);
        }
        catch (Exception e)
        {
            //No StateRenderer
        }
        if (renderer != null)
        {
            try
            {
                log.trace("StateRenderer found, calling encodeBegin.");
                renderer.encodeBegin(facesContext, null);
            }
            catch (IOException e)
            {
                throw new FacesException("Error saving state", e);
            }
        }

        //forward request to JSP page
        //ServletMappingFactory smf = MyFacesFactoryFinder.getServletMappingFactory(externalContext);
        //ServletMapping sm = smf.getServletMapping((ServletContext)externalContext.getContext());
        //String forwardURL = sm.mapViewIdToFilename((ServletContext)externalContext.getContext(),
        //                                           viewToRender.getViewId());

        //RequestDispatcher requestDispatcher
        //    = servletRequest.getRequestDispatcher(forwardURL);
        /*
        try
        {
            //requestDispatcher.forward(servletRequest,
                                      (ServletResponse)facesContext.getExternalContext().getResponse());
        }
        catch(IOException ioe)
        {
            log.error("IOException in method renderView of class " + this.getClass().getName(), ioe);
            throw new IOException(ioe.getMessage());
        }
        catch(ServletException se)
        {
            log.error("ServletException in method renderView of class " + this.getClass().getName(), se);
            throw new FacesException(se.getMessage(), se);
        }
        */

    }

    public UIViewRoot restoreView(FacesContext facescontext, String s)
    {
        // TODO: implement
        throw new UnsupportedOperationException("not yet implemented.");
    }

    public void writeState(FacesContext facescontext) throws IOException
    {
        // TODO: implement
        throw new UnsupportedOperationException("not yet implemented.");
    }
}
