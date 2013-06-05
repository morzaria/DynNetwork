package org.cytoscape.dyn.internal.graphMetrics;


import org.cytoscape.dyn.internal.view.model.DynNetworkViewManagerImpl;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class compute<T> extends AbstractTaskFactory {
	
	private DynNetworkViewManagerImpl<T> dynNetViewManager;
	public compute(DynNetworkViewManagerImpl dynNetViewManager){
		this.dynNetViewManager=dynNetViewManager;
	}
	public TaskIterator createTaskIterator(){
		return new TaskIterator(new MyNetworkTask(dynNetViewManager));
	}

}
