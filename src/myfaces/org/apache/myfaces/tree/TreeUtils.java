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
package net.sourceforge.myfaces.tree;

import javax.faces.component.UIComponent;
import javax.faces.tree.Tree;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * TODO: description
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class TreeUtils
{
    private TreeUtils() {}

    public static UIComponent findComponentById(Tree tree, String componentId)
    {
        return findComponentById(tree.getRoot(), componentId);
    }

    public static UIComponent findComponentById(UIComponent root, String componentId)
    {
        if (root == null)
        {
            return null;
        }
        if (root.getComponentId().equals(componentId))
        {
            return root;
        }
        for (Iterator it = root.getChildren(); it.hasNext();)
        {
            //Recursion:
            UIComponent find = findComponentById((UIComponent)it.next(),
                                                 componentId);
            if (find != null)
            {
                return find;
            }
        }
        return null;
    }


    public static Iterator treeIterator(Tree tree)
    {
        return new TreeIterator(tree);
    }

    private static class TreeIterator
        implements Iterator
    {
        private UIComponent _next = null;
        private boolean _mayHaveNext = true;
        private UIComponent _current = null;
        private Stack _stack = new Stack();

        public TreeIterator(Tree tree)
        {
            _next = tree.getRoot();
            _current = null;
        }

        public boolean hasNext()
        {
            return getNext() != null;
        }

        public Object next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            _current = _next;
            _next = null;
            return _current;
        }

        private UIComponent getNext()
        {
            if (_next == null && _mayHaveNext)
            {
                //has child?
                if (_current.getChildCount() > 0)
                {
                    Iterator children = _current.getChildren();
                    _next = (UIComponent)children.next();
                    //push siblings
                    _stack.push(children);
                }
                else
                {
                    //has next sibling?
                    for (;;)
                    {
                        if (_stack.empty())
                        {
                            _next = null;
                            _mayHaveNext = false;
                            break;
                        }

                        Iterator currentSiblings = (Iterator)_stack.peek();
                        if (currentSiblings.hasNext())
                        {
                            _next = (UIComponent)currentSiblings.next();
                            break;
                        }
                        else
                        {
                            _stack.pop();
                        }
                    }
                }
            }
            return _next;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

}
