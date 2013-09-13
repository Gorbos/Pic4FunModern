package jpact.pic4funmodern.ui.menu;

import java.io.File;

import jpact.pic4funmodern.constant.Field;
import jpact.pic4funmodern.ui.BuildConfig;
import jpact.pic4funmodern.ui.R;
import jpact.pic4funmodern.ui.popups.EditBitmapDialogFragment;
import jpact.pic4funmodern.util.MediaUtility;
import jpact.pic4funmodern.util.Utils;
import jpact.pic4funmodern.util.checker.PackageCheck;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

public class PhotosActivity extends FragmentActivity{
	
	private static String TAG = "PhotosActivity";
	private static String fromTAG;
	public static Handler act_handler;
	
	//create image and video folder
	private File galleryFolder;
	//Uri of output image
	private Uri photoUri, origUri, videoUri;
	//Files initialize
	public MediaUtility mediaUtility;
	public PackageCheck packageCheck;

	public EditBitmapDialogFragment editDialog;
	public static Uri cropUri, outsiderCropUri;
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
	    super.onSaveInstanceState(outState);
	}
	
	public void onCreate(Bundle b) {
		if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }
		
		super.onCreate(b);
		setHandler();
		Bundle extras = getIntent().getExtras();
		
		
		fromTAG = extras.getString("TAG");
		
		PhotosFragment pFragment;
		if(fromTAG.equals("MainMenuFragment") || fromTAG.equals("FacebookFragment"))
			pFragment = PhotosFragment.newInstance(TAG,null);
		else
			pFragment = PhotosFragment.newInstance(fromTAG,null);
		
		//For the Grid function
		if(!(extras.getString("ORIENTATION") == null))
			pFragment = PhotosFragment.newInstance(fromTAG,null,extras.getString("ORIENTATION"));
		//For the Collage function
		if(!(extras.getInt("BACKGROUND") == 0))
			pFragment = PhotosFragment.newInstance(fromTAG,null,extras.getInt("BACKGROUND"));
		
		
		pFragment.setArguments(getIntent().getExtras());

        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, pFragment, TAG);
            ft.commit();
        }
	}
	
	//function to finish this activity in other activities
		private void setHandler() {
			act_handler = new Handler() {
				public void handleMessage(Message msg) {
		            super.handleMessage(msg);
		            switch(msg.what) {
		            	case 0:
		                finish();
		                break;
		            }
		        }
			};
		}
	
		
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
			Log.i("cropuriAct","cropuriAct");
			//Class initialization
			mediaUtility = new MediaUtility(getApplicationContext());
			packageCheck = new PackageCheck(getApplicationContext());
			Log.i("cropuriAct","effing shit1");
			//Get result after cropping an image
			if (requestCode == Field.CAMERA_CROP_REQUEST || requestCode == Field.NON_CAMERA_CROP_REQUEST) {
				Log.i("cropuriAct","effing shit2 " + resultCode);
				if (resultCode == RESULT_OK) {
					Log.i("cropuriAct","cropuriAct2");
					Log.i("cropuri","before cropuri has item, crop = "+cropUri);
					mediaUtility.updateMedia(TAG, cropUri.getPath());
					FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    			editDialog = EditBitmapDialogFragment.newInstance(cropUri,R.layout.editbitmap,TAG,null);
	    			ft.add(editDialog, TAG);
	    			ft.commitAllowingStateLoss();
					
				} 
				else if (resultCode == RESULT_CANCELED) {
				} else {
		        	Toast.makeText(this, "Failed to process cropped image. Please try again.", Toast.LENGTH_LONG).show();
		        }
		    }
			else
				Log.i("cropuriAct","onActivityResult photosActivity");
	        
			
//			((Jpact) this.getApplication()).facebook.authorizeCallback(requestCode, resultCode, data);
	    }
			

}
