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

package org.cytoscape.dyn.internal.task.select;

import java.util.Collection;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * <code> SelectUtils </code> is used to select the appropriate nodes/edges.
 * 
 * @author Cytoscape
 *
 */
public class SelectUtils
{

	void setSelectedNodes(final CyNetwork network, final Collection<CyNode> nodes, final boolean select, final double start, final double end)
	{
		setSelected(network,nodes, select, start, end);
	}

	void setSelectedEdges(final CyNetwork network, final Collection<CyEdge> edges, final boolean select, final double start, final double end)
	{
		setSelected(network,edges, select, start, end);
	}

	private void setSelected(final CyNetwork network, final Collection<? extends CyIdentifiable> objects, final boolean select, final double start, final double end)
	{
		for (final CyIdentifiable nodeOrEdge : objects)
				network.getRow(nodeOrEdge).set(CyNetwork.SELECTED, select);
	}
}
