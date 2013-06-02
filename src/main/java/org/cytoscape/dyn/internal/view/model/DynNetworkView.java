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

import java.util.List;

import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;

/**
 * <code> DynNetworkView </code> is an object that represents a the view of a
 * dynamic network. It provides the link to the current static {@link DynNetwork}. 
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public interface DynNetworkView<T>
{	
	
	/**
	 * Search overlapping intervals for graphs given an interval that changed
	 * from the last search.
	 * @param interval
	 * @return list of changed overlapping intervals
	 */
	public List<DynInterval<T>> searchChangedGraphsAttr(DynInterval<T> interval);
	
	/**
	 * Search overlapping intervals for nodes given an interval that changed
	 * from the last search.
	 * @param interval
	 * @return list of changed overlapping intervals
	 */
	public List<DynInterval<T>> searchChangedNodes(DynInterval<T> interval);
	
	/**
	 * Search overlapping intervals for edges given an interval that changed
	 * from the last search.
	 * @param interval
	 * @return list of changed overlapping intervals
	 */
	public List<DynInterval<T>> searchChangedEdges(DynInterval<T> interval);
	
	/**
	 * Search overlapping intervals for node attributes given an interval that changed
	 * from the last search.
	 * @param interval
	 * @return list of changed overlapping intervals
	 */
	public List<DynInterval<T>> searchChangedNodesAttr(DynInterval<T> interval);
	
	/**
	 * Search overlapping intervals for edge attributes given an interval that changed
	 * from the last search.
	 * @param interval
	 * @return list of changed overlapping intervals
	 */
	public List<DynInterval<T>> searchChangedEdgesAttr(DynInterval<T> interval);
	
	/**
	 * Get network view.
	 * @return view
	 */
	public CyNetworkView getNetworkView();
	
	/**
	 * Update view.
	 */
	public void updateView();

	/**
	 * Get network.
	 * @return
	 */
	public DynNetwork<T> getNetwork();
	
	/**
	 * Get current visualization time.
	 * @return time
	 */
	public double getCurrentTime();
	
	/**
	 * Set current visualization time.
	 * @param currentTime
	 */
	public void setCurrentTime(double currentTime);
	
	/**
	 * Get current visual style.
	 * @return
	 */
	public VisualStyle getCurrentVisualStyle();
	
	/**
	 * Get number of currently visible nodes
	 * @return
	 */
	public int getVisibleNodes();

	/**
	 * Get number of current visible edges.
	 * @return
	 */
	public int getVisibleEdges();
	
	/**
	 * Get node dummy value
	 * @param node
	 * @return node dummy value
	 */
	public int getNodeDummyValue(CyNode node);
	
	/**
	 * Get edge dummy value
	 * @param edge
	 * @return edge dummy value
	 */
	public int getEdgeDummyValue(CyEdge edge);
	
	/**
	 * Set node dummy value
	 * @param node
	 * @param value
	 */
	public void setNodeDummyValue(CyNode node, int value);
	
	/**
	 * Set edge dummy value
	 * @param edge
	 * @param value
	 */
	public void setEdgeDummyValue(CyEdge edge, int value);
	
}
