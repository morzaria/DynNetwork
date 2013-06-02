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

import java.util.ArrayList;

import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.model.CyEdge;

/**
 * <code> OrphanEdge </code> is used to store edges connected to nodes that 
 * have not been yet defined. We will try to insert them once the parser reaches
 * the last command.
 * 
 * @author Sabina Sara Pfister
 *
 */
public final class OrphanEdge<T>
{
	private final DynNetwork<T> currentNetwork;
	private final String id;
	private final String label;
	private final String source;
	private final String target;
	private final String start;
	private final String end;
	
	private final ArrayList<OrphanAttribute<T>> attributes;
	private final ArrayList<OrphanGraphicsAttribute<T>> graphics_attributes;
	
	/**
	 * <code> OrphanEdge </code> constructor.
	 * @param currentNetwork
	 * @param id
	 * @param label
	 * @param source
	 * @param target
	 * @param start
	 * @param end
	 */
	public OrphanEdge(
			DynNetwork<T> currentNetwork, 
			String id, 
			String label,
			String source, 
			String target, 
			String start, 
			String end)
	{
		this.currentNetwork = currentNetwork;
		this.id = id;
		this.label = label;
		this.source = source;
		this.target = target;
		this.start = start;
		this.end = end;
		
		attributes = new ArrayList<OrphanAttribute<T>>();
		graphics_attributes = new ArrayList<OrphanGraphicsAttribute<T>>();
	}
	
	/**
	 * Add orphan edge to network through the handler.
	 * @param handler
	 */
	public void add(DynHandlerXGMML<T> handler)
	{
		CyEdge currentEdge = handler.addEdge(currentNetwork, id, label, source, target, start,  end);
		for (OrphanAttribute<T> attr : attributes)
			attr.add(handler, currentEdge);
		for (OrphanGraphicsAttribute<T> attr : graphics_attributes)
			attr.add(handler, currentEdge);
		
		if (currentEdge==null)
			System.out.println("\nXGMML Parser Warning: skipped edge label=" + label + 
					" source=" + source + " target=" + target + " (missing nodes)");
	}
	
	/**
	 * Add all orphan edge attributes.
	 * @param currentNetwork
	 * @param name
	 * @param value
	 * @param type
	 * @param start
	 * @param end
	 */
	public void addAttribute(DynNetwork<T> currentNetwork, String name, String value, String type, String start, String end)
	{
		attributes.add(new OrphanAttribute<T>(currentNetwork, name, value, type, start, end));
	}
	
	/**
	 * Add all orphan edge graphics attributes.
	 * @param currentNetwork
	 * @param width
	 * @param fill
	 * @param transparency
	 * @param start
	 * @param end
	 */
	public void addGraphics(DynNetwork<T> currentNetwork, String width, String fill, String transparency, String start, String end)
	{
		graphics_attributes.add(new OrphanGraphicsAttribute<T>(currentNetwork, width, fill, transparency, start, end));
	}

}
