package appxscripting.connections;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connections {
	
	public static String authStr = "";//this is set from username & pwd from connection wizard page

	public static String sendGet(String url, String versionStr, String objName, String uid, String dataKey) throws Exception {
		StringBuffer response = new StringBuffer();
		
		if(uid != null){
			objName = objName + "/"+ uid;
		}
		
		String urlStr = url +"/rest/"+versionStr+"/"+objName+"?dataKey="+dataKey;
		System.out.println("URL : "+urlStr);
		try{
			URL urlConnection = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) urlConnection.openConnection();
	 
			// optional default is GET
			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization", authStr);
	
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
	
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		}catch(Exception e){
			//TODO:handle exception
			e.printStackTrace();
		}
		//print result
		System.out.println(response.toString());
 
		return response.toString();
	}
}
