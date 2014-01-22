package com.example.spin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Base64;

public class ServiceHandler {

	static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
 
    public ServiceHandler() {
	}
	
	/**
	 * Making service call
	 * @url		url to make request
	 * @method	http request method
	 * */
    
	public String makeServiceCall(String url, int method) {
	    return this.makeServiceCall(url, method, null, null);
	}
	
	/**
	 * Making service call
	 * @url		url to make request
	 * @method	http request method
	 * @params	http request params
	 * */
	
	public String makeServiceCall(String url, int method,
	        List<NameValuePair> params, String token ) {
	    try {
	        // http client
	        HttpClient httpClient = createHttpClient();
	        HttpEntity httpEntity = null;
	        HttpResponse httpResponse = null;
	         
	        // Checking http request method type
	        if (method == POST) {
	            HttpPost httpPost = new HttpPost(url);
	            // adding post params
	            if (params != null) {
	                httpPost.setEntity(new UrlEncodedFormEntity(params));
	            }
	            String credentials = "297248cf902970966895aa449946fabf:68036b55a3620bb69f3e23086d4e3446";  
	            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);  
	            httpPost.addHeader("Authorization", "Basic " + base64EncodedCredentials);
	            httpResponse = httpClient.execute(httpPost);
	
	        } else if (method == GET) {
	            // appending params to url
	            if (params != null) {
	            	
	                String paramString = URLEncodedUtils
	                        .format(params, "utf-8");
	                if(params.get(0).getName().equalsIgnoreCase("")){
	            		url+= params.get(0).getValue();
	            	}
	                else{
	                	url += "?" + paramString;
	                }
	            }
	            HttpGet httpGet = new HttpGet(url);
	            httpGet.addHeader("Authorization", "Bearer " + token);
	            httpResponse = httpClient.execute(httpGet);
	
	        }
	        httpEntity = httpResponse.getEntity();
	        response = EntityUtils.toString(httpEntity);
	
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	     
	    return response;
	
	}
	private HttpClient createHttpClient()
	{
	    HttpParams params = new BasicHttpParams();
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
	    HttpProtocolParams.setUseExpectContinue(params, true);

	    SchemeRegistry schReg = new SchemeRegistry();
	    schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
	    ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

	    return new DefaultHttpClient(conMgr, params);
	}
		
}
