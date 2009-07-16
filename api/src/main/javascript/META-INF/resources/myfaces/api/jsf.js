/*
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

/**
 *MyFaces core javascripting libraries
 *
 *  Those are the central public API functions in the JSF2
 *  Ajax API! They handle the entire form submit and ajax send
 *  and resolve cycle!
 */

/**
 * reserve the root namespace
 */
if ('undefined' != typeof OpenAjax && ('undefined' == typeof jsf || null == typeof jsf)) {
    OpenAjax.hub.registerLibrary("jsf", "www.sun.com", "1.0", null);
}
//just in case openajax has failed (testing environment)
//under normal circumstances this should not happen
if ('undefined' == typeof jsf || null == jsf) {
    jsf = new Object();
}

/**
 * just to make sure no questions arise, I simply prefer here a weak
 * typeless comparison just in case some frameworks try to interfere
 * by overriding null or fiddeling around with undefined or typeof in some ways
 * it is safer in this case than the standard way of doing a strong comparison
 **/
if ('undefined' == typeof jsf.ajax || null == jsf.ajax) {
    jsf.ajax = new Object();

     jsf.ajax._impl = myfaces._impl._util._Utils.getGlobalConfig("jsfAjaxImpl", myfaces.ajax);

    /**
     * collect and encode data for a given form element (must be of type form)
     * find the javax.faces.ViewState element and encode its value as well!
     * return a concatenated string of the encoded values!
     *
     * @throws an exception in case of the given element not being of type form!
     * https://issues.apache.org/jira/browse/MYFACES-2110
     */
    jsf.getViewState = function(formElement) {
        return jsf.ajax._impl.getViewState(formElement);
    };

    /**
     * this function has to send the ajax requests
     *
     * following request conditions must be met:
     * <ul>
     *  <li> the request must be sent asynchronously! </li>
     *  <li> the request must be a POST!!! request </li>
     *  <li> the request url must be the form action attribute </li>
     *  <li> all requests must be queued with a client side request queue to ensure the request ordering!</li>
     * </ul>
     *
     * @param {String|Node} element: any dom element no matter being it html or jsf, from which the event is emitted
     * @param {|EVENT|} event: any javascript event supported by that object
     * @param {Map||} options : map of options being pushed into the ajax cycle
     */
    jsf.ajax.request = function( element,  event,  options) {
        return jsf.ajax._impl.request(element, event, options);
    };

    jsf.ajax.addOnError = function(/*function*/errorListener) {
        return jsf.ajax._impl.addOnError(errorListener);
    }

    jsf.ajax.addOnEvent = function(/*function*/eventListener) {
        return jsf.ajax._impl.addOnEvent(eventListener);
    }

    /**
     * processes the ajax response if the ajax request completes successfully
     * @param request the ajax request!
     * @param context the ajax context!
     */
    jsf.ajax.response = function(/*xhr request object*/request, context) {
        jsf.ajax._impl.response(request, context);
    };
    /**
     * @return the current project state emitted by the server side method:
     * javax.faces.application.Application.getProjectStage()
     */
    jsf.getProjectStage = function() {
        return jsf.ajax._impl.getProjectStage();
    };
}

if ('undefined' == typeof jsf.util || null == jsf.util) {
    jsf.util = new Object();

    /**
     * varargs function which executes a chain of code (functions or any other code)
     *
     * if any of the code returns false, the execution
     * is terminated prematurely skipping the rest of the code!
     *
     * @param {DomNode} source, the callee object
     * @param {Event} event, the event object of the callee event triggering this function
     * 
     */
    jsf.util.chain = function(source, event) {
        jsf.ajax._impl.chain.apply(jsf.ajax._impl, arguments);
    }

}

