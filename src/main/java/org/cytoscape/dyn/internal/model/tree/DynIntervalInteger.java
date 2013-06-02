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


/**
 * <code> DynIntervalInteger </code> implements Integer intervals.
 *  
 * @author Sabina Sara Pfister
 */
public class DynIntervalInteger extends AbstractDynInterval<Integer>
{
	
	/**
	 * <code> DynIntervalInteger </code> constructor.
	 * @param interval
	 * @param onValue
	 */
	public DynIntervalInteger(DynInterval<Integer> interval, int onValue)
	{
		super(interval, onValue);
	}
	
	/**
	 * <code> DynIntervalInteger </code> constructor.
	 * @param interval
	 */
	public DynIntervalInteger(DynInterval<Integer> interval)
	{
		super(interval);
	}
	
	/**
	 * <code> DynIntervalInteger </code> constructor.
	 * @param onValue
	 * @param start
	 * @param end
	 */
	public DynIntervalInteger(int onValue, double start, double end)
	{
		super(onValue, start, end);
	}
	
	/**
	 * <code> DynIntervalInteger </code> constructor.
	 * @param onValue
	 * @param offValue
	 * @param start
	 * @param end
	 */
	public DynIntervalInteger(int onValue,  int offValue, double start, double end)
	{
		super(onValue, offValue, start, end);
	}

	/**
	 * <code> DynIntervalInteger </code> constructor.
	 * @param start
	 * @param end
	 */
	public DynIntervalInteger(double start, double end)
	{
		super(start, end);
	}

	@Override
	public int compareTo(DynInterval<Integer> interval)
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
	public Integer getOnValue()
	{
		return onValue;
	}
	
	@Override
	public Integer getOffValue()
	{
		return offValue;
	}
	
	@Override
	public Integer getOverlappingValue(DynInterval<Integer> interval)
	{
		if (this.compareTo(interval)>0)
			return onValue;
		else
			return offValue;
	}

	@Override
	public Integer interpolateValue(Integer value2, double alpha)
	{
		return (int) ((1-alpha)*value2.intValue()+alpha*onValue.intValue());
	}
	
}
