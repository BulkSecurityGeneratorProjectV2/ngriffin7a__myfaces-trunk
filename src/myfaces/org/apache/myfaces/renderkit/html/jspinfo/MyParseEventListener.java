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
package net.sourceforge.myfaces.renderkit.html.jspinfo;

import net.sourceforge.myfaces.component.CommonComponentAttributes;
import net.sourceforge.myfaces.component.ext.UISaveState;
import net.sourceforge.myfaces.renderkit.html.jspinfo.jasper.Constants;
import net.sourceforge.myfaces.renderkit.html.jspinfo.jasper.JasperException;
import net.sourceforge.myfaces.renderkit.html.jspinfo.jasper.JspCompilationContext;
import net.sourceforge.myfaces.renderkit.html.jspinfo.jasper.compiler.*;
import net.sourceforge.myfaces.util.bean.BeanUtils;
import net.sourceforge.myfaces.util.logging.LogUtil;
import org.xml.sax.Attributes;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.webapp.FacesTag;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * DOCUMENT ME!
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class MyParseEventListener
        implements ParseEventListener
{
    private TagLibraries _tagLibraries = new MyTagLibraries();
    private JspTreeParser _parser;
    private JspCompilationContext _ctxt;
    private UIComponent _currentComponent;
    private JspInfo _jspInfo;

    public MyParseEventListener(JspTreeParser parser,
                                JspCompilationContext ctxt,
                                JspInfo jspInfo)
    {
        _parser = parser;
        _ctxt = ctxt;
        _currentComponent = jspInfo.getTree().getRoot();
        _jspInfo = jspInfo;
    }

    public void beginPageProcessing() throws JasperException
    {
        //System.out.println("MyParseEventListener.beginPageProcessing");
    }

    public void endPageProcessing() throws JasperException
    {
        //System.out.println("MyParseEventListener.endPageProcessing");
    }

    /*
     * Custom tag support
     */
    public TagLibraries getTagLibraries()
    {
        //System.out.println("MyParseEventListener.getTagLibraries");
        return _tagLibraries;
    }

    public void handleBean(Mark start, Mark stop, Attributes attrs)
            throws JasperException
    {
        handleBean(attrs);
    }

    public void handleBean(Mark start, Mark stop, Attributes attrs, boolean isXml)
            throws JasperException
    {
        handleBean(attrs);
    }

    public void handleBeanEnd(Mark start, Mark stop, Attributes attrs)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handleBeanEnd");
    }

    public void handleCharData(Mark start, Mark stop, char[] chars)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handleCharData");
    }

    public void handleComment(Mark start, Mark stop, char[] text) throws JasperException
    {
        //System.out.println("MyParseEventListener.handleComment");
    }

    public void handleDeclaration(Mark start, Mark stop, Attributes attrs, char[] text)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handleDeclaration");
    }

    public void handleDirective(String directive,
                                Mark start, Mark stop,
                                Attributes attrs)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handleDirective");

        if (directive.equals("taglib"))
        {
            String uri = attrs.getValue("uri");
            String prefix = attrs.getValue("prefix");

            TagLibraryInfo tl = null;

            /*
            String[] location = _ctxt.getTldLocation(uri);
            if (location == null)
            {
            */
                tl = new TagLibraryInfoImpl(_ctxt, prefix, uri);
            /*
            }
            else
            {
                tl = new TagLibraryInfoImpl(_ctxt, prefix, uri, location);
            }
            */
            _tagLibraries.addTagLibrary(prefix, tl);
        }

        else if (directive.equals("include"))
        {
            String file = attrs.getValue("file");
            if (file == null)
            {
                throw new CompileException(start,
                                           Constants.getString("jsp.error.include.missing.file"));
            }

            _parser.parseFile(file);
        }

    }

    public void handleExpression(Mark start, Mark stop, Attributes attrs, char[] text)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handleExpression");
    }

    public void handleForward(Mark start, Mark stop, Attributes attrs, Hashtable param)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handleForward");
    }

    public void handleForward(Mark start, Mark stop, Attributes attrs, Hashtable param, boolean isXml)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handleForward");
    }

    public void handleGetProperty(Mark start, Mark stop, Attributes attrs)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handleGetProperty");
    }

    public void handleInclude(Mark start, Mark stop, Attributes attrs, Hashtable param)
            throws JasperException
    {
        System.out.println("MyParseEventListener.handleInclude");
    }

    public void handleInclude(Mark start, Mark stop, Attributes attrs, Hashtable param, boolean isXml)
            throws JasperException
    {
        System.out.println("MyParseEventListener.handleInclude");
    }

    public void handleJspCdata(Mark start, Mark stop, char[] data)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handleJspCdata");
    }

    public void handlePlugin(Mark start, Mark stop, Attributes attrs, Hashtable param,
                             String fallback)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handlePlugin");
    }

    public void handlePlugin(Mark start, Mark stop, Attributes attrs, Hashtable param,
                             String fallback, boolean isXml)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handlePlugin");
    }

    public void handleRootBegin(Attributes attrs) throws JasperException
    {
        //System.out.println("MyParseEventListener.handleRootBegin");
    }

    public void handleRootEnd()
    {
        //System.out.println("MyParseEventListener.handleRootEnd");
    }

    public void handleScriptlet(Mark start, Mark stop, Attributes attrs, char[] text)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handleScriptlet");
    }

    public void handleSetProperty(Mark start, Mark stop, Attributes attrs)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handleSetProperty");
    }

    public void handleSetProperty(Mark start, Mark stop, Attributes attrs,
                                  boolean isXml)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handleSetProperty");
    }

    /*
     * start: is either the start position at "<" if content type is JSP or empty, or
     *        is the start of the body after the "/>" if content type is tag dependent
     * stop: can be null if the body contained JSP tags...
     */
    public void handleTagBegin(Mark start, Mark stop, Attributes attrs, String prefix, String shortTagName,
                               TagLibraryInfo tli, TagInfo ti, boolean hasBody)
            throws JasperException
    {
        handleTagBegin(prefix, shortTagName, attrs, tli, ti);
    }

    public void handleTagBegin(Mark start, Mark stop, Attributes attrs, String prefix, String shortTagName,
                               TagLibraryInfo tli, TagInfo ti, boolean hasBody, boolean isXml)
            throws JasperException
    {
        handleTagBegin(prefix, shortTagName, attrs, tli, ti);
    }

    public void handleTagEnd(Mark start, Mark stop, String prefix, String shortTagName,
                             Attributes attrs, TagLibraryInfo tli, TagInfo ti, boolean hasBody)
            throws JasperException
    {
        handleTagEnd(prefix, shortTagName, attrs, tli, ti);
    }

    public void handleUninterpretedTagBegin(Mark start, Mark stop,
                                            String rawName, Attributes attrs)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handleUninterpretedTagBegin");
    }

    public void handleUninterpretedTagEnd(Mark start, Mark stop,
                                          String rawName, char[] data)
            throws JasperException
    {
        //System.out.println("MyParseEventListener.handleUninterpretedTagEnd");
    }

    public void setDefault(boolean b)
    {
        //System.out.println("MyParseEventListener.setDefault");
    }

    public void setReader(JspReader reader)
    {
        //System.out.println("MyParseEventListener.setReader");
    }

    public void setTemplateInfo(Mark start, Mark stop)
    {
        //System.out.println("MyParseEventListener.setTemplateInfo");
    }



    private HashMap _tagClasses = new HashMap();
    private static final Object NULL_DUMMY = new Object();
    private FacesTag getFacesTag(TagInfo ti)
    {
        Object obj = _tagClasses.get(ti.getTagClassName());
        if (obj == NULL_DUMMY)
        {
            return null;
        }
        else if (obj != null)
        {
            return (FacesTag)obj;
        }

        Class c;
        try
        {
            c = Class.forName(ti.getTagClassName());
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("Class for tag " + ti.getTagName() + " not found!", e);
        }

        if (!FacesTag.class.isAssignableFrom(c))
        {
            LogUtil.getLogger().fine("Not a FacesTag.");
            _tagClasses.put(ti.getTagClassName(), NULL_DUMMY);
            return null;
        }

        try
        {
            obj = c.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }

        _tagClasses.put(ti.getTagClassName(), obj);
        return (FacesTag)obj;
    }


    private void handleTagBegin(String prefix, String shortTagName,
                                Attributes attrs, TagLibraryInfo tli, TagInfo ti)
    {
        FacesTag tag = getFacesTag(ti);
        if (tag != null)
        {
            String id = null;

            BeanInfo beanInfo = BeanUtils.getBeanInfo(tag);

            TagAttributeInfo[] attrInfos = ti.getAttributes();
            for (int i = 0; i < attrInfos.length; i++)
            {
                TagAttributeInfo attrInfo = attrInfos[i];
                String attrName = attrInfo.getName();
                Object attrValue = attrs.getValue(attrName);

                if (attrValue != null)
                {
                    if (attrInfo.canBeRequestTime() &&
                        ((String)attrValue).trim().startsWith("<%"))
                    {
                        //Request time value --> ignore
                        continue;
                    }

                    if (attrName.equals(CommonComponentAttributes.ID_ATTR))
                    {
                        id = (String)attrValue;
                    }
                    else
                    {
                        PropertyDescriptor propDescr = BeanUtils.findPropertyDescriptor(beanInfo, attrName);
                        if (propDescr == null)
                        {
                            throw new RuntimeException("No PropertyDescriptor found for tag property " + attrName);
                        }

                        if (attrValue instanceof String)
                        {
                            if (attrInfo.getTypeName() != null)
                            {
                                Class type = null;
                                try
                                {
                                    type = Class.forName(attrInfo.getTypeName());
                                }
                                catch (ClassNotFoundException e)
                                {
                                    throw new RuntimeException(e);
                                }
                                attrValue = convertStringToTargetType((String)attrValue, type);
                            }
                            else
                            {
                                attrValue = convertStringToTargetType(propDescr,
                                                                      (String)attrValue);
                            }
                        }
                        BeanUtils.setBeanPropertyValue(tag, propDescr, attrValue);
                    }
                }
            }

            UIComponent comp = tag.createComponent(); //Tag is instanceof FacesTag
            if (comp == null)
            {
                LogUtil.getLogger().warning("Tag class " + tag.getClass().getName() + " did not create a component.");
                //We set current component to a dummy, so that the
                //getParent in handleEndTag returns the right component:
                final UIComponent currComp = _currentComponent;
                _currentComponent = new UIComponentBase() {
                    public String getComponentType() {return "DUMMY";}
                    public UIComponent getParent() {return currComp;}
                };
                tag.release();
                return;
            }

            if (id != null)
            {
                comp.setComponentId(id);
            }
            else
            {
                LogUtil.getLogger().severe("Missing component id.");
            }

            String rendererType = tag.getRendererType();
            if (rendererType != null)
            {
                comp.setRendererType(rendererType);
            }

            overrideProperties(tag, comp);
            tag.release(); //TODO: Do we have to call it really?

            _currentComponent.addChild(comp);
            _currentComponent = comp;

            /*
            String compoundId = comp.getCompoundId();   //TODO: We need a context independent getClientId() method
            if (compoundId != null)
            {
                _jspInfo.setCreatorTag(compoundId, tag);
            }
            */
            comp.setAttribute(JspInfo.CREATOR_TAG_ATTR, tag);

            if (comp.getComponentType().equals(UISaveState.TYPE))
            {
                _jspInfo.addUISaveStateComponent(comp);
            }
        }
    }


    /**
     * Hack to access the protected FacesTag method "overrideProperties".
     * @param tag
     * @param comp
     */
    private void overrideProperties(Object tag, UIComponent comp)
    {
        try
        {
            Method m = null;
            Class c = tag.getClass();
            while (m == null && c != null && !c.equals(Object.class))
            {
                try
                {
                    m = c.getDeclaredMethod("overrideProperties",
                                            new Class[] {UIComponent.class});
                }
                catch (NoSuchMethodException e) {}
                c = c.getSuperclass();
            }

            if (m == null)
            {
                throw new NoSuchMethodException();
            }

            if (m.isAccessible())
            {
                m.invoke(tag, new Object[] {comp});
            }
            else
            {
                final Method finalM = m;
                AccessController.doPrivileged(
                    new PrivilegedAction()
                    {
                        public Object run()
                        {
                            finalM.setAccessible(true);
                            return null;
                        }
                    });
                m.invoke(tag, new Object[]{comp});
                m.setAccessible(false);
            }
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }


    private Object convertStringToTargetType(PropertyDescriptor propertyDescriptor,
                                             String propertyStringValue)
    {
        Class propertyEditorClass = propertyDescriptor.getPropertyEditorClass();
        if (propertyEditorClass != null)
        {
            PropertyEditor pe = null;
            try
            {
                pe = (PropertyEditor)propertyEditorClass.newInstance();
            }
            catch (InstantiationException e)
            {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
            pe.setAsText(propertyStringValue);
            return pe.getValue();
        }

        return convertStringToTargetType(propertyStringValue,
                                         propertyDescriptor.getPropertyType());
    }


    private Object convertStringToTargetType(String s,
                                             Class targetClass)
    {
        if (String.class.isAssignableFrom(targetClass))
        {
            //already a String
            return s;
        }
        else if (targetClass.equals(Boolean.TYPE) ||
                 targetClass.equals(Boolean.class))
        {
            return Boolean.valueOf(s);
        }
        else if (targetClass.equals(Byte.TYPE) ||
                 targetClass.equals(Byte.class))
        {
            return Byte.valueOf(s);
        }
        else if (targetClass.equals(Character.TYPE) ||
                 targetClass.equals(Character.class))
        {
            return s.length() > 0 ? new Character(s.charAt(0)) : null;
        }
        else if (targetClass.equals(Double.TYPE) ||
                 targetClass.equals(Double.class))
        {
            return Double.valueOf(s);
        }
        else if (targetClass.equals(Integer.TYPE) ||
                 targetClass.equals(Integer.class))
        {
            return Integer.valueOf(s);
        }
        else if (targetClass.equals(Float.TYPE) ||
                 targetClass.equals(Float.class))
        {
            return Float.valueOf(s);
        }
        else if (targetClass.equals(Long.TYPE) ||
                 targetClass.equals(Long.class))
        {
            return Long.valueOf(s);
        }
        else if (targetClass.equals(Short.TYPE) ||
                 targetClass.equals(Short.class))
        {
            return Short.valueOf(s);
        }
        else if (targetClass.getName().equals("java.lang.Object"))
        {
            return s;
        }
        else
        {
            LogUtil.getLogger().severe("Could not convert String '" + s + "' to target type " + targetClass.getName());
            return s;
        }
    }


    private void handleTagEnd(String prefix, String shortTagName,
                              Attributes attrs, TagLibraryInfo tli, TagInfo ti)
    {
        if (getFacesTag(ti) != null)
        {
            _currentComponent = _currentComponent.getParent();
        }
    }

    private void handleBean(Attributes attrs)
    {
        String id = attrs.getValue("id");
        if (id == null)
        {
            throw new IllegalArgumentException("No id attribute!");
        }

        String className = attrs.getValue("class");
        String beanName = attrs.getValue("beanName");

        int scope = PageContext.PAGE_SCOPE; //Default as stated in JSP Spec.
        String scopeValue = attrs.getValue("scope");
        if (scopeValue != null)
        {
            scopeValue = scopeValue.trim();
            if (scopeValue.equalsIgnoreCase("page"))
            {
                scope = PageContext.PAGE_SCOPE;
            }
            else if (scopeValue.equalsIgnoreCase("request"))
            {
                scope = PageContext.REQUEST_SCOPE;
            }
            else if (scopeValue.equalsIgnoreCase("session"))
            {
                scope = PageContext.SESSION_SCOPE;
            }
            else if (scopeValue.equalsIgnoreCase("application"))
            {
                scope = PageContext.APPLICATION_SCOPE;
            }
            else
            {
                LogUtil.getLogger().warning("Illegal scope attribute '" + scopeValue + "'. Assuming page scope.");
            }
        }

        //We ignore the scope for convenience, although at the moment it is not
        //necessary to store the type of application or session scope beans.
        _jspInfo.setJspBeanInfo(id, new JspBeanInfo(id,
                                                    className,
                                                    beanName,
                                                    scope));
    }


}
