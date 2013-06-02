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

import java.util.Collection;

import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalDouble;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.UndoSupport;

/**
 * <code> SelectAllVisibleEdgesTask </code> implements the task for selecting visible edges.
 * 
 * @author Sabina Sara Pfister
 *
 */
public class SelectAllVisibleEdgesTask<T> extends AbstractSelectTask<T> 
{
	private final UndoSupport undoSupport;
	
	private double start;
	private double end;

	/**
	 * <code> SelectAllVisibleEdgesTask </code> constructor.
	 * @param undoSupport
	 * @param net
	 * @param dynNet
	 * @param networkViewManager
	 * @param eventHelper
	 * @param start
	 * @param end
	 */
	public SelectAllVisibleEdgesTask(
			final UndoSupport undoSupport, 
			final CyNetwork net,
			final DynNetwork<T> dynNet,
	        final CyNetworkViewManager networkViewManager,
	        final CyEventHelper eventHelper,
	        final double start,
			final double end)
	{
		super(net, dynNet, networkViewManager, eventHelper);
		this.undoSupport = undoSupport;
		this.start = start;
		this.end = end;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public void run(TaskMonitor tm) throws Exception 
	{
		tm.setProgress(0.0);
		final Collection<CyNetworkView> views = networkViewManager.getNetworkViews(network);
		CyNetworkView view = null;
		if(views.size() != 0)
			view = views.iterator().next();
		
		undoSupport.postEdit(
			new SelectionEdit(eventHelper, "Select All Visible Edges", network, view,
			                  SelectionEdit.SelectionFilter.EDGES_ONLY));
		tm.setProgress(0.2);
		selectUtils.setSelectedEdges(network,dynNet.getVisibleEdgeNotList((DynInterval<T>) new DynIntervalDouble(start,end)), false, start, end);
		selectUtils.setSelectedEdges(network,dynNet.getVisibleEdgeList((DynInterval<T>) new DynIntervalDouble(start,end)), true, start, end);
		tm.setProgress(0.6);
		updateView();
		tm.setProgress(1.0);
	}
}
