package jpact.pic4funmodern.ui.popups;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.*;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import jpact.pic4funmodern.constant.Field;
import jpact.pic4funmodern.ui.R;
import jpact.pic4funmodern.ui.TestFragment3;
import jpact.pic4funmodern.ui.menu.FunActivity;
import jpact.pic4funmodern.ui.menu.PhotosFragment;
import jpact.pic4funmodern.util.bitmap.BitmapItem;
import jpact.pic4funmodern.util.bitmap.SaveBitmap;
import jpact.pic4funmodern.util.drag.grid.CustomImageCellAdapter;
import jpact.pic4funmodern.util.drag.grid.DeleteZone;
import jpact.pic4funmodern.util.drag.grid.DragController;
import jpact.pic4funmodern.util.drag.grid.DragLayer;
import jpact.pic4funmodern.util.drag.grid.DragSource;
import jpact.pic4funmodern.util.drag.grid.ImageCell;

@SuppressLint("NewApi")
public class GridBitmapActivityFragment extends FragmentActivity implements OnLongClickListener, OnClickListener
																			, OnTouchListener{
	private static final String TAG = "GridBitmapDialogFragment";
	
	private static int screenWidth;
	private static int screenHeight;
	private Display d;
	private WindowManager wm;

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
	public View view;
	public static int grid;
	public static String orientation;
	public static String fromTAG;
	public static ArrayList<String> path;
	public static BitmapItem bitmapItem;
	public SaveBitmap saveBitmap;
	public DragLayer dl;
	
	public GridBitmapActivityFragment(){}

	 @Override
	 public void onCreate(Bundle b)
//	 public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	 {
		 	super.onCreate(b);
			fromTAG = getIntent().getStringExtra("TAG");
			path = getIntent().getStringArrayListExtra("PATHFILES");
			orientation = getIntent().getStringExtra("ORIENTATION");
		 	
			setContentView(R.layout.editgrid);
	    	dl = (DragLayer)findViewById(R.id.drag_layer);
	    	
	    	wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
	    	d = (Display) wm.getDefaultDisplay();
	    	screenWidth = d.getWidth();
	    	screenHeight = d.getHeight();
	    	
	    	bitmapItem = new BitmapItem(getApplicationContext());
	    	saveBitmap = new SaveBitmap(getApplicationContext());
	    	final GridView gridView = (GridView) findViewById(R.id.image_grid_view);
	    	ImageView refresh = (ImageView) findViewById(R.id.refresh);
			refresh.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent gInstance = new Intent(getApplicationContext(),GridBitmapActivityFragment.class);
					gInstance.putExtra("TAG", TAG);
					gInstance.putExtra("PATHFILES", path);
					gInstance.putExtra("ORIENTATION", orientation);
					startActivity(gInstance);
					finish();
					
				}
			});
			
			ImageView ok = (ImageView) findViewById(R.id.ok);
			ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					gridView.setDrawingCacheEnabled(true);
					Bitmap gridPhoto = gridView.getDrawingCache();
					
					saveBitmap.Save(gridPhoto);
					gridView.setDrawingCacheEnabled(false);
					
					if(fromTAG == "FunActivity")
					{
					Intent intent = new Intent(getApplicationContext(), FunActivity.class);
		            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					}
					
					else if(fromTAG == "PhotosFragment")
					{
					Intent intent = new Intent(getApplicationContext(), PhotosFragment.class);
		            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					}
					finish();
					toast("Bitmap is saved in your gallery!");
				}
			});
			 
			 if (gridView == null) 
				 toast ("Unable to find GridView");
			 else {
//				 toast (orientation);
				 if(orientation.equals("1X2L"))
				 {
					 gridView.setNumColumns(1);
					 gridView.setAdapter (new CustomImageCellAdapter(getApplicationContext(),2,0, screenWidth, screenHeight));
					 
				 }
				 else if(orientation.equals("2X2L"))
				 {
					 gridView.setNumColumns(getResources().getInteger(R.integer.num_columns));
					 gridView.setAdapter (new CustomImageCellAdapter(getApplicationContext(),4,0, screenWidth, screenHeight));
				 }
				 else if(orientation.equals("1X2P"))
				 {
					 gridView.setNumColumns(getResources().getInteger(R.integer.num_columns));
					 gridView.setAdapter (new CustomImageCellAdapter(getApplicationContext(),2,1, screenWidth, screenHeight));
				 }
				 else if(orientation.equals("2X2P"))
				 {
					 gridView.setNumColumns(getResources().getInteger(R.integer.num_columns));
					 gridView.setAdapter (new CustomImageCellAdapter(getApplicationContext(),4,1, screenWidth, screenHeight));
					 gridView.getLayoutParams().height = screenHeight-(screenHeight/10);
//					 gridsetAdapter (new CustomImageCellAdapter(getApplicationContext(),4,3, screenWidth, screenHeight));
				 }
//				 else
//					 gridsetAdapter (new CustomImageCellAdapter(getApplicationContext(),2,1, screenWidth, screenHeight));
//			    	 gridsetAdapter (new ImageCellAdapter(getApplicationContext()));
		 	  }
			 
		 	mDragController = new DragController(this);
		    mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
		    mDragLayer.setDragController (mDragController);
		    mDragLayer.setGridView (gridView);

		    mDragController.setDragListener (mDragLayer);
		    // mDragController.addDropTarget (mDragLayer);
		    addImage();
	    }
	    
	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	    }
	    
	    public void addImage()
	    {
	    	FrameLayout imageHolder1 = (FrameLayout) findViewById (R.id.image_source_frame1);
	    	FrameLayout imageHolder2 = (FrameLayout) findViewById (R.id.image_source_frame2);
	    	FrameLayout imageHolder3 = (FrameLayout) findViewById (R.id.image_source_frame3);
	    	FrameLayout imageHolder4 = (FrameLayout) findViewById (R.id.image_source_frame4);
	    	
	    	FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams (LayoutParams.MATCH_PARENT, 
                    LayoutParams.MATCH_PARENT, 
                    Gravity.CENTER);
	    	if (mLastNewCell != null) mLastNewCell.setVisibility (View.GONE);

	    	for(int item = 0; item <path.size(); item++)
	    	{
	    		switch(item)
	    		{
	    		case 0:
	    			
		   		     if (imageHolder1 != null) {
		   		        ImageCell newView = new ImageCell (getApplicationContext());
//		   		        newsetImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.anniv3));
		   		        imageHolder1.addView (newView, lp);
		   		        newView.mEmpty = false;
		   		        newView.mCellNumber = -1;
		   		        mLastNewCell = newView;
		   		        mImageCount++;
		   		        bitmapItem.loadBitmap(newView, Uri.parse(path.get(item)));
		   		        // Have this activity listen to touch and click events for the 
		   		        newView.setOnClickListener(this);
		   		        newView.setOnLongClickListener(this);
		   		        newView.setOnTouchListener (this);
	
		   		     }
	    			break;
	    			
	    		case 1:
	    			if (imageHolder2 != null) {
	    		        
	    		        ImageCell newView = new ImageCell (getApplicationContext());
	    		        imageHolder2.addView (newView, lp);
	    		        newView.mEmpty = false;
	    		        newView.mCellNumber = -1;
	    		        mLastNewCell = newView;
	    		        mImageCount++;
	    		        bitmapItem.loadBitmap(newView, Uri.parse(path.get(item)));
	    		        // Have this activity listen to touch and click events for the 
	    		        newView.setOnClickListener(this);
	    		        newView.setOnLongClickListener(this);
	    		        newView.setOnTouchListener (this);

	    		     }
	    			break;
	    			
	    		case 2:
	    			if (imageHolder3 != null) {
	    				ImageCell newView = new ImageCell (getApplicationContext());
	    				imageHolder3.addView (newView, lp);
	    				newView.mEmpty = false;
	    				newView.mCellNumber = -1;
	    				mLastNewCell = newView;
	    				mImageCount++;
	    		        bitmapItem.loadBitmap(newView, Uri.parse(path.get(item)));
	    				// Have this activity listen to touch and click events for the 
	    				newView.setOnClickListener(this);
	    				newView.setOnLongClickListener(this);
	    				newView.setOnTouchListener (this);
	    				
	    			}
	    			
	    			break;
	    			
	    		case 3:
	    			ImageCell newView = new ImageCell (getApplicationContext());
			        imageHolder4.addView (newView, lp);
			        newView.mEmpty = false;
			        newView.mCellNumber = -1;
			        mLastNewCell = newView;
			        mImageCount++;
			        bitmapItem.loadBitmap(newView, Uri.parse(path.get(item)));
			        // Have this activity listen to touch and click events for the 
			        newView.setOnClickListener(this);
			        newView.setOnLongClickListener(this);
			        newView.setOnTouchListener (this);
	    			break;
	    			
	    		default:
	    			break;
	    		}
	    	}
		     
	    }
	   
	    

		 /**
		  * Handle a click on a 
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
//		            toast ("isInTouchMode returned false. Try touching the view again.");
		            return false;
		         }
		         return startDrag (v);
		     }

		     // If we get here, return false to indicate that we have not taken care of the event.
		     return false;
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
		    Toast.makeText (getApplicationContext(), msg, Field.SHOWTIME).show ();
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


//	//Create new File when saving picture 
//		private File getNextFileName() {
//			if (galleryFolder != null) {
//				if (galleryFolder.exists()) {
//					File file = new File(galleryFolder, "IMG_" + System.currentTimeMillis() + ".jpg");
//					return file;
//				}
//			}
//			return null;
//		}
		
}
