package jpact.pic4funmodern.ui;

import jpact.pic4funmodern.ui.menu.CameraActivity;
import jpact.pic4funmodern.ui.menu.CircleofFriendsActivity;
import jpact.pic4funmodern.ui.menu.FunActivity;
import jpact.pic4funmodern.ui.menu.PhotosActivity;
import jpact.pic4funmodern.ui.menu.SellPhotoActivity;
import jpact.pic4funmodern.ui.popups.*;
import jpact.pic4funmodern.constant.Field;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

import jpact.pic4funmodern.util.*;

import jpact.pic4funmodern.ui.BuildConfig;

public class MainMenuFragment extends Fragment implements OnClickListener{
	
	private static final String TAG = "MainMenuFragment";
	private JpactConfig config;
	
	private Button Camera;
	private Button Photos;
	private Button Sell_Photo;
	private Button Circle_of_Friends;
	private Button Fun;
	
	private Intent intent;
	
	private int duration=5000;
	private Context context;
	
	/**
     * Empty constructor as per the Fragment documentation
     */
	public MainMenuFragment(){}
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setHasOptionsMenu(true);
	        
	        
	 }
	 
	 @Override
	    public View onCreateView(
	            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 
		final View layoutView = inflater.inflate(R.layout.main_menu, container, false);
		context = MainMenuFragment.this.getActivity();
		config = new JpactConfig(getActivity());
        config.initialize();
		this.setFacebook();
		
	 	//Declarations
		Camera = (Button)layoutView.findViewById(R.id.camera);
		Photos = (Button)layoutView.findViewById(R.id.photos);
		Sell_Photo = (Button)layoutView.findViewById(R.id.sell_photo);
		Circle_of_Friends = (Button)layoutView.findViewById(R.id.cof);
		Fun = (Button)layoutView.findViewById(R.id.fun);
		
		//Setters
		Camera.setOnClickListener(this);
		Photos.setOnClickListener(this);
		Sell_Photo.setOnClickListener(this);
		Circle_of_Friends.setOnClickListener(this);
		Fun.setOnClickListener(this);
		return layoutView;
	 }
	 
	 @Override
    public void onResume() {
        super.onResume();
        
    }

	@Override
	public void onClick(View view) {
//		Toast.makeText(getActivity(), TAG, duration).show();
		switch(view.getId())
		{
		case R.id.camera:
//			intent = new Intent(context, CameraActivity.class);
			String[] item = new String[] {"Take Photo","Record Video"};
			showDialog(null,item,"Camera Option");
			break;
		
		case R.id.photos:
			intent = new Intent(context, PhotosActivity.class);
			intent.putExtra("TAG", TAG);
			startActivity(intent);
			break;
		
		case R.id.sell_photo:
//			intent = new Intent(context, SellPhotoActivity.class);
			sellPhoto();
			break;
			
		case R.id.cof:
			if (isCOFInstalled()) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setComponent(new ComponentName("com.jpact.fb", "com.jpact.fb.SplashScreen"));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} else {
				COFNotInstalledAlert();
			}
//			intent = new Intent(context, CircleofFriendsActivity.class);
			
			break;
			
		case R.id.fun:
			intent = new Intent(context, FunActivity.class);
			startActivity(intent);
			break;
			
		default:
			break;
		}
		
		intent = null;
	}
	
	public void showDialog(Object message, String[] selection, String title){
		FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
	    
	    // Create and show the dialog.
	    
	    
	    CustomDialogFragment newFragment;
	    if(message != null)
	    	newFragment = CustomDialogFragment.newInstance(message.toString(),title);
	    
	    else if(selection != null)
	    	newFragment = CustomDialogFragment.newInstance(selection,title);
	    	
	    else
	    {
	    	newFragment = null;
	    	Toast.makeText(getActivity(),"Sorry, something went wrong.", duration).show();
	    }
	    
	    if(newFragment != null)
	    newFragment.show(ft, "dialog");
	    
//	    GridBitmapDialogFragment nf = GridBitmapDialogFragment.newInstance(TAG);
//	    nf.show(getActivity().getSupportFragmentManager(), TAG);
	}
	
	/**
	 * Otapic Sell Photo section
	 * By Mr. Dave Sangtiago
	 * */
	
	//Function to launch Sell Photo in browser
	private void sellPhoto() {
		String url = "https://otapic.com/buy/search/";
        
		Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
	}

	
	
	/**
	 * The Facebook section
	 * By Mr. Dave Santiago
	 */
	
	
	/** Facebook function for SSO **/
	private void setFacebook() {
		((Jpact) getActivity().getApplication()).facebook = new Facebook(((Jpact)getActivity().getApplication()).fb_app_id);
		((Jpact) getActivity().getApplication()).afRunner = new AsyncFacebookRunner(((Jpact) getActivity().getApplication()).facebook);
		         
		/*
		 * Get existing access_token if any
		*/
		((Jpact)  getActivity().getApplication()).mPrefs = getActivity().getPreferences(context.MODE_PRIVATE);
		String access_token = ((Jpact)  getActivity().getApplication()).mPrefs.getString("access_token", null);
		long expires = ((Jpact)  getActivity().getApplication()).mPrefs.getLong("access_expires", 0);
		if(access_token != null) {
			((Jpact) getActivity().getApplication()).facebook.setAccessToken(access_token);
		}
		if(expires != 0) {
			((Jpact) getActivity().getApplication()).facebook.setAccessExpires(expires);
		}
	}

	//Function to detect if Circle of Friends is installed
	private boolean isCOFInstalled() {
    	boolean fb_installed = false;
    	try {
    	    ApplicationInfo info = context.getPackageManager().getApplicationInfo("com.jpact.fb", 0);
    	    fb_installed = true;
    	    System.out.println("from main menu: COF app is installed...");
    	    System.out.println("from main menu: COF info: " + info);
    	} catch(PackageManager.NameNotFoundException e) {
    		fb_installed = false;
    		System.out.println("from main menu: COF app is not installed...");
    	}
    	return fb_installed;
    }
	
	//Alert when Circle of Friends is not installed.
	private void COFNotInstalledAlert() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setTitle("pic4fun Camera");
    	builder.setMessage("Circle of Friends is currently not installed in your device. Please install Circle of Friends app " +
    			"to enable this feature.");
    	builder.setCancelable(true);
    	builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	    		dialog.dismiss();
	    	}
    	});
    	
    	AlertDialog alert = builder.create();
    	alert.show();
    }
	
}
