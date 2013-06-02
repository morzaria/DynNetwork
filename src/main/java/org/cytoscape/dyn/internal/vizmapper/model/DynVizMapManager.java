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

import java.util.Collection;

import org.cytoscape.view.model.CyNetworkView;

/**
 * <code> DynVizMapManager </code> is the interface of the
 * {@link DynVizMap}s manager.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public interface DynVizMapManager<T>
{
	/**
	 * Add dynamic VizMap.
	 * @param dynVizMap
	 */
	public void addDynVizMap(DynVizMap<T> dynVizMap);

	/**
	 * Get dynamic VizMap associated with the given view.
	 * @param view
	 */
	public DynVizMap<T> getDynVizMap(CyNetworkView view);
	
	/**
	 * Remove DynVizMap associated with view if exists.
	 * @param view
	 */
	public void removeDynVizMap(CyNetworkView view);
	/**
	 * Get all dynVizMaps.
	 * @return dynVizMaps
	 */
	public Collection<DynVizMap<T>> getDynVizMaps();
	
}
