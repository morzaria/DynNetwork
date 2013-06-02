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

package org.cytoscape.dyn.internal.task.select;

import org.cytoscape.dyn.internal.model.DynNetworkManager;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.task.select.SelectAllNodesTaskFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

/**
 * <code> SelectAllVisibleNodesTaskFactoryImpl </code> implements the factory for the task responsible to select 
 * visible nodes.
 * 
 * @author Sabina Sara Pfister
 *
 */
public class SelectAllVisibleNodesTaskFactoryImpl<T> extends AbstractNetworkTaskFactory implements SelectAllNodesTaskFactory
{
	private final UndoSupport undoSupport;
	private CyNetworkViewManager networkViewManager;
	private final CyEventHelper eventHelper;
	
	private final DynNetworkManager<T> dynNetworkManager;
	
	private final double start;
	private final double end;

	/**
	 * <code> SelectAllVisibleNodesTaskFactoryImpl </code> constructor.
	 * @param undoSupport
	 * @param networkViewManager
	 * @param dynNetworkManager
	 * @param eventHelper
	 * @param start
	 * @param end
	 */
	public SelectAllVisibleNodesTaskFactoryImpl(
			final UndoSupport undoSupport,
	        final CyNetworkViewManager networkViewManager,
	        final DynNetworkManager<T> dynNetworkManager,
	        final CyEventHelper eventHelper,
	        final double start,
	        final double end)
	{
		this.undoSupport        = undoSupport;
		this.networkViewManager = networkViewManager;
		this.eventHelper        = eventHelper;
		this.dynNetworkManager  = dynNetworkManager;
		this.start              = start;
		this.end                = end;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetwork network) 
	{
		return new TaskIterator(
				new SelectAllVisibleNodesTask<T>(undoSupport, network,dynNetworkManager.getDynNetwork(network),networkViewManager, eventHelper, start, end));
	}
	
}
