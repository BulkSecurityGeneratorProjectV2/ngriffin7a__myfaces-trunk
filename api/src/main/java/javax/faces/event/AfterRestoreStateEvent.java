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

package javax.faces.event;

import javax.faces.component.UIComponent;

/**
 * Event issued after the components state has been restored!
 * 
 * @author Jan-Kees van Andel (latest modification by $Author$)
 * @since 2.0
 * @version $Revision$ $Date$
 */
public class AfterRestoreStateEvent extends ComponentSystemEvent {
    /**
     * Constructor
     * @param component issuing component
     * @throws NullPointerException in case of the component being null
     */
    public AfterRestoreStateEvent(UIComponent component) {
        super(component);
    }

    public void setComponent(UIComponent newComponent) {
        super.source = newComponent;
    }

}