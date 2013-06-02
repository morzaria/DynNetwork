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

package org.cytoscape.dyn.internal.vizmapper.model;

import org.cytoscape.dyn.internal.io.event.Sink;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;

/**
 * <code> DynVizMapFactory </code> is the interface for the factory of
 * {@link DynVizMap}s.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public interface DynVizMapFactory<T> extends Sink<T> 
{
	/**
	 * Create dynamic VizMap.
	 * @param dynNetwork
	 * @param networkView
	 * @return vizmap
	 */
	public DynVizMap<T> createDynVizMap(DynNetwork<T> dynNetwork, CyNetworkView networkView);
	
	/**
	 * Process added graph graphic attribute event.
	 * @param dynNetwork
	 * @param fill
	 * @param start
	 * @param end
	 */
	public void addedGraphGraphics(DynNetwork<T> dynNetwork, String fill, String start, String end);
	
	/**
	 * Add edge graphic attribute event.
	 * @param dynNetwork
	 * @param currentEdge
	 * @param width
	 * @param fill
	 * @param transparency
	 * @param start
	 * @param end
	 */
	public void addedEdgeGraphics(DynNetwork<T> dynNetwork, CyEdge currentEdge, String width, String fill, String transparency, String start, String end);
	
	/**
	 * Add node graphic attribute event.
	 * @param dynNetwork
	 * @param currentNode
	 * @param type
	 * @param height
	 * @param width
	 * @param size
	 * @param fill
	 * @param line width
	 * @param outline
	 * @param transparency
	 * @param start
	 * @param end
	 */
	public void addedNodeGraphics(DynNetwork<T> dynNetwork, CyNode currentNode, String type, String height, String width, String size, String fill, String linew, String outline, String transparency, String start, String end);
	
	/**
	 * Process finalize dynVizMap event.
	 * @param dynNetworkView
	 */
	public void finalizeDynVizMap(DynNetworkView<T> dynNetworkView);
	
	/**
	 * Remove dynVizMap event and add an empty one.
	 * @param dynNetworkView
	 * @return
	 */
	public void removeDynVizMap(DynNetworkView<T> dynNetworkView);
	
	/**
	 * Remove dynVizMap event and add an empty one.
	 * @param dynNetworkView
	 * @return
	 */
	public void removeDynVizMap(CyNetworkView cynetworkView);
	
}
