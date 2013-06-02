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

import java.util.ArrayList;

/**
 * <code> Spring </code> represents a spring in a force simulation.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class Spring 
{
    private static SpringFactory s_factory = new SpringFactory();
    
    /** The first ForceItem endpoint */
    public ForceItem item1;
    
    /** The second ForceItem endpoint */
    public ForceItem item2;
    
    /** The spring's resting length */
    public float length;
    
    /** The spring tension co-efficient */
    public float coeff;
    
    /** The spring damping co-efficient */
    public float damp;
    
    /**
     * Retrieve the SpringFactory instance, which serves as an object pool
     * for Spring instances.
     * @return the Spring Factory
     */
    public static SpringFactory getFactory() 
    {
        return s_factory;
    }
    
    /**
     * <code> Spring </code> constructor.
     * @param fi1 the first ForceItem endpoint
     * @param fi2 the second ForceItem endpoint
     * @param k the spring tension co-efficient
     * @param c the spring damping co-efficient
     * @param len the spring's resting length
     */
    public Spring(ForceItem fi1, ForceItem fi2, float k, float c, float len) 
    {
        item1 = fi1;
        item2 = fi2;
        coeff = k;
        damp = c;
        length = len;
    }
    
    /**
     * The SpringFactory is responsible for generating Spring instances
     * and maintaining an object pool of Springs to reduce garbage collection
     * overheads while force simulations are running.
     */
    public static final class SpringFactory 
    {
        private int maxSprings = 10000;
        private ArrayList<Spring> springs = new ArrayList<Spring>();
        
        /**
         * Get a Spring instance and set it to the given parameters.
         * @param f1
         * @param f2
         * @param k
         * @param c
         * @param length
         * @return spring
         */
        public Spring getSpring(ForceItem f1, ForceItem f2, float k, float c, float length) 
        {
            if ( springs.size() > 0 ) {
                Spring s = (Spring)springs.remove(springs.size()-1);
                s.item1 = f1;
                s.item2 = f2;
                s.coeff = k;
                s.damp = c;
                s.length = length;
                return s;
            } else {
                return new Spring(f1,f2,k,c,length);
            }
        }
        
        /**
         * Reclaim a Spring into the object pool.
         * @param s
         */
        public void reclaim(Spring s) 
        {
            s.item1 = null;
            s.item2 = null;
            if ( springs.size() < maxSprings )
                springs.add(s);
        }
    } 

}
