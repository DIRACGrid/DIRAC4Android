package dirac.android;

import oauth.signpost.OAuth;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class Connectivity {
	
	private Context context;

	public Connectivity(Context myContext){
		this.context = myContext;
	}


	public boolean isOnline() {
	    ConnectivityManager cm 
        = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    return cm.getActiveNetworkInfo() != null && 
	       cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	
	
	public boolean isGranted(){		
		Log.i("here1","not rganted");

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
  
		String token = prefs.getString(OAuth.OAUTH_TOKEN, "no");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "no");
 

		if(token != "no" && secret != "no"){
		Log.i("here","not rganted");
			return true;
		}else
			return false;
		
	}
	

}
