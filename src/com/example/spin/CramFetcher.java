package com.example.spin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.content.DialogInterface.OnCancelListener;

public class CramFetcher extends ListActivity {
	/**
	 * @link URL*/

	private ProgressDialog pDialog;
	 
    // URL to get contacts JSON
	private static String tokens = "https://api.cram.com/oauth2/token/";
    private static String url = "https://api.Cram.com/v2/search/sets";
    private static String authorize = "http://Cram.com/oauth2/authorize/?client_id=297248cf902970966895aa449946fabf&scope=read&state=oAth2spin&redirect_uri=spin://oauthresponse&response_type=code";
    // JSON Node names
    private static final String TAG_CONTACTS = "contacts";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_QUESTIONS = "questions";
 
    // contacts JSONArray
    JSONArray search = null;
 
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;
	
    private boolean isTaskCancelled = false;
    private Button download;
    
    private String json;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cram);
		
		String str_cram= "Download sets from server";
		((TextView)findViewById (R.id.mainCram)).setText (str_cram);
		
		str_cram="Download";
		((Button)findViewById (R.id.buttonCram)).setText (str_cram);
		
		contactList = new ArrayList<HashMap<String, String>>();
		
		//token
		SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
	    
	    //date
	    Date date = new Date(System.currentTimeMillis());
		long token_date=pref.getLong("expiry", 0);
		if(token_date==0){
			Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorize));
			startActivity(viewIntent);
		}
		else if(token_date<=date.getTime()){
			//TODO request refresh token
			String refresh_t = pref.getString("refresh", null);
			refresh(refresh_t);
		}
		else{
			String token = pref.getString("token", null);
			// Calling async task to get json
			new GetContacts().execute(token);
		}  
	}
	 
	@Override
	protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		
		if(uri != null && uri.toString().startsWith("spin://oauthresponse")){
			String error = null;
			error = uri.getQueryParameter("error");
			if(!(error==null)){
				Toast.makeText(CramFetcher.this,
		          	     "You didn't authorize application!",
		          	     Toast.LENGTH_LONG).show();
				CramFetcher.this.onDestroy();
			}
			else{
				String state = uri.getQueryParameter("state");
				if(!state.equalsIgnoreCase("oAth2spin")){
					Toast.makeText(CramFetcher.this,
							"Problem with server authentification!",
							Toast.LENGTH_LONG).show();
				}
				else{
					String code = uri.getQueryParameter("code");
					try {
						new GetToken().execute("access",code).get();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					CramFetcher.this.finish();
					Intent back = new Intent(CramFetcher.this,CramFetcher.class);
					startActivity(back);
					CramFetcher.this.onDestroy();
				}
			}
		}
		CramFetcher.this.finish();		
	}
	
	private void refresh(String token){
		try {
			new GetToken().execute("refresh",token).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CramFetcher.this.finish();
		Intent back = new Intent(CramFetcher.this,CramFetcher.class);
		startActivity(back);
		CramFetcher.this.onDestroy();
	}
	
	protected void noData() throws InterruptedException{
		
    	Toast.makeText(CramFetcher.this,
          	     "Coudn't access data!",
          	     Toast.LENGTH_LONG).show();
    	Thread.currentThread();
		Thread.sleep(1500);
		startActivity(new Intent(CramFetcher.this,MainActivity.class));
	}
	
	private class GetToken extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... code) {
            // Creating service handler class instance
        	String access_t=null,refresh_t=null,time_t=null;
        	int expiry_t=0;
            ServiceHandler sh = new ServiceHandler();
            List<NameValuePair>parametres = new ArrayList<NameValuePair>();
            if(code[0].equalsIgnoreCase("access")){
            	parametres.add(new BasicNameValuePair("code",code[1]));
            	parametres.add(new BasicNameValuePair("grant_type","authorization_code"));
            }
            else if(code[0].equalsIgnoreCase("refresh")){
            	parametres.add(new BasicNameValuePair("refresh_token",code[1]));
            	parametres.add(new BasicNameValuePair("grant_type","refresh_token"));
            	parametres.add(new BasicNameValuePair("scope","read"));
            }
            
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(tokens, ServiceHandler.POST,parametres,null);
            json=jsonStr;
            Log.d("Response: ", "> " + jsonStr);
 
            if (jsonStr != null) {
                try {
                	JSONObject token_obj = new JSONObject(jsonStr);
    				if(token_obj.has("error")){
    					Toast.makeText(CramFetcher.this,
    			          	     "Error while authenticating token, try again!",
    			          	     Toast.LENGTH_LONG).show();
    					CramFetcher.this.onDestroy();
                    }
    				else{
    					access_t=token_obj.getString("access_token");
        				refresh_t=token_obj.getString("refresh_token");
        				time_t=token_obj.getString("expires_in");
        				expiry_t = Integer.valueOf(time_t);
        				SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        			    Editor editor = pref.edit();
        			    Date date = new Date(System.currentTimeMillis());
        				editor.putString("token", access_t); // Storing string
        				long t=date.getTime();
        				editor.putLong("expiry", t+expiry_t); // Storing long
        				editor.putString("refresh", refresh_t);
        				editor.commit(); // commit changes
    				}
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } 
            else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
			return null;
        }
    }
	
	private class GetContacts extends AsyncTask<String, Void, Void> {

        public void cancelTask(){
             isTaskCancelled = true;
        }
        
        private boolean isTaskCancelled(){
            return isTaskCancelled;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CramFetcher.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);
            pDialog.show();
            pDialog.setOnCancelListener(new OnCancelListener(){

            	  public void onCancel(DialogInterface dialog) {
            	   // TODO Auto-generated method stub
            		  cancelTask();
            	   Toast.makeText(CramFetcher.this,
            	     "Download cancelled!",
            	     Toast.LENGTH_SHORT).show();
            	  }});
        }
 
        @Override
        protected Void doInBackground(String... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
 
            List<NameValuePair>parametres = new ArrayList<NameValuePair>(1);
            parametres.add(new BasicNameValuePair("qstr","sabzy"));
            parametres.add(new BasicNameValuePair("image_filter","0"));
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET,parametres,arg0[0]);
            json=jsonStr;
            if (isTaskCancelled()){
                return null;
             }
            Log.d("Response: ", "> " + jsonStr);
 
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                     
                    // Getting JSON Array node
        			if(jsonObj.has("error")){
        				return null;
        			}
                    search = jsonObj.getJSONArray("results");
 
                    // looping through All Contacts
                    for (int i = 0; i < search.length(); i++) {
                        JSONObject c = search.getJSONObject(i);
                         
                        String id = c.getString("set_id");
                        String name = c.getString("title");
                        String cards = c.getString("card_count");
 
                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        contact.put(TAG_ID, id);
                        contact.put(TAG_NAME, name);
                        contact.put(TAG_QUESTIONS, cards);
 
                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } 
            else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()){
                pDialog.dismiss();
            }
            if(json==null){
            	try{
            		noData();
            	}
            	catch(InterruptedException e){
            		Log.e("No data", "Couldn't get any data from the url");
            	}
            }
            /**
             * Updating parsed JSON data into ListView
             * */
            
            ListAdapter adapter = new SimpleAdapter(
                    CramFetcher.this, contactList,
                    R.layout.list_item, new String[] { TAG_NAME,
                            TAG_QUESTIONS }, new int[] { R.id.name, R.id.questions });
 
            setListAdapter(adapter);
            
            download = (Button)findViewById(R.id.buttonCram); //setting reference for the "START" button
            download.setOnClickListener(new View.OnClickListener(){ //creating a listener object
     			
     			@Override
     			public void onClick(View v){
     				Intent finished = new Intent(CramFetcher.this, MainActivity.class);
     				startActivity(finished);
     			}
     		});
        }
 
    }
 
}
