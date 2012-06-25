package dirac.android;

import oauth.signpost.OAuth;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;
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


	public boolean isGranted(){		

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		String token = prefs.getString(OAuth.OAUTH_TOKEN, "no");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "no");


		if(token != "no" && secret != "no"){
			return true;
		}else{
			Toast.makeText(context, "app not granted, please proceed throuth the \"Manage Certificates\" settings", Toast.LENGTH_SHORT).show();	
			return false;
		}
	}


}
