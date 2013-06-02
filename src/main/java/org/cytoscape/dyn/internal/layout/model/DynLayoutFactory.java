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

import org.cytoscape.dyn.internal.io.event.Sink;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;

/**
 * <code> DynLayoutFactory </code> is the interface for the factory of
 * {@link DynLayout}s.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public interface DynLayoutFactory<T> extends Sink<T>
{
	/**
	 * Create dynamic layout.
	 * @param networkView
	 * @return
	 */
	public DynLayout<T> createDynLayout(CyNetworkView networkView);
	
	/**
	 * Create dynamic layout
	 * @param networkView
	 * @param context
	 * @return
	 */
	public DynLayout<T> createDynLayout(CyNetworkView networkView, Object context);
	
	/**
	 * Process added node graphic attribute event.
	 * @param dynNetwork
	 * @param currentNode
	 * @param x
	 * @param y
	 * @param start
	 * @param end
	 */
	public void addedNodeDynamics(DynNetwork<T> dynNetwork, CyNode currentNode, String x, String y, String start, String end);
	
	/**
	 * Process finalize network event.
	 * @param dynNetworkView
	 */
	public void finalizeLayout(DynNetworkView<T> dynNetworkView);
	
	/**
	 * Remove layout event and add an empty one.
	 * @param dynNetworkView
	 * @return
	 */
	public void removeLayout(DynNetworkView<T> dynNetworkView);
	
	/**
	 * Remove layout event and add an empty one.
	 * @param networkView
	 * @return
	 */
	public void removeLayout(CyNetworkView networkView);
}
