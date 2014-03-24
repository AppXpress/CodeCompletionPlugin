package appxscripting.projects;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class CustomProjectSupport {
	
	private static String BUNDLE_ID = "appxscripting";
	private static String DEFAULT_FILE_PATH = "res/";
	private static String README_FILE_NAME = "README";
	private static String SCRIPT_FILE_NAME = "platformModuleScript.js";
	
    /**
     * For this project we need to:
     * - create the default Eclipse project
     * - add the required project nature
     * - create the folder structure
     *
     * @param projectName
     * @param location
     * @return
     */
    public static IProject createProject(String projectName, URI location, String scriptFileStr) {
 
        IProject project = createBaseProject(projectName, location);
        try {
            addNature(project);
            addProjectFiles(project);
            addScriptFile(project, scriptFileStr);
        } catch (CoreException e) {
            e.printStackTrace();
            project = null;
        }
 
        return project;
    }
 
    /**
     * Just do the basics: create a basic project.
     *
     * @param location
     * @param projectName
     */
    private static IProject createBaseProject(String projectName, URI location) {
        // it is acceptable to use the ResourcesPlugin class
        IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
 
        if (!newProject.exists()) {
            URI projectLocation = location;
            IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());
            if (location != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {
                projectLocation = null;
            }
 
            desc.setLocationURI(projectLocation);
            try {
                newProject.create(desc, null);
                if (!newProject.isOpen()) {
                    newProject.open(null);
                }
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
 
        return newProject;
    }
 
    private static void createFolder(IFolder folder) throws CoreException {
        IContainer parent = folder.getParent();
        if (parent instanceof IFolder) {
            createFolder((IFolder) parent);
        }
        if (!folder.exists()) {
            folder.create(false, true, null);
        }
    }
 
    /**
     * Create a folder structure with a parent root, overlay, and a few child
     * folders.
     *
     * @param newProject
     * @param paths
     * @throws CoreException
     */
    private static void addToProjectStructure(IProject newProject, String[] paths) throws CoreException {
        for (String path : paths) {
            IFolder etcFolders = newProject.getFolder(path);
            createFolder(etcFolders);
        }
    }
    
    /**
     * Copy the required default files into the project
     *
     * @param newProject
     * @throws IOException 
     * @throws CoreException
     */    
    private static void addProjectFiles(IProject newProject){
    	//add README file
    	addProjectFile(newProject , README_FILE_NAME , "" );
    }
    
    /**
     * Copy the required default files into the project
     *
     * @param newProject
     * @throws IOException 
     * @throws CoreException
     */    
    private static void addProjectFile(IProject newProject, String fileName, String projectPath){
    	Bundle bundle = Platform.getBundle(BUNDLE_ID);//plugin id
    	String filePath = DEFAULT_FILE_PATH + fileName;
    	IPath location= Path.fromOSString(filePath); 
    	InputStream stream;
		try {
			stream = FileLocator.openStream( bundle, location, false );
			String createdFilePath = projectPath+fileName;
	    	IFile file = newProject.getFile(createdFilePath);
			file.create( stream, true, null );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Copy the required json files into the project
     *
     * @param newProject
     * @throws IOException 
     * @throws CoreException
     */    
    private static void addScriptFile(IProject newProject, String fileData){
    	InputStream stream;
		try {
			stream = new ByteArrayInputStream(fileData.getBytes("UTF-8"));
			String createdFilePath = SCRIPT_FILE_NAME;
	    	IFile file = newProject.getFile(createdFilePath);
			file.create( stream, true, null );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
 
    private static void addNature(IProject project) throws CoreException {
    	//add javascript as the project nature
    	final String jsProjectNature = "org.eclipse.wst.jsdt.core.jsNature";
    	
        if (!project.hasNature(jsProjectNature)) {
            IProjectDescription description = project.getDescription();
            String[] prevNatures = description.getNatureIds();
            String[] newNatures = new String[prevNatures.length + 1];
            System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
            newNatures[prevNatures.length] = jsProjectNature;
            description.setNatureIds(newNatures);
 
            IProgressMonitor monitor = null;
            project.setDescription(description, monitor);
        }
    }
 
}
