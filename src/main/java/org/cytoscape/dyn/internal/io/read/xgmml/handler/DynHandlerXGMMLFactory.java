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

package org.cytoscape.dyn.internal.io.read.xgmml.handler;

import static org.cytoscape.dyn.internal.io.read.xgmml.ParseDynState.EDGE;
import static org.cytoscape.dyn.internal.io.read.xgmml.ParseDynState.EDGE_ATT;
import static org.cytoscape.dyn.internal.io.read.xgmml.ParseDynState.EDGE_GRAPHICS;
import static org.cytoscape.dyn.internal.io.read.xgmml.ParseDynState.GRAPH;
import static org.cytoscape.dyn.internal.io.read.xgmml.ParseDynState.NET_ATT;
import static org.cytoscape.dyn.internal.io.read.xgmml.ParseDynState.NET_GRAPHICS;
import static org.cytoscape.dyn.internal.io.read.xgmml.ParseDynState.NODE;
import static org.cytoscape.dyn.internal.io.read.xgmml.ParseDynState.NODE_ATT;
import static org.cytoscape.dyn.internal.io.read.xgmml.ParseDynState.NODE_DYNAMICS;
import static org.cytoscape.dyn.internal.io.read.xgmml.ParseDynState.NODE_GRAPHICS;
import static org.cytoscape.dyn.internal.io.read.xgmml.ParseDynState.NONE;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.dyn.internal.io.read.xgmml.ParseDynState;
import org.cytoscape.dyn.internal.layout.model.DynLayoutFactory;
import org.cytoscape.dyn.internal.model.DynNetworkFactory;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewFactory;
import org.cytoscape.dyn.internal.vizmapper.model.DynVizMapFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * <code> DynHandlerXGMMLFactory </code> is the factory for the event Handler. 
 * Implements the finite state machine states and transition rules
 * for start elements and end elements from the SAX Parser.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public final class DynHandlerXGMMLFactory<T>
{
	
	private Map<ParseDynState, Map<String, ParseDynState>> startParseMap;
	
	private DynHandlerXGMML<T> handler;
	
	/**
	 * <code> DynHandlerXGMMLFactory </code> constructor.
	 * @param networkSink
	 * @param viewSink
	 * @param layoutSink
	 */
	public DynHandlerXGMMLFactory(DynNetworkFactory<T> networkSink, DynNetworkViewFactory<T> viewSink, DynLayoutFactory<T> layoutSink, DynVizMapFactory<T> vizMapSink)
	{
		handler = new DynHandlerXGMML<T>(networkSink,viewSink,layoutSink, vizMapSink);
		startParseMap = new HashMap<ParseDynState, Map<String, ParseDynState>>();
		buildMap(createStartParseTable(), startParseMap);
	}
	
	/**
	 * Handle start state.
	 * @param current
	 * @param tag
	 * @param atts
	 * @return
	 * @throws SAXException
	 */
	public ParseDynState handleStartState(ParseDynState current, String tag, Attributes atts) throws SAXException
	{
		current = startParseMap.get(current).get(tag);
		handler.handleStart(atts, current);
		return current;
	}
	
	/**
	 * Handle end state.
	 * @param current
	 * @param tag
	 * @param atts
	 * @return
	 * @throws SAXException
	 */
	public ParseDynState handleEndState(ParseDynState current, String tag, Attributes atts) throws SAXException
	{
		handler.handleEnd(atts, current);
		return current;
	}

	private Object[][] createStartParseTable()
	{

		final Object[][] tbl =
		{
				
				// Handle graphs
				{ NONE, "graph", GRAPH, null },
				{ GRAPH, "att", NET_ATT, null },
				{ GRAPH, "node", NODE, null },
				{ GRAPH, "edge", EDGE, null },
				{ GRAPH, "graphics", NET_GRAPHICS, null },
				{ NET_GRAPHICS, "att", NET_GRAPHICS, null },
				
				// Handle nodes
				{ NODE, "att", NODE_ATT, null },
				{ NODE, "graphics", NODE_GRAPHICS, null },
				{ NODE, "layout", NODE_DYNAMICS, null },
				{ NODE_GRAPHICS, "att", NODE_GRAPHICS, null },
				{ NODE_DYNAMICS, "att", NODE_DYNAMICS, null },
				
				// Handle edges
				{ EDGE, "att", EDGE_ATT, null },
				{ EDGE, "graphics", EDGE_GRAPHICS, null },
				{ EDGE_GRAPHICS, "att", EDGE_GRAPHICS, null }};
		
		return tbl;
	}

	private void buildMap(Object[][] table, Map<ParseDynState, Map<String, ParseDynState>> map)
	{
		int size = table.length;
		Map<String, ParseDynState> internalMap = null;

		for (int i = 0; i < size; i++)
		{
			internalMap = map.get((ParseDynState) table[i][0]);
			if (internalMap == null)
			{
				internalMap = new HashMap<String, ParseDynState>();
			}
			internalMap.put((String) table[i][1], (ParseDynState) table[i][2]);
			map.put((ParseDynState) table[i][0], internalMap);	
		}

	}
}
