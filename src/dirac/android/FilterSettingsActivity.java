package dirac.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class FilterSettingsActivity  extends Activity {

    EditText myMaxJob;
    Switch mySwitch;

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.filters);
	setTitle("dirac > settings");
		
		
	Button save = (Button)findViewById(R.id.buttonSave);
	myMaxJob = (EditText)findViewById(R.id.maxJobsEdit);
	Integer mymax = 10;
	mymax = CacheHelper.readInteger(getApplicationContext(), CacheHelper.NMAXBJOBS, mymax);	
	myMaxJob.setText(mymax.toString());
	
	mySwitch = (Switch)findViewById(R.id.switch1);
	mySwitch.setChecked(CacheHelper.readBoolean(getApplicationContext(), CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE, false));
		
		
	save.setOnClickListener(new OnClickListener(){

		public void onClick(View arg0) {
	
		    Integer mymax = Integer.parseInt(myMaxJob.getText().toString());
		    Boolean myauto = mySwitch.isChecked();
		    CacheHelper.writeInteger(getBaseContext(), CacheHelper.NMAXBJOBS, mymax);	
		    CacheHelper.writeBoolean(getBaseContext(), CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE, myauto);	
		    Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();
		    finish();
		}
	    });
	
    }
	   

}
