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

import org.cytoscape.view.presentation.property.values.NodeShape;


/**
 * <code> DynIntervalPaint </code> implements NodeShape intervals.
 *  
 * @author Sabina Sara Pfister
 */
public class DynIntervalShape extends AbstractDynInterval<NodeShape>
{
	
	/**
	 * <code> DynIntervalShape </code> constructor.
	 * @param interval
	 * @param onValue
	 */
	public DynIntervalShape(DynInterval<NodeShape> interval, NodeShape onValue)
	{
		super(interval, onValue);
	}
	
	/**
	 * <code> DynIntervalShape </code> constructor.
	 * @param interval
	 */
	public DynIntervalShape(DynInterval<NodeShape> interval)
	{
		super(interval);
	}
	
	/**
	 * <code> DynIntervalShape </code> constructor.
	 * @param onValue
	 * @param start
	 * @param end
	 */
	public DynIntervalShape(NodeShape onValue, double start, double end)
	{
		super(onValue, start, end);
	}
	
	/**
	 * <code> DynIntervalShape </code> constructor.
	 * @param onValue
	 * @param offValue
	 * @param start
	 * @param end
	 */
	public DynIntervalShape(NodeShape onValue,  NodeShape offValue, double start, double end)
	{
		super(onValue, offValue, start, end);
	}

	/**
	 * <code> DynIntervalShape </code> constructor.
	 * @param start
	 * @param end
	 */
	public DynIntervalShape(double start, double end)
	{
		super(start, end);
	}

	@Override
	public int compareTo(DynInterval<NodeShape> interval)
	{
		if ((start <= interval.getEnd() && interval.getStart() <= end) &&	
				((start < interval.getEnd() && interval.getStart() < end) ||
				 (interval.getStart() == interval.getEnd() && (start <= interval.getEnd() && interval.getStart() < end)) ||
				 (start == end && (start < interval.getEnd() && interval.getStart() <= end)) ||
				 (start == end && interval.getStart() == interval.getEnd() && start == interval.getEnd())))
			return 1;
		else
			return -1;
	}

	@Override
	public NodeShape getOnValue()
	{
		return onValue;
	}
	
	@Override
	public NodeShape getOffValue()
	{
		return offValue;
	}
	
	@Override
	public NodeShape getOverlappingValue(DynInterval<NodeShape> interval)
	{
		if (this.compareTo(interval)>0)
			return onValue;
		else
			return offValue;
	}
	
	@Override
	public NodeShape interpolateValue(NodeShape value2, double alpha)
	{
		return onValue;
	}
	
}
