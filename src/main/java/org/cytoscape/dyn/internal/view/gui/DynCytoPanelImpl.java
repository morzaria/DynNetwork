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

package org.cytoscape.dyn.internal.view.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkViewEvent;
import org.cytoscape.application.events.SetCurrentNetworkViewListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.dyn.internal.io.write.graphics.PNGWriterFactory;
import org.cytoscape.dyn.internal.layout.model.DynLayoutManager;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.tree.DynInterval;
import org.cytoscape.dyn.internal.model.tree.DynIntervalDouble;
import org.cytoscape.dyn.internal.view.model.DynNetworkView;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManager;
import org.cytoscape.dyn.internal.view.task.BlockingQueue;
import org.cytoscape.dyn.internal.view.task.DynNetworkViewTask;
import org.cytoscape.dyn.internal.view.task.DynNetworkViewTaskIterator;
import org.cytoscape.dyn.internal.view.task.DynNetworkViewTransparencyTask;
import org.cytoscape.dyn.internal.view.task.DynVizmapTask;
import org.cytoscape.dyn.internal.view.task.Transformator;
import org.cytoscape.dyn.internal.vizmapper.model.DynVizMapManager;
import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

/**
 * <code> DynCytoPanelImpl </code> implements the a JPanel component in {@link CytoPanel} 
 * west to provide a time slider for controlling the dynamic visualization.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 * @param <C>
 */
public final class DynCytoPanelImpl<T,C> extends JPanel implements DynCytoPanel<T,C>,
ChangeListener, ActionListener, SetCurrentNetworkViewListener
{
	private static final long serialVersionUID = 1L;
	
	private final CySwingApplication desktopApp;
	private final TaskManager<T,C> taskManager;
	private final BlockingQueue queue;
	private final CyApplicationManager appManager;
	private final DynNetworkViewManager<T> viewManager;
	private final Transformator<T> transformator;
	private final FileUtil fileUtil;
	
	private DynNetwork<T> network;
	private DynNetworkView<T> view;
	
	private double time;
	private double minTime;
	private double maxTime;
	
	private int visibility = 0;
	private int hiddenVisibility = 30;
	private int smoothness;
	
	private volatile boolean valueIsAdjusting = false;
	
	private int sliderMax;
	private DynNetworkViewTask<T,C> singleTask;
	private DynNetworkViewTaskIterator<T,C> recursiveTask;

	private JPanel buttonPanel,recordPanel;
	private JPanel dynVizPanel;
	private JPanel featurePanel;
	private JPanel measurePanel;
	private volatile JLabel currentTime;
	private volatile JLabel nodeNumber;
	private JLabel edgeNumber;
	private JSlider slider;
	private JComboBox resolutionComboBox;
	private JComboBox smoothnessComboBox;
	private JButton forwardButton, backwardButton,stopButton,vizmapButton,recordButton;
	private JCheckBox seeAllCheck;
	private Hashtable<Integer, JLabel> labelTable;
	private DecimalFormat formatter,formatter2;

	/**
	 * <code> DynCytoPanelImpl </code> constructor.
	 * @param desktopApp
	 * @param taskManager
	 * @param appManager
	 * @param viewManager
	 * @param layoutManager
	 * @param vizMapManager
	 * @param continousFactory
	 * @param discreteFactory
	 * @param passthroughFactory
	 */
	public DynCytoPanelImpl(
			final CySwingApplication desktopApp,
			final TaskManager<T,C> taskManager,
			final CyApplicationManager appManager,
			final DynNetworkViewManager<T> viewManager,
			final DynLayoutManager<T> layoutManager,
			final DynVizMapManager<T> vizMapManager,
			final Transformator<T> transformator,
			final FileUtil fileUtil)
	{
		this.desktopApp = desktopApp;
		this.taskManager = taskManager;
		this.appManager = appManager;
		this.viewManager = viewManager;
		this.transformator = transformator;
		this.fileUtil = fileUtil;
		
		this.queue = new BlockingQueue();
		initComponents();
	}

	@Override
	public synchronized void stateChanged(ChangeEvent event)
	{
		view = viewManager.getDynNetworkView(appManager.getCurrentNetworkView());
		if (event.getSource() instanceof JSlider)
			if (view!=null)
			{
				time = slider.getValue()*((maxTime-minTime)/sliderMax)+(minTime);
				currentTime.setText("Current time = " + formatter.format(time));
				if (!valueIsAdjusting)
				{
					updateView();
				}
			}
	}

	@Override
	public synchronized void actionPerformed(ActionEvent event)
	{
		view = viewManager.getDynNetworkView(appManager.getCurrentNetworkView());
		if (view!=null && event.getSource() instanceof JButton)
		{
			if (recursiveTask!=null)
				recursiveTask.cancel();
			
			JButton source = (JButton)event.getSource();
			if (source.equals(forwardButton))
				new Thread(recursiveTask = new DynNetworkViewTaskIterator<T,C>(
						this, view,transformator, queue, slider, +1)).start();
			else if (source.equals(backwardButton))
				new Thread(recursiveTask = new DynNetworkViewTaskIterator<T,C>(
						this, view,transformator, queue, slider, -1)).start();
			else if (source.equals(vizmapButton))
				taskManager.execute(new TaskIterator(1, new DynVizmapTask<T>(
						view,view.getCurrentVisualStyle(), queue)));
			else if (source.equals(recordButton))
			{
				if (recordButton.getBackground().equals(Color.red))
				{
					recordButton.setBackground(forwardButton.getBackground());
					recordButton.setOpaque(false);
					transformator.removeSink(null);
				}
				else
				{
					recordButton.setBackground(Color.red);
					recordButton.setOpaque(true);
					List<FileChooserFilter> filters = getFilters();
					File file = fileUtil.getFile(desktopApp.getJFrame(), "Save Image Sequence", FileUtil.SAVE, filters);
					

					if (file!=null)
					{
						transformator.addSink(new PNGWriterFactory<T>(file,appManager.getCurrentRenderingEngine()));
						updateView();

					}
					else
					{
						recordButton.setBackground(forwardButton.getBackground());
						transformator.removeSink(null);
					}
				}
			}
		}
		else if (event.getSource() instanceof JButton)
		{
			JButton source = (JButton)event.getSource();
			if (source.equals(recordButton) && recordButton.getBackground().equals(Color.red))
			{
				recordButton.setBackground(forwardButton.getBackground());
				transformator.removeSink(null);
			}	
		}
		else if (event.getSource() instanceof JCheckBox)
		{
			JCheckBox source = (JCheckBox)event.getSource();
			if (source==seeAllCheck)
			{
				if (source.isSelected())
					this.visibility = hiddenVisibility;
				else
					this.visibility = 0;
				if (!valueIsAdjusting)
					updateTransparency();
			}
		}
		else if (event.getSource() instanceof JComboBox)
		{
			JComboBox source = (JComboBox)event.getSource();
			if (source==resolutionComboBox)
			{
				updateGui((double) slider.getValue()/sliderMax, ((NameIDObj)source.getSelectedItem()).id);
				if (!valueIsAdjusting)
					updateView();
			}
			else if (source==smoothnessComboBox)
			{
				this.smoothness = ((NameIDObj)source.getSelectedItem()).id;
			}
		}
	}
	
	@Override
	public synchronized void handleEvent(SetCurrentNetworkViewEvent e) 
	{
		if (recursiveTask!=null)
			recursiveTask.cancel();

		if (e.getNetworkView()!=null)
		{
			if (view!=null)
				view.setCurrentTime((double) slider.getValue()/sliderMax);
			
			view = viewManager.getDynNetworkView(e.getNetworkView());
			
			if (view!=null)
			{
				network = view.getNetwork();
				updateGui(view.getCurrentTime(), ((NameIDObj)resolutionComboBox.getSelectedItem()).id);
				updateView();
			}
		}
	}
		
	@SuppressWarnings("unchecked")
	@Override
	public void initView() 
	{
		view = viewManager.getDynNetworkView(appManager.getCurrentNetworkView());
		if (view!=null)
		{
			network = view.getNetwork();
			updateGui(view.getCurrentTime(), ((NameIDObj)resolutionComboBox.getSelectedItem()).id);
			transformator.initialize(view, (DynInterval<T>) new DynIntervalDouble(time,time),visibility);
			updateView();
		}
		
	}

	@Override
	public double getMinTime() 
	{
		return minTime;
	}

	@Override
	public double getMaxTime() 
	{
		return maxTime;
	}
	
	@Override
	public double getTime() 
	{
		return this.time;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DynInterval<T> getTimeInterval() 
	{
		if (time>=maxTime)
			return (DynInterval<T>) new DynIntervalDouble(time-0.0000001, time-0.0000001);
		else
			return (DynInterval<T>) new DynIntervalDouble(time, time);
	}
	
	@Override
	public void setMaxTime(double maxTime) 
	{
		this.maxTime = maxTime;
		updateGui(view.getCurrentTime(), ((NameIDObj)resolutionComboBox.getSelectedItem()).id);
		updateView();
	}

	@Override
	public void setMinTime(double minTime) 
	{
		this.minTime = minTime;
		updateGui(view.getCurrentTime(), ((NameIDObj)resolutionComboBox.getSelectedItem()).id);
		updateView();
	}

	@Override
	public int getVisibility() 
	{
		return visibility;
	}
	
	@Override
	public int getSmoothness() 
	{
		return smoothness;
	}
	
	@Override
	public double getDeltat()
	{
		int id = ((NameIDObj)resolutionComboBox.getSelectedItem()).id;
		if (id==1)
			return id;
		else
			return (maxTime-minTime)/id;
//		return (maxTime-minTime)/((NameIDObj)resolutionComboBox.getSelectedItem()).id;
	}

	@Override
	public int getSliderMax() 
	{
		return sliderMax;
	}

	@Override
	public Component getComponent() 
	{
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() 
	{
		return CytoPanelName.WEST;
	}

	@Override
	public String getTitle() 
	{
		return "Dynamic Network";
	}

	@Override
	public Icon getIcon() 
	{
		return null;
	}
	
	@Override
	public void setNodes(int nodes) 
	{
		nodeNumber.setText("Current nodes = " + formatter2.format(nodes) + "/" + formatter2.format(network.getNetwork().getNodeCount()));
	}

	@Override
	public void setEdges(int edges) 
	{
		edgeNumber.setText("Current edges = " + formatter2.format(edges) + "/" + formatter2.format(network.getNetwork().getEdgeCount()));
	}

	@Override
	public void setValueIsAdjusting(boolean valueIsAdjusting)
	{
		this.valueIsAdjusting = valueIsAdjusting;
	}
	
	private void initComponents()
	{
		formatter = new DecimalFormat("#0.000");
		currentTime = new JLabel("Current time = ");
		
		slider = new JSlider(JSlider.HORIZONTAL,0, 100, 0);
		labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer( 0 ),new JLabel(formatter.format(Double.NEGATIVE_INFINITY)) );
		labelTable.put(new Integer( 100 ),new JLabel(formatter.format(Double.POSITIVE_INFINITY)) );
		slider.setLabelTable(labelTable);
		slider.setMajorTickSpacing(25);
		slider.setMinorTickSpacing(5);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.addChangeListener(this);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		forwardButton = new JButton("Play >>");
		stopButton = new JButton("Stop");
		backwardButton = new JButton("<< Play");
		forwardButton.addActionListener(this);
		stopButton.addActionListener(this);
		backwardButton.addActionListener(this);
		buttonPanel.add(backwardButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(forwardButton);
		
		recordPanel = new JPanel();
		recordPanel.setLayout(new GridBagLayout());
		recordButton = new JButton("                  Record                  ");
		recordButton.addActionListener(this);
		recordPanel.add(recordButton);
		
		dynVizPanel = new JPanel();
		dynVizPanel.setLayout(new GridLayout(4,1));
		dynVizPanel.add(currentTime);
		dynVizPanel.add(slider);
		dynVizPanel.add(buttonPanel);
		dynVizPanel.add(recordPanel);

		NameIDObj[] itemsTimeResolution = { 
				new NameIDObj(1,    "1       "),
				new NameIDObj(10,   "1/10    "), 
				new NameIDObj(25,   "1/25    "),
				new NameIDObj(50,   "1/50    "),
				new NameIDObj(75,   "1/75    "),
				new NameIDObj(100,  "1/100   "), 
				new NameIDObj(1000, "1/1000  "),
				new NameIDObj(10000, "1/10000 "),
				new NameIDObj(100000,"1/100000") };
		resolutionComboBox  = new JComboBox(itemsTimeResolution);
		resolutionComboBox.setSelectedIndex(1);
		resolutionComboBox.addActionListener(this);
		
		NameIDObj[] itemsSmoothness = { 
				new NameIDObj(0,   "0 ms    "), 
				new NameIDObj(250, "250 ms  "),
				new NameIDObj(500, "500 ms  "),
				new NameIDObj(750, "750 ms  "),
				new NameIDObj(1000,"1000 ms "),
				new NameIDObj(2000,"2000 ms "),
				new NameIDObj(3000,"3000 ms "),
				new NameIDObj(4000,"4000 ms ")};
		smoothnessComboBox  = new JComboBox(itemsSmoothness);
		smoothnessComboBox.setSelectedIndex(4);
		smoothnessComboBox.addActionListener(this);
		this.smoothness = ((NameIDObj)smoothnessComboBox.getSelectedItem()).id;

		vizmapButton = new JButton("Reset");
		vizmapButton.addActionListener(this);
		
		seeAllCheck = new JCheckBox("Display all",false);
		seeAllCheck.addActionListener(this);
		
		featurePanel = new JPanel();
		featurePanel.setLayout(new GridLayout(4,2));
		featurePanel.add(new JLabel("Time resolution      "));
		featurePanel.add(resolutionComboBox);
		featurePanel.add(new JLabel("Time smoothness      "));
		featurePanel.add(smoothnessComboBox);
		featurePanel.add(new JLabel("VizMap Range "));
		featurePanel.add(vizmapButton);
		featurePanel.add(new JLabel("Node/edge visibility "));
		featurePanel.add(seeAllCheck);
		
		// TODO: remove this after fixing bugs in the visualization
		seeAllCheck.setEnabled(false);
		
		formatter2 = new DecimalFormat("#0");
		nodeNumber = new JLabel    ("Current nodes = ");
		edgeNumber = new JLabel    ("Current edges = ");
		
		measurePanel = new JPanel();
		measurePanel.setLayout(new GridLayout(10,1));
		measurePanel.add(nodeNumber);
		measurePanel.add(edgeNumber);
		
		dynVizPanel
		.setBorder(BorderFactory.createTitledBorder(null,
				"Dynamic Visualization",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION,
				new Font("SansSerif", 1, 12),
				Color.darkGray));
		
		featurePanel
		.setBorder(BorderFactory.createTitledBorder(null,
				"Options",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION,
				new Font("SansSerif", 1, 12),
				Color.darkGray));
		
		measurePanel
		.setBorder(BorderFactory.createTitledBorder(null,
				"Metrics",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION,
				new Font("SansSerif", 1, 12),
				Color.darkGray));
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(
				   layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				           .addComponent(dynVizPanel, GroupLayout.DEFAULT_SIZE,
				        		   280 , Short.MAX_VALUE)
				           .addComponent(featurePanel, GroupLayout.DEFAULT_SIZE,
				        		   280, Short.MAX_VALUE)
				           .addComponent(measurePanel, GroupLayout.DEFAULT_SIZE,
				        		   280, Short.MAX_VALUE)
				);
				layout.setVerticalGroup(
				   layout.createSequentialGroup()
				      .addComponent(dynVizPanel, 190,
				    		  GroupLayout.DEFAULT_SIZE, 270)
				      .addComponent(featurePanel,  GroupLayout.DEFAULT_SIZE,
				    		  200 , Short.MAX_VALUE)
				      .addComponent(measurePanel, GroupLayout.DEFAULT_SIZE,
				    		   500, Short.MAX_VALUE)
				);

		this.setVisible(true);
	}
	
	private void updateView()
	{
		if (singleTask!=null)
			singleTask.cancel();
		
		new Thread(singleTask = new DynNetworkViewTask<T,C>(this, view,transformator,queue)).start();
	}
	
	private void updateTransparency()
	{
		new Thread(new DynNetworkViewTransparencyTask<T,C>(this,view,queue)).start();
	}
	
	private void updateGui(double absoluteTime, int value)
	{
		if (value==1)
			value = (int) (maxTime-minTime);
		minTime = network.getMinTime();
		maxTime = network.getMaxTime();
		valueIsAdjusting = true;
		sliderMax = value;
		slider.setMaximum(value);
		slider.setValue((int) (absoluteTime*(double) sliderMax));
		valueIsAdjusting = false;
		
		time = slider.getValue()*((maxTime-minTime)/sliderMax)+(minTime);
		currentTime.setText("Current time = " + formatter.format(time));
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				labelTable.clear();
				labelTable.put(new Integer( 0 ),new JLabel(formatter.format(minTime)) );
				labelTable.put(new Integer( sliderMax ),new JLabel(formatter.format(maxTime)) );
				slider.setMaximum(sliderMax);
				slider.setLabelTable(labelTable);
				slider.setMajorTickSpacing((int) (0.5*sliderMax));
				slider.setMinorTickSpacing((int) (0.1*sliderMax));
				slider.setPaintTicks(true);
				slider.setPaintLabels(true);
			}
		});
	}
	
	private List<FileChooserFilter> getFilters()
	{
		List<FileChooserFilter> filters = new ArrayList<FileChooserFilter>();
    	filters.add(new FileChooserFilter("PNG Image", "png"));
    	return filters;
	}

}

final class NameIDObj
{
	int id;
	String name;

	NameIDObj(int id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public String toString()
	{
		return name;
	}
}


