package jpact.pic4funmodern.ui.sharing.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import com.aviary.android.feather.library.utils.ImageLoader;
import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;

import jpact.pic4funmodern.ui.*;
import jpact.pic4funmodern.ui.menu.*;
import jpact.pic4funmodern.util.ConnectionDetection;
import jpact.pic4funmodern.util.FriendArrayList;
import jpact.pic4funmodern.util.FriendData;
/** 
 * 
 * Class to display and share multiple selected pictures from Photos Class 
  	Most of the functions here is used by Main Menu and GalleryActivity Class with slight modification 
  	since we are uploading here multiple photos.
**/

public class GallSelectedImages extends FragmentActivity implements OnClickListener {

	private static final String TAG = "FacebookFragment";
	private Button btnshare;
	private ImageAdapter adapter;
	private GridView grdview;
	private Uri[] images_uri;
	private String[] caption;
	private ArrayList<String> images_path;
	private Bitmap[] photos;
	private int imageWidth, imageHeight;
	private ConnectionDetection cd = new ConnectionDetection(this);
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gall_selected);
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		imageWidth = (int) ((float) metrics.widthPixels / 1.5);
		imageHeight = (int) ((float) metrics.heightPixels / 1.5);

		images_path = ((Jpact) getApplication()).images_path;
		images_uri = new Uri[images_path.size()];
		photos = new Bitmap[images_path.size()];
		caption = new String[images_path.size()];
		
		for (int i = 0; i < images_path.size(); i ++) {
			images_uri[i] = Uri.parse("file://" + images_path.get(i));
			System.out.println("images uris: " + i + " " + images_uri[i].toString());
			caption[i] = "";
			ArrayList<String> list = new ArrayList<String>();
			ArrayList<String> list2 = new ArrayList<String>();
			((Jpact) GallSelectedImages.this.getApplication()).multi_tagged_friends_id.add(i, list);
			((Jpact) GallSelectedImages.this.getApplication()).multi_tagged_friends_name.add(i, list2);
		}
		
		btnshare = (Button) findViewById(R.id.btnshare);
		btnshare.setOnClickListener(this);
		
		new LoadImagesTask().execute("");
		
		System.out.println("multi friend id arraylist size: " + ((Jpact) GallSelectedImages.this.getApplication()).multi_tagged_friends_id.size());
	}
	
	public void onClick(View v) {
		if (v == findViewById(R.id.btnshare)) {
			if (!cd.isConnected()) {
				cd.getNotConnectedAlert("The device has no data connection. Please enable it on wireless and network settings " +
					"to use this feature.");
			} else {
				if(!((Jpact) GallSelectedImages.this.getApplication()).facebook.isSessionValid()) {
					GallSelectedImages.this.authorizeFacebook();
		    	} else {
		    		//perform upload
		    		new UploadImagesToFacebookTask().execute("");
		    	}
			}
		}
	}
	
	@Override
    public void onResume() {
    	if (((Jpact) this.getApplication()).app_state.equals("")) {
    		Intent i = new Intent(this, MainMenuActivity.class);
    		i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	    startActivity(i);
    	}
    	super.onResume();
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		((Jpact) this.getApplication()).facebook.authorizeCallback(requestCode, resultCode, data);
	} 
	
	public class ImageAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater;
		
		public ImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return photos.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.gall_selected_item, null);
				holder.imageview = (ImageView) convertView.findViewById(R.id.image_thumbnail);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.imageview.setId(position);
			
			holder.imageview.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int id = v.getId();
					photoOptionsAlert(id);
				}
			});
			
			holder.imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
			holder.imageview.setImageBitmap(photos[position]);
			holder.id = position;
			return convertView;
		}
	}
	
	class ViewHolder {
		ImageView imageview;
		int id;
	}
	
	private void photoOptionsAlert(final int position) {
		final CharSequence[] items;
		
		if (!caption[position].equals("")) {
			items = new String[]{"Edit Description"};
		} else {
			items = new String[]{"Add Description"};
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Add Description") || items[item].equals("Edit Description")) {
                	final Dialog desc_dialog = new Dialog(GallSelectedImages.this, R.style.customDialogTheme); 
                	desc_dialog.setContentView(R.layout.gall_selected_desc_dialog);
		    		desc_dialog.setCancelable(true);
		    		final EditText txtcaption = (EditText) desc_dialog.findViewById(R.id.txtcaption);
		    		txtcaption.setText(caption[position]);
//		    		final TextView lblfriends = (TextView) desc_dialog.findViewById(R.id.lblfriends);
//		    		lblfriends.setText("");
		    		
		    		Button btntag = (Button) desc_dialog.findViewById(R.id.btntag);
		    		btntag.setOnClickListener(new OnClickListener() {
		    			public void onClick(View v) {
		    				if (!cd.isConnected()) {
		    	    			cd.getNotConnectedAlert("The device has no data connection. Please enable it on wireless and network settings " +
		    	    					"to display your Facebook friends list.");
		    	    		} else {
		    	    			if(!((Jpact) GallSelectedImages.this.getApplication()).facebook.isSessionValid()) {
		    	    				GallSelectedImages.this.authorizeFacebook();
		    			    	} else {
		    			    		new GetFriendsTask(position).execute("");
		    			    	}
		    	    		}
		    			}
		    		});
		    		
		    		Button btnsave = (Button) desc_dialog.findViewById(R.id.btnsave);
		    		btnsave.setOnClickListener(new OnClickListener() {
		    			public void onClick(View v) {
		    				caption[position] = txtcaption.getText().toString();
		    				desc_dialog.dismiss();
		    			}
		    		});
		    		
		    		desc_dialog.show();
                } 
            }
        });
        
        AlertDialog alert = builder.create();
        alert.show();
	}
	
	public class GetFriendsTask extends AsyncTask<String, Void, String> {

		ProgressDialog mProgress;
    	public String finish = "";
    	private String response = "";
    	private int position;
    	
    	public GetFriendsTask(int position) {
    		this.position = position;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		
    		mProgress = new ProgressDialog(GallSelectedImages.this);
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
    			
    			response = ((Jpact) GallSelectedImages.this.getApplication()).facebook.request("me/friends", params);
    			finish = "ok";
			} catch (IOException ioe) {
				finish = "io_exc";
			}
			
			return finish;
    	}
		
		@Override
    	protected void onPostExecute(String result) {
    		super.onPostExecute(result);
    		System.out.println("from gallery activity - get friends: " + result);
    		
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
    						list_picture.add(data.getString("picture"));
    					}
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    				
    				((Jpact) GallSelectedImages.this.getApplication()).friend_list = new FriendArrayList();
    				
    				for (int i = 0; i < list_id.size(); i++) {
    					((Jpact) GallSelectedImages.this.getApplication()).friend_list.add(new FriendData(list_id.get(i), list_name.get(i), list_picture.get(i)));
    				}
    				
    				if (list_id.size() == 0) {
    					this.dismissDialog();
    					
    					alertNotifyUser("You have no Facebook friends yet.");
    				} else {
    					this.dismissDialog();
    					
    					Intent i = new Intent(GallSelectedImages.this, GallSelectedFBFriends.class);
						i.putExtra("pic_position", position);
    					GallSelectedImages.this.startActivity(i);
    					
    					System.out.println("multi friend id arraylist " + position + " size: " + ((Jpact) GallSelectedImages.this.getApplication()).multi_tagged_friends_id.get(position).size());
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
    		
    		GallSelectedImages.this.alertNotifyUser(message);
    	}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		((Jpact ) GallSelectedImages.this.getApplication()).multi_tagged_friends_id.clear();
    		((Jpact ) GallSelectedImages.this.getApplication()).multi_tagged_friends_name.clear();
    		((Jpact) this.getApplication()).images_path.clear();
    		finish();
    	}
    	
        return super.onKeyDown(keyCode, event);
    }
	
	public class LoadImagesTask extends AsyncTask<String, Void, String> {

		ProgressDialog mProgress;
		
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		
    		mProgress = new ProgressDialog(GallSelectedImages.this);
			mProgress.setIndeterminate(true);
			mProgress.setCancelable(false);
			mProgress.setMessage("Loading selected photo(s)...");
			mProgress.show();
    	}
		
		@Override
    	protected String doInBackground(String... urls) {
			try {
				for (int i = 0; i < photos.length; i ++) {
					photos[i] = ImageLoader.loadFromUri(GallSelectedImages.this, images_uri[i], imageWidth, imageHeight, null);
				}
			} catch (Exception e) {
    			e.printStackTrace();
    			System.out.println("load image exception...");
    		}
    		return "";
    	}
		
		@Override
    	protected void onPostExecute(String result) {
    		super.onPostExecute(result);
    		System.out.println("from gall selected images - get image from uri:" + result);
    		
    		if (mProgress.getWindow() != null) {
				mProgress.dismiss();
			}
    		
    		grdview = (GridView) findViewById(R.id.img_grid);
    		adapter = new ImageAdapter();
    		grdview.setAdapter(adapter);
    	}
	}
	
	public class UploadImagesToFacebookTask extends AsyncTask<String, Void, String> {

		ProgressDialog mProgress;
    	public String finish = "";
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		
    		mProgress = new ProgressDialog(GallSelectedImages.this);
			mProgress.setIndeterminate(true);
			mProgress.setCancelable(false);
			mProgress.setMessage("Uploading photo(s) to Facebook. Please wait...");
			mProgress.show();
    	}
		
		@Override
    	protected String doInBackground(String... urls) {
			String response = "";
			String pic_id = "";
			String tag_response = "";
			
			try {
				for (int i = 0; i < images_uri.length; i ++) {
					byte[] data = null;               
	        		Bitmap bitmap = BitmapFactory.decodeFile(images_uri[i].getPath());
	        		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
	    	        data = baos.toByteArray();                
	    	        Bundle params = new Bundle();
	    	        params.putString("message", caption[i]);
	    	        params.putByteArray("picture", data);
	    	        
	    	        response = ((Jpact) GallSelectedImages.this.getApplication()).facebook.request("me/photos", params, "POST");
	    	        System.out.println("upload photo response: " + response);
	    	        
	    	        try {
	    	        	JSONObject json = new JSONObject(response);
	    	        	pic_id = json.getString("id");
	    	        	System.out.println("pic id: " + pic_id);
	    	        } catch (Exception e) {
	    	        	e.printStackTrace();
	    	        	pic_id = "";
	    	        	System.out.println("pic id: " + pic_id);
	    	        }
	    	        
	    	        if (!pic_id.equals("")) {
	    	        	if (((Jpact) GallSelectedImages.this.getApplication()).multi_tagged_friends_id.get(i).size() != 0) {
	    	        		Bundle tag_params = new Bundle();
//	        	        	String tag_ids = "[{'tag_uid':'100000152475013'}, {'tag_uid':'100002409198620'}]";
	    	        		
	    	        		StringBuilder sb = new StringBuilder();
	    	        		for (int j = 0; j < ((Jpact) GallSelectedImages.this.getApplication()).multi_tagged_friends_id.get(i).size(); j ++) {
	    	        			sb.append("{'tag_uid':'" + ((Jpact) GallSelectedImages.this.getApplication()).multi_tagged_friends_id.get(i).get(j) + "'}, ");
	    	        		}
	    	        		String tag_ids = sb.toString().substring(0, sb.toString().length() - 2);
	    	        		String param_tags = "[" + tag_ids + "]";
	        	        	tag_params.putString("tags", param_tags);
	        	        	
	    	    	        tag_response = ((Jpact) GallSelectedImages.this.getApplication()).facebook.request(pic_id + "/tags", tag_params, "POST");
	        	        	System.out.println("tags param: " + param_tags);
	        	        	System.out.println("tag photo response: " + tag_response);
	        	        }
	    	        } 
	    	    }
				
				finish = "ok";
			} catch (IOException ioe) {
				finish = "io_exc";
			}
			
			return finish;
    	}
		
		@Override
    	protected void onPostExecute(String result) {
    		super.onPostExecute(result);
    		System.out.println("from gall selected images - get image from uri: " + result);
    		
    		if (mProgress.getWindow() != null) {
				mProgress.dismiss();
			}
    		
    		if (result.equals("ok")) {
    			alertUploadSuccess("You have successfully uploaded photo(s) to Facebook.");
    		} else if (result.equals("io_exc")) {
    			alertNotifyUser("Failed uploading photo(s) to Facebook. Please check if your device has data connection " +
    					"and try again.");
    		} else {
    			alertNotifyUser("Failed uploading photo(s) to Facebook. Please try again.");
    		}
		}
	}
	
	//Facebook
	private void authorizeFacebook() {
		/*
		* Only call authorize if the access_token has expired.
		* facebook.authorize(this, new String[]{"publish_stream"}, Facebook.FORCE_DIALOG_AUTH, new DialogListener() {
		* 
		*/
		    	
		((Jpact) this.getApplication()).facebook.authorize(this, new String[]{"publish_stream", "user_photos", "friends_photos", "read_friendlists"}, new DialogListener() {
			public void onComplete(Bundle values) {
				SharedPreferences.Editor editor = ((Jpact) GallSelectedImages.this.getApplication()).mPrefs.edit();
		 	    editor.putString("access_token", ((Jpact) GallSelectedImages.this.getApplication()).facebook.getAccessToken());
		 	    editor.putLong("access_expires", ((Jpact) GallSelectedImages.this.getApplication()).facebook.getAccessExpires());
		 	    editor.commit();
		 	        
		 	    System.out.println("access token: " + ((Jpact) GallSelectedImages.this.getApplication()).facebook.getAccessToken());
		 	    System.out.println("access expires: " + ((Jpact) GallSelectedImages.this.getApplication()).facebook.getAccessExpires());
		 	    
		 	    new UploadImagesToFacebookTask().execute("");
		 	}
		 	public void onFacebookError(FacebookError error) {
		 		((Jpact) GallSelectedImages.this.getApplication()).error_message = "Facebook is currently unavailable. Please try again later.";
		 		 Intent i = new Intent(GallSelectedImages.this, Alert.class);
		 	    startActivity(i);
		 	}
		 	public void onError(DialogError e) {
		 		((Jpact) GallSelectedImages.this.getApplication()).error_message = "Unable to load Facebook login dialog. Please try again later.";
		 		Intent i = new Intent(GallSelectedImages.this, Alert.class);
		 	    startActivity(i);
		 	}
		 	public void onCancel() {
		 	}
		});
	}
	
	private void alertUploadSuccess(String message) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(this.getResources().getString(R.string.app_name));
    	builder.setMessage(message);
    	builder.setCancelable(false);
    	builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	    		Intent i = new Intent(GallSelectedImages.this, PhotosActivity.class);
	    		i.putExtra("TAG", TAG);
	    		((Jpact ) GallSelectedImages.this.getApplication()).multi_tagged_friends_id.clear();
	    		((Jpact ) GallSelectedImages.this.getApplication()).multi_tagged_friends_name.clear();
	    		((Jpact) GallSelectedImages.this.getApplication()).images_path.clear();
	    		PhotosActivity.act_handler.sendEmptyMessage(0);
	    		startActivity(i);
	    		finish();
	    	}
    	});
    	
    	AlertDialog alert = builder.create();
    	alert.show();
    }
	
	private void alertNotifyUser(String message) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
}
