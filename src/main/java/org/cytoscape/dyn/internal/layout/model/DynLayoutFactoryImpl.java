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

package org.cytoscape.dyn.internal.layout.model;

import java.util.Stack;

import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * <code> DynLayoutFactoryImpl </code> implements the interface
 * {@link DynLayoutFactory} for creating {@link DynLayout}s.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public class DynLayoutFactoryImpl<T> implements DynLayoutFactory<T> 
{
	private final DynLayoutManager<T> layoutManager;
	private final Stack<NodeDynamicsAttribute<T>> nodeDynamicsList;
	
	/**
	 * <code> DynLayoutFactoryImpl </code> constructor.
	 * @param layoutManager
	 */
	public DynLayoutFactoryImpl(DynLayoutManager<T> layoutManager)
	{
		this.layoutManager = layoutManager;
		this.nodeDynamicsList = new Stack<NodeDynamicsAttribute<T>>();
	}
	
	@Override
	public DynLayout<T> createDynLayout(CyNetworkView networkView)
	{
		DynLayout<T> layout = new DynLayoutImpl<T>(networkView);
		layoutManager.addDynLayout(layout);
		return layout;
	}

	@Override
	public DynLayout<T> createDynLayout(CyNetworkView networkView, Object context) 
	{
		DynLayout<T> layout = new DynLayoutImpl<T>(networkView);
		layoutManager.addDynLayout(layout);
		layoutManager.addDynContext(layout, context);
		return layout;
	}

	@Override
	public void finalizeLayout(DynNetworkView<T> dynNetworkView) 
	{	
		while (!nodeDynamicsList.isEmpty())
			nodeDynamicsList.pop().add(dynNetworkView,layoutManager);
		
		layoutManager.getDynLayout(dynNetworkView.getNetworkView()).finalize();
		initializePositions(dynNetworkView, layoutManager.getDynLayout(dynNetworkView.getNetworkView()));
	}

	@Override
	public void removeLayout(DynNetworkView<T> dynNetworkView) 
	{
		layoutManager.removeDynLayout(dynNetworkView.getNetworkView());
		layoutManager.addDynLayout(new DynLayoutImpl<T>(dynNetworkView.getNetworkView()));
	}
	
	@Override
	public void removeLayout(CyNetworkView networkView) 
	{
		layoutManager.removeDynLayout(networkView);
		layoutManager.addDynLayout(new DynLayoutImpl<T>(networkView));
	}

	@Override
	public void addedNodeDynamics(DynNetwork<T> dynNetwork, CyNode currentNode, String x, String y, String start, String end) 
	{
		this.nodeDynamicsList.push(new NodeDynamicsAttribute<T>(dynNetwork,currentNode,x,y,start,end));
	}
	
	private void initializePositions(DynNetworkView<T> dynView, DynLayout<T> layout)
	{
		for (DynInterval<T> i : layout.getIntervalsX())
		{
			CyNode node = dynView.getNetwork().getNode(i);
			if (node!=null)
				dynView.getNetworkView().getNodeView(node).setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, (Double) i.getOnValue());
		}
			
		for (DynInterval<T> i : layout.getIntervalsY())
		{
			CyNode node = dynView.getNetwork().getNode(i);
			if (node!=null)
				dynView.getNetworkView().getNodeView(node).setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, (Double) i.getOnValue());
		}	
	}

}