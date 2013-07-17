package org.cytoscape.dyn.internal.graphMetrics;

import org.cytoscape.dyn.internal.CyActivator;
import org.cytoscape.dyn.internal.model.DynNetworkManagerImpl;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManagerImpl;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskIterator;

public class GraphMetricsTasks<T, C> extends AbstractNetworkViewTaskFactory {

	private DynNetworkViewManagerImpl<T> dynNetViewManager;
	private CyNetworkFactory networkFactory;
	private CyRootNetworkManager rootNetworkManager;
	private CyNetworkNaming nameUtil;
	private DynNetworkManagerImpl<T> dynNetworkManager;
	private CyActivator<T, C> cyActivator;

	/**
	 * @param dynNetViewManager
	 * @param cyNetworkViewManagerServiceRef
	 * @param cyNetworkFactoryServiceRef
	 * @param cyRootNetworkManagerServiceRef
	 * @param cyNetworkNamingServiceRef
	 * @param dynNetManager
	 */
	public GraphMetricsTasks(DynNetworkViewManagerImpl<T> dynNetViewManager,
			CyNetworkViewManager cyNetworkViewManagerServiceRef,
			CyNetworkFactory cyNetworkFactoryServiceRef,
			CyRootNetworkManager cyRootNetworkManagerServiceRef,
			CyNetworkNaming cyNetworkNamingServiceRef,
			DynNetworkManagerImpl<T> dynNetManager,
			CyActivator<T, C> cyActivator) {
		this.dynNetViewManager = dynNetViewManager;
		this.networkFactory = cyNetworkFactoryServiceRef;
		this.rootNetworkManager = cyRootNetworkManagerServiceRef;
		this.nameUtil = cyNetworkNamingServiceRef;
		this.dynNetworkManager = dynNetManager;
		this.cyActivator = cyActivator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.task.NetworkViewTaskFactory#createTaskIterator(org.cytoscape
	 * .view.model.CyNetworkView)
	 */

	@Override
	public TaskIterator createTaskIterator(CyNetworkView arg0) {
		// TODO Auto-generated method stub
		return new TaskIterator(
				/*
				 * new DynamicDirectedBetweennessStress<T>(dynNetViewManager,
				 * arg0, networkFactory, rootNetworkManager, nameUtil,
				 * dynNetworkManager),new
				 * DynamicDirectedEccCloseRadCentro<T>(dynNetViewManager, arg0,
				 * networkFactory, rootNetworkManager, nameUtil,
				 * dynNetworkManager)
				 */new EigenVector<T>(dynNetViewManager, arg0, networkFactory,
						rootNetworkManager, nameUtil, dynNetworkManager),
				new DynamicDistEccCloseRad<T>(dynNetViewManager, arg0,
						networkFactory, rootNetworkManager, nameUtil,
						dynNetworkManager), new DynamicBetweennessStress<T>(
						dynNetViewManager, arg0, networkFactory,
						rootNetworkManager, nameUtil, dynNetworkManager),
				new DynamicInOutDegree<T>(dynNetViewManager, arg0,
						networkFactory, rootNetworkManager, nameUtil,
						dynNetworkManager), new GraphMetricsPanelTask<T, C>(
						cyActivator, dynNetViewManager, arg0));
	}

}