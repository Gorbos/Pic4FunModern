package jpact.pic4funmodern.util;

import jpact.pic4funmodern.ui.Jpact;

import android.support.v4.app.FragmentActivity;

/** Class used if you want to add config on this app **/

public class JpactConfig {

	public FragmentActivity activity;
	
	public JpactConfig(FragmentActivity act) {
		this.activity = act;
	}
	
	public void initialize() {
		((Jpact)this.activity.getApplication()).app_state = "ok";
	}
}