/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jpact.pic4funmodern.ui.sharing.utils;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.content.Intent;
import android.content.res.Configuration;

import jpact.pic4funmodern.ui.R;
import jpact.pic4funmodern.ui.*;

/**
 *
 * @author Dave
 */

/** This class is the Alert Activity - i.e. the alert that is notifying the user "You have successfully logout your Facebook account." **/

public class Alert extends FragmentActivity implements OnClickListener {

    private TextView lblmessage;
    private Button btnOk;
    private boolean transaction_success;
    private boolean json_error;
    
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.alert);
        transaction_success = ((Jpact) getApplication()).transaction_success;
        json_error = ((Jpact) getApplication()).json_message;
        
        lblmessage = (TextView) findViewById(R.id.lblalert);
        if (transaction_success) {
            lblmessage.setText(((Jpact) this.getApplication()).success_message);
        } else {
            lblmessage.setText(((Jpact) this.getApplication()).error_message);
        }
        
        btnOk = (Button) findViewById(R.id.btnok);
        btnOk.setOnClickListener(this);
    }

    public void onClick(View v) {
    	if (this.json_error) {
    		if (v == findViewById(R.id.btnok)) {
    			((Jpact) this.getApplication()).json_message = false;
                ((Jpact) this.getApplication()).transaction_success = false;
                
                Intent i = new Intent(this, MainMenuActivity.class);
                startActivity(i);
                finish();
            }
    	} else {
    		if (v == findViewById(R.id.btnok)) {
                finish();
            }
    	}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (this.json_error) {
    		((Jpact) this.getApplication()).json_message = false;
            ((Jpact) this.getApplication()).transaction_success = false;
            
            Intent i = new Intent(this, MainMenuActivity.class);
            startActivity(i);
            finish();
    	} else {
    		if (keyCode == KeyEvent.KEYCODE_BACK) {
                finish();
            }
    	}
        
    	return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
