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

import java.util.Stack;

import org.cytoscape.dyn.internal.io.read.util.AttributeTypeMap;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;


public class DynVizMapFactoryImpl<T> implements DynVizMapFactory<T>
{
	private final DynVizMapManager<T> vizMapManager;
	private final Stack<GraphGraphicsAttribute<T>> graphGraphicsList;
	private final Stack<NodeGraphicsAttribute<T>> nodeGraphicsList;
	private final Stack<EdgeGraphicsAttribute<T>> edgeGraphicsList;
	
	private final AttributeTypeMap typeMap;
	
	/**
	 * <code> DynVizMapFactoryImplDynVizMapManager </code> constructor.
	 * @param vizMapManager
	 */
	public DynVizMapFactoryImpl(DynVizMapManager<T> vizMapManager)
	{
		this.vizMapManager = vizMapManager;
		this.graphGraphicsList = new Stack<GraphGraphicsAttribute<T>>();
		this.nodeGraphicsList = new Stack<NodeGraphicsAttribute<T>>();
		this.edgeGraphicsList = new Stack<EdgeGraphicsAttribute<T>>();
		
		this.typeMap = new AttributeTypeMap();
	}

	@Override
	public DynVizMap<T> createDynVizMap(DynNetwork<T> dynNetwork, CyNetworkView networkView) 
	{
		DynVizMap<T> vizmap = new DynVizMapImpl<T>(dynNetwork,networkView);
		vizMapManager.addDynVizMap(vizmap);
		return vizmap;
	}

	@Override
	public void finalizeDynVizMap(DynNetworkView<T> dynNetworkView) 
	{
		while (!graphGraphicsList.isEmpty())
			graphGraphicsList.pop().add(dynNetworkView,vizMapManager,typeMap);
		while (!nodeGraphicsList.isEmpty())
			nodeGraphicsList.pop().add(dynNetworkView,vizMapManager,typeMap);
		while (!edgeGraphicsList.isEmpty())
			edgeGraphicsList.pop().add(dynNetworkView,vizMapManager,typeMap);
		
		vizMapManager.getDynVizMap(dynNetworkView.getNetworkView()).finalize();
	}

	@Override
	public void removeDynVizMap(DynNetworkView<T> dynNetworkView) 
	{
		vizMapManager.removeDynVizMap(dynNetworkView.getNetworkView());
		vizMapManager.addDynVizMap(new DynVizMapImpl<T>(dynNetworkView.getNetwork(), dynNetworkView.getNetworkView()));
	}
	
	@Override
	public void removeDynVizMap(CyNetworkView cynetworkView) 
	{
		DynNetwork<T> dynNet = vizMapManager.getDynVizMap(cynetworkView).getDynNetwork();
		vizMapManager.removeDynVizMap(cynetworkView);
		vizMapManager.addDynVizMap(new DynVizMapImpl<T>(dynNet, cynetworkView));
	}
	
	@Override
	public void addedGraphGraphics(DynNetwork<T> dynNetwork, String fill, String start, String end) 
	{
		this.graphGraphicsList.push(new GraphGraphicsAttribute<T>(dynNetwork,fill,start,end));
	}

	@Override
	public void addedNodeGraphics(DynNetwork<T> dynNetwork, CyNode currentNode, String type, String height, String width, String size, String fill, String linew, String outline, String transparency, String start, String end) 
	{
		this.nodeGraphicsList.push(new NodeGraphicsAttribute<T>(dynNetwork,currentNode,type,height,width,size,fill,linew,outline,transparency,start,end));
	}

	@Override
	public void addedEdgeGraphics(DynNetwork<T> dynNetwork, CyEdge currentEdge, String width, String fill, String transparency, String start, String end) 
	{
		this.edgeGraphicsList.push(new EdgeGraphicsAttribute<T>(dynNetwork,currentEdge,width,fill,transparency,start,end));
	}

}
