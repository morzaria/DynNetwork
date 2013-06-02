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

import java.util.HashMap;
import java.util.List;

import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.tree.AbstractDynInterval;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.view.gui.DynCytoPanel;
import org.cytoscape.dyn.internal.view.gui.DynCytoPanelImpl;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * <code> AbstractDynNetworkViewTask </code> is the abstract calls all visual task
 * have to extend. It provides the functionality to communicate with {@link DynCytoPanelImpl},
 * and update the visualization.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public abstract class AbstractDynNetworkViewTask<T,C>  implements Runnable
{
	protected final DynCytoPanel<T,C> panel;
	protected final DynNetworkView<T> view;
	protected final DynNetwork<T> dynNetwork;
	protected final Transformator<T> transformator;
	protected final BlockingQueue queue;
	
	protected final HashMap<CyNode,AbstractDynInterval<T>> nodeTransparency;
	protected final HashMap<CyEdge,AbstractDynInterval<T>> edgeTransparency;
	
	protected double timeStart;
	protected double timeEnd;
	
	protected DynInterval<T> timeInterval;
	
	protected boolean cancelled = false;
	
	/**
	 * <code> AbstractDynNetworkViewTask </code> constructor.
	 * @param panel
	 * @param view
	 * @param layout
	 * @param queue
	 * @param low
	 * @param high
	 */
	protected AbstractDynNetworkViewTask(
			final DynCytoPanel<T, C> panel,
			final DynNetworkView<T> view,
			final Transformator<T> transformator,
			final BlockingQueue queue) 
	{
		this.panel = panel;
		this.view = view;
		this.dynNetwork = view.getNetwork();
		this.transformator = transformator;
		this.queue = queue;

		this.nodeTransparency = new HashMap<CyNode,AbstractDynInterval<T>>();
		this.edgeTransparency = new HashMap<CyEdge,AbstractDynInterval<T>>();
	}

	/**
	 * Cancel task.
	 */
	public void cancel() 
	{
		this.cancelled = true;
	}

	@Override
	public void run()
	{

	}

	protected void updateGraphAttr(List<DynInterval<T>> intervalList)
	{
		for (DynInterval<T> interval : intervalList)
		{
			dynNetwork.getNetwork().getRow(dynNetwork.getNetwork()).set(interval.getAttribute().getColumn(), interval.getOverlappingValue(timeInterval));
		}
	}

	protected void updateNodeAttr(List<DynInterval<T>> intervalList)
	{
		for (DynInterval<T> interval : intervalList)
		{
			CyNode node = dynNetwork.getNode(interval);
			if (node!=null)
			{
				dynNetwork.getNetwork().getRow(node).set(interval.getAttribute().getColumn(), interval.getOverlappingValue(timeInterval));
			}
		}
	}

	protected void updateEdgeAttr(List<DynInterval<T>> intervalList)
	{
		for (DynInterval<T> interval : intervalList)
		{
			CyEdge edge = dynNetwork.getEdge(interval);
			if (edge!=null)
			{
				// TODO: remove this, it's a hack!
//				if (interval.getOverlappingValue(timeInterval)!=null)
					dynNetwork.getNetwork().getRow(edge).set(interval.getAttribute().getColumn(), interval.getOverlappingValue(timeInterval));
			}
		}
	}
	
}
