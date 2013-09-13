package jpact.pic4funmodern.ui.popups;

import jpact.pic4funmodern.constant.Field;
import jpact.pic4funmodern.ui.menu.CameraActivity;
import jpact.pic4funmodern.ui.menu.CameraFragment;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class CustomDialogFragment extends DialogFragment{
	
	private static String MESSAGE = "MESSAGE";
	private static String OPTION = "OPTION";
	private static String TITLE = "TITLE";
	private static String ACTIVITY = "ACTIVITY";
	private static String TAG = "CustomDialogFragment";
	
	/**
     * Empty constructor as per the Fragment documentation
     */
	public CustomDialogFragment(){}
	
	public static CustomDialogFragment newInstance(String message, String title) {
        CustomDialogFragment frag = new CustomDialogFragment();

        final Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putString(TITLE, title);
        frag.setArguments(args);

        return frag;
    }
	
	public static CustomDialogFragment newInstance(String[] choice, String title) {
        CustomDialogFragment frag = new CustomDialogFragment();

        final Bundle args = new Bundle();
        args.putStringArray(OPTION, choice);
        args.putString(TITLE, title);
        frag.setArguments(args);

        return frag;
    }
	
	public static CustomDialogFragment newInstance(String message, String title, String choice) {
        CustomDialogFragment frag = new CustomDialogFragment();

        final Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putString(TITLE, title);
        frag.setArguments(args);

        return frag;
    }
	
	/**
     * Populate image using a url from extras, use the convenience factory method
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
    	final String dialogTitle = getArguments().getString(TITLE);
        final String message = getArguments().getString(MESSAGE);
        final String[] items = getArguments().getStringArray(OPTION);
        
        if(message != null)
        	return new AlertDialog.Builder(getActivity())
			        .setTitle(dialogTitle)
			        .setMessage(message)
			        .create();
        
        
        else if(items != null)
	        return new AlertDialog.Builder(getActivity())
	                .setTitle(dialogTitle)
	                .setItems(items, new DialogInterface.OnClickListener(){
	
						@Override
						public void onClick(DialogInterface arg, int selected) {
							if (items[selected].equals("Take Photo")) {
//			                	newFragment = CameraFragment.newInstance("Take Photo");
//			                	newFragment.show(ft,PHOTODIALOG);
								Intent intent = new Intent(getActivity(),CameraActivity.class);
//								intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); 
								intent.putExtra(ACTIVITY, "Take Photo");
							    startActivityForResult(intent, Field.CAMERA_REQUEST);
			                } 

							else if(items[selected].equals("Record Video")){
								Intent intent = new Intent(getActivity(),CameraActivity.class);
								intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); 
								intent.putExtra(ACTIVITY, "Record Video");
							    startActivityForResult(intent, Field.VIDEO_REQUEST);
							 
			                }
							else
								getActivity().finish();
						}
		          
	                	})
	                	
	                .create();
//        else if
//        {
//      //Alert for notifying a user on the status of action made.
//    	
//        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        	builder.setTitle(dialogTitle);
//        	builder.setMessage(message);
//        	builder.setCancelable(false);
//        	builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
//    	    	public void onClick(DialogInterface dialog, int id) {
//    	    		dialog.dismiss();
//    	    	}
//        	});
//        	
//        	AlertDialog alert = builder.create();
//        	alert.show();
//        }
        
        else
        	return null;
    }
    
    
	

}
