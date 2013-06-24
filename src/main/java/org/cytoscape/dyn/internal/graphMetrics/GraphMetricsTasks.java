package org.cytoscape.dyn.internal.graphMetrics;

import org.cytoscape.dyn.internal.view.model.DynNetworkViewManagerImpl;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskIterator;

public class GraphMetricsTasks<T> extends AbstractNetworkViewTaskFactory {
	
	private DynNetworkViewManagerImpl<T> dynNetViewManager;
	public GraphMetricsTasks(DynNetworkViewManagerImpl<T> dynNetViewManager,CyNetworkViewManager cyNetworkViewManagerServiceRef){
		this.dynNetViewManager=dynNetViewManager;
	}
	//public TaskIterator createTaskIterator(){
	//	return new TaskIterator(new DynamicBetweennessStress<T>(dynNetViewManager), new DynamicInOutDegree<T>(dynNetViewManager), new DynamicDistEccCloseRad<T>(dynNetViewManager));
	//}
	@Override
	public TaskIterator createTaskIterator(CyNetworkView arg0) {
		// TODO Auto-generated method stub
		return new TaskIterator(new DynamicBetweennessStress<T>(dynNetViewManager, arg0), new DynamicInOutDegree<T>(dynNetViewManager, arg0), new DynamicDistEccCloseRad<T>(dynNetViewManager, arg0));
	}

}