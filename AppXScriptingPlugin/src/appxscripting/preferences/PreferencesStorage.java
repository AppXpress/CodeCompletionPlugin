package appxscripting.preferences;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

public class PreferencesStorage {
	
	private static String prefStorageName = "com.appxscripting.prefs";
	//private static String prefSubStorageName = "com.appxscripting.sub.prefs";
	private IEclipsePreferences prefs = null;
	
	public PreferencesStorage(){
		 prefs =  InstanceScope.INSTANCE
				  .getNode(prefStorageName);
	}
	
	public String getStoredPref(String key){
		String result = null;
		result = prefs.get(key, null);
		return result;
	}
	
	public void saveStoredPref(String key, String value){
		this.prefs.put(key, value);
			  // forces the application to save the preferences
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
	
	public void saveArrayPrefs(String key, ArrayList<String> prefsArr){
		String prefsArrStr = prefsArr.toString();
		prefsArrStr = prefsArrStr.replace("[", "");
		prefsArrStr = prefsArrStr.replace("]", "");
		System.out.println(prefsArrStr);//save as comma separated string
		saveStoredPref(key, prefsArrStr);
	}
	
	public ArrayList<String> getArrayPrefs(String key){
		String prefsArrStr = getStoredPref(key);
		String[] prefsArr = prefsArrStr.split(",");
		return  new ArrayList<String>( Arrays.asList(prefsArr));
	}

}
