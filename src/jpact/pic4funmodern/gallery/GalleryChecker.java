package jpact.pic4funmodern.gallery;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class GalleryChecker {
	private LruCache<String, Bitmap> mMemoryCache;
	private Cursor imagecursor;
	public Context context;
	
	public Object pauseLock = new Object();
	public int image_column_index;
	public String[] columns  = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
	public String orderBy;

	public static int totalCount;
	public int id;
	public int dataColumnIndex;
	public String path;
	public Uri uriPath;
	
	public GalleryChecker(Context localContext)
	{
		context = localContext;
		orderBy = MediaStore.Images.Media._ID;
		imagecursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);		
		
		if(imagecursor != null)
		{
			image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
			totalCount = imagecursor.getCount();
		}
	}

	public Uri getUriPath(int item){
		imagecursor.moveToPosition(item);
		id = imagecursor.getInt(image_column_index);
		dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
		path = imagecursor.getString(dataColumnIndex);
		uriPath = Uri.parse(path);
		
		return uriPath;
	}
}
