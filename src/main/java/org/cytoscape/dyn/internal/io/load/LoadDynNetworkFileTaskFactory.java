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

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

/**
 * <code> LoadDynNetworkFileTaskFactory </code> is interface that provides 
 * a task iterator for loading dynamic networks from files.
 * 
 * @author Sabina Sara Pfister
 *
 */
public interface LoadDynNetworkFileTaskFactory extends TaskFactory
{
	/**
	 * Create a task iterator for loading a network from a file.
	 * The created task runs synchronously in the current thread and does not
	 * create a task monitor.
	 * @param file The file for loading into a network
	 * @return a task iterator of type {@link TaskIterator}
	 */
	TaskIterator creatTaskIterator(final File file);

}