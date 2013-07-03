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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.dyn.internal.io.read.util.KeyPairs;
import org.cytoscape.dyn.internal.model.attribute.AbstractDynAttributeCheck;
import org.cytoscape.dyn.internal.model.attribute.DynAttribute;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalTree;
import org.cytoscape.dyn.internal.model.tree.DynIntervalTreeImpl;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * <code> DynNetworkImpl </code> implements the interface {@link DynNetwork}
 * and provides method and data structures to record and retrieve network dynamic 
 * information. Each graph, node, edge element or attribute is
 * mapped by a pair of keys {@link KeyPairs} (row, column) to its dynamic attribute
 * {@link DynAttribute}. Each dynamic attribute {@link DynAttribute} contains the list of
 * all the corresponding time intervals for that element or attribute. Since searching of the
 * intervals in a given interval time linearly in the list is computationally prohibitive,
 * we store the intervals separately also in a balanced tree {@link DynIntervalTree},
 * which guarantuees fast retrival of the appropriate intervals. We use several interval trees 
 * for different type of elements or attributes to avoid type checking at runtime.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public final class DynNetworkImpl<T> extends AbstractDynAttributeCheck<T> implements DynNetwork<T>
{	
	private final CyNetwork network;
	
	private final boolean isDirected;

	private final Map<String, Long> cyNodes;
	private final Map<String, Long> cyEdges;
	
	private final Map<KeyPairs,DynAttribute<T>> graphTable;
	private final Map<KeyPairs,DynAttribute<T>> nodeTable;
	private final Map<KeyPairs,DynAttribute<T>> edgeTable;

	private final DynIntervalTree<T> graphTree;
	private final DynIntervalTree<T> nodeTree;
	private final DynIntervalTree<T> edgeTree;

	private final DynIntervalTree<T> graphTreeAttr;
	private final DynIntervalTree<T> nodeTreeAttr;
	private final DynIntervalTree<T> edgeTreeAttr;

	private double minStartTime = Double.POSITIVE_INFINITY;
	private double maxStartTime = Double.NEGATIVE_INFINITY;
	private double minEndTime = Double.POSITIVE_INFINITY;
	private double maxEndTime = Double.NEGATIVE_INFINITY;

	/**
	 * <code> DynNetworkImpl </code> constructor.
	 * @param network
	 * @param groupManager
	 * @param isDirected
	 */
	public DynNetworkImpl(
			final CyNetwork network,
			final boolean isDirected)
	{
		this.network = network;
		this.isDirected = isDirected;

		cyNodes = new HashMap<String, Long>();
		cyEdges = new HashMap<String, Long>();

		this.graphTree = new DynIntervalTreeImpl<T>();
		this.nodeTree = new DynIntervalTreeImpl<T>();
		this.edgeTree = new DynIntervalTreeImpl<T>();
		
		this.graphTreeAttr = new DynIntervalTreeImpl<T>();
		this.nodeTreeAttr = new DynIntervalTreeImpl<T>();
		this.edgeTreeAttr = new DynIntervalTreeImpl<T>();

		this.graphTable = new HashMap<KeyPairs,DynAttribute<T>>();
		this.nodeTable = new HashMap<KeyPairs,DynAttribute<T>>();
		this.edgeTable = new HashMap<KeyPairs,DynAttribute<T>>();

	}

	@Override
	public synchronized void insertGraph(String column, DynInterval<T> interval)
	{
		setMinMaxTime(interval);
		setGraphDynAttribute(network, this.graphTable, this.network.getSUID(), column, interval);
	}

	@Override
	public synchronized void insertNode(CyNode node, String column, DynInterval<T> interval)
	{
		setMinMaxTime(interval);
		setNodeDynAttribute(network, this.nodeTable, node.getSUID(), column, interval);
	}
	
	@Override
	public synchronized void insertEdge(CyEdge edge, String column, DynInterval<T> interval)
	{
		setMinMaxTime(interval);
		setEdgeDynAttribute(network, this.edgeTable, edge.getSUID(), column, interval);
	}
	
	@Override
	public List<DynInterval<T>> searchNodes(DynInterval<T> interval)
	{
		return nodeTree.search(interval);
	}
	
	@Override
	public List<CyNode> getVisibleNodeList(DynInterval<T> interval) 
	{
		List<CyNode> nodeList = new ArrayList<CyNode>();
		for (DynInterval<T> i : nodeTree.search(interval))
		{
			CyNode node = this.getNode(i);
			if (node!=null)
				nodeList.add(node);
		}
		return nodeList;
	}

	@Override
	public List<DynInterval<T>> searchEdges(DynInterval<T> interval)
	{
		return edgeTree.search(interval);
	}
	
	@Override
	public List<CyEdge> getVisibleEdgeList(DynInterval<T> interval) 
	{
		List<CyEdge> edgeList = new ArrayList<CyEdge>();
		for (DynInterval<T> i : edgeTree.search(interval))
		{
			CyEdge edge = this.getEdge(i);
			if (edge!=null)
				edgeList.add(edge);
		}
		return edgeList;
	}
	
	@Override
	public List<DynInterval<T>> searchNodesNot(DynInterval<T> interval)
	{
		return nodeTree.searchNot(interval);
	}
	

	@Override
	public List<CyNode> getVisibleNodeNotList(DynInterval<T> interval) 
	{
		List<CyNode> nodeList = new ArrayList<CyNode>();
		for (DynInterval<T> i : nodeTree.searchNot(interval))
		{
			CyNode node = this.getNode(i);
			if (node!=null)
				nodeList.add(node);
		}
		return nodeList;
	}

	@Override
	public List<DynInterval<T>> searchEdgesNot(DynInterval<T> interval)
	{
		return edgeTree.searchNot(interval);
	}
	

	@Override
	public List<CyEdge> getVisibleEdgeNotList(DynInterval<T> interval) 
	{
		List<CyEdge> edgeList = new ArrayList<CyEdge>();
		for (DynInterval<T> i : edgeTree.searchNot(interval))
		{
			CyEdge edge = this.getEdge(i);
			if (edge!=null)
				edgeList.add(edge);
		}
		return edgeList;
	}
	
	@Override
	public List<DynInterval<T>> searchGraphsAttr(DynInterval<T> interval)
	{
		return graphTreeAttr.search(interval);
	}

	@Override
	public List<DynInterval<T>> searchNodesAttr(DynInterval<T> interval)
	{
		return nodeTreeAttr.search(interval);
	}

	@Override
	public List<DynInterval<T>> searchEdgesAttr(DynInterval<T> interval)
	{
		return edgeTreeAttr.search(interval);
	}
	
	@Override
	public List<DynInterval<T>> searchEdgesAttr(DynInterval<T> interval,String attName) 
	{
		List<DynInterval<T>> list = new ArrayList<DynInterval<T>>();
		for (DynInterval<T> i : edgeTreeAttr.search(interval))
			if (i.getAttribute().getColumn().equals(attName))
				list.add(i);
		return list;
	}

	@Override
	public DynAttribute<T> getDynAttribute(CyNetwork network, String column)
	{
		return this.graphTable.get(new KeyPairs(column, network.getSUID()));
	}

	@Override
	public DynAttribute<T> getDynAttribute(CyNode node, String column)
	{
		return this.nodeTable.get(new KeyPairs(column, node.getSUID()));
	}

	@Override
	public DynAttribute<T> getDynAttribute(CyEdge edge, String column)
	{
		return this.edgeTable.get(new KeyPairs(column, edge.getSUID()));
	}
	
	@Override
	public CyNetwork getNetwork() 
	{
		return this.network;
	}
	
	@Override
	public CyNode getNode(DynInterval<T> interval) 
	{
		return network.getNode(interval.getAttribute().getRow());
	}

	@Override
	public CyEdge getEdge(DynInterval<T> interval) 
	{
		return network.getEdge(interval.getAttribute().getRow());
	}

	@Override
	public long getNode(String id)
	{
		return cyNodes.get(id);
	}

	@Override
	public long getEdge(String id) 
	{
		return cyEdges.get(id);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public String getNetworkLabel()
	{
		return ((T) network.getRow(this.network).get(CyNetwork.NAME, String.class)).toString();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public String getNodeLabel(CyNode node) 
	{
		return ((T) network.getRow(node).get(CyNetwork.NAME, String.class)).toString();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public String getEdgeLabel(CyEdge edge) 
	{
		return ((T) network.getRow(edge).get(CyNetwork.NAME, String.class)).toString();
	}

	@Override
	public boolean containsCyNode(String id)
	{
		return cyNodes.containsKey(id);
	}

	@Override
	public boolean containsCyEdge(String id) 
	{
		return cyEdges.containsKey(id);
	}

	@Override
	public void setCyNode(String id, long value) 
	{
		cyNodes.put(id, value);
	}

	@Override
	public void setCyEdge(String id, long value) 
	{
		cyEdges.put(id, value);
	}
	
    @Override
    public List<String> getGraphAttributes()
    {
            List<String> list = new ArrayList<String>();
            for (CyColumn col : network.getDefaultNetworkTable().getColumns())
                    if ((col.getType()==Double.class || col.getType()==Integer.class) && !col.getName().equals("start") && !col.getName().equals("end"))
                            list.add(col.getName());
            return list;
    }
   
    @Override
    public List<String> getNodeAttributes()
    {
            List<String> list = new ArrayList<String>();
            for (CyColumn col : network.getDefaultNodeTable().getColumns())
                    if ((col.getType()==Double.class || col.getType()==Integer.class) && !col.getName().equals("start") && !col.getName().equals("end"))
                            list.add(col.getName());
            return list;
    }

    @Override
    public List<String> getEdgeAttributes()
    {
            List<String> list = new ArrayList<String>();
            for (CyColumn col : network.getDefaultEdgeTable().getColumns())
                    if ((col.getType()==Double.class || col.getType()==Integer.class) && !col.getName().equals("start") && !col.getName().equals("end"))
                            list.add(col.getName());
            return list;
    }
    
	@Override
	public List<DynInterval<T>> getGraphIntervals(String attName) 
	{
		return graphTable.get(new KeyPairs(attName, this.getNetwork().getSUID())).getIntervalList();
	}

	@Override
	public List<DynInterval<T>> getNodeIntervals(CyNode node, String attName) 
	{
		return nodeTable.get(new KeyPairs(attName, node.getSUID())).getIntervalList();
	}
	
	@Override
	public List<DynInterval<T>> getEdgeIntervals(CyEdge edge, String attName) 
	{
		return edgeTable.get(new KeyPairs(attName, edge.getSUID())).getIntervalList();
	}
	
	// For the moment I insert all intervals only at the end of the network creation, since I may need to modify them.
	// An event based implementation should insert intervals directly, and if there is need for modification, they 
	// should be removed from the interval tree and the new interval inserted. I use this strategy since for importing 
	// xgmml files is less computationally expensive.
	@Override
	public void finalizeNetwork() 
	{
		for (DynAttribute<T> attr : graphTable.values())
			for (DynInterval<T> interval : attr.getIntervalList())
				if (attr.getColumn().equals("name"))
					graphTree.insert(interval, attr.getRow());
				else
					graphTreeAttr.insert(interval, attr.getRow());	

		for (DynAttribute<T> attr : nodeTable.values())
			for (DynInterval<T> interval : attr.getIntervalList())
				if (attr.getColumn().equals("name"))
					nodeTree.insert(interval, attr.getRow());
				else
					nodeTreeAttr.insert(interval, attr.getRow());

		for (DynAttribute<T> attr : edgeTable.values())
			for (DynInterval<T> interval : attr.getIntervalList())
				if (attr.getColumn().equals("name"))
					edgeTree.insert(interval, attr.getRow());
				else
					edgeTreeAttr.insert(interval, attr.getRow());
	}

	@Override
	public double getMinTime()
	{
		if (Double.isInfinite(minStartTime))
			if (Double.isInfinite(minEndTime))
				return -1;
			else
				return minEndTime;
		else
			return Math.min(minStartTime,minEndTime);
	}

	@Override
	public double getMaxTime()
	{
		if (Double.isInfinite(maxEndTime))
			if (Double.isInfinite(maxStartTime))
				return 1;
			else
				return maxStartTime;
		else
			return Math.max(maxEndTime,maxStartTime);
	}
	
	@Override
	public boolean isDirected() 
	{
		return this.isDirected;
	}
	
	@Override
	public T getMinValue(String attName, Class<? extends CyIdentifiable> type)
	{
		T minValue = null;
		if (type==CyNode.class)
		{
			for (long row : cyNodes.values())
				if (this.nodeTable.get(new KeyPairs(attName, row))!=null)
					minValue = compareMin(minValue,this.nodeTable.get(new KeyPairs(attName, row)).getMinValue());
		}
		else if (type==CyEdge.class)
		{
			for (long row : cyEdges.values())
				if (this.edgeTable.get(new KeyPairs(attName, row))!=null)
					minValue = compareMin(minValue,this.edgeTable.get(new KeyPairs(attName, row)).getMinValue());
		}
		return minValue;
	}

	@Override
	public T getMaxValue(String attName, Class<? extends CyIdentifiable> type)
	{
		T maxValue = null;
		if (type==CyNode.class)
		{
			for (long row : cyNodes.values())
				if (this.nodeTable.get(new KeyPairs(attName, row))!=null)
					maxValue = compareMax(maxValue,this.nodeTable.get(new KeyPairs(attName, row)).getMinValue());
		}
		else if (type==CyEdge.class)
		{
			for (long row : cyEdges.values())
				if (this.edgeTable.get(new KeyPairs(attName, row))!=null)
					maxValue = compareMax(maxValue,this.edgeTable.get(new KeyPairs(attName, row)).getMinValue());
		}
		return maxValue;
	}

	private void setMinMaxTime(DynInterval<T> interval)
	{
		double start = interval.getStart();
		double end = interval.getEnd();
		if (!Double.isInfinite(start))
		{
			minStartTime = Math.min(minStartTime, start);
			maxStartTime = Math.max(maxStartTime, start);
		}
		if (!Double.isInfinite(end))
		{
			minEndTime = Math.min(minEndTime, end);
			maxEndTime = Math.max(maxEndTime, end);
		}
	}
	
	@Override
	public void print()
	{
		DecimalFormat formatter = new DecimalFormat("#0.000");
		
		System.out.println("\nELEMENT\tLABEL\tCOLUMN\tVALUE\tSTART\tEND");

		for (DynAttribute<T> attr : graphTable.values())
			for (DynInterval<T> interval : attr.getIntervalList())
			{
				System.out.println("graph" + "\t" + this.getNetworkLabel() + "\t" + attr.getKey().getColumn() + 
						"\t" + interval.getOnValue() + "\t" + formatter.format(interval.getStart()) + "\t" + formatter.format(interval.getEnd()));
			}

		for (DynAttribute<T> attr : nodeTable.values())
			for (DynInterval<T> interval : attr.getIntervalList())
			{
				if (this.getNode(interval)!=null)
					System.out.println("node" + "\t" + this.getNodeLabel(this.getNode(interval)) + "\t" + attr.getKey().getColumn() + 
							"\t" + interval.getOnValue() + "\t" + formatter.format(interval.getStart()) + "\t" + formatter.format(interval.getEnd()));
			}

		for (DynAttribute<T> attr : edgeTable.values())
			for (DynInterval<T> interval : attr.getIntervalList())
			{
				if (this.getEdge(interval)!=null)
					System.out.println("edge" + "\t" + this.getEdgeLabel(this.getEdge(interval)) + "\t" + attr.getKey().getColumn() + 
							"\t" + interval.getOnValue() + "\t" + formatter.format(interval.getStart()) + "\t" + formatter.format(interval.getEnd()));
			}
	}
	
	@Override
	public List<Double> getEventTimeList()
	{
		List<Double> timeList = this.nodeTree.getEventTimeList();
		for (Double d : this.edgeTree.getEventTimeList())
			if (!timeList.contains(d))
				timeList.add(d);
		return sortList(timeList);
	}
	
	@Override
	public List<Double> getEventTimeList(String attName)
	{
		List<Double> timeList = this.nodeTree.getEventTimeList();
		for (Double d : this.edgeTree.getEventTimeList())
			if (!timeList.contains(d))
				timeList.add(d);
		for (Double d : this.edgeTreeAttr.getEventTimeList(attName))
			if (!timeList.contains(d))
				timeList.add(d);
		return sortList(timeList);
	}
	
	private List<Double> sortList(List<Double> timeList)
	{
		double mintime = this.getMinTime();
		double maxTime = this.getMaxTime();
		if (!timeList.contains(mintime))
			timeList.add(mintime);
		if (!timeList.contains(maxTime))
			timeList.add(maxTime);
		Collections.sort(timeList);
		return timeList;
	}
	
	@SuppressWarnings("unchecked")
	private T compareMin(T t1, T t2)
	{
		if (t1==null)
			return t2;
		else if (t2==null)
			return t1;
		else if (t1 instanceof Integer)
		{
			if ((Integer) t1 < (Integer) t2)
				return t1;
			else
				return t2;
		}
		else if (t1 instanceof Double)
		{
			if ((Double) t1 < (Double) t2)
				return t1;
			else
				return t2;
		}
		else if (t1 instanceof Boolean)
		{
			return (T) new Boolean(false);
		}
		else
			return null;
	}
	
	@SuppressWarnings("unchecked")
	private T compareMax(T t1, T t2)
	{
		if (t1==null)
			return t2;
		else if (t2==null)
			return t1;
		else if (t1 instanceof Integer)
		{
			if ((Integer) t1 < (Integer) t2)
				return t2;
			else
				return t1;
		}
		else if (t1 instanceof Double)
		{
			if ((Double) t1 < (Double) t2)
				return t2;
			else
				return t1;
		}
		else if (t1 instanceof Boolean)
		{
			return (T) new Boolean(true);
		}
		else
			return null;
	}
	
}
