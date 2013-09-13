package jpact.pic4funmodern.util.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

public class BitmapItem {
	public PhotoEffects photoEffects;
	private Context context;
	public Bitmap bitmap;
	public int sizedWidth;
	public int sizedHeight;
	
	public BitmapItem(Context localContext){
		context = localContext;
		
	}
	
	public void loadBitmap(ImageView iView, Uri photoUri){
		bitmap = new BitmapResizer(context).resizedBitmap(photoUri);
		iView.setImageBitmap(bitmap);
		iView.setTag(photoUri);
		
	}
	
	public void loadBitmapWithSize(ImageView iView, Uri photoUri){
		bitmap = new BitmapResizer(context,320,320).resizedBitmapForCollage(photoUri);
		
//		iView.getLayoutParams().height = (sizedHeight);
//		iView.getLayoutParams().width = (sizedWidth);
		iView.getLayoutParams().height =(bitmap.getHeight());
		iView.getLayoutParams().width = (bitmap.getWidth());
		iView.setImageBitmap(bitmap);
		iView.setTag(photoUri);
		
	}
	
	public void loadBitmap(Bitmap bitmap, ImageView iView)
	{
//		bitmap = 
	}
	
}
