/**
 * This task computes graph distance, eccentricity, closeness and radiality for each time interval using Dijkstra's algorithm.
 * */
package org.cytoscape.dyn.internal.graphMetrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

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
import org.cytoscape.dyn.internal.graphMetrics.DijkstraDistance;

public class dynamicDistEccCloseRad<T> extends AbstractTask {
	
	private DynNetworkViewManagerImpl<T> dynNetViewManager;
	private HashMap<DynInterval<T>,HashMap<CyNode,Double>> nodeTimeEccentricityMap;
	private HashMap<DynInterval<T>,HashMap<CyNode,Double>> nodeTimeClosenessMap;
	private HashMap<DynInterval<T>,HashMap<CyNode,Double>> nodeTimeRadialityMap;
	
	private HashMap<DynInterval<T>,Double> distanceTimeMap;
	
	public dynamicDistEccCloseRad(DynNetworkViewManagerImpl<T> dynNetViewManager){
		this.dynNetViewManager=dynNetViewManager;
	}
	
	
	@SuppressWarnings("unchecked")
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
		
		//HashMap for (TimeInterval, Node, Eccentricity)  and (TimeInterval, Distance)
		nodeTimeEccentricityMap=new HashMap<DynInterval<T>,HashMap<CyNode,Double>>();
		distanceTimeMap=new HashMap<DynInterval<T>,Double>();
		
		//HashMap for (TimeInterval, Node, Closeness)
		nodeTimeClosenessMap=new HashMap<DynInterval<T>,HashMap<CyNode,Double>>();
		
		//HashMap for (TimeInterval, Node, Radiality)
		nodeTimeRadialityMap=new HashMap<DynInterval<T>,HashMap<CyNode,Double>>();
		
		HashMap<CyNode,Double> nodeDistanceMap=new HashMap<CyNode,Double>();
		HashMap<CyNode,Double> nodeEccentricityMap=new HashMap<CyNode,Double>();
		HashMap<CyNode,Double> nodeClosenessMap=new HashMap<CyNode,Double>();
		HashMap<CyNode,Double> nodeRadialityMap=new HashMap<CyNode,Double>();
		
		Double dynamicGraphDistance=0.0; 
		
		DijkstraDistance<T> dijkstraDistance;
		
		while(iterator.hasNext()){
			//loop through all the time intervals
			
			snapshotInterval.setStart(startTime);
			endTime=iterator.next();
			snapshotInterval.setEnd(endTime);
			
			networkSnapshot.setInterval((DynInterval<T>) snapshotInterval, 0.0,0.0,0.0);
			dijkstraDistance = new DijkstraDistance<T>(networkSnapshot);
			nodeList=networkSnapshot.getNodes();
			Iterator<CyNode> nodeIterator=nodeList.iterator();
			
			//computing eccentricity, distance, closeness, radiality, betweenness for each node in a particular time interval
			for(CyNode Node1 : nodeList){
					
				nodeDistanceMap=dijkstraDistance.getDijkstraDistanceMap(Node1);
				
				//finding the distance of the node farthest to the source node
				nodeIterator=nodeList.iterator();
				Double max=0.0, closeness=0.0;
				while(nodeIterator.hasNext()){
					
					node=nodeIterator.next();
					closeness+=nodeDistanceMap.get(node);
					
					if(nodeDistanceMap.get(node)>max){
						max=nodeDistanceMap.get(node);
						}	
				}
				
				nodeClosenessMap.put(Node1, 1/closeness);
				nodeEccentricityMap.put(Node1, 1/max);
				
				//System.out.println(Node1.toString()+"  "+max);
				
				if(max>dynamicGraphDistance){
					dynamicGraphDistance=max;
				}
				
			}
			//System.out.println(nodeClosenessMap);
			
			//Saving the graphDistance for different time intervals in a HashMap
			distanceTimeMap.put((DynInterval<T>) snapshotInterval,dynamicGraphDistance);
				
			//Saving the eccentricity of each node in each time interval in a HashMap
			nodeTimeEccentricityMap.put((DynInterval<T>) snapshotInterval,nodeEccentricityMap);
			
			//Saving the closeness of each node in each time interval in a HashMap
			nodeTimeClosenessMap.put((DynInterval<T>) snapshotInterval, nodeClosenessMap);
			
			
			for(CyNode Node1 : networkSnapshot.getNodes()){
				
				nodeRadialityMap.put(Node1,((networkSnapshot.getNodeCount())*(dynamicGraphDistance+1)-(1/nodeClosenessMap.get(Node1)))/(networkSnapshot.getNodeCount()-1));
			}
			
			//Saving the radiality of each node in each time interval in a HashMap
			nodeTimeRadialityMap.put((DynInterval<T>) snapshotInterval, nodeRadialityMap);
			
			System.out.println(nodeTimeEccentricityMap+"\n");
			System.out.println(nodeTimeClosenessMap+"\n");
			System.out.println(nodeTimeRadialityMap+"\n");
			
			
			//Setting Graph distance to 0 for the next iteration of time
			dynamicGraphDistance=0.0;
			
			startTime=endTime;
			nodeClosenessMap.clear();
			nodeDistanceMap.clear();
		}	
		
	}

}
