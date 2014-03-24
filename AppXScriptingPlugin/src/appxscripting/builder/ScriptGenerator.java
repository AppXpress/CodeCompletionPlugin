package appxscripting.builder;

import java.util.ArrayList;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class ScriptGenerator {
	
	ArrayList<String> parsedObjectList = null;
	
	public ScriptGenerator(){
		parsedObjectList = new ArrayList<String>();
	}

	public String buildLibrary(String mainDocJSON,
			ArrayList<String> supportDocJSONArr) {// build single library file
		
		StringBuilder sb = new StringBuilder();
		
		if(mainDocJSON != null){
			
		String printSkeletonScript = writeSkeletonScriptForDocument(mainDocJSON);
		sb.append(printSkeletonScript);
		
		sb.append("\n\n");
		sb.append("//////////////////////////////");
		sb.append("\n");
		sb.append("//Development Libraries//");
		sb.append("\n");
		sb.append("//////////////////////////////");
		sb.append("\n");
		// String devLibrariesSection = sb.toString();
		// System.out.print(devLibrariesSection);


			String printMainObjectLibraries = createJSLibrariesFromJSON(mainDocJSON);
			sb.append(printMainObjectLibraries);
		}

		// create js libs from support objects
		for (String supportDocJSON : supportDocJSONArr) {
			String printSupportObjectLibraries = createJSLibrariesFromJSON(supportDocJSON);
			sb.append(printSupportObjectLibraries);
		}

		//System.out.print(sb.toString());
		return sb.toString();
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
			sb.append("function " + eventList.get(i) + "(obj, event, params){\n");
			sb.append("\tvar " + objName + "Obj = new " + objName + "();\n");
			sb.append("\t" + objName + "Obj = obj;\n");
			sb.append("\t//TODO: " + objName + "Obj"
					+ " property get/set goes here..\n\n");
			sb.append("\tProviders.getPersistenceProvider().save(" + objName
					+ "Obj" + ");\n");
			sb.append("}\n");
		}
		return sb.toString();

	}

	private String createJSLibrariesFromJSON(String docJSON) {
		JSONObject jsonObject = JSONObject.fromObject(docJSON);
		JSONObject dictionaryJson = jsonObject.getJSONObject("DataDictionary");
		ArrayList<String> listofObjTypes = new ArrayList<String>();
		listofObjTypes.addAll(dictionaryJson.keySet());

		StringBuilder sb = new StringBuilder();

		for (int i = 1; i < listofObjTypes.size(); i++) {// skip first node
															// which is generic
															// 'type'
			System.out.println(listofObjTypes.get(i));
			String printStr = parseObject(
					dictionaryJson.getJSONObject(listofObjTypes.get(i)),
					listofObjTypes.get(i), listofObjTypes);
			sb.append(printStr);
		}

		return sb.toString();
	}

	private String parseObject(JSONObject mainObject, String objType,
			ArrayList<String> listOfOBjTypes) {
		String objName = objType;
		if( objName.charAt(0) == "$".toCharArray()[0] )//is custom object
			objName = objName.substring(1, objName.length());
		// check if object already parsed, if so skip
		if (parsedObjectList.contains(objType))
			return "";

		parsedObjectList.add(objType);// keep record of parsed objects
		StringBuilder sb = new StringBuilder();
		sb.append("function " + objName + "(){");
		ArrayList<String> listofObjProperties = new ArrayList<String>();
		listofObjProperties.addAll(mainObject.keySet());
		// String[] mainObjectKeys = (String[]) mainObject.keySet().toArray();
		for (String key : listofObjProperties) {
			// console.log("KEY : "+JSON.stringify(key));
			JSONObject currentJSONObject = mainObject.getJSONObject(key);
			if (currentJSONObject.getString("type").equalsIgnoreCase("TEXT")) {
				sb.append("\tthis." + key + " = '';");
			} else if (currentJSONObject.getString("type").equalsIgnoreCase(
					"INTEGER")) {
				sb.append("\tthis." + key + " = 0;");
			} else if (currentJSONObject.getString("type").equalsIgnoreCase(
					"DECIMAL")) {
				sb.append("\tthis." + key + " = 0.0;");
			} else if (currentJSONObject.getString("type").equalsIgnoreCase(
					"DATE")) {
				sb.append("\tthis." + key + " = '';");
			} else if (currentJSONObject.getString("type").equalsIgnoreCase(
					"BOOLEAN")) {
				sb.append("\tthis." + key + " = true;");
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
				}else if (isMap == true) {
					System.out.println("ismap");
					sb.append("\tthis." + key + " = new Object();");//difficult to handle all cases of maps
				}else {
					sb.append("\tthis." + key + " = new " + customObjName
							+ "();");
				}
			} else {
				System.out.println("Type '" + key + "' not defined.");
				sb.append("\tthis." + key + " = new Object();");//unkown type
			}
		}
		sb.append("}\n");
		String printStr = sb.toString();
		// System.out.println(printStr);
		return printStr;
	}


	
}
