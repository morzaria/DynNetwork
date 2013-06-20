/**
 * Implementation of Dijkstra'a algorithm. Given a source and a network snapshot of the dynamic 
 * network this class computes the (node,distance) map as well as the (node, previous node) map.
 */
package org.cytoscape.dyn.internal.graphMetrics;

import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshotImpl;
import org.cytoscape.model.CyNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

/**
 * @author Jimmy
 *
 */
public class DijkstraDistance<T>{
	
	private CyNode node;
	private DynNetworkSnapshotImpl<T> networkSnapshot;
	
	//HashMap of (node, distance from the source node)
	private HashMap<CyNode,Double> nodeDistanceMap; 
	
	// HashMap of (node, previous nodes in the path)
	private HashMap<CyNode,List<CyNode>> nodePreviousMap;
	
	
	//private List<CyNode> nodeList;
	
	public DijkstraDistance(DynNetworkSnapshotImpl<T> networkSnapshot){
		this.networkSnapshot=networkSnapshot;
	}
	
	public HashMap<CyNode,Double> getDijkstraDistanceMap(CyNode source){
		
		Queue<CyNode> nodeQueue = new LinkedList<CyNode>();
		nodeQueue.add(source);
		
				
		//HashMap of (node, distance from the source node)
		nodeDistanceMap = new HashMap<CyNode,Double>();
		
		// HashMap of (node, previous nodes in the path) 
		nodePreviousMap = new HashMap<CyNode,List<CyNode>>();
		
		
		//List<CyNode> predecessorNodes = new ArrayList<CyNode>();
		for(CyNode node : networkSnapshot.getNodes()){
			nodeDistanceMap.put(node, Double.POSITIVE_INFINITY);
			nodePreviousMap.put(node,new ArrayList<CyNode>());
		}
		
		nodeDistanceMap.put(source, 0.0);			
		while(!nodeQueue.isEmpty()){
			
			node = nodeQueue.remove();
			
			Iterator<CyNode> nodeIterator=networkSnapshot.getNeighbors(node).iterator();
			while(nodeIterator.hasNext()){
				CyNode Node2=nodeIterator.next();
				//System.out.println(Node2+"   "+nodeDistanceMap.get(Node2));
				if((nodeDistanceMap.get(node)+1.0)<=nodeDistanceMap.get(Node2)){
					nodeDistanceMap.put(Node2,nodeDistanceMap.get(node)+1.0);
					if(!nodePreviousMap.get(Node2).contains(node)){
						nodePreviousMap.get(Node2).add(node);
					}
					nodeQueue.add(Node2);
				}
			}
		}
		return nodeDistanceMap;
	}
	
	public HashMap<CyNode,List<CyNode>> getPreviousNodeMap(){
		return this.nodePreviousMap;
	}
	
	public List<CyNode> getShortestPaths(CyNode source, CyNode target){
		List<CyNode> shortestPath = new ArrayList<CyNode>();
		shortestPath.add(target);
		while(nodePreviousMap.get(target)!=null){
			shortestPath.add(nodePreviousMap.get(target).get(0));
			target=nodePreviousMap.get(target).get(0);
		}
		
		Collections.reverse(shortestPath);
		//HashMap<Integer, List<CyNode>> shortestPaths = new HashMap<Integer, List<CyNode>>();
		//shortestPaths.put(1, shortestPath);
		return shortestPath;
	}	
}

	
