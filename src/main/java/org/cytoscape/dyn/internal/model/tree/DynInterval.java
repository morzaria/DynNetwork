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
 * <code> DynInterval </code> is the interface for storing time intervals,
 * together with a on and off value.
 *  
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public interface DynInterval<T> extends Comparable<DynInterval<T>>
{
	/**
	 * Get interval value when this interval is on. It is used to deal with on events.
	 * @return value
	 */
	public T getOnValue();
	
	/**
	 * Get interval value when this interval is off. It is used to deal with off events.
	 * @return value
	 */
	public T getOffValue();
	
	/**
	 * Interpolate the current value with a second value taking proportion alpha
	 * @param value1
	 * @param value2
	 * @param alpha
	 * @return
	 */
	public T interpolateValue(T value2, double alpha);
	
	/**
	 * Get interval value if the time interval overlaps with the given time interval.
	 * @param interval
	 * @return value
	 */
	public T getOverlappingValue(DynInterval<T> interval);
	
	/**
	 * Set time interval start.
	 * @param start
	 */
	public void setStart(double start);

	/**
	 * Set time interval end.
	 * @param end
	 */
	public void setEnd(double end);

	/**
	 * Get time interval start.
	 * @return start
	 */
	public double getStart();

	/**
	 * Get time interval end.
	 * @return end
	 */
	public double getEnd();
	
	/**
	 * Get time of the closest next interval. Return Double.POSITIVE_INFINITY
	 * if none is found.
	 * @return end
	 */
	public double getNext();
	
	/**
	 * Get time of the closest previous interval. Return Double.NEGATIVE_INFINITY
	 * if none is found.
	 * @return end
	 */
	public double getPrevious();
	
	/**
	 * Get the attribute corresponding to this time interval.
	 * @return
	 */
	public DynAttribute<T> getAttribute();

	/**
	 * Set the attribute corresponding to this time interval.
	 * @param attribute
	 */
	public void setAttribute(DynAttribute<T> attribute);

	/**
	 * Get is on. Is used to know if this interval was turned on or off.
	 * @return
	 */
	public boolean isOn();

	/**
	 * Set is on. Is used to know if this interval was turned on or off..
	 * @param isOn
	 */
	public void setOn(boolean isOn);
       
}

