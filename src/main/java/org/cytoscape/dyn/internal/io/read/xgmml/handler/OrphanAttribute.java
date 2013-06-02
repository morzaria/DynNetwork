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

package org.cytoscape.dyn.internal.io.read.xgmml.handler;

import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.model.CyEdge;

/**
 * <code> OrphanAttribute </code> is used to store attributes of 
 * {@link OrphanEdge}.
 * 
 * @author Sabina Sara Pfister
 * 
 */
public final class OrphanAttribute<T>
{
	private final DynNetwork<T> currentNetwork;
	private final String name;
	private final String value;
	private final String type;
	private final String start;
	private final String end;
	
	/**
	 * <code> OrphanAttribute </code> constructor.
	 * @param currentNetwork
	 * @param name
	 * @param value
	 * @param type
	 * @param start
	 * @param end
	 */
	public OrphanAttribute(
			final DynNetwork<T> currentNetwork, 
			final String name, 
			final String value,
			final String type, 
			final String start, 
			final String end)
	{
		this.currentNetwork = currentNetwork;
		this.name = name;
		this.value = value;
		this.type = type;
		this.start = start;
		this.end = end;
	}
	
	/**
	 * Add orphan edge attribute to edge.
	 * @param handler
	 * @param currentEdge
	 */
	public void add(DynHandlerXGMML<T> handler, CyEdge currentEdge)
	{
		if (currentEdge!= null && name!=null && value!=null && type!=null)
			handler.addEdgeAttribute(currentNetwork, currentEdge, name, value, type, start, end);
	}
	
}
