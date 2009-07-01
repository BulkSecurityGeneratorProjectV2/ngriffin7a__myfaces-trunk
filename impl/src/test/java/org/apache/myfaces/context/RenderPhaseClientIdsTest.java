/*
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.apache.myfaces.context;

import java.util.HashMap;
import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;

import org.apache.myfaces.context.servlet.FacesContextImpl;
import org.apache.shale.test.base.AbstractJsfTestCase;

/**
 * Testcases for the request parameter handling
 * and setter handing on the get and
 * set renderPhaseClientIds in the FacesContext class!
 *
 * @author Werner Punz(latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class RenderPhaseClientIdsTest extends AbstractJsfTestCase {

    public RenderPhaseClientIdsTest() {
        super("RenderPhaseClientIdsTest");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        FactoryFinder.setFactory(FactoryFinder.PARTIAL_VIEW_CONTEXT_FACTORY,
        "org.apache.myfaces.context.PartialViewContextFactoryImpl");        
    }
    
    /**
     * Empty String as request param
     * has to result in an empty list
     */
    public void testRequestParams1() {
        String empty = "    \n \t  ";
        Map<String, String> requestParamMap = new HashMap<String, String>();
        requestParamMap.put(PartialViewContext.PARTIAL_RENDER_PARAM_NAME, empty);
        ContextTestRequestWrapper wrapper = new ContextTestRequestWrapper(request, requestParamMap);

        FacesContext context = new FacesContextImpl(servletContext, wrapper, response);
        
        PartialViewContext pprContext = context.getPartialViewContext();

        assertTrue(pprContext.getRenderIds().isEmpty());
    }

    /**
     * no request param, has to result in an empty list
     */
    public void testRequestParams2() {
        Map<String, String> requestParamMap = new HashMap<String, String>();
        ContextTestRequestWrapper wrapper = new ContextTestRequestWrapper(request, requestParamMap);

        FacesContext context = new FacesContextImpl(servletContext, wrapper, response);
        
        PartialViewContext pprContext = context.getPartialViewContext();

        assertTrue(pprContext.getRenderIds().isEmpty());
    }

    /**
     * NO_PARTIAL_PHASE_CLIENT_IDS as request param, has to result in an empty list
     */
    public void testRequestParams4() {
        Map<String, String> requestParamMap = new HashMap<String, String>();
        requestParamMap.put(PartialViewContext.PARTIAL_RENDER_PARAM_NAME, 
                            PartialViewContext.NO_PARTIAL_PHASE_CLIENT_IDS);
        ContextTestRequestWrapper wrapper = new ContextTestRequestWrapper(request, requestParamMap);

        FacesContext context = new FacesContextImpl(servletContext, wrapper, response);
        
        PartialViewContext pprContext = context.getPartialViewContext();

        assertTrue(pprContext.getRenderIds().isEmpty());
    }

    /**
     * list with one element has to result in a list with one element
     */
    public void testRequestParams5() {
        String params = " view1:panel1:_component1  ";
        Map<String, String> requestParamMap = new HashMap<String, String>();
        requestParamMap.put(PartialViewContext.PARTIAL_RENDER_PARAM_NAME, params);
        ContextTestRequestWrapper wrapper = new ContextTestRequestWrapper(request, requestParamMap);

        FacesContext context = new FacesContextImpl(servletContext, wrapper, response);
        
        PartialViewContext pprContext = context.getPartialViewContext();

        assertTrue("Length must be one",pprContext.getRenderIds().size() == 1);
        assertTrue("Value match",pprContext.getRenderIds().iterator().next().equals("view1:panel1:_component1"));
    }

    /**
     * test on a full blown list containing various
     * blank chars
     */
    public void testRequestParams6() {
        String params = " view1:panel1:_component1,view1:panel1:_component2 \n , component3, component4  ";
        Map<String, String> requestParamMap = new HashMap<String, String>();
        requestParamMap.put(PartialViewContext.PARTIAL_RENDER_PARAM_NAME, params);
        ContextTestRequestWrapper wrapper = new ContextTestRequestWrapper(request, requestParamMap);

        FacesContext context = new FacesContextImpl(servletContext, wrapper, response);
        
        PartialViewContext pprContext = context.getPartialViewContext();

        assertTrue("Length must be four",pprContext.getRenderIds().size() == 4);

        // FIXME: Latest spec uses a Collection so order is not garanteed
//        assertTrue("Value match",pprContext.getRenderIds().get(0).equals("view1:panel1:_component1"));
//        assertTrue("Value match",pprContext.getRenderIds().get(2).equals("component3"));
//
//
//        assertTrue("Value match",pprContext.getRenderIds().get(3).equals("component4"));
    }
}