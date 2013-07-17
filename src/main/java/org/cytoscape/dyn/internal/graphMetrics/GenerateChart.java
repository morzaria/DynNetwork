/**
 * This class is used to generate XYStepChart using JFreeChart
 *         library.
 */
package org.cytoscape.dyn.internal.graphMetrics;

import java.util.List;

import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.model.CyNode;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.Day;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.date.DateUtilities;

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

		XYSeriesCollection dataset = new XYSeriesCollection();
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

		final String title = "Dynamic Graph Metrics";
		final String xAxisLabel = "Time";
		final String yAxisLabel = "Centrality Value";
		final JFreeChart chart = ChartFactory.createXYStepChart(title,
				xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL,
				true, // legend
				false, // tooltips
				false // urls
				);

		NumberAxis axis = new NumberAxis();
		axis.setRange(1.0, 3.0);
		chart.getXYPlot().setDomainAxis(axis);
		chart.getXYPlot().setRangeAxis(new NumberAxis());
		return chart;
	}
}
