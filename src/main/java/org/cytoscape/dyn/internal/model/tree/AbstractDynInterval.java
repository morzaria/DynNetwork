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

import org.cytoscape.dyn.internal.model.attribute.DynAttribute;


/**
 * <code> AbstractDynInterval </code> is the abstract class which provides generic methods 
 * to set and get dynamic value intervals. It represent a value list and its right half-open interval. 
 * Intervals are convenient for representing events that each occupy a continuous 
 * period of time. A half-open interval is an ordered pair of real numbers [startTime, 
 * endTime[ with startTime =< endTime, where startTime is included and endTime is excluded.
 *  
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public abstract class AbstractDynInterval<T> implements DynInterval<T>
{
	
	protected T onValue;
	protected T offValue;
	protected double start;
	protected double end;
	protected boolean isOn;
	
	protected DynAttribute<T> attribute;
	
	/**
	 * <code> AbstractDynInterval </code> constructor.
	 * @param interval
	 * @param generic value
	 */
	protected AbstractDynInterval(DynInterval<T> interval, T onValue)
	{
		this.onValue = onValue;
		this.start = interval.getStart();
		this.end = interval.getEnd();
		this.isOn = false;
	}
	
	/**
	 * <code> DynInterval </code> constructor.
	 * @param interval
	 */
	protected AbstractDynInterval(DynInterval<T> interval)
	{
		this(interval, interval.getOnValue());
	}
	
	/**
	 * <code> AbstractDynInterval </code> constructor.
	 * @param generic value
	 * @param start
	 * @param end
	 */
	protected AbstractDynInterval(T onValue, double start, double end)
	{
		this.onValue = onValue;
		this.start = start;
		this.end = end;
		this.isOn = false;
	}
	
	/**
	 * <code> AbstractDynInterval </code> constructor.
	 * @param onValue
	 * @param offValue
	 * @param start
	 * @param end
	 */
	protected AbstractDynInterval(T onValue,  T offValue, double start, double end)
	{
		this.onValue = onValue;
		this.offValue = offValue;
		this.start = start;
		this.end = end;
		this.isOn = false;
	}

	/**
	 * <code> AbstractDynInterval </code> constructor.
	 * @param start
	 * @param end
	 */
	protected AbstractDynInterval(double start, double end)
	{
		this.start = start;
		this.end = end;
	}

	@Override
	public int compareTo(DynInterval<T> interval)
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
	public abstract T getOnValue();
	
	@Override
	public abstract T getOffValue();
	
	@Override
	public abstract T getOverlappingValue(DynInterval<T> interval);
	
	@Override
	public abstract T interpolateValue(T value2, double alpha);

	@Override
	public void setStart(double start)
	{
		this.start = start;
	}

	@Override
	public void setEnd(double end)
	{
		this.end = end;
	}

	@Override
	public double getStart()
	{
		return start;
	}

	@Override
	public double getEnd()
	{
		return end;
	}
	
	@Override
	public double getNext()
	{
		double next = Double.POSITIVE_INFINITY;
		for (DynInterval<T> interval : this.getAttribute().getIntervalList())
			next = Math.min(next,interval.getStart());
		return next;
	}
	
	@Override
	public double getPrevious()
	{
		double previous = Double.NEGATIVE_INFINITY;
		for (DynInterval<T> interval : this.getAttribute().getIntervalList())
			previous = Math.max(previous,interval.getStart());
		return previous;
	}
	
	@Override
	public DynAttribute<T> getAttribute()
	{
		return attribute;
	}

	@Override
	public void setAttribute(DynAttribute<T> attribute) 
	{
		this.attribute = attribute;
	}

	@Override
	public boolean isOn() 
	{
		return isOn;
	}

	@Override
	public void setOn(boolean isOn) 
	{
		this.isOn = isOn;
	}
	
}
