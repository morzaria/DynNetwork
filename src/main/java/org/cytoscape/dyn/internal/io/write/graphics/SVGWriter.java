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

package org.cytoscape.dyn.internal.io.write.graphics;

import java.awt.Dimension;
import java.io.OutputStream;

import org.cytoscape.view.presentation.RenderingEngine;
import org.freehep.graphicsio.svg.SVGGraphics2D;

/**
 * <code> SVGWriter </code> is a SVG exporter class.
 * 
 * @author Sabina Sara Pfister
 *
 */
public class SVGWriter
{
	private final RenderingEngine<?> engine;
	private final OutputStream stream;

	/**
	 * <code> SVGWriter </code> constructor.
	 * @param engine
	 * @param stream
	 */
	public SVGWriter(final RenderingEngine<?> engine, final OutputStream stream) 
	{
		if (engine == null)
			throw new NullPointerException("Rendering Engine is null.");
		if (stream == null)
			throw new NullPointerException("Stream is null.");
		
		this.engine = engine;
		this.stream = stream;

	}

	public void export(int width, int height)
	{
		final SVGGraphics2D g = new SVGGraphics2D(stream, new Dimension(width, height));
		java.util.Properties p = new java.util.Properties();
		p.setProperty("org.freehep.graphicsio.AbstractVectorGraphicsIO.TEXT_AS_SHAPES",Boolean.toString(!true));
		g.setProperties(p);
		g.startExport();
		engine.printCanvas(g);
		g.endExport();
	}

}
