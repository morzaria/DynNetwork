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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TunableSetter;

/**
 * <code> LoadDynNetworkFileTaskFactoryImpl </code> implements the interface 
 * {@link LoadDynNetworkFileTaskFactory}.
 * 
 * @author Sabina Sara Pfister
 *
 */
public final class LoadDynNetworkFileTaskFactoryImpl extends AbstractTaskFactory implements LoadDynNetworkFileTaskFactory {

	private final InputStreamTaskFactory factory;
	private final TunableSetter tunableSetter;
	private final StreamUtil streamUtil;

	/**
	 * <code> LoadDynNetworkFileTaskFactoryImpl </code> constructor.
	 * @param factory
	 * @param tunableSetter
	 * @param streamUtil
	 */
	public LoadDynNetworkFileTaskFactoryImpl(
			final InputStreamTaskFactory factory, 
			final TunableSetter tunableSetter,
			final StreamUtil streamUtil)
	{	
		this.factory = factory;
		this.tunableSetter = tunableSetter;
		this.streamUtil = streamUtil;
	}
	
	/**
	 * Create task iterator.
	 */
	public TaskIterator createTaskIterator()
	{
		return new TaskIterator(1, new LoadDynNetworkFileTask(factory, streamUtil));
	}

	@Override
	public TaskIterator creatTaskIterator(File file)
	{
		final Map<String, Object> m = new HashMap<String, Object>();
		m.put("file", file);
		return tunableSetter.createTaskIterator(this.createTaskIterator(), m); 
	}
}