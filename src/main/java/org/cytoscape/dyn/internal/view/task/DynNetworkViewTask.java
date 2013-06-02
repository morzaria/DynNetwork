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

import org.cytoscape.dyn.internal.layout.model.DynLayout;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalDouble;
import org.cytoscape.dyn.internal.view.gui.DynCytoPanelImpl;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;

/**
 * <code> DynNetworkViewTask </code> is the task that is responsible for updating
 * the visualization of a dynamic network {@link DynNetwork} by updating the time. In order 
 * to increase speed performance, only elements that changed from the last visualization are 
 * updated (by interval tree search over all elements). The dynamics of the network is stored in 
 * {@link DynNetwork}, whereas the dynamics of the visualization is stored in {@link DynLayout}.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public final class DynNetworkViewTask<T,C> extends AbstractDynNetworkViewTask<T,C> 
{
	private double time;
	private int visibility;
	private int smoothness;
	private double deltat;

	/**
	 * <code> DynNetworkViewTask </code> constructor.
	 * @param panel
	 * @param view
	 * @param layout
	 * @param queue
	 */
	public DynNetworkViewTask(
			final DynCytoPanelImpl<T,C> panel,
			final DynNetworkView<T> view,
			final Transformator<T> transformator,
			final BlockingQueue queue) 
	{
		super(panel, view, transformator, queue);
	}

	@Override
	public void run() 
	{
		
		queue.lock();
		
		if (this.cancelled==true)
		{
			queue.unlock();	
			return;
		}
		
		setParameters();

		// update attributes
		updateGraphAttr(view.searchChangedGraphsAttr(timeInterval));
		updateNodeAttr(view.searchChangedNodesAttr(timeInterval));
		updateEdgeAttr(view.searchChangedEdgesAttr(timeInterval));

		// update node and edges visual properties
		transformator.run(dynNetwork,view,timeInterval,visibility,smoothness,deltat);

		panel.setNodes(view.getVisibleNodes());
		panel.setEdges(view.getVisibleEdges());
		
		view.updateView();
		
		queue.unlock(); 
	}
	
	@SuppressWarnings("unchecked")
	private void setParameters()
	{
		this.time = this.panel.getTime();
		if (time>=panel.getMaxTime())
			timeInterval = (DynInterval<T>) new DynIntervalDouble(time-0.0000001, time-0.0000001);
		else
			timeInterval = (DynInterval<T>) new DynIntervalDouble(time, time);
		
		this.visibility = this.panel.getVisibility();
		this.smoothness = this.panel.getSmoothness();
		this.deltat = this.panel.getDeltat();
	}

}

