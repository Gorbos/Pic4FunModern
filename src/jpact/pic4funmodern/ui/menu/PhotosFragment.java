package jpact.pic4funmodern.ui.menu;

import java.io.File;
import java.util.ArrayList;


import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;


import jpact.pic4funmodern.ui.*;
import jpact.pic4funmodern.ui.popups.CollageBitmapActivityFragment;
import jpact.pic4funmodern.ui.popups.CollageBitmapDialogFragment;
import jpact.pic4funmodern.ui.popups.EditBitmapDialogFragment;
import jpact.pic4funmodern.ui.popups.GridBitmapActivityFragment;
import jpact.pic4funmodern.ui.popups.GridBitmapDialogFragment;
import jpact.pic4funmodern.constant.Field;
import jpact.pic4funmodern.gallery.GalleryChecker;
import jpact.pic4funmodern.gallery.ImageFetcher;
import jpact.pic4funmodern.util.ConnectionDetection;
import jpact.pic4funmodern.util.bitmap.*;
import jpact.pic4funmodern.gallery.*;
import jpact.pic4funmodern.gallery.CustomImageAdapter.ViewHolder;
import jpact.pic4funmodern.gallery.ImageCache.*;
import jpact.pic4funmodern.util.*;
import jpact.pic4funmodern.ui.sharing.utils.*;

public class PhotosFragment extends Fragment {
	
	private Integer photoItem;
	private Cursor imagecursor;
	
	private ImageAdapter mAdapter;
	private BitmapResizer bitmapResizer;
	
	private static int background;
	private static String orientation;
	private static String effectTAG;
	private static String TAG = "PhotosFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    
    public GridView mGridView;
    public Button btn;
    public View cView;

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageFetcher mImageFetcher;
    public GalleryChecker gcr;
  	public static ArrayList<String> path;
  	private boolean[] thumbnails_selection;
	private ConnectionDetection cd;
//    public 
	
	 /**
     * Empty constructor as per the Fragment documentation
     */
    public PhotosFragment() {}
    
    public static PhotosFragment newInstance(String fromTAG, String effectTAG){
    	PhotosFragment frag = new PhotosFragment();
    	path = new ArrayList<String>();
		final Bundle args = new Bundle();
	    args.putString("TAG", fromTAG);
	    args.putString("EFFECT", effectTAG);
	    frag.setArguments(args);
    	return frag;
    }
    
    public static PhotosFragment newInstance(String fromTAG, String effectTAG, String orientation){
    	PhotosFragment frag = new PhotosFragment();
    	path = new ArrayList<String>();
    	
		final Bundle args = new Bundle();
	    args.putString("TAG", fromTAG);
	    args.putString("EFFECT", effectTAG);
	    args.putString("ORIENTATION", orientation);
	    frag.setArguments(args);
    	
    	return frag;
    }
    
    public static PhotosFragment newInstance(String fromTAG, String effectTAG, int bgID){
    	PhotosFragment frag = new PhotosFragment();
    	path = new ArrayList<String>();
    	
		final Bundle args = new Bundle();
	    args.putString("TAG", fromTAG);
	    args.putString("EFFECT", effectTAG);
	    args.putInt("BACKGROUND", bgID);
	    frag.setArguments(args);
    	
    	return frag;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        cd = new ConnectionDetection(getActivity());
        gcr = new GalleryChecker(getActivity());
//        Bundle extras = getActivity().getIntent().getExtras();
//        effectTAG = getArguments().getString("EFFECT");
        
        if(getArguments().getString("TAG") != null)
        {
        	TAG = getArguments().getString("TAG");
        }
        
        //For the Fun fragment
        if(getArguments().getString("EFFECT") != null)
        {
        	effectTAG = getArguments().getString("EFFECT");
        }
        
        //Specifically for Grid Fragment
        if(getArguments().getString("ORIENTATION") != null)
        {
        	orientation = getArguments().getString("ORIENTATION");
        }
        
        //Specifically for Collage Fragment
        if(getArguments().getInt("BACKGROUND") != 0)
        {
        	background = getArguments().getInt("BACKGROUND");
        }
        
//        Toast.makeText(getActivity(),""+ effectTAG, 5000).show();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.gallery_activity, container, false);
        mGridView = (GridView) v.findViewById(R.id.img_grid);
        
        bitmapResizer = new BitmapResizer(getActivity());
//		mAdapter = new CustomImageAdapter(getActivity(), mImageFetcher);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    mImageFetcher.setPauseWork(true);
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
            }
        });
		
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
//				Log.i(TAG, "gridview");
//				Toast.makeText(getActivity(), "HELLO, my position is "+arg2, 5000).show();
//				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//			    Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(TAG);
//			    if (prev != null) {
//			        ft.remove(prev);
//			    }
//			    ft.addToBackStack(null);
//				if(!effectTAG.equals(null)&&!effectTAG.equals("Draw4Fun"))
//				{
//				    EditBitmapDialogFragment editDialog = EditBitmapDialogFragment.newInstance(gcr.getUriPath(pos-mAdapter.getNumColumns()),R.layout.editbitmap,TAG,effectTAG);
//					editDialog.show(getActivity().getSupportFragmentManager(), TAG);
//					
//				}
//				else if(effectTAG.equals("Draw4Fun"))
//				{
//					Intent intent = new Intent(getActivity(), com.jpact.draw4fun.Draw4FunMain.class);
//					intent.putExtra("EXTRA_MESSAGE", gcr.getUriPath(pos-mAdapter.getNumColumns()).toString());
//					getActivity().startActivity(intent);
//				}
			}
		});

        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                                }
                            }
                        }
                    }
                });
        
        btn = (Button) v.findViewById(R.id.btnshare);
        
        if(TAG.equals("MainMenuFragment") || TAG.equals("FacebookFragment"))
        	btn.setText("Share");
        else
        	btn.setText("Select");
        
//        if(TAG.equals("MainMenuFragment") || TAG.equals("FacebookFragment") || effectTAG == "Grids" || effectTAG == "Collages")
//        	btn.setVisibility(View.VISIBLE);
//        else
//        	btn.setVisibility(View.GONE);
        
        btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(TAG.equals("MainMenuFragment") || TAG.equals("FacebookFragment"))
				{
					final CharSequence[] items;
					items = new String[]{"Facebook", "Gmail"};
					
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			        builder.setTitle("Share via");
			        builder.setItems(items, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int item) {
			                if (items[item].equals("Facebook")) {
			                	shareToFacebook();
			                } else {
			                	if (!cd.isConnected()) {
			        				cd.getNotConnectedAlert("The device has no data connection. Please enable it on wireless and network settings " +
			        						"to use this feature.");
			        			} else {
			        				shareToGmail();
			        			}
			                }
			            }
			        });
			        
			        AlertDialog alert = builder.create();
			        alert.show();
				}
				else if(effectTAG.equals("Grids") || effectTAG == "Grids")
				{
//					Toast.makeText(getActivity(), ""+TAG+""+orientation+""+path.size()+"", Field.SHOWTIME).show();
//					GridBitmapDialogFragment nf = GridBitmapDialogFragment.newInstance(TAG,orientation,path);
//					nf.show(getActivity().getSupportFragmentManager(), TAG);

					Intent gInstance = new Intent(getActivity(),GridBitmapActivityFragment.class);
					gInstance.putExtra("TAG", TAG);
					gInstance.putExtra("PATHFILES", path);
					gInstance.putExtra("ORIENTATION", orientation);
					getActivity().startActivity(gInstance);
				}
				else if(effectTAG.equals("Collages") || effectTAG == "Collages")
				{
//					Toast.makeText(getActivity(), ""+TAG+""+background+""+path.size()+"", Field.SHOWTIME).show();
//					Log.i("Showtime", "wahaha"+" .."+TAG+""+background+""+path.size()+"");
//					CollageBitmapDialogFragment cf = CollageBitmapDialogFragment.newInstance(TAG,background,path);
//					cf.show(getActivity().getSupportFragmentManager(), TAG);
					
					Intent cInstance = new Intent(getActivity(),CollageBitmapActivityFragment.class);
					cInstance.putExtra("TAG", TAG);
					cInstance.putExtra("PATHFILES", path);
					cInstance.putExtra("BGID", background+"");
					getActivity().startActivity(cInstance);
				}
				else
				{
					
				}
			}
		});
		
        return v;
      
    }
    

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
        effectTAG = null;
        TAG = null;
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
            
        }

        @Override
        public int getCount() {
            // Size + number of columns for top empty row
//            return gc.totalCount + mNumColumns;
            return gc.totalCount;
        }

        @Override
        public Object getItem(int position) {
            return position ;
        }

        @Override
        public long getItemId(int position) {
            return position ;
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
        	final ViewHolder holder;
        	pos = position;
//        	position = gc.totalCount - position-1;
//        	Toast.makeText(mContext,"Position is "+ pos, 5000).show();
//        	Log.i("Item position","Item count = "+pos);
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
				
				if(TAG.equals("MainMenuFragment") || 
						TAG.equals("FacebookFragment") ||
							effectTAG.equals("Grids") ||
								effectTAG.equals("Collages"))
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
//			mImageFetcher.setTag(gc.getUriPath(position - mNumColumns).toString(),holder.checkbox);

			holder.imageview.setTag(gc.getUriPath(position - mNumColumns));
			holder.imageview.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					
					if(TAG.equals("MainMenuFragment") || 
							TAG.equals("FacebookFragment") ||
								effectTAG.equals("Frames") ||
									effectTAG.equals("ImageEffects") ||
										effectTAG.equals("Draw4Fun") ||
											effectTAG.equals("Text"))
					{
						FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
						EditBitmapDialogFragment editDialog = EditBitmapDialogFragment.newInstance(Uri.parse(v.getTag().toString()),R.layout.editbitmap,TAG,effectTAG);
						editDialog.show(ft, TAG);
					}
					
/**
//					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
////				    Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(TAG);
////				    if (prev != null) {
////				        ft.remove(prev);
////				    }
////				    ft.addToBackStack(null);
//					EditBitmapDialogFragment editDialog = EditBitmapDialogFragment.newInstance(Uri.parse(v.getTag().toString()),R.layout.editbitmap,TAG,effectTAG);
////					editDialog.show(getActivity().getSupportFragmentManager(), TAG);
//					editDialog.show(ft, TAG);
//				
//					if(effectTAG.equals("Draw4Fun"))
//					{
//						Intent intent = new Intent(getActivity(), com.jpact.draw4fun.Draw4FunMain.class);
//						intent.putExtra("EXTRA_MESSAGE", Uri.parse(v.getTag().toString()));
//						getActivity().startActivity(intent);
//					}
//					else
//					{
//						EditBitmapDialogFragment editDialog = EditBitmapDialogFragment.newInstance(Uri.parse(v.getTag().toString()),R.layout.editbitmap,TAG,effectTAG);
//						editDialog.show(getActivity().getSupportFragmentManager(), TAG);
//					}
//						if(!effectTAG.equals(null)&&!effectTAG.equals("Draw4Fun"))
//						{
//						    EditBitmapDialogFragment editDialog = EditBitmapDialogFragment.newInstance(gcr.getUriPath(pos-mAdapter.getNumColumns()),R.layout.editbitmap,TAG,effectTAG);
//							editDialog.show(getActivity().getSupportFragmentManager(), TAG);
//							
//						}
//						else if(effectTAG.equals("Draw4Fun"))
//						{
//							Intent intent = new Intent(getActivity(), com.jpact.draw4fun.Draw4FunMain.class);
//							intent.putExtra("EXTRA_MESSAGE", gcr.getUriPath(pos-mAdapter.getNumColumns()).toString());
//							getActivity().startActivity(intent);
//						}
//					Toast.makeText(getActivity(), holder.imageview.getId(), 5000).show();
**/				
					}
				
			});

			if(TAG.equals("MainMenuFragment") ||
					TAG.equals("FacebookFragment") ||
						effectTAG.equals("Grids") ||
							effectTAG.equals("Collages"))
			holder.checkbox.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					if(cb.isChecked())
					{
//						Toast.makeText(getActivity(), holder.imageview.getTag().toString(), 5000).show();
						path.add(holder.imageview.getTag().toString());
//						Toast.makeText(getActivity(), path.size()+"", 5000).show();
					}
					else
						path.remove(holder.imageview.getTag().toString());					
				}
			});
			cView = convertView;
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
    
    /** Once this function is called, all the selected or checked photos will be displayed in separate Activity 
	for multiple sharing of photos to Facebook **/
	private void shareToFacebook() {
		if (path.size() == 0) {
			Toast.makeText(getActivity(), "Please select at least one photo.", Toast.LENGTH_SHORT).show();
		}
		else {
			((Jpact) getActivity().getApplication()).images_path = path;
			((Jpact) getActivity().getApplication()).multi_tagged_friends_id = new ArrayList<ArrayList<String>>();
			((Jpact) getActivity().getApplication()).multi_tagged_friends_name = new ArrayList<ArrayList<String>>();

			Intent i = new Intent(getActivity(), GallSelectedImages.class);
			startActivity(i);
		}
	}
	
	//function to launch Gmail app with the selected photos attached
		private void shareToGmail() {

			if (path.size() == 0) {
				Toast.makeText(getActivity(), "Please select at least one photo.", Toast.LENGTH_SHORT).show();
			} else {
				if (isPackageExists("com.google.android.gm")) {
					ArrayList<Uri> uris = new ArrayList<Uri>();
					for (String str_file : path) {
					    File file = new File(str_file);
					    Uri uri = Uri.fromFile(file);
					    uris.add(uri);
					}
					
					Intent emailIntent = new Intent(); 
					emailIntent.setPackage("com.google.android.gm");
					emailIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
					emailIntent.setType("image/*");
					emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
					startActivity(emailIntent); 
				} else {
					Toast.makeText(getActivity(), "Gmail app is not available in your device.", Toast.LENGTH_LONG).show();
				}
			}
		}
		
		//check if an app exists in the device.
		private boolean isPackageExists(String target) {
	    	boolean package_exists = false;
	    	try {
	    	    ApplicationInfo info = getActivity().getPackageManager().getApplicationInfo(target, 0);
	    	    package_exists = true;
	    	    System.out.println("from gallery activity: package exists...");
	    	    System.out.println("from gallery activity: package info: " + info);
	    	} catch(PackageManager.NameNotFoundException e) {
	    		package_exists = false;
	    		System.out.println("from gallery activity: package does not exists...");
	    	}
	    	return package_exists;
	    }

}
