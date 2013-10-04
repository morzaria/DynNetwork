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

/**
 * <code>DynamicDegree</code> computes the degree of nodes in directed networks.
 * 
 * @author Jimmy
 *
 * @param <T>
 */
public class DynamicDegree<T> extends AbstractTask {

	private DynNetworkViewManagerImpl<T> dynNetViewManager;
	private CyNetworkView cyNetworkView;
	private CyNetworkFactory networkFactory;
	private CyRootNetworkManager rootNetworkManager;
	private CyNetworkNaming nameUtil;
	private DynNetworkManagerImpl<T> dynNetManager;
	private HashMap<Double, HashMap<CyNode, Integer>> nodeTimeInDegreeMap;
	private HashMap<Double, HashMap<CyNode, Integer>> nodeTimeOutDegreeMap;
	private HashMap<CyNode, Integer> nodeInDegreeMap;
	private HashMap<CyNode, Integer> nodeOutDegreeMap;
	/**
	 * @param dynNetViewManager
	 * @param cyNetworkView
	 * @param networkFactory
	 * @param rootNetworkManager
	 * @param nameUtil
	 * @param dynNetManager
	 */
	public DynamicDegree(DynNetworkViewManagerImpl<T> dynNetViewManager,
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

		DynNetworkFactory<T> dynNetFactory = new DynNetworkFactoryImpl<T>(
				networkFactory, rootNetworkManager, dynNetManager, nameUtil);
		
		DynNetworkView<T> view = dynNetViewManager
				.getDynNetworkView(cyNetworkView);
		DynNetworkSnapshotImpl<T> networkSnapshot = new DynNetworkSnapshotImpl<T>(
				view);
		DynIntervalDouble snapshotInterval = new DynIntervalDouble(3.0, 4.0);
		networkSnapshot.setInterval((DynInterval<T>) snapshotInterval, 0.0,
				0.0, 0.0);

		DynNetwork<T> dynamicnetwork = view.getNetwork();
		dynamicnetwork.getNodeAttributes();
		// Declaring and Initialising eventTimeList of the Dynamic Network

		List<Double> eventTimeList = new ArrayList<Double>();
		eventTimeList = dynamicnetwork.getEventTimeList();

		Iterator<Double> iterator = eventTimeList.iterator();

		// Declaring and Initialising two temporary variables for start time and
		// end time

		Double startTime, endTime;
		startTime = iterator.next();

		// Declaring a nodeList

		List<CyNode> nodeList = new ArrayList<CyNode>();
		CyNode node;

		nodeTimeInDegreeMap = new HashMap<Double, HashMap<CyNode, Integer>>();
		nodeTimeOutDegreeMap = new HashMap<Double, HashMap<CyNode, Integer>>();
		nodeInDegreeMap = new HashMap<CyNode, Integer>();
		nodeOutDegreeMap = new HashMap<CyNode, Integer>();
		// Computing the indegree for each node for each time interval

		while (iterator.hasNext()) {

			snapshotInterval.setStart(startTime);
			endTime = iterator.next();
			snapshotInterval.setEnd(endTime);

			networkSnapshot.setInterval((DynInterval<T>) snapshotInterval, 0.0,
					0.0, 0.0);
			nodeList = networkSnapshot.getNodes();
			Iterator<CyNode> nodeIterator = nodeList.iterator();
			// System.out.println("For Time Interval "+snapshotInterval.getStart()+"-"+snapshotInterval.getEnd()+":");
			while (nodeIterator.hasNext()) {
				node = nodeIterator.next();
				nodeInDegreeMap.put(node, networkSnapshot.inDegree(node));
				nodeOutDegreeMap.put(node, networkSnapshot.outDegree(node));
				// System.out.println("In Degree of Node "+node.getSUID()+" is "+networkSnapshot.inDegree(node));
			}
			nodeTimeInDegreeMap.put(snapshotInterval.getStart(),
					nodeInDegreeMap);
			nodeTimeOutDegreeMap.put(snapshotInterval.getStart(),
					nodeOutDegreeMap);

			for (CyNode node1 : nodeList) {
				dynNetFactory.setAttributesUpdate(
						dynamicnetwork,
						node1,
						"Degree",
						Double.toString(nodeTimeInDegreeMap.get(
								snapshotInterval.getStart()).get(node1)+nodeTimeOutDegreeMap.get(
										snapshotInterval.getStart()).get(node1)),
						"real", startTime.toString(), endTime.toString());
				
			}
			startTime = endTime;

		}
		
	}
}
