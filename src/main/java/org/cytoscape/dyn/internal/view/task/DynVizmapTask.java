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

package org.cytoscape.dyn.internal.view.task;

import java.util.ArrayList;
import java.util.Collection;

import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.ContinuousMappingPoint;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * <code> DynVizmapTask </code> is responsible for updating the {@link VisualStyle} by computing
 * the mapped values range in oder to take into account the whole dynamic range and not only the 
 * current time snapshot. All points are linearly re-mapped to the resized range. At the moment only
 * support for continuous VisualMappings is implemented.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public final class DynVizmapTask<T> extends AbstractTask
{
	private DynNetworkView<T> view;
	private VisualStyle visualStyle;
	private final BlockingQueue queue;
	
	/**
	 * <code> DynVizmapTask </code> constructor.
	 * @param view
	 * @param visualStyle
	 * @param continousFactory
	 * @param discreteFactory
	 * @param passthroughFactory
	 * @param queue
	 */
	public DynVizmapTask(
			DynNetworkView<T> view, 
			VisualStyle visualStyle,
			final BlockingQueue queue) 
	{
		this.view = view;
		this.visualStyle = visualStyle;
		this.queue = queue;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception 
	{
		queue.lock();
		visualStyle = view.getCurrentVisualStyle();
		Collection<VisualMappingFunction<?, ?>> mappings = visualStyle.getAllVisualMappingFunctions();
		for (VisualMappingFunction<?, ?> visualMapping : mappings)
			if (visualMapping instanceof ContinuousMapping<?,?>)
				remap(visualMapping);
		
//			else if (visualMapping instanceof DiscreteMapping<?,?>)
//			{
//
//			}
//			else if (visualMapping instanceof PassthroughMapping<?,?>)
//			{
//
//			}
		
		visualStyle.apply(view.getNetworkView());
		view.updateView();
		
		queue.unlock();
	}
	
	@SuppressWarnings("unchecked")
	private <K, V> void remap(VisualMappingFunction<?, ?> originalMapping)
	{
		ContinuousMapping<K, V> original = (ContinuousMapping<K, V>) originalMapping;
		ArrayList<ContinuousMappingPoint<K, V>>  points = new ArrayList<ContinuousMappingPoint<K, V>>(original.getAllPoints());

		String name = original.getMappingColumnName();
		VisualProperty vp = original.getVisualProperty();
		K newMin = (K) view.getNetwork().getMinValue(name, vp.getTargetDataType());
		K newMax = (K) view.getNetwork().getMaxValue(name, vp.getTargetDataType());
		
		K min = points.get(0).getValue();
		K max = points.get(points.size()-1).getValue();
		
		for (int i=1;i<points.size()-1;i++)
			points.get(i).setValue(
					resize(points.get(i).getValue(),min,max, newMin, newMax));


		points.get(0).setValue(newMin);
		points.get(points.size()-1).setValue(newMax);
	}
	
//	@SuppressWarnings("unchecked")
//	private <K, V> void printContinousMapping(VisualMappingFunction<?, ?> visualMapping)
//	{
//		System.out.println("\nMAPPING attribute : " + visualMapping.getMappingColumnName());
//		ContinuousMapping<K, V> mapping = (ContinuousMapping<K, V>) visualMapping;
//		for (ContinuousMappingPoint<K, V> point : mapping.getAllPoints())
//		{
//			System.out.println(
//					" K=" + point.getValue() + 
//					" lesser=" + point.getRange().lesserValue +
//					" equal=" + point.getRange().equalValue +
//					" greater=" + point.getRange().greaterValue);
//		}
//	}

	@SuppressWarnings("unchecked")
	private <K> K resize(K value, K min, K max, K newMin, K newMax)
	{
		if (value instanceof Integer)
		{
			return (K) new Integer(((Integer)(newMax)-(Integer)(newMin))*(((Integer)value - (Integer) min)/(Integer) max) + (Integer) newMin);
		}
		else if (value instanceof Double)
		{
			return (K) new Double(((Double)(newMax)-(Double)(newMin))*(((Double)value - (Double) min)/(Double) max) + (Double) newMin);
		}
		else if (value instanceof Boolean)
		{
			return (K) new Boolean(false);
		}
		else
			return null;
	}

}
