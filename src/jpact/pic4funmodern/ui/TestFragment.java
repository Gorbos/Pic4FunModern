package jpact.pic4funmodern.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import jpact.pic4funmodern.gallery.GalleryChecker;
import jpact.pic4funmodern.gallery.ImageFetcher;
import jpact.pic4funmodern.gallery.ImageCache.ImageCacheParams;
import jpact.pic4funmodern.ui.R;
import jpact.pic4funmodern.ui.menu.*;
import jpact.pic4funmodern.ui.menu.PhotosFragment.ImageAdapter;
import jpact.pic4funmodern.ui.menu.PhotosFragment.ImageAdapter.ViewHolder;
import jpact.pic4funmodern.ui.popups.EditBitmapDialogFragment;
import jpact.pic4funmodern.util.bitmap.BitmapResizer;
import jpact.pic4funmodern.util.drag.grid.*;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TestFragment extends Fragment implements View.OnLongClickListener, View.OnClickListener, View.OnTouchListener {
	
	public ImageView iv;
	public TextView tv;
	
	public Object pause1 = new Object();
	public Object pause2 = new Object();
	public Object pause3 = new Object();
	public Object pause4 = new Object();
	public int i;
	public String item = "a";
	public boolean check1 = true;
	public boolean check2 = true;
	
	

	private File galleryFolder;
	/**
	 */
	// Constants
	public View layoutView;
	private static final int HIDE_TRASHCAN_MENU_ID = Menu.FIRST;
	private static final int SHOW_TRASHCAN_MENU_ID = Menu.FIRST + 1;
	private static final int ADD_OBJECT_MENU_ID = Menu.FIRST + 2;
	private static final int CHANGE_TOUCH_MODE_MENU_ID = Menu.FIRST + 3;

	/**
	 */
	// Variables

	private DragController mDragController;   // Object that handles a drag-drop sequence. It intersacts with DragSource and DropTarget objects.
	private DragLayer mDragLayer;             // The ViewGroup within which an object can be dragged.
	private DeleteZone mDeleteZone;           // A drop target that is used to remove objects from the screen.
	private int mImageCount = 0;              // The number of images that have been added to screen.
	private ImageCell mLastNewCell = null;    // The last ImageCell added to the screen when Add Image is clicked.
	private boolean mLongClickStartsDrag = false;   // If true, it takes a long click to start the drag operation.
	public static final boolean Debugging = false;   // Use this to see extra toast messages.

	
	
	private Integer photoItem;
	private Cursor imagecursor;
	
	private ImageAdapter mAdapter;
	private BitmapResizer bitmapResizer;
	
	private static String fromTAG;
	private static String effectTAG;
	private static String TAG = "TestFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageFetcher mImageFetcher;

	 /**
     * Empty constructor as per the Fragment documentation
     */
    public TestFragment() {}

    public static TestFragment newInstance(String fromTAG){
    	TestFragment frag = new TestFragment();
    	
		final Bundle args = new Bundle();
	    args.putString("TAG", fromTAG);
	    frag.setArguments(args);
    	
    	return frag;
    }
    
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setHasOptionsMenu(true);
	        galleryFolder = createFolders();
	        
//	        effectTAG = getArguments().getString("EFFECT");
	        effectTAG = "EFFECT";
	        
	        Toast.makeText(getActivity(), TAG, 5000).show();
	        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
	        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
	        ImageCacheParams cacheParams = new ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

	        // Set memory cache to 25% of mem class
	        cacheParams.setMemCacheSizePercent(getActivity(), 0.25f);

	        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
	        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
	        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
	        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);

	        
	        mAdapter = new ImageAdapter(getActivity(), mImageFetcher,TAG);
	 }
	 
	 
	 
	 @Override
	    public View onCreateView(
	            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		 layoutView = inflater.inflate(R.layout.demo, container, false);
		 mDragController = new DragController(getActivity());

////		 setupViews ();
//		 DragController dragController = mDragController;
//
//		    mDragLayer = (DragLayer) layoutView.findViewById(R.id.drag_layer);
//		    
//		    mDragLayer.setDragController(dragController);
//		    dragController.addDropTarget (mDragLayer);
//
//		    ImageView i1 = (ImageView) layoutView.findViewById (R.id.Image1);
//		    ImageView i2 = (ImageView) layoutView.findViewById (R.id.Image2);
//
//		    i1.setOnClickListener(this);
//		    i1.setOnLongClickListener(this);
//
//		    i2.setOnClickListener(this);
//		    i2.setOnLongClickListener(this);
//
//		    TextView tv = (TextView) layoutView.findViewById (R.id.Text1);
//		    tv.setOnLongClickListener(this);
//
//		    Toast.makeText (getActivity(), 
//		                    "Press and hold to drag a view", Toast.LENGTH_LONG).show ();

		final GridView gridView = (GridView) layoutView.findViewById(R.id.image_grid_view);
		 
		 
//		 Button save = (Button) layoutView.findViewById(R.id.button_add_image);
//		 save.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				try {
//					gridView.setDrawingCacheEnabled(true);
//					Bitmap b = Bitmap.createBitmap(gridView.getDrawingCache());
//					File file = getNextFileName();
// 	  				OutputStream fOut = new FileOutputStream(file);
// 	  	            b.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
// 	  	            gridView.setDrawingCacheEnabled(false);
// 	  	            fOut.flush();
// 	  	            fOut.close();
// 	  	            toast ("Success, it is saved in");
// 	  	        } catch (Exception e) {
// 	  	        	
// 	  	        	Log.e("Saving Failed","Saving failed, cause : "+e.getMessage());
// 	  	        	e.printStackTrace();
// 	  	        	toast ("Epic fail");
// 	  			}
//			}
//		 });
		 
		 
		    if (gridView == null) toast ("Unable to find GridView");
		    else {
		         gridView.setAdapter (new ImageCellAdapter(getActivity()));
		         // gridView.setOnItemClickListener (this);
		    }

		    mDragLayer = (DragLayer) layoutView.findViewById(R.id.drag_layer);
		    mDragLayer.setDragController (mDragController);
		    mDragLayer.setGridView (gridView);

		    mDragController.setDragListener (mDragLayer);
		    // mDragController.addDropTarget (mDragLayer);

		    mDeleteZone = (DeleteZone) layoutView.findViewById (R.id.delete_zone_view);

		    // Give the user a little guidance.
		    Toast.makeText (getActivity(), 
		                    getResources ().getString (R.string.instructions),
		                    Toast.LENGTH_LONG).show ();

	        
			
		return layoutView;
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

	     FrameLayout imageHolder = (FrameLayout) layoutView.findViewById (R.id.image_source_frame);
	     if (imageHolder != null) {
	        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams (LayoutParams.FILL_PARENT, 
	                                                                    LayoutParams.FILL_PARENT, 
	                                                                    Gravity.CENTER);
	        ImageCell newView = new ImageCell (getActivity());
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

	 public void onClickAddImage (View v) 
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
	    Toast.makeText (getActivity(), msg, Toast.LENGTH_SHORT).show ();
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
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	   
	    super.onCreateOptionsMenu(menu, inflater);
	    
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
           Toast.makeText (getActivity(), message, Toast.LENGTH_LONG).show ();
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
	
	
	
	
	 /**
	    * 
	    * The image adapter
	    * 
	    * */
	    public class ImageAdapter extends BaseAdapter{
			//TAG
			private LayoutInflater mInflater;
			public ViewHolder holder;
			
			public GalleryChecker gc;
	    	private final Context mContext;
	        private int mItemHeight = 0;
	        private int mNumColumns = 0;
	        private int mActionBarHeight = 0;
	        private GridView.LayoutParams mImageViewLayoutParams;
	        private ImageFetcher mImageFetcher;
	        private int pos;

	        public ImageAdapter(Context context, ImageFetcher imageFetcher, String fromTAG) {
	            super();
	            mContext = context;
	            TAG = fromTAG;
	            gc = new GalleryChecker(mContext);
	            mImageFetcher = imageFetcher;
	            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            mImageViewLayoutParams = new GridView.LayoutParams(
	                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//	            // Calculate ActionBar height
//	            TypedValue tv = new TypedValue();
//	            if (context.getTheme().resolveAttribute(
//	                    android.R.attr.actionBarSize, tv, true)) {
//	                mActionBarHeight = TypedValue.complexToDimensionPixelSize(
//	                        tv.data, context.getResources().getDisplayMetrics());
//	            } 
	            
	        }

	        @Override
	        public int getCount() {
	            // Size + number of columns for top empty row
//	            return gc.totalCount + mNumColumns;
	            return gc.totalCount;
	        }

	        @Override
	        public Object getItem(int position) {
	            return position < mNumColumns ?
	                    null : gc.getUriPath(position - mNumColumns);
	        }

	        @Override
	        public long getItemId(int position) {
	            return position < mNumColumns ? 0 : position - mNumColumns;
	        }

	        @Override
	        public int getViewTypeCount() {
	            // Two types of views, the normal ImageView and the top row of empty views
	            return 2;
	        }

	        @Override
	        public int getItemViewType(int position) {
	            return (position < mNumColumns) ? 1 : 0;
	        }

	        @Override
	        public boolean hasStableIds() {
	            return true;
	        }
	        
	        @Override
	        public View getView(int position, View convertView, ViewGroup container) {
	        	ViewHolder holder;
	        	pos = position;
//	        	Toast.makeText(mContext,"Position is "+ pos, 5000).show();
	        	Log.i("Item position","Item count = "+pos);
	        	// First check if this is the top row
	            if (position < mNumColumns) {
	                if (convertView == null) {
	                    convertView = new View(mContext);
	                }
	                // Set empty view with height of ActionBar
	                convertView.setLayoutParams(new AbsListView.LayoutParams(
	                        ViewGroup.LayoutParams.MATCH_PARENT, mActionBarHeight));
	                return convertView;
	            }
				if (convertView == null) {
					holder = new ViewHolder();
					
					if(TAG.equals("MainMenuFragment"))
					{
						convertView = mInflater.inflate(R.layout.gallery_item, null);
						holder.checkbox = (CheckBox) convertView.findViewById(R.id.chck_select);
					}
					else
						convertView = mInflater.inflate(R.layout.gallery_item_non_multiple, null);
					
					holder.imageview = (ImageView) convertView.findViewById(R.id.image_thumbnail);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				convertView.setLayoutParams(mImageViewLayoutParams);
				
				// Check the height matches our calculated column width
	            if (convertView.getLayoutParams().height != mItemHeight) {
	            	convertView.setLayoutParams(mImageViewLayoutParams);
	            }
				mImageFetcher.loadImage(gc.getUriPath(position - mNumColumns).toString(),holder.imageview);
				Log.i("Item position","Item count = "+pos);
				Log.i("Position","Position count = "+position);
				holder.imageview.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View v) {
//						Toast.makeText(mContext,"Position is "+ v.getTag().toString(), 5000).show();
						
						EditBitmapDialogFragment editDialog = EditBitmapDialogFragment.newInstance(gc.getUriPath(5),R.layout.editbitmap,TAG,effectTAG);
////						final Bundle args = new Bundle();
////					     args.putString("EFFECT", fromTAG);
////					     frag.setArguments(args);
						FragmentTransaction ft = getFragmentManager().beginTransaction();
						 Fragment prev = getFragmentManager().findFragmentByTag("dialog");
						    if (prev != null) {
						        ft.remove(prev);
						    }
						    ft.addToBackStack(null);
						
						editDialog.show(ft, TAG);
						
					}
					
				});
				
//				holder.checkbox.setOnClickListener(new OnClickListener() {
//					public void onClick(View v) {
//						CheckBox cb = (CheckBox) v;
//						int id = cb.getId();
//						if (thumbnails_selection[id]){
//							cb.setChecked(false);
//							thumbnails_selection[id] = false;
//						} else {
//							cb.setChecked(true);
//							thumbnails_selection[id] = true;
//						}
//					}
//				});
				return convertView;
	        }

	        /**
	         * Sets the item height. Useful for when we know the column width so the height can be set
	         * to match.
	         *
	         * @param height
	         */
	        public void setItemHeight(int height) {
	            if (height == mItemHeight) {
	                return;
	            }
	            mItemHeight = height;
	            mImageViewLayoutParams =
	                    new GridView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
	            mImageFetcher.setImageSize(height);
	            notifyDataSetChanged();
	        }

	        public void setNumColumns(int numColumns) {
	            mNumColumns = numColumns;
	        }

	        public int getNumColumns() {
	            return mNumColumns;
	        }
	        
	        public class ViewHolder {
				ImageView imageview;
				CheckBox checkbox;
				int id;
			}
	    
	}
}
