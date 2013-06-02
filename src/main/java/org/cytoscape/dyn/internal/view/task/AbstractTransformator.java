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

import org.cytoscape.dyn.internal.io.event.Sink;
import org.cytoscape.dyn.internal.io.event.Source;
import org.cytoscape.dyn.internal.io.write.DynNetworkViewWriterFactory;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalInteger;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.events.UpdateNetworkPresentationEvent;
import org.cytoscape.view.model.events.UpdateNetworkPresentationListener;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * <code> AbstractTransformator </code> is an abstract class to change visual properties by interpolating
 * the values to change.
 * 
 * @author Sabina Sara Pfister
 *
 */
public abstract class AbstractTransformator<T> implements Source<T>, UpdateNetworkPresentationListener
{

	protected DynNetworkViewWriterFactory<T> writerFactory;
	
	protected double alpha;
	protected int iterations;
	protected int delay;
	
	protected double timeStart;
	protected double timeEnd;
	
	private CyNode node;
	private CyEdge edge;
	
	protected Object signal = new Object();
	protected volatile boolean shouldWait = true;
	
	private int value;
	
	@Override
	public void addSink(Sink<T> sink) 
	{
		if (sink instanceof DynNetworkViewWriterFactory<?>)
			this.writerFactory = (DynNetworkViewWriterFactory<T>) sink;
	}
	
	@Override
	public void removeSink(Sink<T> sink) 
	{
		if (writerFactory!=null)
		{
			this.writerFactory.dispose();
			this.writerFactory = null;
		}
	}

	@Override
	public void handleEvent(UpdateNetworkPresentationEvent e) 
	{
		synchronized(signal) 
		{
			shouldWait = false;
			signal.notify();
		}
	}

	protected final void updateNodeTransparency(DynNetwork<T> dynNet, DynNetworkView<T> view, List<DynInterval<T>> intervalList, int offCounter, int onCounter)
	{
		for (DynInterval<T> interval : intervalList)
		{
			node = dynNet.getNode(interval);
			if (interval.isOn())
			{
				value = (int)((offCounter/255.0)*view.getNodeDummyValue(node));
				view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_TRANSPARENCY,value);
				view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_BORDER_TRANSPARENCY,value);
				view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_LABEL_TRANSPARENCY,value);
			}
			else
			{
				value = (int)((onCounter/255.0)*view.getNodeDummyValue(node));
				view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_TRANSPARENCY,value);
				view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_BORDER_TRANSPARENCY,value);
				view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_LABEL_TRANSPARENCY,value);
			}
		}
	}

	protected final void updateEdgeTransparency(DynNetwork<T> dynNet, DynNetworkView<T> view, List<DynInterval<T>> intervalList, int offCounter, int onCounter)
	{
		for (DynInterval<T> interval : intervalList)
		{
			edge = dynNet.getEdge(interval);
			if (interval.isOn())
			{
				value = (int)((offCounter/255.0)*view.getEdgeDummyValue(edge));
				view.getNetworkView().getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_TRANSPARENCY,value);
				view.getNetworkView().getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_LABEL_TRANSPARENCY,value);
			}
			else
			{
				value = (int)((onCounter/255.0)*view.getEdgeDummyValue(edge));
				view.getNetworkView().getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_TRANSPARENCY,value);
				view.getNetworkView().getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_LABEL_TRANSPARENCY,value);
			}
		}
	}

	protected final void updateVisualProperty(DynNetworkView<T> view, VisualProperty<T> vp, DynInterval<T> interval)
	{
		view.getNetworkView().setVisualProperty(vp,
				interval.interpolateValue(view.getNetworkView().getVisualProperty(vp), this.alpha));
	}

	protected final void updateVisualProperty(DynNetworkView<T> view, CyNode node, VisualProperty<T> vp, DynInterval<T> interval)
	{
		view.getNetworkView().getNodeView(node).setVisualProperty(vp,
				interval.interpolateValue(view.getNetworkView().getNodeView(node).getVisualProperty(vp), this.alpha));
	}

	protected final void updateVisualProperty(DynNetworkView<T> view, CyEdge edge, VisualProperty<T> vp, DynInterval<T> interval)
	{
		view.getNetworkView().getEdgeView(edge).setVisualProperty(vp,
				interval.interpolateValue(view.getNetworkView().getEdgeView(edge).getVisualProperty(vp), this.alpha));
	}
	
	protected final void updateNodeTransparency(DynNetwork<T> dynNet, DynNetworkView<T> view, List<DynInterval<T>> intervalList)
	{
		for (DynInterval<T> interval : intervalList)
			if (interval.isOn())
			{
				node = dynNet.getNode(interval);
				view.setNodeDummyValue(node,((DynIntervalInteger)interval).interpolateValue(view.getNodeDummyValue(node), this.alpha));
				view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_TRANSPARENCY,view.getNodeDummyValue(node));
				view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_BORDER_TRANSPARENCY,view.getNodeDummyValue(node));
				view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_LABEL_TRANSPARENCY,view.getNodeDummyValue(node));
			}
	}

	protected final void updateEdgeTransparency(DynNetwork<T> dynNet, DynNetworkView<T> view, List<DynInterval<T>> intervalList)
	{
		for (DynInterval<T> interval : intervalList)
			if (interval.isOn())
			{
				edge = dynNet.getEdge(interval);
				view.setEdgeDummyValue(edge,((DynIntervalInteger)interval).interpolateValue(view.getEdgeDummyValue(edge), this.alpha));
				view.getNetworkView().getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_TRANSPARENCY,view.getEdgeDummyValue(edge));
				view.getNetworkView().getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_LABEL_TRANSPARENCY,view.getEdgeDummyValue(edge));
			}
	}
	
	protected final void updateVisualPropertyFinal(DynNetworkView<T> view, VisualProperty<T> vp, DynInterval<T> interval)
	{
		view.getNetworkView().setVisualProperty(vp,
				interval.getOnValue());
	}

	protected final void updateVisualPropertyFinal(DynNetworkView<T> view, CyNode node, VisualProperty<T> vp, DynInterval<T> interval)
	{
		view.getNetworkView().getNodeView(node).setVisualProperty(vp,
				interval.getOnValue());
	}

	protected final void updateVisualPropertyFinal(DynNetworkView<T> view, CyEdge edge, VisualProperty<T> vp, DynInterval<T> interval)
	{
		view.getNetworkView().getEdgeView(edge).setVisualProperty(vp,
				interval.getOnValue());
	}
	
	protected final void updateNodeTransparencyFinal(DynNetwork<T> dynNet, DynNetworkView<T> view, List<DynInterval<T>> intervalList)
	{
		for (DynInterval<T> interval : intervalList)
			if (interval.isOn())
			{
				node = dynNet.getNode(interval);
				view.setNodeDummyValue(node,((DynIntervalInteger)interval).getOnValue());
				view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_TRANSPARENCY,view.getNodeDummyValue(node));
				view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_BORDER_TRANSPARENCY,view.getNodeDummyValue(node));
				view.getNetworkView().getNodeView(node).setLockedValue(BasicVisualLexicon.NODE_LABEL_TRANSPARENCY,view.getNodeDummyValue(node));
			}
	}

	protected final void updateEdgeTransparencyFinal(DynNetwork<T> dynNet, DynNetworkView<T> view, List<DynInterval<T>> intervalList)
	{
		for (DynInterval<T> interval : intervalList)
			if (interval.isOn())
			{
				edge = dynNet.getEdge(interval);
				view.setEdgeDummyValue(edge,((DynIntervalInteger)interval).getOnValue());
				view.getNetworkView().getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_TRANSPARENCY,view.getEdgeDummyValue(edge));
				view.getNetworkView().getEdgeView(edge).setLockedValue(BasicVisualLexicon.EDGE_LABEL_TRANSPARENCY,view.getEdgeDummyValue(edge));
			}
	}

	protected void setSmoothness(int smoothness, double deltat)
	{
		if (smoothness==0)
		{
			this.iterations = 1;
			this.delay = 0;
			this.alpha = 1;
		}
		else
		{
			this.iterations = (int) (smoothness*25/1000);
			this.delay = (int) (smoothness/iterations);
			
			switch(smoothness)
			{
			case 250:
				this.alpha = 0.5;
				break;
			case 500:
				this.alpha = 0.35;
				break;
			case 750:
				this.alpha = 0.2;
				break;
			case 1000:
				this.alpha = 0.15;
				break;
			case 2000:
				this.alpha = 0.1;
				break;
			case 3000:
				this.alpha = 0.05;
				break;
			case 4000:
				this.alpha = 0.03;
				break;
			}

		}
		
		timeStart = 0;
		timeEnd = delay;
	}
	
	protected int round(double value)
	{
		return (int) value;
	}
}
