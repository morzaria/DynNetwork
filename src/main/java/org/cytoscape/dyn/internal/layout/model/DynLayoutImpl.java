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

package org.cytoscape.dyn.internal.layout.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.dyn.internal.io.read.util.KeyPairs;
import org.cytoscape.dyn.internal.model.attribute.AbstractDynAttributeCheck;
import org.cytoscape.dyn.internal.model.attribute.DynAttribute;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalTree;
import org.cytoscape.dyn.internal.model.tree.DynIntervalTreeImpl;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * <code> DynLayoutImpl </code> implements the interface {@link DynLayout}
 * and provides method to store dynamic visualization information in the form of 
 * intervals {@link DynInterval} stored in the interval tree {@link DynIntervalTree}.
 * For each node we store a series of intervals corresponding to its x and y
 * positions in time. The interval tree guarantees that the write and read operation
 * to update the visualization are minimal and asynchronous.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public final class DynLayoutImpl<T> extends AbstractDynAttributeCheck<T> implements DynLayout<T>
{
	private final CyNetworkView view;
	
	private List<DynInterval<T>> currentNodesX;
	private List<DynInterval<T>> currentNodesY;
	
	private final DynIntervalTreeImpl<T> nodeXPositionsTree;
	private final DynIntervalTreeImpl<T> nodeYPositionsTree;
	
	private final Map<KeyPairs,DynAttribute<T>> node_X_Pos;
	private final Map<KeyPairs,DynAttribute<T>> node_Y_Pos;
	
	private final List<DynInterval<T>> emptyList;

	/**
	 * <code> DynLayoutImpl </code> constructor.
	 * @param view
	 */
	public DynLayoutImpl(CyNetworkView view)
	{
		this.view = view;

		this.currentNodesX = new ArrayList<DynInterval<T>>();
		this.currentNodesY = new ArrayList<DynInterval<T>>();
		
		this.nodeXPositionsTree = new DynIntervalTreeImpl<T>();
		this.nodeYPositionsTree = new DynIntervalTreeImpl<T>();

		this.node_X_Pos = new HashMap<KeyPairs,DynAttribute<T>>();
		this.node_Y_Pos = new HashMap<KeyPairs,DynAttribute<T>>();
		
		this.emptyList = new ArrayList<DynInterval<T>>();
	}
	
	@Override
	public void insertNodePositionX(CyNode node, DynInterval<T> interval)
	{
		setDynAttributeFast(this.node_X_Pos, node.getSUID(), "node_X_Pos",interval);
	}

	@Override
	public void insertNodePositionY(CyNode node, DynInterval<T> interval)
	{
		setDynAttributeFast(this.node_Y_Pos, node.getSUID(), "node_Y_Pos",interval);
	}
	
	@Override
	public synchronized void removeNode(CyNode node)
	{
		KeyPairs key = new KeyPairs("node_X_Pos", node.getSUID());
		for (DynInterval<T> interval : node_X_Pos.get(key).getIntervalList())
			nodeXPositionsTree.remove(interval, node.getSUID());
		node_X_Pos.remove(key);
		
		key = new KeyPairs("node_Y_Pos", node.getSUID());
		for (DynInterval<T> interval : node_Y_Pos.get(key).getIntervalList())
			nodeYPositionsTree.remove(interval, node.getSUID());
		node_Y_Pos.remove(key);

	}
	
	@Override
	public synchronized void removeAllIntervals()
	{
		nodeXPositionsTree.clear();
		nodeYPositionsTree.clear();
		node_X_Pos.clear();
		node_Y_Pos.clear();
		currentNodesX.clear();
		currentNodesY.clear();
	}
	
	@Override
	public List<DynInterval<T>> getIntervalsX()
	{
		return nodeXPositionsTree.getIntervals();
	}
	
	@Override
	public List<DynInterval<T>> getIntervalsY()
	{
		return nodeYPositionsTree.getIntervals();
	}
	
	@Override
	public List<DynInterval<T>> searchChangedNodePositionsX(DynInterval<T> interval)
	{
		if (!node_X_Pos.isEmpty())
		{
			List<DynInterval<T>> tempList = nodeXPositionsTree.search(interval);
			List<DynInterval<T>> changedList = nonOverlap(currentNodesX, tempList);
			currentNodesX = tempList;
			return changedList;
		}
		return this.emptyList;
	}

	@Override
	public List<DynInterval<T>> searchChangedNodePositionsY(DynInterval<T> interval)
	{
		if (!node_Y_Pos.isEmpty())
		{
			List<DynInterval<T>> tempList = nodeYPositionsTree.search(interval);
			List<DynInterval<T>> changedList = nonOverlap(currentNodesY, tempList);
			currentNodesY = tempList;
			return changedList;
		}
		return this.emptyList;
	}

	@Override
	public void initNodePositions(DynInterval<T> timeInterval) 
	{
		for (DynInterval<T> interval : this.searchChangedNodePositionsX(timeInterval))
		{
			CyNode node = view.getModel().getNode(interval.getAttribute().getKey().getRow());
			if (node!=null)
				view.getNodeView(node).setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, (Double) interval.getOnValue());
		}

		for (DynInterval<T> interval : this.searchChangedNodePositionsY(timeInterval))
		{
			CyNode node = view.getModel().getNode(interval.getAttribute().getKey().getRow());
			if (node!=null)
				view.getNodeView(node).setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, (Double) interval.getOnValue());
		}
	}

	@Override
	public void finalize() 
	{
		for (DynAttribute<T> attr : node_X_Pos.values())
			for (DynInterval<T> interval : attr.getIntervalList())
				nodeXPositionsTree.insert(interval, attr.getRow());	

		for (DynAttribute<T> attr : node_Y_Pos.values())
			for (DynInterval<T> interval : attr.getIntervalList())
				nodeYPositionsTree.insert(interval, attr.getRow());
	}
	
	@Override
	public CyNetworkView getNetworkView() 
	{
		return this.view;
	}
	
	@Override
	public void print()
	{
		DecimalFormat formatter = new DecimalFormat("#0.000");
		
		System.out.println("\nELEMENT\tSUID\tVALUE\tSTART\tEND");

		for (DynAttribute<T> attr : node_X_Pos.values())
			for (DynInterval<T> interval : attr.getIntervalList())
				System.out.println("node X pos" + "\t"  + attr.getRow() + 
						"\t" + interval.getOnValue() + "\t" + formatter.format(interval.getStart()) + "\t" + formatter.format(interval.getEnd()));

		for (DynAttribute<T> attr : node_Y_Pos.values())
			for (DynInterval<T> interval : attr.getIntervalList())
				System.out.println("node Y pos" + "\t" + attr.getRow() +  
							"\t" + interval.getOnValue() + "\t" + formatter.format(interval.getStart()) + "\t" + formatter.format(interval.getEnd()));

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
	
}
