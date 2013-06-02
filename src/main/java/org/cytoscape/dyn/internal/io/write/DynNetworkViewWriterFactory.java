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

package org.cytoscape.dyn.internal.io.write;

import org.cytoscape.dyn.internal.io.event.Sink;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.io.write.CyWriterFactory;
import org.cytoscape.view.presentation.RenderingEngine;

/**
 * <code> DynNetworkViewWriterFactory </code> is interface for a {@link CyWriterFactory} 
 * that allows a dynamic network view rendered by the specified {@link RenderingEngine} to
 * be specified and written.
 * 
 * @author Sabina Sara Pfister
 *
 */
public interface DynNetworkViewWriterFactory<T> extends Sink<T>
{
	/**
     * Process update view event.
     * @param dynNetwork
     * @param currentTime
     * @param iteration
     */
    public void updateView(DynNetwork<T> dynNetwork, double currentTime, int iteration);
    
    /**
     * Dispose current graphics.
     * @param dynNetwork
     * @param currentTime
     */
    public void dispose();

}
