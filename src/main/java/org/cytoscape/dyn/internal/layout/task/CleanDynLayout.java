/*
 * DynNetwork plugin for Cytoscape 3.0 (http://www.cytoscape.org/).
 * Copyright (C) 2012 Sabina Sara Pfister
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.cytoscape.dyn.internal.layout.task;

import java.util.Set;

import org.cytoscape.dyn.internal.layout.model.DynLayoutFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

/**
 * <code> CleanDynLayout </code> instantiate the dynamic layout algorithm task 
 * {@link CleanDynLayoutTask}.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 * @param <C>
 */
public class CleanDynLayout<T,C> extends AbstractLayoutAlgorithm
{
    private final DynLayoutFactory<T> dynLaoutFactory;
    
    /**
     * <code> CleanDynLayout </code> constructor.
     * @param computerName
     * @param humanName
     * @param undoSupport
     * @param panel
     * @param dynLaoutFactory
     */
    public CleanDynLayout(
                    final String computerName, 
                    final String humanName,
                    final UndoSupport undoSupport,
                    final DynLayoutFactory<T> dynLaoutFactory)
    {
            super(computerName, humanName, undoSupport);
            this.dynLaoutFactory = dynLaoutFactory;
    }

    @Override
    public TaskIterator createTaskIterator(
                    CyNetworkView networkView,
                    Object layoutContext, 
                    Set<View<CyNode>> nodesToLayOut,
                    String layoutAttribute)
    {
    	dynLaoutFactory.removeLayout(networkView);
    	return new TaskIterator(new CleanDynLayoutTask(
        		getName(), networkView,nodesToLayOut, layoutAttribute, undoSupport));
    }

}
