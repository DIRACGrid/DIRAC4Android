package dirac.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FilterSettingsActivity  extends Activity {

	EditText myMaxJob;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filters);
		setTitle("Filter Settings");
		
		
		Button save = (Button)findViewById(R.id.buttonSave);
		myMaxJob = (EditText)findViewById(R.id.maxJobsEdit);
		Integer mymax = 10;
		mymax = CacheHelper.readInteger(getApplicationContext(), CacheHelper.NMAXBJOBS, mymax);	
		myMaxJob.setText(mymax.toString());

		
		save.setText("Save");
		
		save.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
	
		int mymax = Integer.parseInt(myMaxJob.getText().toString());
		CacheHelper.writeInteger(getBaseContext(), CacheHelper.NMAXBJOBS, mymax);	
		Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();
		finish();
			}
	    });
	
	}
	   

}