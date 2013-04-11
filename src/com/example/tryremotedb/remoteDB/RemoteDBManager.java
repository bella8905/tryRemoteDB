package com.example.tryremotedb.remoteDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.tryremotedb.MainActivity;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;


public class RemoteDBManager
{
	private static final String tag = "remoteDB";
	
	private final static String url_prefix = "http://10.201.13.61/~bella/hWalker/";
    private Context mContext;
    private Handler mHandler;
    
    // JSON Node names
    private static final String TAG_SUCCESS_DB = "successDB";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_USERS = "users";
    private static final String TAG_PID = "pid";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_EMAIL = "email";
    
    private LoadAllUsers mLoadAllUsers =  null;
	private CreateNewUser mCreateNewUser = null;
	private UserLogIn mUserLogin = null;
	
    public RemoteDBManager(Context context, Handler handler)
    {
    	Log.d(tag, "create db manager");
    	mContext = context;
    	mHandler = handler;
    }
    
    //create a load all users task
    public void startLoadAllUsers()
    {
    	if(mLoadAllUsers == null||mLoadAllUsers.getStatus()==AsyncTask.Status.FINISHED)
    	{
    		mLoadAllUsers = new LoadAllUsers(mContext, mHandler);
    		mLoadAllUsers.execute();
    	}    	
    }
	
    //create a create new user task
    public void StartCreateUser(String t_email, String t_username, String t_password)
    {
    	if(mCreateNewUser == null||mCreateNewUser.getStatus()==AsyncTask.Status.FINISHED)
    	{
    		mCreateNewUser = new CreateNewUser(mContext, mHandler, t_email, t_username, t_password);
    		mCreateNewUser.execute();
    	}    	
    }
    
    //create a user login task
    public void StartUserLogin(String t_email, String t_password)
    {
    	if(mUserLogin == null||mUserLogin.getStatus()==AsyncTask.Status.FINISHED)
    	{
    		mUserLogin = new UserLogIn(mContext, mHandler, t_email, t_password);
    		mUserLogin.execute();
    	}    	
    }
    
    
	//
    //database class
    //
    
	/**
	 * Background Async Task to Load all users by making HTTP Request
	 * */
	class LoadAllUsers extends AsyncTask<String, String, String>  {

	    private Context mContext;
	    private Handler mHandler;
	    private boolean isConnectSuccess = false;
	    
	    // Creating JSON Parser object
	    JSONParser jParser;
	 
	    ArrayList<HashMap<String, String>> usersList;
	 
	    // url to get all products list
	    //!!!change this first
	    private String url_all_users = url_prefix + "get_all_users.php";
	 
	    
	 // products JSONArray
	    JSONArray users = null;
		
	    //load all users into arrayList
	    public LoadAllUsers(Context context, Handler handler)
	    {
	    	Log.d(tag, "create LoadAllUsers");
	    	mContext = context;
	    	mHandler = handler;
	    	isConnectSuccess = false;
	    	jParser = new JSONParser();
	    	usersList = new ArrayList<HashMap<String, String>>();
	    }
	    
	    
	    /**
	     * Before starting background thread Show Progress Dialog
	     * */
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        
	    }
	    
	    /**
	     * getting All users from url
	     * */
	    protected String doInBackground(String... args) {
	    	Log.d(tag, "pass2");
	        // Building Parameters
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        // getting JSON string from URL
	        JSONObject json = jParser.makeHttpRequest(url_all_users, "GET", params);
	    	Log.d(tag, "pass3");
	        // Check your log cat for JSON reponse
	       
	        try {
	            // Checking for SUCCESS TAG
	        	int successDB = json.getInt(TAG_SUCCESS_DB);
	        	if(successDB == 1)
	        	{
	        		mHandler.obtainMessage(MainActivity.CONNECT_SUCCESS).sendToTarget();
	        		int success = json.getInt(TAG_SUCCESS);
	                if (success == 1) 
	                {
	                    isConnectSuccess = true;
	                    
	                    users = json.getJSONArray(TAG_USERS);

	                    // looping through All Products
	                    for (int i = 0; i < users.length(); i++) {
	                        JSONObject c = users.getJSONObject(i);

	                        // Storing each json item in variable
	                        String id = c.getString(TAG_PID);
	                        String username = c.getString(TAG_USERNAME);
	                        String password = c.getString(TAG_PASSWORD);
	                        String email = c.getString(TAG_EMAIL);

	                        // creating new HashMap
	                        HashMap<String, String> map = new HashMap<String, String>();

	                        // adding each child node to HashMap key => value
	                        map.put(TAG_PID, id);
	                        map.put(TAG_USERNAME, username);
	                        map.put(TAG_PASSWORD, password);
	                        map.put(TAG_EMAIL, email);

	                        //adding HashList to ArrayList
	                        usersList.add(map);
	                    }
	                } 
	                else 
	                {
	                	mHandler.obtainMessage(MainActivity.LOAD_USERS_DATA_ERROR).sendToTarget();
	                }
	        	}
	        	else
	        	{
	        		mHandler.obtainMessage(MainActivity.CONNECT_FAIL).sendToTarget();
	        	}            
	        } 
	        catch (Exception e) 
	        {
	        	Log.e(tag, "Error parsing data " + e.toString());
	        	isConnectSuccess = false;
	            mHandler.obtainMessage(MainActivity.LOAD_USERS_ADDR_ERROR).sendToTarget();
	        }
	       
	        return null;
	    }
	    
	    protected void onPostExecute(String file_url) {
	    	//print out all users info
	    	Log.d(tag, "ALL USERS:");
	    	for(HashMap<String, String> map:usersList)
	    	{
	    		Log.d(tag, map.get(TAG_PID));
	    		Log.d(tag, map.get(TAG_USERNAME));
	    		Log.d(tag, map.get(TAG_PASSWORD));
	    		Log.d(tag, map.get(TAG_EMAIL));
	    	}
	    	
	    	if(isConnectSuccess)
	    		mHandler.obtainMessage(MainActivity.LOAD_USERS_COMPLETE).sendToTarget();
	    }
	}
	


/**
 * Background Async Task to Create new user
 * */
	class CreateNewUser extends AsyncTask<String, String, String> {
	   
		private String m_email, m_username, m_password;
		
	    // Creating JSON Parser object
	    JSONParser jParser;
	 
	    // url to get all products list
	    //!!!change this first
	    private String url_create_new_user = url_prefix + "create_user.php";
		
		//constructor
	    public CreateNewUser(Context context, Handler handler, String t_email, String t_username, String t_password)
	    {
	    	Log.d(tag, "create createNewUser");
	    	mContext = context;
	    	mHandler = handler;
	    	jParser = new JSONParser();
	    	
	    	m_email = t_email;
	    	m_username = t_username;
	    	m_password = t_password;
	    }
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	
	    }
	
	    /**
	     * Creating user
	     * */
	    protected String doInBackground(String... args) {	
	    	Log.d(tag, "pass2");
	        // Building Parameters
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("email", m_email));
	        params.add(new BasicNameValuePair("username", m_username));
	        params.add(new BasicNameValuePair("password", m_password));
	    	Log.d(tag, "pass3");
	        // getting JSON Object
	        // Note that create product url accepts POST method
	        JSONObject json = jParser.makeHttpRequest(url_create_new_user, "POST", params);
	    	Log.d(tag, "pass4");
	        // check for success tag
	        try {
	        	int successDB = json.getInt(TAG_SUCCESS_DB);
	        	if(successDB == 1)
	        	{
		            int success = json.getInt(TAG_SUCCESS);
		
		            if (success == 1) 
		            {
		            	Log.d(tag, json.toString());
		            	mHandler.obtainMessage(MainActivity.CREATE_USER_COMPLETE).sendToTarget();
		            } 
		            else 
		            {
		                // failed to create user
		            	String msg = json.getString(TAG_MESSAGE);
		            	Log.d(tag, "msg: " + msg);
		            	if(msg.equals("User exists"))
		            	{
		            		Log.d(tag, "user exists");
		            		//if user exists
		            		mHandler.obtainMessage(MainActivity.CREATE_USER_DATA_ERROR, 1, -1).sendToTarget();
		            	}
		            	else
		            		mHandler.obtainMessage(MainActivity.CREATE_USER_DATA_ERROR, 0, -1).sendToTarget();
		            }
	        	}
	        	else
	        	{
	        		mHandler.obtainMessage(MainActivity.CONNECT_FAIL).sendToTarget();
	        	}
	        } 
	        catch (Exception e) 
	        {
	        	Log.e(tag, "Error parsing data " + e.toString());
	            mHandler.obtainMessage(MainActivity.CREATE_USER_ADDR_ERROR).sendToTarget();
	        }
	
	        return null;
	    }
	
	    /**
	     * After completing background task Dismiss the progress dialog
	     * **/
	    protected void onPostExecute(String file_url) {
	    	
	    }	
	}
	
	/**
	 * Background Async Task to log in
	 * */
	class UserLogIn extends AsyncTask<String, String, String> 
	{
		private String m_email, m_password;
		
	    // Creating JSON Parser object
	    JSONParser jParser;
	 
	    // url to get all products list
	    //!!!change this first
	    private String url_create_new_user = url_prefix + "user_login.php";
		
		//constructor
	    public UserLogIn(Context context, Handler handler, String t_email, String t_password)
	    {
	    	Log.d(tag, "create createNewUser");
	    	mContext = context;
	    	mHandler = handler;
	    	jParser = new JSONParser();
	    	
	    	m_email = t_email;
	    	m_password = t_password;
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	
	    }
	
	    /**
	     * Creating user
	     * */
	    protected String doInBackground(String... args) {	
	    	Log.d(tag, "pass2");
	        // Building Parameters
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("email", m_email));
	        params.add(new BasicNameValuePair("password", m_password));
	    	Log.d(tag, "pass3");
	        // getting JSON Object
	        // Note that create product url accepts POST method
	        JSONObject json = jParser.makeHttpRequest(url_create_new_user, "POST", params);
	    	Log.d(tag, "pass4");
	        // check for success tag
	        try {
	        	int successDB = json.getInt(TAG_SUCCESS_DB);
	        	if(successDB == 1)
	        	{
		            int success = json.getInt(TAG_SUCCESS);
		
		            if (success == 1) 
		            {
		            	Log.d(tag, json.toString());
		            	mHandler.obtainMessage(MainActivity.USER_LOGIN_COMPLETE).sendToTarget();
		            } 
		            else 
		            {
		                // failed to create user
		            	String msg = json.getString(TAG_MESSAGE);
		            	
		            	if(msg.equals("wrong user"))
		            	{
		            		Log.d(tag, "wrong user");
		            		mHandler.obtainMessage(MainActivity.USER_LOGIN_DATA_ERROR, 1, -1).sendToTarget();
		            	}
		            	else if(msg.equals("wrong password"))
		            		mHandler.obtainMessage(MainActivity.USER_LOGIN_DATA_ERROR, 2, -1).sendToTarget();
		            	else
		            		mHandler.obtainMessage(MainActivity.USER_LOGIN_DATA_ERROR, 3, -1).sendToTarget();
		            }
	        	}
	        	else
	        	{
	        		mHandler.obtainMessage(MainActivity.CONNECT_FAIL).sendToTarget();
	        	}
	        } 
	        catch (Exception e) 
	        {
	        	Log.e(tag, "Error parsing data " + e.toString());
	            mHandler.obtainMessage(MainActivity.USER_LOGIN_ADDR_ERROR).sendToTarget();
	        }
	        return null;
	    }
	
	    /**
	     * After completing background task Dismiss the progress dialog
	     * **/
	    protected void onPostExecute(String file_url) {
	    	
	    }	
	}
}

