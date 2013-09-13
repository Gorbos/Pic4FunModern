package jpact.pic4funmodern.util.bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import jpact.pic4funmodern.util.MediaUtility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class SaveBitmap {
	public Bitmap photoBitmap;
	
	public static Uri photoUri;
	public File galleryFolder;
	private Context context;
	private MediaUtility mUtility;
	private String state;
	public SaveBitmap(Context context){
		galleryFolder = createFolders(); 
		this.context = context;
		mUtility = new MediaUtility(context);
	}
	
	public void Save(Bitmap bitmap){
		try{
		  OutputStream fOut = null;
//	  	File file = new File(photoUri.getPath()); this code will replace the source image
	      File file = getNextFileName();
	      fOut = new FileOutputStream(file);
	      photoBitmap = Bitmap.createBitmap(bitmap);
	      photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
	      fOut.flush();
	      fOut.close();
	      photoUri = Uri.parse("file://" + file.getPath());
	      mUtility.updateMedia("SaveBitmap",photoUri.getPath());
	      state = "success";
	      toast("Bitmap saving "+state);
		}
		catch(Exception e){
			state = "failed";
			trace(e.getMessage());
			toast("Bitmap saving "+state);
		}
	}
	
//	public void Save(Uri photoUri){
//	  OutputStream fOut = null;
////  	File file = new File(photoUri.getPath()); this code will replace the source image
//      File file = getNextFileName();
//      fOut = new FileOutputStream(file);
////     photoTemp.compress(Bitmap.CompressFormat.JPEG, 100, fOut); this code will not get the size of source image
//      Matrix matrix = new Matrix();
//      final int width = MainMenu.this.photo.getWidth();
//      final int height = MainMenu.this.photo.getHeight();
//      float scaleWidth = ((float) outWidth) / width;
//      float scaleHeight = ((float) outHeight) / height;
//      //resize the Bitmap
//      matrix.postScale(scaleWidth, scaleHeight);
//      //recreate the new Bitmap
//      Bitmap tmp = Bitmap.createBitmap(MainMenu.this.photo, 0, 0, width, height, matrix, true);
//      tmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//      fOut.flush();
//      fOut.close();
//      photoUri = Uri.parse("file://" + file.getPath());
//	}

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
	
	public void toast (String msg)
	{
	    Toast.makeText (context, msg, Toast.LENGTH_SHORT).show ();
	} // end toast

	/**
	 * Send a message to the debug log. Also display it using Toast if Debugging is true.
	 */

	public void trace (String msg) 
	{
	    Log.d ("DragActivity", msg);
	}

}
