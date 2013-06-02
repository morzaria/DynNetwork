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

package org.cytoscape.dyn.internal.io.read.xgmml;

/**
 * <code> ParseDynState </code> is an enumeration class used in the finite state machine
 * to parse trough the XGMML file and execute the appropriate actions.
 * 
 * @author Sabina Sara Pfister
 *
 */
public enum ParseDynState 
{
	
	// Graph elements
    NONE("none"),
    GRAPH("Graph Element"),
    NODE("Node Element"),
    EDGE("Edge Element"),
    
    // Graph attributes
    NET_ATT("Network Attribute"),
    NODE_ATT("Node Attribute"),
    EDGE_ATT("Edge Attribute"),

    // Graphical attribute
    NET_GRAPHICS("Network Graphics"),
    NODE_GRAPHICS("Node Graphics"),
    EDGE_GRAPHICS("Edge Graphics"),
    LOCKED_VISUAL_PROP_ATT("Bypass Attribute"),
    
    // Dynamic attribute
    NODE_DYNAMICS("Node Dynamics"),
    
    // Others (not implemented yet)
    EDGE_BEND("Edge Bend"),
    EDGE_HANDLE("Edge Handle"),
    EDGE_HANDLE_ATT("Edge Handle Attribute"),
    RDF("RDF"),
    RDF_DESC("RDF Description"),
    ANY("any");

    private String name;

    private ParseDynState(String str) 
    {
        name = str;
    }

    public String toString() 
    {
        return name;
    }
}
