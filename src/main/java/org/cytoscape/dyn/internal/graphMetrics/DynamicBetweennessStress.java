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
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshotImpl;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalDouble;
import org.cytoscape.model.CyNode;
public class DynamicBetweennessStress<T> extends AbstractTask{

	private DynNetworkViewManagerImpl<T> dynNetViewManager;
	private CyNetworkView cyNetworkView;
	private HashMap<DynInterval<T>,HashMap<CyNode,Double>> nodeTimeStressMap;
	private HashMap<DynInterval<T>,HashMap<CyNode,Double>> nodeTimeBetweennessMap;

	public DynamicBetweennessStress(DynNetworkViewManagerImpl<T> dynNetViewManager, CyNetworkView cyNetworkView){
		this.dynNetViewManager=dynNetViewManager;
		this.cyNetworkView=cyNetworkView;
	}
	public void run(TaskMonitor monitor){

		monitor.setTitle("Calculating Betweenness and Stress");

		//To get DynNetworkView which need to be passed to DynNetworkSnapshotImpl
		//Collection<DynNetworkView<T>> dyncollection=new ArrayList<DynNetworkView<T>>();
		//dyncollection=dynNetViewManager.getDynNetworkViews();
		//Iterator<DynNetworkView<T>> it=dyncollection.iterator();
		DynNetworkView<T> view=dynNetViewManager.getDynNetworkView(cyNetworkView);
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
			for(CyNode node1 : nodeList){
				nodeBetweennessMap.put(node1, 0.0);
				nodeStressMap.put(node1, 0.0);
			}
			for(CyNode node1 : nodeList){

				for(CyNode node2 : nodeList){
					nodeDependencyMap.put(node2, 0.0);
					nodeDependencyMap1.put(node2, 0.0);
					nodeDistanceMap.put(node2,-1.0);
					nodeSigmaMap.put(node2,0.0);
					nodePreviousMap.put(node2, new ArrayList<CyNode>());
				}
				Stack<CyNode> nodeStack = new Stack<CyNode>();
				Queue<CyNode> nodeQueue = new LinkedList<CyNode>();
				nodeQueue.add(node1);
				nodeDistanceMap.put(node1, 0.0);
				nodeSigmaMap.put(node1, 1.0);
				while(!nodeQueue.isEmpty()){
					CyNode node2 = nodeQueue.remove();
					nodeStack.push(node2);

					for(CyNode node3 : networkSnapshot.getNeighbors(node2)){

						if(nodeDistanceMap.get(node3)<0){
							nodeQueue.add(node3);
							nodeDistanceMap.put(node3, nodeDistanceMap.get(node2)+1.0);
						}

						if(nodeDistanceMap.get(node3)==(nodeDistanceMap.get(node2)+1.0)){
							nodeSigmaMap.put(node3, nodeSigmaMap.get(node3)+nodeSigmaMap.get(node2));
							nodePreviousMap.get(node3).add(node2);
						}

					}

				}
				while(!nodeStack.isEmpty()){
					CyNode node3 = nodeStack.pop();
					for(CyNode node4 : nodePreviousMap.get(node3)){
						nodeDependencyMap.put(node4, nodeDependencyMap.get(node4)+(double)(nodeSigmaMap.get(node4)/nodeSigmaMap.get(node3))*(1+nodeDependencyMap.get(node3)));
						nodeDependencyMap1.put(node4, nodeDependencyMap1.get(node4)+(double)(nodeSigmaMap.get(node4))*(1+nodeDependencyMap1.get(node3)));
					}
					if(node3!=node1){
						nodeBetweennessMap.put(node3, nodeBetweennessMap.get(node3)+nodeDependencyMap.get(node3));
						nodeStressMap.put(node3, nodeStressMap.get(node3)+nodeDependencyMap1.get(node3));
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