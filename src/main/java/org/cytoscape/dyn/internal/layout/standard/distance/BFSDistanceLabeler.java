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
 * 
 * The code below was adapted from the JUNG Project.
 * 
 * *********************************************************************** 
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 * ***********************************************************************
 */

package org.cytoscape.dyn.internal.layout.standard.distance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshot;
import org.cytoscape.model.CyNode;

/**
 * <code> BFSDistanceLabeler </code> labels each node in the graph according to the BFS 
 * distance from the start node(s).
 * 
 * @author Scott White
 * @author Sabina Sara Pfister - adaptation for Cytoscape
 *
 * @param <T>
 */
public class BFSDistanceLabeler<T> 
{

    private Map<CyNode, Number> distanceDecorator = new HashMap<CyNode,Number>();
    private List<CyNode> mCurrentList;
    private Set<CyNode> mUnvisitedVertices;
    private List<CyNode> mVerticesInOrderVisited;
    private Map<CyNode,HashSet<CyNode>> mPredecessorMap;

	/**
	 * <code> BFSDistanceLabeler </code> constructor.
	 */
	public BFSDistanceLabeler() 
	{
		mPredecessorMap = new HashMap<CyNode,HashSet<CyNode>>();
	}

    /**
     * Returns the list of nodes visited in order of traversal
     * @return the list of nodes
     */
    public List<CyNode> getVerticesInOrderVisited() 
    {
        return mVerticesInOrderVisited;
    }

    /**
     * Returns the set of all nodes that were not visited
     * @return the list of unvisited nodes
     */
    public Set<CyNode> getUnvisitedVertices() 
    {
        return mUnvisitedVertices;
    }

    /**
     * Given a node, returns the shortest distance from any node in the root set to v
     * @param v the node whose distance is to be retrieved
     * @return the shortest distance from any node in the root set to v
     */
    public int getDistance(DynNetworkSnapshot<T> g, CyNode node) 
    {
        if (!g.getNodes().contains(node)) 
            throw new IllegalArgumentException("Vertex is not contained in the graph.");

        return distanceDecorator.get(node).intValue();
    }

    /**
     * Returns set of predecessors of the given node
     * @param v the node whose predecessors are to be retrieved
     * @return the set of predecessors
     */
    public Set<CyNode> getPredecessors(CyNode node) 
    {
        return mPredecessorMap.get(node);
    }

    protected void initialize(DynNetworkSnapshot<T> g, Set<CyNode> rootSet) 
    {
        mVerticesInOrderVisited = new ArrayList<CyNode>();
        mUnvisitedVertices = new HashSet<CyNode>();
        for(CyNode currentVertex : g.getNodes()) 
        {
            mUnvisitedVertices.add(currentVertex);
            mPredecessorMap.put(currentVertex,new HashSet<CyNode>());
        }

        mCurrentList = new ArrayList<CyNode>();
        for(CyNode node : rootSet) 
        {
            distanceDecorator.put(node, new Integer(0));
            mCurrentList.add(node);
            mUnvisitedVertices.remove(node);
            mVerticesInOrderVisited.add(node);
        }
    }

    private void addPredecessor(CyNode predecessor,CyNode sucessor) 
    {
        HashSet<CyNode> predecessors = mPredecessorMap.get(sucessor);
        predecessors.add(predecessor);
    }

    /**
     * Computes the distances of all the node from the starting root nodes. If there is more than one root node
     * the minimum distance from each root node is used as the designated distance to a given node. Also keeps track
     * of the predecessors of each node traversed as well as the order of nodes traversed.
     * @param graph the graph to label
     * @param rootSet the set of starting vertices to traverse from
     */
    public void labelDistances(DynNetworkSnapshot<T> graph, Set<CyNode> rootSet) 
    {

        initialize(graph,rootSet);

        int distance = 1;
        while (true) 
        {
            List<CyNode> newList = new ArrayList<CyNode>();
            for(CyNode currentVertex : mCurrentList) 
            {
            	if(graph.conatinsNode(currentVertex)) 
            	{
            		for(CyNode next : graph.getSuccessors(currentVertex)) 
            		{
            			visitNewVertex(currentVertex,next, distance, newList);
            		}
            	}
            }
            if (newList.size() == 0) break;
            mCurrentList = newList;
            distance++;
        }

        for(CyNode node : mUnvisitedVertices) 
        {
            distanceDecorator.put(node,new Integer(-1));
        }
    }

    /**
     * Computes the distances of all the node from the specified root node. Also keeps track
     * of the predecessors of each node traversed as well as the order of nodes traversed.
     * @param graph the graph to label
     * @param root the single starting node to traverse from
     */
    public void labelDistances(DynNetworkSnapshot<T> graph, CyNode root) 
    {
        labelDistances(graph, Collections.singleton(root));
    }

    private void visitNewVertex(CyNode predecessor, CyNode neighbor, int distance, List<CyNode> newList) {
        if (mUnvisitedVertices.contains(neighbor)) 
        {
            distanceDecorator.put(neighbor, new Integer(distance));
            newList.add(neighbor);
            mVerticesInOrderVisited.add(neighbor);
            mUnvisitedVertices.remove(neighbor);
        }
        int predecessorDistance = distanceDecorator.get(predecessor).intValue();
        int successorDistance = distanceDecorator.get(neighbor).intValue();
        if (predecessorDistance < successorDistance) 
        {
            addPredecessor(predecessor,neighbor);
        }
    }

    /**
     * Returns a map from nodes to minimum distances from the original source(s).
     * Must be called after {@code labelDistances} in order to contain valid data.
     * @return node map
     */
    public Map<CyNode, Number> getDistanceDecorator() 
    {
        return distanceDecorator;
    }
}