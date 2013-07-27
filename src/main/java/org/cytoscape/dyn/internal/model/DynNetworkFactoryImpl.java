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

package org.cytoscape.dyn.internal.model;

import org.cytoscape.dyn.internal.io.read.util.AttributeTypeMap;
import org.cytoscape.dyn.internal.model.tree.AbstractIntervalCheck;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.session.CyNetworkNaming;

/**
 * <code> DynNetworkFactoryImpl </code> implements the interface
 * {@link DynNetworkFactory} for creating {@link DynNetwork}s.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public final class DynNetworkFactoryImpl<T> extends AbstractIntervalCheck<T> implements DynNetworkFactory<T>
{
	private final CyNetworkFactory networkFactory;
	private final CyRootNetworkManager rootNetworkManager;
	private final DynNetworkManager<T> manager;
	private final CyNetworkNaming nameUtil;
	
	private final AttributeTypeMap typeMap;
	
	/**
	 * <code> DynNetworkFactoryImpl </code> constructor.
	 * @param networkFactory
	 * @param rootNetworkManager
	 * @param groupManager
	 * @param groupFactory
	 * @param manager
	 * @param nameUtil
	 */
	public DynNetworkFactoryImpl(
			final CyNetworkFactory networkFactory,
			final CyRootNetworkManager rootNetworkManager,
			final DynNetworkManager<T> manager,
			final CyNetworkNaming nameUtil)
	{
		this.networkFactory = networkFactory;
		this.rootNetworkManager = rootNetworkManager;
		this.manager = manager;
		this.nameUtil = nameUtil;

		this.typeMap = new AttributeTypeMap();
	}

	@Override
	public DynNetwork<T> addedGraph(String id, String label, String start, String end, String directed)
	{
		DynNetwork<T> dynNetwork = createGraph(directed, id, label, start, end);
		setElement(dynNetwork, id, label, null, start, end);
		manager.addDynNetwork(dynNetwork);
		return dynNetwork;
	}

	@Override
	public CyNode addedNode(DynNetwork<T> dynNetwork, String id, String label, String start, String end)
	{
		CyNode currentNode = createNode(dynNetwork, id, label, start, end);
		setElement(dynNetwork, currentNode, id, label, null, start, end);
		return currentNode;
	}
	
	@Override
	public CyEdge addedEdge(DynNetwork<T> dynNetwork, String id, String label, String source, String target, String start, String end)
	{
		if(dynNetwork.containsCyNode(source) && dynNetwork.containsCyNode(target))
		{
			CyEdge currentEdge = createEdge(dynNetwork, id, label, source, target, start, end);
			setElement(dynNetwork, currentEdge, id, label, null, start, end);
			return currentEdge;
		}
		else
			return null;
	}
	
	@Override
	public void addedGraphAttribute(DynNetwork<T> dynNetwork, String attName, String attValue, String attType, String start, String end)
	{
		setAttributes(dynNetwork, attName, attValue, attType, start, end);
	}
	
	@Override
	public void addedNodeAttribute(DynNetwork<T> dynNetwork, CyNode currentNode, String attName, String attValue, String attType, String start, String end)
	{
		setAttributes(dynNetwork, currentNode, attName, attValue, attType, start, end);
	}
	
	@Override
	public void addedEdgeAttribute(DynNetwork<T> dynNetwork, CyEdge currentEdge, String attName, String attValue, String attType, String start, String end)
	{
		setAttributes(dynNetwork, currentEdge, attName, attValue, attType, start, end);
	}

	@SuppressWarnings("unchecked")
	private void setElement(DynNetwork<T> dynNetwork, String id, String label, String value, String start, String end)
	{
		dynNetwork.getNetwork().getRow(dynNetwork.getNetwork()).set(CyNetwork.NAME, nameUtil.getSuggestedNetworkTitle(label));
		DynInterval<T> interval = getInterval(label,(T)label,start,end);
		dynNetwork.insertGraph(CyNetwork.NAME, interval);
	}
	
	@SuppressWarnings("unchecked")
	private void setElement(DynNetwork<T> dynNetwork, CyNode node, String id, String label, String value, String start, String end)
	{
		dynNetwork.getNetwork().getRow(node).set(CyNetwork.NAME, label);
		DynInterval<T> interval = getInterval(dynNetwork,label,(T)label,start,end);
		dynNetwork.insertNode(node, CyNetwork.NAME, interval);
	}
	
	@SuppressWarnings("unchecked")
	private void setElement(DynNetwork<T> dynNetwork, CyEdge edge, String id, String label, String value, String start, String end)
	{
		dynNetwork.getNetwork().getRow(edge).set(CyNetwork.NAME, label);
		DynInterval<T> interval = getInterval(dynNetwork,edge.getSource(),edge.getTarget(),label,(T)label,start,end);
		dynNetwork.insertEdge(edge, CyNetwork.NAME, interval);
	}

	@SuppressWarnings("unchecked")
	private void setAttributes(DynNetwork<T> dynNetwork, String attName, String attValue, String attType, String start, String end)
	{
		Object attr = typeMap.getTypedValue(typeMap.getType(attType), attValue);
		DynInterval<T> interval = getIntervalAttr(dynNetwork,attName,(T)attr ,start, end);
		addRow(dynNetwork.getNetwork(), dynNetwork.getNetwork().getDefaultNetworkTable(), dynNetwork.getNetwork(), attName, attr);
		dynNetwork.insertGraph(attName, interval);
	}

	@SuppressWarnings("unchecked")
	private void setAttributes(DynNetwork<T> dynNetwork, CyNode node, String attName, String attValue, String attType, String start, String end)
	{
		Object attr = typeMap.getTypedValue(typeMap.getType(attType), attValue);
		DynInterval<T> interval = getIntervalAttr(dynNetwork,attName,(T)attr ,start, end);
		addRow(dynNetwork.getNetwork(), dynNetwork.getNetwork().getDefaultNodeTable(), node, attName, attr);
		dynNetwork.insertNode(node, attName, interval);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setAttributesUpdate(DynNetwork<T> dynNetwork, CyNode node, String attName, String attValue, String attType, String start, String end)
	{
		Object attr = typeMap.getTypedValue(typeMap.getType(attType), attValue);
		DynInterval<T> interval = getIntervalAttr(dynNetwork,attName,(T)attr ,start, end);
		addRow(dynNetwork.getNetwork(), dynNetwork.getNetwork().getDefaultNodeTable(), node, attName, attr);
		dynNetwork.insertNodeUpdate(node, attName, interval);
	}
	
	@SuppressWarnings("unchecked")
	private void setAttributes(DynNetwork<T> dynNetwork, CyEdge edge, String attName, String attValue, String attType, String start, String end)
	{
		Object attr = typeMap.getTypedValue(typeMap.getType(attType), attValue);
		DynInterval<T> interval = getIntervalAttr(dynNetwork,edge,attName,(T)attr, start, end);
		addRow(dynNetwork.getNetwork(), dynNetwork.getNetwork().getDefaultEdgeTable(), edge, attName, attr);
		dynNetwork.insertEdge(edge, attName, interval);
	}

	@Override
	public void finalizeNetwork(DynNetwork<T> dynNetwork) 
	{
		dynNetwork.finalizeNetwork();
	}

	private void addRow(CyNetwork currentNetwork, CyTable table, CyIdentifiable ci, String attName, Object attr)
	{
		if (table.getColumn(attName)==null)
			table.createColumn(attName, attr.getClass(), false);
		currentNetwork.getRow(ci).set(attName, attr);
	}

	private DynNetwork<T> createGraph(String directed, String id, String label, String start, String end)
	{
		CyRootNetwork rootNetwork = this.rootNetworkManager.getRootNetwork(networkFactory.createNetwork());
		DynNetworkImpl<T> dynNetwork = new DynNetworkImpl<T>(rootNetwork.getBaseNetwork(), directed.equals("1")?true:false);
		return dynNetwork;
	}

	private CyNode createNode(DynNetwork<T> dynNetwork, String id, String label, String start, String end)
	{
		CyNode node;
		if (!dynNetwork.containsCyNode(id))
		{
			node = dynNetwork.getNetwork().addNode();
			dynNetwork.setCyNode(id, node.getSUID());
		}
		else
		{
//			System.out.println("\nXGMML Parser Warning: updated node label=" + label + " (duplicate)");
			node = dynNetwork.getNetwork().getNode(dynNetwork.getNode(id));
		}
		
		return node;
	}
	
	private CyEdge createEdge(DynNetwork<T> dynNetwork, String id, String label, String source, String target, String start, String end)
	{
			CyNode nodeSource = dynNetwork.getNetwork().getNode(dynNetwork.getNode(source));
			CyNode nodeTarget = dynNetwork.getNetwork().getNode(dynNetwork.getNode(target));

			CyEdge edge;
			if (!dynNetwork.containsCyEdge(id))
			{
				edge = dynNetwork.getNetwork().addEdge(nodeSource, nodeTarget, dynNetwork.isDirected());
				dynNetwork.setCyEdge(id, edge.getSUID());
			}
			else
			{
//				System.out.println("\nXGMML Parser Warning: updated edge label=" + label 
//						+ " source=" + source + " target=" + target + " (duplicate)");
				edge = dynNetwork.getNetwork().getEdge(dynNetwork.getEdge(id));
			}

			return edge;
	}

}
