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
 * 
 * The code below was adapted from the JUNG Project.
 * 
 * *********************************************************************** 
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 * ***********************************************************************
 */

package org.cytoscape.dyn.internal.layout.standard.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <code> BasicMapEntry </code> is a simple minimal implementation of <code>Map.Entry</code>.
 * 
 * @author Sabina Sara Pfister - adaptation for Cytoscape
 *
 * @param <K>
 * @param <V>
 */
public class BasicMapEntry<K,V> implements Map.Entry<K,V> 
{
    final K key;
    V value;
    
    /**
     * <code> BasicMapEntry </code> constructor.
     * @param k
     * @param v
     */
    public BasicMapEntry(K k, V v) 
    {
        value = v;
        key = k;
    }

    public K getKey() 
    {
        return key;
    }

    public V getValue() 
    {
        return value;
    }

    public V setValue(V newValue) 
    {
    V oldValue = value;
        value = newValue;
        return oldValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) 
    {
        if (!(o instanceof Map.Entry))
            return false;
        Map.Entry e = (Map.Entry)o;
        Object k1 = getKey();
        Object k2 = e.getKey();
        if (k1 == k2 || (k1 != null && k1.equals(k2))) 
        {
            Object v1 = getValue();
            Object v2 = e.getValue();
            if (v1 == v2 || (v1 != null && v1.equals(v2))) 
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() 
    {
        return (key==null ? 0 : key.hashCode()) ^
               (value==null   ? 0 : value.hashCode());
    }

    @Override
    public String toString() 
    {
        return getKey() + "=" + getValue();
    }

    /**
     * This method is invoked whenever the value in an entry is
     * overwritten by an invocation of put(k,v) for a key k that's already
     * in the HashMap.
     */
    void recordAccess(HashMap<K,V> m) 
    {
    }

    /**
     * This method is invoked whenever the entry is
     * removed from the table.
     */
    void recordRemoval(HashMap<K,V> m) 
    {
    }
}