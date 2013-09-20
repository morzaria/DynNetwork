/**
 * 
 */
package org.cytoscape.dyn.internal.graphMetrics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.dyn.internal.CyActivator;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * @author Jimmy
 * 
 */

@SuppressWarnings("serial")
public class GraphMetricsPanel<T, C> extends JPanel implements
		CytoPanelComponent, ActionListener {

	private JPanel buttonPanel;
	private JTable attributesTable;
	private JTable edgeAttributesTable;
	private JButton plotChartButton;
	private JButton closeTab;
	private CyActivator<T, C> cyactivator;
	private DynNetwork<T> dynamicNetwork;

	private List<String> checkedAttributes;
	private List<String> edgeCheckedAttributes;
	
	public GraphMetricsPanel(
			org.cytoscape.dyn.internal.CyActivator<T, C> cyActivator,
			DynNetwork<T> dynamicNetwork) {

		this.cyactivator = cyActivator;
		this.dynamicNetwork = dynamicNetwork;

		attributesTable = new JTable(new MyTableModel(
				dynamicNetwork.getNodeAttributes()));
		attributesTable.setPreferredScrollableViewportSize(new Dimension(300,
				400));
		attributesTable.setFillsViewportHeight(true);
		attributesTable.setShowGrid(false);
		JScrollPane tablePanel = new JScrollPane(attributesTable);
		tablePanel.setSize(new Dimension(250, 400));

		edgeAttributesTable = new JTable(new MyTableModel(
				dynamicNetwork.getEdgeAttributes()));
		edgeAttributesTable.setPreferredScrollableViewportSize(new Dimension(
				300, 400));
		edgeAttributesTable.setFillsViewportHeight(true);
		edgeAttributesTable.setShowGrid(false);
		JScrollPane edgeTablePanel = new JScrollPane(edgeAttributesTable);
		edgeTablePanel.setSize(new Dimension(250, 400));

		plotChartButton = new JButton("Plot Selected Attributes");
		closeTab = new JButton("Close Tab");

		plotChartButton.addActionListener(this);
		closeTab.addActionListener(this);

		buttonPanel = new JPanel();

		buttonPanel.setLayout(new FlowLayout());

		buttonPanel.add(plotChartButton);
		buttonPanel.add(closeTab);
		buttonPanel.setBorder(BorderFactory.createTitledBorder(null, "Options",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font("SansSerif", 0, 12),
				Color.darkGray));

		tablePanel.setBorder(BorderFactory.createTitledBorder(null,
				"Node Attributes", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font("SansSerif", 0, 12),
				Color.darkGray));

		edgeTablePanel.setBorder(BorderFactory.createTitledBorder(null,
				"Edge Attributes", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, new Font("SansSerif", 0, 12),
				Color.darkGray));

		GroupLayout cytoLayout = new GroupLayout(this);
		this.setLayout(cytoLayout);
		this.add(tablePanel);
		this.add(buttonPanel);

		cytoLayout.setHorizontalGroup(cytoLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(tablePanel, GroupLayout.DEFAULT_SIZE, 280,
						Short.MAX_VALUE)
				.addComponent(edgeTablePanel, GroupLayout.DEFAULT_SIZE, 280,
						Short.MAX_VALUE)
				.addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, 280,
						Short.MAX_VALUE));
		cytoLayout.setVerticalGroup(cytoLayout
				.createSequentialGroup()
				.addComponent(tablePanel, 200, GroupLayout.DEFAULT_SIZE, 300)
				.addComponent(edgeTablePanel, 200, GroupLayout.DEFAULT_SIZE,
						300)
				.addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, 280,
						Short.MAX_VALUE));

		this.setVisible(true);
	}

	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		// TODO Auto-generated method stub
		return CytoPanelName.WEST;
	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Dynamic Graph Metrics";
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		/*
		 * if Plot Chart JButton is clicked, we check which attributes are
		 * checked by the user and store their names in a String array and then
		 * generate a chart of the checked attributes using a class
		 * GenerateChart.
		 */
		checkedAttributes = new ArrayList<String>();
		edgeCheckedAttributes = new ArrayList<String>();
		if (e.getSource() == plotChartButton) {
			for (int i = 0; i < attributesTable.getRowCount(); i++) {

				if (attributesTable.getValueAt(i, 1).equals(new Boolean(true))) {
					checkedAttributes.add(attributesTable.getValueAt(i, 0)
							.toString());
				}

			}
			for (int i = 0; i < edgeAttributesTable.getRowCount(); i++) {

				if (edgeAttributesTable.getValueAt(i, 1).equals(new Boolean(true))) {
					edgeCheckedAttributes.add(edgeAttributesTable.getValueAt(i, 0)
							.toString());
				}

			}
			List<CyNode> selectedNodes = CyTableUtil.getNodesInState(
					dynamicNetwork.getNetwork(), "selected", true);
			List<CyEdge> selectedEdges = CyTableUtil.getEdgesInState(dynamicNetwork.getNetwork(), "selected", true);
			
			GenerateChart<T> chartGenerator = new GenerateChart<T>(
					this.dynamicNetwork, checkedAttributes, edgeCheckedAttributes, selectedNodes,selectedEdges);
			JFreeChart timeSeries = chartGenerator.generateTimeSeries();
			XYSeriesCollection dataset = chartGenerator.getDataset();
			GraphMetricsResultsPanel<T, C> resultsPanel = new GraphMetricsResultsPanel<T, C>(
					timeSeries, cyactivator, dataset, dynamicNetwork);
			cyactivator.getCyServiceRegistrar().registerService(resultsPanel,
					CytoPanelComponent.class, new Properties());
			cyactivator.getCySwingAppication().getCytoPanel(CytoPanelName.EAST).setState(CytoPanelState.DOCK);
		}
		
		if (e.getSource() == closeTab) {
			
			cyactivator.getCyServiceRegistrar().unregisterService(cyactivator.getCySwingAppication().getCytoPanel(CytoPanelName.EAST).getSelectedComponent(), CytoPanelComponent.class);
		}
	}
}

/* This is the table model for the JTable attributesTable */
@SuppressWarnings("serial")
class MyTableModel extends AbstractTableModel {
	private String[] columnNames;
	private Object[][] data;

	public MyTableModel(List<String> attributeList) {
		columnNames = new String[2];
		columnNames[0] = "Attribute";
		columnNames[1] = "Check";
		data = new Object[attributeList.size()][2];
		for (int i = 0; i < attributeList.size(); i++) {
			data[i][0] = attributeList.get(i);
			data[i][1] = new Boolean(false);
		}
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		// Note that the data/cell address is constant,
		// no matter where the cell appears onscreen.
		return true;
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}
}
