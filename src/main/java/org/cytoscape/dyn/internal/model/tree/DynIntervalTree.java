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

import java.util.List;

/**
 * <code> DynIntervalTree </code> is the interface for the red-black 
 * interval tree.
 *  
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public interface DynIntervalTree<T>
{
    /**
     * Returns root of the tree.
     * @return root
     */
	public DynNode<T> getRoot();
	
    /**
     * Insert new interval to the tree.
	 * @param interval
	 * @param object id
	 */
	public void insert(DynInterval<T> interval, long id);
   
    /**
     * Removes interval from the tree if exists.
	 * @param interval
	 * @param object id
	 */
	public void remove(DynInterval<T> interval, long id);
	
	/**
	 * Get all intervals contained in this interval tree.
	 * @return list of intervals in this interval tree.
	 */
	public List<DynInterval<T>> getIntervals();
	
	/**
	 * Get all intervals corresponding to this object id
	 * @param object id
	 * @return interval list
	 */
	public List<DynInterval<T>> getIntervals(long id);
	
	/**
	 *  Get a list of times at which events occur.
	 * @return time list
	 */
	public List<Double> getEventTimeList();
	
	/**
	 * Get a list of times at which events occur filtered by attribute name.
	 * @param attName
	 * @return time list
	 */
	public List<Double> getEventTimeList(String attName);
	
    /**
     * Search overlapping intervals in the tree.
	 * @param interval
	 * @return list of overlapping intervals with the given interval.
	 */
	public List<DynInterval<T>> search(DynInterval<T> interval);
	
    /**
     * Search not overlapping intervals in the tree.
	 * @param interval
	 * @return list of not overlapping intervals with the given interval.
	 */
	public List<DynInterval<T>> searchNot(DynInterval<T> interval);

    /**
     * Clear interval tree.
	 */
	public void clear();
	
	/**
	 * Print interval tree.
	 */
	public void print();

       
}

