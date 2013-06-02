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

package org.cytoscape.dyn.internal.io.read.xgmml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.cytoscape.dyn.internal.io.read.AbstractDynNetworkReader;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskMonitor;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;

/**
 * <code> XGMMLDynNetworkReader </code> extends {@link AbstractDynNetworkReader}. 
 * It imports and reads static and dynamic XGMML files into {@link DynNetwork} 
 * (dynamic) and {@link CyNetwork} (static) data structures by generating graph
 * events.
 *
 * @author Sabina Sara Pfister
 *
 */
public final class XGMMLDynNetworkReader extends AbstractDynNetworkReader
{
	protected final DefaultHandler parser;

	/**
	 * <code> XGMMLDynNetworkReader </code> constructor.
	 * @param inputStream
	 * @param parser
	 */
	public XGMMLDynNetworkReader(
			final InputStream inputStream,
			final DefaultHandler parser)
	{
		super(inputStream);
		this.parser = parser;
	}

	@Override
	public void run(TaskMonitor tm) throws Exception
	{
		tm.setProgress(0.0);
		try {
			readXGMML(tm);
		} catch (Exception e) {
			throw new IOException("Could not parse XGMML file.", e);
		}
		tm.setProgress(1.0);
	}
	
	protected void readXGMML(TaskMonitor tm) throws SAXException, IOException
	{
		final SAXParserFactory spf = SAXParserFactory.newInstance();

		try {
			SAXParser sp = spf.newSAXParser();
			ParserAdapter pa = new ParserAdapter(sp.getParser());
			pa.setContentHandler(parser);
			pa.setErrorHandler(parser);
			pa.parse(new InputSource(inputStream));
		} catch (OutOfMemoryError oe) {
			System.gc();
			throw new RuntimeException("Out of memory error caught! The network being loaded is too large for the current memory allocation.  Use the -Xmx flag for the java virtual machine to increase the amount of memory available, e.g. java -Xmx1G cytoscape.jar -p apps ....");
		} catch (ParserConfigurationException e) {
			
		} catch (SAXParseException e) {

			throw e;
		} finally {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
		}
	}

}
