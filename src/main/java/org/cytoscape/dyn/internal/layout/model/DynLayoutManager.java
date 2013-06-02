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

package org.cytoscape.dyn.internal.layout.model;

import java.util.Collection;

import org.cytoscape.view.model.CyNetworkView;

/**
 * <code> DynLayoutManager </code> is the interface of the
 * {@link DynLayout}s manager.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public interface DynLayoutManager<T>
{
	/**
	 * Add layout.
	 * @param dynLayout
	 */
	public void addDynLayout(DynLayout<T> dynLayout);
	
	/**
	 * Add layout parameters.
	 * @param dynLayout
	 * @param context
	 */
	public void addDynContext(DynLayout<T> dynLayout, Object context);

	/**
	 * Get layout associated with the given view.
	 * @param view
	 */
	public DynLayout<T> getDynLayout(CyNetworkView view);
	
	/**
	 * Get layout parameters.
	 * @param dynLayout
	 * @return context
	 */
	public Object getDynContext(DynLayout<T> dynLayout);
	
	/**
	 * Remove DynLayout associated with view if exists.
	 * @param view
	 */
	public void removeDynLayout(CyNetworkView view);
	
	/**
	 * Get all dynLayouts.
	 * @return dynLayouts
	 */
	public Collection<DynLayout<T>> getDynLayouts();
	
}
