package appxscripting.wizard;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import appxscripting.Constants;
import appxscripting.builder.ScriptGenerator;
import appxscripting.connections.Connections;
import appxscripting.preferences.PreferencesStorage;
import appxscripting.projects.CustomProjectSupport;

public class CustomProjectNewWizard extends Wizard implements INewWizard, IExecutableExtension {
	
	private WizardNewProjectCreationPage _pageOne;
	private ConnectToServerPage _pageTwo;
	private SelectScriptingPage _pageThree;
	private IConfigurationElement _configurationElement;
	
	public CustomProjectNewWizard() {
		setWindowTitle(Constants.WIZARD_TITLE);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}
	
	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
	    _configurationElement = config;
	}
	
	@Override
	public boolean canFinish() {
		
		String currentWizardPage = this.getContainer().getCurrentPage().getName();
	    SelectScriptingPage pageThree = (SelectScriptingPage)getPages()[2];
	    String mainObjectVal = pageThree.mainObjectSelection.getText();
	    System.out.println(mainObjectVal);
		if(currentWizardPage == Constants.PAGE_THREE_NAME){// && mainObjectVal) != null  && mainObjectVal.length() > 0){
			System.out.println("Can finish");
			return true;
		}
		return false;
	}

	@Override
	public boolean performFinish() {
		System.out.println("Perform finish called");
		final String name = _pageOne.getProjectName();
		URI projectCreationPath = null;
	    if (!_pageOne.useDefaults()) {//user selected a non default path
	    	projectCreationPath = _pageOne.getLocationURI();
	    }
	    final URI location = projectCreationPath;
	 
	    final String urlStr = _pageTwo.serverField.getText();
	    final String versionStr = _pageTwo.versionSelection.getText();
	    final String dataKey = _pageTwo.datakeyField.getText();
	    final String mainObject = _pageThree.mainObjectSelection.getText();
	    final ArrayList<String> supportObjects = new ArrayList<String>(Arrays.asList(_pageThree.supportList.getSelection()));
	    //add Party as a default support object
	    if(!supportObjects.contains("party"))
	    	supportObjects.add("party");
	    
	    //store plugin info for update access
	    PreferencesStorage ps = new PreferencesStorage();
	    ps.saveStoredPref(Constants.PREFS_URL_KEY, urlStr);
	    ps.saveStoredPref(Constants.PREFS_API_VERSION_KEY, versionStr);
	    ps.saveStoredPref(Constants.PREFS_DATA_KEY, dataKey);
	    ps.saveStoredPref(Constants.PREFS_MAIN_OBJ_KEY, mainObject);
	    ps.saveArrayPrefs(Constants.PREFS_SUP_OBJ_KEY, supportObjects);
	    System.out.println("Saving auth string: "+ Connections.authStr);
	    ps.saveStoredPref(Constants.PREFS_AUTH_KEY, Connections.authStr);
	    
		try {
			this.getContainer().run(true, false,new IRunnableWithProgress(){
			     public void run(IProgressMonitor monitor) {
			         monitor.beginTask("Fetching object json ...", 100);
			

			String mainObjJson = null;
			ArrayList<String> supportObjJSonArr = new ArrayList<String>();
			try {
				mainObjJson = Connections.sendGet(urlStr,versionStr, mainObject, dataKey);
				System.out.println("Main object : "+mainObjJson);
				for (String s : supportObjects) {
					String supObjJson = Connections.sendGet(urlStr, versionStr, s, dataKey);
					System.out.println("Support object : "+supObjJson);
					supportObjJSonArr.add(supObjJson);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			         
			System.out.println(location);
			IProject project = CustomProjectSupport.createProject(name, location);
			
			ScriptGenerator sg = new ScriptGenerator(project);
			sg.buildScripts(mainObjJson, supportObjJSonArr, false);
			
			monitor.done();
			}});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		BasicNewProjectResourceWizard.updatePerspective(_configurationElement);
		
		return true;
	}
	
	@Override
	public void addPages() {
	    super.addPages();
	 
	    _pageOne = new WizardNewProjectCreationPage(Constants.PAGE_ONE_NAME);
	    _pageOne.setTitle(Constants.PAGE_ONE_TITLE);
	    _pageOne.setDescription(Constants.PAGE_ONE_DESC);
	    addPage(_pageOne);
	    
	    _pageTwo = new ConnectToServerPage(Constants.PAGE_TWO_NAME);
	    _pageTwo.setTitle(Constants.PAGE_TWO_TITLE);
	    _pageTwo.setDescription(Constants.PAGE_TWO_DESC);
	    addPage(_pageTwo);
	    
	    _pageThree = new SelectScriptingPage(Constants.PAGE_THREE_NAME);
	    _pageThree.setTitle(Constants.PAGE_THREE_TITLE);
	    _pageThree.setDescription(Constants.PAGE_THREE_DESC);
	    addPage(_pageThree);
	}

}
