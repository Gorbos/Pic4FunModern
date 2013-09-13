package jpact.pic4funmodern.ui;


import java.util.ArrayList;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

import jpact.pic4funmodern.util.*;

import jpact.pic4funmodern.ui.BuildConfig;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;


public class MainMenuActivity extends FragmentActivity{
	private static final String TAG = "MainMenuActivity";

	public void onCreate(Bundle b) {
		if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }
		super.onCreate(b);
//		WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//    	Display d = (Display) wm.getDefaultDisplay();
//    	int screenWidth = d.getWidth();
//    	int screenHeight = d.getHeight();
//		Toast.makeText(getApplicationContext(), " w = "+screenWidth+" h = "+screenHeight, 5000).show();
		if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, new MainMenuFragment(), TAG);
//            ft.add(android.R.id.content, new TestFragment(), TAG);
//            ft.add(android.R.id.content, new TestFragment2(), TAG);
            ft.commit();
        }
		
//		Intent i = new Intent(getApplicationContext(), TestFragment3.class);
//		startActivity(i);
	}
	
	@Override
	   public boolean onKeyDown(int keyCode, KeyEvent event) {
	       if (keyCode == KeyEvent.KEYCODE_BACK) {
//	           return true
	    	   exitAlert();
	       }
	       return super.onKeyDown(keyCode, event);
	   }
	
	@Override
	   public void onConfigurationChanged(Configuration newConfig) {
	       super.onConfigurationChanged(newConfig);
	   }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.splashscreen, menu);
//		return true;
//	}
	
	/** Set the About, Exit, Feedback and Logout options menu **/
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, 1, "About").setIcon(R.drawable.about);
        menu.add(2, 2, 2, "Exit").setIcon(R.drawable.logout);
        menu.add(3, 3, 3, "Feedback").setIcon(R.drawable.feedback);
        menu.add(4, 4, 4, "Logout FB").setIcon(R.drawable.fb_logo);
        
        return true;
    }
	
	/** Add action to options menu (1 - About, 2 - Exit, 3 - Feedback, 4 - Logout FB) **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
//            case 1: 
//            	about_dialog.show();
//            	break;
            case 2: 
            	exitAlert();
            	break;
//            case 3: 
//            	this.sendFeedback();
//            	break;
//            case 4: 
//            	if (!cd.isConnected()) {
//        			cd.getNotConnectedAlert("The device has no data connection. Please enable it on wireless and network settings " +
//        					"before doing this action.");
//        		} else {
//        			if (!((Jpact) this.getApplication()).facebook.isSessionValid()) {
//            			this.alertAuthorizeFacebook("Please login first your Facebook account.");
//            		} else {
//            			if (isPackageExists("com.facebook.katana")) {
//            				alertLogoutWithFBApp();
//            			} else {
//            				this.logoutFacebook();
//            			}
//            		}
//        		}
//            	break;
        }
        return false;
    }
	
	
	//Alert Exit
			private void exitAlert() {
		    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    	builder.setTitle("Exit");
		    	builder.setMessage("Exit " + this.getResources().getString(R.string.app_name) + " application?");
		    	builder.setCancelable(true);
		    	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int id) {
			    		moveTaskToBack(true);
			    		finish();
			    	}
		    	});
		    	builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int id) {
			    		dialog.cancel();
			    	}
		    	});
		    	
		    	AlertDialog alert = builder.create();
		    	alert.show();
		    }
			

}
