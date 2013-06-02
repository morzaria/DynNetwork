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
import org.cytoscape.dyn.internal.layout.model.DynLayout;
import org.cytoscape.dyn.internal.layout.model.DynLayoutFactory;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

/**
 * <code> LoadDynLayoutTask </code> implements {@link Task} and is responsible
 * for creating the network view layout {@link DynLayout}.
 * 
 * @author Sabina Sara Pfister
 *
 */
public final class LoadDynLayoutTask<T> extends AbstractTask implements Source<T> 
{
	private final CyApplicationManager appManager;
	private final DynNetworkViewManager<T> dynNetworkViewManager;
	
	private DynLayoutFactory<T> layoutFactory;
	
	/**
	 * <code> LoadDynLayoutTask </code> constructor.
	 * @param appManager
	 * @param dynNetworkViewManager
	 * @param dynNetworkViewFactory
	 */
	public LoadDynLayoutTask(
			final CyApplicationManager appManager,
			final DynNetworkViewManager<T> dynNetworkViewManager,
			final DynLayoutFactory<T> dynLayoutFactory)
	{
		this.appManager = appManager;
		this.dynNetworkViewManager = dynNetworkViewManager;
		this.addSink(dynLayoutFactory);
	}
	
	/**
	 * Run.
	 */
	public void run(TaskMonitor tm) throws Exception
	{
		tm.setProgress(0.0);
		DynNetworkView<T> dynNetworkView = dynNetworkViewManager.getDynNetworkView(appManager.getCurrentNetworkView());
		layoutFactory.createDynLayout(dynNetworkView.getNetworkView());
		layoutFactory.finalizeLayout(dynNetworkView);
		tm.setProgress(1.0);
	}

	@Override
	public void addSink(Sink<T> sink) 
	{
		if (sink instanceof DynLayoutFactory<?>)
			this.layoutFactory = (DynLayoutFactory<T>) sink;
	}
	
	@Override
	public void removeSink(Sink<T> sink) 
	{
		if (this.layoutFactory == sink)
			this.layoutFactory = null;
	}
	
}
