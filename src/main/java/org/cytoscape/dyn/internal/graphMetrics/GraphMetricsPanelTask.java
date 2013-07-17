/**
 * 
 */
package org.cytoscape.dyn.internal.graphMetrics;

import java.util.Properties;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.dyn.internal.CyActivator;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.DynNetworkManagerImpl;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManagerImpl;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * @author Jimmy
 * 
 */
public class GraphMetricsPanelTask<T, C> extends AbstractTask {

	private CyActivator<T, C> cyActivator;
	private DynNetworkViewManagerImpl<T> dynNetViewManager;
	private CyNetworkView cyNetworkView;

	/**
	 * @param cyActivator
	 * @param dynNetViewManager
	 * @param cyNetworkView
	 */
	public GraphMetricsPanelTask(CyActivator<T, C> cyActivator,
			DynNetworkViewManagerImpl<T> dynNetViewManager,
			CyNetworkView cyNetworkView) {
		this.cyActivator = cyActivator;
		this.dynNetViewManager = dynNetViewManager;
		this.cyNetworkView = cyNetworkView;

	}

	@Override
	public void run(TaskMonitor monitor) throws Exception {
		// TODO Auto-generated method stub
		monitor.setTitle("Building the User Interface");
		DynNetworkView<T> view = dynNetViewManager
				.getDynNetworkView(cyNetworkView);
		DynNetwork<T> dynamicnetwork = view.getNetwork();
		GraphMetricsPanel<T, C> graphMetricsPanel = new GraphMetricsPanel<T, C>(
				this.cyActivator, dynamicnetwork);
		this.cyActivator.getcyServiceRegistrar().registerService(
				graphMetricsPanel, CytoPanelComponent.class, new Properties());

	}

}
