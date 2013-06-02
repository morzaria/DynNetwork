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

package org.cytoscape.dyn.internal.task.select;

import java.util.Collection;

import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskMonitor;

/**
 * <code> AbstractSelectTask </code> implements an abstract selection task.
 * 
 * @author Cytoscape
 *
 */
public class AbstractSelectTask<T> extends AbstractNetworkTask 
{
	protected final CyNetworkViewManager networkViewManager;
	protected final SelectUtils selectUtils;
	protected final CyEventHelper eventHelper;
	
	protected final DynNetwork<T> dynNet;

	/**
	 * <code> AbstractSelectTask </code> constructor.
	 * @param net
	 * @param dynNet
	 * @param networkViewManager
	 * @param eventHelper
	 */
	public AbstractSelectTask(
			final CyNetwork net,
			final DynNetwork<T> dynNet,
			final CyNetworkViewManager networkViewManager, 
			final CyEventHelper eventHelper) 
	{
		super(net);
		this.dynNet = dynNet;
		this.networkViewManager = networkViewManager;
		this.selectUtils = new SelectUtils();
		this.eventHelper = eventHelper;
	}

	protected final void updateView() 
	{
		// This is necessary, otherwise, this does not update presentation!
		eventHelper.flushPayloadEvents();
		
		final Collection<CyNetworkView> views = networkViewManager.getNetworkViews(network);
		CyNetworkView view = null;
		if(views.size() != 0)
			view = views.iterator().next();
		
		if (view != null)
			view.updateView();
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception 
	{
		
	}
}
