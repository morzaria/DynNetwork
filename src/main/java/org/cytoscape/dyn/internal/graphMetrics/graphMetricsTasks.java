package org.cytoscape.dyn.internal.graphMetrics;

import org.cytoscape.dyn.internal.view.model.DynNetworkViewManagerImpl;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class graphMetricsTasks<T> extends AbstractTaskFactory {
	
	private DynNetworkViewManagerImpl<T> dynNetViewManager;
	public graphMetricsTasks(DynNetworkViewManagerImpl<T> dynNetViewManager){
		this.dynNetViewManager=dynNetViewManager;
	}
	public TaskIterator createTaskIterator(){
		return new TaskIterator(new dynamicBetweennessStress<T>(dynNetViewManager), new dynamicInOutDegree<T>(dynNetViewManager), new dynamicDistEccCloseRad<T>(dynNetViewManager));
	}

}