package org.cytoscape.dyn.internal.layout.task;

import java.util.List;

import org.cytoscape.work.Tunable;

/**
 * 
 * <code> ForceDirectedLayoutContext </code> encodes the parameters for the Dynamic Perfuse Layout. 
 * 
 * @author Sabina Sara Pfister
 *
 */
public class ForceDirectedLayoutContext
{	
	/**
	 * Event type.
	 */
	@Tunable(description="Event type")
	public int m_event_type = 0;
	
	/**
	 * Attribute name.
	 */
	@Tunable(description="Attribute name")
	public String m_attribute_name;
	
	/**
	 * Event list.
	 */
	public  List<Double> m_event_list;
	
	/**
	 * Maximum number of iterations.
	 */
	@Tunable(description="Maximum number of iterations")
	public int m_max_iterations = 100;
	
	/**
	 * Iteration rate.
	 */
	public double m_iteration_rate;
	
	/**
	 * Number of past events to consider.
	 */
	@Tunable(description="Number of past events")
	public int m_past_events = 0;
	
	/**
	 * Number of future events to consider.
	 */
	@Tunable(description="Number of future events")
	public int m_future_events = 0;
	
	/**
	 * Cancel layout algorithm execution.
	 */
	@Tunable(description="Cancel algorithm")
	public boolean m_cancel = true;

}
