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

import java.util.Random;


/**
 * <code> SpringForce </code> is the force function that computes the force acting on ForceItems due to a
 * given Spring.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class SpringForce extends AbstractForce 
{

    private static String[] pnames 
        = new String[] { "SpringCoefficient", "DefaultSpringLength", "DampingCoefficient" };

    private Random rand = null; // deterministic randomness
    
    public static final float DEFAULT_SPRING_COEFF = 1E-4f;
    public static final float DEFAULT_SPRING_DAMPING = 0f;
    public static final float DEFAULT_MAX_SPRING_COEFF = 1E-3f;
    public static final float DEFAULT_MIN_SPRING_COEFF = 1E-5f;
    public static final float DEFAULT_SPRING_LENGTH = 50;
    public static final float DEFAULT_MIN_SPRING_LENGTH = 0;
    public static final float DEFAULT_MAX_SPRING_LENGTH = 200;
    public static final int SPRING_COEFF = 0;
    public static final int SPRING_LENGTH = 1;
    public static final int SPRING_DAMP = 2;
    
    /**
     * <code> SpringForce </code> constructor.
     * @param springCoeff the default spring co-efficient to use. This will
     * be used if the spring's own co-efficient is less than zero.
     * @param defaultLength the default spring length to use. This will
     * be used if the spring's own length is less than zero.
     */
    public SpringForce(float springCoeff, float defaultLength, float dampingCoeff) 
    {
        params = new float[] { springCoeff, defaultLength, dampingCoeff};
        minValues = new float[] 
            { DEFAULT_MIN_SPRING_COEFF, DEFAULT_MIN_SPRING_LENGTH };
        maxValues = new float[] 
            { DEFAULT_MAX_SPRING_COEFF, DEFAULT_MAX_SPRING_LENGTH };
    		rand = new Random(12345678L); // deterministic randomness
    }
    
    /**
     * Constructs a new SpringForce instance with default parameters.
     */
    public SpringForce() 
    {
        this(DEFAULT_SPRING_COEFF, DEFAULT_SPRING_LENGTH, DEFAULT_SPRING_DAMPING);
    }

    /**
     * @see prefuse.util.force.Force#isSpringForce()
     */
    public boolean isSpringForce() 
    {
        return true;
    }
    
    /**
     * @see prefuse.util.force.AbstractForce#getParameterNames()
     */
    protected String[] getParameterNames() 
    {
        return pnames;
    } 
    
    /**
     * Calculates the force vector acting on the items due to the given spring.
     * @param s the Spring for which to compute the force
     * @see prefuse.util.force.Force#getForce(prefuse.util.force.Spring)
     */
    public void getForce(Spring s) 
    {
        ForceItem item1 = s.item1;
        ForceItem item2 = s.item2;
        float length = (s.length < 0 ? params[SPRING_LENGTH] : s.length);
        float dx = item2.location[0]-item1.location[0];
        float dy = item2.location[1]-item1.location[1];
        float r  = (float)Math.sqrt(dx*dx+dy*dy);
        if ( r == 0.0 ) 
        {
        	dx = (rand.nextFloat()-0.5f) / 50.0f;
            dy = (rand.nextFloat()-0.5f) / 50.0f;
            r  = (float)Math.sqrt(dx*dx+dy*dy);
        }
        float d  = r-length;
        float coeff = (s.coeff < 0 ? params[SPRING_COEFF] : s.coeff)*d/r;
        float dvx = item2.velocity[0]-item1.velocity[0];
        float dvy = item2.velocity[1]-item1.velocity[1];
        float damp = (s.damp < 0 ? params[SPRING_DAMP] : s.damp)*d/r;
        item1.force[0] += coeff*dx - damp*dvx;
        item1.force[1] += coeff*dy - damp*dvy;
        item2.force[0] += -coeff*dx + damp*dvx;
        item2.force[1] += -coeff*dy + damp*dvy;
    }
    
} 
