package dirac.android;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class JobActivity extends Activity {

	private static final String TAG = "hehe";
	ArrayAdapter<String[]> adapter;
	private JobsDataSource datasource;
	List<String[]> job_infos;
private Connectivity connect;
	private SharedPreferences prefs;
	private Job myjob;

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
			    	String result = performApiCall(Constants.API_JOBS+"/"+myjob.getJid()+"/description");
				//	JobDescription  jobd = gson.fromJson(result, JobDescription.class);
				//	Log.e(TAG,jobd.getOwnerDN());
			    	
			    	
					Intent myIntent = new Intent(view.getContext(), JobDescriptionActivity.class);			


					myIntent.putExtra("jid", myjob.getJid());
					myIntent.putExtra("description",result);

					startActivity(myIntent);
					}else{
						Toast.makeText(context, "App not granted, please proceed throuth the \"Manage certificates\" settings", Toast.LENGTH_SHORT).show();

					}
				}else
					Toast.makeText(context, "no internet connectivity", Toast.LENGTH_SHORT).show();
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

	
	

	
	
	
	private String performApiCall(String myUrl) {

		String jsonOutput = "";
		try {  	      	

			try{
				jsonOutput = doGet(myUrl,getConsumer(this.prefs));

			}catch (Exception e) {
				Toast.makeText(getApplicationContext(), "ERROR CONNECTIUON", Toast.LENGTH_LONG).show();			//	textView.setText("Error retrieving contacts : " + jsonOutput);.show
			}

		} catch (Exception e) {
			Log.e(TAG, "Error executing request",e);
		}
		return jsonOutput;
	}


	private OAuthConsumer getConsumer(SharedPreferences prefs) {

		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");

	
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		consumer.setTokenWithSecret(token, secret);
		//	Log.d("getConsumer",consumer.toString());
		return consumer;
	}

	private String doGet(String url,OAuthConsumer consumer) throws Exception {
		Log.i(TAG,"Requesting URL : " + url);

		try{
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			Log.i(TAG,"Requesting URL : " + url);
			consumer.sign(request);
			HttpResponse response = httpclient.execute(request);
			Log.i(TAG,"Statusline : " + response.getStatusLine());
			InputStream data = response.getEntity().getContent();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
			String responeLine;
			StringBuilder responseBuilder = new StringBuilder();
			while ((responeLine = bufferedReader.readLine()) != null) {
				responseBuilder.append(responeLine);
			}
			Log.i(TAG,"Response : " + responseBuilder.toString());
			return responseBuilder.toString();
		}catch (Exception e) {
			Log.e(TAG, "Error executing request",e);
			//	textView.setText("Error retrieving contacts : " + jsonOutput);
			return "";

		}
	}	



}