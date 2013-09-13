package jpact.pic4funmodern.util;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

public class PhotoUtility {

	//Function for video recording
	public Intent recordVideo(Context c) {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); 
		return intent;
	}
}
