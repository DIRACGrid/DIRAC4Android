package dirac.android;

import android.content.Context;
import android.net.ConnectivityManager;

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

}
