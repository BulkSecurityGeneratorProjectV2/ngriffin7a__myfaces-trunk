/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.view.facelets.tag.jsf;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ValueExpression;
import javax.faces.FacesWrapper;
import javax.faces.application.Application;
import javax.faces.application.ProjectStage;
import javax.faces.component.ActionSource;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UniqueIdVendor;
import javax.faces.component.ValueHolder;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.validator.BeanValidator;
import javax.faces.validator.Validator;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRuleset;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;
import javax.faces.view.facelets.TagHandlerDelegate;

import org.apache.myfaces.util.ExternalSpecifications;
import org.apache.myfaces.view.facelets.AbstractFaceletContext;
import org.apache.myfaces.view.facelets.FaceletCompositionContext;
import org.apache.myfaces.view.facelets.tag.MetaRulesetImpl;
import org.apache.myfaces.view.facelets.tag.jsf.core.AjaxHandler;
import org.apache.myfaces.view.facelets.tag.jsf.core.FacetHandler;

/**
 *  
 * Implementation of the tag logic used in the JSF specification. 
 * 
 * @see org.apache.myfaces.view.facelets.tag.jsf.ComponentHandler
 * @author Leonardo Uribe (latest modification by $Author$)
 * @version $Revision$ $Date$
 *
 * @since 2.0
 */
public class ComponentTagHandlerDelegate extends TagHandlerDelegate
{
    //private final static Logger log = Logger.getLogger("facelets.tag.component");
    private final static Logger log = Logger.getLogger(ComponentTagHandlerDelegate.class.getName());

    private final ComponentHandler _delegate;

    private final String _componentType;

    private final TagAttribute _id;

    private final String _rendererType;
    
    private final ComponentBuilderHandler _componentBuilderHandlerDelegate;
    
    private final RelocatableResourceHandler _relocatableResourceHandler;

    @SuppressWarnings("unchecked")
    public ComponentTagHandlerDelegate(ComponentHandler delegate)
    {
        _delegate = delegate;
        
        ComponentHandler handler = _delegate;
        boolean found = false;
        while(handler != null && !found)
        {
            if (handler instanceof ComponentBuilderHandler)
            {
                found = true;
            }
            else if (handler instanceof FacesWrapper)
            {
                handler = ((FacesWrapper<? extends ComponentHandler>)handler).getWrapped();
            }
            else
            {
                handler = null;
            }
        }
        if (found)
        {
            _componentBuilderHandlerDelegate = (ComponentBuilderHandler) handler;
        }
        else
        {
            _componentBuilderHandlerDelegate = null;
        }
        
        //Check if this component is instance of RelocatableResourceHandler
        handler = _delegate;
        found = false;
        while(handler != null && !found)
        {
            if (handler instanceof RelocatableResourceHandler)
            {
                found = true;
            }
            else if (handler instanceof FacesWrapper)
            {
                handler = ((FacesWrapper<? extends ComponentHandler>)handler).getWrapped();
            }
            else
            {
                handler = null;
            }
        }
        if (found)
        {
            _relocatableResourceHandler = (RelocatableResourceHandler) handler;
        }
        else
        {
            _relocatableResourceHandler = null;
        }
        
        ComponentConfig delegateComponentConfig = delegate.getComponentConfig();
        _componentType = delegateComponentConfig.getComponentType();
        _rendererType = delegateComponentConfig.getRendererType();
        _id = delegate.getTagAttribute("id");
    }

    /**
     * Method handles UIComponent tree creation in accordance with the JSF 1.2 spec.
     * <ol>
     * <li>First determines this UIComponent's id by calling {@link #getId(FaceletContext) getId(FaceletContext)}.</li>
     * <li>Search the parent for an existing UIComponent of the id we just grabbed</li>
     * <li>If found, {@link #FaceletCompositionContext.markForDeletion(UIComponent) mark} its children for deletion.</li>
     * <li>If <i>not</i> found, call {@link #createComponent(FaceletContext) createComponent}.
     * <ol>
     * <li>Only here do we apply {@link TagHandler#setAttributes(FaceletCompositionContext, Object) attributes}</li>
     * <li>Set the UIComponent's id</li>
     * <li>Set the RendererType of this instance</li>
     * </ol>
     * </li>
     * <li>Now apply the nextHandler, passing the UIComponent we've created/found.</li>
     * <li>Now add the UIComponent to the passed parent</li>
     * <li>Lastly, if the UIComponent already existed (found), then {@link #finalizeForDeletion(FaceletCompositionContext, UIComponent) finalize}
     * for deletion.</li>
     * </ol>
     * 
     * @see javax.faces.view.facelets.FaceletHandler#apply(javax.faces.view.facelets.FaceletContext, javax.faces.component.UIComponent)
     * 
     * @throws TagException
     *             if the UIComponent parent is null
     */
    @SuppressWarnings("unchecked")
    @Override
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException
    {
        // make sure our parent is not null
        if (parent == null)
        {
            throw new TagException(_delegate.getTag(), "Parent UIComponent was null");
        }
        
        FacesContext facesContext = ctx.getFacesContext();

        // possible facet scoped
        String facetName = this.getFacetName(ctx, parent);

        // our id
        String id = ctx.generateUniqueId(_delegate.getTagId());

        // Cast to use UniqueIdVendor stuff
        FaceletCompositionContext mctx = (FaceletCompositionContext) FaceletCompositionContext.getCurrentInstance(ctx);
                
        // grab our component
        UIComponent c = null;
        //boolean componentFoundInserted = false;
        // MYFACES-2924 This optimization does not work as expected when component bindings are used.
        //if (mctx.isRefreshingTransientBuild())
        //{
            if (_relocatableResourceHandler != null)
            {
                c = _relocatableResourceHandler.findChildByTagId(ctx, parent, id);
            }
            else
            {
                c = ComponentSupport.findChildByTagId(parent, id); 
            }
    
            // Check if the component was relocated using
            // composite:insertChildren or composite:insertFacet
            /*
            if (c == null && UIComponent.isCompositeComponent(parent))
            {
                if (facetName == null)
                {
                    String targetClientId = (String) parent.getAttributes().get(InsertChildrenHandler.INSERT_CHILDREN_TARGET_ID);
                    if (targetClientId != null)
                    {
                        UIComponent targetComponent = parent.findComponent(targetClientId.substring(parent.getClientId().length()+1));
                        if (targetComponent != null)
                        {
                            c = ComponentSupport.findChildByTagId(targetComponent, id);
                        }
                    }
                    if (c != null)
                    {
                        c.getAttributes().put(InsertChildrenHandler.USES_INSERT_CHILDREN, Boolean.TRUE);
                        componentFoundInserted = true;
                    }
                }
                else
                {
                    String targetClientId = (String) parent.getAttributes().get(InsertFacetHandler.INSERT_FACET_TARGET_ID+facetName);
                    if (targetClientId != null)
                    {
                        UIComponent targetComponent = parent.findComponent(targetClientId.substring(parent.getClientId().length()+1));
                        if (targetComponent != null)
                        {
                            c = ComponentSupport.findChildByTagId(targetComponent, id);
                            if (c != null)
                            {
                                c.getAttributes().put(InsertFacetHandler.USES_INSERT_FACET, Boolean.TRUE);
                                componentFoundInserted = true;
                            }
                        }
                    }
                }
            }
            */
        //}
        boolean componentFound = false;
        if (c != null)
        {
            componentFound = true;
            // mark all children for cleaning
            if (log.isLoggable(Level.FINE))
            {
                log.fine(_delegate.getTag() + " Component[" + id + "] Found, marking children for cleanup");
            }
            mctx.markForDeletion(c);
        }
        else
        {
            c = this.createComponent(ctx);
            if (log.isLoggable(Level.FINE))
            {
                log.fine(_delegate.getTag() + " Component[" + id + "] Created: " + c.getClass().getName());
            }
            
            _delegate.setAttributes(ctx, c);

            // mark it owned by a facelet instance
            c.getAttributes().put(ComponentSupport.MARK_CREATED, id);

            if (facesContext.isProjectStage(ProjectStage.Development))
            {
                c.getAttributes().put(UIComponent.VIEW_LOCATION_KEY,
                        _delegate.getTag().getLocation());
            }

            // assign our unique id
            if (this._id != null)
            {
                c.setId(this._id.getValue(ctx));
            }
            else
            {
                UniqueIdVendor uniqueIdVendor = mctx.getUniqueIdVendorFromStack();
                if (uniqueIdVendor == null)
                {
                    uniqueIdVendor = facesContext.getViewRoot();
                    
                    if (uniqueIdVendor == null)
                    {
                        // facesContext.getViewRoot() returns null here if we are in
                        // phase restore view, so we have to try to get the view root
                        // via the method in ComponentSupport and our parent
                        uniqueIdVendor = ComponentSupport.getViewRoot(ctx, parent);
                    }
                }
                if (uniqueIdVendor != null)
                {
                    // UIViewRoot implements UniqueIdVendor, so there is no need to cast to UIViewRoot
                    // and call createUniqueId()
                    String uid = uniqueIdVendor.createUniqueId(facesContext, id);
                    c.setId(uid);
                }
            }

            if (this._rendererType != null)
            {
                c.setRendererType(this._rendererType);
            }

            // hook method
            _delegate.onComponentCreated(ctx, c, parent);
        }
        c.pushComponentToEL(facesContext, c);

        if (c instanceof UniqueIdVendor)
        {
            mctx.pushUniqueIdVendorToStack((UniqueIdVendor)c);
        }
        // first allow c to get populated
        _delegate.applyNextHandler(ctx, c);

        boolean oldProcessingEvents = facesContext.isProcessingEvents();
        // finish cleaning up orphaned children
        if (componentFound)
        {
            mctx.finalizeForDeletion(c);

            //if (!componentFoundInserted)
            //{
                if (mctx.isRefreshingTransientBuild())
                {
                    facesContext.setProcessingEvents(false); 
                }
                if (facetName == null)
                {
                    parent.getChildren().remove(c);
                }
                else
                {
                    ComponentSupport.removeFacet(ctx, parent, c, facetName);
                }
                if (mctx.isRefreshingTransientBuild())
                {
                    facesContext.setProcessingEvents(oldProcessingEvents);
                }
            //}
        }
        
        /*
        if (mctx.isRefreshingTransientBuild() && 
                UIComponent.isCompositeComponent(parent))
        {
            // Save the child structure behind this component, so it can be
            // used later by InsertChildrenHandler and InsertFacetHandler
            // to update components correctly.
            if (facetName != null)
            {
                if (parent.getAttributes().containsKey(InsertFacetHandler.INSERT_FACET_TARGET_ID+facetName))
                {
                    List<String> ordering = (List<String>) parent.getAttributes().get(
                            InsertFacetHandler.INSERT_FACET_ORDERING+facetName);
                    if (ordering == null)
                    {
                        ordering = new ArrayList<String>();
                        parent.getAttributes().put(InsertFacetHandler.INSERT_FACET_ORDERING+facetName, ordering);
                    }
                    ordering.remove(id);
                    ordering.add(id);
                }
            }
            else
            {
                if (parent.getAttributes().containsKey(InsertChildrenHandler.INSERT_CHILDREN_TARGET_ID))
                {
                    List<String> ordering = (List<String>) parent.getAttributes().get(
                            InsertChildrenHandler.INSERT_CHILDREN_ORDERING);
                    if (ordering == null)
                    {
                        ordering = new ArrayList<String>();
                        parent.getAttributes().put(InsertChildrenHandler.INSERT_CHILDREN_ORDERING, ordering);
                    }
                    ordering.remove(id);
                    ordering.add(id);
                }
            }
        }
        */

        if (!componentFound)
        {
            if (c instanceof ClientBehaviorHolder && !UIComponent.isCompositeComponent(c))
            {
                Iterator<AjaxHandler> it = ((AbstractFaceletContext) ctx).getAjaxHandlers();
                if (it != null)
                {
                    while(it.hasNext())
                    {
                        it.next().applyAttachedObject(facesContext, c);
                    }
                }
            }
            
            if (c instanceof EditableValueHolder)
            {
                // add default validators here, because this feature 
                // is only available in facelets (see MYFACES-2362 for details)
                addDefaultValidators(mctx, facesContext, (EditableValueHolder) c);
            }
        }
        
        _delegate.onComponentPopulated(ctx, c, parent);

        //if (!componentFoundInserted)
        //{
            // add to the tree afterwards
            // this allows children to determine if it's
            // been part of the tree or not yet
            if (componentFound && mctx.isRefreshingTransientBuild())
            {
                facesContext.setProcessingEvents(false); 
            }
            if (facetName == null)
            {
                parent.getChildren().add(c);
            }
            else
            {
                ComponentSupport.addFacet(ctx, parent, c, facetName);
            }
            if (componentFound && mctx.isRefreshingTransientBuild())
            {
                facesContext.setProcessingEvents(oldProcessingEvents);
            }
        //}
        /*
        else
        {
            if (facetName != null)
            {
                if (UIComponent.isCompositeComponent(parent))
                {
                    UIComponent facet = parent.getFacet(facetName);
                    if (Boolean.TRUE.equals(facet.getAttributes().get(ComponentSupport.FACET_CREATED_UIPANEL_MARKER)))
                    {
                        facet.getAttributes().put(InsertFacetHandler.USES_INSERT_FACET, Boolean.TRUE);
                    }
                }
            }
        }*/
        
        if (c instanceof UniqueIdVendor)
        {
            mctx.popUniqueIdVendorToStack();
        }

        c.popComponentFromEL(facesContext);
        
        if (mctx.isMarkInitialState())
        {
            //Call it only if we are using partial state saving
            c.markInitialState();
        }
    }
    
    /**
     * Return the Facet name we are scoped in, otherwise null
     * 
     * @param ctx
     * @return
     */
    protected final String getFacetName(FaceletContext ctx, UIComponent parent)
    {
        return (String) parent.getAttributes().get(FacetHandler.KEY);
    }

    /**
     * If the binding attribute was specified, use that in conjuction with our componentType String variable to call
     * createComponent on the Application, otherwise just pass the componentType String. <p /> If the binding was used,
     * then set the ValueExpression "binding" on the created UIComponent.
     * 
     * @see Application#createComponent(javax.faces.el.ValueBinding, javax.faces.context.FacesContext, java.lang.String)
     * @see Application#createComponent(java.lang.String)
     * @param ctx
     *            FaceletContext to use in creating a component
     * @return
     */
    protected UIComponent createComponent(FaceletContext ctx)
    {
        if (_componentBuilderHandlerDelegate != null)
        {
            // the call to Application.createComponent(FacesContext, Resource)
            // is delegated because we don't have here the required Resource instance
            return _componentBuilderHandlerDelegate.createComponent(ctx);
        }
        UIComponent c = null;
        FacesContext faces = ctx.getFacesContext();
        Application app = faces.getApplication();
        if (_delegate.getBinding() != null)
        {
            ValueExpression ve = _delegate.getBinding().getValueExpression(ctx, Object.class);
            if (this._rendererType == null)
            {
                c = app.createComponent(ve, faces, this._componentType);
            }
            else
            {
                c = app.createComponent(ve, faces, this._componentType, this._rendererType);
            }
            if (c != null)
            {
                c.setValueExpression("binding", ve);
            }
        }
        else
        {
            if (this._rendererType == null)
            {
                c = app.createComponent(this._componentType);
            }
            else
            {
                c = app.createComponent(faces, this._componentType, this._rendererType);
            }
        }
        return c;
    }

    /**
     * If the id TagAttribute was specified, get it's value, otherwise generate a unique id from our tagId.
     * 
     * @see TagAttribute#getValue(FaceletContext)
     * @param ctx
     *            FaceletContext to use
     * @return what should be a unique Id
     */
    protected String getId(FaceletContext ctx)
    {
        if (this._id != null)
        {
            return this._id.getValue(ctx);
        }
        return ctx.generateUniqueId(_delegate.getTagId());
    }

    @Override
    public MetaRuleset createMetaRuleset(Class type)
    {
        MetaRuleset m = new MetaRulesetImpl(_delegate.getTag(), type);
        // ignore standard component attributes
        m.ignore("binding").ignore("id");

        // add auto wiring for attributes
        m.addRule(ComponentRule.Instance);

        // if it's an ActionSource
        if (ActionSource.class.isAssignableFrom(type))
        {
            m.addRule(ActionSourceRule.Instance);
        }

        // if it's a ValueHolder
        if (ValueHolder.class.isAssignableFrom(type))
        {
            m.addRule(ValueHolderRule.Instance);

            // if it's an EditableValueHolder
            if (EditableValueHolder.class.isAssignableFrom(type))
            {
                m.ignore("submittedValue");
                m.ignore("valid");
                m.addRule(EditableValueHolderRule.Instance);
            }
        }
        
        return m;
    }
    
    /**
     * Add the default Validators to the component.
     * Also adds all validators specified by enclosing <f:validateBean> tags
     * (e.g. the BeanValidator if it is not a default validator).
     *
     * @param context The FacesContext.
     * @param mctx the AbstractFaceletContext
     * @param component The EditableValueHolder to which the validators should be added
     */
    private void addDefaultValidators(FaceletCompositionContext mctx, FacesContext context, 
                                      EditableValueHolder component)
    {
        // add all defaultValidators
        Map<String, String> defaultValidators = context.getApplication().getDefaultValidatorInfo();
        if (defaultValidators != null && defaultValidators.size() != 0)
        {
            for (Map.Entry<String, String> entry : defaultValidators.entrySet())
            {
                addDefaultValidator( mctx, context, component, entry.getKey(), entry.getValue());
            }
        }
        // add all enclosing validators
        Iterator<String> enclosingValidatorIds = mctx.getEnclosingValidatorIds();
        if (enclosingValidatorIds != null)
        {
            while (enclosingValidatorIds.hasNext())
            {
                String validatorId = enclosingValidatorIds.next();
                if (!defaultValidators.containsKey(validatorId))
                {
                    addDefaultValidator(mctx, context, component, validatorId, null);
                }
            }
        }
    }
    
    private void addDefaultValidator(FaceletCompositionContext mctx, FacesContext context, 
            EditableValueHolder component, String validatorId, String validatorClassName)
    {
        Validator enclosingValidator = null;
        
        if (validatorClassName == null)
        {
            // we have no class name for validators of enclosing <f:validateBean> tags
            // --> we have to create it to get the class name
            // note that normally we can use this instance later anyway!
            enclosingValidator = context.getApplication().createValidator(validatorId);
            validatorClassName = enclosingValidator.getClass().getName();
        }
        
        // check if the validator is already registered for the given component
        // this happens if <f:validateBean /> is nested inside the component on the view
        Validator validator = null;
        for (Validator v : component.getValidators())
        {
            if (v.getClass().getName().equals(validatorClassName))
            {
                // found
                validator = v;
                break;
            }
        }
        
        if (validator == null)
        {
            if (shouldAddDefaultValidator(mctx, context, component, validatorId))
            {
                if (enclosingValidator != null)
                {
                    // we can use the instance from before
                    validator = enclosingValidator;
                }
                else
                {
                    // create it
                    validator = context.getApplication().createValidator(validatorId);
                }
                // add the validator to the component
                component.addValidator(validator);
            }
            else
            {
                // we should not add the validator
                return;
            }
        }
        
        // special things to configure for a BeanValidator
        if (validator instanceof BeanValidator)
        {
            BeanValidator beanValidator = (BeanValidator) validator;
            
            // check the validationGroups
            String validationGroups =  beanValidator.getValidationGroups();
            if (validationGroups == null 
                    || validationGroups.matches(BeanValidator.EMPTY_VALIDATION_GROUPS_PATTERN))
            {
                // no validationGroups available
                // --> get the validationGroups from the stack
                String stackGroup = mctx.getFirstValidationGroupFromStack();
                if (stackGroup != null)
                {
                    validationGroups = stackGroup;
                }
                else
                {
                    // no validationGroups on the stack
                    // --> set the default validationGroup
                    validationGroups = javax.validation.groups.Default.class.getName();
                }
                beanValidator.setValidationGroups(validationGroups);
            }
        }
    }

    /**
     * Determine if the default Validator with the given validatorId should be added.
     *
     * @param validatorId The validatorId.
     * @param facesContext The FacesContext.
     * @param mctx the AbstractFaceletContext
     * @param component The EditableValueHolder to which the validator should be added.
     * @return true if the Validator should be added, false otherwise.
     */
    @SuppressWarnings("unchecked")
    private boolean shouldAddDefaultValidator(FaceletCompositionContext mctx,
                                              FacesContext facesContext,
                                              EditableValueHolder component, 
                                              String validatorId)
    {
        // check if the validatorId is on the exclusion list on the component
        List<String> exclusionList 
                = (List<String>) ((UIComponent) component).getAttributes()
                        .get(ValidatorTagHandlerDelegate.VALIDATOR_ID_EXCLUSION_LIST_KEY);
        if (exclusionList != null)
        {
            for (String excludedId : exclusionList)
            {
                if (excludedId.equals(validatorId))
                {
                    return false;
                }
            }
        }
        
        // check if the validatorId is on the exclusion list on the stack
        Iterator<String> it = mctx.getExcludedValidatorIds();
        if (it != null)
        {            
            while (it.hasNext())
            {
                String excludedId = it.next();
                if (excludedId.equals(validatorId))
                {
                    return false;
                }
            }
        }
        
        // Some extra rules are required for Bean Validation.
        if (validatorId.equals(BeanValidator.VALIDATOR_ID))
        {
            if (!ExternalSpecifications.isBeanValidationAvailable())
            {
                // the BeanValidator was added as a default-validator, but
                // bean validation is not available on the classpath.
                // --> log a warning about this scenario.
                log.log(Level.WARNING, "Bean validation is not available on the " +
                        "classpath, thus the BeanValidator will not be added for " +
                        "the component " + component);
                return false;
            }
        }

        // By default, all default validators should be added
        return true;
    }
}
