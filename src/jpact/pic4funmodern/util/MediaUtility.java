package jpact.pic4funmodern.util;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import jpact.pic4funmodern.constant.*;

public class MediaUtility {
	
	private Context context;
	
	public MediaUtility(Context localContext){
		context = localContext;
	}
	
	//Update the device Gallery App
	public void updateMedia(String TAG, String filepath) {
		System.out.println("from "+TAG+": - notify media scanner and update gallery: " + filepath);
		MediaScannerConnection.scanFile(context, new String[] {filepath}, null, null);
	}
	
	/** Create a file Uri for saving an image or video */
	public Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}
	
	/** Create a File for saving an image or video */
	public File getOutputMediaFile(int type){
		// To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "pic4funCamera");
//		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "pic4funCamera");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (!mediaStorageDir.exists()){
	        if (!mediaStorageDir.mkdirs()){
	            Log.d("pic4funCamera", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));
	    File mediaFile;
	    if (type == Field.MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
	    } else if(type == Field.MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	//Checking if SD Card is mounted
	public boolean isExternalStorageAvailable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

}
