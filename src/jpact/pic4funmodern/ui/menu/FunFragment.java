package jpact.pic4funmodern.ui.menu;

import jpact.pic4funmodern.ui.MainMenuFragment;
import jpact.pic4funmodern.ui.R;
import jpact.pic4funmodern.ui.popups.*;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FunFragment  extends Fragment implements OnClickListener{
	private static String TAG = "FunFragment";
	private static String EFFECT = "EFFECT";
	
	private Button ImageEffect;
	private Button Frames;
	private Button Grids;
	private Button Collage;
	private Button Draw4Fun;
	private Button Text;
	private Intent intent;
	
	private int duration=5000;
	private Context context;
  	public GridBitmapDialogFragment nf;
  	public EditBitmapDialogFragment editDialog;
	 /**
     * Empty constructor as per the Fragment documentation
     */
    public FunFragment() {}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
       
    }
    
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	 
	final View layoutView = inflater.inflate(R.layout.fun_option, container, false);
	context = FunFragment.this.getActivity();
	
 	//Declarations
	ImageEffect = (Button)layoutView.findViewById(R.id.funimageeffect);
	Frames = (Button)layoutView.findViewById(R.id.funframe);
	Grids = (Button)layoutView.findViewById(R.id.fungrid);
	Collage = (Button)layoutView.findViewById(R.id.funcollage);
	Draw4Fun = (Button)layoutView.findViewById(R.id.fundraw);
	Text = (Button)layoutView.findViewById(R.id.funtext);
	//Setters
	ImageEffect.setOnClickListener(this);
	Frames.setOnClickListener(this);
	Grids.setOnClickListener(this);
	Collage.setOnClickListener(this);
	Draw4Fun.setOnClickListener(this);
	Text.setOnClickListener(this);
	return layoutView;
 }
    
    @Override
    public void onResume() {
        super.onResume();
//        EditBitmapDialogFragment editDialog = EditBitmapDialogFragment.newInstance(photoUri,R.layout.upload_options);
		
    }


	@Override
	public void onClick(View view) {
		switch(view.getId())
		{
		case R.id.funimageeffect:
			intent = new Intent(context, PhotosActivity.class);
			intent.putExtra("TAG", TAG);
			intent.putExtra(EFFECT, "ImageEffects");
			startActivity(intent);
			break;
		
		case R.id.funframe:
			intent = new Intent(context, PhotosActivity.class);
			intent.putExtra("TAG", TAG);
			intent.putExtra(EFFECT, "Frames");
			startActivity(intent);
			break;
		
		case R.id.fungrid:
//			intent = new Intent(context, PhotosActivity.class);
//			intent.putExtra("TAG", TAG);
//			intent.putExtra(EFFECT, "Grids");
//			startActivity(intent);
////			nf = GridBitmapDialogFragment.newInstance(TAG);
////		    nf.show(getActivity().getSupportFragmentManager(), TAG);
			editDialog = EditBitmapDialogFragment.newInstance(null,R.layout.editbitmap,TAG,"Grids");
			editDialog.show(getActivity().getSupportFragmentManager(), TAG);
			break;
			
		case R.id.funcollage:
			editDialog = EditBitmapDialogFragment.newInstance(null,R.layout.editbitmap,TAG,"Collages");
			editDialog.show(getActivity().getSupportFragmentManager(), TAG);
//			intent = new Intent(context, PhotosActivity.class);
//			intent.putExtra("TAG", TAG);
//			intent.putExtra(EFFECT, "Collages");
//			startActivity(intent);
			break;
			
		case R.id.fundraw:
			intent = new Intent(context, PhotosActivity.class);
			intent.putExtra("TAG", TAG);
			intent.putExtra(EFFECT, "Draw4Fun");
			startActivity(intent);
			break;
			
		case R.id.funtext:
			intent = new Intent(context, PhotosActivity.class);
			intent.putExtra("TAG", TAG);
			intent.putExtra(EFFECT, "Text");
			startActivity(intent);
			break;
			
		default:
			break;
		}
	
	}
}
