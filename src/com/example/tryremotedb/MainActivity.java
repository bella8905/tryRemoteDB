package com.example.tryremotedb;

import com.example.tryremotedb.remoteDB.RemoteDBManager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String tag = "remoteDB";	
	public static final String TOAST = "toast";
	
	// Message types sent from the database management Handler
	public static final int CONNECT_SUCCESS = 1;
	public static final int CONNECT_FAIL = 2;
	public static final int LOAD_USERS_COMPLETE = 3;
	public static final int LOAD_USERS_ADDR_ERROR = 4;
	public static final int LOAD_USERS_DATA_ERROR = 5;
	public static final int CREATE_USER_COMPLETE = 6;
	public static final int CREATE_USER_ADDR_ERROR = 7;
	public static final int CREATE_USER_DATA_ERROR = 8;
	public static final int USER_LOGIN_COMPLETE = 9;
	public static final int USER_LOGIN_ADDR_ERROR = 10;
	public static final int USER_LOGIN_DATA_ERROR = 11;
	    
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(tag, "handle message");
            switch (msg.what) {
            //remote database connect
            case CONNECT_FAIL:
            	Log.d(tag, "CONNECT_FAIL");
            	Toast.makeText(getApplicationContext(), "Unable to connect to remote database, please check database config", Toast.LENGTH_SHORT).show();
                break;
            case CONNECT_SUCCESS:
            	Log.d(tag, "CONNECT_SUCCESS");
            	Toast.makeText(getApplicationContext(), "connect to remote database successfully", Toast.LENGTH_SHORT).show();
                break;
            //////////////////////////
            //load_all_users
            case LOAD_USERS_ADDR_ERROR:
            	Log.d(tag, "LOAD_USERS_ADDR_ERROR");
            	Toast.makeText(getApplicationContext(), "Unable to get data from database, please check http address in LoadAllUsers class", Toast.LENGTH_SHORT).show();
                break;
            case LOAD_USERS_COMPLETE:
            	Log.d(tag, "LOAD_USERS_COMPLETE");
            	Toast.makeText(getApplicationContext(), "load products complete", Toast.LENGTH_SHORT).show();
                break;
            case LOAD_USERS_DATA_ERROR:
            	Log.d(tag, "LOAD_USERS_DATA_ERROR");
            	Toast.makeText(getApplicationContext(), "Get user data unsuccessfully, check the get_all_users php file", Toast.LENGTH_SHORT).show();
                break;
            //////////////////////////  
            //create user
            case CREATE_USER_ADDR_ERROR:
            	Log.d(tag, "CREATE_USER_ADDR_ERROR");
            	Toast.makeText(getApplicationContext(), "Unable to get data from database, please check http address in CreateNewUser class", Toast.LENGTH_SHORT).show();
                break;
            case CREATE_USER_COMPLETE:
            	Log.d(tag, "CREATE_USER_COMPLETE");
            	Toast.makeText(getApplicationContext(), "create new user complete", Toast.LENGTH_SHORT).show();
                break;
            case CREATE_USER_DATA_ERROR:
            	Log.d(tag, "CREATE_USER_DATA_ERROR");
            	if(msg.arg1 == 1)
            	{
            		//user exists
            		Toast.makeText(getApplicationContext(), "User exists!", Toast.LENGTH_SHORT).show();
            	}
            	else
            	{
            		Toast.makeText(getApplicationContext(), "Get user data unsuccessfully, check the create_user php file", Toast.LENGTH_SHORT).show();
            	}
                break;
                //////////////////////////  
                //user login
                case USER_LOGIN_ADDR_ERROR:
                	Log.d(tag, "USER_LOGIN_ADDR_ERROR");
                	Toast.makeText(getApplicationContext(), "Unable to get data from database, please check http address in UserLogIn class", Toast.LENGTH_SHORT).show();
                    break;
                case USER_LOGIN_COMPLETE:
                	Log.d(tag, "USER_LOGIN_COMPLETE");
                	Toast.makeText(getApplicationContext(), "user login complete", Toast.LENGTH_SHORT).show();
                    break;
                case USER_LOGIN_DATA_ERROR:
                	Log.d(tag, "USER_LOGIN_DATA_ERROR");
                	if(msg.arg1 == 1)
                	{
                		//wroing user
                		Toast.makeText(getApplicationContext(), "User not exists!", Toast.LENGTH_SHORT).show();
                	}
                	else if(msg.arg1 == 2)
                	{
                		Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                	}
                	else
                	{
                		Toast.makeText(getApplicationContext(), "Get user data unsuccessfully, check the create_user php file", Toast.LENGTH_SHORT).show();
                	}
                    break;
            }
        }
    };
	
	private RemoteDBManager mDBManager;
	
	//edit text
	EditText et_email;
	EditText et_username;
	EditText et_password;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_screen);
        
        mDBManager = new RemoteDBManager(getApplication(), mHandler);
        
        //edit text
        et_email = (EditText)findViewById(R.id.et_email);
        et_username = (EditText)findViewById(R.id.et_username);
        et_password = (EditText)findViewById(R.id.et_password);
    }
     
    public void signIn(View view)
    {
    	Log.d(tag, "sign in");
    	//mDBManager.startLoadAllUsers();
    	//get user detail by email, if email exists, check 
    	String email = et_email.getText().toString();
    	String password = et_password.getText().toString();
    	
    	if(email.equals(""))
    	{
    		Toast.makeText(getApplicationContext(), "no email", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	if(password.equals(""))
    	{
    		Toast.makeText(getApplicationContext(), "no password", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	//create new user
    	mDBManager.StartUserLogin(email, password);
    }

    public void signUp(View view)
    {
    	Log.d(tag, "sign up");
    	String email = et_email.getText().toString();
    	String username = et_username.getText().toString();
    	String password = et_password.getText().toString();
    	
    	if(email.equals(""))
    	{
    		Toast.makeText(getApplicationContext(), "no email", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	if(username.equals(""))
    	{
    		Toast.makeText(getApplicationContext(), "no username", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	if(password.equals(""))
    	{
    		Toast.makeText(getApplicationContext(), "no password", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	//create new user
    	mDBManager.StartCreateUser(email, username, password);
    }
}
