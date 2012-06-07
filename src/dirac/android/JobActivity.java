package dirac.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class JobActivity extends Activity {
	
	ArrayAdapter<String> adapter;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jobinfo);
		Intent i = getIntent();
		Job myjob = (Job) i.getParcelableExtra("myJob");
		setTitle("Job id: "+myjob.toString());
		Status s = new Status();
		int myposition = s.get(myjob.state);
		TextView job_info_color = (TextView)findViewById(R.id.job_info_color);
		TextView job_info_state = (TextView)findViewById(R.id.job_info_state);
		TextView job_info_site = (TextView)findViewById(R.id.job_info_site);
		TextView job_info_time = (TextView)findViewById(R.id.job_info_time);
		TextView job_info_name = (TextView)findViewById(R.id.job_info_name);
		job_info_site.setText(myjob.site);
		job_info_state.setText(myjob.state);
		job_info_time.setText(myjob.time);
		job_info_name.setText(myjob.name);
		
		job_info_color.setBackgroundColor(getResources().getColor(s.ColorStatus[myposition]));
		
		
		
	}
}
