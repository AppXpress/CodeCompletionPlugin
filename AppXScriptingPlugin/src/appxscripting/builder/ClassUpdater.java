package appxscripting.builder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import appxscripting.Constants;
import appxscripting.connections.Connections;
import appxscripting.preferences.PreferencesStorage;

public class ClassUpdater extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		System.out.println("Called updater");
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window
					.getSelectionService().getSelection();
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IAdaptable) {
				IProject project = (IProject) ((IAdaptable) firstElement)
						.getAdapter(IProject.class);
				if (project != null)
					runUpdateJob(project);
			}
		}
		return null;
	}

	private void runUpdateJob(final IProject project) {
		//TODO: needs error handling
		Job job = new Job("Update Job") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// set total number of work units
				monitor.beginTask("Updating script files", 100);
				makeUpdateRequest(project);
				monitor.worked(100);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private void makeUpdateRequest(IProject project) {
		String mainObjJson = null;
		ArrayList<String> supportObjJSonArr = new ArrayList<String>();
		try {
			PreferencesStorage ps = new PreferencesStorage();
			String urlStr = ps.getStoredPref(Constants.PREFS_URL_KEY);
			String versionStr = ps
					.getStoredPref(Constants.PREFS_API_VERSION_KEY);
			String mainObject = ps.getStoredPref(Constants.PREFS_MAIN_OBJ_KEY);
			String dataKey = ps.getStoredPref(Constants.PREFS_DATA_KEY);
			Connections.authStr = ps.getStoredPref(Constants.PREFS_AUTH_KEY);
			ArrayList<String> supportObjects = ps
					.getArrayPrefs(Constants.PREFS_SUP_OBJ_KEY);
			mainObjJson = Connections.sendGet(urlStr, versionStr, mainObject,
					dataKey);
			System.out.println("Main object : " + mainObjJson);
			for (String s : supportObjects) {
				s = s.trim();//remove any whitespaces
				String supObjJson = Connections.sendGet(urlStr, versionStr, s,
						dataKey);
				System.out.println("Support object : " + supObjJson);
				if (supObjJson != null)
					supportObjJSonArr.add(supObjJson);
			}
			if (mainObjJson != null)
				updateScriptFiles(project, mainObjJson, supportObjJSonArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateScriptFiles(IProject project, String mainObjJson,
			ArrayList<String> supportObjJSonArr) {
		ScriptGenerator sg = new ScriptGenerator(project);
		sg.buildScripts(mainObjJson, supportObjJSonArr, true);
	}

}
