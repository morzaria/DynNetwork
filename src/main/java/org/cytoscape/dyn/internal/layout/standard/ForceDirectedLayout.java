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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.dyn.internal.layout.standard.distance.Distance;
import org.cytoscape.dyn.internal.layout.standard.force.DragForce;
import org.cytoscape.dyn.internal.layout.standard.force.Force;
import org.cytoscape.dyn.internal.layout.standard.force.ForceItem;
import org.cytoscape.dyn.internal.layout.standard.force.ForceSimulator;
import org.cytoscape.dyn.internal.layout.standard.force.NBodyForce;
import org.cytoscape.dyn.internal.layout.standard.force.Spring;
import org.cytoscape.dyn.internal.layout.standard.force.SpringForce;
import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshot;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * <code> ForceDirectedLayout </code> implements the Prefuse force-directed layout algorithm.
 * 
 * @see <a href="http://prefuse.org">Prefuse web site</a>
 * 
 * @author Sabina Sara Pfister - adapted from static implementation for Cytoscape
 * 
 * @param <T>
 */
public class ForceDirectedLayout<T> extends AbstractLayout<T>
{
	private ForceSimulator m_fsim;
	private Map<CyNode,ForceItem> forceItems;
	private Map<CyEdge,Spring> springItems;
	private List<CyNode> nodeList;
	private List<CyEdge> edgeList;
	
	private long timestep;
	private int currentIteration;
	
	// maximum number of iterations
	private int maxIterations = 100;
	
	// center graph on center of gravity
	private boolean adjustForGravity = true;
	
	// default spring coefficient
	private double defaultSpringCoefficient = 1e-4;
	
	// default damping coefficient
	private double defaultDampingCoefficient = 0;
	
	// default spring length
	private double defaultSpringLength = 100.0;
	
	// default node mass
	private double defaultNodeMass = 1.0;

	/**
	 * <code> ForceDirectedLayout </code> constructor.
	 * @param g
	 * @param size
	 */
	public ForceDirectedLayout(DynNetworkSnapshot<T> g, Dimension size) 
    {
		super(g,size);
		m_fsim = new ForceSimulator();
		m_fsim.addForce((Force)new NBodyForce());
		m_fsim.addForce((Force)new SpringForce());
		m_fsim.addForce((Force)new DragForce());
		
		forceItems = new HashMap<CyNode, ForceItem>();
		springItems = new HashMap<CyEdge, Spring>(); 
		
		nodeList = new ArrayList<CyNode>();
		edgeList = new ArrayList<CyEdge>();
		
		timestep = 1000L;
	}

	/**
	 * Set spring coefficient.
	 * @param defaultSpringCoefficient
	 */
	public void setDefaultSpringCoefficient(double defaultSpringCoefficient) 
	{
		this.defaultSpringCoefficient = defaultSpringCoefficient;
	}

	/**
	 * Set maximum number of iterations.
	 * @param maxIterations
	 */
	public void setMaxIterations(int maxIterations) 
	{
		this.maxIterations = maxIterations;
	}
	
	/**
	 * Set spring length.
	 * @param defaultSpringLength
	 */
	public void setDefaultSpringLength(double defaultSpringLength) 
	{
		this.defaultSpringLength = defaultSpringLength;
	}

	/**
	 * Set node masses.
	 * @param defaultNodeMass
	 */
	public void setDefaultNodeMass(double defaultNodeMass) 
	{
		this.defaultNodeMass = defaultNodeMass;
	}
	
	/**
	 * Set damping constant.
	 * @param defaultNodeMass
	 */
	public void setDefaultDampingCoefficient(double defaultDampingCoefficient) 
	{
		this.defaultDampingCoefficient = defaultDampingCoefficient;
	}
	
    /**
     * Set adjust for gravity method.
     * @param on
     */
	public void setAdjustForGravity(boolean on) 
	{
		adjustForGravity = on;
	}

	@Override
	public boolean done() 
	{
		return (currentIteration > maxIterations);
	}

	@Override
	public void initialize() 
	{
		super.updateLocationsDirected();
		
		currentIteration = 0;
		
		// remove nodes
		for (CyNode ln : nodeList)
			if (!graph.conatinsNode(ln))
			{
				m_fsim.removeItem(forceItems.get(ln));
				forceItems.remove(ln);
			}
		
		// remove edges
		for (CyEdge le : edgeList)
			if (!graph.conatinsEdge(le))
			{
				m_fsim.removeSpring(springItems.get(le));
				springItems.remove(le);
			}

		// initialize nodes
		nodeList = graph.getNodes();
		for (CyNode ln: nodeList) 
		{
			ForceItem fitem = forceItems.get(ln); 
			if ( fitem == null ) 
			{
				fitem = new ForceItem();
				forceItems.put(ln, fitem);
				fitem.mass = (float) defaultNodeMass;
				fitem.location[0] = (float) this.locations.get(ln).getX(); 
				fitem.location[1] = (float) this.locations.get(ln).getY(); 
				m_fsim.addItem(fitem);
			}
		}
		
		// initialize edges
		edgeList = graph.getEdges();
		for (CyEdge e: edgeList) 
		{
			CyNode n1 = e.getSource();
			ForceItem f1 = forceItems.get(n1); 
			CyNode n2 = e.getTarget();
			ForceItem f2 = forceItems.get(n2); 
			if ( f1 == null || f2 == null )
				continue;
			Spring sitem = springItems.get(e);
			if ( sitem==null )
			{ 
				m_fsim.addSpring(
						f1, 
						f2, 
						(float) defaultSpringCoefficient, 
						(float) defaultDampingCoefficient,
						(float) defaultSpringLength*graph.getWeightMap().get(e).floatValue()); 
				springItems.put(e, sitem);
			}
		}
	}

	@Override
	public void print() 
	{
//		System.out.println("\nNODE LOCATIONS POSITIONS  ");
//		for (CyNode node : vertices)
//			System.out.println("  node : " + node.getSUID() + " " + 
//					this.locations.get(node).getX() + " : " + this.locations.get(node).getY());
	}

	@Override
	public void reset() 
	{
		m_fsim = new ForceSimulator();
		m_fsim.addForce((Force) new NBodyForce());
		m_fsim.addForce((Force) new SpringForce());
		m_fsim.addForce((Force) new DragForce());
		forceItems.clear();
	}

	@Override
	public void setDistance(Distance<T> distance) {}

	@Override
	public void step()
	{
		currentIteration++;
		timestep *= (1.0 - currentIteration/(double) maxIterations);
		m_fsim.runSimulator(timestep+50L);
		
		if (adjustForGravity)
			adjustForGravity();

		for (CyNode ln: graph.getNodes()) 
			if (!this.isLocked(ln)) 
				this.locations.get(ln).setLocation(forceItems.get(ln).location[0], forceItems.get(ln).location[1]);

	}

	// Shift all vertices so that the center of gravity is located at
	// the center of the screen.
	private void adjustForGravity() 
	{
//		Dimension d = getSize();
//		double height = d.getHeight();
//		double width = d.getWidth();
		double gx = 0;
		double gy = 0;
		for (CyNode ln: graph.getNodes()) 
		{
			gx += forceItems.get(ln).location[0];
			gy += forceItems.get(ln).location[1];
		}
		gx /= graph.getNodes().size();
		gy /= graph.getNodes().size();
//		double diffx = width / 2 - gx;
//		double diffy = height / 2 - gy;
		double diffx = 0 - gx;
		double diffy = 0 - gy;
		for (CyNode ln: graph.getNodes())
		{
			forceItems.get(ln).plocation[0] = (float) (forceItems.get(ln).plocation[0]+diffx);
			forceItems.get(ln).plocation[1] = (float) (forceItems.get(ln).plocation[1]+diffy);
			forceItems.get(ln).location[0] = (float) (forceItems.get(ln).location[0]+diffx);
			forceItems.get(ln).location[1] = (float) (forceItems.get(ln).location[1]+diffy);
		}
	}

}
