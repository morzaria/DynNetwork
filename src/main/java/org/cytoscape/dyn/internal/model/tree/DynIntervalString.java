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
 * <code> DynIntervalString </code> implements String intervals.
 *  
 * @author Sabina Sara Pfister
 */
public class DynIntervalString extends AbstractDynInterval<String>
{
	
	/**
	 * <code> DynIntervalString </code> constructor.
	 * @param interval
	 * @param onValue
	 */
	public DynIntervalString(DynInterval<String> interval, String onValue)
	{
		super(interval, onValue);
	}
	
	/**
	 * <code> DynIntervalString </code> constructor.
	 * @param interval
	 */
	public DynIntervalString(DynInterval<String> interval)
	{
		super(interval);
	}
	
	/**
	 * <code> DynIntervalString </code> constructor.
	 * @param onValue
	 * @param start
	 * @param end
	 */
	public DynIntervalString(String onValue, double start, double end)
	{
		super(onValue, start, end);
	}
	
	/**
	 * <code> DynIntervalString </code> constructor.
	 * @param onValue
	 * @param offValue
	 * @param start
	 * @param end
	 */
	public DynIntervalString(String onValue,  String offValue, double start, double end)
	{
		super(onValue, offValue, start, end);
	}

	/**
	 * <code> DynIntervalString </code> constructor.
	 * @param start
	 * @param end
	 */
	public DynIntervalString(double start, double end)
	{
		super(start, end);
	}

	@Override
	public int compareTo(DynInterval<String> interval)
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
	public String getOnValue()
	{
		return onValue;
	}
	
	@Override
	public String getOffValue()
	{
		return offValue;
	}
	
	@Override
	public String getOverlappingValue(DynInterval<String> interval)
	{
		if (this.compareTo(interval)>0)
			return onValue;
		else
			return offValue;
	}
	
	@Override
	public String interpolateValue(String value2, double alpha)
	{
		return onValue;
	}
	
}
