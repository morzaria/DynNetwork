/**
 * 
 */
package org.cytoscape.dyn.internal.graphMetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManagerImpl;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.DynNetworkFactory;
import org.cytoscape.dyn.internal.model.DynNetworkFactoryImpl;
import org.cytoscape.dyn.internal.model.DynNetworkManagerImpl;
import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshotImpl;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalDouble;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.dyn.internal.graphMetrics.DijkstraDistance;

/**
 * <code>DynamicDistEccCloseRad</code> computes Eccentricity,
 * Closenness, Radiality and Centroid of nodes in
 * undirected networks.
 * 
 * @author Jimmy
 *
 * @param <T>
 */

public class DynamicDistEccCloseRad<T> extends AbstractTask {

	private DynNetworkViewManagerImpl<T> dynNetViewManager;
	private CyNetworkView cyNetworkView;
	private CyNetworkFactory networkFactory;
	private CyRootNetworkManager rootNetworkManager;
	private CyNetworkNaming nameUtil;
	private DynNetworkManagerImpl<T> dynNetManager;
	private HashMap<Double, HashMap<CyNode, Double>> nodeTimeEccentricityMap;
	private HashMap<Double, HashMap<CyNode, Double>> nodeTimeClosenessMap;
	private HashMap<Double, HashMap<CyNode, Double>> nodeTimeRadialityMap;
	private HashMap<Double, HashMap<CyNode, Integer>> nodeTimeCentroidMap;
	private HashMap<Double, Double> distanceTimeMap;

	/**
	 * @param dynNetViewManager
	 * @param cyNetworkView
	 * @param networkFactory
	 * @param rootNetworkManager
	 * @param nameUtil
	 * @param dynNetManager
	 */
	public DynamicDistEccCloseRad(
			DynNetworkViewManagerImpl<T> dynNetViewManager,
			CyNetworkView cyNetworkView, CyNetworkFactory networkFactory,
			CyRootNetworkManager rootNetworkManager, CyNetworkNaming nameUtil,
			DynNetworkManagerImpl<T> dynNetManager) {
		this.dynNetViewManager = dynNetViewManager;
		this.cyNetworkView = cyNetworkView;
		this.networkFactory = networkFactory;
		this.rootNetworkManager = rootNetworkManager;
		this.nameUtil = nameUtil;
		this.dynNetManager = dynNetManager;
	}

	@SuppressWarnings("unchecked")
	public void run(TaskMonitor monitor) {

		monitor.setTitle("Calculating Centrality parameters");

		DynNetworkFactory<T> dynNetFactory = new DynNetworkFactoryImpl<T>(
				networkFactory, rootNetworkManager, dynNetManager, nameUtil);
		// To get DynNetworkView which need to be passed to
		// DynNetworkSnapshotImpl
		// Collection<DynNetworkView<T>> dyncollection=new
		// ArrayList<DynNetworkView<T>>();
		// dyncollection=dynNetViewManager.getDynNetworkViews();
		// Iterator<DynNetworkView<T>> it=dyncollection.iterator();

		DynNetworkView<T> view = dynNetViewManager
				.getDynNetworkView(cyNetworkView);
		DynNetworkSnapshotImpl<T> networkSnapshot = new DynNetworkSnapshotImpl<T>(
				view);

		// Need the dynamic network to get event time list
		DynNetwork<T> dynamicnetwork = view.getNetwork();

		// Declaring and Initialising eventTimeList of the Dynamic Network
		List<Double> eventTimeList = new ArrayList<Double>();
		eventTimeList = dynamicnetwork.getEventTimeList();
		Iterator<Double> iterator = eventTimeList.iterator();

		// Declaring and Initialising two temporary variables for start time and
		// end time
		Double startTime, endTime;
		startTime = iterator.next();
		DynIntervalDouble snapshotInterval = new DynIntervalDouble(startTime,
				startTime);
		networkSnapshot.setInterval((DynInterval<T>) snapshotInterval, 0.0,
				0.0, 0.0);

		// Declaring a nodeList
		List<CyNode> nodeList = new ArrayList<CyNode>();
		CyNode node;

		// HashMap for (TimeInterval, Node, Eccentricity) and (TimeInterval,
		// Distance)
		nodeTimeEccentricityMap = new HashMap<Double, HashMap<CyNode, Double>>();
		distanceTimeMap = new HashMap<Double, Double>();

		// HashMap for (TimeInterval, Node, Closeness)
		nodeTimeClosenessMap = new HashMap<Double, HashMap<CyNode, Double>>();

		// HashMap for (TimeInterval, Node, Radiality)
		nodeTimeRadialityMap = new HashMap<Double, HashMap<CyNode, Double>>();

		// HashMap for (TimeInterval, Node, Centroid)
		nodeTimeCentroidMap = new HashMap<Double, HashMap<CyNode, Integer>>();

		DijkstraDistance<T> dijkstraDistance;

		while (iterator.hasNext()) {
			// loop through all the time intervals

			Double dynamicGraphDistance = 0.0;
			HashMap<CyNode, Double> nodeDistanceMap = new HashMap<CyNode, Double>();
			HashMap<CyNode, Double> nodeEccentricityMap = new HashMap<CyNode, Double>();
			HashMap<CyNode, Double> nodeClosenessMap = new HashMap<CyNode, Double>();
			HashMap<CyNode, Double> nodeRadialityMap = new HashMap<CyNode, Double>();
			HashMap<CyNode, Integer> nodeCentroidMap = new HashMap<CyNode, Integer>();
			HashMap<CyNode, HashMap<CyNode, Double>> sourceNodeDistanceMap = new HashMap<CyNode, HashMap<CyNode, Double>>();

			snapshotInterval.setStart(startTime);
			endTime = iterator.next();
			snapshotInterval.setEnd(endTime);

			networkSnapshot.setInterval((DynInterval<T>) snapshotInterval, 0.0,
					0.0, 0.0);
			dijkstraDistance = new DijkstraDistance<T>(networkSnapshot);
			nodeList = networkSnapshot.getNodes();
			Iterator<CyNode> nodeIterator = nodeList.iterator();

			// computing eccentricity, distance, closeness, radiality,
			// betweenness for each node in a particular time interval
			for (CyNode node1 : nodeList) {

				nodeDistanceMap = dijkstraDistance
						.getDijkstraDistanceMap(node1);
				sourceNodeDistanceMap.put(node1, nodeDistanceMap);

				// finding the distance of the node farthest to the source node
				nodeIterator = nodeList.iterator();
				Double max = 0.0, closeness = 0.0;
				while (nodeIterator.hasNext()) {

					node = nodeIterator.next();
					closeness += nodeDistanceMap.get(node);

					if (nodeDistanceMap.get(node) > max) {
						max = nodeDistanceMap.get(node);
					}
				}

				nodeClosenessMap.put(node1, 1 / closeness);
				nodeEccentricityMap.put(node1, 1 / max);

				// System.out.println(Node1.toString()+"  "+max);

				if (max > dynamicGraphDistance) {
					dynamicGraphDistance = max;
				}

			}
			// System.out.println(nodeClosenessMap);

			// Saving the graphDistance for different time intervals in a
			// HashMap
			distanceTimeMap.put(snapshotInterval.getStart(),
					dynamicGraphDistance);

			// Saving the eccentricity of each node in each time interval in a
			// HashMap
			nodeTimeEccentricityMap.put(snapshotInterval.getStart(),
					nodeEccentricityMap);

			// Saving the closeness of each node in each time interval in a
			// HashMap
			nodeTimeClosenessMap.put(snapshotInterval.getStart(),
					nodeClosenessMap);

			// System.out.println(dynamicGraphDistance);

			for (CyNode node1 : networkSnapshot.getNodes()) {

				nodeRadialityMap
						.put(node1,
								((networkSnapshot.getNodeCount() - 1)
										* (dynamicGraphDistance + 1) - (1 / nodeClosenessMap
										.get(node1)))
										/ (networkSnapshot.getNodeCount() - 1));
				nodeCentroidMap.put(node1, 0);
			}

			// Saving the radiality of each node in each time interval in a
			// HashMap
			nodeTimeRadialityMap.put(snapshotInterval.getStart(),
					nodeRadialityMap);

			int countNode1 = 0, countNode2 = 0;

			for (CyNode node1 : networkSnapshot.getNodes()) {

				for (CyNode node2 : networkSnapshot.getNodes()) {

					for (CyNode node3 : networkSnapshot.getNodes()) {

						if (node1 != node3 && node2 != node3 && node1 != node2) {

							if (sourceNodeDistanceMap.get(node1).get(node3) < sourceNodeDistanceMap
									.get(node2).get(node3)) {
								countNode1++;
							} else if (sourceNodeDistanceMap.get(node1).get(
									node3) > sourceNodeDistanceMap.get(node2)
									.get(node3)) {
								countNode2++;
							}
						}
					}

					if ((countNode1 - countNode2) < nodeCentroidMap.get(node1)) {
						nodeCentroidMap.put(node1, (countNode1 - countNode2));
					}
					countNode1 = 0;
					countNode2 = 0;
				}

			}

			nodeTimeCentroidMap.put(snapshotInterval.getStart(),
					nodeCentroidMap);

			// Setting Graph distance to 0 for the next iteration of time
			dynamicGraphDistance = 0.0;
			for (CyNode node1 : nodeList) {
				dynNetFactory.setAttributesUpdate(
						dynamicnetwork,
						node1,
						"Centroid",
						Double.toString(nodeTimeCentroidMap.get(
								snapshotInterval.getStart()).get(node1)),
						"real", startTime.toString(), endTime.toString());
				dynNetFactory.setAttributesUpdate(
						dynamicnetwork,
						node1,
						"Eccentricity",
						Double.toString(nodeTimeEccentricityMap.get(
								snapshotInterval.getStart()).get(node1)),
						"real", startTime.toString(), endTime.toString());
				dynNetFactory.setAttributesUpdate(
						dynamicnetwork,
						node1,
						"Closeness",
						Double.toString(nodeTimeClosenessMap.get(
								snapshotInterval.getStart()).get(node1)),
						"real", startTime.toString(), endTime.toString());
				dynNetFactory.setAttributesUpdate(
						dynamicnetwork,
						node1,
						"Radiality",
						Double.toString(nodeTimeRadialityMap.get(
								snapshotInterval.getStart()).get(node1)),
						"real", startTime.toString(), endTime.toString());
			}
			startTime = endTime;
		}
		// System.out.println("Eccentricity\n"+nodeTimeEccentricityMap+"\n");
		// System.out.println("Closeness\n"+nodeTimeClosenessMap+"\n");
		// System.out.println("Radiality\n"+nodeTimeRadialityMap+"\n");
		// System.out.println("Centroid\n"+nodeTimeCentroidMap);
	}
}
