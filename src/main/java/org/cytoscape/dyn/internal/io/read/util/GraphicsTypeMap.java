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

package org.cytoscape.dyn.internal.io.read.util;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;

/**
 * <code> GraphicsTypeMap </code> used to convert string graphics types into the 
 * corresponding classes.
 * 
 * @author Sabina Sara Pfister
 *
 */
public class GraphicsTypeMap 
{
	   private Map<String, GraphicsType> typeMap;

	   /**
	    * <code> GraphicsTypeMap </code> constructor.
	    */
	    public GraphicsTypeMap()
	    {
	        typeMap = new HashMap<String, GraphicsType>();

	        for (GraphicsType type : GraphicsType.values())
	            typeMap.put(type.getName(), type);
	    }

	    /**
	     * Get type.
	     * @param name
	     * @return type
	     */
	    public GraphicsType getType(String name)
	    {
	        final GraphicsType type = typeMap.get(name.toUpperCase());
	        
	        if (type != null)
	            return type;
	        else
	            return GraphicsType.NONE;
	    }

	    /**
	     * Get object associated with value.
	     * @param type
	     * @return object
	     */
	    public Object getTypedValue(GraphicsType type)
	    {
	        switch (type) {
	            case RECTANGLE:
	                return NodeShapeVisualProperty.RECTANGLE;
	            case RECT:
	                return NodeShapeVisualProperty.RECTANGLE;
	            case BOX:
	                return NodeShapeVisualProperty.RECTANGLE;
	            case ROUND_RECTANGLE:
	                return NodeShapeVisualProperty.ROUND_RECTANGLE;
	            case ROUND_RECT:
	                return NodeShapeVisualProperty.ROUND_RECTANGLE;
	            case TRIANGLE:
	                return NodeShapeVisualProperty.TRIANGLE;
	            case PARALLELOGRAM:
	                return NodeShapeVisualProperty.PARALLELOGRAM;
	            case RHOMBUS:
	                return NodeShapeVisualProperty.PARALLELOGRAM;
	            case DIAMOND:
	                return NodeShapeVisualProperty.DIAMOND;
	            case ELLIPSE:
	                return NodeShapeVisualProperty.ELLIPSE;
	            case VER_ELLIPSE:
	                return NodeShapeVisualProperty.ELLIPSE;
	            case HOR_ELLIPSE:
	                return NodeShapeVisualProperty.ELLIPSE;
	            case CIRCLE:
	                return NodeShapeVisualProperty.ELLIPSE;
	            case HEXAGON:
	                return NodeShapeVisualProperty.HEXAGON;
	            case OCTAGON:
	                return NodeShapeVisualProperty.OCTAGON;
	        }

	        return null;
	    }
	    
	}
