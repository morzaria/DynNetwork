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

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.dyn.internal.io.read.util.KeyPairs;
import org.cytoscape.dyn.internal.model.tree.DynInterval;

/**
 * <code> AbstractDynAttribute </code> is the abstract class which provides generic methods 
 * to set and get dynamic attributes intervals.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public abstract class AbstractDynAttribute<T> implements DynAttribute<T>
{
	protected Class<T> type;
	
	protected List<DynInterval<T>> intervalList;
	
	protected KeyPairs key;
	
	protected List<DynAttribute<T>> children;
	
	/**
	 * <code> AbstractDynAttribute </code> constructor.
	 * @param type
	 */
	protected AbstractDynAttribute(Class<T> type)
	{
		this.type = type;
		intervalList = new ArrayList<DynInterval<T>>();
		children = new ArrayList<DynAttribute<T>>();
	}
	
	/**
	 * <code> AbstractDynAttribute </code> constructor.
	 * @param interval
	 * @param key
	 */
	protected AbstractDynAttribute(Class<T> type, DynInterval<T> interval, KeyPairs key)
	{
		this(type);
		this.key = key;
		this.intervalList.add(interval);
		interval.setAttribute(this);
	}
		
	@Override
	public void addInterval(DynInterval<T> interval)
	{
		if (interval!=null)
		{
			DynInterval<T> previous = this.getPredecessor(interval);
			DynInterval<T> next = this.getSuccesor(interval);

			if (previous!=null)
				previous.setEnd(interval.getEnd());
			else if (next!=null)
				next.setStart(interval.getStart());
			else
			{
				if(!intervalList.contains(interval)){
					intervalList.add(interval);
				}
				interval.setAttribute(this);
			}
		}
	}
	
	@Override
	public void removeInterval(DynInterval<T> interval)
	{
		intervalList.remove(interval);
	}
	
	@Override
    public List<DynInterval<T>> getIntervalList()
    {
		return intervalList;
	}
	
	@Override
    public List<DynInterval<T>> getIntervalList(DynInterval<T> interval)
    {
		List<DynInterval<T>> list = new ArrayList<DynInterval<T>>();
		for (DynInterval<T> i : intervalList)
			if (i.compareTo(interval)>0)
				list.add(i);
		return list;
	}
    
	@Override
    public List<DynInterval<T>> getRecursiveIntervalList(ArrayList<DynInterval<T>> list)
    {
    	for (DynInterval<T> interval : intervalList)
    		list.add(interval);
    	for (DynAttribute<T> attr : children)
    		attr.getRecursiveIntervalList(list);
    	return list;
    }

	@Override
    public void setKey(long row, String column)
    {
    	this.key = new KeyPairs(column, row);
    }

	@Override
    public KeyPairs getKey() 
    {
    	return key;
    }
	
	@Override
	public String getColumn() 
	{
		return key.getColumn();
	}
	
	@Override
	public long getRow() 
	{
		return key.getRow();
	}
	
	@Override
	public void addChildren(DynAttribute<T> attr)
	{
		this.children.add(attr);
	}
	
	@Override
	public void removeChildren(DynAttribute<T> attr)
	{
		if (this.children.contains(attr))
			this.children.remove(attr);
	}
	
	@Override
	public void clear()
	{
		this.intervalList.clear();
		this.children.clear();
		this.key = null;
	}

	@Override
	public Class<T> getType()
	{
		return type;
	}
	
	@Override
	public abstract T getMinValue();
	
	@Override
	public abstract T getMaxValue();

	@Override
    public double getMinTime()
    {
            double minTime = Double.POSITIVE_INFINITY;
            for (DynInterval<T> i : intervalList)
                    minTime = Math.min(minTime, i.getStart());
            return minTime;
    }

	@Override
	public double getMaxTime()
    {
            double maxTime = Double.NEGATIVE_INFINITY;
            for (DynInterval<T> i : intervalList)
                    maxTime = Math.max(maxTime, i.getEnd());
            return maxTime;
    }

	@Override	
	public DynInterval<T> getSuccesor(DynInterval<T> interval)
	{
		for (DynInterval<T> i : intervalList)
			if (interval.getEnd()==i.getStart() &&
					interval.getOnValue().equals(i.getOnValue()))
				return i;
		return null;
	}

	@Override
	public DynInterval<T> getPredecessor(DynInterval<T> interval)
	{
		for (DynInterval<T> i : intervalList)
			if (interval.getStart()==i.getEnd() &&
					interval.getOnValue().equals(i.getOnValue()))
				return i;
		return null;
	}

}
