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

package org.cytoscape.dyn.internal.layout.task;

import java.util.Set;

import javax.swing.JFrame;

import org.cytoscape.dyn.internal.layout.model.DynLayout;
import org.cytoscape.dyn.internal.layout.model.DynLayoutFactory;
import org.cytoscape.dyn.internal.layout.model.DynLayoutManager;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalDouble;
import org.cytoscape.dyn.internal.view.gui.DynCytoPanel;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

/**
 * <code> ForceDirectedDynLayout </code> instantiate the dynamic layout algorithm task 
 * {@link ForceDirectedDynLayoutTask}.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 * @param <C>
 */
public final class ForceDirectedDynLayout<T,C> extends AbstractLayoutAlgorithm
{
	private final DynCytoPanel<T,C> panel;
    private final DynLayoutFactory<T> dynLayoutFactory;
    private final DynNetworkViewManager<T> viewManager;
    private final DynLayoutManager<T> layoutManager;
    
    private double time;
    private DynInterval<T> timeInterval;
    
    private DynLayout<T> layout;
    private ForceDirectedLayoutContext context;
    
    /**
     * <code> ForceDirectedDynLayout </code> constructor.
     * @param computerName
     * @param humanName
     * @param undoSupport
     * @param panel
     * @param dynLayoutFactory
     */
    public ForceDirectedDynLayout(
                    final String computerName, 
                    final String humanName,
                    final UndoSupport undoSupport,
                    final DynCytoPanel<T, C> panel,
                    final DynLayoutFactory<T> dynLayoutFactory,
                    final DynNetworkViewManager<T> viewManager,
                    final DynLayoutManager<T> layoutManager)
    {
            super(computerName, humanName, undoSupport);
            this.panel = panel;
            this.dynLayoutFactory = dynLayoutFactory;
            this.viewManager = viewManager;
            this.layoutManager = layoutManager;
    }

    @Override
    public TaskIterator createTaskIterator(
    		CyNetworkView networkView,
    		Object layoutContext, 
    		Set<View<CyNode>> nodesToLayOut,
    		String layoutAttribute)
    {    	
    	setParameters();
    	
    	if(layoutManager.getDynLayout(networkView)!=null 
    			&& layoutManager.getDynContext(layoutManager.getDynLayout(networkView))!=null
    			&& layoutManager.getDynContext(layoutManager.getDynLayout(networkView)) instanceof ForceDirectedLayoutContext)
    	{
    		layout = layoutManager.getDynLayout(networkView);
    		context = (ForceDirectedLayoutContext) layoutManager.getDynContext(layoutManager.getDynLayout(networkView));
    		layout.removeAllIntervals();
    	}
    	else
    	{
    		context = new ForceDirectedLayoutContext();
    		layout = dynLayoutFactory.createDynLayout(networkView, context);
    	}
    		
    		new ForceDirectedDynLayoutDialog<T>(new JFrame(), viewManager.getDynNetworkView(networkView), context);
    		
    	return new TaskIterator(new ForceDirectedDynLayoutTask<T>(getName(),layout, context,viewManager.getDynNetworkView(networkView), nodesToLayOut, layoutAttribute, undoSupport,timeInterval));
    }
    
    @Override
    public Object createLayoutContext() 
    {
		return new ForceDirectedLayoutContext();
	}
    
//	@Override
//	public Set<Class<?>> getSupportedEdgeAttributeTypes() 
//	{
//		final Set<Class<?>> ret = new HashSet<Class<?>>();
//
//		ret.add(Integer.class);
//		ret.add(Double.class);
//
//		return ret;
//	}
    
	@SuppressWarnings("unchecked")
	private void setParameters()
	{
		this.time = this.panel.getTime();
		if (time>=panel.getMaxTime())
			timeInterval = (DynInterval<T>) new DynIntervalDouble(time-0.0000001, time+0.0000001);
		else
			timeInterval = (DynInterval<T>) new DynIntervalDouble(time, time);

	}
    

}