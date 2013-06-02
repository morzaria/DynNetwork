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

package org.cytoscape.dyn.internal.model;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;

/**
 * <code> DynNetworkManagerImpl </code> implements the interface
 * {@link DynNetworkManager} for managing multiple {@link DynNetwork}s.
 *  
 * @author Sabina Sara Pfister
 * 
 */
public final class DynNetworkManagerImpl<T> implements DynNetworkManager<T>
{
	private final CyNetworkManager cyNetworkManager;
	private final Map<CyNetwork, DynNetwork<T>> dynNetworkMap;
	
	/**
	 * <code> DynNetworkManagerImpl </code> constructor.
	 * @param cyNetworkManager
	 */
	public DynNetworkManagerImpl(CyNetworkManager cyNetworkManager)
	{
		this.cyNetworkManager = cyNetworkManager;
		this.dynNetworkMap = new WeakHashMap<CyNetwork, DynNetwork<T>>();
	}

	@Override
	public void addDynNetwork(DynNetwork<T> dynNetwork)
	{
		this.cyNetworkManager.addNetwork(dynNetwork.getNetwork());
		this.dynNetworkMap.put(dynNetwork.getNetwork(), dynNetwork);
	}

	@Override
	public DynNetwork<T> getDynNetwork(CyNetwork network)
	{
		return dynNetworkMap.get(network);
	}
	
	@Override
	public Collection<DynNetwork<T>> getDynNetworks() 
	{
		return dynNetworkMap.values();
	}
	
}

