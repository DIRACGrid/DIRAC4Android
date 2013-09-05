package dirac.android;

import java.util.Calendar;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class Connectivity {

    private Context context;


    public Connectivity(Context myContext){
	this.context = myContext;
    }


    public boolean isOnline() {
	ConnectivityManager cm 
	    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	if( cm.getActiveNetworkInfo() != null && 
	    cm.getActiveNetworkInfo().isConnectedOrConnecting()){
	    return true;
	}else{
	    Toast.makeText(context, "no internet connectivity", Toast.LENGTH_SHORT).show();

	    return false;
	}
    }

	

	
    public boolean isGranted() {		

	String accessToken = CacheHelper.readString(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN, "no");
	long  exptime = CacheHelper.readLong(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_EXPIRES_TIME, -1);

	Calendar c = Calendar.getInstance(); 
	long currentime = c.getTimeInMillis();

	if(accessToken != "no" && exptime > currentime ){

			
	    return true;
	}else{
			
	    CacheHelper.writeString(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN, "no");

	    //	Toast.makeText(context, "app not granted, please proceed throuth the \"Manage Certificates\" settings", Toast.LENGTH_SHORT).show();	
	    return false;
	}
    }
	
}

