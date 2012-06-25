package dirac.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class JobActivity extends Activity {

	ArrayAdapter<String[]> adapter;
	private JobsDataSource datasource;
	List<String[]> job_infos;
	private Connectivity connect;
	private SharedPreferences prefs;
	private Job myjob;
	private PerformAPICall apiCall;

	private final Context context = this;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainjobinfo);
		Intent i = getIntent();
		connect = new Connectivity(context);

		datasource = new JobsDataSource(this);
		datasource.open();
		myjob = datasource.getJobInfo(i.getStringExtra("jid"));	

		datasource.close();

		job_infos = parse_again(myjob);

		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		apiCall = new PerformAPICall(context,prefs);
		adapter = new JobInfoArrayAdapter(getApplicationContext(), R.layout.listjob, job_infos);


		TextView tt = (TextView) this.findViewById(R.id.JOBLVCOLOR);
		Status status = new Status();
		tt.setBackgroundColor(getResources().getColor(status.ColorStatus[status.get(myjob.getStatus())]));
		tt = (TextView) this.findViewById(R.id.JOBLVCOLOR2);
		tt.setBackgroundColor(getResources().getColor(status.ColorStatus[status.get(myjob.getStatus())]));


		// Get reference to ListView holder
		ListView lv = (ListView) this.findViewById(R.id.JOBLV);
		View footer = getLayoutInflater().inflate(R.layout.list_footer, null);

		TextView footerTV = (TextView)footer.findViewById(R.id.footer_text);
		footerTV.setText("Get JDL");

		lv.addFooterView(footer);


		// Set the ListView adapter
		lv.setAdapter(adapter);


		setTitle("Job id: "+myjob.getJid());

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position ==job_infos.size() ){

					if(connect.isOnline()){
						if(connect.isGranted()){
							
							
							apiCall.SetExtraInfo(myjob.getJid());
							apiCall.performApiCall(Constants.API_JOBS+"/"+myjob.getJid()+"/description", "JDL");
							
						}
					}
				}
			}
		});

	}




	public List<String[]> parse_again(Job i){


		List<String[]> infos = new ArrayList<String[]>();
		String[] temp = new String[2];

		temp[0] = "Name";
		temp[1] = i.getName();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Status";
		temp[1] = i.getStatus();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Site";
		temp[1] =i.getSite();	
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Owner Group";
		temp[1] = i.getOwnerGroup();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "App Status";
		temp[1] = i.getAppStatus();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Minor Status";
		temp[1] = i.getMinorStatus();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Owner";
		temp[1] = i.getOwner();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Owner DN";
		temp[1] = i.getOwnerDN();
		infos.add(temp);

		temp = new String[2];
		temp[0] = "Job Group";
		temp[1] = i.getJobGroup();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Reschedules";
		temp[1] = i.getReschedules();
		infos.add(temp);


		temp = new String[2];
		temp[0] = "Setup";
		temp[1] = i.getSetup();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "CPU Time";
		temp[1] = i.getCpuTime();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Start Execution";
		temp[1] = i.getTimes().getStartExecution();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Last Update";
		temp[1] = i.getTimes().getLastUpdate();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Submission";
		temp[1] = i.getTimes().getSubmission();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Last SOL";
		temp[1] = i.getTimes().getLastSOL();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "End Execution";
		temp[1] = i.getTimes().getEndExecution();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Heart Beat";
		temp[1] = i.getTimes().getHeartBeat();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Priority";
		temp[1] = i.getPriority();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Verified Flag";
		temp[1] = i.getFlags().getVerified();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Deleted Flag";
		temp[1] = i.getFlags().getDeleted();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Retrieved Flag";
		temp[1] = i.getFlags().getRetrieved();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Output SB Ready";
		temp[1] = i.getFlags().getOutputSandboxReady();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Input SB Ready";
		temp[1] = i.getFlags().getInputSandboxReady();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Accounted Flag";
		temp[1] = i.getFlags().getAccounted();
		infos.add(temp);
		temp = new String[2];
		temp[0] = "Killed Flag";
		temp[1] = i.getFlags().getKilled();
		infos.add(temp);

		return infos;



	}




}