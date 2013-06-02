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

import java.util.Map;

import org.cytoscape.model.CyNode;

/**
 * <code> Distance </code> is an interface for classes which calculate the distance between
 * one node and another.
 * 
 * @author Joshua O'Madadhain
 * @author Sabina Sara Pfister - adaptation for Cytoscape
 *
 * @param <T>
 */
public interface Distance<T>
{

    /**
     * Returns the distance from the source node 
     * to the target node. 
     * @param source
     * @param target
     * @return distance
     */
     Number getDistance(CyNode source, CyNode target);
     
     /**
      * Returns a Map which maps each node 
      * in the graph to its distance.
      * @param source
      * @return distance map
      */
     Map<CyNode,Number> getDistanceMap(CyNode source);
}