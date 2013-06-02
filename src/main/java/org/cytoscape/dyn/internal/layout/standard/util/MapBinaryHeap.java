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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Vector;

/**
 * <code> MapBinaryHeap </code> is an array-based binary heap implementation of a priority queue, 
 * which also provides efficient <code>update()</code> and <code>contains</code> operations.
 * 
 * @author Joshua O'Madadhain
 * @author Sabina Sara Pfister - adaptation for Cytoscape
 *
 * @param <T>
 */
public class MapBinaryHeap<T> extends AbstractCollection<T> implements Queue<T>
{
	private Vector<T> heap = new Vector<T>();            
    private Map<T,Integer> object_indices = new HashMap<T,Integer>(); 
    private Comparator<T> comp;
    private final static int TOP = 0;

    /**
     * <code> MapBinaryHeap </code> constructor.
     * @param comp
     */
    public MapBinaryHeap(Comparator<T> comp)
    {
        initialize(comp);
    }
    
    /**
     * <code> MapBinaryHeap </code> constructor.
     */
    public MapBinaryHeap()
    {
        initialize(new ComparableComparator());
    }

    /**
     * <code> MapBinaryHeap </code> constructor.
     * @param c
     */
    public MapBinaryHeap(Collection<T> c)
    {
    	this();
        addAll(c);
    }
    
    /**
     * <code> MapBinaryHeap </code> constructor.
     * @param c
     * @param comp
     */
    public MapBinaryHeap(Collection<T> c, Comparator<T> comp)
    {
        this(comp);
        addAll(c);
    }
    
    private void initialize(Comparator<T> comp)
    {
        this.comp = comp;
        clear();
    }
    
	@Override
	public void clear()
	{
        object_indices.clear();
        heap.clear();
	}

	@Override
	public boolean add(T o)
	{
        int i = heap.size();  // index 1 past the end of the heap
        heap.setSize(i+1);
        percolateUp(i, o);
        return true;
	}

	@Override
	public boolean isEmpty()
	{
        return heap.isEmpty();
	}

	/**
	 * Returns the element at the top of the heap; does not
     * alter the heap.
	 */
	public T peek()
	{
		if (heap.size() > 0)
			return heap.elementAt(TOP);
		else
			return null;
	}

	@Deprecated
    public T pop() throws NoSuchElementException
	{
		return this.remove();
	}

    @Override
    public int size() 
    {
        return heap.size();
    }
       
    /**
     * Informs the heap that this object's internal key value has been
     * updated, and that its place in the heap may need to be shifted
     * (up or down).
     * @param o
     */
    public void update(T o)
    {
        int cur = object_indices.get(o).intValue(); // current index
        int new_idx = percolateUp(cur, o);
        percolateDown(new_idx);
    }

    @Override
    public boolean contains(Object o)
    {
        return object_indices.containsKey(o);
    }
    
    private void percolateDown(int cur)
    {
        int left = lChild(cur);
        int right = rChild(cur);
        int smallest;

        if ((left < heap.size()) && 
        		(comp.compare(heap.elementAt(left), heap.elementAt(cur)) < 0)) {
			smallest = left;
		} else {
			smallest = cur;
		}

        if ((right < heap.size()) && 
        		(comp.compare(heap.elementAt(right), heap.elementAt(smallest)) < 0)) {
			smallest = right;
		}

        if (cur != smallest)
        {
            swap(cur, smallest);
            percolateDown(smallest);
        }
    }

    private int percolateUp(int cur, T o)
    {
        int i = cur;
        
        while ((i > TOP) && (comp.compare(heap.elementAt(parent(i)), o) > 0))
        {
            T parentElt = heap.elementAt(parent(i));
            heap.setElementAt(parentElt, i);
            object_indices.put(parentElt, new Integer(i));
            i = parent(i);
        }
        
        object_indices.put(o, new Integer(i));
        heap.setElementAt(o, i);

        return i;
    }
    
    private int lChild(int i)
    {
    	return (i<<1) + 1;
    }
    
    private int rChild(int i)
    {
    	return (i<<1) + 2;
    }
    
    private int parent(int i)
    {
    	return (i-1)>>1;
    }
    
    private void swap(int i, int j)
    {
        T iElt = heap.elementAt(i);
        T jElt = heap.elementAt(j);

        heap.setElementAt(jElt, i);
        object_indices.put(jElt, new Integer(i));

        heap.setElementAt(iElt, j);
        object_indices.put(iElt, new Integer(j));
    }
    
    private class ComparableComparator implements Comparator<T>
    {
        @SuppressWarnings("unchecked")
        public int compare(T arg0, T arg1)
        {
            if (!(arg0 instanceof Comparable) || !(arg1 instanceof Comparable))
                throw new IllegalArgumentException("Arguments must be Comparable");
            
            return ((Comparable<T>)arg0).compareTo(arg1);
        }
    }

    @Override
    public Iterator<T> iterator()
    {
    	return heap.iterator();
    }

    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

	public T element() throws NoSuchElementException 
	{
		T top = this.peek();
		if (top == null) 
			throw new NoSuchElementException();
		return top;
	}

	public boolean offer(T o) 
	{
		return add(o);
	}

	public T poll() 
	{
        T top = this.peek();
        if (top != null)
        {
	        T bottom_elt = heap.lastElement();
	        heap.setElementAt(bottom_elt, TOP);
	        object_indices.put(bottom_elt, new Integer(TOP));
	        
	        heap.setSize(heap.size() - 1); 
	        if (heap.size() > 1)
	        	percolateDown(TOP);
	
	        object_indices.remove(top);
        }
        return top;
	}

	public T remove() 
	{
		T top = this.poll();
		if (top == null)
			throw new NoSuchElementException();
		return top;
	}

}