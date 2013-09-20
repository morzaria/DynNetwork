/**
 * 
 */
package org.cytoscape.dyn.internal.graphMetrics;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.dyn.internal.CyActivator;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author Jimmy
 * 
 */
@SuppressWarnings("serial")
public class GraphMetricsResultsPanel<T, C> extends JPanel implements
		CytoPanelComponent, ActionListener {

	private JPanel buttonsPanel;
	private JButton enlargeButton;
	private JButton saveChartButton;
	private JButton saveDataButton;
	private JButton closeTabButton;
	private JButton helpButton;
	private JFreeChart timeSeries;
	private ChartPanel chartPanel;
	private ChartPanel chartPanelForDialog;
	private CyActivator<T, C> cyActivator;
	private XYSeriesCollection dataset;
	private DynNetwork<T> dynamicNetwork;

	/**
	 * @param timeSeries
	 * @param cyActivator
	 * @param dataset
	 */
	public GraphMetricsResultsPanel(JFreeChart timeSeries,
			CyActivator<T, C> cyActivator, XYSeriesCollection dataset, DynNetwork dynamicNetwork) {

		this.timeSeries = timeSeries;
		this.cyActivator = cyActivator;
		this.dataset = dataset;
		this.dynamicNetwork = dynamicNetwork;
		initComponents();
	}

	/**
	 * 
	 */
	public void initComponents() {
		enlargeButton = new JButton("Enlarge Chart");
		enlargeButton.addActionListener(this);
		enlargeButton.setToolTipText("View chart in a new window.");
		saveChartButton = new JButton("Save Chart");
		saveChartButton.addActionListener(this);
		saveChartButton
				.setToolTipText("Save the chart as a .jpg/.png/.svg file.");
		saveDataButton = new JButton("Save Data");
		saveDataButton.addActionListener(this);
		saveDataButton.setToolTipText("Save data in a file.");
		closeTabButton = new JButton("Close Tab");
		closeTabButton.addActionListener(this);
		closeTabButton.setToolTipText("Close this tab.");
		helpButton = new JButton("Help");
		helpButton.setToolTipText("Get help!");
		helpButton.addActionListener(this);
		buttonsPanel = new JPanel();
		chartPanel = new ChartPanel(this.timeSeries);
		buttonsPanel.add(enlargeButton);
		buttonsPanel.add(saveChartButton);
		buttonsPanel.add(saveDataButton);
		buttonsPanel.add(closeTabButton);
		buttonsPanel.add(helpButton);
		buttonsPanel.setLayout(new FlowLayout());
		this.add(chartPanel);
		this.add(buttonsPanel);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object source = e.getSource();
		if (source == enlargeButton) {
			enlargeChart();
		} else if (source == saveChartButton) {
			SaveChartDialog dialog = new SaveChartDialog(cyActivator
					.getCySwingAppication().getJFrame(), timeSeries);
			dialog.setVisible(true);
		} else if (source == saveDataButton) {
			saveData();
		} else if (source == closeTabButton) {
			cyActivator.getCyServiceRegistrar().unregisterService(this,
					CytoPanelComponent.class);
		} else if (source == helpButton) {
			DynamicNetworkHelp help = new DynamicNetworkHelp();
			help.displayHelp();
		}
	}

	/**
	 * 
	 */
	public void enlargeChart() {
		chartPanelForDialog = new ChartPanel(this.timeSeries);
		JDialog dialog = new JDialog(cyActivator.getCySwingAppication()
				.getJFrame(), "Dynamic Graph Metrics", false);
		dialog.getContentPane().add(chartPanelForDialog);
		dialog.pack();
		dialog.setLocationRelativeTo(cyActivator.getCySwingAppication()
				.getJFrame());
		dialog.setVisible(true);
	}

	/**
	 * 
	 */
	public void saveData() {
		JFileChooser saveFileDialog = new JFileChooser();
		int save = saveFileDialog.showSaveDialog(null);
		if (save == JFileChooser.APPROVE_OPTION) {
			FileWriter writer = null;
			try {
				File file = saveFileDialog.getSelectedFile();
				if (file.exists()) {
					if (JOptionPane
							.showConfirmDialog(
									null,
									"The specified file already exists. Do you want to overwrite it?",
									"Warning - File Exists",
									JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						writer = new FileWriter(file);
						for (int i = 0; i < this.dataset.getSeriesCount(); i++) {
							writer.write(this.dataset.getSeries(i).getKey()
									.toString()
									+ "\n");
							for (int j = 0; j < this.dataset.getSeries(i)
									.getItemCount(); j++) {
								writer.write(this.dataset.getSeries(i)
										.getDataItem(j).toString()
										+ "\n");
							}
						}
					}
				} else {
					writer = new FileWriter(file);
					for (int i = 0; i < this.dataset.getSeriesCount(); i++) {
						writer.write(this.dataset.getSeries(i).getKey()
								.toString()
								+ "\n");
						for (int j = 0; j < this.dataset.getSeries(i)
								.getItemCount(); j++) {
							writer.write(this.dataset.getSeries(i)
									.getDataItem(j).toString()
									+ "\n");
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
		return dynamicNetwork.getNetworkLabel();
	}

}
