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

package org.cytoscape.dyn.internal.layout.model;

import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.tree.AbstractIntervalCheck;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;

/**
 * <code> NodeDynamicsAttribute </code> is used to store node dynamical attributes
 * to be added later to the visualization.
 * 
 * @author Sabina Sara Pfister
 * 
 */
public final class NodeDynamicsAttribute<T>  extends AbstractIntervalCheck<T>
{
	private final CyNode currentNode;
	
	private final String x;
	private final String y;
	private final String start;
	private final String end;
	
	/**
	 * <code> NodeGraphicsAttribute </code> constructor.
	 * @param currentNetwork
	 * @param currentNode
	 * @param x
	 * @param y
	 * @param end
	 * @param start
	 */
	public NodeDynamicsAttribute(
			final DynNetwork<T> currentNetwork,
			final CyNode currentNode,
			final String x, 
			final String y,   
			final String start,
			final String end)
	{
		this.currentNode = currentNode;
		this.x = x;
		this.y = y;
		this.start = start;
		this.end = end;
	}

	/**
	 * Add node graphics attribute.
	 * @param dynNetworkView
	 * @param layoutManager
	 */
	@SuppressWarnings("unchecked")
	public void add(DynNetworkView<T> dynNetworkView, DynLayoutManager<T> layoutManager)
	{
		CyNetworkView view = dynNetworkView.getNetworkView();
		DynLayout<T> layout = layoutManager.getDynLayout(view);
		
		if (x!=null)
			layout.insertNodePositionX(
				currentNode,getIntervalAttr(dynNetworkView.getNetwork(),"node_X_Pos",(T) new Double(Double.parseDouble(x)),start, end));
		if (y!=null)
			layout.insertNodePositionY(
				currentNode,getIntervalAttr(dynNetworkView.getNetwork(),"node_Y_Pos",(T) new Double(Double.parseDouble(y)),start, end));
	}
	
}
