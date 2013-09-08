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

package org.cytoscape.dyn.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkViewListener;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.dyn.internal.action.MenuActionLoadCSV;
import org.cytoscape.dyn.internal.action.MenuActionLoadXGMML;
import org.cytoscape.dyn.internal.action.MenuActionSelectVisibleEdges;
import org.cytoscape.dyn.internal.action.MenuActionSelectVisibleNodes;
import org.cytoscape.dyn.internal.layout.model.DynLayoutFactory;
import org.cytoscape.dyn.internal.layout.model.DynLayoutFactoryImpl;
import org.cytoscape.dyn.internal.layout.model.DynLayoutManager;
import org.cytoscape.dyn.internal.layout.model.DynLayoutManagerImpl;
import org.cytoscape.dyn.internal.layout.task.CleanDynLayout;
import org.cytoscape.dyn.internal.layout.task.ForceDirectedDynLayout;
import org.cytoscape.dyn.internal.layout.task.KKDynLayout;
import org.cytoscape.dyn.internal.model.DynNetworkFactory;
import org.cytoscape.dyn.internal.model.DynNetworkFactoryImpl;
import org.cytoscape.dyn.internal.model.DynNetworkManager;
import org.cytoscape.dyn.internal.model.DynNetworkManagerImpl;
import org.cytoscape.dyn.internal.view.gui.DynCytoPanelImpl;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewFactoryImpl;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManager;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManagerImpl;
import org.cytoscape.dyn.internal.view.task.Transformator;
import org.cytoscape.dyn.internal.vizmapper.model.DynVizMapFactory;
import org.cytoscape.dyn.internal.vizmapper.model.DynVizMapFactoryImpl;
import org.cytoscape.dyn.internal.vizmapper.model.DynVizMapManager;
import org.cytoscape.dyn.internal.vizmapper.model.DynVizMapManagerImpl;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.events.UpdateNetworkPresentationListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableSetter;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;
import org.cytoscape.dyn.internal.graphMetrics.GraphMetricsPanel;
import org.cytoscape.dyn.internal.graphMetrics.GraphMetricsResultsPanel;
import org.cytoscape.dyn.internal.graphMetrics.GraphMetricsTasks;

/**
 * <code> CyActivator </code> for DynNetwork plugin.
 * 
 * @author Sabina Sara Pfister
 * 
 * @param <T>
 * @param <C>
 */
public class CyActivator<T, C> extends AbstractCyActivator {
	/**
	 * <code> CyActivator </code> Constructor
	 */
	private CyServiceRegistrar cyServiceRegistrarRef;
	private CySwingApplication cytoscapeDesktopService;
	public CyActivator() {
		super();
	}

	/**
	 * Start bundle.
	 */
	@SuppressWarnings("unchecked")
	public void start(BundleContext context) {

		cytoscapeDesktopService = getService(context,
				CySwingApplication.class);
		CyApplicationManager cyApplicationManagerServiceRef = getService(
				context, CyApplicationManager.class);
		CyNetworkManager cyNetworkManagerServiceRef = getService(context,
				CyNetworkManager.class);
		CyNetworkViewManager cyNetworkViewManagerServiceRef = getService(
				context, CyNetworkViewManager.class);
		CyNetworkViewFactory cyNetworkViewFactoryServiceRef = getService(
				context, CyNetworkViewFactory.class);
		CyNetworkFactory cyNetworkFactoryServiceRef = getService(context,
				CyNetworkFactory.class);
		CyRootNetworkManager cyRootNetworkManagerServiceRef = getService(
				context, CyRootNetworkManager.class);
		CyNetworkNaming cyNetworkNamingServiceRef = getService(context,
				CyNetworkNaming.class);
		TaskManager<T, C> taskManager = getService(context, TaskManager.class);
		VisualMappingManager visualMappingServiceRef = getService(context,
				VisualMappingManager.class);
		FileUtil fileUtil = getService(context, FileUtil.class);
		StreamUtil streamUtil = getService(context, StreamUtil.class);
		TunableSetter tunableSetterServiceRef = getService(context,
				TunableSetter.class);
		UndoSupport undo = getService(context, UndoSupport.class);
		CyEventHelper cyEventHelperRef = getService(context,
				CyEventHelper.class);

		DynNetworkManagerImpl<T> dynNetManager = new DynNetworkManagerImpl<T>(
				cyNetworkManagerServiceRef);
		DynNetworkFactoryImpl<T> dynNetworkFactory = new DynNetworkFactoryImpl<T>(
				cyNetworkFactoryServiceRef, cyRootNetworkManagerServiceRef,
				dynNetManager, cyNetworkNamingServiceRef);
		DynNetworkViewManagerImpl<T> dynNetViewManager = new DynNetworkViewManagerImpl<T>(
				cyNetworkViewManagerServiceRef);
		DynNetworkViewFactoryImpl<T> dynNetworkViewFactory = new DynNetworkViewFactoryImpl<T>(
				dynNetViewManager, cyNetworkViewFactoryServiceRef,
				cyNetworkViewManagerServiceRef, visualMappingServiceRef);

		DynLayoutManager<T> dynLayoutManager = new DynLayoutManagerImpl<T>();
		DynLayoutFactory<T> dynLayoutFactory = new DynLayoutFactoryImpl<T>(
				dynLayoutManager);
		DynVizMapManager<T> dynVizMapManager = new DynVizMapManagerImpl<T>();
		DynVizMapFactory<T> vizMapFactory = new DynVizMapFactoryImpl<T>(
				dynVizMapManager);
		Transformator<T> transformator = new Transformator<T>(dynLayoutManager,
				dynVizMapManager);
		DynCytoPanelImpl<T, C> dynCytoPanel = new DynCytoPanelImpl<T, C>(
				cytoscapeDesktopService, taskManager,
				cyApplicationManagerServiceRef, dynNetViewManager,
				dynLayoutManager, dynVizMapManager, transformator, fileUtil);

		CyLayoutAlgorithm dynKKLayout = new KKDynLayout<T, C>(
				"Dynamic Layouts", "Kamada-Kawai DynLayout", undo,
				dynCytoPanel, dynLayoutFactory, dynNetViewManager,
				dynLayoutManager);
		CyLayoutAlgorithm dynPerfuseLayout = new ForceDirectedDynLayout<T, C>(
				"Dynamic Layouts", "Prefuse DynLayout", undo, dynCytoPanel,
				dynLayoutFactory, dynNetViewManager, dynLayoutManager);
		CyLayoutAlgorithm dynClearLayout = new CleanDynLayout<T, C>(
				"Dynamic Layouts", "Remove DynLayout", undo, dynLayoutFactory);
		// CyLayoutAlgorithm dynClearVizMap = new
		// CleanDynVizMap<T,C>("Dynamic VizMaps",
		// "Remove DynVizMap",undo,vizMapFactory);

		MenuActionLoadXGMML<T, C> loadAction = new MenuActionLoadXGMML<T, C>(
				cytoscapeDesktopService, cyApplicationManagerServiceRef,
				dynCytoPanel, taskManager, dynNetManager, dynNetViewManager,
				dynNetworkFactory, dynNetworkViewFactory, dynLayoutFactory,
				vizMapFactory, fileUtil, streamUtil, tunableSetterServiceRef);
		MenuActionSelectVisibleNodes<T, C> selectNodesAction = new MenuActionSelectVisibleNodes<T, C>(
				cyApplicationManagerServiceRef, cyNetworkViewManagerServiceRef,
				dynNetManager, undo, cyEventHelperRef, taskManager,
				dynCytoPanel);
		MenuActionSelectVisibleEdges<T, C> selectEdgesAction = new MenuActionSelectVisibleEdges<T, C>(
				cyApplicationManagerServiceRef, cyNetworkViewManagerServiceRef,
				dynNetManager, undo, cyEventHelperRef, taskManager,
				dynCytoPanel);
		MenuActionLoadCSV<T, C> loadActionCSV = new MenuActionLoadCSV<T, C>(
				cytoscapeDesktopService, cyApplicationManagerServiceRef,
				dynCytoPanel, taskManager, dynNetManager, dynNetViewManager,
				dynNetworkFactory, dynNetworkViewFactory, dynLayoutFactory,
				vizMapFactory, fileUtil, streamUtil, tunableSetterServiceRef);

		Properties myLayoutProps = new Properties();
		myLayoutProps.setProperty("preferredMenu", "Dynamic Layouts");

		// Properties myLayoutProps2 = new Properties();
		// myLayoutProps2.setProperty("preferredMenu","Dynamic VizMaps");

		GraphMetricsTasks<T, C> c = new GraphMetricsTasks<T, C>(
				dynNetViewManager, cyNetworkViewManagerServiceRef,
				cyNetworkFactoryServiceRef, cyRootNetworkManagerServiceRef,
				cyNetworkNamingServiceRef, dynNetManager, this);

		Properties cprops = new Properties();
		cprops.setProperty("preferredMenu", "Apps");
		cprops.setProperty("menuGravity", "11.0");
		cprops.setProperty("title", "Dynamic Graph Metrics");
		registerService(context, c, NetworkViewTaskFactory.class, cprops);

		cyServiceRegistrarRef = getService(context, CyServiceRegistrar.class);

		
		registerService(context, dynNetManager, DynNetworkManager.class,
				new Properties());
		registerService(context, dynNetworkFactory, DynNetworkFactory.class,
				new Properties());
		registerService(context, dynNetViewManager,
				DynNetworkViewManager.class, new Properties());
		registerService(context, dynNetworkViewFactory,
				DynNetworkViewFactoryImpl.class, new Properties());
		registerService(context, dynCytoPanel, CytoPanelComponent.class,
				new Properties());
		registerService(context, loadAction, CyAction.class, new Properties());
		registerService(context, loadActionCSV, CyAction.class, new Properties());
		registerService(context, selectNodesAction, CyAction.class,
				new Properties());
		registerService(context, selectEdgesAction, CyAction.class,
				new Properties());
		registerService(context, dynCytoPanel,
				SetCurrentNetworkViewListener.class, new Properties());
		registerService(context, transformator,
				UpdateNetworkPresentationListener.class, new Properties());
		registerService(context, dynKKLayout, CyLayoutAlgorithm.class,
				myLayoutProps);
		registerService(context, dynPerfuseLayout, CyLayoutAlgorithm.class,
				myLayoutProps);
		registerService(context, dynClearLayout, CyLayoutAlgorithm.class,
				myLayoutProps);
		// registerService(context,dynClearVizMap,CyLayoutAlgorithm.class,
		// myLayoutProps2);
		registerService(context, dynLayoutManager, DynLayoutManager.class,
				new Properties());
		registerService(context, dynVizMapManager, DynVizMapManager.class,
				new Properties());

	}

	public CyServiceRegistrar getCyServiceRegistrar() {
		return cyServiceRegistrarRef;
	}
	public CySwingApplication getCySwingAppication(){
		return cytoscapeDesktopService;
	}

}
