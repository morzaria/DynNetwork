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

package org.cytoscape.dyn.internal.layout.task;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cytoscape.dyn.internal.view.model.DynNetworkView;

/**
 * <code> ForceDirectedDynLayoutDialog </code> implements the dialog to set the parameters of the 
 * Dynamic Kamada Kawai Layout{@link KKDynLayout}.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public class ForceDirectedDynLayoutDialog<T> extends JDialog implements ActionListener, ChangeListener
{
	private static final long serialVersionUID = 1L;

	private final JFrame parent;
	private final DynNetworkView<T> dynView;
	private final ForceDirectedLayoutContext context;
	
	private List<Double> events;
	
	private JLabel currentPast;
	private JLabel currentFuture;
	private JLabel currentItertions;
	
	private JButton okButton;
    private JButton cancelButton;
    private JSlider sliderPast;
    private JSlider sliderFuture;
    private JSlider sliderIterations;
    private JComboBox attrComboBox;
    private Hashtable<Integer,JLabel> labelTablePast;
    private Hashtable<Integer,JLabel> labelTableFuture;
    private Hashtable<Integer,JLabel> labelTableIterations;
	
    /**
     * <code> KKDynLayoutDialog </code> constructor.
     * @param parent
     * @param dynView
     * @param context
     */
	public ForceDirectedDynLayoutDialog(
			final JFrame parent, 
			final DynNetworkView<T> dynView,
			final ForceDirectedLayoutContext context) 
	{
		super(parent, "Dynamic Prefuse", true);
		this.parent = parent;
		this.dynView = dynView;
		this.context = context;
		context.m_cancel = true;
		initComponents(context.m_event_type,context.m_max_iterations,context.m_past_events,context.m_future_events);
	}

	private void initComponents(int type, int iterations, int past, int future) 
	{	
		JPanel topPanel = new JPanel(new GridLayout(4,2));
		
		List<String> attList = dynView.getNetwork().getEdgeAttributes();
		NameIDObj[] itemsAttributes = new NameIDObj[attList.size()+1];
		itemsAttributes[0] = new NameIDObj(0,   "none");
		for (int i=0;i<attList.size();i++)
			itemsAttributes[i+1] = new NameIDObj(i+1,attList.get(i));
		attrComboBox  = new JComboBox(itemsAttributes);
		attrComboBox.setSelectedIndex(context.m_event_type);
		attrComboBox.addActionListener(this);
		
		events = getEvents(((NameIDObj)attrComboBox.getSelectedItem()).id);
		
		currentItertions = new JLabel("Maximum iterations = " + Math.max(1, iterations));
		sliderIterations = new JSlider(JSlider.HORIZONTAL, 0, 1000, iterations);
		labelTableIterations = new Hashtable<Integer, JLabel>();
		labelTableIterations.put(new Integer( 0 ),new JLabel("0") );
		labelTableIterations.put(new Integer( 500 ),new JLabel("500") );
		labelTableIterations.put(new Integer( 1000 ),new JLabel("1000") );
		sliderIterations.setLabelTable(labelTableIterations);
		sliderIterations.setMajorTickSpacing(250);
		sliderIterations.setMinorTickSpacing(50);
		sliderIterations.setPaintTicks(true);
		sliderIterations.setPaintLabels(true);
		sliderIterations.addChangeListener(this);
		
		currentPast = new JLabel("Past events = " + past);
		sliderPast = new JSlider(JSlider.HORIZONTAL, 0, events.size(), past);
		labelTablePast = new Hashtable<Integer, JLabel>();
		labelTablePast.put(new Integer( 0 ),new JLabel("0") );
		labelTablePast.put(new Integer( events.size() ),new JLabel(Integer.toString(events.size())) );
		sliderPast.setLabelTable(labelTablePast);
		sliderPast.setMajorTickSpacing(events.size()/4);
		sliderPast.setPaintTicks(true);
		sliderPast.setPaintLabels(true);
		sliderPast.addChangeListener(this);
		
		currentFuture = new JLabel("Future events = " + future);
		sliderFuture = new JSlider(JSlider.HORIZONTAL, 0, events.size(), future);
		labelTableFuture = new Hashtable<Integer, JLabel>();
		labelTableFuture.put(new Integer( 0 ),new JLabel("0") );
		labelTableFuture.put(new Integer( events.size() ),new JLabel(Integer.toString(events.size())) );
		sliderFuture.setLabelTable(labelTableFuture);
		sliderFuture.setMajorTickSpacing(events.size()/4);
		sliderFuture.setPaintTicks(true);
		sliderFuture.setPaintLabels(true);
		sliderFuture.addChangeListener(this);
		
//		topPanel.add(new JLabel("Distance Edge Attribute"));
//		topPanel.add(attrComboBox);
//		topPanel.add(Box.createRigidArea(new Dimension(10, 3)));
//		topPanel.add(Box.createRigidArea(new Dimension(10, 3)));
		topPanel.add(currentItertions);
		topPanel.add(sliderIterations);
		topPanel.add(currentPast);
		topPanel.add(sliderPast);
		topPanel.add(currentFuture);
		topPanel.add(sliderFuture);
		topPanel.add(Box.createRigidArea(new Dimension(10, 3)));
		topPanel.add(Box.createRigidArea(new Dimension(10, 3)));
		
		this.okButton = new JButton("OK");
        this.okButton.addActionListener(this);
        this.cancelButton = new JButton("Cancel");
        this.cancelButton.addActionListener(this);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(this.okButton);
		bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		bottomPanel.add(this.cancelButton);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		mainPanel.add(topPanel, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.PAGE_END);
		
		this.add(mainPanel);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parent);
		setSize(700,300);
		pack();

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		if (event.getSource() instanceof JButton)
		{
			JButton source = (JButton)event.getSource();
			if (source.equals(okButton))
			{
				context.m_cancel = false;
				context.m_event_type = ((NameIDObj)attrComboBox.getSelectedItem()).id;
				context.m_attribute_name = ((NameIDObj)attrComboBox.getSelectedItem()).name;
				context.m_iteration_rate = context.m_max_iterations/getMaxDiff(events);
				context.m_event_list = filterEvents(events, context.m_iteration_rate);
				context.m_max_iterations = Math.max(1,sliderIterations.getValue());
				context.m_past_events = sliderPast.getValue();
				context.m_future_events = sliderFuture.getValue();
				setVisible(false); 
				dispose();
			}
			else if (source.equals(cancelButton))
			{
				setVisible(false); 
				dispose();
			}
		}
		else if (event.getSource() instanceof JComboBox)
		{
			JComboBox source = (JComboBox)event.getSource();
			if (source.equals(attrComboBox))
			{
				events = getEvents(((NameIDObj)attrComboBox.getSelectedItem()).id);
				updateGui();
			}
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent event) 
	{
		JSlider source = (JSlider)event.getSource();
		if (source.equals(sliderIterations))
			currentItertions.setText("Maximum iterations = " + Integer.toString(Math.max(1, sliderIterations.getValue())));
		else if (source.equals(sliderPast))
			currentPast.setText("Past events = " + Integer.toString(sliderPast.getValue()));
		else if (source.equals(sliderFuture))
			currentFuture.setText("Future events = " + Integer.toString(sliderFuture.getValue()));
	}
	
	// Get a list of times when events happen
	private List<Double> getEvents(int eventType)
	{
		if (eventType==0)
			return dynView.getNetwork().getEventTimeList();
		else
			return dynView.getNetwork().getEventTimeList(((NameIDObj)attrComboBox.getSelectedItem()).name);
	}
	
	// Compute when events trigger layout iterations based on the given iteration rate and
	// merge events that don't trigger any iteration.
	private List<Double> filterEvents(List<Double> events, double iterationRate)
	{
		double itertions = 0;
		double increment = 0;
		for (int t=events.size()-1;t>0;t--)
		{
			itertions = iterationRate*(events.get(t)-events.get(t-1));
			if (increment<1 && Math.floor(itertions)==0)
			{
				increment = increment+itertions-Math.floor(itertions);
				events.remove(t-1);	
			}
			else
				increment = 0;
		}
		return events;
	}
	
	private double getMaxDiff(List<Double> eventList)
	{
		double max = Double.NEGATIVE_INFINITY;
		for (int i=0; i<eventList.size()-1; i++)
			max = Math.max(max,eventList.get(i+1)-eventList.get(i));
		if (max==Double.NEGATIVE_INFINITY)
			return 0;
		else 
			return max;
	}
	
	private void updateGui()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				labelTablePast.clear();
				labelTablePast.put(new Integer( 0 ),new JLabel("0") );
				labelTablePast.put(new Integer( events.size() ),new JLabel(Integer.toString(events.size())) );
				sliderPast.setMaximum(events.size());
				sliderPast.setLabelTable(labelTablePast);
				sliderPast.setMajorTickSpacing(events.size()/4);
				sliderPast.setPaintTicks(true);
				sliderPast.setPaintLabels(true);
				
				labelTableFuture.clear();
				labelTableFuture.put(new Integer( 0 ),new JLabel("0") );
				labelTableFuture.put(new Integer( events.size() ),new JLabel(Integer.toString(events.size())) );
				sliderFuture.setMaximum(events.size());
				sliderFuture.setLabelTable(labelTablePast);
				sliderFuture.setMajorTickSpacing(events.size()/4);
				sliderFuture.setPaintTicks(true);
				sliderFuture.setPaintLabels(true);
			}
		});
	}
	
}
