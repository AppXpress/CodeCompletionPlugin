/**
 * Providers are Java classes on the GT Nexus backend that are made available via the scripting engine. 
 * The Javascript class below provides a skeleton Providers object which can be accessed via platformModuleScript for intellisense.
 */

function ProviderObject(){

	var SessionProvider = function(){
		this.getCurrentUserId = function(){
			return "";//return type is String
		};
		
		this.getCurrentOrgId = function(){
			return "";//return type is String
		};
		
		this.getCurrentOrgName = function(){
			return "";
		};
		
		/**
		 * @returns {Array}
		 */
		this.getCurrentOrgRoles = function(){
			var orgRoles = [""];
			return orgRoles;
		};
	};
	
	this.getSessionProvider = function(){
		return new SessionProvider();
	};

	var ObjectFactoryProvider = function(){
		/**
		* @param {String} objectType - The object type to create
		* @param {Number} version - API version
		* @returns {Object} - Returns a new object of the type specified
		*/
		this.newObject = function (objectType, version){
			return new Object();
		};
		
	};
	
	this.getObjectFactoryProvider = function(){
		return new ObjectFactoryProvider();
	};
	
	var PlatformQuery = function(){
		
		/**
		* @param {String} key
		* @param {String} value
		* @returns {PlatformQuery} - Returns the query object
		*/
		this.addParameter = function(key, value){
			return this;
		};
		
		/**
		* @param {String} oql - OQL string
		* @returns {PlatformQuery} - Returns the query object
		*/
		this.setOQL = function(oql){
			return this;
		};
		
	};
	
	var ViewAccessProvider = function(){
		/**
		 * @param {String} orgId
		 */
		this.grantRuntimeAccess = function(orgId){
			
		};
	};
	
	this.getViewAccessProvider = function(){
		return new ViewAccessProvider();
	};
	
	var DictionaryProvider = function(){
		/**
		* @param {String} objectType - The object type
		* @returns {String} - Returns a json representation of the object definition
		*/
		this.getDataDictionary = function(objectType){
			return "";
		};
		
		/**
		* @param {String} objectType - The object type
		* @param {Number} version - API version
		* @returns {String} - Returns a json representation of the object definition
		*/
		this.getDataDictionary = function(objectType, version){
			return "";
		};
	};
	
	this.getDictionaryProvider = function(){
		return new DictionaryProvider();
	};
	
	var QueryProvider = function(){
		
		/**
		* @param {String} objectType - The object type to query
		* @param {Number} version - API version
		* @returns {PlatformQuery} - Returns a new object of the type specified
		*/
		this.createQuery = function(objectType, version){
			return new PlatformQuery();
		};
		
		/**
		* @param {PlatformQuery} query - The query object created using createQuery
		* @returns {Array} - Returns a new object of the type specified
		*/
		this.execute = function(query){
			return [];
		};
		
	};
	
	this.getQueryProvider = function(){
		return new QueryProvider();
	};
	
	this.getPersistenceProvider = function(){
		return new PersistenceProvider();
	};
	
	
	function PlatformFetchRequest(){
	
		//Commented out because javascript doesnt support method overloading
//		/**
//		 * @param {String} key
//		 * @param {String} value
//		 * @returns {PlatformFetchRequest}
//		 */
//		this.addParameter = function(key,value){
//			return this;
//		};
//		
//		/**
//		 * @param {Number} apiVersion
//		 * @returns {PlatformFetchRequest}
//		 */
//		this.apiVersion = function(apiVersion){
//			return this;
//		};

		/**
		 * @param {String} resource - For example to retrieve attachments details: resource("attachments")
		 * @param {String} resourceId
		 * @returns {PlatformFetchRequest}
		 */
		this.resource = function(resource,resourceId){
			return this;
		};
		
		this.execute = function(){
			return null;
		};
	};

	function PersistenceProvider(){
		/**
		 * @param {Object} obj
		 */
		this.save = function(obj){ return null; };
		
		/**
		 * @param {Object} obj - Object on which to perform transition
		 * @param {String} action - Action for object transition
		 */
		this.processAction = function(obj,action){ return null; };
		
		/**
		 * @param {Object} obj
		 * @returns {Boolean} - Returns true if the object passed in is on the Save agenda; it has been called to be saved and has not yet been saved.
		 */
		this.isMarkedForSave = function(){ return true; };
		
		/**
		 * @returns {String}
		 */
		this.getStateAsLoaded = function(){ return "";};
		
		//TODO: need to handle function overloading
		/**
		 * @param {String} objectType
		 * @param {Number} version
		 * @param {String} id
		 * @returns {PlatformFetchRequest}
		 */
		this.createFetchRequest = function(objectType,version,id){
			return new PlatformFetchRequest();
		};
	};
	
	var MessageBuilder = function(){
		/**
		 * @param {String} msgKey -  MsgKey is used in the localization tab to put in display text.
		 */
		this.msgKey = function(msgKey){ return this; };
		
		/**
		 * @param {String} fieldPath - FieldPath is the path of the field this error or info refers to. You should reference the field using XPath.  XPath uses “/“ to traverse the object graph.
		 */
		this.fieldPath = function(fieldPath){ return this; };
		
		
		/**
		 * @param {String} ruleId - RuleId is a user specified ID to identify this error or info.
		 */
		this.ruleId = function(ruleId){ return this; };
		
		/**
		 * @param {String} key
		 * @param {Object} value
		 */
		this.arg = function(key,value){ return this; };
		
		/**
		 * @param {Object} obj - Args for when you have multiple arguments.
		 */
		this.args = function(obj){ return this; };

		this.build = function(){ return null; };
		
		/**
		 * @returns {String} - Returns the unique msgID.
		 */
		this.msgId = function(){ return ""; };
		
		
		/**
		 * @param {Boolean} suppressible - Suppressible allows a future script or extension to mark this error or info as suppressible.
		 */
		this.suppressible = function(suppressible){ return this; };
		
		
	}; 
	
	var MessageProvider = function(){
		this.info = function(){ return new MessageBuilder();};
		this.error = function(){ return new MessageBuilder();};
	};
	
	this.getMessageProvider = function(){ return new MessageProvider();};
	
}

var Providers = new ProviderObject();

/**
 * Example of setting object type for an object fetched using a provider is given below. Set @returns to the returning object type.
 * @returns {OrderDetail}
 */
//var orderObj = Providers.getObjectFactoryProvider().newObject("OrderDetail", 310);