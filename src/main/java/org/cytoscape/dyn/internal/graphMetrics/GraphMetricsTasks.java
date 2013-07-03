package org.cytoscape.dyn.internal.graphMetrics;

import org.cytoscape.dyn.internal.model.DynNetworkManagerImpl;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManagerImpl;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskIterator;

public class GraphMetricsTasks<T> extends AbstractNetworkViewTaskFactory {
	
	private DynNetworkViewManagerImpl<T> dynNetViewManager;
	private CyNetworkFactory networkFactory;
	private CyRootNetworkManager rootNetworkManager;
	private CyNetworkNaming nameUtil;
	private DynNetworkManagerImpl dynNetworkManager;
	
	/**
	 * @param dynNetViewManager
	 * @param cyNetworkViewManagerServiceRef
	 * @param cyNetworkFactoryServiceRef
	 * @param cyRootNetworkManagerServiceRef
	 * @param cyNetworkNamingServiceRef
	 * @param dynNetManager
	 */
	public GraphMetricsTasks(DynNetworkViewManagerImpl<T> dynNetViewManager,CyNetworkViewManager cyNetworkViewManagerServiceRef, CyNetworkFactory cyNetworkFactoryServiceRef, CyRootNetworkManager cyRootNetworkManagerServiceRef, CyNetworkNaming cyNetworkNamingServiceRef, DynNetworkManagerImpl<T> dynNetManager){
		this.dynNetViewManager = dynNetViewManager;
		this.networkFactory = cyNetworkFactoryServiceRef;
		this.rootNetworkManager = cyRootNetworkManagerServiceRef;
		this.nameUtil = cyNetworkNamingServiceRef;
		this. dynNetworkManager = dynNetManager;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.task.NetworkViewTaskFactory#createTaskIterator(org.cytoscape.view.model.CyNetworkView)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TaskIterator createTaskIterator(CyNetworkView arg0) {
		// TODO Auto-generated method stub
		return new TaskIterator(/*new DynamicDirectedBetweennessStress<T>(dynNetViewManager, arg0, networkFactory, rootNetworkManager, nameUtil, dynNetworkManager),new DynamicDirectedEccCloseRadCentro<T>(dynNetViewManager, arg0, networkFactory, rootNetworkManager, nameUtil, dynNetworkManager)*/new EigenVector<T>(dynNetViewManager, arg0, networkFactory, rootNetworkManager, nameUtil, dynNetworkManager),new DynamicDistEccCloseRad<T>(dynNetViewManager, arg0, networkFactory, rootNetworkManager, nameUtil, dynNetworkManager), new DynamicBetweennessStress<T>(dynNetViewManager, arg0, networkFactory, rootNetworkManager, nameUtil, dynNetworkManager), new DynamicInOutDegree<T>(dynNetViewManager, arg0, networkFactory, rootNetworkManager, nameUtil, dynNetworkManager));
	}

}