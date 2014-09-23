package appxscripting.builder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class ScriptGenerator {
	
	private static String SCRIPT_FILE_NAME = "platformModuleScript.js";
	ArrayList<String> parsedObjectList = null;
	IProject project = null;
	
	public ScriptGenerator(IProject p){
		parsedObjectList = new ArrayList<String>();
		project = p;
	}
	
    /**
     * Create script files in the project
     *
     * @throws IOException 
     * @throws CoreException
     */    
    private void addScriptFile(String fileData, String filePath){
    	InputStream stream;
		try {
			stream = new ByteArrayInputStream(fileData.getBytes("UTF-8"));
			String createdFilePath = filePath;
	    	IFile file = project.getFile(createdFilePath);
	    	if(file.exists())
	    		file.delete(true, null);
			file.create( stream, IResource.FORCE, null );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public void buildScripts(String mainDocJSON,
			ArrayList<String> supportDocJSONArr, String refDocJSON, boolean isUpdate) {// build single library file
		
		if(mainDocJSON != null){
			
		if(!isUpdate){	
			String printSkeletonScript = writeSkeletonScriptForDocument(mainDocJSON);
			addScriptFile( printSkeletonScript, SCRIPT_FILE_NAME);
		}

		createJSLibrariesFromJSON(mainDocJSON);

		}
		
		if(refDocJSON != null){
			JSONObject refDocObj = JSONObject.fromObject(refDocJSON);
			String refDocJSONStr = refDocObj.toString(3);
			//refDocJSONStr.replaceAll("\\{", "\\{\\n");
			addScriptFile( refDocJSONStr, "classes/REF.json");
		}

		// create js libs from support objects
		for (String supportDocJSON : supportDocJSONArr) {
			createJSLibrariesFromJSON(supportDocJSON);
		}
		
		//create default scripts
		createPartyMapClass();
		createDateMapClass();
		createItemIdentifierMap();
		createItemDateMap();
		createItemDescriptorMap();
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

	private String createJSLibrariesFromJSON(String docJSON) {
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
				addScriptFile(printStr, "classes/"+objType+".js");
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
					"INTEGER") || currentJSONObject.getString("type").equalsIgnoreCase(
							"NUMBER")) {
				if(key.split("\\.").length == 2){//is a CO party field
					String indexKey = key.split("\\.")[0];
					if(!coPartyList.contains(indexKey)){
						sb.append("\tthis." + indexKey + " = new Party();");
						sb.append("\n");
						coPartyList.add(indexKey);
					}
				}else{
					sb.append("\tthis." + key + " = 0;");
					sb.append("\n");
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
					//known map types..
					//party - ok
					//order date - ok
					//reference - orderterms - not added
					//additionalDocumentRequired - not added
					//itemIdentifier - base item - ok
					//itemDescriptor - base item - ok
					//item date - base item - ok
					//party - identification - not added
					//party reference - not added
					System.out.println("ismap");
					if(customObjName.equalsIgnoreCase("party")){//is a map of party objects
						sb.append("\tthis." + key + " = new PartyMap();");
						sb.append("\n");
					}
					else if(customObjName.equalsIgnoreCase("orderDate") || customObjName.equalsIgnoreCase("invoiceDate")){
						sb.append("\tthis." + key + " = new DateMap();");
						sb.append("\n");
					}
					else if(customObjName.equalsIgnoreCase("itemIdentifier")){
						sb.append("\tthis." + key + " = new ItemIdentifierMap();");
						sb.append("\n");
					}
					else if(customObjName.equalsIgnoreCase("itemDate")){
						sb.append("\tthis." + key + " = new ItemDateMap();");
						sb.append("\n");
					}
					else if(customObjName.equalsIgnoreCase("itemDescriptor")){
						sb.append("\tthis." + key + " = new ItemDescriptorMap();");
						sb.append("\n");
					}
					else{
						sb.append("\tthis." + key + " = new Object();");//difficult to handle all cases of maps
						sb.append("\n");
					}
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
	
	private void createPartyMapClass(){
		String[] partyRoleArray = {"AdditionalParty","Beneficiary","BillOfLadingIssuer","BillTo","BondHolder","BookingParty","Buyer","BuyersAgent","Carrier","Consignee","Consignor","Consolidator","ContainerStuffingLocation","CounterParty","CoverageProvider","CustomsBroker","FilingImporter","FilingProvider","FinalDestination","FinalReceiver","FinanceProvider","Importer","InspectionCompany","IntermediateDestination","IssuedTo1","IssuedTo2","IssuedTo3","IssuedTo4","IssuedTo5","LogisticsProvider","Message","NotifyParty1","NotifyParty2","OriginOfGoods","Owner","Payer","PaymentProvider","PrintServiceProvider","ReceivedBy","Receiver","RepresentedBeneficiary","RepresentedPayer","RequestedBy","Requester","Seller","SellersAgent","Sender","ShipTo","ShipmentDestination","TransferTo"};
		
		StringBuilder sb = new StringBuilder();
		sb.append("function PartyMap(){\n");
		for (String partyRole : partyRoleArray) {
			sb.append("\tthis."+partyRole+" = [ new Party() ];\n");
		}
		sb.append("}\n");
		
		addScriptFile( sb.toString(), "classes/PartyMap.js");
	}
	
	private void createDateMapClass(){
		String[] dateTypeArray = {"CancelAfter","Earliest","Issue","Latest","OfferExpiry"};
		
		StringBuilder sb = new StringBuilder();
		sb.append("function DateMap(){\n");
		for (String dateRole : dateTypeArray) {
			sb.append("\tthis."+dateRole+" = '';\n");
		}
		sb.append("}\n");
		
		addScriptFile( sb.toString(), "classes/DateMap.js");
	}
	
	private void createItemIdentifierMap(){
		String[] itemIdentifierMap = {"BuyerNumber", "CountryOfOrigin", "ItemSequenceNumber", "Level", "LotNumber", "QuotaCategory", "SellerNumber", "ShortDescription", "SkuNumber", "UpcNumber"};
		StringBuilder sb = new StringBuilder();
		sb.append("function ItemIdentifierMap(){\n");
		for (String itemId : itemIdentifierMap) {
			sb.append("\tthis."+itemId+" = '';\n");
		}
		sb.append("}\n");
		
		addScriptFile( sb.toString(), "classes/ItemIdentifierMap.js");
	}
	
	private void createItemDateMap(){
		String[] itemDateMap = {"EarliestDate", "LatestDate"};
		StringBuilder sb = new StringBuilder();
		sb.append("function ItemDateMap(){\n");
		for (String item : itemDateMap) {
			sb.append("\tthis."+item+" = '';\n");
		}
		sb.append("}\n");
		
		addScriptFile( sb.toString(), "classes/ItemDateMap.js");
	}

	private void createItemDescriptorMap(){
		String[] itemMap = {"LongDescription"};
		StringBuilder sb = new StringBuilder();
		sb.append("function ItemDescriptorMap(){\n");
		for (String item : itemMap) {
			sb.append("\tthis."+item+" = '';\n");
		}
		sb.append("}\n");
		
		addScriptFile( sb.toString(), "classes/ItemDescriptorMap.js");
	}

	
}
