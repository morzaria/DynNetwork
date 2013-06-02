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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import org.cytoscape.dyn.internal.io.write.AbstractDynNetworkViewWriterFactory;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * <code> SVGWriterFactory </code> extends {@link AbstractDynNetworkViewWriterFactory}. 
 * Is used to create instance of the image writer {@link SVGWriter}.
 * 
 * @author Sabina Sara Pfister
 *
 */
public class SVGWriterFactory<T> extends AbstractDynNetworkViewWriterFactory<T> 
{
	private final RenderingEngine<?> engine;
	private final String fileName;
	
	private final Double width;
	private final Double height;
	
//	private DecimalFormat formatter = new DecimalFormat("#0.000");
//	private DecimalFormat formatter2 = new DecimalFormat("#00");
	private DecimalFormat formatter3 = new DecimalFormat("#0000000000");
	
	private int counter;

	/**
	 * <code> SVGWriterFactory </code> constructor.
	 * @param engine
	 * @param stream
	 */
	public SVGWriterFactory(
			final File file,
			final RenderingEngine<?> engine) 
	{
		if (engine == null)
			throw new NullPointerException("Rendering Engine is null.");
		
		this.engine = engine;
		this.fileName = trim(file.getAbsolutePath());
		
		width = engine.getViewModel().getVisualProperty(BasicVisualLexicon.NETWORK_WIDTH);
		height = engine.getViewModel().getVisualProperty(BasicVisualLexicon.NETWORK_HEIGHT);
		
		counter = 0;
	}

	@Override
	public void updateView(DynNetwork<T> dynNetwork, double currentTime, int iteration) 
	{
		//		File outputFile = new File(trim(file.getAbsolutePath()) + 
		//		"_" + Calendar.getInstance().getTimeInMillis() +
		//		"_Time_" + formatter.format(currentTime) + ".png");

//		File outputFile = new File(trim(file.getAbsolutePath()) + 
//				"_T" + formatter.format(currentTime) + 
//				"_I" + formatter2.format(iteration) + ".svg");
		
		File outputFile = new File(fileName +  
				"_" + formatter3.format(counter) + ".png");

		try {
			(new SVGWriter(engine, new FileOutputStream(outputFile,false))).export(width.intValue(),height.intValue());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		counter = counter + 1;
	}
	
	public void dispose()
	{
		// Do nothing.
	}
	
	private String trim(String str)
	{
		if (str.lastIndexOf('.')>0)
			return str.substring(0, str.lastIndexOf('.'));
		else
			return str;
	}

}
