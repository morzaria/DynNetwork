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

package org.cytoscape.dyn.internal.model.attribute;

import java.awt.Paint;
import java.util.Map;

import org.cytoscape.dyn.internal.io.read.util.KeyPairs;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.presentation.property.values.NodeShape;

/**
 * <code> AbstractDynAttributeCheck </code> is the abstract class which provides generic methods 
 * to check attribute data consistency.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public abstract class AbstractDynAttributeCheck<T>
{
	protected void setGraphDynAttribute(CyNetwork network, Map<KeyPairs,DynAttribute<T>> map, long id, String column, DynInterval<T> interval)
	{
		KeyPairs key = new KeyPairs(column, id);
		if (map.containsKey(key))
		{
			checkGraphIntervals(network,map.get(key), interval);
			extendInterval(map.get(key).getPredecessor(interval), interval, map.get(key).getSuccesor(interval));
		}
		else
			map.put(key, getAttr(interval,key));
	}
	
	protected void setNodeDynAttribute(CyNetwork network, Map<KeyPairs,DynAttribute<T>> map, long id, String column, DynInterval<T> interval)
	{
		KeyPairs key = new KeyPairs(column, id);
		if (map.containsKey(key))
		{
			checkNodeIntervals(network,map.get(key), interval);
			extendInterval(map.get(key).getPredecessor(interval), interval, map.get(key).getSuccesor(interval));
		}
		else
			map.put(key, getAttr(interval,key));
	}
	
	protected void setEdgeDynAttribute(CyNetwork network, Map<KeyPairs,DynAttribute<T>> map, long id, String column, DynInterval<T> interval)
	{
		KeyPairs key = new KeyPairs(column, id);
		if (map.containsKey(key))
		{
			checkEdgeIntervals(network,map.get(key), interval);
			extendInterval(map.get(key).getPredecessor(interval), interval, map.get(key).getSuccesor(interval));
		}
		else
			map.put(key, getAttr(interval,key));
	}

	protected void setDynAttributeFast(Map<KeyPairs,DynAttribute<T>> map, long id, String column, DynInterval<T> interval)
	{
		KeyPairs key = new KeyPairs(column, id);
		if (map.containsKey(key))
		{
			map.get(key).addInterval(interval);
			extendInterval(map.get(key).getPredecessor(interval), interval, map.get(key).getSuccesor(interval));
		}
		else
			map.put(key, getAttr(interval,key));
	}
	
	@SuppressWarnings("unchecked")
	protected DynAttribute<T> getAttr(DynInterval<T> interval, KeyPairs key)
	{
		if (interval.getOnValue() instanceof Integer)
			return (DynAttribute<T>) new DynIntegerAttribute((DynInterval<Integer>) interval, key);
		else if (interval.getOnValue() instanceof Double)
			return (DynAttribute<T>) new DynDoubleAttribute((DynInterval<Double>) interval, key);
		else if (interval.getOnValue() instanceof Boolean)
			return (DynAttribute<T>) new DynBooleanAttribute((DynInterval<Boolean>) interval, key);
		else if (interval.getOnValue() instanceof String)
			return (DynAttribute<T>) new DynStringAttribute((DynInterval<String>) interval, key);
		else if (interval.getOnValue() instanceof Paint)
			return (DynAttribute<T>) new DynPaintAttribute((DynInterval<Paint>) interval, key);
		else if (interval.getOnValue() instanceof NodeShape)
			return (DynAttribute<T>) new DynShapeAttribute((DynInterval<NodeShape>) interval, key);
		System.out.println("\nXGMML Parser Error: Unrecognized Attribute Class Type: " +  interval.getOnValue().getClass());
		throw new NullPointerException("Invalid attribute class " + interval.getOnValue().getClass());
	}
	
	protected void checkGraphIntervals(CyNetwork network, DynAttribute<T> attr, DynInterval<T> interval)
	{
		for (DynInterval<T> i : attr.getIntervalList())
			if (interval!=i && i.compareTo(interval)>0)
			{
				if (interval.getStart()>i.getStart() && interval.getEnd()>=i.getEnd())
					i.setEnd(interval.getStart());
				else if (interval.getStart()==i.getStart() && interval.getEnd()>i.getEnd())
					interval.setStart(i.getEnd());
				else if (interval.getEnd()<i.getEnd() && interval.getStart()<=i.getStart())
					i.setStart(interval.getEnd());
				else if (interval.getEnd()==i.getEnd() && interval.getStart()<i.getStart())
					interval.setEnd(i.getStart());
				else if (interval.getEnd()==i.getEnd() && interval.getStart()==i.getStart() && interval.getOnValue().equals(i.getOnValue()))
				{
//					String label = ((T) network.getRow(network).get(CyNetwork.NAME, String.class)).toString();
//					System.out.println("\nXGMML Parser Warning: skipping duplicate attribute interval for graph label=" + label + 
//							"\n  > attr=" + attr.getColumn() + " value=" + i.getOnValue() + " start=" + i.getStart() + " end=" + i.getEnd() +
//							"\n  > attr=" + attr.getColumn() + " value=" + interval.getOnValue() + " start=" + interval.getStart() + " end=" + interval.getEnd());
					return;
				}
				else
				{
//					String label = ((T) network.getRow(network).get(CyNetwork.NAME, String.class)).toString();
//					System.out.println("\nXGMML Parser Warning: inconsistent attribute interval for graph label=" + label + 
//							"\n  > attr=" + attr.getColumn() + " value=" + i.getOnValue() + " start=" + i.getStart() + " end=" + i.getEnd() +
//							"\n  > attr=" + attr.getColumn() + " value=" + interval.getOnValue() + " start=" + interval.getStart() + " end=" + interval.getEnd());
					return;
				}
			}
		attr.addInterval(interval);
	}
	
	protected void checkNodeIntervals(CyNetwork network, DynAttribute<T> attr, DynInterval<T> interval)
	{
		for (DynInterval<T> i : attr.getIntervalList())
			if (interval!=i && i.compareTo(interval)>0)
			{
				if (interval.getStart()>i.getStart() && interval.getEnd()>=i.getEnd())
					i.setEnd(interval.getStart());
				else if (interval.getStart()==i.getStart() && interval.getEnd()>i.getEnd())
					interval.setStart(i.getEnd());
				else if (interval.getEnd()<i.getEnd() && interval.getStart()<=i.getStart())
					i.setStart(interval.getEnd());
				else if (interval.getEnd()==i.getEnd() && interval.getStart()<i.getStart())
					interval.setEnd(i.getStart());
				else if (interval.getEnd()==i.getEnd() && interval.getStart()==i.getStart() && interval.getOnValue().equals(i.getOnValue()))
				{
//					String label = ((T) network.getRow(network.getNode(attr.getRow())).get(CyNetwork.NAME, String.class)).toString();
//					System.out.println("\nXGMML Parser Warning: skipping duplicate attribute interval for node label=" + label + 
//							"\n  > attr=" + attr.getColumn() + " value=" + i.getOnValue() + " start=" + i.getStart() + " end=" + i.getEnd() +
//							"\n  > attr=" + attr.getColumn() + " value=" + interval.getOnValue() + " start=" + interval.getStart() + " end=" + interval.getEnd());
					return;
				}
				else
				{
//					String label = ((T) network.getRow(network.getNode(attr.getRow())).get(CyNetwork.NAME, String.class)).toString();
//					System.out.println("\nXGMML Parser Warning: skipping inconsistent attribute interval for node label=" + label + 
//							"\n  > attr=" + attr.getColumn() + " value=" + i.getOnValue() + " start=" + i.getStart() + " end=" + i.getEnd() +
//							"\n  > attr=" + attr.getColumn() + " value=" + interval.getOnValue() + " start=" + interval.getStart() + " end=" + interval.getEnd());
					return;
				}
			}
		attr.addInterval(interval);
	}
	
	protected void checkEdgeIntervals(CyNetwork network, DynAttribute<T> attr, DynInterval<T> interval)
	{
		for (DynInterval<T> i : attr.getIntervalList())
			if (interval!=i && i.compareTo(interval)>0)
			{
				if (interval.getStart()>i.getStart() && interval.getEnd()>=i.getEnd())
					i.setEnd(interval.getStart());
				else if (interval.getStart()==i.getStart() && interval.getEnd()>i.getEnd())
					interval.setStart(i.getEnd());
				else if (interval.getEnd()<i.getEnd() && interval.getStart()<=i.getStart())
					i.setStart(interval.getEnd());
				else if (interval.getEnd()==i.getEnd() && interval.getStart()<i.getStart())
					interval.setEnd(i.getStart());
				else if (interval.getEnd()==i.getEnd() && interval.getStart()==i.getStart() && interval.getOnValue().equals(i.getOnValue()))
				{
//					String label = ((T) network.getRow(network.getEdge(attr.getRow())).get(CyNetwork.NAME, String.class)).toString();
//					System.out.println("\nXGMML Parser Warning: skipping duplicate attribute interval for edge label=" + label + 
//							"\n  > attr=" + attr.getColumn() + " value=" + i.getOnValue() + " start=" + i.getStart() + " end=" + i.getEnd() +
//							"\n  > attr=" + attr.getColumn() + " value=" + interval.getOnValue() + " start=" + interval.getStart() + " end=" + interval.getEnd());
					return;
				}
				else
				{   
//					String label = ((T) network.getRow(network.getEdge(attr.getRow())).get(CyNetwork.NAME, String.class)).toString();
//					System.out.println("\nXGMML Parser Warning: inconsistent attribute interval for edge label=" + label + 
//							"\n  > attr=" + attr.getColumn() + " value=" + i.getOnValue() + " start=" + i.getStart() + " end=" + i.getEnd() +
//							"\n  > attr=" + attr.getColumn() + " value=" + interval.getOnValue() + " start=" + interval.getStart() + " end=" + interval.getEnd());
					return;
				}
			}
		attr.addInterval(interval);
	}
	
	protected void extendInterval(DynInterval<T> previous, DynInterval<T> interval, DynInterval<T> next)
	{
		if (previous!=null)
		{
			interval.setStart(previous.getStart());
			interval.getAttribute().removeInterval(previous);
		}
		if (next!=null)
		{
			interval.setEnd(previous.getEnd());
			interval.getAttribute().removeInterval(next);
		}
	}

}
