/**
 * 
 */
package org.cytoscape.dyn.internal.graphMetrics;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

/**
 * @author Jimmy
 *
 */
public class DynDirectedNetworkTask extends AbstractTask{

	@Tunable(description="The network loaded is directed. Would you like to treat it as directed in the computation of centralities?")
	public boolean treatDirected = false;
	
	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public boolean wantsDirected(){
		return this.treatDirected;
	}

}
