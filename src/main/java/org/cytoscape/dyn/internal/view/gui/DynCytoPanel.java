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

package org.cytoscape.dyn.internal.view.gui;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.dyn.internal.model.tree.DynInterval;

/**
 * <code> DynCytoPanel </code> is the interface for the a dynamic panel to
 * visualize dynamic networks.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 * @param <C>
 */
public interface DynCytoPanel<T,C> extends CytoPanelComponent
{
	/**
	 * Initialize visualization.
	 */
	public void initView();
	
	/**
	 * Set value is adjusting.
	 * @param valueIsAdjusting
	 */
	public void setValueIsAdjusting(boolean valueIsAdjusting);
	
	/**
	 * Get current time.
	 * @return time
	 */
	public double getTime();
	
	/**
	 * Return current time interval.
	 * @return time interval
	 */
	public DynInterval<T> getTimeInterval();
	
	/**
	 * Get minimum time.
	 * @return minimum time
	 */
	public double getMinTime();
	
	/**
	 * Set minimum time.
	 * @return minimum time
	 */
	public void setMinTime(double minTime);
	
	/**
	 * Get maximum time.
	 * @return maximum time
	 */
	public double getMaxTime();

	/**
	 * Set maximum time.
	 */
	public void setMaxTime(double maxTime);
	
	/**
	 * Get slider maximum value;
	 * @return max slider
	 */
	public int getSliderMax();
	
	/**
	 * Set number of visible nodes.
	 * @param nodes
	 */
	public void setNodes(int nodes);
	
	/**
	 * Set number of visible edges.
	 * @param edges
	 */
	public void setEdges(int edges);
	
	/**
	 * Get visibility.
	 * @return visibility
	 */
	public int getVisibility();
	
	/**
	 * Get smoothness.
	 * @return smoothness
	 */
	public int getSmoothness();
	
	/**
	 * Get time resolution interval.
	 * @return deltat
	 */
	public double getDeltat();
}
