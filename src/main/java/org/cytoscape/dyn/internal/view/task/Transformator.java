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

package org.cytoscape.dyn.internal.view.task;

import java.util.List;

import org.cytoscape.dyn.internal.layout.model.DynLayout;
import org.cytoscape.dyn.internal.layout.model.DynLayoutManager;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.dyn.internal.vizmapper.model.DynVizMap;
import org.cytoscape.dyn.internal.vizmapper.model.DynVizMapManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * <code> Transformator </code> is used to change visual properties by interpolating
 * the values to change.
 * 
 * @author Sabina Sara Pfister
 *
 */
@SuppressWarnings("unchecked")
public class Transformator<T> extends AbstractTransformator<T>
{
	private final DynLayoutManager<T> layoutManager;
	private final DynVizMapManager<T> vizMapManager;
	
	private int onCounter;
	private int offCounter;
	
	private VisualProperty<T> node_x_pos = (VisualProperty<T>) BasicVisualLexicon.NODE_X_LOCATION;
	private VisualProperty<T> node_y_pos = (VisualProperty<T>) BasicVisualLexicon.NODE_Y_LOCATION;
	
	/**
	 * <code> Transformator </code> constructor.
	 * @param layoutManager
	 * @param vizMapManager
	 */
	public Transformator(
			final DynLayoutManager<T> layoutManager,
			final DynVizMapManager<T> vizMapManager) 
	{
		this.layoutManager = layoutManager;
		this.vizMapManager = vizMapManager;
	}

	/**
	 * Run transformation on given interval lists.
	 * @param dynNetwork
	 * @param view
	 * @param timeInterval
	 * @param visibility
	 * @param smoothness
	 * @param deltat
	 */
	public void run(
			final DynNetwork<T> dynNetwork,
			final DynNetworkView<T> view,
			final DynInterval<T> timeInterval,
			final int visibility,
			final int smoothness,
			final double deltat)
	{	
		setSmoothness(smoothness,deltat);
		
		this.onCounter = 255;
		this.offCounter = visibility;

		List<DynInterval<T>> nodes = view.searchChangedNodes(timeInterval);
		List<DynInterval<T>> edges = view.searchChangedEdges(timeInterval);

		DynLayout<T> layout = layoutManager.getDynLayout(view.getNetworkView());
		List<DynInterval<T>> nodesPosX = layout.searchChangedNodePositionsX(timeInterval);
		List<DynInterval<T>> nodesPosY = layout.searchChangedNodePositionsY(timeInterval);

		DynVizMap<T> vizMap = vizMapManager.getDynVizMap(view.getNetworkView());
		List<DynInterval<T>> graphVizMap = vizMap.searchChangedGraphGraphics(timeInterval);
		List<DynInterval<T>> nodesVizMap = vizMap.searchChangedNodeGraphics(timeInterval);
		List<DynInterval<T>> edgesVizMap = vizMap.searchChangedEdgeGraphics(timeInterval);
		List<DynInterval<T>> nodesTrasnparencyVizMap = vizMap.searchChangedNodeTransparencyGraphics(timeInterval);
		List<DynInterval<T>> edgesTrasnparencyVizMap = vizMap.searchChangedEdgeTransparencyGraphics(timeInterval);

		for (int i=0;i<iterations;i++)
		{
			
			timeStart = System.currentTimeMillis();

			if (i<iterations-1)
			{

				// Set transparency
				onCounter = (int) ((1-alpha)*onCounter+alpha*visibility);
				offCounter = (int) ((1-alpha)*offCounter+alpha*255);
				updateNodeTransparency(dynNetwork,view,nodesTrasnparencyVizMap);
				updateEdgeTransparency(dynNetwork,view,edgesTrasnparencyVizMap);
				updateNodeTransparency(dynNetwork,view,nodes,offCounter,onCounter);
				updateEdgeTransparency(dynNetwork,view,edges,offCounter,onCounter);

				// Set other graphical attributes
				for (DynInterval<T> interval : nodesPosX)
					if (interval.isOn())
						updateVisualProperty(view,dynNetwork.getNode(interval),node_x_pos,interval);

				for (DynInterval<T> interval : nodesPosY)
					if (interval.isOn())
						updateVisualProperty(view,dynNetwork.getNode(interval),node_y_pos,interval);

				for (DynInterval<T> interval : graphVizMap)
					if (interval.isOn())
						updateVisualProperty(view,vizMap.getVisualProperty(interval.getAttribute()),interval);

				for (DynInterval<T> interval : nodesVizMap)
					if (interval.isOn())
						updateVisualProperty(view,dynNetwork.getNode(interval),vizMap.getVisualProperty(interval.getAttribute()),interval);

				for (DynInterval<T> interval : edgesVizMap)
					if (interval.isOn())
						updateVisualProperty(view,dynNetwork.getEdge(interval),vizMap.getVisualProperty(interval.getAttribute()),interval);

			}
			else
			{
				// Set transparency
				updateNodeTransparencyFinal(dynNetwork,view,nodesTrasnparencyVizMap);
				updateEdgeTransparencyFinal(dynNetwork,view,edgesTrasnparencyVizMap);
				updateNodeTransparency(dynNetwork,view,nodes,255,visibility);
				updateEdgeTransparency(dynNetwork,view,edges,255,visibility);

				// Set other graphical attributes
				for (DynInterval<T> interval : nodesPosX)
					if (interval.isOn())
						updateVisualProperty(view,dynNetwork.getNode(interval),node_x_pos,interval);

				for (DynInterval<T> interval : nodesPosY)
					if (interval.isOn())
						updateVisualProperty(view,dynNetwork.getNode(interval),node_y_pos,interval);

				for (DynInterval<T> interval : graphVizMap)
					if (interval.isOn())
						updateVisualPropertyFinal(view,vizMap.getVisualProperty(interval.getAttribute()),interval);

				for (DynInterval<T> interval : nodesVizMap)
					if (interval.isOn())
						updateVisualPropertyFinal(view,dynNetwork.getNode(interval),vizMap.getVisualProperty(interval.getAttribute()),interval);

				for (DynInterval<T> interval : edgesVizMap)
					if (interval.isOn())
						updateVisualPropertyFinal(view,dynNetwork.getEdge(interval),vizMap.getVisualProperty(interval.getAttribute()),interval);
			}

			view.updateView();
			synchronized(signal) 
			{
				while(shouldWait)
				{
					try {
						signal.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}  
				}
			}
			
			timeEnd = System.currentTimeMillis();
			if (writerFactory==null && round(timeEnd-timeStart)<delay)
			{
				try {
					Thread.sleep(delay-round(timeEnd-timeStart));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if (writerFactory!=null)
				writerFactory.updateView(dynNetwork,timeInterval.getStart(),i);
			
			
		}
	}

	/**
	 * Initialize visualization.
	 * @param view
	 * @param visibility
	 * @param timeInterval
	 */
	public void initialize(
			final DynNetworkView<T> view,
			final DynInterval<T> timeInterval,
			final int visibility)
	{
		DynVizMap<T> vizMap = vizMapManager.getDynVizMap(view.getNetworkView());
		
		for (final View<CyNode> nodeView : view.getNetworkView().getNodeViews())
		{
			nodeView.setLockedValue(BasicVisualLexicon.NODE_TRANSPARENCY, visibility);
			nodeView.setLockedValue(BasicVisualLexicon.NODE_BORDER_TRANSPARENCY, visibility);
			nodeView.setLockedValue(BasicVisualLexicon.NODE_LABEL_TRANSPARENCY, visibility);
			if (vizMap.contrainsTransparentNode(nodeView.getModel()))
				view.setNodeDummyValue(nodeView.getModel(),visibility);
			else
				view.setNodeDummyValue(nodeView.getModel(),255);
		}

		for (final View<CyEdge> edgeView : view.getNetworkView().getEdgeViews())
		{
			edgeView.setLockedValue(BasicVisualLexicon.EDGE_TRANSPARENCY, visibility);
			edgeView.setLockedValue(BasicVisualLexicon.EDGE_LABEL_TRANSPARENCY, visibility);
			if (vizMap.contrainsTransparentEdge(edgeView.getModel()))
				view.setEdgeDummyValue(edgeView.getModel(),visibility);
			else
				view.setEdgeDummyValue(edgeView.getModel(),255);
		}
		
		layoutManager.getDynLayout(view.getNetworkView()).initNodePositions(timeInterval);
		view.getNetworkView().fitContent();
	}



}
