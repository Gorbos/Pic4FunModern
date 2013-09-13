package jpact.pic4funmodern.ui.menu;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;

import jpact.pic4funmodern.ui.BuildConfig;
import jpact.pic4funmodern.ui.R;
import jpact.pic4funmodern.util.MediaUtility;
import jpact.pic4funmodern.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

//Imported from different packages
import jpact.pic4funmodern.util.*;
import jpact.pic4funmodern.constant.Field;
import jpact.pic4funmodern.util.checker.*;
import jpact.pic4funmodern.ui.popups.*;

public class CameraActivity extends FragmentActivity{
	
	//TAG
	public static String TAG = "CameraActivity";

	//create image and video folder
	private File galleryFolder;
	//Uri of output image
	private Uri photoUri, origUri, videoUri;
	//Files initialize
	public MediaUtility mediaUtility;
	public PackageCheck packageCheck;
	
	public static Uri cropUri, outsiderCropUri;
	public EditBitmapDialogFragment editDialog;
	//Bug fix in saveinstancestat
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
	    super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreate(Bundle b) {
		setContentView(R.layout.main_menu);
        super.onCreate(b);
        if (BuildConfig.DEBUG) {
        	Utils.enableStrictMode();
        }
        //Class initialization
        mediaUtility = new MediaUtility(this);
        packageCheck = new PackageCheck(this);
        
        Log.i("CameraActivity Check","Intent Request = "+getIntent().getStringExtra("REQUEST"));
        Log.i("CameraActivity Check","Intent Activity = "+getIntent().getStringExtra("ACTIVITY"));
//        Log.i("CameraActivity Check","Intent Request = "+getIntent().getStringExtra("REQUEST"));
//        Log.i("CameraActivity Check","Intent Request = "+getIntent().getStringExtra("REQUEST"));
//        Log.i("CameraActivity Check","Intent Request = "+getIntent().getStringExtra("REQUEST"));
        
       
        if(getIntent().getStringExtra("ACTIVITY").equals("Take Photo"))
        {
        	takePhoto();
        }
        
        else if(getIntent().getStringExtra("ACTIVITY").equals("Record Video"))
        {
        	recordVideo();
        }
        else
        	Toast.makeText(this, "Something went wrong.", Field.SHOWTIME).show();
    	}
	
	//Function for video recording
	private void recordVideo() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); 
	    startActivityForResult(intent, Field.VIDEO_REQUEST);
	}

	//Function for capturing photo
	private void takePhoto() {

		/** We are making checking here to avoid displaying choices of different camera apps installed in Android device. 
		 Choices of camera apps will only show once our app did not detect the default camera app of the device **/
		try{
			if (packageCheck.isPackageExists(("com.google.android.camera"))&& ! packageCheck.isPackageExists("com.sec.android.app.camera")) {
				Intent intent = new Intent();
				intent.setPackage("com.google.android.camera");
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				photoUri = mediaUtility.getOutputMediaFileUri(Field.MEDIA_TYPE_IMAGE); 
				intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); 
				intent.putExtra("image_path", mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE));
				startActivityForResult(intent, Field.CAMERA_REQUEST);
			} else if (packageCheck.isPackageExists("com.sec.android.app.camera") && ! packageCheck.isPackageExists("com.google.android.camera")) {
				Intent intent = new Intent();
				intent.setPackage("com.sec.android.app.camera");
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				photoUri = mediaUtility.getOutputMediaFileUri(Field.MEDIA_TYPE_IMAGE); 
				intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); 
				intent.putExtra("image_path", mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE));
				startActivityForResult(intent, Field.CAMERA_REQUEST);
			} else {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
				photoUri = mediaUtility.getOutputMediaFileUri(Field.MEDIA_TYPE_IMAGE); 
				intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); 
				intent.putExtra("image_path", mediaUtility.getOutputMediaFile(Field.MEDIA_TYPE_IMAGE));
				startActivityForResult(intent, Field.CAMERA_REQUEST);
			}
		}
		catch(Exception e)
		{
			Toast.makeText(this,"Sorry, something went wrong with the camera. Error : "+e.getCause(), Field.SHOWTIME).show();
		}
	}

//	/***
//	 * 
//	 Fix for Android documentation null data - http://thanksmister.com/2012/03/16/android_null_data_camera_intent/
//	 * 
//	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		//Class initialization
		mediaUtility = new MediaUtility(getApplicationContext());
		packageCheck = new PackageCheck(getApplicationContext());
		//Class initialization
		//Get result after taking a photo
        if (requestCode == Field.CAMERA_REQUEST) {
    		if (resultCode == RESULT_OK) {
//    			Bitmap photo = (Bitmap) data.getExtras().get("data"); use this if you need a Bitmap result instead of URI
    			System.out.println("from "+TAG+"u - captured image uri: " + photoUri.toString());
    			removeImage(getLastImageId());
    			mediaUtility.updateMedia(TAG, photoUri.getPath());
    			origUri = photoUri;
    			Toast.makeText(this, "Success.", Toast.LENGTH_LONG).show();
//    			new GetImageFromUriTask().execute("");
    			
    			//Fragment initialization
    			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
    		    Fragment prev = getSupportFragmentManager().findFragmentByTag(TAG);
    		    if (prev != null) {
    		        ft.remove(prev);
    		    }
//    		    ft.addToBackStack(null);
//    		    
    			editDialog = EditBitmapDialogFragment.newInstance(photoUri,R.layout.editbitmap,TAG,null);
    			
//    			editDialog.show(this.getSupportFragmentManager(), TAG);
//    			finish();
    					
    			ft.add(editDialog, TAG);
    			ft.commitAllowingStateLoss();
    			
    		} else if (resultCode == RESULT_CANCELED) {
    		} else {
            	Toast.makeText(this, "Failed to capture image. Please try again.", Toast.LENGTH_LONG).show();
            }
        }  
        //Get result after taking a video
		if (requestCode == Field.VIDEO_REQUEST) {
            if (resultCode == RESULT_OK) {
//            	Toast.makeText(this, videoUri.toString(), Toast.LENGTH_LONG).show();
            	videoUri = (Uri) data.getData();
            	
            	if (videoUri != null) {
            		System.out.println("from "+TAG+" - captured video uri: " + videoUri.toString());
            		System.out.println("from "+TAG+" - captured video path: " + videoUri.getPath());
            		//use uri.getLastPathSegment() if store in folder
//            		System.out.println("from "+TAG+" - captured video name: " + getName(videoUri));
            		Toast.makeText(this, "Video is saved in Gallery ► Camera folder.", Toast.LENGTH_LONG).show();
            	} else {
            		Toast.makeText(this, "A problem occured while displaying your captured video. Please try again.", Toast.LENGTH_LONG).show();
            	}
            } else if (resultCode == RESULT_CANCELED) {
            } else {
            	Toast.makeText(this, "Failed to capture video. Please try again.", Toast.LENGTH_LONG).show();
            }
        }
		//Get result after launching the Gallery app on our app.
		if (requestCode == Field.GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
            	photoUri = (Uri) data.getData();
            } else if (resultCode == RESULT_CANCELED) {
            } else {
            	Toast.makeText(this, "Failed to get image from Gallery. Please try again.", Toast.LENGTH_LONG).show();
            }
        }
		//Get result after cropping an image
		if (requestCode == Field.CAMERA_CROP_REQUEST || requestCode == Field.NON_CAMERA_CROP_REQUEST) {
			Log.i("cropuriAct","effing shit2 " + resultCode);
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
	
	/*** Functions to delete the duplicate image in Camera Folder ***/
	private int getLastImageId() {
	    final String[] imageColumns = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
	    final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
	    Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
	    
	    if (imageCursor.moveToFirst()) {
	        int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
	        String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
	        System.out.println("from "+TAG+": last image id " + id);
	        System.out.println("from "+TAG+": last image path " + fullPath);
//	        imageCursor.close();
	        return id;
	    } else {
	        return 0;
	    }
	}
	
	private void removeImage(int id) {
		ContentResolver cr = getContentResolver();
	    cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID + "=?", new String[]{Long.toString(id)});
	    System.out.println("from "+TAG+": image from Camera folder deleted.");
	}

	
	
	@Override
	   public boolean onKeyDown(int keyCode, KeyEvent event) {
		finish();
//	       if (keyCode == KeyEvent.KEYCODE_BACK) {
////	           return true
//	    	   finish();
//	       }
	       return super.onKeyDown(keyCode, event);
	   }
}
