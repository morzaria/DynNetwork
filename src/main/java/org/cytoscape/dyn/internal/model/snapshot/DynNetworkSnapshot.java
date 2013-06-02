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

package org.cytoscape.dyn.internal.model.snapshot;

import java.util.List;
import java.util.Map;

import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * <code> DynNetworkSnapshot </code> is the interface to investigate a dynamic network in a specific
 * snapshot in time given a time interval.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public interface DynNetworkSnapshot<T>
{
	/**
	 * Set time interval for snapshot.
	 * @param interval
	 * @param gaussMean
	 * @param gaussStdPast
	 * @param gaussStdFuture
	 */
	public void setInterval(DynInterval<T> interval, double gaussMean, double gaussStdPast, double gaussStdFuture);
	
	/**
	 * Get neighbors of this node
	 * @param node
	 * @return node list
	 */
	public List<CyNode> getNeighbors(CyNode node);
	
	/**
	 * Get edges connected to this node.
	 * @param node
	 * @return edge list
	 */
	public List<CyEdge> getEdges(CyNode node);
	
	/**
	 * Get incident edges connected to this node.
	 * @param node
	 * @return edge list
	 */
	public List<CyEdge> getInEdges(CyNode node);
	
	/**
	 * Get outgoing edges connected to this node.
	 * @param node
	 * @return edge list
	 */
	public List<CyEdge> getOutEdges(CyNode node);
	
	/**
	 * Get nodes connected to this edge (source first, target second).
	 * @param edge
	 * @return edge source and target list
	 */
	public List<CyNode> getNodes(CyEdge edge);
	
	/**
	 * Get opposite node.
	 * @param node
	 * @param edge
	 * @return node
	 */
	public CyNode getOpposite(CyNode node, CyEdge edge);
	
	/**
	 * Find first edge between node1 and node2
	 * @param node1
	 * @param node2
	 * @return edge
	 */
	public CyEdge findEdge(CyNode node1, CyNode node2);
	
	/**
	 * Find all edges between node1 and node2.
	 * @param node1
	 * @param node2
	 * @return edge list
	 */
	public List<CyEdge> findEdgeSet(CyNode node1, CyNode node2);
	
	/**
	 * AIf nod1 and node2 are neighbor.
	 * @param node1
	 * @param node2
	 * @return boolean
	 */
	public boolean isNeighbor(CyNode node1, CyNode node2);
	
	/**
	 * Is edge incident on node.
	 * @param node
	 * @param edge
	 * @return boolean
	 */
	public boolean isIncident(CyNode node, CyEdge edge);
	
	/**
	 * Get list of isDirected edges.
	 * @param isDirected
	 * @return edge list.
	 */
	public List<CyEdge> getEdges(boolean isDirected);
	
	/**
	 * Get number of isDirected edges.
	 * @param isDirected
	 * @return count
	 */
	public  int getEdgeCount(boolean isDirected);
	
	/**
	 * Get all nodes.
	 * @return node list.
	 */
	public List<CyNode> getNodes();

	/**
	 * Get all edges.
	 * @return edge list
	 */
	public List<CyEdge> getEdges();
	
	/**
	 * Get distance map for given edge attribute (set up with the constructor).
	 * @return distance map for edge
	 */
	public Map<CyEdge,? extends Number> getWeightMap();
	
	/**
	 * Get degree.
	 * @param node
	 * @return degree
	 */
	public int getDegree(CyNode node);
	
	/**
	 * Get in degree.
	 * @param node
	 * @return in degree
	 */
	public int inDegree(CyNode node);
	
	/**
	 * Get out degree.
	 * @param node
	 * @return out degree
	 */
	public int outDegree(CyNode node);
	
	/**
	 * Get list of predecessor nodes.
	 * @param node
	 * @return node list
	 */
	public List<CyNode> getPredecessors(CyNode node);
	
	/**
	 *Get list of successors nodes. 
	 * @param node
	 * @return node list
	 */
	public List<CyNode> getSuccessors(CyNode node);
	
	/**
	 * Is node 1 predecessor of node2.
	 * @param node1
	 * @param node2
	 * @return boolean
	 */
	public boolean isPredecessor(CyNode node1, CyNode node2);
	
	/**
	 * Is node 1 successors of node2.
	 * @param node1
	 * @param node2
	 * @return boolean
	 */
	public boolean isSuccessor(CyNode node1, CyNode node2);
	
	/**
	 * Get predecessors count.
	 * @param node
	 * @return predecessors
	 */
	public int getPredecessorCount(CyNode node);
	
	/**
	 * Get successors count
	 * @param node
	 * @return successors
	 */
	public int getSuccessorCount(CyNode node);
	
	/**
	 * If contains node.
	 * @param node
	 * @return boolean
	 */
	public boolean conatinsNode(CyNode node);
	
	/**
	 * If contains edge.
	 * @param edge
	 * @return boolean
	 */
	public boolean conatinsEdge(CyEdge edge);
	
	/**
	 * Get node count.
	 * @return node count
	 */
	public int getNodeCount();
	
	/**
	 * Get edge count.
	 * @return edge count
	 */
	public int getEdgeCount();
	
	/**
	 * Get dynamic network view.
	 * @return dynamic network view class
	 */
	public DynNetworkView<T> getNetworkView();

	/**
	 * Get time interval for this snapshot.
	 * @return
	 */
	public DynInterval<T> getInterval();
	
	/**
	 * Get time interval list for node.
	 * @return
	 */
	public List<DynInterval<T>> getIntervalList(CyNode node);
	
	/**
	 * Get time interval list for edge.
	 * @return
	 */
	public List<DynInterval<T>> getIntervalList(CyEdge edge);	
	
	/**
	 * Print list of nodes and edges.
	 * @return
	 */
	public void print();
	
}
