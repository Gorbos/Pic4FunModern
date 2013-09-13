package jpact.pic4funmodern.util.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import jpact.pic4funmodern.util.checker.*;

public class BitmapResizer {
	
	private Context context;
	
	public Integer shortHeight;
	public Integer shortWidth;
	public Integer screenHeight;
	public Integer screenWidth;
	
	public BitmapResizer(Context localContext){
		context = localContext;
	}
	
	public BitmapResizer(Context localContext, Integer width, Integer height){
		context = localContext;
		screenHeight = height;
		screenWidth = width;
	}
	
	
	public Bitmap resizedBitmap(Uri photoUri){
		if(screenHeight == null)
		{
		screenHeight = new PhoneChecker(context).screenHeight;
		screenWidth = new PhoneChecker(context).screenWidth;
		}
		
		shortHeight = (int)(screenHeight * 0.4);
		shortWidth = (int)(screenWidth * 0.4);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		options.inJustDecodeBounds = true;
		Bitmap temp = BitmapFactory.decodeFile(photoUri.getPath(), options);
		
		 // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, shortWidth, shortHeight);
	    
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(photoUri.getPath(), options);
		
	}
	
	public Bitmap resizedBitmapForCollage(Uri photoUri){
		if(screenHeight == null)
		{
		screenHeight = new PhoneChecker(context).screenHeight;
		screenWidth = new PhoneChecker(context).screenWidth;
		}
		
		shortHeight = (int)(screenHeight * 0.4);
		shortWidth = (int)(screenWidth * 0.4);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		options.inJustDecodeBounds = true;
		Bitmap temp = BitmapFactory.decodeFile(photoUri.getPath(), options);
		
		 // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, shortWidth, shortHeight);
	    
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(photoUri.getPath(), options);
		
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);

	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }

	    return inSampleSize;
	}

}
