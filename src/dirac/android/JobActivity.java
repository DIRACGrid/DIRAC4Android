package dirac.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class JobActivity extends Activity {

	ArrayAdapter<String> adapter;
	private JobsDataSource datasource;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jobinfo);
		Intent i = getIntent();
		
		datasource = new JobsDataSource(this);
		datasource.open();
		Log.d("JA",i.getStringExtra("jid") );
		Job myjob = datasource.getJobInfo(i.getStringExtra("jid"));
				
				
		datasource.close();
	
		setTitle("Job id: "+myjob.getJid());
		
		
		Status s = new Status();
		int myposition = s.get(myjob.getStatus());
		TextView job_info_color = (TextView)findViewById(R.id.job_info_color);
		TextView job_info_state = (TextView)findViewById(R.id.job_info_state);
		TextView job_info_site = (TextView)findViewById(R.id.job_info_site);
		TextView job_info_time = (TextView)findViewById(R.id.job_info_time);
		TextView job_info_name = (TextView)findViewById(R.id.job_info_name);
		job_info_site.setText(myjob.getSite());
		job_info_state.setText(myjob.getStatus());
		job_info_time.setText(myjob.getCpuTime());
		job_info_name.setText(myjob.getName());

		job_info_color.setBackgroundColor(getResources().getColor(Status.ColorStatus[myposition]));



	}
}
