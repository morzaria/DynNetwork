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

package org.cytoscape.dyn.internal.layout.standard;

import java.awt.Dimension;

import org.cytoscape.dyn.internal.layout.standard.distance.Distance;
import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshot;
import org.cytoscape.model.CyNode;

/**
 * <code> Layout </code> is a  generalized interface for returning (x,y) coordinates 
 * from nodes.
 * 
 * @author Sabina Sara Pfister - adaptation for Cytoscape
 *
 * @param <T>
 */
public interface Layout<T> 
{
    
	/**
	 * Initialize layout.
	 */
	public void initialize();
	
	/**
	 * Set distance matrix.
	 * @param distance
	 */
	public void setDistance(Distance<T> distance);

	/**
	 * Get graph.
	 * @return graph snapshot
	 */
	public DynNetworkSnapshot<T> getGraph();
	
	/**
	 * Reset layout.
	 */
	public void reset();
	
	/**
	 * Set dimensions for layout.
	 * @param d
	 */
	public void setSize(Dimension d);
	
	/**
	 * Get layout dimensions
	 * @return dimension
	 */
	public Dimension getSize();


	/**
	 * Lock/unlock node
	 * @param node
	 * @param state
	 */
	public void lock(CyNode node, boolean state);
	
	/**
	 * Set lock/unlock.
	 * @param lock
	 */
	public void lock(boolean lock);

    /**
     * Return if node is locked.
     * @param node
     * @return
     */
	public boolean isLocked(CyNode node);
	
	/**
	 * Advances one step.
	 */
	public void step();
	
	/**
	 * Run energy minimization on all unlocked nodes.
	 */
	public void run();

	/**
	 * Returns true if this iterative process is finished, and false otherwise.
	 */
	public boolean done();
	
	/**
	 * Get node X position.
	 * @param node
	 * @return
	 */
	public double getX(CyNode node);
	
	/**
	 * Get node Z position.
	 * @param node
	 * @return
	 */
	public double getY(CyNode node);
	
	/**
	 * Print node positions.
	 */
	public void print();


}