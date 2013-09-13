package jpact.pic4funmodern.ui;

import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.Menu;
import jpact.pic4funmodern.util.JpactDB;
import jpact.pic4funmodern.ui.R;

public class SplashScreen extends Activity {
	
	public static String TAG = "jpact.pic4fun.ui.SplashScreen";

	 private JpactDB db = new JpactDB(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		
		Thread splashThread = new Thread() {
	         @Override
	         public void run() {
	            try {
	               int waited = 0;
	               while (waited < 500) {
	                  sleep(100);
	                  waited += 100;
	               }
	            } catch (InterruptedException e) {
	               // do nothing
	            } finally {
	            	if (db.isLanguageSelected(getApplicationContext())) {
	            		Locale locale = new Locale(db.getLanguage()); 
	    	            Locale.setDefault(locale);
	    	            Configuration config = new Configuration();
	    	            config.locale = locale;
	    	            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
	    	            System.out.println("language found: " + db.getLanguage());
	            	} else {
	            		Locale locale = new Locale("en"); 
	    	            Locale.setDefault(locale);
	    	            Configuration config = new Configuration();
	    	            config.locale = locale;
	    	            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
	    	            db.saveLanguage("en");
	    	            System.out.println("language default to: " + db.getLanguage());
	    	        }
	            	
//	            	Intent i = new Intent(SplashScreen.this, MainMenuActivity.class);
	            	Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
	            	startActivity(i);
	            	finish();
	            }
	         }
	      };
	      splashThread.start();
	      
	}
	
	 @Override
	   public boolean onKeyDown(int keyCode, KeyEvent event) {
	       if (keyCode == KeyEvent.KEYCODE_BACK) {
	           return true;
	       }
	       return super.onKeyDown(keyCode, event);
	   }
	 
	 @Override
	   public void onConfigurationChanged(Configuration newConfig) {
	       super.onConfigurationChanged(newConfig);
	   }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splashscreen, menu);
		return true;
	}
	
	

}
