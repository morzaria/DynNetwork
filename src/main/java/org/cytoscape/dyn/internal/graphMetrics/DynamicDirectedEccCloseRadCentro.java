/**
 * 
 */
package org.cytoscape.dyn.internal.graphMetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.DynNetworkFactory;
import org.cytoscape.dyn.internal.model.DynNetworkFactoryImpl;
import org.cytoscape.dyn.internal.model.DynNetworkManagerImpl;
import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshot;
import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshotImpl;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalDouble;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManagerImpl;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * @author Jimmy
 * 
 */
public class DynamicDirectedEccCloseRadCentro<T> extends AbstractTask {

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
	public DynamicDirectedEccCloseRadCentro(
			DynNetworkViewManagerImpl<T> dynNetViewManager,
			CyNetworkView cyNetworkView, CyNetworkFactory networkFactory,
			CyRootNetworkManager rootNetworkManager, CyNetworkNaming nameUtil,
			DynNetworkManagerImpl<T> dynNetManager) {
		super();
		this.dynNetViewManager = dynNetViewManager;
		this.cyNetworkView = cyNetworkView;
		this.networkFactory = networkFactory;
		this.rootNetworkManager = rootNetworkManager;
		this.nameUtil = nameUtil;
		this.dynNetManager = dynNetManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(TaskMonitor monitor) throws Exception {
		// TODO Auto-generated method stub

		monitor.setTitle("Calculating Centrality parameters");

		DynNetworkFactory<T> dynNetFactory = new DynNetworkFactoryImpl<T>(
				networkFactory, rootNetworkManager, dynNetManager, nameUtil);

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
		List<CyNode> nodeList = new ArrayList<CyNode>();

		// HashMap for (TimeInterval, Node, Eccentricity) and (TimeInterval,
		// Distance)
		nodeTimeEccentricityMap = new HashMap<Double, HashMap<CyNode, Double>>();

		// HashMap for (node,Distance to node from source)
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
						.getDirectedDijkstraDistanceMap(node1);
				sourceNodeDistanceMap.put(node1, nodeDistanceMap);
				// System.out.println(nodeDistanceMap);
				// finding the distance of the node farthest to the source node
				nodeIterator = nodeList.iterator();
				Double max = 0.0, closeness = 0.0;
				while (nodeIterator.hasNext()) {

					CyNode node = nodeIterator.next();
					if (node != node1) {
						closeness += 1 / nodeDistanceMap.get(node);
					}
					if (nodeDistanceMap.get(node) > max
							&& nodeDistanceMap.get(node) != Double.POSITIVE_INFINITY) {
						max = nodeDistanceMap.get(node);
					}
				}

				nodeClosenessMap.put(node1, closeness);
				if (max != 0) {
					nodeEccentricityMap.put(node1, 1 / max);
				} else {
					nodeEccentricityMap.put(node1, max);
				}
				// System.out.println(Node1.toString()+"  "+max);

				if (max > dynamicGraphDistance) {
					dynamicGraphDistance = max;
				}

			}

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

				nodeRadialityMap.put(node1,
						(1 / ((networkSnapshot.getNodeCount() - 1)
								* (dynamicGraphDistance) - (nodeClosenessMap
								.get(node1)))));
				nodeCentroidMap.put(node1, 0);
			}

			// Saving the radiality of each node in each time interval in a
			// HashMap
			nodeTimeRadialityMap.put(snapshotInterval.getStart(),
					nodeRadialityMap);

			// System.out.println(dynamicGraphDistance);
			// System.out.println(nodeEccentricityMap);
			// System.out.println(nodeClosenessMap);

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
		dynNetFactory.finalizeNetwork(dynamicnetwork);
	}

}
