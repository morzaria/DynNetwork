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

import java.awt.Color;
import java.awt.Paint;


/**
 * <code> DynIntervalPaint </code> implements Paint intervals.
 *  
 * @author Sabina Sara Pfister
 */
public class DynIntervalPaint extends AbstractDynInterval<Paint>
{
	private int redValue;
	private int greenValue;
	private int blueValue;
	
	/**
	 * <code> DynIntervalPaint </code> constructor.
	 * @param interval
	 * @param onValue
	 */
	public DynIntervalPaint(DynInterval<Paint> interval, Paint onValue)
	{
		super(interval, onValue);
		redValue = ((Color) onValue).getRed();
		greenValue = ((Color) onValue).getGreen();
		blueValue = ((Color) onValue).getBlue();
	}
	
	/**
	 * <code> DynIntervalPaint </code> constructor.
	 * @param interval
	 */
	public DynIntervalPaint(DynInterval<Paint> interval)
	{
		super(interval);
		redValue = ((Color) interval.getOnValue()).getRed();
		greenValue = ((Color) interval.getOnValue()).getGreen();
		blueValue = ((Color) interval.getOnValue()).getBlue();
	}
	
	/**
	 * <code> DynIntervalPaint </code> constructor.
	 * @param onValue
	 * @param start
	 * @param end
	 */
	public DynIntervalPaint(Paint onValue, double start, double end)
	{
		super(onValue, start, end);
		redValue = ((Color) onValue).getRed();
		greenValue = ((Color) onValue).getGreen();
		blueValue = ((Color) onValue).getBlue();
	}
	
	/**
	 * <code> DynIntervalPaint </code> constructor.
	 * @param onValue
	 * @param offValue
	 * @param start
	 * @param end
	 */
	public DynIntervalPaint(Paint onValue,  Paint offValue, double start, double end)
	{
		super(onValue, offValue, start, end);
		redValue = ((Color) onValue).getRed();
		greenValue = ((Color) onValue).getGreen();
		blueValue = ((Color) onValue).getBlue();
	}

	/**
	 * <code> DynIntervalPaint </code> constructor.
	 * @param start
	 * @param end
	 */
	public DynIntervalPaint(double start, double end)
	{
		super(start, end);
	}

	@Override
	public int compareTo(DynInterval<Paint> interval)
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
	public Paint getOnValue()
	{
		return onValue;
	}
	
	@Override
	public Paint getOffValue()
	{
		return offValue;
	}
	
	@Override
	public Paint getOverlappingValue(DynInterval<Paint> interval)
	{
		if (this.compareTo(interval)>0)
			return onValue;
		else
			return offValue;
	}
	
	@Override
	public Paint interpolateValue(Paint value2, double alpha)
	{
		return new Color(
				(int)((1-alpha)*((Color) value2).getRed()+alpha*this.redValue),
				(int)((1-alpha)*((Color) value2).getGreen()+alpha*this.greenValue),
				(int)((1-alpha)*((Color) value2).getBlue()+alpha*this.blueValue));
	}
	
}
