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

package org.cytoscape.dyn.internal.view.task;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <code> BlockingQueue </code> implements a simple blocking queue used to 
 * process visualization updates sequentially.
 * 
 * @author Sabina Sara Pfister
 *
 */
public class BlockingQueue
{

	private final Lock lock;

	/**
	 * <code> BlockingQueue </code> constructor.
	 */
    public BlockingQueue()
    {
    	lock = new ReentrantLock(false);
    }

    /**
     * Lock blocking queue.
     */
    public void lock()
    {
    	lock.lock();
    }

    /**
     * Unlock blocking queue.
     */
    public void unlock()
    {
    	lock.unlock();
    }
    
    /**
     * Try to lock to blocking queue.
     */
    public boolean trylock()
    {
    	return lock.tryLock();
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}