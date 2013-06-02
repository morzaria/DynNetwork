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
import java.net.URI;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.util.StreamUtil;

/**
 * <code> ParseDynState </code> is used to filter XGMML file extensions.
 * 
 * @author cytoscape
 *
 */
public final class XGMMLDynFileFilter extends BasicCyFileFilter 
{
	
	public static final Pattern XGMML_HEADER_PATTERN = Pattern
			.compile("<graph[^<>]+[\\'\"]http://www.cs.rpi.edu/XGMML[\\'\"][^<>]*>");
	
	public static final Pattern XGMML_VIEW_ATTRIBUTE_PATTERN = Pattern.compile("cy:view=[\\'\"](1|true)[\\'\"]");

	/**
	 * <code> XGMMLDynFileFilter </code> constructor.
	 * @param extensions
	 * @param contentTypes
	 * @param description
	 * @param category
	 * @param streamUtil
	 */
	public XGMMLDynFileFilter(Set<String> extensions, Set<String> contentTypes,
			String description, DataCategory category, StreamUtil streamUtil) {
		super(extensions, contentTypes, description, category, streamUtil);
	}

	/**
	 * <code> XGMMLDynFileFilter </code> constructor.
	 * @param extensions
	 * @param contentTypes
	 * @param description
	 * @param category
	 * @param streamUtil
	 */
	public XGMMLDynFileFilter(String[] extensions, String[] contentTypes,
			String description, DataCategory category, StreamUtil streamUtil) 
	{
		super(extensions, contentTypes, description, category, streamUtil);
	}

	@Override
	public boolean accepts(InputStream stream, DataCategory category) 
	{
		if (category != this.category)
			return false;
		
		final String header = this.getHeader(stream, 20);
		Matcher matcher = XGMML_HEADER_PATTERN.matcher(header);
		
		if (matcher.find()) 
		{
			final String graph = matcher.group(0);
			matcher = XGMML_VIEW_ATTRIBUTE_PATTERN.matcher(graph);
			
			if (!matcher.find())
				return true;
		}
		
		return false;
	}

	@Override
	public boolean accepts(URI uri, DataCategory category) 
	{
		try {
			return accepts(uri.toURL().openStream(), category);
		} catch (IOException e) {
			return false;
		}
	}
}
