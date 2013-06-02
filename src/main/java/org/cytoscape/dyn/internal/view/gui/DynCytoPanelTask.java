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

package org.cytoscape.dyn.internal.view.gui;

import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * <code> DynCytoPanelTask </code> updates the visualization by resetting the view.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 * @param <C>
 */
public class DynCytoPanelTask<T,C> extends AbstractTask 
{
	private final DynCytoPanel<T,C> panel;
	private final CytoPanel cytoPanelWest;
	
	/**
	 * <code> DynCytoPanelTask </code> constructor.
	 * @param panel
	 * @param cytoPanelWest
	 */
	public DynCytoPanelTask(
			final DynCytoPanel<T,C> panel,
			final CytoPanel cytoPanelWest)
	{
		this.panel = panel;
		this.cytoPanelWest = cytoPanelWest;
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception
	{
		panel.initView();

		if (cytoPanelWest.getState() == CytoPanelState.HIDE)
		{
			cytoPanelWest.setState(CytoPanelState.DOCK);
		}	

		int index = cytoPanelWest.indexOfComponent(panel.getComponent());
		if (index == -1)
		{
			return;
		}
		cytoPanelWest.setSelectedIndex(index);
	}

}
