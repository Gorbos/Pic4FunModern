/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jpact.pic4funmodern.ui.sharing.utils;

import jpact.pic4funmodern.util.friends_list.ImageLoader;

import jpact.pic4funmodern.ui.*;
import jpact.pic4funmodern.ui.R;
import jpact.pic4funmodern.util.ConnectionDetection;
import jpact.pic4funmodern.util.FriendArrayList;
import jpact.pic4funmodern.util.FriendData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.res.Configuration;
import java.util.ArrayList;

/**
 *
 * @author Dave
 */

/** Class to display Facebook friends list **/

public class GallSelectedFBFriends extends ListActivity implements OnClickListener {

	private FriendsListAdapter adapter;
	private ArrayAdapter<String> txtsearch_adapter;
	private AutoCompleteTextView txtsearch;
	private Button btnclear, btnsearchtag, btntag, btndone;
	private FriendArrayList data;
	private ArrayList<String> data_array = new ArrayList<String>();
	public String[] search_array;
	private String[] profile_pic_urls;
	private ConnectionDetection cd = new ConnectionDetection(this);
	private int pic_position;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.facebook_friends_layout);
        b = this.getIntent().getExtras();
        this.setTitle("Select Friends");
        
        this.data = ((Jpact) this.getApplication()).friend_list;
        pic_position = b.getInt("pic_position");
        
        System.out.println("friend size: " + data.size());
        System.out.println("selected photo position: " + pic_position);
        
        search_array = new String[data.size()];
        profile_pic_urls = new String[data.size()];
        
        for (int i = 0; i < this.data.size(); i ++) {
        	data_array.add(this.data.getObject(i).getName());
        	search_array[i] = this.data.getObject(i).getName();
        	profile_pic_urls[i] = this.data.getObject(i).getPicture();
        }
        
        if (!cd.isConnected()) {
        	cd.getNotConnectedAlert();
        } 
        
        txtsearch = (AutoCompleteTextView) this.findViewById(R.id.txtsearch);
//        txtsearch.requestFocus();
        btnsearchtag = (Button) this.findViewById(R.id.btnsearchadd);
        btnclear = (Button) this.findViewById(R.id.btnclear);
        btndone = (Button) this.findViewById(R.id.btndone);
        
        adapter = new FriendsListAdapter(this, this.profile_pic_urls, this.data_array);
        this.setListAdapter(adapter);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        //AutoCompleteTextView txtsearch
        txtsearch_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, this.search_array);
        txtsearch.setAdapter(txtsearch_adapter);
        btnsearchtag.setOnClickListener(this);
        btnclear.setOnClickListener(this);
        btndone.setOnClickListener(this);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
    	Thread thread = new Thread() {
    		public void run() {
    			adapter.imageLoader.clearCache();
    	    	System.out.println("from friends list: activity is destroyed... cache is cleared...");
    		}
    	};
    	thread.start();
    	super.onDestroy();
    }
    
    @Override
    public void onPause() {
    	System.out.println("from friends list: activity is paused... cache is cleared...");
    	super.onPause();
    }
    
    @Override
    public void onResume() {
    	if (((Jpact) this.getApplication()).app_state.equals("")) {
    		Intent i = new Intent(this, MainMenuActivity.class);
    		i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		startActivity(i);
//    		finish();
    	}
    	super.onResume();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    }
    
    public void onClick(View v) {
    	if (v == btndone) {
    		/*if (((Jpact) this.getApplication()).multi_tagged_friends_name.get(pic_position).size() != 0
    				&& ((Jpact) this.getApplication()).multi_tagged_friends_id.get(pic_position).size() != 0) {
    			for (int i = 0; i < ((Jpact) this.getApplication()).multi_tagged_friends_name.get(pic_position).size(); i ++) {
        			System.out.println("tag names: " + ((Jpact) this.getApplication()).multi_tagged_friends_name.get(pic_position).get(i));
        		}
        		
        		StringBuilder sb = new StringBuilder();
        		for (int i = 0; i < ((Jpact) this.getApplication()).multi_tagged_friends_id.get(pic_position).size(); i ++) {
        			sb.append("{'tag_uid':'" + ((Jpact) this.getApplication()).multi_tagged_friends_id.get(pic_position).get(i) + "'}, ");
        		}
        		System.out.println("tags: " + pic_position + " " + sb.toString().substring(0, sb.toString().length() - 2));
    		}*/
    		
    		((Jpact) this.getApplication()).friend_list.clear();
    		finish();
    	}
    	if (v == btnclear) {
    		txtsearch.setText("");
    	}
    	if (v == btnsearchtag) {
    		String friend_id = "";
    		String friend_name = "";
    		String name = txtsearch.getText().toString();
    		boolean has_result = false;
    		boolean already_added = false;
    		for (int i = 0; i < this.data.size(); i ++) {
        		if (name.equals(this.data.getObject(i).getName())) {
        			friend_id = this.data.getObject(i).getID();
        			friend_name = this.data.getObject(i).getName();
        	    	has_result = true;
        	    } 
        	}
    		
    		if (has_result) {
    			for (int i = 0; i < ((Jpact) this.getApplication()).multi_tagged_friends_id.get(pic_position).size(); i ++) {
    				if (friend_id.equals(((Jpact) this.getApplication()).multi_tagged_friends_id.get(pic_position).get(i))) {
    					already_added = true;
    				} 
    			}
    			
    			if (already_added) {
    				Toast.makeText(getApplicationContext(), friend_name + " is already selected.", Toast.LENGTH_SHORT).show();
    				System.out.println("search and tag button: already exists, don't add");
    			} else {
    				((Jpact) this.getApplication()).multi_tagged_friends_id.get(pic_position).add(friend_id);
    	    		((Jpact) this.getApplication()).multi_tagged_friends_name.get(pic_position).add(friend_name);
    	    		Toast.makeText(getApplicationContext(), friend_name + " is selected.", Toast.LENGTH_SHORT).show();
    				System.out.println("search and tag button: add success");
    			}
    		} else {
    			this.alertNoResult("No search result.");
    		}
    	}
    }
    
    private void alertNoResult(String message) {
        AlertDialog.Builder alert_dialog = new AlertDialog.Builder(this);
        alert_dialog.setTitle("Find Friend");
        alert_dialog.setMessage(message);
        alert_dialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int arg1) {
                di.dismiss();
            }
        });

        alert_dialog.show();
    }
    
    public class FriendsListAdapter extends BaseAdapter {
        
        private Activity activity;
        private ArrayList<String> friendsName;
        private String[] pic_urls;
        private LayoutInflater inflater = null;
        public ImageLoader imageLoader; 
        
        public FriendsListAdapter(Activity act, String[] urls, ArrayList<String> names) {
            this.activity = act;
            this.friendsName = names;
            this.pic_urls = urls;
            this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.imageLoader = new ImageLoader(activity.getApplicationContext());
        }

        public int getCount() {
            return pic_urls.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if (convertView == null) vi = inflater.inflate(R.layout.facebook_friends, null);

            TextView label = (TextView) vi.findViewById(R.id.profile_name);
            ImageView icon = (ImageView) vi.findViewById(R.id.profile_pic);
            btntag = (Button) vi.findViewById(R.id.btntag);
            
            label.setText(friendsName.get(position));
            imageLoader.DisplayImage(pic_urls[position], icon);
            
            btntag.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					final int index = getListView().getPositionForView((LinearLayout) v.getParent());
					String friend_id = data.getObject(index).getID();
		    		String friend_name = data.getObject(index).getName();
		    		boolean already_added = false;
		    		
		    		for (int i = 0; i < ((Jpact) GallSelectedFBFriends.this.getApplication()).multi_tagged_friends_id.get(pic_position).size(); i ++) {
	    				if (friend_id.equals(((Jpact) GallSelectedFBFriends.this.getApplication()).multi_tagged_friends_id.get(pic_position).get(i))) {
	    					already_added = true;
	    				} 
	    			}
	    			
	    			if (already_added) {
	    				Toast.makeText(getApplicationContext(), friend_name + " is already selected.", Toast.LENGTH_SHORT).show();
	    				System.out.println("tag button: already exists, don't add");
	    			} else {
	    				((Jpact) GallSelectedFBFriends.this.getApplication()).multi_tagged_friends_id.get(pic_position).add(friend_id);
	    	    		((Jpact) GallSelectedFBFriends.this.getApplication()).multi_tagged_friends_name.get(pic_position).add(friend_name);
	    				Toast.makeText(getApplicationContext(), friend_name + " is selected.", Toast.LENGTH_SHORT).show();
	    				System.out.println("tag button: add success");
	    			}
				}
			});
            
            return vi;
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		((Jpact) this.getApplication()).friend_list.clear();
    		finish();
    	}
        
    	return super.onKeyDown(keyCode, event);
    }
}
