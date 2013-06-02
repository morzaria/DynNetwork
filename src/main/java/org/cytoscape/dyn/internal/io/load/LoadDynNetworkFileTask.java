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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

import org.cytoscape.dyn.internal.io.read.AbstractLoadDynNetworkTask;
import org.cytoscape.dyn.internal.io.read.DynNetworkReader;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;


/**
 * <code> LoadDynNetworkFileTask </code> implements {@link Task} and is responsible
 * for loading the network from a given file.
 * 
 * @author rozagh, Sabina Sara Pfister
 *
 */
public final class LoadDynNetworkFileTask extends AbstractLoadDynNetworkTask 
{
	@Tunable(description = "Network file to load", params = "fileCategory=network;input=true")
	public File file;
	
	/**
	 * Get title.
	 */
	@ProvidesTitle
	public String getTitle() 
	{
		return "Load Network from File";
	}
	
	/**
	 * <code> LoadDynNetworkFileTask </code> constructor.
	 * @param factory
	 * @param streamUtil
	 */
	public LoadDynNetworkFileTask(
			final InputStreamTaskFactory factory, 
			final StreamUtil streamUtil)
	{
		super(factory, streamUtil);
	}
	
	/**
	 * Run.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception
	{
		this.taskMonitor = taskMonitor;
		
		if (file == null)
			throw new NullPointerException("No file specified!");

			InputStream stream = streamUtil.getInputStream(file.toURI().toURL());
			if (!stream.markSupported())
				stream = new BufferedInputStream(stream);

			reader = (DynNetworkReader) factory.createTaskIterator(stream, file.getName()).next();

			if (cancelled)
				return;

			if (factory == null)
				throw new NullPointerException("Failed to find appropriate reader for file: " + file);

			insertTasksAfterCurrentTask(reader);
	}
	
}
