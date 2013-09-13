/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jpact.pic4funmodern.ui;

import java.util.ArrayList;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import jpact.pic4funmodern.util.FriendArrayList;
import android.app.Application;
import android.content.SharedPreferences;

import android.support.v4.app.*;
/**
 *
 * @author Dave
 */

/** Here we declare global variables that can be available in any Activities **/

public class Jpact extends Application {
	
	public String app_state = "";
	//Facebook ID in developers http://developers.facebook.com/
	public String fb_app_id = "398960903502058";
	public Facebook facebook;
	public AsyncFacebookRunner afRunner;
	public SharedPreferences mPrefs;
    
    //Alert
	public String error_message;
    public String success_message;
    public boolean transaction_success;
    public boolean json_message;
    
    //Logout
    public boolean logout_success;
    
    //Get User Info
    public boolean refresh_user = true;
    public String u_name;
    public String u_id;
    
    //Tag Friends
    public FriendArrayList friend_list;
    public ArrayList<String> tagged_friends_id;
    public ArrayList<String> tagged_friends_name;
    public ArrayList<ArrayList<String>> multi_tagged_friends_id;
    public ArrayList<ArrayList<String>> multi_tagged_friends_name;
    public String[] tagged_friends;
    
    //Upload Photos
    public ArrayList<String> images_path;
    
    @Override
    public void onCreate() {
    	this.app_state = "";
    	this.fb_app_id = "398960903502058";
    }
}
