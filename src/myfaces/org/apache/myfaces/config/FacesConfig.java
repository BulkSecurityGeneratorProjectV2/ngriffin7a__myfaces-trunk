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
package net.sourceforge.myfaces.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;


/**
 * DOCUMENT ME!
 * @author Manfred Geiler (latest modification by $Author$)
 * @author Anton Koinov
 * @author Thomas Spiegl
 * @version $Revision$ $Date$
 */
public class FacesConfig
    implements Config
{
    private static final Log log = LogFactory.getLog(FacesConfig.class);

    private FactoryConfig _factoryConfig;
    private LifecycleConfig _lifecycleConfig;

    // Application specific
    private ApplicationConfig _applicationConfig;
    private final Map _converterMap = new HashMap();
    private final Map _converterTypeMap = new HashMap();
    private final Map _componentClassMap = new HashMap();
    private final Map _validatorClassMap = new HashMap();

    private final Map _managedBeanConfigMap = new HashMap();
    private final List _navigationRuleConfigList = new ArrayList();
    private final Map _referencedBeanConfigMap = new HashMap();
    private final Map _renderKitConfigMap = new HashMap();


    public ApplicationConfig getApplicationConfig()
    {
        return _applicationConfig;
    }

    /**
     * Replaces the current ApplicationConfig by a new one.
     * @param applicationConfig
     */
    public void setApplicationConfig(ApplicationConfig applicationConfig)
    {
        _applicationConfig = applicationConfig;
    }

    /**
     * Other than in {@link #setApplicationConfig} the current ApplicationConfig
     * is not replaced, but rather all non-null properties of the given
     * ApplicationConfig are copied to the current ApplicationConfig.
     * @param applicationConfig
     */
    public void addApplicationConfig(ApplicationConfig applicationConfig)
    {
        if (_applicationConfig == null)
        {
            _applicationConfig = applicationConfig;
        }
        else
        {
            _applicationConfig.update(applicationConfig);
        }
    }

    public void addConverterConfig(ConverterConfig converterConfig)
    {
        if (converterConfig.getConverterId() != null)
        {
            _converterMap.put(converterConfig.getConverterId(), converterConfig.getConverterClass());
        }
        else
        {
            _converterTypeMap.put(converterConfig.getConverterForClass(), converterConfig.getConverterClass());
        }
    }

    public Map getConverterMap()
    {
        return _converterMap == null ? Collections.EMPTY_MAP : _converterMap;
    }

    public Map getConverterTypeMap()
    {
        return _converterTypeMap == null ? Collections.EMPTY_MAP : _converterTypeMap;
    }

    public void addComponentConfig(ComponentConfig componentConfig)
    {
        _componentClassMap.put(componentConfig.getComponentType(),
                               componentConfig.getComponentClass());
    }

    public Map getComponentClassMap()
    {
        return _componentClassMap == null ? Collections.EMPTY_MAP : _componentClassMap;
    }

    public void addValidatorConfig(ValidatorConfig validatorConfig)
    {
        addValidator(validatorConfig.getValidatorId(),
                     validatorConfig.getValidatorClass());
    }

    public void addValidator(String validatorId, String validatorClass)
    {
        _validatorClassMap.put(validatorId, validatorClass);
    }

    public Map getValidatorClassMap()
    {
        return _validatorClassMap == null ? Collections.EMPTY_MAP : _validatorClassMap;
    }

    public void addManagedBeanConfig(ManagedBeanConfig managedBeanConfig)
    {
        getManagedBeanConfigMap().put(managedBeanConfig.getManagedBeanName(), managedBeanConfig);
    }

    public ManagedBeanConfig getManagedBeanConfig(String managedBeanName)
    {
        return (ManagedBeanConfig)getManagedBeanConfigMap().get(managedBeanName);
    }

    private Map getManagedBeanConfigMap()
    {
        return _managedBeanConfigMap;
    }



    public void addNavigationRuleConfig(NavigationRuleConfig navigationRuleConfig)
    {
        _navigationRuleConfigList.add(navigationRuleConfig);
    }

    public List getNavigationRuleConfigList()
    {
        return _navigationRuleConfigList == null
                ? Collections.EMPTY_LIST
                : _navigationRuleConfigList;
    }



    public void addReferencedBeanConfig(ReferencedBeanConfig referencedBeanConfig)
    {
        getReferencedBeanConfigMap().put(referencedBeanConfig.getReferencedBeanName(),
                                         referencedBeanConfig);
    }

    public ReferencedBeanConfig getReferencedBeanConfig(String referencedBeanName)
    {
        return (ReferencedBeanConfig)getReferencedBeanConfigMap().get(referencedBeanName);
    }

    public Iterator getReferencedBeanNames()
    {
        return getReferencedBeanConfigMap().keySet().iterator();
    }

    private Map getReferencedBeanConfigMap()
    {
        return _referencedBeanConfigMap;
    }



    public void addRenderKitConfig(RenderKitConfig newRKC)
    {
        RenderKitConfig oldRKC = getRenderKitConfig(newRKC.getRenderKitId());
        if (oldRKC == null)
        {
            getRenderKitConfigMap().put(newRKC.getRenderKitId(),
                                        newRKC);
        }
        else
        {
            //merge RenderKitConfigs

            //check consistence
            if (oldRKC.getRenderKitClass() != null &&
                newRKC.getRenderKitClass() != null &&
                oldRKC.getRenderKitClass().equals(newRKC.getRenderKitClass()))
            {
                log.warn("RenderKit '" + newRKC.getRenderKitId() + "' defined twice with different classes!");
            }
            else if (oldRKC.getRenderKitClass() == null)
            {
                oldRKC.setRenderKitClass(newRKC.getRenderKitClass());
            }

            for (Iterator it = newRKC.getRendererTypes(); it.hasNext(); )
            {
                RendererConfig rc = newRKC.getRendererConfig((String)it.next());
                oldRKC.addRendererConfig(rc);
            }
        }
    }

    public RenderKitConfig getRenderKitConfig(String renderKitId)
    {
        return (RenderKitConfig)getRenderKitConfigMap().get(renderKitId);
    }

    public Iterator getRenderKitIds()
    {
        return getRenderKitConfigMap().keySet().iterator();
    }

    public Iterator getRenderKitConfigs()
    {
        return getRenderKitConfigMap().values().iterator();
    }

    private Map getRenderKitConfigMap()
    {
        return _renderKitConfigMap;
    }

    public FactoryConfig getFactoryConfig()
    {
        return _factoryConfig;
    }
    
    public void setFactoryConfig(FactoryConfig factoryConfig)
    {
        if (_factoryConfig == null)
        {
            _factoryConfig = factoryConfig;
        }
        else
        {
            _factoryConfig.update(factoryConfig);
        }
    }
    
    public LifecycleConfig getLifecycleConfig()
    {
        return _lifecycleConfig;
    }

    public void addLifecycleConfig(LifecycleConfig lifecycleConfig)
    {
        if (_lifecycleConfig == null)
        {
            _lifecycleConfig = lifecycleConfig;
        }
        else
        {
            _lifecycleConfig.addPhaseListenerClasses(lifecycleConfig.getPhaseListenerClasses());
        }
    }
}
