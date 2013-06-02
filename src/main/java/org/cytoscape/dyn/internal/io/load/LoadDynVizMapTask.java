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
import org.cytoscape.dyn.internal.io.event.Sink;
import org.cytoscape.dyn.internal.io.event.Source;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManager;
import org.cytoscape.dyn.internal.vizmapper.model.DynVizMapFactory;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

/**
 * <code> LoadDynVizMapTask </code> implements {@link Task} and is responsible
 * for creating the network dynamic graphics {@link LoadDynVizMap}.
 * 
 * @author Sabina Sara Pfister
 *
 */
public final class LoadDynVizMapTask<T> extends AbstractTask implements Source<T> 
{
	private final CyApplicationManager appManager;
	private final DynNetworkViewManager<T> dynNetworkViewManager;
	
	private DynVizMapFactory<T> vizMapFactory;
	
	/**
	 * <code> LoadDynVizMapTask </code> constructor.
	 * @param appManager
	 * @param vizMapManager
	 * @param vizMapFactory
	 */
	public LoadDynVizMapTask(
			final CyApplicationManager appManager,
			final DynNetworkViewManager<T> dynNetworkViewManager,
			final DynVizMapFactory<T> vizMapFactory)
	{
		this.appManager = appManager;
		this.dynNetworkViewManager = dynNetworkViewManager;
		this.addSink(vizMapFactory);
	}
	
	/**
	 * Run.
	 */
	public void run(TaskMonitor tm) throws Exception
	{
		tm.setProgress(0.0);
		DynNetworkView<T> dynNetworkView = dynNetworkViewManager.getDynNetworkView(appManager.getCurrentNetworkView());
		vizMapFactory.createDynVizMap(dynNetworkView.getNetwork(),dynNetworkView.getNetworkView());
		vizMapFactory.finalizeDynVizMap(dynNetworkView);
		tm.setProgress(1.0);
	}

	@Override
	public void addSink(Sink<T> sink) 
	{
		if (sink instanceof DynVizMapFactory<?>)
			this.vizMapFactory = (DynVizMapFactory<T>) sink;
	}
	
	@Override
	public void removeSink(Sink<T> sink) 
	{
		if (this.vizMapFactory == sink)
			this.vizMapFactory = null;
	}
	
}
