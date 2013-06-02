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

import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.UndoSupport;

/**
 * <code> CleanDynLayoutTask </code> does nothing, but prevents the TaskIterator to
 * throw an exception.
 * 
 * @author Sabina Sara Pfister
 */
public class CleanDynLayoutTask extends AbstractLayoutTask
{

	/**
	 * <code> CleanDynLayoutTask </code> constructor.
	 * @param name
	 * @param networkView
	 * @param nodesToLayOut
	 * @param layoutAttribute
	 * @param undo
	 */
	public CleanDynLayoutTask(
			String name, 
			CyNetworkView networkView,
			Set<View<CyNode>> nodesToLayOut, 
			String layoutAttribute,
			UndoSupport undo) {
		super(name, networkView, nodesToLayOut, layoutAttribute, undo);
	}

	@Override
	protected void doLayout(TaskMonitor taskMonitor) 
	{
		// do nothing
	}

}
