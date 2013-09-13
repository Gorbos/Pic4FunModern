package jpact.pic4funmodern.ui.popups;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import jpact.pic4funmodern.ui.R;
import jpact.pic4funmodern.util.bitmap.BitmapItem;
import jpact.pic4funmodern.util.bitmap.SaveBitmap;
import jpact.pic4funmodern.util.drag.collage.*;
@SuppressLint("NewApi")
public class CollageBitmapDialogFragment extends DialogFragment 
											implements View.OnLongClickListener, 
															View.OnClickListener{
	
	private DeleteZone mDeleteZone;
	private DragController mDragController;   // Object that sends out drag-drop events while a view is being moved.
	public DragLayer dl;            // The ViewGroup that supports drag-drop.
	public static final boolean Debugging = false;
	public SaveBitmap saveBitmap;
	
	public View view;
	public static String TAG;
	public static ArrayList<String> path;
	public static int resID;
	public BitmapItem bitItem;
	ImageView drag1, drag2, drag3, drag4, drag5,
				drag6, drag7, drag8, drag9, drag10,
					drag11, drag12, drag13, drag14, drag15;
	public CollageBitmapDialogFragment(){}
	
	public static CollageBitmapDialogFragment newInstance(String fromTAG, int bgID ,ArrayList<String> pathFiles) {
		CollageBitmapDialogFragment frag = new CollageBitmapDialogFragment();
		final Bundle args = new Bundle();
		TAG = fromTAG;
		path = pathFiles;
		resID = bgID;
        frag.setArguments(args);
        return frag;
    }
	
	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
			bitItem = new BitmapItem(getActivity());
			saveBitmap = new SaveBitmap(getActivity());
	    	view = inflater.inflate(R.layout.editcollage, container);
	    	dl = (DragLayer)view.findViewById(R.id.drag_layer);
	    	mDeleteZone = (DeleteZone) view.findViewById (R.id.delete_zone_view);
	    	dl.setBackgroundResource(resID);
	    	mDragController = new DragController(getActivity());
	    	DragController dragController = mDragController;
	    	dl.setDragController(dragController);
	    	dragController.addDropTarget (dl);
	    	dragController.addDropTarget(mDeleteZone);
	    	
	    	ImageView refresh = (ImageView) view.findViewById(R.id.refresh);
			refresh.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CollageBitmapDialogFragment nf = CollageBitmapDialogFragment.newInstance(TAG, resID, path);
				    nf.show(getActivity().getSupportFragmentManager(), TAG);
				    getDialog().dismiss();
				    
				}
			});
			
			ImageView ok = (ImageView) view.findViewById(R.id.ok);
			ok.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dl.setDrawingCacheEnabled(true);
					Bitmap gridPhoto = dl.getDrawingCache();
					
					saveBitmap.Save(gridPhoto);
					dl.setDrawingCacheEnabled(false);
					getDialog().dismiss();
					getActivity().finish();
					toast("Bitmap is saved in your gallery!");
				}
			});
			
			ImageView add = (ImageView) view.findViewById(R.id.add);
			add.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					getDialog().dismiss();
//				    EditBitmapDialogFragment.im
				}
			});
			setDraggableViews();
	   return view;
	}

	@Override
	public void onClick(View arg0) {
//		toast ("You clicked. Try a long click");
	}

	@Override
	public boolean onLongClick(View v) {
		v.bringToFront();
		trace ("onLongClick in view: " + v);

	    // Make sure the drag was started by a long press as opposed to a long click.
	    // (Note: I got this from the Workspace object in the Android Launcher code. 
	    //  I think it is here to ensure that the device is still in touch mode as we start the drag operation.)
	    if (!v.isInTouchMode()) {
//	       toast ("isInTouchMode returned false. Try touching the view again.");
	       return false;
	    }
		return startDrag (v);
	}

	public boolean startDrag (View v)
	{
	    // Let the DragController initiate a drag-drop sequence.
	    // I use the dragInfo to pass along the object being dragged.
	    // I'm not sure how the Launcher designers do this.
	    Object dragInfo = v;
	    mDragController.startDrag (v, dl, dragInfo, DragController.DRAG_ACTION_MOVE);
	    return true;
	}
	
	public void setDraggableViews(){
		for(int i = 0; i < path.size(); i++)
		{
			setView(i);
		}
	}
	
	public void setView(int item){
		switch(item)
		{
			case 0:
				drag1 = (ImageView)view.findViewById(R.id.drag1);
				drag1.setOnClickListener(this);
				drag1.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag1, Uri.parse(path.get(0)));
				drag1.setVisibility(View.VISIBLE);
				break;
			
			case 1:
				drag2 = (ImageView)view.findViewById(R.id.drag2);
				drag2.setOnClickListener(this);
				drag2.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag2, Uri.parse(path.get(1)));
				drag2.setVisibility(View.VISIBLE);
				break;
				
			case 2:
				drag3 = (ImageView)view.findViewById(R.id.drag3);
				drag3.setOnClickListener(this);
				drag3.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag3, Uri.parse(path.get(2)));
				drag3.setVisibility(View.VISIBLE);
				break;
				
			case 3:
				drag4 = (ImageView)view.findViewById(R.id.drag4);
				drag4.setOnClickListener(this);
				drag4.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag4, Uri.parse(path.get(3)));
				drag4.setVisibility(View.VISIBLE);
				break;
				
			case 4:
				drag5 = (ImageView)view.findViewById(R.id.drag5);
				drag5.setOnClickListener(this);
				drag5.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag5, Uri.parse(path.get(4)));
				drag5.setVisibility(View.VISIBLE);
				break;
				
			case 5:
				drag6 = (ImageView)view.findViewById(R.id.drag6);
				drag6.setOnClickListener(this);
				drag6.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag6, Uri.parse(path.get(5)));
				drag6.setVisibility(View.VISIBLE);
				break;
			
			case 6:
				drag7 = (ImageView)view.findViewById(R.id.drag7);
				drag7.setOnClickListener(this);
				drag7.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag7, Uri.parse(path.get(6)));
				drag7.setVisibility(View.VISIBLE);
				break;
				
			case 7:
				drag8 = (ImageView)view.findViewById(R.id.drag8);
				drag8.setOnClickListener(this);
				drag8.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag8, Uri.parse(path.get(7)));
				drag8.setVisibility(View.VISIBLE);
				break;
				
			case 8:
				drag9 = (ImageView)view.findViewById(R.id.drag9);
				drag9.setOnClickListener(this);
				drag9.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag9, Uri.parse(path.get(8)));
				drag9.setVisibility(View.VISIBLE);
				break;
				
			case 9:
				drag10 = (ImageView)view.findViewById(R.id.drag10);
				drag10.setOnClickListener(this);
				drag10.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag10, Uri.parse(path.get(9)));
				drag10.setVisibility(View.VISIBLE);
				break;
				
			case 10:
				drag11 = (ImageView)view.findViewById(R.id.drag11);
				drag11.setOnClickListener(this);
				drag11.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag11, Uri.parse(path.get(10)));
				drag11.setVisibility(View.VISIBLE);
				break;
			
			case 11:
				drag12 = (ImageView)view.findViewById(R.id.drag12);
				drag12.setOnClickListener(this);
				drag12.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag12, Uri.parse(path.get(11)));
				drag12.setVisibility(View.VISIBLE);
				break;
				
			case 12:
				drag13 = (ImageView)view.findViewById(R.id.drag13);
				drag13.setOnClickListener(this);
				drag13.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag13, Uri.parse(path.get(12)));
				drag13.setVisibility(View.VISIBLE);
				break;
				
			case 13:
				drag14 = (ImageView)view.findViewById(R.id.drag14);
				drag14.setOnClickListener(this);
				drag14.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag14, Uri.parse(path.get(13)));
				drag14.setVisibility(View.VISIBLE);
				break;
				
			case 14:
				drag15 = (ImageView)view.findViewById(R.id.drag15);
				drag15.setOnClickListener(this);
				drag15.setOnLongClickListener(this);
				bitItem.loadBitmapWithSize(drag15, Uri.parse(path.get(14)));
				drag15.setVisibility(View.VISIBLE);
				break;
				
			default:
				break;
		}
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
}
