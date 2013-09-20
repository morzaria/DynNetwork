/**
 * 
 */
package org.cytoscape.dyn.internal.graphMetrics;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.svg.SVGGraphics2D;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

/**
 * Dialog for saving a chart as a .png, .jpeg file.
 *  
 * @author Jimmy
 * 
 *        
 */
@SuppressWarnings("serial")
public class SaveChartDialog extends JDialog implements ActionListener {

	private JFreeChart chart;
	private JButton saveChartButton;
	private JButton cancelButton;
	private JSpinner heightSpinner;
	private JSpinner widthSpinner;

	public SaveChartDialog(JFrame frame, JFreeChart chart) {
		super(frame, "Save Chart to File", false);
		this.chart = chart;
		JPanel sizePanel = new JPanel(new GridLayout(2, 3, 4, 4));
		sizePanel.setBorder(BorderFactory.createTitledBorder("Image Size"));

		// Add a spinner for choosing width
		sizePanel.add(new JLabel("Width:", SwingConstants.RIGHT));
		int width = ChartPanel.DEFAULT_WIDTH;
		int minWidth = ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH;
		int maxWidth = ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH;
		SpinnerModel widthSettings = new SpinnerNumberModel(width, minWidth,
				maxWidth, 1);
		sizePanel.add(widthSpinner = new JSpinner(widthSettings));
		sizePanel.add(new JLabel("pixels"));

		// Add a spinner for choosing height
		sizePanel.add(new JLabel("Height:", SwingConstants.RIGHT));
		int height = ChartPanel.DEFAULT_HEIGHT;
		int minHeight = ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT;
		int maxHeight = ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT;
		SpinnerModel heightSettings = new SpinnerNumberModel(height, minHeight,
				maxHeight, 1);
		sizePanel.add(heightSpinner = new JSpinner(heightSettings));
		sizePanel.add(new JLabel("pixels"));

		JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 4, 0));
		saveChartButton = new JButton("Save");
		saveChartButton.setMaximumSize(new Dimension(Short.MAX_VALUE,
				saveChartButton.getHeight()));
		saveChartButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.setMaximumSize(new Dimension(Short.MAX_VALUE, cancelButton
				.getHeight()));
		cancelButton.addActionListener(this);
		buttonsPanel.add(saveChartButton);
		buttonsPanel.add(cancelButton);
		Box buttonsBox = Box.createHorizontalBox();
		buttonsBox.add(Box.createHorizontalGlue());
		buttonsBox.add(buttonsPanel);
		buttonsBox.add(Box.createHorizontalGlue());

		Container contentPane = getContentPane();
		contentPane.add(sizePanel, BorderLayout.NORTH);
		contentPane.add(Box.createVerticalStrut(3));
		contentPane.add(buttonsBox, BorderLayout.PAGE_END);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getRootPane().setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		pack();
		setModal(true);
		setResizable(false);
		setLocationRelativeTo(frame);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == cancelButton) {
			this.setVisible(false);
			this.dispose();
		} else if (e.getSource() == saveChartButton) {
			JFileChooser saveFileDialog = new JFileChooser();
			saveFileDialog.addChoosableFileFilter(new ExtensionFileFilter(
					".jpeg", ".jpg", "Jpeg images (.jpeg, .jpg)"));
			saveFileDialog.addChoosableFileFilter(new ExtensionFileFilter(
					".png", "Portable Network Graphic images (.png)"));
			saveFileDialog.addChoosableFileFilter(new ExtensionFileFilter(
					".svg", "Scalable Vector Graphics (.svg)"));
			int save = saveFileDialog.showSaveDialog(this);
			if (save == JFileChooser.APPROVE_OPTION) {
				File file = saveFileDialog.getSelectedFile();
				int width = ((SpinnerNumberModel) widthSpinner.getModel())
						.getNumber().intValue();
				int height = ((SpinnerNumberModel) heightSpinner.getModel())
						.getNumber().intValue();

				ExtensionFileFilter filter = null;
				try {
					filter = (ExtensionFileFilter) saveFileDialog
							.getFileFilter();
					if (!filter.hasExtension(file)) {
						file = filter.appendExtension(file);
					}
				} catch (ClassCastException ex) {
					// Try to infer the type of file by its extension
					FileFilter[] filters = saveFileDialog
							.getChoosableFileFilters();
					for (int i = 0; i < filters.length; ++i) {
						if (filters[i] instanceof ExtensionFileFilter) {
							filter = (ExtensionFileFilter) filters[i];
							if (filter.hasExtension(file)) {
								break;
							}
							filter = null;
						}
					}

					if (filter == null) {
						// Could not infer the type
						JOptionPane
								.showMessageDialog(
										null,
										"File type not specified!\nWhen giving file name, please also select one of the supported file types.",
										"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				// Save the chart to the specified file name
				try {
					String ext = filter.getExtension();
					if (ext.equals("jpeg")) {
						ChartUtilities.saveChartAsJPEG(file, chart, width,
								height);
					} else if (ext.equals("png")) {
						ChartUtilities.saveChartAsPNG(file, chart, width,
								height);
					} else {
						VectorGraphics graphics = new SVGGraphics2D(file,
								new Dimension(width, height));
						graphics.startExport();
						chart.draw(graphics, new Rectangle2D.Double(0, 0,
								width, height));
						graphics.endExport();
					}
				} catch (IOException ex) {
					JOptionPane
							.showMessageDialog(
									null,
									"An error occurred while creating or writing to the file.",
									"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				this.setVisible(false);
				this.dispose();
			} else if (save == JFileChooser.ERROR_OPTION) {
				JOptionPane.showMessageDialog(null,
						"An error occurred while initializing the window.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
