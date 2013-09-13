package jpact.pic4funmodern.ui;

import java.io.File;

import jpact.pic4funmodern.gallery.ImageFetcher;
import jpact.pic4funmodern.ui.TestFragment.ImageAdapter;
import jpact.pic4funmodern.util.Utils;
import jpact.pic4funmodern.util.bitmap.BitmapResizer;
import jpact.pic4funmodern.util.drag.grid.CustomImageCellAdapter;
import jpact.pic4funmodern.util.drag.grid.DeleteZone;
import jpact.pic4funmodern.util.drag.grid.DragController;
import jpact.pic4funmodern.util.drag.grid.DragLayer;
import jpact.pic4funmodern.util.drag.grid.DragSource;
import jpact.pic4funmodern.util.drag.grid.ImageCell;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TestFragment3 extends FragmentActivity implements View.OnLongClickListener, View.OnClickListener, View.OnTouchListener{
	private static final String TAG = "TestFragment3";
	public int i;
	private File galleryFolder;

	// Constants
	public View layoutView;
	private static final int HIDE_TRASHCAN_MENU_ID = Menu.FIRST;
	private static final int SHOW_TRASHCAN_MENU_ID = Menu.FIRST + 1;
	private static final int ADD_OBJECT_MENU_ID = Menu.FIRST + 2;
	private static final int CHANGE_TOUCH_MODE_MENU_ID = Menu.FIRST + 3;
	
	// Variables

	private DragController mDragController;   // Object that handles a drag-drop sequence. It intersacts with DragSource and DropTarget objects.
	private DragLayer mDragLayer;             // The ViewGroup within which an object can be dragged.
	private DeleteZone mDeleteZone;           // A drop target that is used to remove objects from the screen.
	private int mImageCount = 0;              // The number of images that have been added to screen.
	private ImageCell mLastNewCell = null;    // The last ImageCell added to the screen when Add Image is clicked.
	private boolean mLongClickStartsDrag = false;   // If true, it takes a long click to start the drag operation.
	public static final boolean Debugging = false;   // Use this to see extra toast messages.

	public void onCreate(Bundle b) {
		if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }
		super.onCreate(b);
		
//		if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
//            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
////            ft.add(android.R.id.content, new MainMenuFragment(), TAG);
////            ft.add(android.R.id.content, new TestFragment(), TAG);
//            ft.add(android.R.id.content, new TestFragment2(), TAG);
//            ft.commit();
//        }
		
		setContentView(R.layout.editgrid);
		
		
		final GridView gridView = (GridView) findViewById(R.id.image_grid_view);
		onClickAddImage();
		 
//		 Button save = (Button) findViewById(R.id.button_add_image);
//		 save.setText("SAVE!!!");
//		 save.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
////				try {
////					gridView.setDrawingCacheEnabled(true);
////					Bitmap b = Bitmap.createBitmap(gridView.getDrawingCache());
////					File file = getNextFileName();
////	  				OutputStream fOut = new FileOutputStream(file);
////	  	            b.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
////	  	            gridView.setDrawingCacheEnabled(false);
////	  	            fOut.flush();
////	  	            fOut.close();
////	  	            toast ("Success, it is saved in");
////	  	        } catch (Exception e) {
////	  	        	
////	  	        	Log.e("Saving Failed","Saving failed, cause : "+e.getMessage());
////	  	        	e.printStackTrace();
////	  	        	toast ("Epic fail");
////	  			}
//				onClickAddImage(v);
//			}
//		 });
		ImageView refresh = (ImageView)findViewById(R.id.refresh);
		refresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), TestFragment3.class);
				startActivity(i);
				finish();
			}
		});
		 
		 if (gridView == null) toast ("Unable to find GridView");
		    else {
		    	
		    	 gridView.setAdapter (new CustomImageCellAdapter(getApplicationContext(),4,1));
//		    	 gridView.setAdapter (new ImageCellAdapter(getApplicationContext()));
		    }
		 	mDragController = new DragController(this);
		    mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
		    mDragLayer.setDragController (mDragController);
		    mDragLayer.setGridView (gridView);

		    mDragController.setDragListener (mDragLayer);
		    // mDragController.addDropTarget (mDragLayer);

		    mDeleteZone = (DeleteZone) findViewById (R.id.delete_zone_view);

		    // Give the user a little guidance.
		    Toast.makeText (getApplicationContext(), 
		                    getResources ().getString (R.string.instructions),
		                    Toast.LENGTH_LONG).show ();
	}
	
	 /**
	  * Add a new image so the user can move it around. It shows up in the image_source_frame
	  * part of the screen.
	  * 
	  * @param resourceId int - the resource id of the image to be added
	  */    

	 public void addNewImageToScreen (int resourceId)
	 {
	     if (mLastNewCell != null) mLastNewCell.setVisibility (View.GONE);

	     FrameLayout imageHolder = (FrameLayout) findViewById (R.id.image_source_frame1);
	     if (imageHolder != null) {
	        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams (LayoutParams.MATCH_PARENT, 
	                                                                    LayoutParams.MATCH_PARENT, 
	                                                                    Gravity.CENTER);
	        ImageCell newView = new ImageCell (getApplicationContext());
	        newView.setImageResource (resourceId);
	        imageHolder.addView (newView, lp);
	        newView.mEmpty = false;
	        newView.mCellNumber = -1;
	        mLastNewCell = newView;
	        mImageCount++;

	        // Have this activity listen to touch and click events for the view.
	        newView.setOnClickListener(this);
	        newView.setOnLongClickListener(this);
	        newView.setOnTouchListener (this);

	     }
	     
	     FrameLayout imageHolder2 = (FrameLayout) findViewById (R.id.image_source_frame2);
	     if (imageHolder2 != null) {
	        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams (LayoutParams.MATCH_PARENT, 
	                                                                    LayoutParams.MATCH_PARENT, 
	                                                                    Gravity.CENTER);
	        ImageCell newView = new ImageCell (getApplicationContext());
	        newView.setImageResource (resourceId);
	        imageHolder2.addView (newView, lp);
	        newView.mEmpty = false;
	        newView.mCellNumber = -1;
	        mLastNewCell = newView;
	        mImageCount++;

	        // Have this activity listen to touch and click events for the view.
	        newView.setOnClickListener(this);
	        newView.setOnLongClickListener(this);
	        newView.setOnTouchListener (this);

	     }
//	     
//	     FrameLayout imageHolder3 = (FrameLayout) findViewById (R.id.image_source_frame3);
//	     if (imageHolder3 != null) {
//	        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams (LayoutParams.MATCH_PARENT, 
//	                                                                    LayoutParams.MATCH_PARENT, 
//	                                                                    Gravity.CENTER);
//	        ImageCell newView = new ImageCell (getApplicationContext());
//	        newView.setImageResource (resourceId);
//	        imageHolder3.addView (newView, lp);
//	        newView.mEmpty = false;
//	        newView.mCellNumber = -1;
//	        mLastNewCell = newView;
//	        mImageCount++;
//
//	        // Have this activity listen to touch and click events for the view.
//	        newView.setOnClickListener(this);
//	        newView.setOnLongClickListener(this);
//	        newView.setOnTouchListener (this);
//
//	     }
//	     
//	     FrameLayout imageHolder4 = (FrameLayout) findViewById (R.id.image_source_frame4);
//	     if (imageHolder4 != null) {
//	        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams (LayoutParams.MATCH_PARENT, 
//	                                                                    LayoutParams.MATCH_PARENT, 
//	                                                                    Gravity.CENTER);
//	        ImageCell newView = new ImageCell (getApplicationContext());
//	        newView.setImageResource (resourceId);
//	        imageHolder4.addView (newView, lp);
//	        newView.mEmpty = false;
//	        newView.mCellNumber = -1;
//	        mLastNewCell = newView;
//	        mImageCount++;
//
//	        // Have this activity listen to touch and click events for the view.
//	        newView.setOnClickListener(this);
//	        newView.setOnLongClickListener(this);
//	        newView.setOnTouchListener (this);
//
//	     }
//	     getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
	 }

	 /**
	  * Add one of the images to the screen so the user has a new image to move around. 
	  * See addImageToScreen.
	  *
	  */    

	 public void addNewImageToScreen ()
	 {
	     int resourceId = R.drawable.hello;

	     int m = mImageCount % 3;
	     if (m == 1) resourceId = R.drawable.photo1;
	     else if (m == 2) resourceId = R.drawable.photo2;
	     addNewImageToScreen (resourceId);
	 }

	 /**
	  * Handle a click on a view.
	  *
	  */    

	 public void onClick(View v) 
	 {
	     if (mLongClickStartsDrag) {
	        // Tell the user that it takes a long click to start dragging.
	        toast ("Press and hold to drag an image.");
	     }
	 }
	 
	 public boolean onLongClick(View v) 
	 {
	     if (mLongClickStartsDrag) {
	        
	         //trace ("onLongClick in view: " + v + " touchMode: " + v.isInTouchMode ());

	         // Make sure the drag was started by a long press as opposed to a long click.
	         // (Note: I got this from the Workspace object in the Android Launcher code. 
	         //  I think it is here to ensure that the device is still in touch mode as we start the drag operation.)
	         if (!v.isInTouchMode()) {
	            toast ("isInTouchMode returned false. Try touching the view again.");
	            return false;
	         }
	         return startDrag (v);
	     }

	     // If we get here, return false to indicate that we have not taken care of the event.
	     return false;
	 }

	 /**
	  * Handle a click of the Add Image button
	  *
	  */    

	 public void onClickAddImage () 
	 {
	     addNewImageToScreen ();
	 }
	
	 public boolean startDrag (View v)
	 {
	     DragSource dragSource = (DragSource) v;

	     // We are starting a drag. Let the DragController handle it.
	     mDragController.startDrag (v, dragSource, dragSource, DragController.DRAG_ACTION_MOVE);

	     return true;
	 }
	
	public void toast (String msg)
	{
	    Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
	} // end toast

	/**
	 * Send a message to the debug log and display it using Toast.
	 */

	public void trace (String msg) 
	{
	    if (!Debugging) return;
	    Log.d ("DragActivity", msg);
	    toast (msg);
	}

	@Override
	public boolean onTouch (View v, MotionEvent ev) 
	{
	    // If we are configured to start only on a long click, we are not going to handle any events here.
	    if (mLongClickStartsDrag) return false;

	    boolean handledHere = false;

	    final int action = ev.getAction();

	    // In the situation where a long click is not needed to initiate a drag, simply start on the down event.
	    if (action == MotionEvent.ACTION_DOWN) {
	       handledHere = startDrag (v);
	    }
	    
	    return handledHere;
	}   

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
	{
	    ImageCell i = (ImageCell) v;
	    trace ("onItemClick in view: " + i.mCellNumber);
	}
	
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	   
	    super.onCreateOptionsMenu(menu);
	    
	    menu.add(0, HIDE_TRASHCAN_MENU_ID, 0, "Hide Trashcan").setShortcut('1', 'c');
	    menu.add(0, SHOW_TRASHCAN_MENU_ID, 0, "Show Trashcan").setShortcut('2', 'c');
	    menu.add(0, ADD_OBJECT_MENU_ID, 0, "Add View").setShortcut('9', 'z');
	    menu.add (0, CHANGE_TOUCH_MODE_MENU_ID, 0, "Change Touch Mode");
	}
	

public boolean onOptionsItemSelected (MenuItem item) 
{
 switch (item.getItemId()) {
     case HIDE_TRASHCAN_MENU_ID:
         if (mDeleteZone != null) mDeleteZone.setVisibility (View.INVISIBLE);
         return true;
     case SHOW_TRASHCAN_MENU_ID:
         if (mDeleteZone != null) mDeleteZone.setVisibility (View.VISIBLE);
         return true;
     case ADD_OBJECT_MENU_ID:
         // Add a new object to the screen;
         addNewImageToScreen ();
         return true;
     case CHANGE_TOUCH_MODE_MENU_ID:
         mLongClickStartsDrag = !mLongClickStartsDrag;
         String message = mLongClickStartsDrag ? "Changed touch mode. Drag now starts on long touch (click)." 
                                               : "Changed touch mode. Drag now starts on touch (click).";
         Toast.makeText (getApplicationContext(), message, Toast.LENGTH_LONG).show ();
         return true;
 }

 return super.onOptionsItemSelected(item);
}



/** Create pic4fun Camera folder to SD Card **/
private File createFolders() {
	File baseDir;

	if (android.os.Build.VERSION.SDK_INT < 8) {
		baseDir = Environment.getExternalStorageDirectory();
	} else {
		baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	}

	if (baseDir == null) return Environment.getExternalStorageDirectory();

	System.out.println("from main menu - pictures folder: " + baseDir.getAbsolutePath());
	File pic4funFolder = new File(baseDir, "pic4funCamera");

	if (pic4funFolder.exists()) return pic4funFolder;
	if (pic4funFolder.mkdirs()) return pic4funFolder;

	return Environment.getExternalStorageDirectory();
}


//Create new File when saving picture 
	private File getNextFileName() {
		if (galleryFolder != null) {
			if (galleryFolder.exists()) {
				File file = new File(galleryFolder, "IMG_" + System.currentTimeMillis() + ".jpg");
				return file;
			}
		}
		return null;
	}
	
}
