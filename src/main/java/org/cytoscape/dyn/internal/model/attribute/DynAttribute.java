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
import org.cytoscape.dyn.internal.model.tree.DynIntervalTree;

/**
 * <code> DynAttribute </code> is the interface to represents dynamic attributes, 
 * i.e. a list of intervals containing the value of type T and the time interval.
 * The intervals list is not used to search for the current intervals, instead intervals
 * are also stored into {@link DynIntervalTree}s, which provide much faster search time.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public interface DynAttribute<T>
{	
	/**
	 * Add time interval to this attribute.
	 * @param interval
	 */
	public void addInterval(DynInterval<T> interval);
	
	/**
	 * Remove time interval from this attribute.
	 * @param interval
	 */
	public void removeInterval(DynInterval<T> interval);
	
	/**
	 * Get time interval list of this attribute.
	 * @return interval list
	 */
    public List<DynInterval<T>> getIntervalList();
    
	/**
	 * Get time interval list of this attribute overlapping with interval.
	 * @return interval list
	 */
    public List<DynInterval<T>> getIntervalList(DynInterval<T> interval);
    
    /**
     * Get time interval of this attribute and of its children.
     * @param list
     * @return interval list
     */
    public List<DynInterval<T>> getRecursiveIntervalList(ArrayList<DynInterval<T>> list);

    /**
     * Set key reference to the value mapped by this attribute in CyTable.
     * @param row
     * @param column
     */
    public void setKey(long row, String column);

    /**
     * Get key reference to the value mapped by this attribute in CyTable.
     * @return pair of keys (row, column)
     */
    public KeyPairs getKey();
	
    /**
     * Get the column reference to the value mapped by this attribute in CyTable.
     * @return column
     */
	public String getColumn();
	
	/**
	 * Get the row reference to the value mapped by this attribute in CyTable.
	 * @return row
	 */
	public long getRow();
	
	/**
	 * Add children attribute.
	 * @param attr
	 */
	public void addChildren(DynAttribute<T> attr);
	
	/**
	 * Remove children attribute.
	 * @param attr
	 */
	public void removeChildren(DynAttribute<T> attr);
	
	/**
	 * Clear all.
	 */
	public void clear();

	/**
	 * Get class type.
	 * @return class type
	 */
	public Class<T> getType();
	
	/**
	 * Get minimum value present in the time interval list.
	 * @return minimum value
	 */
	public T getMinValue();
	
	/**
	 * Get maximum value present in the time interval list.
	 * @return maximum value
	 */
	public T getMaxValue();

	/**
	 * Get minimum time present in the time interval list.
	 * @return minimum time
	 */
    public double getMinTime();

    /**
     * Get maximum time present in the time interval list.
     * @return maximum time
     */
	public double getMaxTime();
	
	/**
	 * Get next successor interval with same value. If none is found, returns null
	 * @param interval
	 * @return interval
	 */
	public DynInterval<T> getSuccesor(DynInterval<T> interval);
	
	/**
	 * Get next predecessor interval with same value. If none is found, returns null.
	 * @param interval
	 * @return interval
	 */
	public DynInterval<T> getPredecessor(DynInterval<T> interval);

}
