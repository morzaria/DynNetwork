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

package org.cytoscape.dyn.internal.view.task;

import java.util.List;

import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalDouble;
import org.cytoscape.dyn.internal.view.gui.DynCytoPanelImpl;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * <code> DynNetworkViewTransparencyTask </code> is responsible for updating the {@link CyNetworkView}
 * everytime the background transparency level is set to show/hide elements of the network that are
 * not in the current time interval.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public final class DynNetworkViewTransparencyTask<T,C> extends AbstractDynNetworkViewTask<T,C>  
{
	private double time;
	private int visibility;

	/**
	 * <code> DynNetworkViewTransparencyTask </code> constructor.
	 * @param panel
	 * @param view
	 * @param queue
	 * @param low
	 */
	public DynNetworkViewTransparencyTask(
			final DynCytoPanelImpl<T,C> panel,
			final DynNetworkView<T> view,
			final BlockingQueue queue) 
	{
		super(panel, view, null, queue);
	}

	@Override
	public void run() 
	{
		queue.lock(); 

		setParameters();
		
		// update nodes
		List<DynInterval<T>> intervalList = dynNetwork.searchNodesNot(timeInterval);
		for (DynInterval<T> interval : intervalList)
			setTransparency(dynNetwork.getNode(interval), visibility);

		// update edges
		intervalList = dynNetwork.searchEdgesNot(timeInterval);
		for (DynInterval<T> interval : intervalList)
			setTransparency(dynNetwork.getEdge(interval), visibility);

		view.updateView();
		
		queue.unlock();
	}
	
	private void setTransparency(CyNode node, int visibility)
	{
		view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_TRANSPARENCY,visibility);
		view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_BORDER_TRANSPARENCY,visibility);
		view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_LABEL_TRANSPARENCY,visibility);
	}

	private void setTransparency(CyEdge edge, int visibility)
	{
		view.getNetworkView().getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_TRANSPARENCY,visibility);
		view.getNetworkView().getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_LABEL_TRANSPARENCY,visibility);
	}
	
	@SuppressWarnings("unchecked")
	private void setParameters()
	{
		this.time = this.panel.getTime();
		if (time>=panel.getMaxTime())
			timeInterval = (DynInterval<T>) new DynIntervalDouble(time-0.0000001, time-0.0000001);
//			timeInterval = new DynInterval<T>(time, time+0.0000001);
		else
			timeInterval = (DynInterval<T>) new DynIntervalDouble(time, time);
		
		this.visibility = this.panel.getVisibility();
	}

}

