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

package org.cytoscape.dyn.internal.view.model;

import org.cytoscape.dyn.internal.io.event.Sink;
import org.cytoscape.dyn.internal.model.DynNetwork;

/**
 * <code> DynNetworkViewFactory </code> is a the interface for the factory of
 * {@link DynNetworkView}s and is an event sink.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public interface DynNetworkViewFactory<T> extends Sink<T>
{
	/**
	 * Process create view event.
	 * @param dynNetwork
	 * @return dynNetworkView
	 */
	public DynNetworkView<T> createView(DynNetwork<T> dynNetwork);
	
//	/**
//	 * Process added graph graphic attribute event.
//	 * @param dynNetwork
//	 * @param fill
//	 */
//	public void addedGraphGraphics(DynNetwork<T> dynNetwork, String fill, String start, String end);
//	
//	/**
//	 * Process added edge graphic attribute event.
//	 * @param dynNetwork
//	 * @param currentEdge
//	 * @param width
//	 * @param fill
//	 * @param start
//	 * @param end
//	 */
//	public void addedEdgeGraphics(DynNetwork<T> dynNetwork, CyEdge currentEdge, String width, String fill, String start, String end);
//	
//	/**
//	 * Process added node graphic attribute event.
//	 * @param dynNetwork
//	 * @param currentNode
//	 * @param type
//	 * @param height
//	 * @param width
//	 * @param size
//	 * @param x
//	 * @param y
//	 * @param fill
//	 * @param line width
//	 * @param outline
//	 * @param start
//	 * @param end
//	 */
//	public void addedNodeGraphics(DynNetwork<T> dynNetwork, CyNode currentNode, String type, String height, String width, String size, String x, String y, String fill, String linew, String outline, String start, String end);
	
	/**
	 * Process finalize network event.
	 * @param dynNetworkView
	 */
	public void finalizeNetwork(DynNetworkView<T> dynNetworkView);
	
//	/**
//	 * Set graph graphics attributes.
//	 * @param dynNetwork
//	 * @param fill
//	 */
//	public void setGraphGraphics(DynNetwork<T> dynNetwork, String fill);
//	
//	/**
//	 * Set node graphics attributes.
//	 * @param dynNetwork
//	 * @param currentNode
//	 * @param type
//	 * @param height
//	 * @param width
//	 * @param x
//	 * @param y
//	 * @param line width
//	 * @param fill
//	 * @param outline
//	 */
//	public void setNodeGraphics(DynNetwork<T> dynNetwork, CyNode currentNode, String type, String height, String width, String x, String y, String fill, String linew, String outline);
//	
//	/**
//	 * Set edge graphics attributes.
//	 * @param dynNetwork
//	 * @param currentEdge
//	 * @param width
//	 * @param fill
//	 */
//	public void setEdgeGraphics(DynNetwork<T> dynNetwork, CyEdge currentEdge, String width, String fill);
	
}
