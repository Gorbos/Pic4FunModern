package jpact.pic4funmodern.ui.popups;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.Toast;

import jpact.pic4funmodern.ui.R;
import jpact.pic4funmodern.ui.menu.FunActivity;
import jpact.pic4funmodern.util.bitmap.BitmapItem;
import jpact.pic4funmodern.util.bitmap.SaveBitmap;
import jpact.pic4funmodern.util.drag.collage.*;
@SuppressLint("NewApi")
public class EditTextBitmapActivityFragment extends FragmentActivity
											implements View.OnLongClickListener, 
															View.OnClickListener{
	public static String edittext;
	
	
	private DragController mDragController;   // Object that sends out drag-drop events while a view is being moved.
	public DragLayer dl;            // The ViewGroup that supports drag-drop.
	public static final boolean Debugging = false;
	public SaveBitmap saveBitmap;
	
	public View view;
	public static String TAG = "EditTextBitmapActivityFragment";
	public static String texttype;
	public Bitmap bitmap;
	public BitmapItem bitItem;
	public ImageView imageView;
	public static TextView textView;
	public TextView desc;
	
	public EditTextBitmapActivityFragment(){}
	
	@Override
	 public void onCreate(Bundle b)
	{
		super.onCreate(b);
		TAG = getIntent().getExtras().getString("TAG");
		texttype = getIntent().getExtras().getString("TYPE");
		bitmap = EditBitmapDialogFragment.tempPhoto;
//		bitmap = getIntent().getParcelableExtra("IMAGE");
		Log.i("TEXTTYPE","texttype = "+texttype);
		if(bitmap == null)
			Log.e("BITMAP", "Null si bitmap");
			
		
		bitItem = new BitmapItem(getApplicationContext());
		saveBitmap = new SaveBitmap(getApplicationContext());
		setContentView(R.layout.edittext);
		dl = (DragLayer)findViewById(R.id.drag_layer);
    	mDragController = new DragController(this);
    	DragController dragController = mDragController;
    	dl.setDragController(dragController);
    	dragController.addDropTarget (dl);
    	
    	imageView = (ImageView)findViewById(R.id.image);
//		bitItem.loadBitmap(bitmap, imageView);
    	imageView.setImageBitmap(bitmap);
		
		
		textView = (TextView)findViewById(R.id.text);
		textView.setOnClickListener(this);
		textView.setOnLongClickListener(this);
    	
    	ImageView refresh = (ImageView) findViewById(R.id.refresh);
		refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent iInstance = new Intent(getApplicationContext(),EditTextBitmapActivityFragment.class);
				iInstance.putExtra("TAG", TAG);
				iInstance.putExtra("TYPE", texttype);
				startActivity(iInstance);
				finish();
			}
		});
		
		ImageView ok = (ImageView) findViewById(R.id.ok);
		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override 
			public void onClick(View v) {
				dl.setDrawingCacheEnabled(true);
				Bitmap textPhoto = dl.getDrawingCache();
				saveBitmap.Save(textPhoto);
				dl.setDrawingCacheEnabled(false);
				
				bitItem.loadBitmap(EditBitmapDialogFragment.image, saveBitmap.photoUri);
				
				EditBitmapDialogFragment.tempPhoto = bitItem.bitmap;
				EditBitmapDialogFragment.origPhoto = bitItem.bitmap;
				
				finish();
//				startActivity(intent); 		
				toast("Bitmap is saved in your gallery!");
			}
		});
		
		CustomAlertDialogFragment alertD = CustomAlertDialogFragment.newInstance(texttype);
		alertD.show(getSupportFragmentManager(), TAG);
		
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public boolean onLongClick(View v) {
		v.bringToFront();
		trace ("onLongClick in view: " + v);

	    if (!v.isInTouchMode()) {
	       return false;
	    }
		return startDrag (v);
	}

	public boolean startDrag (View v)
	{
	    Object dragInfo = v;
	    mDragController.startDrag (v, dl, dragInfo, DragController.DRAG_ACTION_MOVE);
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
}
