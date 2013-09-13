package jpact.pic4funmodern.util.drag.grid;

import jpact.pic4funmodern.ui.R;
import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

/**
 * This class is used with a GridView object. It provides a set of ImageCell objects 
 * that support dragging and dropping.
 * 
 */

public class CustomImageCellAdapter extends BaseAdapter 
{

// Constants
public static final int DEFAULT_NUM_IMAGES = 8;
public static int imageCount = 0 ;
public static int orientation = 0;
public static int screenWidth = 0;
public static int screenHeight = 0;
/**
 */
// Variables
public ViewGroup mParentView = null;
private Context mContext;

public CustomImageCellAdapter(Context c, int imageCount, int orientation) 
{
    mContext = c;
    this.imageCount = imageCount;
    this.orientation = orientation;
}

public CustomImageCellAdapter(Context c, int imageCount, int orientation, int scnWidth, int scnHeight) 
{
    mContext = c;
    this.imageCount = imageCount;
    this.orientation = orientation;
    this.screenWidth = scnWidth;
    this.screenHeight = scnHeight;
}


/**
 */
// Methods

/**
 * getCount
 */

public int getCount() 
{   
    return imageCount;
}

public Object getItem(int position) 
{
    return null;
}

public long getItemId(int position) {
    return position;
}

/**
 * getView
 * Return a view object for the grid.
 * 
 * @return ImageCell
 */

public View getView (int position, View convertView, ViewGroup parent) 
{
    mParentView = parent;

    ImageCell v = null;
    if (convertView == null) {
        // If it's not recycled, create a new ImageCell.
        v = new ImageCell (mContext);
        
        /*Changes by RJ*/        
        if(orientation == 0)
        	v.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT,(screenWidth/3)));
        else if(orientation == 1)
        	v.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT,(screenHeight/3)));
        else if(orientation == 3)
        	v.setLayoutParams(new GridView.LayoutParams(320,240));
        else
        	v.setLayoutParams(new GridView.LayoutParams(320,320));
        
        v.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        v.setPadding(8, 8, 8, 8);

    } else {
        v = (ImageCell) convertView;
    }

    v.mCellNumber = position;
    v.mGrid = (GridView) mParentView;
    v.mEmpty = true;
//    v.setBackgroundResource (R.color.drop_target_enabled);
    v.setBackgroundResource (R.color.cell_empty);

    //v.mGrid.requestDisallowInterceptTouchEvent (true);

    //v.setImageResource (R.drawable.hello);

    // Set up to relay events to the activity.
    // The activity decides which events trigger drag operations.
    // Activities like the Android Launcher require a long click to get a drag operation started.
//    v.setOnTouchListener ((View.OnTouchListener) mContext);
//    v.setOnClickListener ((View.OnClickListener) mContext);
//    v.setOnLongClickListener ((View.OnLongClickListener) mContext);

    return v;
}

} // end class
