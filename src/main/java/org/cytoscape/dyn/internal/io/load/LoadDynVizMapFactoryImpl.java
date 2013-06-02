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

package org.cytoscape.dyn.internal.io.load;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManager;
import org.cytoscape.dyn.internal.vizmapper.model.DynVizMapFactory;
import org.cytoscape.work.TaskIterator;

/**
 * <code> LoadDynVizMapFactoryImpl </code> implements the interface 
 * {@link LoadDynVizMapFactory}.
 * 
 * @author Sabina Sara Pfister
 *
 */
public class LoadDynVizMapFactoryImpl<T> implements LoadDynFactory<T>
{
	private final CyApplicationManager appManager;
	private final DynNetworkViewManager<T> dynNetworkViewManager;
	private final DynVizMapFactory<T> dynVizMapFactory;

	/**
	 * <code> LoadDynVizMapFactoryImpl </code> constructor.
	 * @param appManager
	 * @param dynNetworkViewManager
	 * @param dynVizMapFactory
	 */
	public LoadDynVizMapFactoryImpl(
			CyApplicationManager appManager,
			DynNetworkViewManager<T> dynNetworkViewManager,
			DynVizMapFactory<T> dynVizMapFactory)
	{
		this.appManager = appManager;
		this.dynNetworkViewManager = dynNetworkViewManager;
		this.dynVizMapFactory = dynVizMapFactory;
	}

	@Override
	public TaskIterator creatTaskIterator()
	{
		return new TaskIterator(1, new LoadDynVizMapTask<T>(appManager, dynNetworkViewManager, dynVizMapFactory));
	}

}
