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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import org.cytoscape.dyn.internal.io.write.AbstractDynNetworkViewWriterFactory;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * <code> PNGWriterFactory </code> extends {@link AbstractDynNetworkViewWriterFactory}. 
 * Is used to create instance of the image writer {@link PNGWriter}.
 * 
 * @author Sabina Sara Pfister
 *
 */
public class PNGWriterFactory<T> extends AbstractDynNetworkViewWriterFactory<T> 
{
	private final RenderingEngine<?> engine;
	private final String fileName;
	
	private final Double width;
	private final Double height;
	private final int heightInPixels;
	private final int widthInPixels;
	private final double zoom;
	
	private final BufferedImage image;
	private final Graphics2D g;
	
//	private DecimalFormat formatter = new DecimalFormat("#0.000");
//	private DecimalFormat formatter2 = new DecimalFormat("#00");
	private DecimalFormat formatter3 = new DecimalFormat("#0000000000");
	
	private int counter;

	/**
	 * <code> PNGWriterFactory </code> constructor.
	 */
	public PNGWriterFactory(
			final File file,
			final RenderingEngine<?> engine) 
	{
		if (engine == null)
			throw new NullPointerException("Rendering Engine is null.");
		
		this.engine = engine;
		this.fileName = trim(file.getAbsolutePath());
		
		width = engine.getViewModel().getVisualProperty(BasicVisualLexicon.NETWORK_WIDTH);
		height = engine.getViewModel().getVisualProperty(BasicVisualLexicon.NETWORK_HEIGHT);
		zoom = 600;
		
		final double scale = zoom / 100.0; 	
		heightInPixels = (int) ((zoom/100) * height);
		widthInPixels = (int) ((zoom/100) * width);
		image = new BufferedImage(widthInPixels, heightInPixels, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		g.setBackground(new Color(255, 255, 255, 0));
		g.scale(scale, scale);
		
		counter = 0;
	}

	@Override
	public void updateView(DynNetwork<T> dynNetwork, double currentTime, int iteration) 
	{

//		File outputFile = new File(trim(file.getAbsolutePath()) + 
//				"_" + Calendar.getInstance().getTimeInMillis() +
//				"_Time_" + formatter.format(currentTime) + ".png");
		
//		File outputFile = new File(trim(file.getAbsolutePath()) + 
//				"_T" + formatter.format(currentTime) + 
//				"_I" + formatter2.format(iteration) + ".png");
		
		File outputFile = new File(fileName +  "_" + formatter3.format(counter) + ".png");

		g.clearRect(0, 0, widthInPixels, heightInPixels);
		
		try {
			(new PNGWriter(engine, new FileOutputStream(outputFile,false))).export(g,image);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		counter = counter + 1;

	}
	
	public void dispose()
	{
		g.dispose();
	}
	
	private String trim(String str)
	{
		if (str.lastIndexOf('.')>0)
			return str.substring(0, str.lastIndexOf('.'));
		else
			return str;
	}

}
