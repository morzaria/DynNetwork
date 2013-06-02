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

package org.cytoscape.dyn.internal.model.tree;

import java.awt.Paint;

import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.attribute.DynAttribute;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.presentation.property.values.NodeShape;

/**
 * <code> AbstractIntervalCheck </code> is the abstract class which provides generic methods 
 * to check interval data consistency.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public abstract class AbstractIntervalCheck<T>
{
	// Checking for graph
	protected DynInterval<T> getInterval(String label, T value, String start, String end)
	{
		DynInterval<T> interval = getInt(value, parseStart(start), parseEnd(end));
		if (interval.getStart()>interval.getEnd())
		{
			System.out.println("\nXGMML Parser Error: invalid interval for graph label=" + label + " start=" + start + " end=" + end + "\n");
			throw new IndexOutOfBoundsException("Invalid interval for graph label=" + label + " start=" + start + " end=" + end);
		}
		return interval;
	}
	
	// Checking for node
	protected DynInterval<T> getInterval(DynNetwork<T> dynNetwork, String label, T value, String start, String end)
	{
		DynAttribute<T> parentAttr = dynNetwork.getDynAttribute(dynNetwork.getNetwork(), CyNetwork.NAME);
		DynInterval<T> interval = getInt(value,
				max(parentAttr.getMinTime(), parseStart(start)),
				min(parentAttr.getMaxTime(), parseEnd(end)) );

		if (interval.getStart()>interval.getEnd())
		{
			System.out.println("\nXGMML Parser Error: invalid interval for node label=" + label + " start=" + start + " end=" + end + "\n");
			throw new IndexOutOfBoundsException("Invalid interval for node label=" + label + " start=" + start + " end=" + end);
		}
		return interval;
	}
	
	// Checking for edge
	protected DynInterval<T> getInterval(DynNetwork<T> dynNetwork, CyNode souce, CyNode target, String label, T value, String start, String end)
	{
		DynAttribute<T> parentAttrSoruce = dynNetwork.getDynAttribute(souce, CyNetwork.NAME);
		DynAttribute<T> parentAttrTarget = dynNetwork.getDynAttribute(target, CyNetwork.NAME);
		DynInterval<T> interval = getInt(value,
				max(max(parentAttrSoruce.getMinTime(),parentAttrTarget.getMinTime()), parseStart(start)) ,
				min(min(parentAttrSoruce.getMaxTime(),parentAttrTarget.getMaxTime()), parseEnd(end)) );
		if (interval.getStart()>interval.getEnd())
		{
			System.out.println("\nXGMML Parser Error: invalid interval for edge label=" + label + " start=" + start + " end=" + end + "\n");
			throw new IndexOutOfBoundsException("Invalid interval for edge label=" + label + " start=" + start + " end=" + end);
		}
		return interval;
	}
	
	// Checking for graph attributes
	protected DynInterval<T> getIntervalAttr(DynNetwork<T> dynNetwork, String label, T value, String start, String end)
	{
		DynAttribute<T> parentAttr = dynNetwork.getDynAttribute(dynNetwork.getNetwork(), CyNetwork.NAME);
		DynInterval<T> interval = getInt(value,
				max(parentAttr.getMinTime(), parseStart(start)),
				min(parentAttr.getMaxTime(), parseEnd(end)) );
		if (interval.getStart()>interval.getEnd())
		{
			System.out.println("\nXGMML Parser Error: skipped invalid interval for graph attr=" + label +" start=" + start + " end=" + end);
			throw new IndexOutOfBoundsException("Invalid interval for graph attr=" + label +" start=" + start + " end=" + end);
		}
		return interval;
	}
	
	// Checking for node attributes
	protected DynInterval<T> getIntervalAttr(DynNetwork<T> dynNetwork, CyNode node, String label, T value, String start, String end)
	{
		DynAttribute<T> parentAttr = dynNetwork.getDynAttribute(node, CyNetwork.NAME);
		DynInterval<T> interval = getInt(value,
				max(parentAttr.getMinTime(), parseStart(start)),
				min(parentAttr.getMaxTime(), parseEnd(end)) );
		if (interval.getStart()>interval.getEnd())
		{
			System.out.println("\nXGMML Parser Error: skipped invalid interval for node label=" + dynNetwork.getNodeLabel(node) + " attr=" + label +" start=" + start + " end=" + end);
			throw new IndexOutOfBoundsException("Invalid interval for node label=" + dynNetwork.getNodeLabel(node) + " attr=" + label +" start=" + start + " end=" + end);
		}
		return interval;
	}

	// Checking for edge attributes
	protected DynInterval<T> getIntervalAttr(DynNetwork<T> dynNetwork, CyEdge edge, String label, T value, String start, String end)
	{
		DynAttribute<T> parentAttr = dynNetwork.getDynAttribute(edge, CyNetwork.NAME);
		DynInterval<T> interval = getInt(value,
				max(parentAttr.getMinTime(), parseStart(start)),
				min(parentAttr.getMaxTime(), parseEnd(end)) );
		if (interval.getStart()>interval.getEnd())
		{
			System.out.println("\nXGMML Parser Error: skipped invalid interval for edge label=" + dynNetwork.getEdgeLabel(edge) + " attr=" + label +" start=" + start + " end=" + end);
			throw new IndexOutOfBoundsException("Invalid interval for edge label=" + dynNetwork.getEdgeLabel(edge) + " attr=" + label +" start=" + start + " end=" + end);
		}
		return interval;
	}
	
	@SuppressWarnings("unchecked")
	private DynInterval<T> getInt(T value, double start, double end)
	{
		if (value instanceof Integer)
			return (DynInterval<T>) new DynIntervalInteger(((Integer) value).intValue(), start, end);
		else if (value instanceof Double)
			return (DynInterval<T>) new DynIntervalDouble(((Double) value).doubleValue(), start, end);
		else if (value instanceof Boolean)
			return (DynInterval<T>) new DynIntervalBoolean(((Boolean) value).booleanValue(), start, end);
		else if (value instanceof String)
			return (DynInterval<T>) new DynIntervalString((String) value, start, end);
		else if (value instanceof Paint)
			return (DynInterval<T>) new DynIntervalPaint((Paint) value, start, end);
		else if (value instanceof NodeShape)
			return (DynInterval<T>) new DynIntervalShape((NodeShape) value, start, end);
		System.out.println("\nXGMML Parser Error: Unrecognized Attribute Class Type: " +  value.getClass());
		throw new NullPointerException("Invalid attribute class " + value.getClass());
	}


	private double min(double a, double b)
	{
		if (a>b)
			return b;
		else
			return a;
	}
	
	private double max(double a, double b)
	{
		if (a<b)
			return b;
		else
			return a;
	}
	
	private double parseStart(String start)
	{
		if (start!=null)
			return Double.parseDouble(start);
		else
			return Double.NEGATIVE_INFINITY;
	}
	
	private double parseEnd(String end)
	{
		if (end!=null)
			return Double.parseDouble(end);
		else
			return Double.POSITIVE_INFINITY;
	}
}
