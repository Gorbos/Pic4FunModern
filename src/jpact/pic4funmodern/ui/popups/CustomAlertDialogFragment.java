package jpact.pic4funmodern.ui.popups;

import jpact.pic4funmodern.ui.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CustomAlertDialogFragment extends DialogFragment implements View.OnClickListener{
	public static String font;
	public static int fType;
	public TextView tv;
	public Typeface tf;
	public EditText eText;
	public Button ok;
	public Button cancel;
	public SeekBar sBar;
	public int fontSize;
	
	public CustomAlertDialogFragment(){}
	
	public static CustomAlertDialogFragment newInstance(String type) {
		CustomAlertDialogFragment frag = new CustomAlertDialogFragment();
		
        final Bundle args = new Bundle();
        font = type;
        fType = Integer.parseInt(font);
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
	    	View view = inflater.inflate(R.layout.addtextdialog, container);
	    	eText = (EditText) view.findViewById(R.id.txtcaption);
	    	ok = (Button)view.findViewById(R.id.btntag);
	    	cancel = (Button)view.findViewById(R.id.btnsave);
	    	tv = (TextView)view.findViewById(R.id.desc);
	    	sBar = (SeekBar)view.findViewById(R.id.seekBar);

	    	sBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					Toast.makeText(getActivity(),"Size: "+seekBar.getProgress()	,5000).show();
					
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
				}
			});
	    	eText.setHint("Type text here...");
	    	ok.setOnClickListener(this);
	    	cancel.setOnClickListener(this);
	    	tv.setText("");
	    	
	    	ok.setText("OK");
	    	cancel.setText("CANCEL");
	    	return view;
	}
	
	/**
  * Populate image using a url from extras, use the convenience factory method
  */

 
	 @Override
	 public void onDestroy() {
	     super.onDestroy();
	 }

	@Override
	public void onClick(View v) {

		switch(v.getId())
		{
		case R.id.btntag:
			
//			EditTextBitmapActivityFragment.edittext = eText.getText().toString();
			if(fontType(fType) != null)
				EditTextBitmapActivityFragment.textView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), fontType(fType)));
			
			if(sBar.getProgress()>10){
				EditTextBitmapActivityFragment.textView.setTextSize(sBar.getProgress());
				EditTextBitmapActivityFragment.textView.setText(eText.getText().toString());
				getDialog().dismiss();
			}
			else{
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    	builder.setTitle("Text Size");
		    	builder.setMessage("The text will be too small, is it ok?");
		    	builder.setCancelable(false);
		    	builder.setPositiveButton("Yeah, its fine...", new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int id) {

			    		EditTextBitmapActivityFragment.textView.setTextSize(sBar.getProgress());
						EditTextBitmapActivityFragment.textView.setText(eText.getText().toString());
						getDialog().dismiss();
			    	}
		    	});
		    	
		    	builder.setNegativeButton("Wait, let me change it.", new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int id) {
			    		
			    	}
		    	});
		    	
		    	AlertDialog alert = builder.create();
		    	alert.show();
			}
			
			
			break;
			
		case R.id.btnsave:
			getDialog().dismiss();
			getActivity().finish();
			break;
		}
	}
	
	public String fontType(int type){
		String theFont;
		switch(type)
		{
		
		case R.id.normal:
			theFont = null;
			break;	
			
		case R.id.chantelli:
			theFont = "Chantelli_Antiqua.ttf";
			break;	
			
		case R.id.calligra:
			theFont = "calligra.ttf";
			break;	
			
		case R.id.verase:
			theFont = "VeraSe.ttf";
			break;	
			
		case R.id.impact:
			theFont = "Impact.ttf";
			break;	
			
		case R.id.ankecalligraph:
			theFont = "AnkeCalligraph.TTF";
			break;	
		
		default:
			return null;
		}
		
		return theFont;
	}

}
