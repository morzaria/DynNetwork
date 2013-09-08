/**
 * 
 */
package org.cytoscape.dyn.internal.io.read.csv;

import org.cytoscape.dyn.internal.io.event.Sink;
import org.cytoscape.dyn.internal.io.event.Source;
import org.cytoscape.dyn.internal.layout.model.DynLayoutFactory;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.DynNetworkFactory;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewFactory;
import org.cytoscape.dyn.internal.vizmapper.model.DynVizMapFactory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * @author Jimmy
 * 
 */
public abstract class AbstractCSVSource<T> implements Source<T> {

	protected DynNetworkFactory<T> networkSink;
	protected DynNetworkViewFactory<T> viewSink;
	protected DynLayoutFactory<T> layoutSink;
	protected DynVizMapFactory<T> vizMapSink;

	protected DynNetwork<T> addGraph(String id, String label, String start,
			String end, String directed) {
		return networkSink.addedGraph(id, label, start, end, directed);
	}

	protected CyNode addNode(DynNetwork<T> currentNetwork, String id,
			String label, String start, String end) {
		return networkSink.addedNode(currentNetwork, id, label, start, end);
	}

	protected CyEdge addEdge(DynNetwork<T> currentNetwork, String id,
			String label, String source, String target, String start, String end) {
		return networkSink.addedEdge(currentNetwork, id, label, source, target,
				start, end);
	}

	protected void addGraphAttribute(DynNetwork<T> currentNetwork, String name,
			String value, String type, String start, String end) {
		networkSink.addedGraphAttribute(currentNetwork, name, value, type,
				start, end);
	}

	protected void addNodeAttribute(DynNetwork<T> network, CyNode currentNode,
			String name, String value, String type, String start, String end) {
		networkSink.addedNodeAttribute(network, currentNode, name, value, type,
				start, end);
	}

	protected void addEdgeAttribute(DynNetwork<T> network, CyEdge currentEdge,
			String name, String value, String type, String start, String end) {
		networkSink.addedEdgeAttribute(network, currentEdge, name, value, type,
				start, end);
	}

	protected void addGraphGraphics(DynNetwork<T> network, String fill,
			String start, String end) {
		vizMapSink.addedGraphGraphics(network, fill, start, end);
	}

	protected void addNodeGraphics(DynNetwork<T> network, CyNode currentNode,
			String type, String height, String width, String size, String fill,
			String linew, String outline, String transparency, String start,
			String end) {
		vizMapSink.addedNodeGraphics(network, currentNode, type, height, width,
				size, fill, linew, outline, transparency, start, end);
	}

	protected void addEdgeGraphics(DynNetwork<T> network, CyEdge currentEdge,
			String width, String fill, String transparency, String start,
			String end) {
		vizMapSink.addedEdgeGraphics(network, currentEdge, width, fill,
				transparency, start, end);
	}

	protected void addNodeDynamics(DynNetwork<T> network, CyNode currentNode,
			String x, String y, String start, String end) {
		layoutSink.addedNodeDynamics(network, currentNode, x, y, start, end);
	}

	protected void finalize(DynNetwork<T> currentNetwork) {
		networkSink.finalizeNetwork(currentNetwork);
	}

	@Override
	public void addSink(Sink<T> sink) {
		if (sink instanceof DynNetworkViewFactory<?>)
			this.networkSink = (DynNetworkFactory<T>) sink;
		else if (sink instanceof DynNetworkViewFactory<?>)
			this.viewSink = (DynNetworkViewFactory<T>) sink;
		else if (sink instanceof DynLayoutFactory<?>)
			this.layoutSink = (DynLayoutFactory<T>) sink;
		else if (sink instanceof DynVizMapFactory<?>)
			this.vizMapSink = (DynVizMapFactory<T>) sink;
	}

	@Override
	public void removeSink(Sink<T> sink) {
		if (this.networkSink == sink)
			this.networkSink = null;
		else if (this.viewSink == sink)
			this.viewSink = null;
		else if (this.layoutSink == sink)
			this.layoutSink = null;
		else if (this.vizMapSink == sink)
			this.vizMapSink = null;
	}
}
