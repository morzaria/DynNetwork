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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.dyn.internal.io.load.LoadDynFactory;
import org.cytoscape.dyn.internal.io.load.LoadDynLayoutFactoryImpl;
import org.cytoscape.dyn.internal.io.load.LoadDynNetworkFileTaskFactory;
import org.cytoscape.dyn.internal.io.load.LoadDynNetworkFileTaskFactoryImpl;
import org.cytoscape.dyn.internal.io.load.LoadDynNetworkViewFactoryImpl;
import org.cytoscape.dyn.internal.io.load.LoadDynVizMapFactoryImpl;
import org.cytoscape.dyn.internal.io.read.xgmml.XGMMLDynFileFilter;
import org.cytoscape.dyn.internal.io.read.xgmml.XGMMLDynNetworkReaderFactory;
import org.cytoscape.dyn.internal.io.read.xgmml.XGMMLDynParser;
import org.cytoscape.dyn.internal.layout.model.DynLayoutFactory;
import org.cytoscape.dyn.internal.model.DynNetworkFactory;
import org.cytoscape.dyn.internal.model.DynNetworkManager;
import org.cytoscape.dyn.internal.view.gui.DynCytoPanel;
import org.cytoscape.dyn.internal.view.gui.DynCytoPanelImpl;
import org.cytoscape.dyn.internal.view.gui.DynCytoPanelTask;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewFactory;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManager;
import org.cytoscape.dyn.internal.vizmapper.model.DynVizMapFactory;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableSetter;

/**
 * <code> MenuActionLoadXGMML </code> launches an ActionEvent from the menu 
 * "File/Import/Dynamic Network/XGMML File..." to import dynamic networks into Cytoscape, and is
 * also responsible to create the panel {@link CytoPanel} to control the visualization
 * of dynamic graphical information.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 * @param <C>
 */
public class MenuActionLoadXGMML<T,C> extends AbstractCyAction
{
	private static final long serialVersionUID = 1L;
	
	private final CySwingApplication desktopApp;
	private final CyApplicationManager appManager;
	
	private final CytoPanel cytoPanelWest;
	private final DynCytoPanel<T,C> myDynPanel;
	private final TaskManager<T,C> taskManager;
	private final DynNetworkManager<T> dynNetworkManager;
	private final DynNetworkViewManager<T> dynNetworkViewManager;
	private final DynNetworkFactory<T> dynNetworkFactory;
	private final DynNetworkViewFactory<T> dynNetworkViewFactory;
	private final DynLayoutFactory<T> dynLayoutFactory;
	private final DynVizMapFactory<T> vizMapFactory;
	private final FileUtil fileUtil;
	private final StreamUtil streamUtil;
	private final TunableSetter tunableSetterServiceRef;

	/**
	 * <code> MenuActionLoadXGMML </code> constructor.
	 * @param desktopApp
	 * @param appManager
	 * @param myDynPanel
	 * @param taskManager
	 * @param dynNetworkManager
	 * @param dynNetworkViewManager
	 * @param dynLayoutManager
	 * @param dynNetworkFactory
	 * @param dynNetworkViewFactory
	 * @param dynLayoutFactory
	 * @param vizMapFactory
	 * @param fileUtil
	 * @param streamUtil
	 * @param tunableSetterServiceRef
	 */
    public MenuActionLoadXGMML(
    		final CySwingApplication desktopApp,
    		final CyApplicationManager appManager,
    		final DynCytoPanelImpl<T,C> myDynPanel,
    		final TaskManager<T,C> taskManager,
    		final DynNetworkManager<T> dynNetworkManager,
    		final DynNetworkViewManager<T> dynNetworkViewManager,
    		final DynNetworkFactory<T> dynNetworkFactory,
    		final DynNetworkViewFactory<T> dynNetworkViewFactory,
    		final DynLayoutFactory<T> dynLayoutFactory,
    		final DynVizMapFactory<T> vizMapFactory,
    		final FileUtil fileUtil,
    		final StreamUtil streamUtil,
    		final TunableSetter tunableSetterServiceRef)
    {
        super("XGMML File...");
        this.setPreferredMenu("File.Import.Dynamic Network");
        this.desktopApp = desktopApp;
        this.appManager = appManager;
        this.cytoPanelWest = desktopApp.getCytoPanel(CytoPanelName.WEST);
        this.myDynPanel = myDynPanel;
        this.taskManager = taskManager;
        this.dynNetworkManager = dynNetworkManager;
        this.dynNetworkViewManager = dynNetworkViewManager;
        this.dynNetworkFactory = dynNetworkFactory;
        this.dynNetworkViewFactory = dynNetworkViewFactory;
        this.dynLayoutFactory = dynLayoutFactory;
        this.vizMapFactory = vizMapFactory;
        this.fileUtil = fileUtil;
        this.streamUtil = streamUtil;
        this.tunableSetterServiceRef = tunableSetterServiceRef;
    }

    /**
     * Fire action.
     */
    public void actionPerformed(ActionEvent e)
    {
    	XGMMLDynParser<T> xgmmlParser = new XGMMLDynParser<T>(dynNetworkFactory,dynNetworkViewFactory,dynLayoutFactory,vizMapFactory);
    	XGMMLDynFileFilter xgmmlFilter = new XGMMLDynFileFilter(new String[]{"xgmml","xml","XGMML","XML"}, new String[]{"text/xgmml","text/xgmml+xml","text/xgmml","text/xgmml+xml"}, "XGMML files",DataCategory.NETWORK, streamUtil);
    	XGMMLDynNetworkReaderFactory xgmmlNetworkReaderFactory = new XGMMLDynNetworkReaderFactory(xgmmlFilter,xgmmlParser);
    	File file = fileUtil.getFile(desktopApp.getJFrame(), "Load Dynamic Network", FileUtil.LOAD, getFilters());
    	LoadDynNetworkFileTaskFactory loadFactory = new LoadDynNetworkFileTaskFactoryImpl(xgmmlNetworkReaderFactory, tunableSetterServiceRef, streamUtil);
    	LoadDynFactory<T> loadViewFactory = new LoadDynNetworkViewFactoryImpl<T>(appManager,dynNetworkManager,dynNetworkViewFactory);
    	LoadDynFactory<T> loadLayoutFactory = new LoadDynLayoutFactoryImpl<T>(appManager,dynNetworkViewManager,dynLayoutFactory);
    	LoadDynFactory<T> loadvizMapFactory = new LoadDynVizMapFactoryImpl<T>(appManager,dynNetworkViewManager,vizMapFactory);
    	
    	Task loadTask = loadFactory.creatTaskIterator(file).next();
    	Task loadViewTask = loadViewFactory.creatTaskIterator().next();
    	Task loadLayoutTask = loadLayoutFactory.creatTaskIterator().next();
    	Task loadvizMapTask = loadvizMapFactory.creatTaskIterator().next();
    	Task loadPanelTask = new DynCytoPanelTask<T,C>(myDynPanel, cytoPanelWest);
//    	TaskIterator iterator = new TaskIterator(loadTask,loadViewTask,loadPanelTask,loadLayoutTask,loadvizMapTask);
    	TaskIterator iterator = new TaskIterator(loadTask,loadViewTask,loadLayoutTask,loadvizMapTask,loadPanelTask);
    	
    	
    	
    	taskManager.execute(iterator);
    }
    
	private List<FileChooserFilter> getFilters()
	{
		List<FileChooserFilter> filters = new ArrayList<FileChooserFilter>();
    	filters.add(new FileChooserFilter("XGMML", "xgmml"));
    	return filters;
	}
}
