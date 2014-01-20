package com.example.spin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
import android.widget.ListView;
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
    private static String url = "http://api.androidhive.info/contacts/";
    private static String authorize = "http://Cram.com/oauth2/authorize/?client_id=5ca79e5c66d941d2a8b9586274c70a2e&scope=read&state=oAth2spin&redirect_uri=spin://oauthresponse&response_type=code";
    // JSON Node names
    private static final String TAG_CONTACTS = "contacts";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_QUESTIONS = "questions";
 
    // contacts JSONArray
    JSONArray contacts = null;
 
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
		String token = null;
		ListView lv = getListView();
		if(token_date==0){
			Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorize));
			startActivity(viewIntent);
		}
		else if(token_date<=date.getTime()){
			//TODO request refresh token
		}
		else{
			token = pref.getString("token", null);
			// Calling async task to get json
			new GetContacts().execute();
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
				CramFetcher.this.finish();
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
					token(code);
				}
			}
		}
		CramFetcher.this.finish();		
	}
	
	private void token(String code){
		SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
	    Editor editor = pref.edit();
	    Date date = new Date(System.currentTimeMillis());
		editor.putString("token", code); // Storing string
		editor.putLong("expiry", date.getTime()+60000); // Storing long
		editor.commit(); // commit changes
		Toast.makeText(CramFetcher.this,
         	     code,
         	     Toast.LENGTH_LONG).show();
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
	
	private class GetContacts extends AsyncTask<Void, Void, Void> {

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
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
 
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            json=jsonStr;
            if (isTaskCancelled()){
                return null;
             }
            Log.d("Response: ", "> " + jsonStr);
 
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                     
                    // Getting JSON Array node
                    contacts = jsonObj.getJSONArray(TAG_CONTACTS);
 
                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                         
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);
                        String email = c.getString(TAG_EMAIL);
 
                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject(TAG_PHONE);
                        String mobile = phone.getString(TAG_QUESTIONS);
 
                        // tmp hashmap for single contact
                        HashMap<String, String> contact = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        contact.put(TAG_ID, id);
                        contact.put(TAG_NAME, name);
                        contact.put(TAG_EMAIL, email);
                        contact.put(TAG_QUESTIONS, mobile);
 
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
            		Log.e("No dara", "Couldn't get any data from the url");
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
