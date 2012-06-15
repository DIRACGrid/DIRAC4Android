package dirac.android;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserProfileActivity  extends Activity {



	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user);
		setTitle("User Profile");
		
		
		Button launchOauth = (Button) findViewById(R.id.getGrant);
		Button clearCredentials = (Button) findViewById(R.id.clearGrant);

		launchOauth.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	startActivity(new Intent().setClass(v.getContext(), PrepareRequestTokenActivity.class));
		    }
		});
		
		
		
		
		
		
		
		
	}
}
		