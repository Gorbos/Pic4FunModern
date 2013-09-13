package jpact.pic4funmodern.ui.menu;

import java.io.File;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import jpact.pic4funmodern.constant.*;
import jpact.pic4funmodern.ui.BuildConfig;
import jpact.pic4funmodern.ui.R;
import jpact.pic4funmodern.ui.popups.EditBitmapDialogFragment;
import jpact.pic4funmodern.util.MediaUtility;
import jpact.pic4funmodern.util.Utils;
import jpact.pic4funmodern.util.checker.PackageCheck;

public class CameraFragment extends DialogFragment{
	private static String ACTIVITY = "ACTIVITY";
	//TAG
		public static String TAG = "CameraFragment";
	//create image and video folder
		private File galleryFolder;
		//Uri of output image
		private Uri photoUri, origUri, videoUri, cropUri;
		//Files initialize
		public MediaUtility mediaUtility;
		public PackageCheck packageCheck;
	
	/**
     * Empty constructor as per the Fragment documentation
     */
	public CameraFragment(){}
	
	public static CameraFragment newInstance(String activity) {
        CameraFragment frag = new CameraFragment();

        final Bundle args = new Bundle();
        args.putString(ACTIVITY, activity);
        frag.setArguments(args);

        return frag;
    }
	
	@Override
	public void onCreate(Bundle b) {
		if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }
		//Class initialization
		mediaUtility = new MediaUtility(getActivity());
		packageCheck = new PackageCheck(getActivity());
		
        super.onCreate(b);
        if(getActivity().getIntent().getStringExtra("ACTIVITY").equals("Take Photo"))
        {
        	takePhoto();
        }
        
        else if(getActivity().getIntent().getStringExtra("ACTIVITY").equals("Record Video"))
        {
        	recordVideo();
        }
        else
        	Toast.makeText(getActivity(), "Something went wrong.", Field.SHOWTIME).show();
    	
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
			Toast.makeText(getActivity(),"Sorry, something went wrong with the camera. Error : "+e.getCause(), Field.SHOWTIME).show();
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
		//Get result after taking a photo
        if (requestCode == Field.CAMERA_REQUEST) {
    		if (resultCode == getActivity().RESULT_OK) {
//    			Bitmap photo = (Bitmap) data.getExtras().get("data"); use getActivity() if you need a Bitmap result instead of URI
    			System.out.println("from "+TAG+"u - captured image uri: " + photoUri.toString());
    			removeImage(getLastImageId());
    			mediaUtility.updateMedia(TAG, photoUri.getPath());
    			origUri = photoUri;
    			Toast.makeText(getActivity(), "Success.", Toast.LENGTH_LONG).show();
//    			new GetImageFromUriTask().execute("");
    			
    			//Fragment initialization
    			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
    		    Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(TAG);
    		    if (prev != null) {
    		        ft.remove(prev);
    		    }
//    		    ft.addToBackStack(null);
//    		    
    			EditBitmapDialogFragment editDialog = EditBitmapDialogFragment.newInstance(photoUri,R.layout.editbitmap,TAG,null);
    			
//    			editDialog.show(ft, TAG);
    			ft.add(editDialog, TAG);
    			ft.commitAllowingStateLoss();
    			
    		} else if (resultCode == getActivity().RESULT_CANCELED) {
    		} else {
            	Toast.makeText(getActivity(), "Failed to capture image. Please try again.", Toast.LENGTH_LONG).show();
            }
        }  
        //Get result after taking a video
		if (requestCode == Field.VIDEO_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
//            	Toast.makeText(getActivity(), videoUri.toString(), Toast.LENGTH_LONG).show();
            	videoUri = (Uri) data.getData();
            	
            	if (videoUri != null) {
            		System.out.println("from "+TAG+" - captured video uri: " + videoUri.toString());
            		System.out.println("from "+TAG+" - captured video path: " + videoUri.getPath());
            		//use uri.getLastPathSegment() if store in folder
//            		System.out.println("from "+TAG+" - captured video name: " + getName(videoUri));
            		Toast.makeText(getActivity(), "Video is saved in Gallery ? Camera folder.", Toast.LENGTH_LONG).show();
            	} else {
            		Toast.makeText(getActivity(), "A problem occured while displaying your captured video. Please try again.", Toast.LENGTH_LONG).show();
            	}
            } else if (resultCode == getActivity().RESULT_CANCELED) {
            } else {
            	Toast.makeText(getActivity(), "Failed to capture video. Please try again.", Toast.LENGTH_LONG).show();
            }
        }
		//Get result after launching the Gallery app on our app.
		if (requestCode == Field.GALLERY_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
            	photoUri = (Uri) data.getData();
            } else if (resultCode == getActivity().RESULT_CANCELED) {
            } else {
            	Toast.makeText(getActivity(), "Failed to get image from Gallery. Please try again.", Toast.LENGTH_LONG).show();
            }
        }
//		//Get result after cropping an image
//		if (requestCode == Field.CROP_REQUEST) {
//			if (resultCode == RESULT_OK) {
////				Bundle extras = data.getExtras();
////				photo = extras.getParcelable("data"); use getActivity() if the output must be Bitmap
//				System.out.println("crop output: " + cropUri);
//				photoUri = cropUri;
//				updateGallery.updateMedia(photoUri.getPath());
//				new GetImageFromUriTask().execute("");
//			} else if (resultCode == RESULT_CANCELED) {
//			} else {
//	        	Toast.makeText(getActivity(), "Failed to process cropped image. Please try again.", Toast.LENGTH_LONG).show();
//	        }
//	    }
		
//		((Jpact) getActivity().getApplication()).facebook.authorizeCallback(requestCode, resultCode, data);
    }
	
	/*** Functions to delete the duplicate image in Camera Folder ***/
	private int getLastImageId() {
	    final String[] imageColumns = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
	    final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
	    Cursor imageCursor = getActivity().managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
	    
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
		ContentResolver cr = getActivity().getContentResolver();
	    cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID + "=?", new String[]{Long.toString(id)});
	    System.out.println("from "+TAG+": image from Camera folder deleted.");
	}

	
//	 @Override
//	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//	            Bundle savedInstanceState) {
//	        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
//	 }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		return null;
	}
	

}
