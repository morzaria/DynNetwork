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

import org.cytoscape.dyn.internal.io.read.util.KeyPairs;
import org.cytoscape.dyn.internal.model.tree.DynInterval;

/**
 * <code> DynIntegerAttribute </code> implements Integer attributes and contains
 * a list of their interval times.
 * 
 * @author Sabina Sara Pfister
 * 
 */
public class DynIntegerAttribute extends AbstractDynAttribute<Integer>
{
	/**
	 * <code> DynIntegerAttribute </code> constructor.
	 */
	public DynIntegerAttribute()
	{
		super(Integer.class);
	}
	
	/**
	 * <code> DynIntegerAttribute </code> constructor.
	 * @param interval
	 * @param key
	 */
	public DynIntegerAttribute(DynInterval<Integer> interval, KeyPairs key)
	{
		super(Integer.class,interval,key);
	}
	
	@Override
	public Integer getMinValue()
    {
		int min = Integer.MAX_VALUE;
		for (DynInterval<Integer> i : intervalList)
			min = Math.min(min, i.getOnValue());
    	return min;
    }
    
	@Override
	public Integer getMaxValue()
	{
		int max = Integer.MIN_VALUE;
		for (DynInterval<Integer> i : intervalList)
			max = Math.max(max, i.getOnValue());
    	return max;
	}
	
}
