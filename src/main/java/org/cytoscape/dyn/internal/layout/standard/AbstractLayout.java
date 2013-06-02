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
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.cytoscape.dyn.internal.model.snapshot.DynNetworkSnapshot;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * <code> AbstractLayout </code> is an abstract class for implementations of {@link Layout}.
 * It handles some of the basic functions: storing coordinates, maintaining the dimensions, 
 * initializing the locations, maintaining locked vertices.
 * 
 * @author Danyel Fisher, Scott White
 * @author Tom Nelson - converted to jung2
 * @author Sabina Sara Pfister - adaptation for Cytoscape
 *
 * @param <T>
 */
abstract public class AbstractLayout<T> implements Layout<T> 
{
	protected Dimension size;
	protected DynNetworkSnapshot<T> graph;
    protected Map<CyNode, Point2D> locations;
    private Set<CyNode> dontmove = new HashSet<CyNode>();
    
    private Random random = new Random(new Date().getTime());

    /**
     * <code> AbstractLayout </code> constructor.
     * @param graph
     * @param size
     */
	protected AbstractLayout(DynNetworkSnapshot<T> graph, Dimension size)
	{
		this.graph = graph;
		this.size = size;
		this.locations = new HashMap<CyNode, Point2D>();
	}

	@Override
	public void setSize(Dimension size)
	{
		if(size != null && graph != null)
		{
			Dimension oldSize = this.size;
			this.size = size;
			initialize();
			
			if(oldSize != null) 
			{
				adjustLocations(oldSize, size);
			}
		}
	}

	@Override
	public Dimension getSize() 
	{
		return size;
	}

	@Override
	public DynNetworkSnapshot<T> getGraph() 
	{
	    return graph;
	}
	
	@Override
    public boolean isLocked(CyNode node)
    {
        return dontmove.contains(node);
    }

	@Override
	public void lock(CyNode node, boolean lock) 
	{
		if(lock == true) 
		    dontmove.add(node);
		else 
		    dontmove.remove(node);
	}
	
	@Override
	public void lock(boolean lock)
	{
		if(lock == true)
			for(CyNode node : graph.getNodes()) 
				dontmove.add(node);
		else
			for(CyNode node : graph.getNodes()) 
				dontmove.remove(node);
	}

	@Override
	public void run()
	{
		while (!this.done())
			this.step();
	}
	
	@Override
	public double getX(CyNode node) 
	{
		assert this.locations.get(node)!=null : "Cannot getX for an unmapped vertex " + node;
		return this.locations.get(node).getX();
	}

	@Override
	public double getY(CyNode node) 
	{
		assert this.locations.get(node)!=null : "Cannot getY for an unmapped vertex " + node;
		return this.locations.get(node).getY();
	}
	
    protected void updateLocations()
    {
    	for (CyNode node : graph.getNodes())
    	{
    		double gx,gy;
    		int lenght;
    		if (!this.locations.containsKey(node))
    		{
    			gx = 0;
        		gy = 0;
        		lenght = 0;
        		
    			for (CyNode n : graph.getNeighbors(node)) 
    				if (this.locations.containsKey(n))
    				{
    					gx += this.locations.get(n).getX();
    					gy += this.locations.get(n).getY();
    					lenght++;
    				}

    			if (lenght!=0)
    				this.locations.put(node,new Point2D.Double(gx/lenght,gy/lenght));
    			else
    				this.locations.put(node,new Point2D.Double(
        					random.nextDouble() * this.size.width,
        					random.nextDouble() * this.size.height));
    		}
    	}
    }
	
    protected void updateLocationsDirected()
    {
    	for (CyNode node : graph.getNodes())
    	{
    		double gx,gy;
    		int lenght;
    		if (!this.locations.containsKey(node))
    		{
    			gx = 0;
        		gy = 0;
        		lenght = 0;
        		
        		for (CyEdge edge : graph.getInEdges(node))
        		{
        			CyNode n = edge.getSource();
        			if (this.locations.containsKey(n))
        			{
        				gx += this.locations.get(n).getX();
        				gy += this.locations.get(n).getY();
        				lenght++;
        			}
        		}

    			if (lenght!=0)
    			{
    				gx = gx/lenght;
    				gy = gy/lenght;
    				double angle = Math.atan2(gx-size.width/2, gy-size.height/2)+(2*random.nextDouble()-1)/Math.PI;
    				this.locations.put(node,new Point2D.Double(gx+100*Math.cos(angle),gy+100*Math.sin(angle)));
    			}
    			else
    				this.locations.put(node,new Point2D.Double(
        					random.nextDouble() * this.size.width,
        					random.nextDouble() * this.size.height));
    		}
    	}
    }

	protected Point2D getCoordinates(CyNode node) 
	{
        return locations.get(node);
	}
	
	protected Point2D transform(CyNode node) 
	{
		return getCoordinates(node);
	}
	
	protected void offsetVertex(CyNode node, double xOffset, double yOffset) 
	{
		Point2D c = getCoordinates(node);
        c.setLocation(c.getX()+xOffset, c.getY()+yOffset);
		setLocation(node, c);
	}

	private void setLocation(CyNode node, Point2D p)
	{
		Point2D coord = getCoordinates(node);
		coord.setLocation(p);
	}
	
	private void adjustLocations(Dimension oldSize, Dimension size)
	{
		int xOffset = (size.width - oldSize.width) / 2;
		int yOffset = (size.height - oldSize.height) / 2;

		while(true) 
		{
		    try {
                for(CyNode node : getGraph().getNodes()) {
		            offsetVertex(node, xOffset, yOffset);
		        }
		        break;
		    } catch(ConcurrentModificationException cme) {
		    }
		}
	}

}