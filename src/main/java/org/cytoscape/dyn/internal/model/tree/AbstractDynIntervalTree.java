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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code> AbstractDynIntervalTree </code> abstract class for the implementation of a 
 * the interval tree.
 *  
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public abstract class AbstractDynIntervalTree<T> implements DynIntervalTree<T>
{	
	protected final DynNode<T> root;
	protected final DynNode<T> nil;
	
	protected final Map<Long,List<DynInterval<T>>> intervalMap;
	protected List<DynInterval<T>> currentIntervals;
	
	/**
	 * <code> AbstractDynIntervalTree </code> constructor.
	 */
	protected AbstractDynIntervalTree()
	{
		this.nil = new DynNode<T>();
		this.root = new DynNode<T>();
		
		this.intervalMap = new HashMap<Long,List<DynInterval<T>>>();
		this.currentIntervals = new ArrayList<DynInterval<T>>();
	}
	
	/**
	 * <code> AbstractDynIntervalTree </code> constructor.
	 * @param root
	 */
	protected AbstractDynIntervalTree(DynNode<T> root)
	{
		this();
		this.root.setLeft(root);
	}
	
	/**
	 * <code> AbstractDynIntervalTree </code> constructor.
	 * @param interval
	 * @param id
	 */
	protected AbstractDynIntervalTree(DynInterval<T> interval, long id)
	{
		this(new DynNode<T>(interval, new DynNode<T>()));
		addInterval(id, interval);
	}

	@Override
	public DynNode<T> getRoot()
	{
		return root.getLeft();
	}
	
	@Override
	public void insert(DynInterval<T> interval, long id)
	{	
		insert(new DynNode<T>(interval, nil), root.getLeft());
		addInterval(id, interval);
	}
	
	abstract protected void insert(DynNode<T> z, DynNode<T> root);
	
	@Override
	public void remove(DynInterval<T> interval, long id)
	{
		DynNode<T> z = searchThisNode(interval);
		if (z!=null)
			if (z.getIntervalList().size()>1)
				z.removeInterval(interval);
			else
				remove(z);	
		removeInterval(id, interval);
	}
	
	protected DynNode<T> searchThisNode(DynInterval<T> interval)
	{
		return root.getLeft().searchThisNode(interval);
	}
	
	abstract protected void remove(DynNode<T> z);
	
	@Override
	public List<DynInterval<T>> getIntervals()
	{
		return root.getLeft().getIntervals(new ArrayList<DynInterval<T>>());
	}
	
	@Override
	public List<DynInterval<T>> searchNot(DynInterval<T> interval)
	{
		return root.getLeft().searchNot(new ArrayList<DynInterval<T>>(), interval);
	}
	
	@Override
	public List<DynInterval<T>> search(DynInterval<T> interval)
	{
		return root.getLeft().search(new ArrayList<DynInterval<T>>(), interval);
	}

	protected List<DynNode<T>> searchNodes(DynInterval<T> interval)
	{
		return root.getLeft().searchNodes(interval, new ArrayList<DynNode<T>>());
	}
	
	@Override
	public void clear()
	{
		this.nil.setParent(this.nil);
		this.nil.setLeft(this.nil);
		this.nil.setRight(this.nil);
		this.root.setLeft(this.nil);
	}
	
	@Override
	public List<DynInterval<T>> getIntervals(long id)
	{
		if (this.intervalMap.containsKey(id))
			return this.intervalMap.get(id);
		else
			return new ArrayList<DynInterval<T>>();
	}
	
	@Override
	public List<Double> getEventTimeList()
	{
		List<Double> timeList = new ArrayList<Double>();
		for (DynInterval<T> interval : this.getIntervals())
		{
			if (interval.getStart()!=Double.NEGATIVE_INFINITY && !timeList.contains(interval.getStart()))
				timeList.add(interval.getStart());
			if (interval.getEnd()!=Double.POSITIVE_INFINITY && !timeList.contains(interval.getEnd()))
				timeList.add(interval.getEnd());
		}
		return timeList;
	}
	
	@Override
	public List<Double> getEventTimeList(String attName)
	{
		List<Double> timeList = new ArrayList<Double>();
		for (DynInterval<T> interval : this.getIntervals())
		{
			if (interval.getAttribute().getColumn().equals(attName))
			{
				if (interval.getStart()!=Double.NEGATIVE_INFINITY && !timeList.contains(interval.getStart()))
					timeList.add(interval.getStart());
				if (interval.getEnd()!=Double.POSITIVE_INFINITY && !timeList.contains(interval.getEnd()))
					timeList.add(interval.getEnd());
			}
		}
		return timeList;
	}

	protected void addInterval(long id, DynInterval<T> interval)
	{
		if (!this.intervalMap.containsKey(id))
			this.intervalMap.put(id, new ArrayList<DynInterval<T>>());
		this.intervalMap.get(id).add(interval);	
	}
	
	protected void removeInterval(long id, DynInterval<T> interval)
	{
		if (this.intervalMap.containsKey(id))
			if (this.intervalMap.get(id).contains(interval))
				this.intervalMap.get(id).remove(interval);	
	}
	
	protected void removeInterval(long id)
	{
		this.intervalMap.remove(id);
	}
	
	@Override
	public void print()
	{
		System.out.println(this.root.getLeft().print(""));
	}

}
