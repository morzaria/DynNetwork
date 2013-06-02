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

import org.cytoscape.dyn.internal.io.read.util.AttributeTypeMap;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.tree.AbstractIntervalCheck;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * <code> GraphGraphicsAttribute </code> is used to store graph graphics attributes
 * to be added later to the visualization.
 * 
 * @author Sabina Sara Pfister
 * 
 */
public final class GraphGraphicsAttribute<T> extends AbstractIntervalCheck<T>
{
	private final String fill;
	private final String start;
	private final String end;
	
	/**
	 * <code> GraphGraphicsAttribute </code> constructor.
	 * @param currentNetwork
	 * @param fill
	 * @param end
	 * @param start
	 */
	public GraphGraphicsAttribute(
			final DynNetwork<T> currentNetwork,
			final String fill,   
			final String start,
			final String end)
	{
		this.fill = fill;
		this.start = start;
		this.end = end;
	}

	/**
	 * Add graph graphics attribute.
	 * @param dynNetworkView
	 * @param vizMapManager
	 * @param typeMap
	 */
	@SuppressWarnings("unchecked")
	public void add(DynNetworkView<T> dynNetworkView, DynVizMapManager<T> vizMapManager, AttributeTypeMap typeMap)
	{
		CyNetworkView view = dynNetworkView.getNetworkView();
		DynVizMap<T> vizMap = vizMapManager.getDynVizMap(view);
		
		if (fill!=null)
		{
			Object attr = typeMap.getTypedValue(typeMap.getType("paint"), fill);
			vizMap.insertGraphGraphics(
					(VisualProperty<T>) BasicVisualLexicon.NETWORK_BACKGROUND_PAINT,
					"GRAPHICS.graph.fill",
					getIntervalAttr(dynNetworkView.getNetwork(),"GRAPHICS.graph.fill",(T) attr ,start, end));
		}
	}
	
}
