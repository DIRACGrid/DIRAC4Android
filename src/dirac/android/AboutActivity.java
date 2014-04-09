package dirac.android;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AboutActivity  extends Activity {


    public void onCreate(Bundle savedInstanceState) {

    	
    	
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.about);
    	setTitle("dirac > about");
    	
    	String Name = "";
    	@SuppressWarnings("unused")
		Integer Version = 0;
    	String VersionName = "";
    	try {
    	    PackageInfo manager=getPackageManager().getPackageInfo(getPackageName(), 0);
    	    Name = manager.packageName;
    	    Version = manager.versionCode;
    	    VersionName = manager.versionName;
    	    
    	    


        	    TextView  text0 = (TextView) findViewById(R.id.aboutWeb);
        	    text0.setText("http://diracgrid.org/");
    	    TextView  text = (TextView) findViewById(R.id.aboutName);
    	    text.setText(Name);
    	    TextView  text2 = (TextView) findViewById(R.id.aboutVersion);
    	    text2.setText(VersionName);
    	    
    	} catch (NameNotFoundException e) {
    	    //Handle exception
    	}
    	
    	
    	
    	
    	
    	
    	
    	
    	
    }
	   

}
