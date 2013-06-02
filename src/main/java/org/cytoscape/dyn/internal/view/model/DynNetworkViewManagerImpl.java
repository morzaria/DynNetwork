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

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;

/**
 * <code> DynNetworkManagerImpl </code> implements the interface
 * {@link DynNetworkViewManager} for managing multiple {@link DynNetworkView}s.
 *  
 * @author Sabina Sara Pfister
 * 
 */
public final class DynNetworkViewManagerImpl<T> implements DynNetworkViewManager<T>
{
	private final CyNetworkViewManager cyNetworkViewManager;
	private final Map<CyNetworkView, DynNetworkView<T>> dynNetworkViewMap;

	/**
	 * <code> DynNetworkViewManagerImpl </code> constructor.
	 * @param cyNetworkViewManager
	 */
	public DynNetworkViewManagerImpl(final CyNetworkViewManager cyNetworkViewManager) 
	{
		this.cyNetworkViewManager = cyNetworkViewManager;
		this.dynNetworkViewMap = new WeakHashMap<CyNetworkView, DynNetworkView<T>>();
	}
	
	@Override
	public void addDynNetworkView(DynNetworkView<T> dynNetworkView)
	{
		this.cyNetworkViewManager.addNetworkView(dynNetworkView.getNetworkView());
		this.dynNetworkViewMap.put(dynNetworkView.getNetworkView(), dynNetworkView);
	}

	@Override
	public DynNetworkView<T> getDynNetworkView(CyNetworkView view)
	{
		return dynNetworkViewMap.get(view);
	}

	@Override
	public Collection<DynNetworkView<T>> getDynNetworkViews() 
	{
		return dynNetworkViewMap.values();
	}	
	
}
