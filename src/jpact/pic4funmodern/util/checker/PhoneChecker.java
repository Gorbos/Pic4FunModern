package jpact.pic4funmodern.util.checker;

import android.content.Context;
import android.util.DisplayMetrics;

public class PhoneChecker {
	
	private Context context;
	
	public Integer screenWidth;
	public Integer screenHeight;
	
	public PhoneChecker(Context localContext){
		context = localContext;
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
	}
	
	
	

}
