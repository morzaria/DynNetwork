/**
 * This class is used to generate XYStepChart using JFreeChart
 *         library.
 */
package org.cytoscape.dyn.internal.graphMetrics;

import java.awt.Color;
import java.util.List;

import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.model.CyNode;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 * @author Jimmy
 */
public class GenerateChart<T> {

	/*
	 * dynamic network is required to get the attributes and their values of the
	 * selected nodes
	 */
	private DynNetwork<T> dynamicNetwork;
	/*
	 * a list of attributes that are checked by the user in the control panel
	 * which will be plotted as separate series in the same graph
	 */
	private List<String> checkedAttributes;
	/*
	 * a list of the nodes that are selected by the user, the attributes of
	 * which will be plotted in the same graph
	 */
	private List<CyNode> selectedNodes;
	private XYSeriesCollection dataset;

	/**
	 * @param dynamicNetwork
	 * @param checkedAttributes
	 * @param selectedNodes
	 *            The constructor takes as parameters the dynamic network, a
	 *            list of checked attributes and a list of selected nodes.
	 */
	public GenerateChart(DynNetwork<T> dynamicNetwork,
			List<String> checkedAttributes, List<CyNode> selectedNodes) {
		this.dynamicNetwork = dynamicNetwork;
		this.checkedAttributes = checkedAttributes;
		this.selectedNodes = selectedNodes;
	}

	/*
	 * generates an XYStepChart using JFreeChart library
	 */
	public JFreeChart generateTimeSeries() {

		dataset = new XYSeriesCollection();
		XYSeries[] attributeSeries = new XYSeries[selectedNodes.size()
				* checkedAttributes.size()];
		int j = 0;
		for (CyNode node : selectedNodes) {
			// System.out.println(checkedAttributes.size());
			for (int i = 0; i < checkedAttributes.size(); i++) {
				attributeSeries[j] = new XYSeries(
						dynamicNetwork.getNodeLabel(node)
								+ checkedAttributes.get(i), false, true);
				// System.out.println(dynamicNetwork.getDynAttribute(node,
				// checkedAttributes.get(i)).getKey().getColumn());

				for (DynInterval<T> interval : dynamicNetwork.getDynAttribute(
						node, checkedAttributes.get(i)).getIntervalList()) {
					// System.out.println(interval.getOnValue());
					double value = (Double) interval.getOnValue();
					// System.out.println(value);
					attributeSeries[j].add(interval.getStart(), value);
					attributeSeries[j].add(interval.getEnd(), value);
				}
				dataset.addSeries(attributeSeries[j++]);
			}
		}

		String title = "Dynamic Graph Metrics";
		String xAxisLabel = "Time";
		String yAxisLabel = "Centrality Value";
		JFreeChart chart = ChartFactory.createXYStepChart(title, xAxisLabel,
				yAxisLabel, dataset, PlotOrientation.VERTICAL, true, // legend
				true, // tooltips
				false // urls
				);

		NumberAxis xaxis = new NumberAxis();
		xaxis.setAutoRangeMinimumSize(1.0);
		xaxis.setLabel("Time");
		chart.getXYPlot().setDomainAxis(xaxis);
		NumberAxis yaxis = new NumberAxis();
		yaxis.setAutoRangeIncludesZero(true);
		yaxis.setLabel("Centrality Value");
		chart.getXYPlot().setRangeAxis(yaxis);
		chart.setBackgroundPaint(Color.white);
		//chart.setPadding(new RectangleInsets(20,20,20,20));
		chart.getXYPlot().setBackgroundPaint(Color.white);
		chart.getXYPlot().setDomainGridlinePaint(Color.gray);
		chart.getXYPlot().setRangeGridlinePaint(Color.gray);
		return chart;
	}

	public XYSeriesCollection getDataset() {
		return dataset;
	}
}
