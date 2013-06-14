package org.cytoscape.dyn.internal.graphMetrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

public class dynamicEccentricity<T> extends AbstractTask {
	
	private DynNetworkViewManagerImpl<T> dynNetViewManager;
	HashMap<DynInterval<T>,HashMap<CyNode,Integer>> nodeTimeEccentricityMap;
	
	public dynamicEccentricity(DynNetworkViewManagerImpl<T> dynNetViewManager){
		this.dynNetViewManager=dynNetViewManager;
	}
	public void run(TaskMonitor monitor){
		
		//To get DynNetworkView which need to be passed to DynNetworkSnapshotImpl
		Collection<DynNetworkView<T>> dyncollection=new ArrayList<DynNetworkView<T>>();
		dyncollection=dynNetViewManager.getDynNetworkViews();
		Iterator<DynNetworkView<T>> it=dyncollection.iterator();
		
		DynNetworkView<T> view=it.next();
		DynNetworkSnapshotImpl<T> networkSnapshot=new DynNetworkSnapshotImpl<T>(view);
		
		//Need the dynamic network to get event time list
		DynNetwork<T> dynamicnetwork=view.getNetwork();
		
		//Declaring and Initialising eventTimeList of the Dynamic Network
		
		List<Double> eventTimeList=new ArrayList<Double>();
		eventTimeList=dynamicnetwork.getEventTimeList();
		
		Iterator<Double> iterator=eventTimeList.iterator();
		
		//Declaring and Initialising two temporary variables for start time and end time
		
		Double startTime, endTime;
		startTime=iterator.next();
		DynIntervalDouble snapshotInterval=new DynIntervalDouble(startTime,startTime);
		networkSnapshot.setInterval((DynInterval<T>) snapshotInterval, 0.0,0.0,0.0);
		
		//Declaring a nodeList 
		
		List<CyNode> nodeList=new ArrayList<CyNode>();
		CyNode node;
		
		nodeTimeEccentricityMap=new HashMap<DynInterval<T>,HashMap<CyNode,Integer>>();
		
		//computing the eccentricity for each time interval
		
		Queue<CyNode> nodeQueue=new LinkedList<CyNode>();
		HashMap<CyNode,Integer> nodeDistanceMap=new HashMap<CyNode,Integer>();
		
		while(iterator.hasNext()){
			//loop through all the time intervals
			
			snapshotInterval.setStart(startTime);
			endTime=iterator.next();
			snapshotInterval.setEnd(endTime);
			
			networkSnapshot.setInterval((DynInterval<T>) snapshotInterval, 0.0,0.0,0.0);
			nodeList=networkSnapshot.getNodes();
			Iterator<CyNode> nodeIterator=nodeList.iterator();
			
			//computing eccentricity for each node in a particular time interval
			for(CyNode Node1 : nodeList){
				
				//setting distance to all the nodes as infinity
				for(CyNode Node : nodeList){
					nodeDistanceMap.put(Node,-1);
				}
								
				//choosing a source node
				nodeDistanceMap.put(Node1,0);
				for(CyNode nodee : networkSnapshot.getNeighbors(Node1))
					System.out.println(nodee);
								
				//adding the node to Queue
				nodeQueue.add(Node1);
				
				while(!nodeQueue.isEmpty()){
					
					node=nodeQueue.remove();
					nodeIterator=networkSnapshot.getNeighbors(node).iterator();
					while(nodeIterator.hasNext()){
						CyNode Node2=nodeIterator.next();
						//System.out.println(Node2+"   "+nodeDistanceMap.get(Node2));
						if(nodeDistanceMap.get(Node2)==-1){
							nodeDistanceMap.put(Node2,nodeDistanceMap.get(node)+1);
							nodeQueue.add(Node2);
						}
					}
				}
				nodeIterator=nodeList.iterator();
				int max=0;
				while(nodeIterator.hasNext()){
					node=nodeIterator.next();
					if(nodeDistanceMap.get(node)>max){
						max=nodeDistanceMap.get(node);
						}	
					
				}
				//System.out.println(snapshotInterval.getStart()+"-"+snapshotInterval.getEnd());
				//System.out.println(Node1.toString()+"  "+max);
				//((HashMap<CyNode, Integer>) nodeTimeEccentricityMap.get(snapshotInterval)).put(Node1,max);
			}
			
			
			startTime=endTime;
			break;
			//nodeDistanceMap.clear();
		}	
		
	}

}
