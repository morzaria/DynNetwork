/*
 * DynNetwork plugin for Cytoscape 3.0 (http://www.cytoscape.org/).
 * Copyright (C) 2012 Sabina Sara Pfister
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.cytoscape.dyn.internal.model;

import java.util.List;

import org.cytoscape.dyn.internal.model.attribute.DynAttribute;
import org.cytoscape.dyn.internal.model.tree.AbstractDynInterval;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalTree;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * <code> DynNetwork </code> is an the interface to the object that represents a dynamic network
 * composed of {@link CyNode}s, connecting {@link CyEdge}s, attributes, and
 * the respective time intervals {@link AbstractDynInterval}s. It provides the link 
 * to the current static {@link CyNewtork}. In addition it maintains the
 * information about dynamic attributes {@link DynAttribute} in form of a
 * {@link DynIntervalTree}. 
 *
 * @author Sabina Sara Pfister
 *
 */
public interface DynNetwork<T>
{
	/**
	 * Insert graph.
	 * @param column
	 * @param interval
	 */
	public void insertGraph(String column, DynInterval<T> interval);
	
	/**
	 * Insert node.
	 * @param node
	 * @param column
	 * @param interval
	 */
	public void insertNode(CyNode node, String column, DynInterval<T> interval);
			
	/**
	 * Insert edge.
	 * @param ede
	 * @param column
	 * @param interval
	 */
	public void insertEdge(CyEdge ede, String column, DynInterval<T> interval);
	
	/**
	 * Search overlapping intervals for nodes given an interval.
	 * @param interval
	 * @return list of overlapping intervals
	 */
	public List<DynInterval<T>> searchNodes(DynInterval<T> interval);
	
	/**
	 * Search nodes given an interval.
	 * @param interval
	 * @return list of nodes
	 */
	public List<CyNode> getVisibleNodeList(DynInterval<T> interval);
	
	/**
	 * Search overlapping intervals for edges given an interval.
	 * @param interval
	 * @return list of overlapping intervals
	 */
	public List<DynInterval<T>> searchEdges(DynInterval<T> interval);
	
	/**
	 * Search edges given an interval.
	 * @param interval
	 * @return list of edges
	 */
	public List<CyEdge> getVisibleEdgeList(DynInterval<T> interval);
	
	/**
	 * Search overlapping intervals for graph attributes given an interval.
	 * @param interval
	 * @return list of overlapping intervals
	 */
	public List<DynInterval<T>> searchGraphsAttr(DynInterval<T> interval);
	
	/**
	 * Search overlapping intervals for node attributes given an interval.
	 * @param interval
	 * @return list of overlapping intervals
	 */
	public List<DynInterval<T>> searchNodesAttr(DynInterval<T> interval);
	
	/**
	 * Search overlapping intervals for edge attributes given an interval.
	 * @param interval
	 * @return list of overlapping intervals
	 */
	public List<DynInterval<T>> searchEdgesAttr(DynInterval<T> interval);
	
	/**
	 * Search overlapping intervals for edge attributes given an interval.
	 * filtered by the given attribute name.
	 * @param interval
	 * @param attName
	 * @return list of overlapping intervals
	 */
	public List<DynInterval<T>> searchEdgesAttr(DynInterval<T> interval, String attName);
	
	/**
	 * Search not overlapping intervals for nodes given an interval.
	 * @param interval
	 * @return list of not overlapping intervals
	 */
	public List<DynInterval<T>> searchNodesNot(DynInterval<T> interval);
	
	/**
	 * Search nodes outside the interval.
	 * @param interval
	 * @return list of invisible nodes
	 */
	public List<CyNode> getVisibleNodeNotList(DynInterval<T> interval);
	
	/**
	 * Search not overlapping intervals for edges given an interval.
	 * @param interval
	 * @return list of not overlapping intervals
	 */
	public List<DynInterval<T>> searchEdgesNot(DynInterval<T> interval);
	
	/**
	 * Search edges outside the interval.
	 * @param interval
	 * @return list of invisible edges
	 */
	public List<CyEdge> getVisibleEdgeNotList(DynInterval<T> interval);
	
    /**
     * Get graph attribute list
     * @return graph attribute list
     */
    public List<String> getGraphAttributes();
   
    /**
     * Get node attribute list
     * @return node attribute list
     */
    public List<String> getNodeAttributes();

    /**
     * Get edge attribute list
     * @return edge attribute list
     */
    public List<String> getEdgeAttributes();

	/**
	 * Get a list of times at which events occur.
	 * @return time list
	 */
	public List<Double> getEventTimeList();
	
	/**
	 * Get a list of times at which events occur filtered by attribute name.
	 * @param attName
	 * @return
	 */
	public List<Double> getEventTimeList(String attName);
	
	/**
	 * Get dynamic attribute for given network and name.
	 * @param network
	 * @param column
	 * @return dynamic attribute
	 */
	public DynAttribute<T> getDynAttribute(CyNetwork network, String column);
	
	/**
	 *  Get dynamic attribute for given node and name.
	 * @param node
	 * @param column
	 * @return dynamic attribute
	 */
	public DynAttribute<T> getDynAttribute(CyNode node, String column);
	
	/**
	 *  Get dynamic attribute for given edge and name.
	 * @param edge
	 * @param column
	 * @return dynamic attribute
	 */
	public DynAttribute<T> getDynAttribute(CyEdge edge, String column);
	
    /**
     * Get network.
     * @return CyNetwork
     */
	public CyNetwork getNetwork();
	
	/**
	 * Get network label.
	 * @return network label
	 */
	public String getNetworkLabel();
	
	/**
	 * Get node.
	 * @param id
	 * @return node
	 */
	public long getNode(String id);
	
	/**
	 * Get node label.
	 * @param node
	 * @return node label
	 */
	public String getNodeLabel(CyNode node);
	
	/**
	 * Get node.
	 * @param interval
	 * @return node
	 */
	public CyNode getNode(DynInterval<T> interval);
	
	/**
	 * Get edge.
	 * @param id
	 * @return edge
	 */
	public long getEdge(String id);
	
	/**
	 * Get edge label.
	 * @param edge
	 * @return edge label
	 */
	public String getEdgeLabel(CyEdge edge);
	
	/**
	 * Get edge.
	 * @param interval
	 * @return edge
	 */
	public CyEdge getEdge(DynInterval<T> interval);
	
	/**
	 * Contains node.
	 * @param id
	 * @return boolean
	 */
	public boolean containsCyNode(String id);
	
	/**
	 * Contains edge.
	 * @param id
	 * @return boolean
	 */
	public boolean containsCyEdge(String id);
	
	/**
	 * Set node.
	 * @param id
	 * @param value
	 */
	public void setCyNode(String id, long value);
	
	/**
	 * Set Edge.
	 * @param id
	 * @param value
	 */
	public void setCyEdge(String id, long value);
	
	/**
	 * Get interval list for graph attribute.
	 * @param attribute name (use 'name' to get the interval for the graph)
	 * @return interval list
	 */
	public List<DynInterval<T>> getGraphIntervals(String attName);
	
	/**
	 * Get interval list for node attribute.
	 * @param node
	 * @param attribute name (use 'name' to get the interval for the node)
	 * @return interval list
	 */
	public List<DynInterval<T>> getNodeIntervals(CyNode node, String attName);
	
	/**
	 * Get interval list for edge attribute.
	 * @param edge
	 * @param attribute name (use 'name' to get the interval for the edge)
	 * @return interval list
	 */
	public List<DynInterval<T>> getEdgeIntervals(CyEdge edge, String attName);
	
	/**
	 * Finalize network. We perform here all operations that require the network construction
	 * to be finished.
	 */
	public void finalizeNetwork();
	
	/**
	 * Get minimum time.
	 * @return minimum time
	 */
	public double getMinTime();
	
	/**
	 * Get maximum time.
	 * @return maximum time
	 */
	public double getMaxTime();
	
	/**
	 * Get minimum value over the entire dynamic network for this attribute.
	 * @param AttName
	 * @param type
	 * @return
	 */
	public T getMinValue(String AttName, Class<? extends CyIdentifiable> type);
	
	/**
	 * Get maximum value over the entire dynamic network for this attribute.
	 * @param AttName
	 * @param type
	 * @return
	 */
	public T getMaxValue(String AttName, Class<? extends CyIdentifiable> type);
	
	/**
	 * Get if the network is directed
	 * @return boolean
	 */
	public boolean isDirected();
	
	/**
	 * Print out network structure.
	 * @return
	 */
	public void print();

}
