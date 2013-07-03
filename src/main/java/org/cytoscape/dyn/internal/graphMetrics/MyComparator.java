/**
 * 
 */
package org.cytoscape.dyn.internal.graphMetrics;

import java.util.Comparator;

import org.cytoscape.model.CyNode;

/**
 * @author Jimmy
 *
 */
public class MyComparator implements Comparator<CyNode> {

	@Override
	public int compare(CyNode node1, CyNode node2) {
		// TODO Auto-generated method stub
		return (node2.getSUID()>node1.getSUID() ? -1 : node1.getSUID() == node2.getSUID() ? 0 : 1);
	}

}
