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

package org.cytoscape.dyn.internal.io.read;

import java.net.URI;

import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.TaskMonitor;

/**
 * <code> AbstractLoadDynNetworkTask </code> abstract class for the implementation of a 
 * the load network task.
 * 
 * @author Sabina Sara Pfister
 *
 */
public abstract class AbstractLoadDynNetworkTask extends AbstractTask
{	
	protected DynNetworkReader reader;
	protected InputStreamTaskFactory factory;
	protected URI uri;
	protected TaskMonitor taskMonitor;
	protected String name;
	protected boolean interrupted = false;
	protected StreamUtil streamUtil;

	/**
	 * <code> AbstractLoadDynNetworkTask </code> constructor.
	 * @param factory
	 * @param streamUtil
	 */
	protected AbstractLoadDynNetworkTask(
			final InputStreamTaskFactory factory,
			StreamUtil streamUtil)
	{
		this.factory = factory;
		this.streamUtil = streamUtil;
	}
	
	@ProvidesTitle
	public String getTitle()
	{
		return "Import Network";
	}
	
}
