package edu.uark.spARK;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;


public class JSONQuery extends AsyncTask<String, Void, String> {

		private AsyncResponse delegate = null;
		private InputStream is = null;
	    static JSONObject jObj = null;
		static String json = "";
		public boolean threadReady = true;
	
		public interface AsyncResponse {
        	void processFinish(JSONObject result);
    	}
	
		public JSONQuery (AsyncResponse delegate) {
			this.delegate = delegate;
		}
	    
	    protected String doInBackground(String... values) {
		    try {
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new HttpPost(values[0]);
		        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		       
		        for (int i=1; i<values.length;i++) {
		        	nameValuePairs.add(new BasicNameValuePair("value" + i, values[i]));
		        }
		        
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        
		        is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder(); 
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
				jObj = new JSONObject(json);
		    } catch (Exception e) {
		        Log.e("log_tag", "Error in http connection " + e.toString());
		    }
		  return null;
	    }

	    protected void onPostExecute(String result) {
	    	delegate.processFinish(jObj);
	    }
}
