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
 */

package org.cytoscape.dyn.internal.layout.standard.force;

import java.util.Iterator;


/**
 * <code> EulerIntegrator </code> updates velocity and position data using Euler's Method. This is the
 * simplest and fastest method, but is somewhat inaccurate and less smooth
 * than more costly approaches.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class EulerIntegrator implements Integrator 
{
    
	/**
	 * @see prefuse.util.force.Integrator#integrate(prefuse.util.force.ForceSimulator, long)
	 */
	public void integrate(ForceSimulator sim, long timestep) 
	{
		float speedLimit = sim.getSpeedLimit();
		Iterator<ForceItem> iter = sim.getItems();
		while ( iter.hasNext() ) {
			ForceItem item = (ForceItem)iter.next();
			item.location[0] += timestep * item.velocity[0];
			item.location[1] += timestep * item.velocity[1];
			float coeff = timestep / item.mass;
			item.velocity[0] += coeff * item.force[0];
			item.velocity[1] += coeff * item.force[1];
			float vx = item.velocity[0];
			float vy = item.velocity[1];
			float v = (float)Math.sqrt(vx*vx+vy*vy);
			if ( v > speedLimit ) {
				item.velocity[0] = speedLimit * vx / v;
				item.velocity[1] = speedLimit * vy / v;
			}
		}
	}

} 
