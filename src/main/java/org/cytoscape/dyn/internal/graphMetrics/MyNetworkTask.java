package org.cytoscape.dyn.internal.graphMetrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManagerImpl;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshotImpl;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalDouble;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class MyNetworkTask<T> extends AbstractTask{
	
	private DynNetworkViewManagerImpl<T> dynNetViewManager;
	
	public MyNetworkTask(DynNetworkViewManagerImpl dynNetViewManager){
		this.dynNetViewManager=dynNetViewManager;
	}
	
	public void run(TaskMonitor monitor){
		
		
		Collection<DynNetworkView<T>> dyncollection=new ArrayList<DynNetworkView<T>>();
		dyncollection=dynNetViewManager.getDynNetworkViews();
		Iterator<DynNetworkView<T>> it=dyncollection.iterator();
		
		DynNetworkView<T> view=it.next();
		DynNetworkSnapshotImpl<T> networkSnapshot=new DynNetworkSnapshotImpl<T>(view);
		DynIntervalDouble snapshotInterval=new DynIntervalDouble(3.0,4.0);
		networkSnapshot.setInterval((DynInterval<T>) snapshotInterval, 0.0,0.0,0.0);
		
				
		DynNetwork<T> dynamicnetwork=view.getNetwork();
		
		//Declaring and Initialising eventTimeList of the Dynamic Network
		
		List<Double> eventTimeList=new ArrayList<Double>();
		eventTimeList=dynamicnetwork.getEventTimeList();
		
		Iterator<Double> iterator=eventTimeList.iterator();
		
		//Declaring and Initialising two temporary variables for start time and end time
		
		Double startTime, endTime;
		startTime=iterator.next();
		
		
		//Declaring a nodeList 
		
		List<CyNode> nodeList=new ArrayList<CyNode>();
		CyNode node;
				
		//Computing the indegree for each node for each time interval
		
		while(iterator.hasNext()){
			
			snapshotInterval.setStart(startTime);
			endTime=iterator.next();
			snapshotInterval.setEnd(endTime);
			
			networkSnapshot.setInterval((DynInterval<T>) snapshotInterval, 0.0,0.0,0.0);
			nodeList=networkSnapshot.getNodes();
			Iterator<CyNode> nodeIterator=nodeList.iterator();
			System.out.println("For Time Interval "+snapshotInterval.getStart()+"-"+snapshotInterval.getEnd()+":");
			while(nodeIterator.hasNext()){
				node=nodeIterator.next();
				System.out.println("In Degree of Node "+node.getSUID()+" is "+networkSnapshot.inDegree(node));
			}
			
			startTime=endTime;
			
		}
		
		view.updateView();
	}

}
