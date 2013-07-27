package org.cytoscape.dyn.internal.graphMetrics;

import java.net.URL;

import javax.help.HelpBroker;
import javax.help.HelpSet;


public class DynamicNetworkHelp {
	
	private HelpBroker helpBroker;
	private HelpSet newHelpSet;
	public DynamicNetworkHelp(){
		final String HELP_SET_NAME = "/help/jhelpset";
		final ClassLoader classLoader = DynamicNetworkHelp.class.getClassLoader();
		URL helpSetURL;
		try {
			helpSetURL = HelpSet.findHelpSet(classLoader, HELP_SET_NAME);
			newHelpSet = new HelpSet(classLoader, helpSetURL);
			helpBroker = newHelpSet.createHelpBroker();
		} catch (final Exception e) {
			System.out.println("Sample24: Could not find help set: \"" + HELP_SET_NAME + ".");
		}
	}
	
	public void displayHelp(){
		try{
			helpBroker.setDisplayed(true);
		}catch(Exception e){
			System.out.println("Something didn't work as I intended it to!");
		}
	}
}
