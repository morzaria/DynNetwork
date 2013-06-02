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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.cytoscape.dyn.internal.layout.standard.util.BasicMapEntry;
import org.cytoscape.dyn.internal.layout.standard.util.MapBinaryHeap;
import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshot;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * <code> DijkstraDistance </code> calculates distances in a specified graph, using  
 * Dijkstra's single-source-shortest-path algorithm.  All edge weights
 * in the graph must be nonnegative.
 * 
 * @author Joshua O'Madadhain
 * @author Tom Nelson converted to jung2
 * @author Sabina Sara Pfister - adaptation for Cytoscape
 */
public class DijkstraDistance<T> implements Distance<T>
{
    protected DynNetworkSnapshot<T> g;
    protected Map<CyEdge, ? extends Number> weightMap;
    protected Map<CyNode,SourceData> sourceMap;
    protected boolean cached;
    protected double max_distance;
    protected int max_targets;
    
    /**
     * <code> DijkstraDistance </code> constructor.
     * @param g
     * @param nev
     * @param cached
     */
    public DijkstraDistance(DynNetworkSnapshot<T> g, Map<CyEdge, ? extends Number> weightMap, boolean cached) 
    {
        this.g = g;
        this.weightMap = weightMap;
        this.sourceMap = new HashMap<CyNode,SourceData>();
        this.cached = cached;
        this.max_distance = Double.POSITIVE_INFINITY;
        this.max_targets = Integer.MAX_VALUE;
    }
    
    /**
     * <code> DijkstraDistance </code> constructor.
     * @param g
     * @param nev
     */
    public DijkstraDistance(DynNetworkSnapshot<T> g, Map<CyEdge, ? extends Number> nev) 
    {
        this(g, nev, true);
    }
    
    /**
     * Implements Dijkstra's single-source shortest-path algorithm for
     * weighted graphs.
     * @param source    the vertex from which distances are to be measured
     * @param numDests  the number of distances to measure
     * @param targets   the set of vertices to which distances are to be measured
     */
    protected LinkedHashMap<CyNode,Number> singleSourceShortestPath(CyNode source, Collection<CyNode> targets, int numDests)
    {
        SourceData sd = getSourceData(source);

        Set<CyNode> to_get = new HashSet<CyNode>();
        if (targets != null) {
            to_get.addAll(targets);
            Set<CyNode> existing_dists = sd.distances.keySet();
            for(CyNode o : targets) {
                if (existing_dists.contains(o))
                    to_get.remove(o);
            }
        }
        
        if (sd.reached_max ||
            (targets != null && to_get.isEmpty()) ||
            (sd.distances.size() >= numDests))
        {
            return sd.distances;
        }
        
        while (!sd.unknownVertices.isEmpty() && (sd.distances.size() < numDests || !to_get.isEmpty()))
        {
            Map.Entry<CyNode,Number> p = sd.getNextVertex();
            CyNode v = p.getKey();
            double v_dist = p.getValue().doubleValue();
            to_get.remove(v);
            if (v_dist > this.max_distance) 
            {
                sd.restoreVertex(v, v_dist);
                sd.reached_max = true;
                break;
            }
            sd.dist_reached = v_dist;

            if (sd.distances.size() >= this.max_targets)
            {
                sd.reached_max = true;
                break;
            }
            
            for (CyEdge e : getEdgesToCheck(v) )
            {
                for (CyNode w : g.getNodes(e))
                {
                    if (!sd.distances.containsKey(w))
                    {
                        double edge_weight = weightMap.get(e).doubleValue();
                        if (edge_weight < 0)
                            throw new IllegalArgumentException("Edges weights must be non-negative");
                        double new_dist = v_dist + edge_weight;
                        if (!sd.estimatedDistances.containsKey(w))
                        {
                            sd.createRecord(w, e, new_dist);
                        }
                        else
                        {
                            double w_dist = ((Double)sd.estimatedDistances.get(w)).doubleValue();
                            if (new_dist < w_dist) 
                                sd.update(w, e, new_dist);
                        }
                    }
                }
            }
        }
        return sd.distances;
    }

    protected SourceData getSourceData(CyNode source)
    {
        SourceData sd = sourceMap.get(source);
        if (sd == null)
            sd = new SourceData(source);
        return sd;
    }
    
    /**
     * Returns the set of edges incident to <code>v</code> that should be tested.
     * By default, this is the set of outgoing edges for instances of <code>Graph</code>,
     * the set of incident edges for instances of <code>Hypergraph</code>,
     * and is otherwise undefined.
     * @param node
     */
    protected Collection<CyEdge> getEdgesToCheck(CyNode node)
    {
        if (g instanceof DynNetworkSnapshot<?>)
            return ((DynNetworkSnapshot<T>)g).getOutEdges(node);
        else
            return g.getEdges(node);

    }

    /**
     * Returns the length of a shortest path from the source to the target vertex,
     * or null if the target is not reachable from the source.
     * @param source
     * @param target
     */
    public Number getDistance(CyNode source, CyNode target)
    {
        if (g.conatinsNode(target) == false)
            throw new IllegalArgumentException("Specified target vertex " + 
                    target + " is not part of graph " + g);
        if (g.conatinsNode(source) == false)
            throw new IllegalArgumentException("Specified source vertex " + 
                    source + " is not part of graph " + g);
        
        Set<CyNode> targets = new HashSet<CyNode>();
        targets.add(target);
        Map<CyNode,Number> distanceMap = getDistanceMap(source, targets);
        return distanceMap.get(target);
    }
    
    /**
     * Returns a {@code Map} from each element {@code t} of {@code targets} to the 
     * shortest-path distance from {@code source} to {@code t}.
     * @param source
     * @param targets
     * @return
     */
    public Map<CyNode,Number> getDistanceMap(CyNode source, Collection<CyNode> targets)
    {
       if (g.conatinsNode(source) == false)
            throw new IllegalArgumentException("Specified source vertex " + 
                    source + " is not part of graph " + g);
       if (targets.size() > max_targets)
            throw new IllegalArgumentException("size of target set exceeds maximum " +
                    "number of targets allowed: " + this.max_targets);
        
        Map<CyNode,Number> distanceMap = 
        	singleSourceShortestPath(source, targets, 
        			Math.min(g.getNodeCount(), max_targets));
        if (!cached)
            reset(source);
        
        return distanceMap;
    }
    
    /**
     * Returns a <code>LinkedHashMap</code> which maps each vertex 
     * in the graph (including the <code>source</code> vertex) 
     * to its distance from the <code>source</code> vertex.
     */
    public Map<CyNode,Number> getDistanceMap(CyNode source)
    {
        return getDistanceMap(source, Math.min(g.getNodeCount(), max_targets));
    }

    /**
     * Returns a <code>LinkedHashMap</code> which maps each of the closest 
     * <code>numDist</code> vertices to the <code>source</code> vertex 
     * in the graph (including the <code>source</code> vertex) 
     * to its distance from the <code>source</code> vertex.
     * @param source
     * @param numDests
     * @return
     */
    public LinkedHashMap<CyNode,Number> getDistanceMap(CyNode source, int numDests)
    {

    	if(g.getNodes().contains(source) == false) {
            throw new IllegalArgumentException("Specified source vertex " + 
                    source + " is not part of graph " + g);
    		
    	}
        if (numDests < 1 || numDests > g.getNodeCount())
            throw new IllegalArgumentException("numDests must be >= 1 " + 
                "and <= g.numVertices()");

        if (numDests > max_targets)
            throw new IllegalArgumentException("numDests must be <= the maximum " +
                    "number of targets allowed: " + this.max_targets);
            
        LinkedHashMap<CyNode,Number> distanceMap = 
        	singleSourceShortestPath(source, null, numDests);
                
        if (!cached)
            reset(source);
        
        return distanceMap;        
    }
    
    /**
     * Allows the user to specify the maximum distance that this instance will calculate.
     * @param max_dist
     */
    public void setMaxDistance(double max_dist)
    {
        this.max_distance = max_dist;
        for (CyNode node : sourceMap.keySet())
        {
            SourceData sd = sourceMap.get(node);
            sd.reached_max = (this.max_distance <= sd.dist_reached) || (sd.distances.size() >= max_targets);
        }
    }
       
    /**
     * Allows the user to specify the maximum number of target vertices per source vertex 
     * for which this instance will calculate distances.
     * @param max_targets
     */
    public void setMaxTargets(int max_targets)
    {
        this.max_targets = max_targets;
        for (CyNode node : sourceMap.keySet())
        {
            SourceData sd = sourceMap.get(node);
            sd.reached_max = (this.max_distance <= sd.dist_reached) || (sd.distances.size() >= max_targets);
        }
    }
    
    /**
     * Reset.
     */
    public void reset()
    {
        sourceMap = new HashMap<CyNode,SourceData>();
    }
        
    /**
     * Enable caching.
     * @param enable
     */
    public void enableCaching(boolean enable)
    {
        this.cached = enable;
    }
    
    /**
     * Clears all stored distances for the specified source vertex 
     * <code>source</code>.
     * @param source
     */
    public void reset(CyNode source)
    {
        sourceMap.put(source, null);
    }

    /**
     * Compares according to distances, so that the BinaryHeap knows how to 
     * order the tree.  
     */
    protected static class VertexComparator implements Comparator<CyNode>
    {
        private Map<CyNode,Number> distances;
        
        protected VertexComparator(Map<CyNode,Number> distances)
        {
            this.distances = distances;
        }

        public int compare(CyNode o1, CyNode o2)
        {
            return ((Double) distances.get(o1)).compareTo((Double) distances.get(o2));
        }
    }
    
    /**
     * For a given source vertex, holds the estimated and final distances, 
     * tentative and final assignments of incoming edges on the shortest path from
     * the source vertex, and a priority queue (ordered by estimated distance)
     * of the vertices for which distances are unknown.
     * 
     * @author Joshua O'Madadhain
     */
    protected class SourceData
    {
        protected LinkedHashMap<CyNode,Number> distances;
        protected Map<CyNode,Number> estimatedDistances;
        protected MapBinaryHeap<CyNode> unknownVertices;
        protected boolean reached_max = false;
        protected double dist_reached = 0;

        protected SourceData(CyNode source)
        {
            distances = new LinkedHashMap<CyNode,Number>();
            estimatedDistances = new HashMap<CyNode,Number>();
            unknownVertices = new MapBinaryHeap<CyNode>(new VertexComparator(estimatedDistances));
            sourceMap.put(source, this);
            estimatedDistances.put(source, new Double(0));
            unknownVertices.add(source);
            reached_max = false;
            dist_reached = 0;
        }
        
        protected Map.Entry<CyNode,Number> getNextVertex()
        {
        	CyNode node = unknownVertices.remove();
            Double dist = (Double)estimatedDistances.remove(node);
            distances.put(node, dist);
            return new BasicMapEntry<CyNode,Number>(node, dist);
        }
        
        protected void update(CyNode dest, CyEdge tentative_edge, double new_dist)
        {
            estimatedDistances.put(dest, new_dist);
            unknownVertices.update(dest);
        }
        
        protected void createRecord(CyNode node, CyEdge e, double new_dist)
        {
            estimatedDistances.put(node, new_dist);
            unknownVertices.add(node);
        }
        
        protected void restoreVertex(CyNode node, double dist) 
        {
            estimatedDistances.put(node, dist);
            unknownVertices.add(node);
            distances.remove(node);
        }
    }
}