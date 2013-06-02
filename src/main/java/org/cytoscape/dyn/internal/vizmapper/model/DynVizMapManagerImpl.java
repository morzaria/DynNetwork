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
import java.util.Map;
import java.util.WeakHashMap;

import org.cytoscape.view.model.CyNetworkView;

/**
 * <code> DynVizMapManagerImpl </code> implements the interface
 * {@link DynVizMapManager} for managing multiple {@link DynVizMap}s.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public class DynVizMapManagerImpl<T>  implements DynVizMapManager<T>
{
	private final Map<CyNetworkView, DynVizMap<T>> dynVizMapMapping;
	
	/**
	 * <code> DynLayoutManagerImpl </code> constructor.
	 */
	public DynVizMapManagerImpl()
	{
		this.dynVizMapMapping = new WeakHashMap<CyNetworkView, DynVizMap<T>>();
	}
	
	@Override
	public void addDynVizMap(DynVizMap<T> dynVizMap) 
	{
		this.dynVizMapMapping.put(dynVizMap.getNetworkView(), dynVizMap);
	}

	@Override
	public DynVizMap<T> getDynVizMap(CyNetworkView view) 
	{
		return dynVizMapMapping.get(view);
	}

	@Override
	public Collection<DynVizMap<T>> getDynVizMaps() 
	{
		return dynVizMapMapping.values();
	}

	@Override
	public void removeDynVizMap(CyNetworkView view) 
	{
		if (dynVizMapMapping.containsKey(view))
		{
			dynVizMapMapping.remove(view);
		}
	}

}
