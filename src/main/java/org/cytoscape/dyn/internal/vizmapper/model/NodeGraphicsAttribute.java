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
import org.cytoscape.model.CyNode;
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
public final class NodeGraphicsAttribute<T> extends AbstractIntervalCheck<T>
{
	private final CyNode currentNode;
	
	private final String type;
	private final String height;
	private final String width;
	private final String size;
	private final String fill;
	private final String linew;
	private final String outline;
	private final String transparency;
	private final String start;
	private final String end;

	
	/**
	 * <code> NodeGraphicsAttribute </code> constructor.
	 * @param currentNetwork
	 * @param currentNode
	 * @param type
	 * @param height
	 * @param width
	 * @param size
	 * @param fill
	 * @param linew
	 * @param outline
	 * @param transparency
	 * @param start
	 * @param end
	 */
	public NodeGraphicsAttribute(
			final DynNetwork<T> currentNetwork,
			final CyNode currentNode,
			final String type,
			final String height,
			final String width,
			final String size,
			final String fill,
			final String linew,
			final String outline,
			final String transparency,
			final String start,
			final String end)
	{
		this.currentNode = currentNode;
		this.type = type;
		this.height = height;
		this.width = width;
		this.size = size;
		this.fill = fill;
		this.linew = linew;
		this.outline = outline;
		this.transparency = transparency;
		this.start = start;
		this.end = end;
	}

	/**
	 * Add node graphics attribute.
	 * @param dynNetworkView
	 * @param vizMapManager
	 * @param typeMap
	 */
	@SuppressWarnings("unchecked")
	public void add(DynNetworkView<T> dynNetworkView, DynVizMapManager<T> vizMapManager, AttributeTypeMap typeMap)
	{
		CyNetworkView view = dynNetworkView.getNetworkView();
		DynVizMap<T> vizMap = vizMapManager.getDynVizMap(view);
		
		if (type!=null)
		{
			Object attr = typeMap.getTypedValue(typeMap.getType(type), type);
			vizMap.insertNodeGraphics(
					currentNode,
					(VisualProperty<T>) BasicVisualLexicon.NODE_SHAPE,
					"GRAPHICS.node.type",
					getIntervalAttr(dynNetworkView.getNetwork(),currentNode,"GRAPHICS.node.type",(T) attr ,start, end));
		}
		if (height!=null && size==null)
		{
			Object attr = typeMap.getTypedValue(typeMap.getType("real"), height);
			vizMap.insertNodeGraphics(
				currentNode,
				(VisualProperty<T>) BasicVisualLexicon.NODE_HEIGHT,
				"GRAPHICS.node.height",
				getIntervalAttr(dynNetworkView.getNetwork(),currentNode,"GRAPHICS.node.height",(T) attr ,start, end));
		}
		if (width!=null && size==null)
		{
			Object attr = typeMap.getTypedValue(typeMap.getType("real"), width);
			vizMap.insertNodeGraphics(
				currentNode,
				(VisualProperty<T>) BasicVisualLexicon.NODE_WIDTH,
				"GRAPHICS.node.width",
				getIntervalAttr(dynNetworkView.getNetwork(),currentNode,"GRAPHICS.node.width",(T) attr ,start, end));
		}
		if (size!=null)
		{
			Object attr = typeMap.getTypedValue(typeMap.getType("real"), size);
			vizMap.insertNodeGraphics(
					currentNode,
					(VisualProperty<T>) BasicVisualLexicon.NODE_HEIGHT,
					"GRAPHICS.node.height",
					getIntervalAttr(dynNetworkView.getNetwork(),currentNode,"GRAPHICS.node.height",(T) attr ,start, end));
			vizMap.insertNodeGraphics(
					currentNode,
					(VisualProperty<T>) BasicVisualLexicon.NODE_WIDTH,
					"GRAPHICS.node.width",
					getIntervalAttr(dynNetworkView.getNetwork(),currentNode,"GRAPHICS.node.width",(T) attr ,start, end));
//			vizMap.insertNodeGraphics(
//				currentNode,
//				(VisualProperty<T>) BasicVisualLexicon.NODE_SIZE,
//				"GRAPHICS.node.size",
//				getIntervalAttr(dynNetworkView.getNetwork(),currentNode,"GRAPHICS.node.size",(T) attr ,start, end));
		}
		if (fill!=null)
		{
			Object attr = typeMap.getTypedValue(typeMap.getType("paint"), fill);
			vizMap.insertNodeGraphics(
				currentNode,
				(VisualProperty<T>) BasicVisualLexicon.NODE_FILL_COLOR,
				"GRAPHICS.node.fill",
				getIntervalAttr(dynNetworkView.getNetwork(),currentNode,"GRAPHICS.node.fill",(T) attr ,start, end));
		}
		if (linew!=null)
		{
			Object attr = typeMap.getTypedValue(typeMap.getType("real"), linew);
			vizMap.insertNodeGraphics(
				currentNode,
				(VisualProperty<T>) BasicVisualLexicon.NODE_BORDER_WIDTH,
				"GRAPHICS.node.linew",
				getIntervalAttr(dynNetworkView.getNetwork(),currentNode,"GRAPHICS.node.linew",(T) attr ,start, end));
		}
		if (outline!=null)
		{
			Object attr = typeMap.getTypedValue(typeMap.getType("paint"), outline);
			vizMap.insertNodeGraphics(
				currentNode,
				(VisualProperty<T>) BasicVisualLexicon.NODE_BORDER_PAINT,
				"GRAPHICS.node.outline",
				getIntervalAttr(dynNetworkView.getNetwork(),currentNode,"GRAPHICS.node.outline",(T) attr ,start, end));
		}
		if (transparency!=null)
		{
			Object attr = typeMap.getTypedValue(typeMap.getType("integer"), transparency);
			vizMap.insertNodeGraphics(
				currentNode,
				(VisualProperty<T>) BasicVisualLexicon.NODE_TRANSPARENCY,
				"GRAPHICS.node.transparency",
				getIntervalAttr(dynNetworkView.getNetwork(),currentNode,"GRAPHICS.node.transparency",(T) attr ,start, end));
		}
	}
	
}
