package jpact.pic4funmodern.gallery;

import jpact.pic4funmodern.ui.R;
import jpact.pic4funmodern.ui.menu.PhotosActivity;
import jpact.pic4funmodern.ui.popups.EditBitmapDialogFragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * The main adapter that backs the GridView. This is fairly standard except the number of
 * columns in the GridView is used to create a fake top row of empty views as we use a
 * transparent ActionBar and don't want the real top row of images to start off covered by it.
 */
public class CustomImageAdapter extends BaseAdapter{
		//TAG
		public static String TAG;
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

        public CustomImageAdapter(Context context, ImageFetcher imageFetcher, String fromTAG) {
            super();
            mContext = context;
            TAG = fromTAG;
            gc = new GalleryChecker(mContext);
            mImageFetcher = imageFetcher;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//            // Calculate ActionBar height
//            TypedValue tv = new TypedValue();
//            if (context.getTheme().resolveAttribute(
//                    android.R.attr.actionBarSize, tv, true)) {
//                mActionBarHeight = TypedValue.complexToDimensionPixelSize(
//                        tv.data, context.getResources().getDisplayMetrics());
//            } 
            
        }

        @Override
        public int getCount() {
            // Size + number of columns for top empty row
            return gc.totalCount + mNumColumns;
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
				convertView = mInflater.inflate(R.layout.gallery_item, null);
				holder.imageview = (ImageView) convertView.findViewById(R.id.image_thumbnail);
				holder.checkbox = (CheckBox) convertView.findViewById(R.id.chck_select);
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
//			holder.imageview.setOnClickListener(new View.OnClickListener(){
//
//				@Override
//				public void onClick(View v) {
//					Toast.makeText(mContext, gc.getUriPath(pos).toString(), 5000).show();
//					EditBitmapDialogFragment editDialog = EditBitmapDialogFragment.newInstance((Uri) v.getTag(),R.layout.editbitmap,TAG,null);
////				editDialog.show(manager, tag)
//				}
//				
//			});
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
