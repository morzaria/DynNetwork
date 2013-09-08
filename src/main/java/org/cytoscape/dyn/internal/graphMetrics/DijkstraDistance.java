package org.cytoscape.dyn.internal.graphMetrics;

import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshotImpl;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Given a source and a network snapshot of the dynamic network
 * <code>DijkstraDistance</code> computes the (node,distance) map as well as the
 * (node, previous node) map.
 * 
 * @param <T>
 * @author Jimmy
 */
public class DijkstraDistance<T> {

	private DynNetworkSnapshotImpl<T> networkSnapshot;

	// HashMap of (node, distance from the source node)
	private HashMap<CyNode, Double> nodeDistanceMap;

	// HashMap of (node, previous nodes in the path)
	private HashMap<CyNode, List<CyNode>> nodePreviousMap;

	/**
	 * @param networkSnapshot
	 *            DynNetworkSnapshot
	 */
	public DijkstraDistance(DynNetworkSnapshotImpl<T> networkSnapshot) {
		this.networkSnapshot = networkSnapshot;
	}

	/**
	 * Returns a HashMap<CyNode, Double> which has the shortest distances of all
	 * the nodes from a given source node.
	 * 
	 * @param source
	 *            Source Node
	 * @return nodeDistanceMap A HashMap (CyNode node, Double Distance from
	 *         Source Node)
	 */
	public HashMap<CyNode, Double> getDijkstraDistanceMap(CyNode source) {

		PriorityQueue<Long> nodeQueue = new PriorityQueue<Long>();
		nodeQueue.add(source.getSUID());

		// HashMap of (nodeSUID, node)
		HashMap<Long, CyNode> suidNodeMap = new HashMap<Long, CyNode>();

		// HashMap of (node, distance from the source node)
		nodeDistanceMap = new HashMap<CyNode, Double>();

		// HashMap of (node, previous nodes in the path)
		nodePreviousMap = new HashMap<CyNode, List<CyNode>>();

		for (CyNode node : networkSnapshot.getNodes()) {
			nodeDistanceMap.put(node, Double.POSITIVE_INFINITY);
			nodePreviousMap.put(node, new ArrayList<CyNode>());
			suidNodeMap.put(node.getSUID(), node);
		}

		nodeDistanceMap.put(source, 0.0);
		while (!nodeQueue.isEmpty()) {

			long nodeSUID = nodeQueue.remove();
			CyNode node = suidNodeMap.get(nodeSUID);
			Iterator<CyNode> nodeIterator = networkSnapshot.getNeighbors(node)
					.iterator();
			while (nodeIterator.hasNext()) {
				CyNode node2 = nodeIterator.next();
				// System.out.println(Node2+"   "+nodeDistanceMap.get(Node2));
				if ((nodeDistanceMap.get(node) + 1.0) <= nodeDistanceMap
						.get(node2)) {
					nodeDistanceMap.put(node2, nodeDistanceMap.get(node) + 1.0);
					if (!nodePreviousMap.get(node2).contains(node)) {
						nodePreviousMap.get(node2).add(node);
					}
					nodeQueue.add(node2.getSUID());
				}
			}
		}
		return nodeDistanceMap;
	}

	/**
	 * Returns a HashMap<CyNode, Double> which has the shortest distances of all
	 * the nodes from a given source node for a directed network.
	 * 
	 * @param source
	 *            source node
	 * @return nodeDistanceMap A HashMap (CyNode node, Double Directed Distance
	 *         from Source)
	 */
	public HashMap<CyNode, Double> getDirectedDijkstraDistanceMap(CyNode source) {

		PriorityQueue<Long> nodeQueue = new PriorityQueue<Long>();
		nodeQueue.add(source.getSUID());

		// HashMap of (nodeSUID, node)
		HashMap<Long, CyNode> suidNodeMap = new HashMap<Long, CyNode>();

		// HashMap of (node, distance from the source node)
		nodeDistanceMap = new HashMap<CyNode, Double>();

		// HashMap of (node, previous nodes in the path)
		nodePreviousMap = new HashMap<CyNode, List<CyNode>>();

		for (CyNode node : networkSnapshot.getNodes()) {
			nodeDistanceMap.put(node, Double.POSITIVE_INFINITY);
			nodePreviousMap.put(node, new ArrayList<CyNode>());
			suidNodeMap.put(node.getSUID(), node);
		}
		nodeDistanceMap.put(source, 0.0);
		while (!nodeQueue.isEmpty()) {

			long nodeSUID = nodeQueue.remove();
			CyNode node = suidNodeMap.get(nodeSUID);
			Iterator<CyEdge> edgeIterator = networkSnapshot.getOutEdges(node)
					.iterator();
			while (edgeIterator.hasNext()) {
				CyEdge edge = edgeIterator.next();
				// System.out.println(Node2+"   "+nodeDistanceMap.get(Node2));
				if ((nodeDistanceMap.get(node) + 1.0) <= nodeDistanceMap
						.get(edge.getTarget())) {
					nodeDistanceMap.put(edge.getTarget(),
							nodeDistanceMap.get(node) + 1.0);
					if (!nodePreviousMap.get(edge.getTarget()).contains(node)) {
						nodePreviousMap.get(edge.getTarget()).add(node);
					}
					nodeQueue.add(edge.getTarget().getSUID());
				}
			}
		}
		return nodeDistanceMap;
	}

	/**
	 * Returns a HashMap<CyNode, List<CyNode>> which has the list of predecessor
	 * nodes that lie on the shortest path to a particular node from a given
	 * source node. This method should be called after either the method
	 * getDijkstraDistanceMap or the method getDirectedDijkstraDistanceMap.
	 * 
	 * @return nodePreviousMap
	 */
	public HashMap<CyNode, List<CyNode>> getPreviousNodeMap() {
		return this.nodePreviousMap;
	}

	/**
	 * @param source
	 *            source node
	 * @param target
	 *            target node
	 * @return shortestPath Shortest Path from Source node to target.
	 */
	public List<CyNode> getShortestPaths(CyNode source, CyNode target) {
		List<CyNode> shortestPath = new ArrayList<CyNode>();
		List<CyNode> previous = nodePreviousMap.get(target);
		shortestPath.add(target);
		while (previous != null) {
			shortestPath.add(previous.get(0));
			previous = nodePreviousMap.get(target);
		}

		Collections.reverse(shortestPath);
		// HashMap<Integer, List<CyNode>> shortestPaths = new HashMap<Integer,
		// List<CyNode>>();
		// shortestPaths.put(1, shortestPath);
		return shortestPath;
	}
}