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

import org.cytoscape.dyn.internal.io.event.Sink;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;


/**
 * <code> DynNetworkFactory </code> is the interface for the factory of
 * {@link DynNetwork}s and is an event sink.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public interface DynNetworkFactory<T> extends Sink<T>
{
	/**
	 * Process added graph event.
	 * @param id
	 * @param label
	 * @param start
	 * @param end
	 * @param directed
	 * @return network
	 */
	public DynNetwork<T> addedGraph(String id, String label, String start, String end, String directed);
	
	/**
	 * Process added node event.
	 * @param dynNetwork
	 * @param id
	 * @param label
	 * @param start
	 * @param end
	 * @return node
	 */
	public CyNode addedNode(DynNetwork<T> dynNetwork, String id, String label, String start, String end);
	
	/**
	 * Process added edge event.
	 * @param dynNetwork
	 * @param id
	 * @param label
	 * @param source
	 * @param target
	 * @param start
	 * @param end
	 * @return edge
	 */
	public CyEdge addedEdge(DynNetwork<T> dynNetwork, String id, String label, String source, String target, String start, String end);
	
	/**
	 * Process added graph attribute event.
	 * @param dynNetwork
	 * @param name
	 * @param value
	 * @param Type
	 * @param start
	 * @param end
	 */
	public void addedGraphAttribute(DynNetwork<T> dynNetwork, String name, String value, String Type, String start, String end);
	
	/**
	 * Process added node attribute event.
	 * @param dynNetwork
	 * @param currentNode
	 * @param name
	 * @param value
	 * @param Type
	 * @param start
	 * @param end
	 */
	public void addedNodeAttribute(DynNetwork<T> dynNetwork, CyNode currentNode, String name, String value, String Type, String start, String end);
	
	/**
	 * Process added edge attribute event.
	 * @param dynNetwork
	 * @param currentEdge
	 * @param name
	 * @param value
	 * @param Type
	 * @param start
	 * @param end
	 */
	public void addedEdgeAttribute(DynNetwork<T> dynNetwork, CyEdge currentEdge, String name, String value, String Type, String start, String end);
	
	/**
	 * Process finalize network event.
	 * @param dynNetwork
	 */
	public void finalizeNetwork(DynNetwork<T> dynNetwork);
	
	public void setAttributesUpdate(DynNetwork<T> dynNetwork, CyNode node, String attName, String attValue, String attType, String start, String end);
}
