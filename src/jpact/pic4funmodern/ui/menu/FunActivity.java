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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

public class FunActivity extends FragmentActivity{
	private static final String TAG = "FunActivity";
	
	//create image and video folder
	private File galleryFolder;
	//Uri of output image
	private Uri photoUri, origUri, videoUri;
	//Files initialize
	public MediaUtility mediaUtility;
	public PackageCheck packageCheck;

	public EditBitmapDialogFragment editDialog;
	public static Uri cropUri, outsiderCropUri;
	
	public void onCreate(Bundle b) {
		if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }
		
		super.onCreate(b);
		 if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
	            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	            ft.add(android.R.id.content, new FunFragment(), TAG);
	            ft.commit();
	        }
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		//Class initialization
		mediaUtility = new MediaUtility(getApplicationContext());
		packageCheck = new PackageCheck(getApplicationContext());
		
		//Get result after cropping an image
		if (requestCode == Field.CAMERA_CROP_REQUEST || requestCode == Field.NON_CAMERA_CROP_REQUEST) {
			if (resultCode == RESULT_OK) {
				
				Log.i("cropuri","before cropuri has item, crop = "+cropUri);
				mediaUtility.updateMedia(TAG, cropUri.getPath());
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    			editDialog = EditBitmapDialogFragment.newInstance(cropUri,R.layout.editbitmap,TAG,null);
    			ft.add(editDialog, TAG);
    			ft.commitAllowingStateLoss();
				
			} else if (resultCode == RESULT_CANCELED) {
			} else {
	        	Toast.makeText(this, "Failed to process cropped image. Please try again.", Toast.LENGTH_LONG).show();
	        }
	    }
		
//		((Jpact) this.getApplication()).facebook.authorizeCallback(requestCode, resultCode, data);
    }
		
}
