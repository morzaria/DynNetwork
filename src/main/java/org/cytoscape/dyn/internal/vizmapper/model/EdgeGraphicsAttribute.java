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
import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * <code> NodeGraphicsAttribute </code> is used to store node graphics attributes
 * to be added later to the visualization.
 * 
 * @author Sabina Sara Pfister
 * 
 */
public final class EdgeGraphicsAttribute<T> extends AbstractIntervalCheck<T>
{
	private final CyEdge currentEdge;
	private final String width;
	private final String fill;
	private final String transparency;
	private final String start;
	private final String end;

	
	/**
	 * <code> EdgeGraphicsAttribute </code> constructor.
	 * @param currentNetwork
	 * @param currentEdge
	 * @param width
	 * @param fill
	 * @param transparency
	 * @param start
	 * @param end
	 */
	public EdgeGraphicsAttribute(
			final DynNetwork<T> currentNetwork,
			final CyEdge currentEdge,
			final String width,
			final String fill,
			final String transparency,
			final String start,
			final String end)
	{
		this.currentEdge = currentEdge;
		this.width = width;
		this.fill = fill;
		this.transparency = transparency;
		this.start = start;
		this.end = end;
	}

	/**
	 * Add edge graphics attribute.
	 * @param dynNetworkView
	 * @param vizMapManager
	 * @param typeMap
	 */
	@SuppressWarnings("unchecked")
	public void add(DynNetworkView<T> dynNetworkView, DynVizMapManager<T> vizMapManager, AttributeTypeMap typeMap)
	{
		CyNetworkView view = dynNetworkView.getNetworkView();
		DynVizMap<T> vizMap = vizMapManager.getDynVizMap(view);
		
		if (width!=null)
		{
			Object attr = typeMap.getTypedValue(typeMap.getType("real"), width);
			vizMap.insertEdgeGraphics(
					currentEdge,
					(VisualProperty<T>) BasicVisualLexicon.EDGE_WIDTH,
					"GRAPHICS.edge.width",
					getIntervalAttr(dynNetworkView.getNetwork(),currentEdge,"GRAPHICS.edge.width",(T) attr ,start, end));
		}
		if (fill!=null)
		{
			Object attr = typeMap.getTypedValue(typeMap.getType("paint"), fill);
			vizMap.insertEdgeGraphics(
					currentEdge,
					(VisualProperty<T>) BasicVisualLexicon.EDGE_UNSELECTED_PAINT,
					"GRAPHICS.edge.fill",
					getIntervalAttr(dynNetworkView.getNetwork(),currentEdge,"GRAPHICS.edge.fill",(T) attr ,start, end));
		}
		if (transparency!=null)
		{
			Object attr = typeMap.getTypedValue(typeMap.getType("integer"), transparency);
			vizMap.insertEdgeGraphics(
					currentEdge,
					(VisualProperty<T>) BasicVisualLexicon.EDGE_TRANSPARENCY,
					"GRAPHICS.edge.transparency",
					getIntervalAttr(dynNetworkView.getNetwork(),currentEdge,"GRAPHICS.edge.transparency",(T) attr ,start, end));
		}
	}
	
}
