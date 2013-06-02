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

package org.cytoscape.dyn.internal.action;

import java.awt.event.ActionEvent;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.dyn.internal.model.DynNetworkManager;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.task.select.SelectAllVisibleNodesTaskFactoryImpl;
import org.cytoscape.dyn.internal.view.gui.DynCytoPanelImpl;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.undo.UndoSupport;

/**
 * <code> MenuActionSelectVisibleNodes </code> launches an ActionEvent from the menu 
 * "Select/Show all visible nodes..." to select visible nodes during dynamic 
 * visualizations.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 * @param <C>
 */
public class MenuActionSelectVisibleNodes<T,C> extends AbstractCyAction
{
	private static final long serialVersionUID = 1L;
	
	private final CyApplicationManager appManager;
	private final CyNetworkViewManager viewManager;
	private final DynNetworkManager<T> dynNetworkManager;
	private final UndoSupport undoSupport;
	private final CyEventHelper cyEventHelper;
	private final TaskManager<T,C> taskManager;
	private final DynCytoPanelImpl<T,C> myDynPanel;

	/**
	 * <code> MenuActionSelectVisibleNodes </code> constructor.
	 * @param viewManager
	 * @param dynNetworkManager
	 * @param undoSupport
	 * @param cyEventHelper
	 */
    public MenuActionSelectVisibleNodes(
    		final CyApplicationManager appManager,
    		final CyNetworkViewManager viewManager,
    		final DynNetworkManager<T> dynNetworkManager,
    		final UndoSupport undoSupport,
    		final CyEventHelper cyEventHelper,
    		final TaskManager<T,C> taskManager,
    		final DynCytoPanelImpl<T,C> myDynPanel)
    {
        super("Select all visible nodes");
        this.setPreferredMenu("Select");
        this.appManager = appManager;
        this.viewManager = viewManager;
        this.dynNetworkManager  = dynNetworkManager;
        this.undoSupport = undoSupport;
        this.cyEventHelper = cyEventHelper;
        this.taskManager = taskManager;
        this.myDynPanel = myDynPanel;
    }

    /**
     * Fire action.
     */
    public void actionPerformed(ActionEvent e)
    {
    	DynInterval<T> timeInterval = myDynPanel.getTimeInterval();
    	SelectAllVisibleNodesTaskFactoryImpl<T> selectAllNodesTaskFactory = new SelectAllVisibleNodesTaskFactoryImpl<T>(undoSupport,viewManager,dynNetworkManager,cyEventHelper,timeInterval.getStart(),timeInterval.getEnd());
    	taskManager.execute(selectAllNodesTaskFactory.createTaskIterator(appManager.getCurrentNetwork()));
    }

}
