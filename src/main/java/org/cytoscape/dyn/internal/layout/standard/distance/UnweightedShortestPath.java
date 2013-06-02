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

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshot;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * <code> UnweightedShortestPath </code> computes the shortest path distances 
 * for graphs whose edges are not weighted (using BFS).
 * 
 * @author Scott White
 * @author Sabina Sara Pfister - adaptation for Cytoscape
 *
 * @param <T>
 */
public class UnweightedShortestPath<T> implements ShortestPath<T>, Distance<T>
{
	private Map<CyNode,Map<CyNode,Number>> mDistanceMap;
	private Map<CyNode,Map<CyNode,CyEdge>> mIncomingEdgeMap;
	private DynNetworkSnapshot<T> mGraph;
    private Map<CyNode, Number> distances = new HashMap<CyNode,Number>();

	/**
	 * <code> UnweightedShortestPath </code> constructor.
	 * @param g
	 */
	public UnweightedShortestPath(DynNetworkSnapshot<T> g)
	{
		mDistanceMap = new HashMap<CyNode,Map<CyNode,Number>>();
		mIncomingEdgeMap = new HashMap<CyNode,Map<CyNode,CyEdge>>();
		mGraph = g;
	}

    /**
     * @see edu.uci.ics.jung.algorithms.shortestpath.Distance#getDistance(Object, Object)
     */
	public Number getDistance(CyNode source, CyNode target)
	{
		Map<CyNode, Number> sourceSPMap = getDistanceMap(source);
		return sourceSPMap.get(target);
	}

    /**
     * @see edu.uci.ics.jung.algorithms.shortestpath.Distance#getDistanceMap(Object)
     */
	public Map<CyNode,Number> getDistanceMap(CyNode source)
	{
		Map<CyNode,Number> sourceSPMap = mDistanceMap.get(source);
		if (sourceSPMap == null)
		{
			computeShortestPathsFromSource(source);
			sourceSPMap = mDistanceMap.get(source);
		}
		return sourceSPMap;
	}

	/**
	 * @see edu.uci.ics.jung.algorithms.shortestpath.ShortestPath#getIncomingEdgeMap(Object)
	 */
	public Map<CyNode,CyEdge> getIncomingEdgeMap(CyNode source)
	{
		Map<CyNode,CyEdge> sourceIEMap = mIncomingEdgeMap.get(source);
		if (sourceIEMap == null)
		{
			computeShortestPathsFromSource(source);
			sourceIEMap = mIncomingEdgeMap.get(source);
		}
		return sourceIEMap;
	}

	private void computeShortestPathsFromSource(CyNode source)
	{
		BFSDistanceLabeler<T> labeler = new BFSDistanceLabeler<T>();
		labeler.labelDistances(mGraph, source);
        distances = labeler.getDistanceDecorator();
		Map<CyNode,Number> currentSourceSPMap = new HashMap<CyNode,Number>();
		Map<CyNode,CyEdge> currentSourceEdgeMap = new HashMap<CyNode,CyEdge>();

        for(CyNode vertex : mGraph.getNodes()) {
            
			Number distanceVal = distances.get(vertex);

            if (distanceVal != null && distanceVal.intValue() >= 0) 
            {
                currentSourceSPMap.put(vertex, distanceVal);
                int minDistance = distanceVal.intValue();
                for(CyEdge incomingEdge : mGraph.getInEdges(vertex)) 
                {
                	for (CyNode neighbor : mGraph.getNodes(incomingEdge))
                	{
                		if (neighbor.equals(vertex))
                			continue;
	
	                    Number predDistanceVal = distances.get(neighbor);
	
	                    int pred_distance = predDistanceVal.intValue();
	                    if (pred_distance < minDistance && pred_distance >= 0)
	                    {
	                        minDistance = predDistanceVal.intValue();
	                        currentSourceEdgeMap.put(vertex, incomingEdge);
	                    }
                	}
                }
            }
		}
		mDistanceMap.put(source, currentSourceSPMap);
		mIncomingEdgeMap.put(source, currentSourceEdgeMap);
	}
    
    /**
     * Reset object.
     */
    public void reset()
    {
        mDistanceMap.clear();
        mIncomingEdgeMap.clear();
    }
    
    /**
     * Reset node.
     * @param node
     */
    public void reset(CyNode node)
    {
        mDistanceMap.remove(node);
        mIncomingEdgeMap.remove(node);
    }
}