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

package org.cytoscape.dyn.internal.io.read.util;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * <code> KeyPairs </code> implements hash map with two keys to store information
 * about {@link CyNode}s, connecting {@link CyEdge}s, and the respective attributes.
 * Each pair is identified by an id long value and by a String column.
 * 
 * @author Sabina Sara Pfister
 *
 */
public class KeyPairs 
{
	private String column;
	private Long row;
	
	/**
	 * <code> KeyPairs </code> constructor.
	 * @param column
	 * @param row
	 */
	public KeyPairs(String column, Long row) 
	{
		this.column = column;
		this.row = row;
	}
	
	/**
	 * Get column.
	 * @return
	 */
	public String getColumn() 
	{
		return column;
	}

	/**
	 * Get row.
	 * @return
	 */
	public Long getRow() 
	{
		return row;
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result + ((row == null) ? 0 : row.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		KeyPairs o = (KeyPairs) obj;
		if (this.row.equals(o.row) && this.column.equals(o.column))
			return true;
		return false;
	}
	
}