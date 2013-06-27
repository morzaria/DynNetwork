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
	
	public GraphMetricsTasks(DynNetworkViewManagerImpl<T> dynNetViewManager,CyNetworkViewManager cyNetworkViewManagerServiceRef, CyNetworkFactory cyNetworkFactoryServiceRef, CyRootNetworkManager cyRootNetworkManagerServiceRef, CyNetworkNaming cyNetworkNamingServiceRef, DynNetworkManagerImpl<T> dynNetManager){
		this.dynNetViewManager = dynNetViewManager;
		this.networkFactory = cyNetworkFactoryServiceRef;
		this.rootNetworkManager = cyRootNetworkManagerServiceRef;
		this.nameUtil = cyNetworkNamingServiceRef;
		this. dynNetworkManager = dynNetManager;
	}
	//public TaskIterator createTaskIterator(){
	//	return new TaskIterator(new DynamicBetweennessStress<T>(dynNetViewManager), new DynamicInOutDegree<T>(dynNetViewManager), new DynamicDistEccCloseRad<T>(dynNetViewManager));
	//}
	@Override
	public TaskIterator createTaskIterator(CyNetworkView arg0) {
		// TODO Auto-generated method stub
		return new TaskIterator(new DynamicBetweennessStress<T>(dynNetViewManager, arg0, networkFactory, rootNetworkManager, nameUtil, dynNetworkManager), new DynamicInOutDegree<T>(dynNetViewManager, arg0), new DynamicDistEccCloseRad<T>(dynNetViewManager, arg0));
	}

}