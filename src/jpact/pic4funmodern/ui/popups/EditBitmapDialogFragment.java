package jpact.pic4funmodern.ui.popups;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import jpact.pic4funmodern.constant.Field;
import jpact.pic4funmodern.ui.R;
import jpact.pic4funmodern.util.ConnectionDetection;
import jpact.pic4funmodern.util.FriendArrayList;
import jpact.pic4funmodern.util.FriendData;
import jpact.pic4funmodern.util.MediaUtility;
import jpact.pic4funmodern.util.bitmap.*;
import jpact.pic4funmodern.util.checker.PackageCheck;
import jpact.pic4funmodern.ui.*;
import jpact.pic4funmodern.ui.sharing.utils.*;
import jpact.pic4funmodern.ui.menu.*;
import jpact.pic4funmodern.ui.quickedit.*;

@SuppressLint("NewApi")
public class EditBitmapDialogFragment extends DialogFragment implements OnClickListener{
	private static final String TAG = "EditBitmapDialogFragment";
	
	public PhotoEffects photoEffects;
	public static Uri cropUri;
	private static Uri photoUri, wallpaperUri, origUri;
	private static int resID;
	public static String resourceTag;
	public static String effectTag;
	public static Bitmap gridBitmap, wallpaperBitmap;
	public BitmapItem bitmapItem;
	
	public static Bitmap origPhoto,tempPhoto,thePhoto;
	
	public TextView label;
	public ImageView grid1x2l, grid2x2l, grid1x2p, grid2x2p, collageImage;
	public static ImageView  image;
	public RelativeLayout collageLayout;
	public Button reset, save, original, share, camera;
	private ScrollView imageEffects, frames, grids, collages, draw4Fun, addtext;
	private ImageView Up, Down, Left, Right, quickedit;
	public ViewFlipper viewFlipper;
	
	//Quick Edit
  	private QuickAction quickAction;
  	
  	//Wallpaper
  	WallpaperManager wallpaperManager;
	//Button set
	private Button fcSet;
  	private Button btn_drawing_feature;  	
	
  	public View mainView;

  	public CustomDialogFragment customDialogFragment;
  	public GridBitmapDialogFragment nf;
  	public CollageBitmapDialogFragment  cb;
	private Intent intent;
	private ConnectionDetection cd;
	private SaveBitmap saveBitmap;
	public int backgroundID;
	public MediaUtility mUtility;
	
	public Dialog upload_dialog;
	public EditText updlg_caption;
	
	//Text function
	Typeface tfFont;
	

	//Files initialize
	public MediaUtility mediaUtility;
	public PackageCheck packageCheck;
	/**
     * Empty constructor as per the Fragment documentation
     */
	public EditBitmapDialogFragment(){}
	
	public static EditBitmapDialogFragment newInstance(Uri localphotoUri, int localresID, String tag, String effect) {
		EditBitmapDialogFragment frag = new EditBitmapDialogFragment();
		
        final Bundle args = new Bundle();
        photoUri = localphotoUri;
        resID = localresID;
        resourceTag = tag;
        effectTag = effect; 
       
        frag.setArguments(args);
        return frag;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setStyle(STYLE_NO_TITLE, 0); // remove title from dialogfragment
	}
	
	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
	    	View view = inflater.inflate(resID, container);
	    	wallpaperManager = WallpaperManager.getInstance(getActivity());
	    	
	    	mUtility = new MediaUtility(getActivity());
	    	saveBitmap = new SaveBitmap(getActivity());
	        viewFlipper = (ViewFlipper)view.findViewById(R.id.viewflipper);
	        
	        camera = (Button) view.findViewById(R.id.camera);
	        share = (Button)view.findViewById(R.id.share);
	        original = (Button)view.findViewById(R.id.original);
	        save = (Button)view.findViewById(R.id.save);
	        quickedit = (ImageView) view.findViewById(R.id.quickedit);
	        collageLayout = (RelativeLayout) view.findViewById(R.id.collageLayout);
	        
	        share.setOnClickListener(this);
	        original.setOnClickListener(this);
	        save.setOnClickListener(this);
	        collageLayout.setOnClickListener(this);
	        quickedit.setOnClickListener(this);
	        
	        cd= new ConnectionDetection(getActivity());
	    	((Jpact) getActivity().getApplication()).tagged_friends_id = new ArrayList<String>();
			((Jpact) getActivity().getApplication()).tagged_friends_name = new ArrayList<String>();
			
	        if(effectTag != "Grids" && effectTag != "Draw4Fun" && effectTag != "Collages"){
	        image = (ImageView) view.findViewById(R.id.image);
	        bitmapItem = new BitmapItem(getActivity());
	        bitmapItem.loadBitmap(image, photoUri);
	        backgroundID = R.drawable.collagebday1;
	        origPhoto = bitmapItem.bitmap;
	        tempPhoto = bitmapItem.bitmap;
	        }
	        
	        if(resourceTag == "CameraActivity"){
	        	camera.setOnClickListener(this);
	        }
	        else
	        	camera.setVisibility(View.GONE);
	        
	        
	        initArrowPortrait(view);
	        initIE(view);
	        initFrame(view);
	        initGrid(view);
	        initCollage(view);
	        initdraw4Fun(view);
	        inittext(view);
	        if(effectTag != null)
	        {
	        	initSpecificEffect(effectTag,0);
	        }
			return view;
				
	    }
	
	/**
     * Populate image using a url from extras, use the convenience factory method
     */
 
    
    @Override
    public void onDestroy() {
        super.onDestroy();
//        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//        ft.addToBackStack(null); 
        if(resourceTag == "CameraActivity")
        getActivity().finish();
        
    }
   
    /**
     * Initialization section
     * */
    public void initArrowPortrait(View view){
    	imageEffects = (ScrollView) view.findViewById(R.id.image_effects);
    	frames = (ScrollView) view.findViewById(R.id.frames);
    	grids = (ScrollView) view.findViewById(R.id.image_grids);
    	collages = (ScrollView) view.findViewById(R.id.image_collage);
    	draw4Fun = (ScrollView) view.findViewById(R.id.image_draws);
    	addtext = (ScrollView) view.findViewById(R.id.addtext);
    	label = (TextView)view.findViewById(R.id.filmstrip_label);
    	
    	Up = (ImageView)view.findViewById(R.id.btnup);
    	Down = (ImageView)view.findViewById(R.id.btndown);
    	
    	Up.setOnClickListener(this);
    	Down.setOnClickListener(this);
    }
    
    public void initArrowLandscape(View view){
    	imageEffects = (ScrollView) view.findViewById(R.id.image_effects);
    	frames = (ScrollView) view.findViewById(R.id.frames);
    	grids = (ScrollView) view.findViewById(R.id.image_grids);
    	collages = (ScrollView) view.findViewById(R.id.image_collage);
    	draw4Fun = (ScrollView) view.findViewById(R.id.image_draws);
    	label = (TextView)view.findViewById(R.id.filmstrip_label);
    	
    	ImageView Left = (ImageView)view.findViewById(R.id.btnleft);
    	ImageView Right = (ImageView)view.findViewById(R.id.btnright);
    }

    public void initIE(View view){
    	
    	Button btn_e_original = (Button) view.findViewById(R.id.btn_e_original);
    	Button btn_e_roundcorner = (Button) view.findViewById(R.id.btn_e_roundcorner);
    	Button btn_e_sepia1 = (Button) view.findViewById(R.id.btn_e_sepia1);
    	Button btn_e_invert = (Button) view.findViewById(R.id.btn_e_invert);
    	Button btn_e_tint = (Button) view.findViewById(R.id.btn_e_tint);
    	Button btn_e_contrast = (Button)view.findViewById(R.id.btn_e_contrast);
    	Button btn_e_sharpen = (Button) view.findViewById(R.id.btn_e_sharpen);
    	Button btn_e_engrave = (Button) view.findViewById(R.id.btn_e_engrave);
    	Button btn_e_boost1 = (Button) view.findViewById(R.id.btn_e_boost1);
    	Button btn_e_saturation = (Button) view.findViewById(R.id.btn_e_saturation);
    	Button btn_e_sepia4 = (Button) view.findViewById(R.id.btn_e_sepia4);
    	Button btn_e_sepia3 = (Button) view.findViewById(R.id.btn_e_sepia3);
    	Button btn_e_shading2 = (Button) view.findViewById(R.id.btn_e_shading2);
    	Button btn_e_hue = (Button) view.findViewById(R.id.btn_e_hue);
    	Button btn_e_boost2 = (Button) view.findViewById(R.id.btn_e_boost2);
    	Button btn_e_shading3 = (Button) view.findViewById(R.id.btn_e_shading3);
    	Button btn_e_sepia2 = (Button) view.findViewById(R.id.btn_e_sepia2);
    	Button btn_e_boost3 = (Button) view.findViewById(R.id.btn_e_boost3);
    	Button btn_e_shading1 = (Button) view.findViewById(R.id.btn_e_shading1);
    	
    	btn_e_original.setOnClickListener(this);
    	btn_e_roundcorner.setOnClickListener(this);
    	btn_e_sepia1.setOnClickListener(this);
    	btn_e_invert.setOnClickListener(this);
    	btn_e_tint.setOnClickListener(this);
    	btn_e_contrast.setOnClickListener(this);
    	btn_e_sharpen.setOnClickListener(this);
    	btn_e_engrave.setOnClickListener(this);
    	btn_e_boost1.setOnClickListener(this);
    	btn_e_saturation.setOnClickListener(this);
    	btn_e_sepia4.setOnClickListener(this);
    	btn_e_sepia3.setOnClickListener(this);
    	btn_e_shading2.setOnClickListener(this);
    	btn_e_hue.setOnClickListener(this);
    	btn_e_boost2.setOnClickListener(this);
    	btn_e_shading3.setOnClickListener(this);
    	btn_e_sepia2.setOnClickListener(this);
    	btn_e_boost3.setOnClickListener(this);
    	btn_e_shading1.setOnClickListener(this);
    	
    }
    
    public void initFrame(View view){
    	
    	Button btn_e_frame1 = (Button) view.findViewById(R.id.btn_e_frame1);
    	Button btn_e_frame2 = (Button) view.findViewById(R.id.btn_e_frame2);
    	Button btn_e_frame3 = (Button) view.findViewById(R.id.btn_e_frame3);
    	Button btn_e_frame4 = (Button) view.findViewById(R.id.btn_e_frame4);
    	Button btn_e_frame5 = (Button) view.findViewById(R.id.btn_e_frame5);
    	Button btn_e_frame6 = (Button) view.findViewById(R.id.btn_e_frame6);
    	Button btn_e_frame7 = (Button) view.findViewById(R.id.btn_e_frame7);
    	Button btn_e_frame8 = (Button) view.findViewById(R.id.btn_e_frame8);
    	Button btn_e_frame9 = (Button) view.findViewById(R.id.btn_e_frame9);
    	Button btn_e_frame10 = (Button) view.findViewById(R.id.btn_e_frame10);
    	Button btn_e_frame11 = (Button) view.findViewById(R.id.btn_e_frame11);
    	Button btn_e_frame12 = (Button) view.findViewById(R.id.btn_e_frame12);
    	Button btn_e_frame13 = (Button) view.findViewById(R.id.btn_e_frame13);
    	Button btn_e_frame14 = (Button) view.findViewById(R.id.btn_e_frame14);
    	Button btn_e_frame15 = (Button) view.findViewById(R.id.btn_e_frame15);
    	Button btn_e_frame16 = (Button) view.findViewById(R.id.btn_e_frame16);
    	Button btn_e_frame17 = (Button) view.findViewById(R.id.btn_e_frame17);
    	Button btn_e_frame18 = (Button) view.findViewById(R.id.btn_e_frame18);
    	Button btn_e_frame19 = (Button) view.findViewById(R.id.btn_e_frame19);
    	Button btn_e_frame20 = (Button) view.findViewById(R.id.btn_e_frame20);
    	Button btn_e_frame21 = (Button) view.findViewById(R.id.btn_e_frame21);
    	Button btn_e_frame22 = (Button) view.findViewById(R.id.btn_e_frame22);
    	Button btn_e_frame23 = (Button) view.findViewById(R.id.btn_e_frame23);
    	Button btn_e_frame24 = (Button) view.findViewById(R.id.btn_e_frame24);
    	Button btn_e_frame25 = (Button) view.findViewById(R.id.btn_e_frame25);
    	
    	btn_e_frame1.setOnClickListener(this);
    	btn_e_frame2.setOnClickListener(this);
    	btn_e_frame3.setOnClickListener(this);
    	btn_e_frame4.setOnClickListener(this);
    	btn_e_frame5.setOnClickListener(this);
    	btn_e_frame6.setOnClickListener(this);
    	btn_e_frame7.setOnClickListener(this);
    	btn_e_frame8.setOnClickListener(this);
    	btn_e_frame9.setOnClickListener(this);
    	btn_e_frame10.setOnClickListener(this);
    	btn_e_frame11.setOnClickListener(this);
    	btn_e_frame12.setOnClickListener(this);
    	btn_e_frame13.setOnClickListener(this);
    	btn_e_frame14.setOnClickListener(this);
    	btn_e_frame15.setOnClickListener(this);
    	btn_e_frame16.setOnClickListener(this);
    	btn_e_frame17.setOnClickListener(this);
    	btn_e_frame18.setOnClickListener(this);
    	btn_e_frame19.setOnClickListener(this);
    	btn_e_frame20.setOnClickListener(this);
    	btn_e_frame21.setOnClickListener(this);
    	btn_e_frame22.setOnClickListener(this);
    	btn_e_frame23.setOnClickListener(this);
    	btn_e_frame24.setOnClickListener(this);
    	btn_e_frame25.setOnClickListener(this);
    	
    }
    
	public void initGrid(View view){
    	
    	Button btn_e_grid1 = (Button) view.findViewById(R.id.btn_e_grid1);
    	Button btn_e_grid2 = (Button) view.findViewById(R.id.btn_e_grid2);
    	Button btn_e_grid3 = (Button) view.findViewById(R.id.btn_e_grid3);
    	Button btn_e_grid4 = (Button) view.findViewById(R.id.btn_e_grid4);
    	
    	ImageView grid1x2l = (ImageView) view.findViewById(R.id.gridLayout1x2l);
    	ImageView grid2x2l = (ImageView) view.findViewById(R.id.gridLayout2x2l);
    	ImageView grid1x2p = (ImageView) view.findViewById(R.id.gridLayout1x2p);
    	ImageView grid2x2p = (ImageView) view.findViewById(R.id.gridLayout2x2p);
    	
    	
    	btn_e_grid1.setOnClickListener(this);
    	btn_e_grid2.setOnClickListener(this);
    	btn_e_grid3.setOnClickListener(this);
    	btn_e_grid4.setOnClickListener(this);
    	grid1x2l.setOnClickListener(this);
    	grid2x2l.setOnClickListener(this);
    	grid1x2p.setOnClickListener(this);
    	grid2x2p.setOnClickListener(this);
    
    }
	
	public void initCollage(View view){

		Button btn_e_collage1 = (Button) view.findViewById(R.id.btn_e_collage1);
    	Button btn_e_collage2 = (Button) view.findViewById(R.id.btn_e_collage2);
    	Button btn_e_collage3 = (Button) view.findViewById(R.id.btn_e_collage3);
    	Button btn_e_collage4 = (Button) view.findViewById(R.id.btn_e_collage4);
    	Button btn_e_collage5 = (Button) view.findViewById(R.id.btn_e_collage5);
    	Button btn_e_collage6 = (Button) view.findViewById(R.id.btn_e_collage6);
    	Button btn_e_collage7 = (Button) view.findViewById(R.id.btn_e_collage7);
    	Button btn_e_collage8 = (Button) view.findViewById(R.id.btn_e_collage8);
    	Button btn_e_collage9 = (Button) view.findViewById(R.id.btn_e_collage9);
    	Button btn_e_collage10 = (Button) view.findViewById(R.id.btn_e_collage10);
    	Button btn_e_collage11 = (Button) view.findViewById(R.id.btn_e_collage11);
    	Button btn_e_collage12 = (Button) view.findViewById(R.id.btn_e_collage12);
    	Button btn_e_collage13 = (Button) view.findViewById(R.id.btn_e_collage13);
    	Button btn_e_collage14 = (Button) view.findViewById(R.id.btn_e_collage14);
    	Button btn_e_collage15 = (Button) view.findViewById(R.id.btn_e_collage15);
    	Button btn_e_collage16 = (Button) view.findViewById(R.id.btn_e_collage16);
    	Button btn_e_collage17 = (Button) view.findViewById(R.id.btn_e_collage17);
    	Button btn_e_collage18 = (Button) view.findViewById(R.id.btn_e_collage18);
    	Button btn_e_collage19 = (Button) view.findViewById(R.id.btn_e_collage19);
    	Button btn_e_collage20 = (Button) view.findViewById(R.id.btn_e_collage20);
    	Button btn_e_collage21 = (Button) view.findViewById(R.id.btn_e_collage21);
    	Button btn_e_collage22 = (Button) view.findViewById(R.id.btn_e_collage22);
    	Button btn_e_collage23 = (Button) view.findViewById(R.id.btn_e_collage23);
    	Button btn_e_collage24 = (Button) view.findViewById(R.id.btn_e_collage24);
    	Button btn_e_collage25 = (Button) view.findViewById(R.id.btn_e_collage25);
    	Button btn_e_collage26 = (Button) view.findViewById(R.id.btn_e_collage26);
    	Button btn_e_collage27 = (Button) view.findViewById(R.id.btn_e_collage27);
    	Button btn_e_collage28 = (Button) view.findViewById(R.id.btn_e_collage28);
    	
    	btn_e_collage1.setOnClickListener(this);
    	btn_e_collage2.setOnClickListener(this);
    	btn_e_collage3.setOnClickListener(this);
    	btn_e_collage4.setOnClickListener(this);
    	btn_e_collage5.setOnClickListener(this);
    	btn_e_collage6.setOnClickListener(this);
    	btn_e_collage7.setOnClickListener(this);
    	btn_e_collage8.setOnClickListener(this);
    	btn_e_collage9.setOnClickListener(this);
    	btn_e_collage10.setOnClickListener(this);
    	btn_e_collage11.setOnClickListener(this);
    	btn_e_collage12.setOnClickListener(this);
    	btn_e_collage13.setOnClickListener(this);
    	btn_e_collage14.setOnClickListener(this);
    	btn_e_collage15.setOnClickListener(this);
    	btn_e_collage16.setOnClickListener(this);
    	btn_e_collage17.setOnClickListener(this);
    	btn_e_collage18.setOnClickListener(this);
    	btn_e_collage19.setOnClickListener(this);
    	btn_e_collage20.setOnClickListener(this);
    	btn_e_collage21.setOnClickListener(this);
    	btn_e_collage22.setOnClickListener(this);
    	btn_e_collage23.setOnClickListener(this);
    	btn_e_collage24.setOnClickListener(this);
    	btn_e_collage25.setOnClickListener(this);
    	btn_e_collage26.setOnClickListener(this);
    	btn_e_collage27.setOnClickListener(this);
    	btn_e_collage28.setOnClickListener(this);
    
    }
    
    public void initdraw4Fun(View view){
    	Button btn_drawing_feature = (Button) view.findViewById(R.id.btn_drawing_feature);
    	btn_drawing_feature.setOnClickListener(this);
    }
    
    public void inittext(View view){
    	Button normal = (Button) view.findViewById(R.id.normal);
    	Button chantelli = (Button) view.findViewById(R.id.chantelli);
    	Button anke = (Button) view.findViewById(R.id.ankecalligraph);
    	Button impact = (Button) view.findViewById(R.id.impact);
    	Button calligra = (Button) view.findViewById(R.id.calligra);
    	Button verase = (Button) view.findViewById(R.id.verase);
    	
    	normal.setOnClickListener(this);
    	chantelli.setOnClickListener(this);
    	anke.setOnClickListener(this);
    	impact.setOnClickListener(this);
    	calligra.setOnClickListener(this);
    	verase.setOnClickListener(this);
    	
    	//Chantelli
    	tfFont= Typeface.createFromAsset(getActivity().getAssets(), "Chantelli_Antiqua.ttf");  
    	chantelli.setTypeface(tfFont);  
    	//AnkeCalligraph
    	tfFont= Typeface.createFromAsset(getActivity().getAssets(), "AnkeCalligraph.TTF");  
    	anke.setTypeface(tfFont); 
    	//Calligra
    	tfFont= Typeface.createFromAsset(getActivity().getAssets(), "calligra.ttf");  
    	calligra.setTypeface(tfFont); 
    	//Impact
    	tfFont= Typeface.createFromAsset(getActivity().getAssets(), "Impact.ttf");  
    	impact.setTypeface(tfFont); 
    	//VeraSe
    	tfFont= Typeface.createFromAsset(getActivity().getAssets(), "VeraSe.ttf");  
    	verase.setTypeface(tfFont); 
    }
    
	public void initSpecificEffect(String effect,int orientation){
		
		if(effect.equals("ImageEffects"))
		{
			showImageEffects(orientation);
			Up.setVisibility(View.INVISIBLE);
			Down.setVisibility(View.INVISIBLE);
		}
		else if(effect.equals("Frames"))
		{
			showFrames(orientation);
			Up.setVisibility(View.INVISIBLE);
			Down.setVisibility(View.INVISIBLE);
		}
		else if(effect.equals("Grids"))
		{
			showGrids(orientation);
			Up.setVisibility(View.INVISIBLE);
			Down.setVisibility(View.INVISIBLE);
		}
		else if(effect.equals("Collages"))
		{
			showCollages(orientation);
			Up.setVisibility(View.INVISIBLE);
			Down.setVisibility(View.INVISIBLE);
		}
		else if(effect.equals("Draw4Fun"))
		{
			showDraw4Fun(orientation);
			Up.setVisibility(View.INVISIBLE);
			Down.setVisibility(View.INVISIBLE);
		}
		else if(effect.equals("Text"))
		{
			showText(orientation);
			Up.setVisibility(View.INVISIBLE);
			Down.setVisibility(View.INVISIBLE);
		}
		
	}

	/**
	 * Show Section
	 * */
	public void showImageEffects(int i){
		
		if(i == 0)
		{
			imageEffects.setVisibility(View.VISIBLE);
			frames.setVisibility(View.GONE);
			grids.setVisibility(View.GONE);
			collages.setVisibility(View.GONE);
			draw4Fun.setVisibility(View.GONE); //Added by Adrian
			addtext.setVisibility(View.GONE); // Added by RJ
		}
		
		else if(i == 1)
		{
			imageEffects.setVisibility(View.VISIBLE);
			frames.setVisibility(View.GONE);
			grids.setVisibility(View.GONE);
			collages.setVisibility(View.GONE);
			draw4Fun.setVisibility(View.GONE); //Added by Adrian
			addtext.setVisibility(View.GONE); // Added by RJ
		}
		
		switchMainImage(0);
		label.setText(R.string.effectie);
	}
	
	public void showFrames(int i){
		
		if(i == 0)
		{
			imageEffects.setVisibility(View.GONE);
			frames.setVisibility(View.VISIBLE);
			grids.setVisibility(View.GONE);
			collages.setVisibility(View.GONE);
			draw4Fun.setVisibility(View.GONE); //Added by Adrian
			addtext.setVisibility(View.GONE); // Added by RJ
		}
		
		else if(i == 1)
		{
			imageEffects.setVisibility(View.GONE);
			frames.setVisibility(View.VISIBLE);
			grids.setVisibility(View.GONE);
			collages.setVisibility(View.GONE);
			draw4Fun.setVisibility(View.GONE); //Added by Adrian
			addtext.setVisibility(View.GONE); // Added by RJ
		}
		
		switchMainImage(0);
		label.setText(R.string.effectframe);
	}
	
	public void showGrids(int i){
		
		if(i == 0)
		{
			imageEffects.setVisibility(View.GONE);
			frames.setVisibility(View.GONE);
			grids.setVisibility(View.VISIBLE);
			collages.setVisibility(View.GONE);
			draw4Fun.setVisibility(View.GONE); //Added by Adrian
			addtext.setVisibility(View.GONE); // Added by RJ
			
		}
		
		else if(i == 1)
		{
			imageEffects.setVisibility(View.GONE);
			frames.setVisibility(View.GONE);
			grids.setVisibility(View.VISIBLE);
			collages.setVisibility(View.GONE);
			draw4Fun.setVisibility(View.GONE); //Added by Adrian
			addtext.setVisibility(View.GONE); // Added by RJ
		}
		
		switchMainImage(1);
		label.setText(R.string.effectgrid);
	}
	
	public void showCollages(int i){
		
		if(i == 0)
		{
			imageEffects.setVisibility(View.GONE);
			frames.setVisibility(View.GONE);
			grids.setVisibility(View.GONE);
			collages.setVisibility(View.VISIBLE);
			draw4Fun.setVisibility(View.GONE); //Added by Adrian
			addtext.setVisibility(View.GONE); // Added by RJ
		}
		
		else if(i == 1)
		{
			imageEffects.setVisibility(View.GONE);
			frames.setVisibility(View.GONE);
			grids.setVisibility(View.GONE);
			collages.setVisibility(View.VISIBLE);
			draw4Fun.setVisibility(View.GONE); //Added by Adrian
			addtext.setVisibility(View.GONE); // Added by RJ
		}
		
		switchMainImage(5);
		label.setText(R.string.effectcollage);
	}
	
	public void showDraw4Fun(int i){
		if(i == 0)
		{
			imageEffects.setVisibility(View.GONE);
			frames.setVisibility(View.GONE);
			grids.setVisibility(View.GONE);
			collages.setVisibility(View.GONE);
			draw4Fun.setVisibility(View.VISIBLE);
			addtext.setVisibility(View.GONE); // Added by RJ
		}
		
		else if(i == 1)
		{
			imageEffects.setVisibility(View.GONE);
			frames.setVisibility(View.GONE);
			grids.setVisibility(View.GONE);
			collages.setVisibility(View.GONE);
			draw4Fun.setVisibility(View.VISIBLE);
			addtext.setVisibility(View.GONE); // Added by RJ
		}
		
		switchMainImage(0);
		label.setText(R.string.effectdraw);
		
	}
	
	public void showText(int i){
		if(i == 0)
		{
			imageEffects.setVisibility(View.GONE);
			frames.setVisibility(View.GONE);
			grids.setVisibility(View.GONE);
			collages.setVisibility(View.GONE);
			draw4Fun.setVisibility(View.GONE);
			addtext.setVisibility(View.VISIBLE); // Added by RJ
		}
		
		else if(i == 1)
		{
			imageEffects.setVisibility(View.GONE);
			frames.setVisibility(View.GONE);
			grids.setVisibility(View.GONE);
			collages.setVisibility(View.GONE);
			draw4Fun.setVisibility(View.GONE);
			addtext.setVisibility(View.VISIBLE); // Added by RJ
		}
		
		switchMainImage(0);
		label.setText(R.string.effecttext);
		
	}

	//Task when an specific effect is applied to photo
  	public class EffectsTask extends AsyncTask<String, Void, String> {

  		ProgressDialog mProgress;
  		private String effect_type = "";
  		
  		public EffectsTask(String effect) {
  			this.effect_type = effect;
  		}
  		
      	@Override
      	protected void onPreExecute() {
      		super.onPreExecute();
      		
      		mProgress = new ProgressDialog(getActivity());
  			mProgress.setIndeterminate(true);
  			mProgress.setCancelable(false);
  			mProgress.setMessage("Applying effect to photo...");
  			mProgress.show();
      	}
  		
  		@Override
      	protected String doInBackground(String... urls) {
  			//Sharpen
  			if (effect_type.equals("sharpen")) {
  				tempPhoto = photoEffects.doSharpenToPhoto(tempPhoto, 11);
//  				tempPhoto = doSharpenToPhoto(tempPhoto, kernalBlur); this code will use the other sharpen function
  				Log.i("Effects", "Sharpen is happenning");
  			} 
  			//Invert
  			if (effect_type.equals("invert")) {
  				tempPhoto = photoEffects.doInvertToPhoto(tempPhoto);
  				Log.i("Effects", "Sharpen is happenning");
  			}
  			//Tint
  			if (effect_type.equals("tint")) {
  				tempPhoto = photoEffects.doTintToPhoto(tempPhoto, 180);
  				Log.i("Effects", "Sharpen is happenning");
  			}
  			//Engrave
  			if (effect_type.equals("engrave")) {
  				tempPhoto = photoEffects.doEngraveToPhoto(tempPhoto);
  			}
  			//Contrast
  			if (effect_type.equals("contrast")) {
  				tempPhoto = photoEffects.doContrastToPhoto(tempPhoto, 50);
  			}
  			//Sepia 1
  			if (effect_type.equals("sepia_1")) {
  				tempPhoto = photoEffects.doSepiaToPhoto(tempPhoto, 1, 60, 30, 0);
  			}
  			//Sepia 2 - Sophia
  			if (effect_type.equals("sepia_2")) {
  				tempPhoto = photoEffects.doSepiaToPhoto(tempPhoto, 1, 120, 60, 0);
  			}
  			//Sepia 3 - Emma
  			if (effect_type.equals("sepia_3")) {
  				tempPhoto = photoEffects.doSepiaToPhoto(tempPhoto, 1, 0, 60, 0);
  			}
  			//Sepia 4 - Emily
  			if (effect_type.equals("sepia_4")) {
  				tempPhoto = photoEffects.doSepiaToPhoto(tempPhoto, 1, 0, 0, 60);
  			}
  			//Round Corner - Abigail
  			if (effect_type.equals("round_corner")) {
  				tempPhoto = photoEffects.doRoundCornerToPhoto(tempPhoto, 45);
  			}
  			//Boost Up 1 Red - Alexis
  			if (effect_type.equals("boost_up1")) {
  				tempPhoto = photoEffects.doBoostToPhoto(tempPhoto, 1, Float.valueOf("1.50"));
  			}
  			//Boost Up 2 Green - Kristy
  			if (effect_type.equals("boost_up2")) {
  				tempPhoto = photoEffects.doBoostToPhoto(tempPhoto, 2, Float.valueOf("0.50"));
  			}
  			//Boost Up 3 Blue -  Yenny
  			if (effect_type.equals("boost_up3")) {
  				tempPhoto = photoEffects.doBoostToPhoto(tempPhoto, 3, Float.valueOf("0.67"));
  			}
  			//Hue Filter - Jennifer
  			if (effect_type.equals("hue")) {
  				tempPhoto = photoEffects.doHueToPhoto(tempPhoto, 9);
  			}
  			//Saturation Filter - Chloe
  			if (effect_type.equals("saturation")) {
  				tempPhoto = photoEffects.doSaturationToPhoto(tempPhoto, 2);
  			}
  			//Shading Filter 1 Sky Blue - Zoey
  			if (effect_type.equals("shading1")) {
  				tempPhoto = photoEffects.doShadingToPhoto(tempPhoto, Color.parseColor("#3BB9FF"));
  			}
  			//Shading Filter 2 Lawn Green - Hannah
  			if (effect_type.equals("shading2")) {
  				tempPhoto = photoEffects.doShadingToPhoto(tempPhoto, Color.parseColor("#87F717"));
  			}
  			//Shading Filter 3 Violet - Mia
  			if (effect_type.equals("shading3")) {
  				tempPhoto = photoEffects.doShadingToPhoto(tempPhoto, Color.parseColor("#8D38C9"));
  			}
  			
  			//Birthday Frame 1
			if (effect_type.equals("birthday1")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framebirthday1, getActivity());
			}
			
			//Birthday Frame 2
			if (effect_type.equals("birthday2")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framebirthday2, getActivity());
			}
			
			//Birthday Frame 3
			if (effect_type.equals("birthday3")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framebirthday3, getActivity());
			}
  			
  			//Candy Frame 1
			if (effect_type.equals("candy1")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framecandy1, getActivity());
			}
			
			//Candy Frame 2
			if (effect_type.equals("candy2")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framecandy1, getActivity());
			}
			
			//Christmas Frame 1
			if (effect_type.equals("christmas1")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framechristmas1, getActivity());
			}
			
			//Christmas Frame 2
			if (effect_type.equals("christmas2")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framechristmas2, getActivity());
			}
			
			//Christmas Frame 3
			if (effect_type.equals("christmas3")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framechristmas3, getActivity());
			}
			
			//Christmas Frame 4
			if (effect_type.equals("christmas4")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framechristmas4, getActivity());
			}
			
			//Clam Frame 
			if (effect_type.equals("clam")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.frameclam, getActivity());
			}
			
			//Flower Frame 1
			if (effect_type.equals("flower1")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.frameflower1, getActivity());
			}
			
			//Flower Frame 2
			if (effect_type.equals("flower2")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.frameflower2, getActivity());
			}
			
			//Gold Frame 1
			if (effect_type.equals("gold1")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framegold1, getActivity());
			}
			
			//Gold Frame 2
			if (effect_type.equals("gold2")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framegold2, getActivity());
			}
			
			//Hearts Frame 1
			if (effect_type.equals("hearts1")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framehearts1, getActivity());
			}
			
			//Hearts Frame 2
			if (effect_type.equals("hearts2")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framehearts2, getActivity());
			}
			
			//Holloween Frame 1
			if (effect_type.equals("holloween1")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.frameholloween1, getActivity());
			}
			
			//Holloween Frame 2
			if (effect_type.equals("holloween2")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.frameholloween2, getActivity());
			}
			
			//Holloween Frame 3
			if (effect_type.equals("holloween3")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.frameholloween3, getActivity());
			}
			
			//Lady and Tree Frame 
			if (effect_type.equals("ladytree")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.frameladyandtree, getActivity());
			}
			
			//Leaf Frame 
			if (effect_type.equals("leaf")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.frameleaf, getActivity());
			}
			
			//Mag Frame 
			if (effect_type.equals("mag")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framemag, getActivity());
			}
			
			//Old Frame 
			if (effect_type.equals("old")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.frameold, getActivity());
			}
			
			//Pink Frame 
			if (effect_type.equals("pink")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framepink, getActivity());
			}
			
			//Old Frame 
			if (effect_type.equals("wanted")) {
				tempPhoto = photoEffects.doFrame(tempPhoto, R.drawable.framewanted, getActivity());
			}
  			
  			return "";
      	}
  		
  		@Override
      	protected void onPostExecute(String result) {
      		super.onPostExecute(result);
      		
//      		if (mProgress.getWindow() != null) {
  				mProgress.dismiss();
//  			}
      		image.setImageBitmap(tempPhoto);
      		imageisChanged();
  		}
  	}
	
	public void switchMainImage(int viewItem){
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
		viewFlipper.setDisplayedChild(viewItem);
//		rotateLeftFrag(viewFlipper);
	}
	
	
	//Button set visibility
	public void setFunctionButtons(boolean saveVis, boolean originalVis){
		if(saveVis)
			save.setVisibility(View.VISIBLE);
		else 
			save.setVisibility(View.GONE);
		
		if(originalVis)
			original.setVisibility(View.VISIBLE);
		else
			original.setVisibility(View.GONE);
		
	}
	
	/**
	*The Save function
	*/
	public void saveTheImage()
	{
		saveBitmap.Save(tempPhoto);
		origPhoto = tempPhoto;
		imageisChanged();
	}
	
	public void imageisChanged(){
		if(tempPhoto != origPhoto)
			setFunctionButtons(true,true);
		else
			setFunctionButtons(false,false);
	}
	
	public boolean imageEdited()
	{
		return tempPhoto != origPhoto ? true:false;
	}
	
	
	/**
	 *  The crop function
	 * */
	//Dialog when the user wants to set unsaved photo as wallpaper
	private void notifyNotSavedViaSetAs() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setTitle(this.getResources().getString(R.string.app_name));
    	builder.setMessage("You did not save your edited photo. Last saved photo will be used if edited photo is not saved.");
    	builder.setCancelable(true);
    	builder.setPositiveButton("Save and Set as", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	    		/*savePhoto(); this code will save photo without using asynctask
				callSetAsIntent(photoUri);*/
//	    		new SaveTask("set_as").execute("");
	    		saveTheImage();
	    		callSetAsIntent(photoUri);
	    	}
    	});
    	
    	builder.setNegativeButton("Set as Without Saving", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	    		
	    		callSetAsIntent(photoUri);
	    	}
    	});
    	
    	AlertDialog alert = builder.create();
    	alert.show();
    }
	
	//Dialog when the user wants to crop unsaved photo
		private void notifyNotSavedViaCrop() {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	builder.setTitle(this.getResources().getString(R.string.app_name));
	    	builder.setMessage("You did not save your edited photo. Last saved photo will be used if edited photo is not saved.");
	    	builder.setCancelable(true);
	    	builder.setPositiveButton("Save and Crop", new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int id) {
		    		/*savePhoto(); this code will save photo without using asynctask
		    		callCropIntent(photoUri);*/
//		    		new SaveTask("crop").execute("");
		    		saveTheImage();
		    		callCropIntent(photoUri);
		    	}
	    	});
	    	
	    	builder.setNegativeButton("Crop Without Saving", new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int id) {
		    		callCropIntent(photoUri);
		    	}
	    	});
	    	
	    	AlertDialog alert = builder.create();
	    	alert.show();
	    }
		
	//Set As
	private void callSetAsIntent(Uri uri) {
	    Intent setAsIntent = new Intent(Intent.ACTION_ATTACH_DATA);
	    setAsIntent.setDataAndType(uri, "image/*");
	    setAsIntent.putExtra("mimeType", "image/*");
//	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK	| Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    getActivity().startActivity( setAsIntent);
	}
	
	//Crop
	private void callCropIntent(Uri uri) {
		if (!mUtility.isExternalStorageAvailable()) {	        
			Log.e("crop", "the effing crop 1");
			String message = "SD card is missing or currently not mounted. Cannot continue.";		
			customDialogFragment = CustomDialogFragment.newInstance(message,getActivity().getResources().getResourceName(R.string.app_name));
			customDialogFragment.show(getChildFragmentManager(), TAG);
			return;
		} 
		else {
			Log.e("crop", "the effing crop 2");
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setType("image/*");
	            
	        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);
	        int size = list.size();
	            
	        if (size == 0) {	   
	        	Log.e("crop", "the effing crop 8");
	        	String message = "Unable to find image crop app in your device.";
	        	customDialogFragment = CustomDialogFragment.newInstance(message,getActivity().getResources().getResourceName(R.string.app_name));
				customDialogFragment.show(getChildFragmentManager(), TAG);
				
	        	return;
	        } else {
	        	Log.e("crop", "the effing crop 9 "+ resourceTag+" , "+uri.toString());
	        	intent.setDataAndType(uri, "image/*");
//	        	intent.putExtra("outputX", 200);
//	            intent.putExtra("outputY", 200);
//	            intent.putExtra("aspectX", 1);
//	            intent.putExtra("aspectY", 1);
//	            intent.putExtra("scale", true);
//	            intent.putExtra("return-data", true);
//	            intent.putExtra("noFaceDetection", true);
	        	intent.putExtra("crop", "true");
    	        intent.putExtra("scale", "false");
    	        intent.putExtra("return-data", false);
    	        cropUri = mUtility.getOutputMediaFileUri(Field.MEDIA_TYPE_IMAGE); 
    	        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
    	        intent.putExtra("CROP", cropUri);
//    	        process_crop = true;
	            Intent i = new Intent(intent);
	            ResolveInfo res	= list.get(0);
	            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//	            i.putExtra("CROP", cropUri);	            
	            
	            if(resourceTag == "CameraActivity"){
	            	Log.i("Crop", cropUri.toString());
//	            	startActivityForResult(i, Field.CAMERA_CROP_REQUEST);
	            	CameraActivity.cropUri = cropUri;
	            	getActivity().startActivityForResult(i, Field.CAMERA_CROP_REQUEST);
	            }
	            else
	            {
	            	Log.i("Crop", cropUri.toString());
//	            	startActivityForResult(i, Field.CAMERA_CROP_REQUEST);
	            	PhotosActivity.cropUri = cropUri;
	            	FunActivity.cropUri = cropUri;
	            	Log.i("Crop", "before getActivity()....");
	            	getActivity().startActivityForResult(i, Field.NON_CAMERA_CROP_REQUEST);
	            	Log.i("Crop", "after getActivity()....");
	            	
	            }
	            if (size == 1) {
	            	
	            } 
	            
	        }
		}
	}
	
	/**
	 * Rotate function
	 * */
	//Rotate Left
	private void rotateLeft() {
		Matrix matrix = new Matrix();
		matrix.postRotate(-90);
		
		tempPhoto = Bitmap.createBitmap(tempPhoto, 0, 0, tempPhoto.getWidth(),  tempPhoto.getHeight(), matrix, true);
		image.setImageBitmap(tempPhoto);
//		tempPhoto = Bitmap.createBitmap(source, x, y, width, height, m, filter)
	}
	
	//Rotate Left
	private void rotateRight() {
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		
		tempPhoto = Bitmap.createBitmap(tempPhoto, 0, 0, tempPhoto.getWidth(),  tempPhoto.getHeight(), matrix, true);
		image.setImageBitmap(tempPhoto);
//			tempPhoto = Bitmap.createBitmap(source, x, y, width, height, m, filter)
	}
	
	/**
	 *  The sharing function
	 * */
	//Alert for sharing image
	private void shareImage() {
	    Intent shareIntent = new Intent(Intent.ACTION_SEND);
	    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	    shareIntent.setType("image/*");
	    //For a file in shared storage.  For data in private storage, use a ContentProvider.
//		    Uri uri = Uri.fromFile(getFileStreamPath(""));
	    shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
	    startActivity(Intent.createChooser(shareIntent, "Share photo via"));
	}
	
	//Alert for Facebook sharing. this is the alert where we put caption and tag friends. 
    private void showFBShareAlert() {
    	upload_dialog = new Dialog(getActivity(), R.style.customDialogTheme); 
		upload_dialog.setContentView(R.layout.upload_photo_to_facebook);
		upload_dialog.setCancelable(true);
		updlg_caption = (EditText) upload_dialog.findViewById(R.id.txtcaption);
		
		Button btntag = (Button) upload_dialog.findViewById(R.id.btntag);
		btntag.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new GetFriendsTask().execute("");
				upload_dialog.dismiss();
			}
		});
		
		Button btnupload = (Button) upload_dialog.findViewById(R.id.btnupload);
		btnupload.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (imageEdited()) {
					notifyNotSavedViaFacebook(updlg_caption.getText().toString());
				} else {
					if(!((Jpact) getActivity().getApplication()).facebook.isSessionValid()) {
			    		authorizeFacebook(true, false);
			    	} else {
			    		new UploadImageToFacebookTask(updlg_caption.getText().toString(), false).execute("");
			    	}
				}
			    		upload_dialog.dismiss();
			}
		});
		
		upload_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				dialog.dismiss();
			}
		});
		
		upload_dialog.show();
	}
    
    
    
  //Task to get Facebook Friends List
  	public class GetFriendsTask extends AsyncTask<String, Void, String> {

  		ProgressDialog mProgress;
      	public String finish = "";
      	private String response = "";
      	
      	@Override
      	protected void onPreExecute() {
      		super.onPreExecute();
      		
      		mProgress = new ProgressDialog(getActivity());
  			mProgress.setIndeterminate(true);
  			mProgress.setCancelable(false);
  			mProgress.setMessage("Loading friends...");
  			mProgress.show();
      	}
  		
  		@Override
      	protected String doInBackground(String... urls) {
  			try {
  				Bundle params = new Bundle();
      			params.putString("fields", "id,name,picture");
      			
      			response = ((Jpact) getActivity().getApplication()).facebook.request("me/friends", params);
      			finish = "ok";
  			} catch (IOException ioe) {
  				finish = "io_exc";
  			}
  			
  			return finish;
      	}
  		
  		@Override
      	protected void onPostExecute(String result) {
      		super.onPostExecute(result);
      		System.out.println("from main menu - get friends: " + result);
      		
      		if (result.equals("ok")) {
      			JSONObject check_json = null;
      			JSONObject check_error = null;
      			
      			try {
      				check_json = new JSONObject(response);
      				check_error = check_json.getJSONObject("error");
      			} catch (Exception e) {
      				e.printStackTrace();
      			}
      			
      			if (check_error != null) {
      				this.parseJSONError(response);
      			} else {
      				ArrayList<String> list_id = new ArrayList<String>();
      				ArrayList<String> list_name = new ArrayList<String>();
      				ArrayList<String> list_picture = new ArrayList<String>();
      				
      				try {
      					JSONObject json = new JSONObject(response);
      					JSONArray friends = json.getJSONArray("data");
      					System.out.println("json array size: " + friends.length());
      					
      					for (int i = 0; i < friends.length(); i ++) {
      						JSONObject data = (JSONObject) friends.get(i);
      						list_id.add(data.getString("id"));
      						list_name.add(data.getString("name"));
      						JSONObject jsn = new JSONObject(data.getString("picture"));
      						JSONObject pic = jsn.getJSONObject("data");
      						list_picture.add(pic.getString("url"));
      					}
      				} catch (Exception e) {
      					e.printStackTrace();
      				}
      				
      				((Jpact) getActivity().getApplication()).friend_list = new FriendArrayList();
      				
      				for (int i = 0; i < list_id.size(); i++) {
      					((Jpact) getActivity().getApplication()).friend_list.add(new FriendData(list_id.get(i), list_name.get(i), list_picture.get(i)));
      				}
      				
      				if (list_id.size() == 0) {
      					this.dismissDialog();
      					
      					alertNotifyUser("You have no Facebook friends yet.");
      				} else {
      					this.dismissDialog();
      					
      					Intent i = new Intent(getActivity(), FacebookFriends.class);
      					getActivity().startActivity(i);
      				}
      			}
      		} else if (result.equals("io_exc")) {
      			this.dismissDialog();
      			
      			alertNotifyUser("Failed to get Facebook friends. Please check if your device has data connection " +
      					"and try again.");
      		} else {
      			this.dismissDialog();
      			
      			alertNotifyUser("Failed to get Facebook friends. Please try again.");
      		}
  		}
      		
  		private void dismissDialog() {
  			if (mProgress.getWindow() != null) {
  				mProgress.dismiss();
  			}
  		}
  		
      	private void parseJSONError(String response) {
      		String message = "";
      		
      		try {
      			JSONObject json = new JSONObject(response);
      			JSONObject jsonError = json.getJSONObject("error");
      			message = jsonError.getString("message");
      			System.out.println("message: " + jsonError.getString("message"));
      		} catch (Exception e) {
      			e.printStackTrace();
      		}
      			
      		this.dismissDialog();
      		
      		alertNotifyUser(message);
      	}
  	}
  	
  	
  //Task for uploading photo to Facebook
  	public class UploadImageToFacebookTask extends AsyncTask<String, Void, String> {

  		ProgressDialog mProgress;
      	public String finish = "";
      	private String caption;
      	private boolean image_not_saved;
      	
      	public UploadImageToFacebookTask(String caption, boolean image_not_saved) {
      		this.caption = caption;
      		this.image_not_saved = image_not_saved;
      	}
      	
      	@Override
      	protected void onPreExecute() {
      		super.onPreExecute();
      		
      		mProgress = new ProgressDialog(getActivity());
  			mProgress.setIndeterminate(true);
  			mProgress.setCancelable(false);
  			mProgress.setMessage("Uploading photo to Facebook. Please wait...");
  			mProgress.show();
      	}
  		
  		@Override
      	protected String doInBackground(String... urls) {
  			String response = "";
  			String pic_id = "";
  			String tag_response = "";
  			
  			try {
  				byte[] data = null;
  				Bitmap bitmap = tempPhoto;
//  				if (this.image_not_saved) {
//  					bitmap = tempPhoto;
//  				} else {
//  					bitmap = BitmapFactory.decodeFile(photoUri.getPath());
//  				}
          		ByteArrayOutputStream baos = new ByteArrayOutputStream();
      	        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
      	        data = baos.toByteArray();                
      	        Bundle params = new Bundle();
      	        params.putString("message", this.caption);
      	        params.putByteArray("picture", data);

        	    response = ((Jpact)getActivity().getApplication()).facebook.request("me/photos", params, "POST");
      	        
      	        try {
      	        	JSONObject json = new JSONObject(response);
      	        	pic_id = json.getString("id");
      	        } catch (Exception e) {
      	        	e.printStackTrace();
      	        	pic_id = "";
      	        }
      	        
      	        if (!pic_id.equals("")) {
      	        	if (((Jpact) getActivity().getApplication()).tagged_friends_id.size() != 0) {
      	        		Bundle tag_params = new Bundle();
//          	        	String tag_ids = "[{'tag_uid':'100000152475013'}, {'tag_uid':'100002409198620'}]";
      	        		
      	        		StringBuilder sb = new StringBuilder();
      	        		for (int i = 0; i < ((Jpact) getActivity().getApplication()).tagged_friends_id.size(); i ++) {
      	        			sb.append("{'tag_uid':'" + ((Jpact) getActivity().getApplication()).tagged_friends_id.get(i) + "'}, ");
      	        		}
      	        		String tag_ids = sb.toString().substring(0, sb.toString().length() - 2);
      	        		String param_tags = "[" + tag_ids + "]";
          	        	tag_params.putString("tags", param_tags);
          	        	
      	    	        tag_response = ((Jpact) getActivity().getApplication()).facebook.request(pic_id + "/tags", tag_params, "POST");
          	        	System.out.println("tags param: " + param_tags);
      	        	}
      	        } 
  				
  				finish = "ok";
  				
  				System.out.println("upload photo response: " + response);
  				System.out.println("pic id: " + pic_id);
  				System.out.println("tag photo response: " + tag_response);
  			} catch (IOException ioe) {
  				finish = "io_exc";
  			}
  			
  			return finish;
      	}
  		
  		@Override
      	protected void onPostExecute(String result) {
      		super.onPostExecute(result);
      		System.out.println("from main menu - upload image task: " + result);
      		
      		if (mProgress.getWindow() != null) {
  				mProgress.dismiss();
  			}
      		
      		if (result.equals("ok")) {
      			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      	    	builder.setTitle(getActivity().getResources().getString(R.string.app_name));
      	    	builder.setMessage("You have successfully uploaded photo to Facebook.");
      	    	builder.setCancelable(false);
      	    	builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
      		    	public void onClick(DialogInterface dialog, int id) {
      		    		((Jpact) getActivity().getApplication()).tagged_friends_id.clear();
      		    		((Jpact) getActivity().getApplication()).tagged_friends_name.clear();
      		    	}
      	    	});
      	    	
      	    	AlertDialog alert = builder.create();
      	    	alert.show();
      	    	upload_dialog.dismiss();
      		} else if (result.equals("io_exc")) {
      			alertNotifyUser("Failed uploading photo to Facebook. Please check if your device has data connection " +
      					"and try again.");
      		} else {
      			alertNotifyUser("Failed uploading photo to Facebook. Please try again.");
      		}
  		}
  	}
  	
  //Alert for notifying a user on the status of action made.
  	public void alertNotifyUser(String message) {
      	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      	builder.setTitle(this.getResources().getString(R.string.app_name));
      	builder.setMessage(message);
      	builder.setCancelable(false);
      	builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
  	    	public void onClick(DialogInterface dialog, int id) {
  	    		dialog.dismiss();
  	    	}
      	});
      	
      	AlertDialog alert = builder.create();
      	alert.show();
      }
  	
  //Alert when the device has no data connection
  	private void getNotConnectedAlert() {
      	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      	builder.setTitle("pic4fun Camera");
      	builder.setMessage("The device has no data connection, other application may need data connection. Please " +
  				"turn it on in wireless and network settings.");
      	builder.setCancelable(true);
      	builder.setPositiveButton("Proceed to share", new DialogInterface.OnClickListener() {
  	    	public void onClick(DialogInterface dialog, int id) {
  	    		if (imageEdited()) {
  					notifyNotSavedViaShare();
  				} else {
  					shareImage();
  				}
  	    	}
      	});
      	builder.setNegativeButton("Wireless Settings", new DialogInterface.OnClickListener() {
  	    	public void onClick(DialogInterface dialog, int id) {
  	    		Intent callWirelessSettingIntent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
  		    	startActivity(callWirelessSettingIntent);
  	    	}
      	});
      	
      	AlertDialog alert = builder.create();
      	alert.show();
      }
  	
    //Dialog when the user wants to share unsaved photo
    private void notifyNotSavedViaShare() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setTitle(this.getResources().getString(R.string.app_name));
    	builder.setMessage("You did not save your edited photo. Last saved photo will be used if edited photo is not saved.");
    	builder.setCancelable(true);
    	builder.setPositiveButton("Save and Share", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	    		/*savePhoto();
	    		shareImage();*/
//	    		new SaveTask("share_via").execute("");
	    		saveTheImage();
	    		shareImage();
	    	}
    	});
    	
    	builder.setNegativeButton("Share Without Saving", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	    		shareImage();
	    	}
    	});
    	
    	AlertDialog alert = builder.create();
    	alert.show();
    }
    
  //Dialog when the user wants to share to Facebook an unsaved photo
  	private void notifyNotSavedViaFacebook(final String caption) {
      	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      	builder.setTitle(this.getResources().getString(R.string.app_name));
      	builder.setMessage("You did not save your edited photo.");
      	builder.setCancelable(true);
      	builder.setPositiveButton("Save and Share", new DialogInterface.OnClickListener() {
  	    	public void onClick(DialogInterface dialog, int id) {
  	    		/*savePhoto();
  				if(!((Jpact) MainMenu.this.getApplication()).facebook.isSessionValid()) {
  		    		MainMenu.this.authorizeFacebook(true, false);
  		    	} else {
  		    		new UploadImageToFacebookTask(caption, false).execute("");
  		    	}*/
//  	    		new SaveTask("facebook_share", caption).execute("");
  	    		saveTheImage();
  	    		if(!((Jpact) getActivity().getApplication()).facebook.isSessionValid()) {
  		    		authorizeFacebook(true, true);
  		    	} else {
  		    		new UploadImageToFacebookTask(caption, true).execute("");
  		    	}
  			}
      	});
      	
      	builder.setNegativeButton("Share Without Saving", new DialogInterface.OnClickListener() {
  	    	public void onClick(DialogInterface dialog, int id) {
  	    		if(!((Jpact) getActivity().getApplication()).facebook.isSessionValid()) {
  		    		authorizeFacebook(true, true);
  		    	} else {
  		    		new UploadImageToFacebookTask(caption, true).execute("");
  		    	}
  	    	}
      	});
      	
      	AlertDialog alert = builder.create();
      	alert.show();
      }
  	
  	private void authorizeFacebook(final boolean from_share, final boolean not_saved) {
		/*
 	    * Only call authorize if the access_token has expired.
 	    * facebook.authorize(this, new String[]{"publish_stream"}, Facebook.FORCE_DIALOG_AUTH, new DialogListener() {
 	    * 
 	    */
    	
		((Jpact) getActivity().getApplication()).facebook.
			authorize(getActivity(), new String[]{"publish_stream", "user_photos", "friends_photos", "read_friendlists"}, 
					new DialogListener() {
				
			public void onComplete(Bundle values) {
 	    		SharedPreferences.Editor editor = ((Jpact) getActivity().getApplication()).mPrefs.edit();
 	            editor.putString("access_token", ((Jpact) getActivity().getApplication()).facebook.getAccessToken());
 	            editor.putLong("access_expires", ((Jpact) getActivity().getApplication()).facebook.getAccessExpires());
 	            editor.commit();
 	        
 	            System.out.println("access token: " + ((Jpact) getActivity().getApplication()).facebook.getAccessToken());
 	            System.out.println("access expires: " + ((Jpact) getActivity().getApplication()).facebook.getAccessExpires());
 	            
 	            if (from_share) {
 	            	if (not_saved) {
 	            		new UploadImageToFacebookTask(updlg_caption.getText().toString(), true).execute("");
 	            	} else {
 	            		new UploadImageToFacebookTask(updlg_caption.getText().toString(), false).execute("");
 	            	}
 	            }

 	        }
 	        public void onFacebookError(FacebookError error) {
 	        	((Jpact) getActivity().getApplication()).error_message = "Facebook is currently unavailable. Please try again later.";
 	        	Intent i = new Intent(getActivity(), Alert.class);
 	        	startActivity(i);
 	        }
 	        public void onError(DialogError e) {
 	        	((Jpact) getActivity().getApplication()).error_message = "Unable to load Facebook login dialog. Please try again later.";
 	        	Intent i = new Intent(getActivity(), Alert.class);
 	        	startActivity(i);
 	        }
 	        public void onCancel() {
 	        }
 	    });
 	}
	
      
	
    /**
     * 
     * The onClickListener
     * 
     * */
	@Override
	public void onClick(View view) {
		
		switch(view.getId())
		{
		
		case R.id.quickedit:
//			Toast.makeText(getActivity(), photoUri.toString()+" eto un", 5000).show();
//			Log.e("","photoUri = "+photoUri);
			ActionItem wallPaperItem 	= new ActionItem(Field.SET_AS, "Set as", getResources().getDrawable(R.drawable.btnwallpaper));
			ActionItem cropItem 	= new ActionItem(Field.CROP, "Crop", getResources().getDrawable(R.drawable.btncrop));
	        ActionItem rotLeftItem 	= new ActionItem(Field.ROT_LEFT, "Rotate Left", getResources().getDrawable(R.drawable.btnrotleft));
	        ActionItem rotRightItem 	= new ActionItem(Field.ROT_RIGHT, "Rotate Right", getResources().getDrawable(R.drawable.btnrotright));
	        
	        //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
//	        wallPaperItem.setSticky(true);
//	        cropItem.setSticky(true);
	        
			
			//create QuickAction. Use QuickAction.VERTICAL or QuickAction.HORIZONTAL param to define layout 
	        //orientation
			quickAction = new QuickAction(getActivity(), QuickAction.VERTICAL);
			
			//add action items into QuickAction
	        quickAction.addActionItem(wallPaperItem);
			quickAction.addActionItem(cropItem);
	        quickAction.addActionItem(rotLeftItem);
	        quickAction.addActionItem(rotRightItem);
	        
	        //Set listener for action item clicked
			quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
				@Override
				public void onItemClick(QuickAction source, int pos, int actionId) {		
					//here we can filter which action item was clicked with pos or actionId parameter
					switch(actionId)
					{
					case Field.SET_AS:
						if(imageEdited()){
							AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					    	builder.setTitle("Set as WallPaper?");
					    	builder.setMessage("You did not save your edited photo. Last saved photo will be used if edited photo is not saved.");
					    	builder.setCancelable(true);
					    	builder.setPositiveButton("Save and Set as", new DialogInterface.OnClickListener() {
						    	public void onClick(DialogInterface dialog, int id) {
						    		saveTheImage();
									try {
										wallpaperManager.setBitmap(saveBitmap.photoBitmap);
									} catch (IOException e) {
										e.printStackTrace();
									}
						    	}
					    	});
					    	
					    	builder.setNegativeButton("Set as Without Saving", new DialogInterface.OnClickListener() {
						    	public void onClick(DialogInterface dialog, int id) {
						    		try {
										wallpaperManager.setBitmap(tempPhoto);
									} catch (IOException e) {
										e.printStackTrace();
									}
						    	}
					    	});
					    	
					    	AlertDialog alert = builder.create();
					    	alert.show();
						}
						else
						{
							try {
								wallpaperManager.setBitmap(origPhoto);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						Toast.makeText(getActivity(),"WallPaper was successfully set.", Field.SHOWTIME).show();
						break;
						
					case Field.CROP:
						if (imageEdited()) {
	    					notifyNotSavedViaCrop();
	    				} else {
	    					callCropIntent(photoUri);
	    				}
						break;
						
					case Field.ROT_LEFT:
						rotateLeft();
						break;
						
					case Field.ROT_RIGHT:
						rotateRight();
						break;
					}
					
					
//					Toast.makeText(getApplicationContext(), actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
				}
			});
			
			//set listnener for on dismiss event, this listener will be called only if QuickAction dialog was dismissed
			//by clicking the area outside the dialog.
			quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {			
				@Override
				public void onDismiss() {
				}
			});
			
			quickAction.show(view);
			break;
		
		case R.id.original:
			tempPhoto = origPhoto;
			image.setImageBitmap(tempPhoto);
			imageisChanged();
			break;
		
		case R.id.save:
			saveTheImage();
			break;
		
		/**
		 * The camera block
		 **/
		case R.id.camera:
			Intent inte = new Intent(getActivity(),CameraActivity.class);
//			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); 
			inte.putExtra("ACTIVITY", "Take Photo");
		    startActivityForResult(inte, Field.CAMERA_REQUEST);
		    getDialog().dismiss();
			break;
			
		/**
		 * The share block
		 **/
		case R.id.share:
			final CharSequence[] items;
			items = new String[]{"Share to Facebook", "Share Via"};
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle("Select option");
	        builder.setItems(items, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	                if (items[item].equals("Share to Facebook")) {
	                	if (!cd.isConnected()) {
	    	    			cd.getNotConnectedAlert("The device has no data connection. Please enable it on wireless and network settings " +
	    	    					"to use this feature.");
	    	    		} else {
	    	    			//Share Alert
	    	    			showFBShareAlert();
	    	    		}
	                } else {
//	                	shareImage();
	                	
	                	//Share Via
	                	if (!cd.isConnected()) {
	        				getNotConnectedAlert();
	        			} 
	                	else {
	        				if (imageEdited()) {
	        					notifyNotSavedViaShare();
	        				} else {
	        					shareImage();
	        				}
	        			}
	                }
	            }
	        });
	        
	        AlertDialog alert = builder.create();
	        alert.show();
			break;
		
		/**
		 * The change effect block
		 **/
		case R.id.btnup:
//			setFunctionButtons(false,false);
			imageisChanged();
			if(label.getText().equals("Frames"))
			{
				showImageEffects(0);
			}
			
			else if(label.getText().equals("Grids"))
			{
				showFrames(0);
			}
			
			else if(label.getText().equals("Collages"))
			{
				showGrids(0);
				setFunctionButtons(false,false);
			}
			
			else if(label.getText().equals("Draw4Fun"))
			{
				showCollages(0);
				setFunctionButtons(false,false);
			}
			
			else if(label.getText().equals("Text"))
			{
				showDraw4Fun(0);
			}
			
			break;	
			
		case R.id.btndown:
			imageisChanged();
			if(label.getText().equals("Image Effects"))
			{
				showFrames(0);
			}
			
			else if(label.getText().equals("Frames"))
			{
				showGrids(0);
				setFunctionButtons(false,false);
			}
			
			else if(label.getText().equals("Grids"))
			{
				showCollages(0);
				setFunctionButtons(false,false);
			}
			
			else if(label.getText().equals("Collages"))
			{
				showDraw4Fun(0);
			}
			
			else if(label.getText().equals("Draw4Fun"))
			{
				showText(0);
			}
			
			break;	
		
		case R.id.btnright:
			break;	
			
		case R.id.btnleft:
			break;	
			
		
		/**
		 * The Image Effect Block
		 **/
		case R.id.btn_e_original:
			tempPhoto = origPhoto;
			image.setImageBitmap(tempPhoto);
			break;	
			
		case R.id.btn_e_engrave:
			new EffectsTask("engrave").execute();
			break;	
			
		case R.id.btn_e_hue:
			new EffectsTask("hue").execute("");
			break;
			
		case R.id.btn_e_invert:
			new EffectsTask("invert").execute();	
			break;
			
		case R.id.btn_e_roundcorner:
//			new EffectsTask("round_corner").execute("");
			new EffectsTask("round_corner").execute("");
			break;
			
		case R.id.btn_e_boost1:
			new EffectsTask("boost_up1").execute();	
			break;
			
		case R.id.btn_e_boost2:
			new EffectsTask("boost_up2").execute("");
			break;	
			
		case R.id.btn_e_boost3:
			new EffectsTask("boost_up3").execute("");
			break;
			
		case R.id.btn_e_saturation:
			new EffectsTask("saturation").execute("");
			break;	
			
		case R.id.btn_e_sharpen:
			new EffectsTask("sharpen").execute();		
			break;
			
		case R.id.btn_e_sepia1:
			new EffectsTask("sepia_1").execute();
			break;	
			
		case R.id.btn_e_sepia2:
			new EffectsTask("sepia_2").execute("");
			break;	
			
		case R.id.btn_e_sepia3:
			new EffectsTask("sepia_3").execute("");
			break;
			
		case R.id.btn_e_sepia4:
			new EffectsTask("sepia_4").execute("");
			break;	
			
		case R.id.btn_e_shading1:
			new EffectsTask("shading1").execute("");
			break;
			
		case R.id.btn_e_shading2:
			new EffectsTask("shading2").execute("");
			break;	
			
		case R.id.btn_e_shading3:
			new EffectsTask("shading3").execute("");
			break;
			
		case R.id.btn_e_tint:
			new EffectsTask("tint").execute();
			break;	
			
		case R.id.btn_e_contrast:
			new EffectsTask("contrast").execute();
			break;	
			
			
		/**
		 * The Frame Block
		 **/	
		case R.id.btn_e_frame1:
			new EffectsTask("birthday1").execute();
			break;
			
		case R.id.btn_e_frame2:
			new EffectsTask("birthday2").execute();
			break;
			
		case R.id.btn_e_frame3:
			new EffectsTask("birthday3").execute();
			break;
			
		case R.id.btn_e_frame4:
			new EffectsTask("candy1").execute();
			break;
			
		case R.id.btn_e_frame5:
			new EffectsTask("candy2").execute();
			break;
			
		case R.id.btn_e_frame6:
			new EffectsTask("christmas1").execute();
			break;
			
		case R.id.btn_e_frame7:
			new EffectsTask("christmas2").execute();
			break;
			
		case R.id.btn_e_frame8:
			new EffectsTask("christmas3").execute();
			break;
			
		case R.id.btn_e_frame9:
			new EffectsTask("christmas4").execute();
			break;
			
		case R.id.btn_e_frame10:
			new EffectsTask("clam").execute();
			break;
			
		case R.id.btn_e_frame11:
			new EffectsTask("flower1").execute();
			break;
			
		case R.id.btn_e_frame12:
			new EffectsTask("flower2").execute();
			break;
			
		case R.id.btn_e_frame13:
			new EffectsTask("gold1").execute();
			break;
			
		case R.id.btn_e_frame14:
			new EffectsTask("gold2").execute();
			break;
			
		case R.id.btn_e_frame15:
			new EffectsTask("hearts1").execute();
			break;
			
		case R.id.btn_e_frame16:
			new EffectsTask("hearts2").execute();
			break;
			
		case R.id.btn_e_frame17:
			new EffectsTask("holloween1").execute();
			break;
			
		case R.id.btn_e_frame18:
			new EffectsTask("holloween2").execute();
			break;
			
		case R.id.btn_e_frame19:
			new EffectsTask("holloween3").execute();
			break;
			
		case R.id.btn_e_frame20:
			new EffectsTask("ladytree").execute();
			break;
			
		case R.id.btn_e_frame21:
			new EffectsTask("leaf").execute();
			break;
			
		case R.id.btn_e_frame22:
			new EffectsTask("mag").execute();
			break;
			
		case R.id.btn_e_frame23:
			new EffectsTask("old").execute();
			break;
			
		case R.id.btn_e_frame24:
			new EffectsTask("pink").execute();
			break;
			
		case R.id.btn_e_frame25:
			new EffectsTask("wanted").execute();
			break;
			
		/**
		 * The Grid Block
		 **/		
		case R.id.btn_e_grid1:
			switchMainImage(1);
			
			break;
			
		case R.id.btn_e_grid2:
			switchMainImage(2);
			break;
			
		case R.id.btn_e_grid3:
			switchMainImage(3);
			break;
			
		case R.id.btn_e_grid4:
			switchMainImage(4);
			break;
			
		case R.id.gridLayout1x2l:
//			nf = GridBitmapDialogFragment.newInstance(TAG,2,);
//		    nf.show(getActivity().getSupportFragmentManager(), TAG);
		    intent = new Intent(getActivity(), PhotosActivity.class);
			intent.putExtra("TAG", TAG);
			intent.putExtra("EFFECT", "Grids");
			intent.putExtra("ORIENTATION","1X2L");
			startActivity(intent);
			break;
			
		case R.id.gridLayout2x2l:
			intent = new Intent(getActivity(), PhotosActivity.class);
			intent.putExtra("TAG", TAG);
			intent.putExtra("EFFECT", "Grids");
			intent.putExtra("ORIENTATION","2X2L");
			startActivity(intent);
			break;
//			
		case R.id.gridLayout1x2p:
			intent = new Intent(getActivity(), PhotosActivity.class);
			intent.putExtra("TAG", TAG);
			intent.putExtra("EFFECT", "Grids");
			intent.putExtra("ORIENTATION","1X2P");
			startActivity(intent);
			break;
//			
		case R.id.gridLayout2x2p:
			intent = new Intent(getActivity(), PhotosActivity.class);
			intent.putExtra("TAG", TAG);
			intent.putExtra("EFFECT", "Grids");
			intent.putExtra("ORIENTATION","2X2P");
			startActivity(intent);
			break;
			
		/**
		 * The Collage Block
		 **/		
		case R.id.collageLayout:
			intent = new Intent(getActivity(), PhotosActivity.class);
			intent.putExtra("TAG", TAG);
			intent.putExtra("EFFECT", "Collages");
			intent.putExtra("BACKGROUND",backgroundID);
			startActivity(intent);
			break;
			
		case R.id.btn_e_collage1:
//			intent = new Intent(getActivity(), PhotosActivity.class);
//			intent.putExtra("TAG", TAG);
//			intent.putExtra("EFFECT", "Collages");
//			intent.putExtra("BACKGROUND",R.drawable.collagebday1);
//			startActivity(intent);
//			CollageBitmapDialogFragment  cb = CollageBitmapDialogFragment.newInstance(TAG, bgID, pathFiles);
//			collageLayout.setBackgroundResource(R.drawable.collagebday1);
//			cb = CollageBitmapDialogFragment.newInstance(TAG, R.drawable.collagebday1, null);
//			cb.show(getFragmentManager(), TAG);
			collageLayout.setBackgroundResource(R.drawable.buttoncollagebday1);
			backgroundID = R.drawable.collagebday1;
			break;
			
		case R.id.btn_e_collage2:
			collageLayout.setBackgroundResource(R.drawable.buttoncollagebday2);
			backgroundID = R.drawable.collagebday2;
			break;
			
		case R.id.btn_e_collage3:
			collageLayout.setBackgroundResource(R.drawable.buttoncollagebday3);
			backgroundID = R.drawable.collagebday3;
			break;
			
		case R.id.btn_e_collage4:
			collageLayout.setBackgroundResource(R.drawable.buttoncollagebday4);
			backgroundID = R.drawable.collagebday4;
			break;
			
		case R.id.btn_e_collage5:
			collageLayout.setBackgroundResource(R.drawable.buttoncollagebday5);
			backgroundID = R.drawable.collagebday5;
			break;
			
		case R.id.btn_e_collage6:
			collageLayout.setBackgroundResource(R.drawable.buttoncollageanniv1);
			backgroundID = R.drawable.collageanniv1;
			break;
			
		case R.id.btn_e_collage7:
			collageLayout.setBackgroundResource(R.drawable.buttoncollageanniv2);
			backgroundID = R.drawable.collageanniv2;
			break;
			
		case R.id.btn_e_collage8:
			collageLayout.setBackgroundResource(R.drawable.buttoncollageanniv3);
			backgroundID = R.drawable.collageanniv3;
			break;
			
		case R.id.btn_e_collage9:
			collageLayout.setBackgroundResource(R.drawable.buttoncollagelove1);
			backgroundID = R.drawable.collagelove1;
			break;
			
		case R.id.btn_e_collage10:
			collageLayout.setBackgroundResource(R.drawable.buttoncollagelove2);
			backgroundID = R.drawable.collagelove2;
			break;
			
		case R.id.btn_e_collage11:
			collageLayout.setBackgroundResource(R.drawable.buttoncollagelove3);
			backgroundID = R.drawable.collagelove3;
			break;	
			
		case R.id.btn_e_collage12:
			collageLayout.setBackgroundResource(R.drawable.buttoncollagehalloween1);
			backgroundID = R.drawable.collagehalloween1;
			break;
			
		case R.id.btn_e_collage13:
			collageLayout.setBackgroundResource(R.drawable.buttoncollagehalloween2);
			backgroundID = R.drawable.collagehalloween2;
			break;
			
		case R.id.btn_e_collage14:
			collageLayout.setBackgroundResource(R.drawable.buttoncollagechristmas1);
			backgroundID = R.drawable.collagechristmas1;
			break;
			
		case R.id.btn_e_collage15:
			collageLayout.setBackgroundResource(R.drawable.buttoncollageleaves1);
			backgroundID = R.drawable.collageleaves1;
			break;
			
		case R.id.btn_e_collage16:
			collageLayout.setBackgroundResource(R.drawable.buttoncollageleaves2);
			backgroundID = R.drawable.collageleaves2;
			break;
			
		case R.id.btn_e_collage17:
			collageLayout.setBackgroundResource(R.drawable.buttoncollageleaves3);
			backgroundID = R.drawable.collageleaves3;
			break;
			
		case R.id.btn_e_collage18:
			collageLayout.setBackgroundResource(R.drawable.buttoncollageleaves4);
			backgroundID = R.drawable.collageleaves4;
			break;
			
		case R.id.btn_e_collage19:
			
			collageLayout.setBackgroundResource(R.drawable.buttoncollagearrows1);
			backgroundID = R.drawable.collagearrows1;
			break;
			
		case R.id.btn_e_collage20:
			collageLayout.setBackgroundResource(R.drawable.buttoncollagefleurdelis);
			backgroundID = R.drawable.collagefleurdelis;
			break;
			
		case R.id.btn_e_collage21:
			collageLayout.setBackgroundResource(R.drawable.buttoncollagecamo1);
			backgroundID = R.drawable.collagecamo1;
			break;	
			
		case R.id.btn_e_collage22:
			collageLayout.setBackgroundResource(R.drawable.buttoncollageshapes1);
			backgroundID = R.drawable.collageshapes1;
			break;
			
		case R.id.btn_e_collage23:
			collageLayout.setBackgroundResource(R.drawable.buttoncollageshapes2);
			backgroundID = R.drawable.collageshapes1;
			break;
			
		case R.id.btn_e_collage24:
			collageLayout.setBackgroundResource(R.drawable.buttoncollagecircles1);
			backgroundID = R.drawable.collagecircles1;
			break;	
			
		case R.id.btn_e_collage25:
			collageLayout.setBackgroundResource(R.drawable.buttoncollagecircles2);
			backgroundID = R.drawable.collagecircles2;
			break;
			
		case R.id.btn_e_collage26:
			collageLayout.setBackgroundResource(R.drawable.buttoncollageabstract1);
			backgroundID = R.drawable.collageabstract1;
			break;
			
		case R.id.btn_e_collage27:
			collageLayout.setBackgroundResource(R.drawable.buttoncollageabstract2);
			backgroundID = R.drawable.collageabstract2;
			break;
			
		case R.id.btn_e_collage28:
			collageLayout.setBackgroundResource(R.drawable.buttoncollageabstract3);
			backgroundID = R.drawable.collageabstract3;
			break;
			
		/**
		 * The Draw Block
		 **/			
		case R.id.btn_drawing_feature:
			intent = new Intent(getActivity(), com.jpact.draw4fun.Draw4FunMain.class);
			intent.putExtra("EXTRA_MESSAGE", photoUri.getPath());
			intent.putExtra("EXTRA_MESSAGE_PATH","Pic4Fun");
			getActivity().startActivity(intent);
			break;
			
		/**
		 * The Text Block
		 * */	
		case R.id.normal:
			intent = new Intent(getActivity(), EditTextBitmapActivityFragment.class);
//			intent.putExtra("IMAGE", tempPhoto);
			intent.putExtra("TYPE",0+"");
			getActivity().startActivity(intent);
			break;	
			
		case R.id.chantelli:
			intent = new Intent(getActivity(), EditTextBitmapActivityFragment.class);
//			intent.putExtra("IMAGE", tempPhoto);
			intent.putExtra("TYPE", R.id.chantelli+"");
			getActivity().startActivity(intent);
			break;	
			
		case R.id.calligra:
			intent = new Intent(getActivity(), EditTextBitmapActivityFragment.class);
//			intent.putExtra("IMAGE", (Object)tempPhoto);
			intent.putExtra("TYPE", R.id.calligra+"");
			getActivity().startActivity(intent);
			break;	
			
		case R.id.verase:
			intent = new Intent(getActivity(), EditTextBitmapActivityFragment.class);
//			intent.putExtra("IMAGE", (Object)tempPhoto);
			intent.putExtra("TYPE", R.id.verase+"");
			getActivity().startActivity(intent);
			break;	
			
		case R.id.impact:
			intent = new Intent(getActivity(), EditTextBitmapActivityFragment.class);
//			intent.putExtra("IMAGE", (Object)tempPhoto);
			intent.putExtra("TYPE", R.id.impact+"");
			getActivity().startActivity(intent);
			break;	
			
		case R.id.ankecalligraph:
			intent = new Intent(getActivity(), EditTextBitmapActivityFragment.class);
//			intent.putExtra("IMAGE", (Object)tempPhoto);
			intent.putExtra("TYPE", R.id.ankecalligraph+"");
			getActivity().startActivity(intent);
			break;		
			
		default:
			Toast.makeText(getActivity(), "Button is not yet added to EditBitmapDialog.", Field.SHOWTIME).show();
			break;
		}
		
	}
	
public static void changePhoto(Bitmap bitmap){
	thePhoto = bitmap;
}
//	
//	private void rotateLeftFrag(View v) {
//        
//            ObjectAnimator.ofFloat(v, "rotationY", 0, 180)
//                    .setDuration(500).start();
//        
//    }

//
//public void onActivityResult(int requestCode, int resultCode, Intent data) {  
//	//Class initialization
//	mediaUtility = new MediaUtility(getActivity());
//	packageCheck = new PackageCheck(getActivity());
//	//Get result after cropping an image
//	if (requestCode == Field.CAMERA_CROP_REQUEST || requestCode == Field.NON_CAMERA_CROP_REQUEST) {
//		if (resultCode == getActivity().RESULT_OK) {
//			mediaUtility.updateMedia(TAG, photoUri.getPath());
//			origUri = photoUri;
//			Toast.makeText(getActivity(), "Success.", Toast.LENGTH_LONG).show();
////			new GetImageFromUriTask().execute("");
//			
//			//Fragment initialization
//			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//		    Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(TAG);
//		    if (prev != null) {
//		        ft.remove(prev);
//		    }
////		    ft.addToBackStack(null);
////		    
//			EditBitmapDialogFragment editDialog = EditBitmapDialogFragment.newInstance(photoUri,R.layout.editbitmap,TAG,null);
//			
////			editDialog.show(this.getSupportFragmentManager(), TAG);
////			finish();
//					
//			ft.add(editDialog, TAG);
//			ft.commitAllowingStateLoss();
//		} else if (resultCode == getActivity().RESULT_CANCELED) {
//		} else {
//        	Toast.makeText(getActivity(), "Failed to process cropped image. Please try again.", Toast.LENGTH_LONG).show();
//        }
//    }
	
//	((Jpact) this.getApplication()).facebook.authorizeCallback(requestCode, resultCode, data);
//}

}
