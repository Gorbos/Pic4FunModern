package jpact.pic4funmodern.util;

import jpact.pic4funmodern.ui.R;
//import com.jpact.pic4fun.Jpact;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.util.Log;

/** This Class will check if data connection is available in the device **/

public class ConnectionDetection {

	private Activity activity = null;
	
	public ConnectionDetection(Activity activity) {
		this.activity = activity;
	}
	
	public boolean isConnected() {
    	boolean connected = true;
    	ConnectivityManager con = (ConnectivityManager) activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
    	boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
    	boolean mobile = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
    	if(wifi) {
    		connected = true;
    	}
    	else if(mobile) {
    		connected = true;
    	}
    	else {
    		connected = false;
    	}
    	Log.v("connected via wifi", String.valueOf(wifi));
    	Log.v("connected via mobile network", String.valueOf(mobile));
    	return connected;
    }
	
	public void checkConnection() {
		boolean isConnected = this.isConnected();
        
        if(!isConnected) {
        	this.getNotConnectedAlert();
        }
	}
	
	public void getNotConnectedAlert() {
		AlertDialog alert_detection;
    	alert_detection = new AlertDialog.Builder(activity).create();
    	alert_detection.setTitle(activity.getResources().getString(R.string.app_name));
    	alert_detection.setMessage(activity.getResources().getString(R.string.message_checkconnection));
    	alert_detection.setCancelable(false);
    	alert_detection.setButton(activity.getResources().getString(R.string.ok_btn), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
//				activity.moveTaskToBack(true);
//				activity.finish();
				arg0.dismiss();
			}
		});
    	alert_detection.show();
	}
	
	public void getNotConnectedAlert(String message) {
		AlertDialog alert_detection;
    	alert_detection = new AlertDialog.Builder(activity).create();
    	alert_detection.setTitle(activity.getResources().getString(R.string.app_name));
    	alert_detection.setMessage(message);
    	alert_detection.setCancelable(false);
    	alert_detection.setButton(activity.getResources().getString(R.string.ok_btn), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
//				activity.moveTaskToBack(true);
//				activity.finish();
				arg0.dismiss();
			}
		});
    	alert_detection.show();
	}
}
