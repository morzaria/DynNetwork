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

import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshot;
import org.cytoscape.model.CyNode;

/**
 * <code> DistanceStatistics </code> computes the statistics relating to vertex-vertex 
 * distances in a graph.
 * 
 * @author Scott White
 * @author Joshua O'Madadhain
 * @author Sabina Sara Pfister - adaptation for Cytoscape
 *
 */
public class DistanceStatistics
{
    
	/**
	 * Returns the diameter of the graph> using the metric 
     * specified by the given distance. The value returned
     * will be the the maximum shortest path length over all pairs of connected 
     * nodes.
	 * @param <T>
	 * @param g
	 * @param d
	 * @param use_max
	 * @return diameter
	 */
    public static <T> double diameter(DynNetworkSnapshot<T> g, Distance<T> d, boolean use_max)
    {
        double diameter = 0;
        Collection<CyNode> vertices = g.getNodes();
        for(CyNode v : vertices) {
            for(CyNode w : vertices) {

                if (v.equals(w) == false) // don't include self-distances
                {
                    Number dist = d.getDistance(v, w);
                    if (dist == null)
                    {
                        if (!use_max)
                            return Double.POSITIVE_INFINITY;
                    }
                    else
                        diameter = Math.max(diameter, dist.doubleValue());
                }
            }
        }
        return diameter;
    }
    
    /**
     * Returns the diameter of the graph> using the metric 
     * specified by the given distance.
     * @param <T>
     * @param g
     * @param d
     * @return diameter
     */
    public static <T> double diameter(DynNetworkSnapshot<T> g, Distance<T> d)
    {
        return diameter(g, d, false);
    }
    
    /**
     * Returns the diameter of the graph, ignoring edge weights.
     * @param <T>
     * @param g
     * @return diameter
     */
    public static <T> double diameter(DynNetworkSnapshot<T> g)
    {
        return diameter(g, new UnweightedShortestPath<T>(g));
    }
    
}