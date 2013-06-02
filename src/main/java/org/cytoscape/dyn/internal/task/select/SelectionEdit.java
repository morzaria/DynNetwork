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

package org.cytoscape.dyn.internal.task.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.undo.AbstractCyEdit;

/**
 * <code> SelectionEdit </code> is used to store information on the last selection task.
 * 
 * @author Cytoscape
 *
 */
public class SelectionEdit extends AbstractCyEdit 
{
	/**
	 * Selection filter.
	 * 
	 * @author Cytoscape
	 *
	 */
	public static enum SelectionFilter 
	{
		NODES_ONLY, EDGES_ONLY, NODES_AND_EDGES;
	}

	private final CyEventHelper eventHelper;
	private final CyNetwork network;
	private final CyNetworkView view;
	private final SelectionFilter filter;
	private List<RowAndSelectionState> nodeRows;
	private List<RowAndSelectionState> edgeRows;

	/**
	 * <code> SelectionEdit </code> constructor.
	 * @param eventHelper
	 * @param description
	 * @param network
	 * @param view
	 * @param filter
	 */
	public SelectionEdit(
			final CyEventHelper eventHelper, 
			final String description,
	        final CyNetwork network, 
	        final CyNetworkView view,
	        final SelectionFilter filter)
	{
		super(description);

		this.eventHelper   = eventHelper;
		this.network       = network;
		this.view          = view;
		this.filter        = filter;

		saveSelectionState();
	}

	/**
	 * Redo.
	 */
	public void redo() 
	{
		saveAndRestoreState();
	}

	/**
	 * Undo.
	 */
	public void undo() 
	{
		saveAndRestoreState();
	}

	private void saveAndRestoreState() 
	{
		final List<RowAndSelectionState> oldNodeRows = nodeRows;
		final List<RowAndSelectionState> oldEdgeRows = edgeRows;

		saveSelectionState();
		
		if (filter == SelectionFilter.NODES_ONLY || filter == SelectionFilter.NODES_AND_EDGES) 
		{
			for (final RowAndSelectionState rowAndState : oldNodeRows)
				rowAndState.getRow().set(CyNetwork.SELECTED, rowAndState.isSelected());
		}

		if (filter == SelectionFilter.EDGES_ONLY || filter == SelectionFilter.NODES_AND_EDGES) 
		{
			for (final RowAndSelectionState rowAndState : oldEdgeRows)
				rowAndState.getRow().set(CyNetwork.SELECTED, rowAndState.isSelected());
		}

		eventHelper.flushPayloadEvents();
		view.updateView();
	}

	private void saveSelectionState() 
	{
		if (filter == SelectionFilter.NODES_ONLY || filter == SelectionFilter.NODES_AND_EDGES) 
		{
			final Collection<CyRow> rows = network.getDefaultNodeTable().getAllRows();
			nodeRows = new ArrayList<RowAndSelectionState>(rows.size());
			for (final CyRow row : rows)
				nodeRows.add(new RowAndSelectionState(row, row.get(CyNetwork.SELECTED, Boolean.class)));
		}

		if (filter == SelectionFilter.EDGES_ONLY || filter == SelectionFilter.NODES_AND_EDGES) 
		{
			final Collection<CyRow> rows = network.getDefaultEdgeTable().getAllRows();
			edgeRows = new ArrayList<RowAndSelectionState>(rows.size());
			for (final CyRow row : rows)
				edgeRows.add(new RowAndSelectionState(row, row.get(CyNetwork.SELECTED, Boolean.class)));
		}
	}
}

final class RowAndSelectionState 
{
	private final CyRow row;
	private final Boolean selected;

	public RowAndSelectionState(final CyRow row, final Boolean selected) 
	{
		this.row      = row;
		this.selected = selected;
	}

	public CyRow getRow() 
	{ 
		return row; 
	}
	
	public Boolean isSelected() 
	{ 
		return selected; 
	}
}