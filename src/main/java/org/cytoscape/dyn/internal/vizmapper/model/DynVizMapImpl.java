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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.dyn.internal.io.read.util.KeyPairs;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.attribute.AbstractDynAttributeCheck;
import org.cytoscape.dyn.internal.model.attribute.DynAttribute;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalTree;
import org.cytoscape.dyn.internal.model.tree.DynIntervalTreeImpl;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualProperty;

/**
 * <code> DynVizMapImpl </code> implements the interface {@link DynVizMap}
 * and provides method to store dynamic graphical visualization in the form of 
 * intervals {@link DynInterval} stored in the interval tree {@link DynIntervalTree}.
 * For each graph, node, and edge, we store a series of intervals corresponding to its 
 * graphical attributes in time. The interval tree guarantees that the write and read operation
 * to update the visualization are minimal and asynchronous.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public class DynVizMapImpl<T> extends AbstractDynAttributeCheck<T> implements DynVizMap<T>
{
	private final CyNetworkView view;
	private final DynNetwork<T> dynNetwork;
	
	private List<DynInterval<T>> currentGraphsAttr;
	private List<DynInterval<T>> currentNodesAttr;
	private List<DynInterval<T>> currentEdgesAttr;
	private List<DynInterval<T>> currentTrasnparencyNodesAttr;
	private List<DynInterval<T>> currentTrasnparencyEdgesAttr;
	
	private final Map<KeyPairs,DynAttribute<T>> graphTable;
	private final Map<KeyPairs,DynAttribute<T>> nodeTable;
	private final Map<KeyPairs,DynAttribute<T>> edgeTable;
	
	private final DynIntervalTree<T> graphTreeAttr;
	private final DynIntervalTree<T> nodeTreeAttr;
	private final DynIntervalTree<T> edgeTreeAttr;
	private final DynIntervalTree<T> nodeTrasnparencyTreeAttr;
	private final DynIntervalTree<T> edgeTrasnparencyTreeAttr;
	
	private List<CyNode> transparentNodes;
	private List<CyEdge> transparentEdges;
	
	private final Map<DynAttribute<T>,VisualProperty<T>> visualProprtiesMap;
	
	private final List<DynInterval<T>> emptyList;
	
	/**
	 * <code> DynVizMapImpl </code> constructor.
	 * @param view
	 */
	public DynVizMapImpl(DynNetwork<T> dynNetwork, CyNetworkView networkView)
	{
		this.view = networkView;
		this.dynNetwork = dynNetwork;
		
		this.currentGraphsAttr = new ArrayList<DynInterval<T>>();
		this.currentNodesAttr = new ArrayList<DynInterval<T>>();
		this.currentEdgesAttr = new ArrayList<DynInterval<T>>();
		this.currentTrasnparencyNodesAttr = new ArrayList<DynInterval<T>>();
		this.currentTrasnparencyEdgesAttr = new ArrayList<DynInterval<T>>();

		this.graphTreeAttr = new DynIntervalTreeImpl<T>();
		this.nodeTreeAttr = new DynIntervalTreeImpl<T>();
		this.edgeTreeAttr = new DynIntervalTreeImpl<T>();
		this.nodeTrasnparencyTreeAttr = new DynIntervalTreeImpl<T>();
		this.edgeTrasnparencyTreeAttr = new DynIntervalTreeImpl<T>();

		this.graphTable = new HashMap<KeyPairs,DynAttribute<T>>();
		this.nodeTable = new HashMap<KeyPairs,DynAttribute<T>>();
		this.edgeTable = new HashMap<KeyPairs,DynAttribute<T>>();
		
		this.transparentNodes = new ArrayList<CyNode>();
		this.transparentEdges = new ArrayList<CyEdge>();
		
		this.visualProprtiesMap = new HashMap<DynAttribute<T>,VisualProperty<T>>();
		
		this.emptyList = new ArrayList<DynInterval<T>>();
	}

	@Override
	public void insertGraphGraphics(VisualProperty<T> vp, String column, DynInterval<T> interval) 
	{
		setGraphDynAttribute(view.getModel(), this.graphTable, view.getModel().getSUID(), column, interval);
		this.visualProprtiesMap.put(interval.getAttribute(), vp);
	}

	@Override
	public void insertNodeGraphics(CyNode node, VisualProperty<T> vp, String column, DynInterval<T> interval) 
	{
		setNodeDynAttribute(view.getModel(), this.nodeTable, node.getSUID(), column, interval);
		this.visualProprtiesMap.put(interval.getAttribute(), vp);
	}
	
	@Override
	public void insertEdgeGraphics(CyEdge edge, VisualProperty<T> vp, String column, DynInterval<T> interval) 
	{
		setEdgeDynAttribute(view.getModel(), this.edgeTable, edge.getSUID(), column, interval);
		this.visualProprtiesMap.put(interval.getAttribute(), vp);
	}
	
	@Override
	public void finalize() 
	{
		for (DynAttribute<T> attr : graphTable.values())
			for (DynInterval<T> interval : attr.getIntervalList())
				graphTreeAttr.insert(interval, attr.getRow());	

		for (DynAttribute<T> attr : nodeTable.values())
			for (DynInterval<T> interval : attr.getIntervalList())
				if (attr.getColumn().equals("GRAPHICS.node.transparency"))
				{
					nodeTrasnparencyTreeAttr.insert(interval, attr.getRow());
					this.transparentNodes.add(dynNetwork.getNode(interval));
				}
				else
					nodeTreeAttr.insert(interval, attr.getRow());

		for (DynAttribute<T> attr : edgeTable.values())
			for (DynInterval<T> interval : attr.getIntervalList())
				if (attr.getColumn().equals("GRAPHICS.edge.transparency"))
				{
					edgeTrasnparencyTreeAttr.insert(interval, attr.getRow());
					this.transparentEdges.add(dynNetwork.getEdge(interval));
				}
				else
					edgeTreeAttr.insert(interval, attr.getRow());
	}

	@Override
	public CyNetworkView getNetworkView() 
	{
		return this.view;
	}
	
	@Override
	public VisualProperty<T> getVisualProperty(DynAttribute<T> attr) 
	{
		return this.visualProprtiesMap.get(attr);
	}

	@Override
	public List<DynInterval<T>> searchChangedGraphGraphics(DynInterval<T> interval) 
	{
		if (!graphTable.isEmpty())
		{
			List<DynInterval<T>> tempList = graphTreeAttr.search(interval);
			List<DynInterval<T>> changedList = nonOverlap(currentGraphsAttr, tempList);
			currentGraphsAttr = tempList;
			return changedList;
		}
		else
			return this.emptyList;
	}

	@Override
	public List<DynInterval<T>> searchChangedNodeGraphics(DynInterval<T> interval) 
	{
		if (!nodeTable.isEmpty())
		{
			List<DynInterval<T>> tempList = nodeTreeAttr.search(interval);
			List<DynInterval<T>> changedList = nonOverlap(currentNodesAttr, tempList);
			currentNodesAttr = tempList;
			return changedList;
		}
		else
			return this.emptyList;
	}

	@Override
	public List<DynInterval<T>> searchChangedEdgeGraphics(DynInterval<T> interval) 
	{
		if (!edgeTable.isEmpty())
		{
			List<DynInterval<T>> tempList = edgeTreeAttr.search(interval);
			List<DynInterval<T>> changedList = nonOverlap(currentEdgesAttr, tempList);
			currentEdgesAttr = tempList;
			return changedList;
		}
		else
			return this.emptyList;
	}
	
	@Override
	public List<DynInterval<T>> searchChangedNodeTransparencyGraphics(DynInterval<T> interval) 
	{
		if (!nodeTable.isEmpty())
		{
			List<DynInterval<T>> tempList = nodeTrasnparencyTreeAttr.search(interval);
			List<DynInterval<T>> changedList = nonOverlap(currentTrasnparencyNodesAttr, tempList);
			currentTrasnparencyNodesAttr = tempList;
			return changedList;
		}
		else
			return this.emptyList;
	}

	@Override
	public List<DynInterval<T>> searchChangedEdgeTransparencyGraphics(DynInterval<T> interval) 
	{
		if (!edgeTable.isEmpty())
		{
			List<DynInterval<T>> tempList = edgeTrasnparencyTreeAttr.search(interval);
			List<DynInterval<T>> changedList = nonOverlap(currentTrasnparencyEdgesAttr, tempList);
			currentTrasnparencyEdgesAttr = tempList;
			return changedList;
		}
		else
			return this.emptyList;
	}
	
	private List<DynInterval<T>> nonOverlap(List<DynInterval<T>> list1, List<DynInterval<T>> list2) 
	{
		List<DynInterval<T>> diff = new ArrayList<DynInterval<T>>();
		for (DynInterval<T> i : list1)
			if (!list2.contains(i))
			{
				diff.add(i);
				i.setOn(false);
			}
		for (DynInterval<T> i : list2)
			if (!list1.contains(i))
			{
				diff.add(i);
				i.setOn(true);
			}
		return diff;
	}
	
	@Override
	public void addTransparentNode(CyNode node) 
	{
		this.transparentNodes.add(node);
	}
	
	@Override
	public void addTransparentEdge(CyEdge edge) 
	{
		this.transparentEdges.add(edge);
	}
	
	@Override
	public boolean contrainsTransparentNode(CyNode node) 
	{
		return this.transparentNodes.contains(node);
	}
	
	@Override
	public boolean contrainsTransparentEdge(CyEdge edge) 
	{
		return this.transparentEdges.contains(edge);
	}

	@Override
	public DynNetwork<T> getDynNetwork() 
	{
		return this.dynNetwork;
	}

}
