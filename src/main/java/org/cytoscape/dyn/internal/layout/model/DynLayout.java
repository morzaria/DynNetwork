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

import java.util.List;

import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;

/**
 * <code> DynLayout </code> is the interface to represent the dynamics in time
 * of a {@link CyNetworkView} and is implemented by {@link DynLayoutImpl}.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public interface DynLayout<T>
{
	
	/**
	 * Insert node position X for when the given interval goes on.
	 * @param node
	 * @param interval
	 */
	public void insertNodePositionX(CyNode node, DynInterval<T> interval);
	
	/**
	 * Insert node position Y for when the given interval goes on.
	 * @param node
	 * @param interval
	 */
	public void insertNodePositionY(CyNode node, DynInterval<T> interval);

	/**
	 * Remove intervals and attributes belonging to node.
	 * @param node
	 */
	public void removeNode(CyNode node);
	
	/**
	 * Remove all node position intervals.
	 */
	public void removeAllIntervals();
	
	/**
	 * Get X position intervals.
	 * @param node
	 * @return list of X intervals
	 */
	public List<DynInterval<T>> getIntervalsX();

	/**
	 * Get all Y intervals.
	 * @param node
	 * @return list of Y intervals
	 */
	public List<DynInterval<T>> getIntervalsY();
	
	/**
	 * Search X positions of visible nodes that changed from the last time interval.
	 * @param interval
	 * @return list of X positions of visible nodes that changed from the last time interval
	 */
	public List<DynInterval<T>> searchChangedNodePositionsX(DynInterval<T> interval);
	
	/**
	 * Search Y positions of visible nodes that changed from the last time interval.
	 * @param interval
	 * @return list of Y positions of visible nodes that changed from the last time interval
	 */
	public List<DynInterval<T>> searchChangedNodePositionsY(DynInterval<T> interval);
	
	/**
	 * Initialize node positions.
	 * @param time interval
	 */
	public void initNodePositions(DynInterval<T> timeInterval);
	
	/**
	 * Finalize layout. We perform here all operations that require the network layout
	 * to be finished.
	 */
	public void finalize();
	
	/**
	 * Print intervals.
	 */
	public void print();
	
	/**
	 * Get network view.
	 * @return view
	 */
	public CyNetworkView getNetworkView();

}
