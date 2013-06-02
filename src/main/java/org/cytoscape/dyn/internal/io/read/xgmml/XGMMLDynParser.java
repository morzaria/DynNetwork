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

import java.util.Stack;

import org.cytoscape.dyn.internal.io.read.xgmml.handler.DynHandlerXGMMLFactory;
import org.cytoscape.dyn.internal.layout.model.DynLayoutFactory;
import org.cytoscape.dyn.internal.model.DynNetworkFactory;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewFactory;
import org.cytoscape.dyn.internal.vizmapper.model.DynVizMapFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <code> XGMMLDynParser </code> is used to parse XGMML files.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public final class XGMMLDynParser<T> extends DefaultHandler
{
	private final DynHandlerXGMMLFactory<T> handler;
	
	private ParseDynState parseState;
	private Stack<ParseDynState> startStack;

	/**
	 * <code> XGMMLDynParser </code> constructor.
	 * @param networkSink
	 * @param viewSink
	 * @param layoutSink
	 */
	public XGMMLDynParser(DynNetworkFactory<T> networkSink, DynNetworkViewFactory<T> viewSink, DynLayoutFactory<T> layoutSink, DynVizMapFactory<T> vizMapSink)
	{
		this.handler = new DynHandlerXGMMLFactory<T>(networkSink,viewSink,layoutSink,vizMapSink);
	}

	@Override
	public void startDocument() throws SAXException
	{
		startStack = new Stack<ParseDynState>();
		parseState = ParseDynState.NONE;
		super.startDocument();
	}

	@Override
	public void startElement(String namespace, String localName, String qName, Attributes atts) throws SAXException
	{
		final ParseDynState nextState = handler.handleStartState(parseState, localName, atts);
		startStack.push(parseState);
		parseState = nextState;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		handler.handleEndState(parseState, localName, null);
		parseState = startStack.pop();
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException
	{
		String err = "Fatal parsing error on line " + e.getLineNumber() + " -- '" + e.getMessage() + "'";
		throw new SAXException(err);
	}

}
