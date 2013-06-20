/**
 * This task computes Betweenness and Stress using Brandes' algorithm which takes O(VE) time for
 * unweighed graphs.
 * 
 */
package org.cytoscape.dyn.internal.graphMetrics;

/**
 * @author Jimmy
 *
 */

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
public class dynamicBetweennessStress<T> extends AbstractTask{
	
	private DynNetworkViewManagerImpl<T> dynNetViewManager;
	private HashMap<DynInterval<T>,HashMap<CyNode,Double>> nodeTimeStressMap;
	private HashMap<DynInterval<T>,HashMap<CyNode,Double>> nodeTimeBetweennessMap;
	
	public dynamicBetweennessStress(DynNetworkViewManagerImpl<T> dynNetViewManager){
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
		List<CyNode> nodeList=new ArrayList<CyNode>();
		
		
		nodeTimeStressMap=new HashMap<DynInterval<T>,HashMap<CyNode,Double>>();
		nodeTimeBetweennessMap=new HashMap<DynInterval<T>,HashMap<CyNode,Double>>();
		HashMap<CyNode,Double> nodeBetweennessMap=new HashMap<CyNode,Double>();
		HashMap<CyNode,Double> nodeStressMap=new HashMap<CyNode,Double>();
		HashMap<CyNode,Double> nodeDependencyMap=new HashMap<CyNode,Double>();
		HashMap<CyNode,Double> nodeDependencyMap1=new HashMap<CyNode,Double>();
		HashMap<CyNode,Double> nodeDistanceMap=new HashMap<CyNode,Double>();
		HashMap<CyNode,Double> nodeSigmaMap=new HashMap<CyNode,Double>();
		HashMap<CyNode,List<CyNode>> nodePreviousMap=new HashMap<CyNode,List<CyNode>>();
		
		/*Implementation of Brandes' Algorithm
		 * 
		 * */
		while(iterator.hasNext()){
			snapshotInterval.setStart(startTime);
			endTime=iterator.next();
			snapshotInterval.setEnd(endTime);
			
			networkSnapshot.setInterval((DynInterval<T>) snapshotInterval, 0.0,0.0,0.0);
			nodeList=networkSnapshot.getNodes();
			//Iterator<CyNode> nodeIterator=nodeList.iterator();
			for(CyNode Node1 : nodeList){
				nodeBetweennessMap.put(Node1, 0.0);
				nodeStressMap.put(Node1, 0.0);
			}
			for(CyNode Node1 : nodeList){
				
				for(CyNode Node2 : nodeList){
					nodeDependencyMap.put(Node2, 0.0);
					nodeDependencyMap1.put(Node2, 0.0);
					nodeDistanceMap.put(Node2,-1.0);
					nodeSigmaMap.put(Node2,0.0);
					nodePreviousMap.put(Node2, new ArrayList<CyNode>());
				}
				Stack<CyNode> nodeStack = new Stack<CyNode>();
				Queue<CyNode> nodeQueue = new LinkedList<CyNode>();
				nodeQueue.add(Node1);
				nodeDistanceMap.put(Node1, 0.0);
				nodeSigmaMap.put(Node1, 1.0);
				while(!nodeQueue.isEmpty()){
					CyNode Node2 = nodeQueue.remove();
					nodeStack.push(Node2);
					
					for(CyNode Node3 : networkSnapshot.getNeighbors(Node2)){
						
						if(nodeDistanceMap.get(Node3)<0){
							nodeQueue.add(Node3);
							nodeDistanceMap.put(Node3, nodeDistanceMap.get(Node2)+1.0);
						}
						
						if(nodeDistanceMap.get(Node3)==(nodeDistanceMap.get(Node2)+1.0)){
							nodeSigmaMap.put(Node3, nodeSigmaMap.get(Node3)+nodeSigmaMap.get(Node2));
							nodePreviousMap.get(Node3).add(Node2);
						}
						
					}
					
				}
				while(!nodeStack.isEmpty()){
					CyNode Node3 = nodeStack.pop();
					for(CyNode Node4 : nodePreviousMap.get(Node3)){
						nodeDependencyMap.put(Node4, nodeDependencyMap.get(Node4)+(double)(nodeSigmaMap.get(Node4)/nodeSigmaMap.get(Node3))*(1+nodeDependencyMap.get(Node3)));
						nodeDependencyMap1.put(Node4, nodeDependencyMap1.get(Node4)+(double)(nodeSigmaMap.get(Node4))*(1+nodeDependencyMap1.get(Node3)));
					}
					if(Node3!=Node1){
						nodeBetweennessMap.put(Node3, nodeBetweennessMap.get(Node3)+nodeDependencyMap.get(Node3));
						nodeStressMap.put(Node3, nodeStressMap.get(Node3)+nodeDependencyMap1.get(Node3));
					}
				}
				
			}
			nodeTimeStressMap.put((DynInterval<T>)snapshotInterval, nodeStressMap);
			nodeTimeBetweennessMap.put((DynInterval<T>)snapshotInterval, nodeBetweennessMap);
			System.out.println(nodeStressMap+"\n");
			System.out.println(nodeBetweennessMap+"\n");
			startTime=endTime;
			
			nodeDistanceMap.clear();
			nodeBetweennessMap.clear();
		}
		
	}
}
