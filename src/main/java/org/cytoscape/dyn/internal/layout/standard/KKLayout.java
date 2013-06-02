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
import java.awt.geom.Point2D;

import org.cytoscape.dyn.internal.layout.standard.distance.Distance;
import org.cytoscape.dyn.internal.layout.standard.distance.DistanceStatistics;
import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshot;
import org.cytoscape.model.CyNode;

/**
 * <code> KKLayout </code> implements the Kamada-Kawai algorithm for node layout.
 * 
 * @see "Tomihisa Kamada and Satoru Kawai: An algorithm for drawing general indirect graphs. Information Processing Letters 31(1):7-15, 1989" 
 * @see "Tomihisa Kamada: On visualization of abstract objects and relations. Ph.D. dissertation, Dept. of Information Science, Univ. of Tokyo, Dec. 1988."
 * 
 * @author Masanori Harada
 * @author Sabina Sara Pfister - adaptation for Cytoscape
 * 
 * @param <T>
 */
public final class KKLayout<T> extends AbstractLayout<T>
{
	private String status = "KKLayout";
	
	private CyNode[] vertices;
	private Point2D[] xydata;
	
	private int currentIteration;
	
	// maximum number of iterations
    private int maxIterations = 2000;
    
    // epsilon
    private double EPSILON = 0.1d;

	// the ideal length of an edge
	private double L;			
	
	// arbitrary constant number
	private double K = 1;		
	
	// distance matrix
	private double[][] dm;      

	// center graph on center of gravity
	private boolean adjustForGravity = true;
	
	// local minima escape method
	private boolean exchangeVertices = true;
	
	// autoscaling method
	private boolean autoscaling = true;

    // Graph distances between vertices of the visible graph
    private Distance<T> distance;

    // The diameter of the visible graph. In other words, the maximum over all pairs
    // of vertices of the length of the shortest path between a and bf the visible graph.
	private double diameter;

    // A multiplicative factor which partly specifies the "preferred" length of an edge (L).
    private double length_factor = 0.9;

    // A multiplicative factor which specifies the fraction of the graph's diameter to be 
    // used as the inter-vertex distance between disconnected vertices.
    private double disconnected_multiplier = 1;
    
	/**
	 * <code> KKLayout </code> constructor.
	 * @param g
	 * @param size
	 */
	public KKLayout(DynNetworkSnapshot<T> g, Dimension size) 
    {
		super(g,size);
	}

    /**
     * Sets a multiplicative factor which 
     * partly specifies the "preferred" length of an edge.
     */
    public void setLengthFactor(double length_factor)
    {
        this.length_factor = length_factor;
    }
    
    /**
     * Sets a multiplicative factor that specifies the fraction of the graph's diameter to be 
     * used as the inter-vertex distance between disconnected vertices.
     */
    public void setDisconnectedDistanceMultiplier(double disconnected_multiplier)
    {
        this.disconnected_multiplier = disconnected_multiplier;
    }
    
    /**
     * Returns a string with information about the current status of the algorithm.
     */
	public String getStatus() 
	{
		return status + this.getSize();
	}

	/**
	 * Sets the maximum number of iterations.
	 */
    public void setMaxIterations(int maxIterations) 
    {
        this.maxIterations = maxIterations;
    }

    /**
     * Set adjust for gravity method.
     * @param on
     */
	public void setAdjustForGravity(boolean on) 
	{
		adjustForGravity = on;
	}

	/**
	 * Set exchanged method to escape local minima.
	 * @param on
	 */
	public void setExchangeVertices(boolean on) 
	{
		exchangeVertices = on;
	}
	
	/**
	 * Set autoscaling method.
	 * @param on
	 */
    public void setAutoscaling(boolean autoscaling) 
    {
		this.autoscaling = autoscaling;
	}

	@Override
	public boolean done() 
	{
		return (currentIteration > maxIterations);
	}
	
	@Override
	public void reset() 
	{
		currentIteration = 0;
	}
	
	@Override
	public void print()
	{
		System.out.println("\nNODE LOCATIONS POSITIONS  ");
		for (CyNode node : vertices)
			System.out.println("  node : " + node.getSUID() + " " + 
					this.locations.get(node).getX() + " : " + this.locations.get(node).getY());
			
	}
	
	@Override
    public void setDistance(Distance<T> distance) 
    {
		this.distance = distance;
    }

	@Override
    public void initialize() 
    {
		super.updateLocations();
		
    	currentIteration = 0;

    	if(graph != null && size != null) 
    	{
    		double height = size.getHeight();
    		double width = size.getWidth();

    		int n = graph.getNodeCount();
    		dm = new double[n][n];
    		vertices = (CyNode[])graph.getNodes().toArray(new CyNode[graph.getNodes().size()]);
    		xydata = new Point2D[n];

    		int index = 0;
			for(CyNode node : graph.getNodes()) 
			{
				Point2D xyd = transform(node);
				vertices[index] = node;
				xydata[index] = xyd;
				index++;
			}

			if (diameter==0 || this.autoscaling)
				diameter = DistanceStatistics.diameter(graph, distance, true);

    		double L0 = Math.min(height, width);
    		L = (L0 / diameter) * length_factor;

    		for (int i = 0; i < n - 1; i++) 
    		{
    			for (int j = i + 1; j < n; j++) 
    			{
    				Number d_ij = distance.getDistance(vertices[i], vertices[j]);
    				Number d_ji = distance.getDistance(vertices[j], vertices[i]);
    				double dist = diameter * disconnected_multiplier;
    				if (d_ij != null)
    					dist = Math.min(d_ij.doubleValue(), dist);
    				if (d_ji != null)
    					dist = Math.min(d_ji.doubleValue(), dist);
//    				if (dist==0)
//    					dist = 0.00001;
    				dm[i][j] = dm[j][i] = dist;
    			}
    		}
    	}
	}

	@Override
	public void step() 
	{
		try {
			currentIteration++;
			double energy = calcEnergy();
			status = "Kamada-Kawai V=" + getGraph().getNodeCount()
			+ "(" + getGraph().getNodeCount() + ")"
			+ " IT: " + currentIteration
			+ " E=" + energy
			;

			int n = graph.getNodeCount();
			if (n == 0)
				return;

			double maxDeltaM = 0;
			int pm = -1;            
			for (int i = 0; i < n; i++) 
			{
				if (isLocked(vertices[i]))
					continue;
				double deltam = calcDeltaM(i);

				if (maxDeltaM < deltam) 
				{
					maxDeltaM = deltam;
					pm = i;
				}
			}
			if (pm == -1)
				return;

			for (int i = 0; i < 100; i++) 
			{
				double[] dxy = calcDeltaXY(pm);
				xydata[pm].setLocation(xydata[pm].getX()+dxy[0], xydata[pm].getY()+dxy[1]);

				double deltam = calcDeltaM(pm);
				if (deltam < EPSILON)
					break;
			}

			if (adjustForGravity)
				adjustForGravity();

			if (exchangeVertices && maxDeltaM < EPSILON) 
			{
				energy = calcEnergy();
				for (int i = 0; i < n - 1; i++) 
				{
					if (isLocked(vertices[i]))
						continue;
					for (int j = i + 1; j < n; j++) 
					{
						if (isLocked(vertices[j]))
							continue;
						double xenergy = calcEnergyIfExchanged(i, j);
						if (energy > xenergy) {
							double sx = xydata[i].getX();
							double sy = xydata[i].getY();
							xydata[i].setLocation(xydata[j]);
							xydata[j].setLocation(sx, sy);
							return;
						}
					}
				}
			}
		}
		finally {
		}
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
		for (int i = 0; i < xydata.length; i++) 
		{
			gx += xydata[i].getX();
			gy += xydata[i].getY();
		}
		gx /= xydata.length;
		gy /= xydata.length;
//		double diffx = width / 2 - gx;
//		double diffy = height / 2 - gy;
		double diffx = 0 - gx;
		double diffy = 0 - gy;
		for (int i = 0; i < xydata.length; i++) 
            xydata[i].setLocation(xydata[i].getX()+diffx, xydata[i].getY()+diffy);
	}

	// Determines a step to new position of the vertex m.
	private double[] calcDeltaXY(int m) 
	{
		double dE_dxm = 0;
		double dE_dym = 0;
		double d2E_d2xm = 0;
		double d2E_dxmdym = 0;
		double d2E_dymdxm = 0;
		double d2E_d2ym = 0;

		for (int i = 0; i < vertices.length; i++) 
		{
			if (i != m) {
                
                double dist = dm[m][i];
				double l_mi = L * dist;
				double k_mi = K / (dist * dist);
				double dx = xydata[m].getX() - xydata[i].getX();
				double dy = xydata[m].getY() - xydata[i].getY();
				double d = Math.sqrt(dx * dx + dy * dy);
				double ddd = d * d * d;
				
				dE_dxm += k_mi * (1 - l_mi / d) * dx;
				dE_dym += k_mi * (1 - l_mi / d) * dy;
				d2E_d2xm += k_mi * (1 - l_mi * dy * dy / ddd);
				d2E_dxmdym += k_mi * l_mi * dx * dy / ddd;
				d2E_d2ym += k_mi * (1 - l_mi * dx * dx / ddd);
			
			}
		}
		
		d2E_dymdxm = d2E_dxmdym;

		double denomi = d2E_d2xm * d2E_d2ym - d2E_dxmdym * d2E_dymdxm;
		double deltaX = (d2E_dxmdym * dE_dym - d2E_d2ym * dE_dxm) / denomi;
		double deltaY = (d2E_dymdxm * dE_dxm - d2E_d2xm * dE_dym) / denomi;
		return new double[]{deltaX, deltaY};
	}

	// Calculates the gradient of energy function at the vertex m.
	private double calcDeltaM(int m) 
	{
		double dEdxm = 0;
		double dEdym = 0;
		for (int i = 0; i < vertices.length; i++) 
		{
			if (i != m) {
                double dist = dm[m][i];
				double l_mi = L * dist;
				double k_mi = K / (dist * dist);

				double dx = xydata[m].getX() - xydata[i].getX();
				double dy = xydata[m].getY() - xydata[i].getY();
				double d = Math.sqrt(dx * dx + dy * dy);

				double common = k_mi * (1 - l_mi / d);
				dEdxm += common * dx;
				dEdym += common * dy;
			}
		}
		return Math.sqrt(dEdxm * dEdxm + dEdym * dEdym);
	}

	// Calculates the energy function E.
	private double calcEnergy() 
	{
		double energy = 0;
		for (int i = 0; i < vertices.length - 1; i++) 
		{
			for (int j = i + 1; j < vertices.length; j++) 
			{
                double dist = dm[i][j];
				double l_ij = L * dist;
				double k_ij = K / (dist * dist);
				double dx = xydata[i].getX() - xydata[j].getX();
				double dy = xydata[i].getY() - xydata[j].getY();
				double d = Math.sqrt(dx * dx + dy * dy);

				energy += k_ij / 2 * (dx * dx + dy * dy + l_ij * l_ij -
									  2 * l_ij * d);
			}
		}
		return energy;
	}

	// Calculates the energy function E as if positions of the
	// specified vertices are exchanged.
	private double calcEnergyIfExchanged(int p, int q) 
	{
		if (p >= q)
			throw new RuntimeException("p should be < q");
		double energy = 0;		// < 0
		for (int i = 0; i < vertices.length - 1; i++) 
		{
			for (int j = i + 1; j < vertices.length; j++) 
			{
				int ii = i;
				int jj = j;
				if (i == p) ii = q;
				if (j == q) jj = p;

                double dist = dm[i][j];
				double l_ij = L * dist;
				double k_ij = K / (dist * dist);
				double dx = xydata[ii].getX() - xydata[jj].getX();
				double dy = xydata[ii].getY() - xydata[jj].getY();
				double d = Math.sqrt(dx * dx + dy * dy);
				
				energy += k_ij / 2 * (dx * dx + dy * dy + l_ij * l_ij -
									  2 * l_ij * d);
			}
		}
		return energy;
	}

}