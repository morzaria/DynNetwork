/**
 * 
 */
package org.cytoscape.dyn.internal.graphMetrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.DynNetworkFactoryImpl;
import org.cytoscape.dyn.internal.model.DynNetworkManagerImpl;
import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshotImpl;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalDouble;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManagerImpl;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * @author Jimmy
 * 
 */
public class EigenVector<T> extends AbstractTask {

	private DynNetworkViewManagerImpl<T> dynNetViewManager;
	private CyNetworkView cyNetworkView;
	private CyNetworkFactory networkFactory;
	private CyRootNetworkManager rootNetworkManager;
	private CyNetworkNaming nameUtil;
	private DynNetworkManagerImpl<T> dynNetManager;

	/**
	 * @param dynNetViewManager
	 * @param cyNetworkView
	 * @param networkFactory
	 * @param rootNetworkManager
	 * @param nameUtil
	 * @param dynNetManager
	 */
	public EigenVector(DynNetworkViewManagerImpl<T> dynNetViewManager,
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
	@Override
	public void run(TaskMonitor monitor) throws Exception {
		// TODO Auto-generated method stub
		monitor.setTitle("Calculating EigenVectorCentrality");
		DynNetworkFactoryImpl<T> dynNetFactory = new DynNetworkFactoryImpl<T>(
				networkFactory, rootNetworkManager, dynNetManager, nameUtil);
		DynNetworkView<T> view = dynNetViewManager
				.getDynNetworkView(cyNetworkView);
		DynNetworkSnapshotImpl<T> networkSnapshot = new DynNetworkSnapshotImpl<T>(
				view);

		// Need the dynamic network to get event time list
		DynNetwork<T> dynamicnetwork = view.getNetwork();
		System.out.println(CyTableUtil.getNodesInState(
				dynamicnetwork.getNetwork(), "selected", true));
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

		while (iterator.hasNext()) {

			snapshotInterval.setStart(startTime);
			endTime = iterator.next();
			snapshotInterval.setEnd(endTime);
			// List<CyNode> nodeList=new ArrayList<CyNode>();

			networkSnapshot.setInterval((DynInterval<T>) snapshotInterval, 0.0,
					0.0, 0.0);
			nodeList = networkSnapshot.getNodes();
			Collections.sort(nodeList, new MyComparator());

			double[][] adjacencyMatrixOfNetwork = new double[nodeList.size()][nodeList
					.size()];

			int i = 0;
			// System.out.println(nodeList);
			for (CyNode root : nodeList) {
				for (CyNode neighbor : networkSnapshot.getNeighbors(root)) {
					adjacencyMatrixOfNetwork[i][nodeList.indexOf(neighbor)] = 1.0;
				}
				i++;
			}
			Matrix A = new Matrix(adjacencyMatrixOfNetwork);

			EigenvalueDecomposition e = A.eig();
			Matrix V = e.getV();

			double[][] EigenVectors = V.getArray();

			double min = Double.MAX_VALUE, max = -Double.MAX_VALUE, totalsum = 0, currentvalue;
			for (int j = 0; j < nodeList.size(); j++) {

				currentvalue = EigenVectors[j][nodeList.size() - 1];
				// System.out.println(currentvalue);
				if (currentvalue < min) {
					min = currentvalue;
				}
				if (currentvalue > max) {
					max = currentvalue;
				}
				dynNetFactory.addedNodeAttribute(dynamicnetwork,
						nodeList.get(j), "Eigenvector",
						Double.toString(currentvalue), "real",
						startTime.toString(), endTime.toString());
				System.out.println(currentvalue);
				totalsum = totalsum + currentvalue;
			}
			startTime = endTime;
		}
	}
}
