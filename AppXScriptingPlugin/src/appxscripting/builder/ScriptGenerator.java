package appxscripting.builder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class ScriptGenerator {
	
	private static String SCRIPT_FILE_NAME = "platformModuleScript.js";
	ArrayList<String> parsedObjectList = null;
	
	public ScriptGenerator(){
		parsedObjectList = new ArrayList<String>();
	}
	
    /**
     * Create script files in the project
     *
     * @param newProject
     * @throws IOException 
     * @throws CoreException
     */    
    private void addScriptFile(IProject newProject, String fileData, String filePath){
    	InputStream stream;
		try {
			stream = new ByteArrayInputStream(fileData.getBytes("UTF-8"));
			String createdFilePath = filePath;
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

	public void buildScripts(IProject project, String mainDocJSON,
			ArrayList<String> supportDocJSONArr) {// build single library file
		
		if(mainDocJSON != null){
			
		String printSkeletonScript = writeSkeletonScriptForDocument(mainDocJSON);
		addScriptFile(project, printSkeletonScript, SCRIPT_FILE_NAME);

		createJSLibrariesFromJSON(mainDocJSON, project);

		}

		// create js libs from support objects
		for (String supportDocJSON : supportDocJSONArr) {
			createJSLibrariesFromJSON(supportDocJSON, project);
		}

	}

	private String writeSkeletonScriptForDocument(String docJSON) {
		JSONObject jsonObject = JSONObject.fromObject(docJSON);
		JSONObject dictionaryJson = jsonObject.getJSONObject("DataDictionary");
		String objName = dictionaryJson.getString("type");
		ArrayList<String> eventList = new ArrayList<String>();
		if( objName.charAt(0) == "$".toCharArray()[0] ){//is custom object
			//custom object definition
			objName = objName.substring(1, objName.length());
			eventList.add("objectOnCreateFunction");
			eventList.add("objectOnSaveFunction");
		}else{
			eventList.add("docOnSaveFunction");
			eventList.add("docOnValidateFunction");
			eventList.add("docOnActivateFunction");
			eventList.add("docOnCloseFunction");
			eventList.add("docOnCancelFunction");
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < eventList.size(); i++) {
			sb.append("/**\n");
			sb.append("* @param {"+objName+"} obj\n");
			sb.append("*/\n");
			sb.append("function " + eventList.get(i) + "(obj, event, params){\n");
			sb.append("\t//TODO: obj property get/set goes here..\n\n");
			sb.append("\tProviders.getPersistenceProvider().save(obj);\n");
			sb.append("}\n");
		}
		return sb.toString();

	}

	private String createJSLibrariesFromJSON(String docJSON, IProject project) {
		JSONObject jsonObject = JSONObject.fromObject(docJSON);
		JSONObject dictionaryJson = jsonObject.getJSONObject("DataDictionary");
		ArrayList<String> listofObjTypes = new ArrayList<String>();
		listofObjTypes.addAll(dictionaryJson.keySet());

		StringBuilder sb = new StringBuilder();

		for (int i = 1; i < listofObjTypes.size(); i++) {// skip first node
															// which is generic
			
			String objType = listofObjTypes.get(i);
			if( objType.charAt(0) == "$".toCharArray()[0] )//is custom object
				objType = objType.substring(1, objType.length());												// 'type'
			System.out.println(objType);
			String printStr = parseObject(
					dictionaryJson.getJSONObject(listofObjTypes.get(i)),
					objType, listofObjTypes);
			if(printStr != null)
				addScriptFile(project, printStr, "classes/"+objType+".js");
		}

		return sb.toString();
	}
	
	private String getObjectTypeFromJSON(String docJSON){
		JSONObject jsonObject = JSONObject.fromObject(docJSON);
		JSONObject dictionaryJson = jsonObject.getJSONObject("DataDictionary");
		String type = dictionaryJson.getString("type");
		if( type.charAt(0) == "$".toCharArray()[0] )//is custom object
			type = type.substring(1, type.length());
		return type;
	}

	private String parseObject(JSONObject mainObject, String objType,
			ArrayList<String> listOfOBjTypes) {
		String objName = objType;
		// check if object already parsed, if so skip
		if (parsedObjectList.contains(objType))
			return null;

		parsedObjectList.add(objType);// keep record of parsed objects
		StringBuilder sb = new StringBuilder();
		sb.append("function " + objName + "(){");
		sb.append("\n");
		ArrayList<String> listofObjProperties = new ArrayList<String>();
		listofObjProperties.addAll(mainObject.keySet());
		//keep track of party objects for COs in a separate list, since their definition is different
		ArrayList<String> coPartyList = new ArrayList<String>();
		
		for (String key : listofObjProperties) {
			// console.log("KEY : "+JSON.stringify(key));
			JSONObject currentJSONObject = mainObject.getJSONObject(key);
			if (currentJSONObject.getString("type").equalsIgnoreCase("TEXT")) {
				if(key.split("\\.").length == 2){//is a CO party field
					String indexKey = key.split("\\.")[0];
					if(!coPartyList.contains(indexKey)){
						sb.append("\tthis." + indexKey + " = new Party();");
						sb.append("\n");
						coPartyList.add(indexKey);
					}
				}else{
					sb.append("\tthis." + key + " = '';");
					sb.append("\n");
				}
			} else if (currentJSONObject.getString("type").equalsIgnoreCase(
					"INTEGER")) {
				if(key.split("\\.").length == 2){//is a CO party field
					String indexKey = key.split("\\.")[0];
					if(!coPartyList.contains(indexKey)){
						sb.append("\tthis." + indexKey + " = new Party();");
						sb.append("\n");
						coPartyList.add(indexKey);
					}
				}else{
					sb.append("\tthis." + key + " = 0;");
				}
			} else if (currentJSONObject.getString("type").equalsIgnoreCase(
					"DECIMAL")) {
				sb.append("\tthis." + key + " = 0.0;");
				sb.append("\n");
			} else if (currentJSONObject.getString("type").equalsIgnoreCase(
					"DATE")) {
				sb.append("\tthis." + key + " = '';");
				sb.append("\n");
			} else if (currentJSONObject.getString("type").equalsIgnoreCase(
					"BOOLEAN")) {
				sb.append("\tthis." + key + " = true;");
				sb.append("\n");
			} else if (listOfOBjTypes.contains(currentJSONObject
					.getString("type"))) {
				String customObjType = currentJSONObject.getString("type");
				System.out.println(customObjType);
				String customObjName = customObjType;
				if( customObjName.charAt(0) == "$".toCharArray()[0] )
					customObjName = customObjName.substring(1, customObjName.length());
				// TODO: must handle custom object types in future
				boolean isCollection = false;
				boolean isMap = false;
				try {
					isCollection = currentJSONObject.getBoolean("isCollection");
				} catch (JSONException e) {
					//skip
				}
				try {
					isMap = currentJSONObject.getBoolean("isMap");
				} catch (JSONException e) {
					//skip
				}
				if (isCollection == true) {
					System.out.println("iscollection");
					sb.append("\tthis." + key + " = [ new " + customObjName
							+ "() ];");
					sb.append("\n");
				}else if (isMap == true) {
					System.out.println("ismap");
					sb.append("\tthis." + key + " = new Object();");//difficult to handle all cases of maps
					sb.append("\n");
				}else {
					sb.append("\tthis." + key + " = new " + customObjName
							+ "();");
					sb.append("\n");
				}
			} else {
				System.out.println("Type '" + key + "' not defined.");
				sb.append("\tthis." + key + " = new Object();");//unkown type
				sb.append("\n");
			}
		}
		sb.append("}\n");		
		String printStr = sb.toString();
		// System.out.println(printStr);
		return printStr;
	}


	
}
