/**
 * 
 */
package org.cytoscape.dyn.internal.graphMetrics;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * @author Jimmy
 * 
 */
@SuppressWarnings("serial")
public class GraphMetricsResultsPanel extends JPanel implements
		CytoPanelComponent, ActionListener {

	private JPanel buttonsPanel;
	private JButton enlargeButton;
	private JFreeChart timeSeries;
	private ChartPanel chartPanel;

	public GraphMetricsResultsPanel(JFreeChart timeSeries) {

		this.timeSeries = timeSeries;
		enlargeButton = new JButton("Enlarge Chart");

		buttonsPanel = new JPanel();
		initComponents();
	}

	public void initComponents() {
		chartPanel = new ChartPanel(this.timeSeries);
		buttonsPanel.add(enlargeButton);
		buttonsPanel.setLayout(new FlowLayout());
		this.add(chartPanel);
		this.add(buttonsPanel);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		// TODO Auto-generated method stub
		return CytoPanelName.EAST;
	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Dynamic Graph Metrics Results";
	}

}
