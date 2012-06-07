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
		Job myjobid = (Job) i.getParcelableExtra("myJob");
		setTitle("Job id: "+myjobid.toString());
		
		TextView job_info_color = (TextView)findViewById(R.id.job_info_color);
		TextView job_info_state = (TextView)findViewById(R.id.job_info_state);
		TextView job_info_site = (TextView)findViewById(R.id.job_info_site);
		TextView job_info_time = (TextView)findViewById(R.id.job_info_time);
		TextView job_info_name = (TextView)findViewById(R.id.job_info_name);
		job_info_site.setText(myjobid.site);
		job_info_state.setText(myjobid.state);
		job_info_time.setText(myjobid.time);
		job_info_name.setText(myjobid.name);
		
		int state_color = Color.rgb(170,102,204);

		if("running".equals(myjobid.state)){
			 state_color = Color.rgb(153,204,0);
		}else if ("completed".equals(myjobid.state)){
			state_color = Color.rgb(51,181,229);
		}else if ("failed".equals(myjobid.state)){
			state_color = Color.rgb(255,68,68);
		}
		
		
		job_info_color.setBackgroundColor(state_color);
		
		
		
	}
}
