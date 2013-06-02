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

package org.cytoscape.dyn.internal.model.snapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * <code> DynNetworkTreeImpl </code> implements all methods to investigate a tree network in
 * time snapshots within the given time interval. It's computationally intensive, and should 
 * be used only when it is not sufficient to search for time intervals only.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public class DynNetworkTreeSnapshotImpl<T> extends DynNetworkSnapshotImpl<T> implements DynNetworkTreeSnapshot<T>
{

	private List<CyNode> roots;
	
	public DynNetworkTreeSnapshotImpl(DynNetworkView<T> view) 
	{
		super(view);
		this.roots = new ArrayList<CyNode>();
	}

	@Override
	public int getChildCount(CyNode node)
	{
		return this.getOutEdges(node).size();
	}

	@Override
	public Collection<CyEdge> getChildEdges(CyNode node) 
	{
		return this.getOutEdges(node);
	}

	@Override
	public Collection<CyNode> getChildren(CyNode node) 
	{
		ArrayList<CyNode> list = new ArrayList<CyNode>();
		for (CyEdge edge : this.getOutEdges(node))
			list.add(edge.getTarget());
		return list;
	}

	@Override
	public CyNode getParent(CyNode node) 
	{
		return this.getInEdges(node).get(0).getSource();
	}

	@Override
	public CyEdge getParentEdge(CyNode node) 
	{
		if (this.getInEdges(node).size()>0)
			return this.getInEdges(node).get(0);
		else
			return null;
	}

	@Override
	public Collection<CyNode> getRoots() 
	{
		return roots;
	}

	@Override
	public int getDepth(CyNode node) 
	{
		return getDepth(node, 0);
	}
	
	private int getDepth(CyNode node, int depth) 
	{
		if (this.getParent(node)!=null)
			return getDepth(this.getParent(node),depth+1);
		else
			return depth;
	}

	@Override
	public int getHeight(CyNode node) 
	{
		return getHeight(getRoot(node), 0);
	}
	
	private int getHeight(CyNode node, int depth) 
	{
		for (CyNode child : this.getChildren(node))
			depth =  Math.max(depth,getDepth(child,depth+1));
		return depth;
	}

	@Override
	public CyNode getRoot(CyNode node) 
	{
		if (this.getParent(node)!=null)
			return getRoot(this.getParent(node));
		else
			return node;
	}
	
	@Override
	protected void addNode(CyNode node)
	{
		if (node!=null)
		{
			this.nodeList.add(node);
			for (CyEdge edge : this.edgeList)
				if (edge.getSource()==node)
					addOutEdge(node, edge);
				else if (edge.getTarget()==node)
					addInEdge(node, edge);
			this.addRoot(node);
		}
	}

	@Override
	protected void removeNode(CyNode node)
	{
		if (node!=null)
		{
			this.inEdges.remove(node);
			this.outEdges.remove(node);
			this.nodeList.remove(node);
			this.removeRoot(node);
		}
	}
	
	@Override
	protected void addEdge(CyEdge edge)
	{
		if (edge!=null)
		{
			this.edgeList.add(edge);
			for (CyNode node : this.nodeList)
			{
				if (edge.getSource()==node)
					addOutEdge(node, edge);
				if (edge.getTarget()==node)
					addInEdge(node, edge);
			}
			this.removeRoot(edge.getTarget());
			this.addRoot(edge.getSource());
		}
	}

	@Override
	protected void removeEdge(CyEdge edge)
	{
		if (edge!=null)
		{
			for (CyNode node : this.inEdges.keySet())
				this.inEdges.get(node).remove(edge);
			for (CyNode node : this.outEdges.keySet())
				this.outEdges.get(node).remove(edge);
			this.edgeList.remove(edge);
			this.addRoot(edge.getSource());
			this.addRoot(edge.getTarget());
		}
	}
	
	private void addRoot(CyNode node)
	{
		if (!this.roots.contains(node) && (!this.inEdges.containsKey(node) || this.inEdges.get(node).size()==0))
		{
			this.roots.add(node);
			for (CyNode child : this.getChildren(node))
				this.roots.remove(child);
		}
	}

	private void removeRoot(CyNode node)
	{
		if (this.roots.contains(node))
		{
			this.roots.remove(node);
			for (CyNode child : this.getChildren(node))
				this.roots.add(child);
		}
	}
	
	@Override
	public void print() 
	{	
		System.out.println("number of roots: " + this.roots.size());
		for (CyNode node : this.roots)
		{
			System.out.println("\n ROOT (in-order)");
			printDown(node);
		}
	}
	
	private void printDown(CyNode node)
	{
		System.out.println("\nLABEL:" + network.getNodeLabel(node) +
				" all-edges:" + this.edgeList.size() +
				" in-edges:" + this.inDegree(node) +
				" out-edges:" + this.outDegree(node));
		for (CyNode child : this.getChildren(node))
		{
			System.out.println("children:");
			printDown(child);
		}
	}

	
}
