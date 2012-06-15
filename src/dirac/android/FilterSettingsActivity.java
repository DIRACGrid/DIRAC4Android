package dirac.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class FilterSettingsActivity  extends Activity {



	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filters);
		setTitle("Filter Settings");
		
		 android.widget.Spinner spinner = (android.widget.Spinner) findViewById(R.id.spinner1);
		 

		 
		 
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	                this, R.array.spin_mnth_array, android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner.setAdapter(adapter);

	        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	    }
	
	
	    public class MyOnItemSelectedListener implements OnItemSelectedListener {

	        public void onItemSelected(AdapterView<?> parent,
	            View view, int pos, long id) {
	        	

	        	
	        	if(pos == 0){
	        		Toast.makeText(parent.getContext(), "Next Update with only your jobs", Toast.LENGTH_LONG).show();
	    			CacheHelper.writeString(getBaseContext(), CacheHelper.GETJOBSTYPE,"");


	        	}else{
	        		Toast.makeText(parent.getContext(), "Next Update with allOwners jobs", Toast.LENGTH_LONG).show();
	    			CacheHelper.writeString(getBaseContext(), CacheHelper.GETJOBSTYPE,"allOwners=true");
	   

	        	}
	        	

	        	
	        }

	        public void onNothingSelected(AdapterView parent) {

	          // Do nothing.
	        }
}
}