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

import java.util.List;

import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.attribute.DynAttribute;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualProperty;

/**
 * <code> DynVizMap </code> is the interface to represent the graphical dynamics in time
 * of a {@link CyNetworkView} and is implemented by {@link DynVizMapImpl}. This implementation
 * is to be preferred over custom static {@link VizMap}, since it is much faster and reliable.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public interface DynVizMap<T>
{
	
	/**
	 * Insert graph graphical attribute for when the given interval goes on.
	 * @param visual property
	 * @param label
	 * @param interval
	 */
	public void insertGraphGraphics(VisualProperty<T> vp, String column, DynInterval<T> interval);
	
	/**
	 * Insert node graphical attribute for when the given interval goes on.
	 * @param node
	 * @param visual property
	 * @param label
	 * @param interval
	 */
	public void insertNodeGraphics(CyNode node, VisualProperty<T> vp, String column, DynInterval<T> interval);
	
	/**
	 * Insert edge graphical attribute for when the given interval goes on.
	 * @param edge
	 * @param visual property
	 * @param label
	 * @param interval
	 */
	public void insertEdgeGraphics(CyEdge edge, VisualProperty<T> vp, String column, DynInterval<T> interval);
	
	/**
	 * Search graphical attributes of the graph that changed from the last time interval.
	 * @param interval
	 * @return list of graphical attributes of the graph that changed from the last time interval
	 */
	public List<DynInterval<T>> searchChangedGraphGraphics(DynInterval<T> interval);
	
	/**
	 * Search graphical attributes of nodes that changed from the last time interval.
	 * @param interval
	 * @return list of graphical attributes of nodes that changed from the last time interval
	 */
	public List<DynInterval<T>> searchChangedNodeGraphics(DynInterval<T> interval);

	/**
	 * Search graphical attributes of edges that changed from the last time interval.
	 * @param interval
	 * @return list of graphical attributes of edges that changed from the last time interval
	 */
	public List<DynInterval<T>> searchChangedEdgeGraphics(DynInterval<T> interval);	
	
	/**
	 * Search transparency graphical attributes of nodes that changed from the last time interval.
	 * @param interval
	 * @return list of graphical attributes of nodes that changed from the last time interval
	 */
	public List<DynInterval<T>> searchChangedNodeTransparencyGraphics(DynInterval<T> interval);

	/**
	 * Search transparency graphical attributes of edges that changed from the last time interval.
	 * @param interval
	 * @return list of graphical attributes of edges that changed from the last time interval
	 */
	public List<DynInterval<T>> searchChangedEdgeTransparencyGraphics(DynInterval<T> interval);
	
	/**
	 * Finalize dynamic vizmap. We perform here all operations that require the network vizmap
	 * to be finished.
	 */
	public void finalize();
	
	
	/**
	 * Get network view.
	 * @return view
	 */
	public CyNetworkView getNetworkView();
	
	/**
	 * Get visual mapping associated with given dynamic attribute.
	 * @param attr
	 * @return
	 */
	public VisualProperty<T> getVisualProperty(DynAttribute<T> attr);
	
	/**
	 * Add node to transparent node list.
	 * @param node
	 */
	public void addTransparentNode(CyNode node);
	
	/**
	 * Add edge to transparent edge list.
	 * @param edge
	 */
	public void addTransparentEdge(CyEdge edge);
	
	/**
	 * Check if node transparent list contains node.
	 * @param node
	 * @return
	 */
	public boolean contrainsTransparentNode(CyNode node);
	
	/**
	 * Check if edge transparent list contains edge.
	 * @param edge
	 * @return
	 */
	public boolean contrainsTransparentEdge(CyEdge edge);
	
	/**
	 * Get dynNetwork
	 * @return dynNetwork
	 */
	public DynNetwork<T> getDynNetwork();

}