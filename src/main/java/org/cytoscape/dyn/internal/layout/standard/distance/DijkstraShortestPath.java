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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshot;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * <code> DijkstraShortestPath </code> calculates distances and shortest paths using Dijkstra's   
 * single-source-shortest-path algorithm.
 * 
 * @author Joshua O'Madadhain
 * @author Tom Nelson converted to jung2
 * @author Sabina Sara Pfister - adaptation for Cytoscape
 * 
 * @see DijkstraDistance
 */
public class DijkstraShortestPath<T> extends DijkstraDistance<T> implements ShortestPath<T>
{
    /**
     * <code> DijkstraShortestPath </code> constructor.
     * @param g
     * @param nev
     * @param cached
     */
    public DijkstraShortestPath(DynNetworkSnapshot<T> g, Map<CyEdge, ? extends Number> nev, boolean cached)
    {
        super(g, nev, cached);
    }
    
    /**
     * <code> DijkstraShortestPath </code> constructor.
     * @param g
     * @param nev
     */
    public DijkstraShortestPath(DynNetworkSnapshot<T> g, Map<CyEdge, ? extends Number> nev)
    {
        super(g, nev);
    }
    
    /**
     * <code> DijkstraShortestPath </code> constructor.
     * @param g
     * @param nev
     * @param max_targets
     */
    public DijkstraShortestPath(DynNetworkSnapshot<T> g, Map<CyEdge, ? extends Number> nev, int max_targets)
    {
        super(g, nev);
        super.setMaxTargets(max_targets);
    }
    
    @Override
    protected SourceData getSourceData(CyNode source)
    {
        SourceData sd = sourceMap.get(source);
        if (sd == null)
            sd = new SourcePathData(source);
        return sd;
    }
    
    /**
     * Returns the last edge on a shortest path from <code>source</code>
     * to <code>target</code>
     * @param source
     * @param target
     * @return
     */
	public CyEdge getIncomingEdge(CyNode source, CyNode target)
	{
        if (!g.conatinsNode(source))
            throw new IllegalArgumentException("Specified source vertex " + 
                    source + " is not part of graph " + g);
        
        if (!g.conatinsNode(target))
            throw new IllegalArgumentException("Specified target vertex " + 
                    target + " is not part of graph " + g);

        Set<CyNode> targets = new HashSet<CyNode>();
        targets.add(target);
        singleSourceShortestPath(source, targets, g.getNodeCount());
        Map<CyNode,CyEdge> incomingEdgeMap = 
            ((SourcePathData)sourceMap.get(source)).incomingEdges;
        CyEdge incomingEdge = incomingEdgeMap.get(target);
        
        if (!cached)
            reset(source);
        
        return incomingEdge;
	}

    /**
     * Returns a <code>LinkedHashMap</code> which maps each vertex 
     * in the graph (including the <code>source</code> vertex) 
     * to the last edge on the shortest path from the 
     * <code>source</code> vertex.
     * @param source
     */
    public Map<CyNode,CyEdge> getIncomingEdgeMap(CyNode source)
	{
		return getIncomingEdgeMap(source, g.getNodeCount());
	}
    
    /**
     * Returns a <code>List</code> of the edges on the shortest path from 
     * <code>source</code> to <code>target</code>, in order of their
     * occurrence on this path.
     * @param source
     * @param target
     * @return edge list
     */
	public List<CyEdge> getPath(CyNode source, CyNode target)
	{
		if(!g.conatinsNode(source)) 
            throw new IllegalArgumentException("Specified source vertex " + 
                    source + " is not part of graph " + g);
        
		if(!g.conatinsNode(target)) 
            throw new IllegalArgumentException("Specified target vertex " + 
                    target + " is not part of graph " + g);
        
        LinkedList<CyEdge> path = new LinkedList<CyEdge>();

        // collect path data; must use internal method rather than
        // calling getIncomingEdge() because getIncomingEdge() may
        // wipe out results if results are not cached
        Set<CyNode> targets = new HashSet<CyNode>();
        targets.add(target);
        singleSourceShortestPath(source, targets, g.getNodeCount());
        Map<CyNode,CyEdge> incomingEdges = 
            ((SourcePathData)sourceMap.get(source)).incomingEdges;
        
        if (incomingEdges.isEmpty() || incomingEdges.get(target) == null)
            return path;
        CyNode current = target;
        while (!current.equals(source))
        {
        	CyEdge incoming = incomingEdges.get(current);
            path.addFirst(incoming);
            current = ((DynNetworkSnapshot<T>)g).getOpposite(current, incoming);
        }
		return path;
	}

    /**
     * Returns a <code>LinkedHashMap</code> which maps each of the closest 
     * <code>numDist</code> vertices to the <code>source</code> vertex 
     * in the graph (including the <code>source</code> vertex) 
     * to the incoming edge along the path from that vertex.
     * @param source
     * @param numDests
     * @return map of closest nodes
     */
	public LinkedHashMap<CyNode,CyEdge> getIncomingEdgeMap(CyNode source, int numDests)
	{
        if (g.getNodes().contains(source) == false)
            throw new IllegalArgumentException("Specified source vertex " + 
                    source + " is not part of graph " + g);

        if (numDests < 1 || numDests > g.getNodeCount())
            throw new IllegalArgumentException("numDests must be >= 1 " + 
            "and <= g.numVertices()");

        singleSourceShortestPath(source, null, numDests);
        
        LinkedHashMap<CyNode,CyEdge> incomingEdgeMap = 
            ((SourcePathData)sourceMap.get(source)).incomingEdges;
        
        if (!cached)
            reset(source);
        
        return incomingEdgeMap;        
	}
     
    
    /**
     * For a given source vertex, holds the estimated and final distances, 
     * tentative and final assignments of incoming edges on the shortest path from
     * the source vertex, and a priority queue (ordered by estimaed distance)
     * of the vertices for which distances are unknown.
     * 
     * @author Joshua O'Madadhain
     * @author Sabina Sara Pfister - adaptation for Cytoscape
     */
    protected class SourcePathData extends SourceData
    {
        protected Map<CyNode,CyEdge> tentativeIncomingEdges;
		protected LinkedHashMap<CyNode,CyEdge> incomingEdges;

		protected SourcePathData(CyNode source)
		{
            super(source);
            incomingEdges = new LinkedHashMap<CyNode,CyEdge>();
            tentativeIncomingEdges = new HashMap<CyNode,CyEdge>();
		}
        
        @Override
        public void update(CyNode dest, CyEdge tentative_edge, double new_dist)
        {
            super.update(dest, tentative_edge, new_dist);
            tentativeIncomingEdges.put(dest, tentative_edge);
        }
        
        @Override
        public Map.Entry<CyNode,Number> getNextVertex()
        {
            Map.Entry<CyNode,Number> p = super.getNextVertex();
            CyNode v = p.getKey();
            CyEdge incoming = tentativeIncomingEdges.remove(v);
            incomingEdges.put(v, incoming);
            return p;
        }
        
        @Override
        public void restoreVertex(CyNode v, double dist)
        {
            super.restoreVertex(v, dist);
            CyEdge incoming = incomingEdges.get(v);
            tentativeIncomingEdges.put(v, incoming);
        }
        
        @Override
        public void createRecord(CyNode w, CyEdge e, double new_dist)
        {
            super.createRecord(w, e, new_dist);
            tentativeIncomingEdges.put(w, e);
        }
       
    }

}