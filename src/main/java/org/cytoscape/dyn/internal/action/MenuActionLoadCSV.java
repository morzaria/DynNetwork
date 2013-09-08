/**
 * 
 */
package org.cytoscape.dyn.internal.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.dyn.internal.io.load.LoadDynFactory;
import org.cytoscape.dyn.internal.io.load.LoadDynLayoutFactoryImpl;
import org.cytoscape.dyn.internal.io.load.LoadDynNetworkViewFactoryImpl;
import org.cytoscape.dyn.internal.io.load.LoadDynVizMapFactoryImpl;
import org.cytoscape.dyn.internal.io.read.csv.DynHandlerCSV;
import org.cytoscape.dyn.internal.layout.model.DynLayoutFactory;
import org.cytoscape.dyn.internal.model.DynNetwork;
import org.cytoscape.dyn.internal.model.DynNetworkFactory;
import org.cytoscape.dyn.internal.model.DynNetworkManager;
import org.cytoscape.dyn.internal.view.gui.DynCytoPanel;
import org.cytoscape.dyn.internal.view.gui.DynCytoPanelTask;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewFactory;
import org.cytoscape.dyn.internal.view.model.DynNetworkViewManager;
import org.cytoscape.dyn.internal.vizmapper.model.DynVizMapFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableSetter;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author Jimmy
 * 
 */
public class MenuActionLoadCSV<T, C> extends AbstractCyAction {
	private static final long serialVersionUID = 1L;

	private final CySwingApplication desktopApp;
	private final CyApplicationManager appManager;

	private final CytoPanel cytoPanelWest;
	private final DynCytoPanel<T, C> myDynPanel;
	private final TaskManager<T, C> taskManager;
	private final DynNetworkManager<T> dynNetworkManager;
	private final DynNetworkViewManager<T> dynNetworkViewManager;
	private final DynNetworkFactory<T> dynNetworkFactory;
	private final DynNetworkViewFactory<T> dynNetworkViewFactory;
	private final DynLayoutFactory<T> dynLayoutFactory;
	private final DynVizMapFactory<T> vizMapFactory;
	private final FileUtil fileUtil;
	private final StreamUtil streamUtil;
	private final TunableSetter tunableSetterServiceRef;

	/**
	 * @param desktopApp
	 * @param appManager
	 * @param myDynPanel
	 * @param taskManager
	 * @param dynNetworkManager
	 * @param dynNetworkViewManager
	 * @param dynNetworkFactory
	 * @param dynNetworkViewFactory
	 * @param dynLayoutFactory
	 * @param vizMapFactory
	 * @param fileUtil
	 * @param streamUtil
	 * @param tunableSetterServiceRef
	 */
	public MenuActionLoadCSV(CySwingApplication desktopApp,
			CyApplicationManager appManager, DynCytoPanel<T, C> myDynPanel,
			TaskManager<T, C> taskManager,
			DynNetworkManager<T> dynNetworkManager,
			DynNetworkViewManager<T> dynNetworkViewManager,
			DynNetworkFactory<T> dynNetworkFactory,
			DynNetworkViewFactory<T> dynNetworkViewFactory,
			DynLayoutFactory<T> dynLayoutFactory,
			DynVizMapFactory<T> vizMapFactory, FileUtil fileUtil,
			StreamUtil streamUtil, TunableSetter tunableSetterServiceRef) {
		super("CSV File...");
		this.setPreferredMenu("File.Import.Dynamic Network");
		this.desktopApp = desktopApp;
		this.appManager = appManager;
		this.cytoPanelWest = desktopApp.getCytoPanel(CytoPanelName.WEST);
		this.myDynPanel = myDynPanel;
		this.taskManager = taskManager;
		this.dynNetworkManager = dynNetworkManager;
		this.dynNetworkViewManager = dynNetworkViewManager;
		this.dynNetworkFactory = dynNetworkFactory;
		this.dynNetworkViewFactory = dynNetworkViewFactory;
		this.dynLayoutFactory = dynLayoutFactory;
		this.vizMapFactory = vizMapFactory;
		this.fileUtil = fileUtil;
		this.streamUtil = streamUtil;
		this.tunableSetterServiceRef = tunableSetterServiceRef;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		File file = fileUtil.getFile(desktopApp.getJFrame(),
				"Load Dynamic Network", FileUtil.LOAD, getFilters());
		
		CSVReader csvReader = null;
		
		try {
			csvReader = new CSVReader(new FileReader(file.getAbsolutePath()));
			DynHandlerCSV<T> csvHandler = new DynHandlerCSV<T>(dynNetworkFactory, dynNetworkViewFactory, dynLayoutFactory, vizMapFactory, csvReader, file);
			csvHandler.readNetwork();
			
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Could not open the file!");
		}
		
		LoadDynFactory<T> loadViewFactory = new LoadDynNetworkViewFactoryImpl<T>(appManager,dynNetworkManager,dynNetworkViewFactory);
    	LoadDynFactory<T> loadLayoutFactory = new LoadDynLayoutFactoryImpl<T>(appManager,dynNetworkViewManager,dynLayoutFactory);
    	LoadDynFactory<T> loadvizMapFactory = new LoadDynVizMapFactoryImpl<T>(appManager,dynNetworkViewManager,vizMapFactory);
    	
    	Task loadViewTask = loadViewFactory.creatTaskIterator().next();
    	Task loadLayoutTask = loadLayoutFactory.creatTaskIterator().next();
    	Task loadvizMapTask = loadvizMapFactory.creatTaskIterator().next();
    	Task loadPanelTask = new DynCytoPanelTask<T,C>(myDynPanel, cytoPanelWest);
    	TaskIterator iterator = new TaskIterator(loadViewTask,loadLayoutTask,loadvizMapTask,loadPanelTask);
    	
    	taskManager.execute(iterator);
	}

	private List<FileChooserFilter> getFilters() {
		List<FileChooserFilter> filters = new ArrayList<FileChooserFilter>();
		filters.add(new FileChooserFilter("XGMML", "xgmml"));
		filters.add(new FileChooserFilter("CSV", "csv"));
		return filters;
	}
	

}
