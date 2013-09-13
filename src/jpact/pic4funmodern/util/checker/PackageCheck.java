package jpact.pic4funmodern.util.checker;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class PackageCheck {
	
	private Context context;
	
	public PackageCheck(Context localContext){
		context = localContext;
	}
	
	public boolean isPackageExists(String target) {
    	boolean package_exists = false;
    	try {
    	    ApplicationInfo info = context.getPackageManager().getApplicationInfo(target, 0);
    	    package_exists = true;
    	    System.out.println("from main menu: package exists...");
    	    System.out.println("from main menu: package info: " + info);
    	} catch(PackageManager.NameNotFoundException e) {
    		package_exists = false;
    		System.out.println("from main menu: package does not exists...");
    	}
    	return package_exists;
    }
	
	public boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	

}
