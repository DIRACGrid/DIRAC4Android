package dirac.android;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class StateActivity extends Activity{


	Random r;
	private 	JobArrayAdapter adapter;
	private View footer; 
	private ListView lv ;
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String itemSelected;
	private final Context context = this;
	private	final CharSequence[] jodActionFailed = {"Reschedule", "Delete", "Kill"};
	ArrayAdapter<String> adapter2;
	private JobsDataSource datasource;
	List<Job> myjobids;
	private String state;
private Job selectJob;
	Integer mymax = 10;
	protected ProgressBar PBar;
	private PerformAPICall apiCall;
	final String TAG = getClass().getName();
	private SharedPreferences prefs;
	/** Ca	private CommentsDataSource datasource;
lled when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		datasource = new JobsDataSource(this);
		datasource.open();


		Bundle b = getIntent().getExtras();

		if(b!=null){
			state = b.getString("myState");
			myjobids = datasource.getAllJobIDsOfSatus(state);
			adapter = new JobArrayAdapter(
					getApplicationContext(), R.layout.listjob, myjobids);	
		}else{		
			myjobids = datasource.getAllJobIDs();

			adapter = new JobArrayAdapter(
					getApplicationContext(), R.layout.listjob, myjobids);

		}


		CacheHelper.writeInteger(context, CacheHelper.STARTJOBNB, myjobids.size());

		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);

		//T1.setBackgroundResource(R.color.completed);

		// Set the View layer
		setContentView(R.layout.mainstate);
		//	setTitle("TestIconizedListView");


		// Get reference to ListView holder
		lv = (ListView) this.findViewById(R.id.STATELV);


		footer = getLayoutInflater().inflate(R.layout.list_footer, null);

		TextView footerTV = (TextView)footer.findViewById(R.id.footer_text);
		footerTV.setText("Download more"); 
		mymax = CacheHelper.readInteger(getApplicationContext(), CacheHelper.NMAXBJOBS, mymax);	

		if(myjobids.size()%mymax == 0)
			lv.addFooterView(footer);

		// Set the ListView adapter
		lv.setAdapter(adapter);

		apiCall = new PerformAPICall(context,prefs);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {


				// When clicked, show a toast with the TextView text
				if(position != myjobids.size()){
					itemSelected=parent.getItemAtPosition(position).toString();
					// Parse the inputstream
					Job myjobid = myjobids.get(position);

					Intent myIntent = new Intent(view.getContext(), JobActivity.class);			


					myIntent.putExtra("jid", myjobid.getJid());
					startActivity(myIntent);	
				}else{

					ProgressDialog dialog = ProgressDialog.show(context, "","Downloading/Loading. Please wait...", true);


					String SJobs = "";
					String SdefValue = "";
					String JobType = CacheHelper.readString(context, CacheHelper.GETJOBSTYPE, SdefValue);
					Integer defInt	= 20;
					Integer nbJob = CacheHelper.readInteger(context, CacheHelper.STARTJOBNB, defInt);

					SJobs =  apiCall.performApiCall(Constants.API_JOBS+"?status="+state+"&startJob="+nbJob.toString()+"&maxJobs="+mymax.toString()+"&"+JobType);

					CacheHelper.writeInteger(context, CacheHelper.STARTJOBNB, (nbJob+mymax));

					Log.i("SJobs",SJobs);
					datasource.open();
					dbHelper = new MySQLiteHelper(context);
	 
					database = dbHelper.getWritableDatabase(); 
					Gson gson = new Gson();

					Jobs  jobs = gson.fromJson(SJobs, Jobs.class);
					datasource.parse(jobs);	
					database.close();	
					Log.e(TAG,SJobs);    
					Collections.reverse(jobs.getJobs());
				//	for(int k = 0; k < jobs.getJobs().size(); k++)
				  //  adapter.addAll(jobs.getJobs());
					for(int k = 0; k < jobs.getJobs().size(); k++)
						 adapter.add(jobs.getJobs().get(k));
					dialog.dismiss();
					if(jobs.getJobs().size() < mymax)
						lv.removeFooterView(footer);


				}

			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View arg1,
					final int position2, long arg3) {
				if(position2 != myjobids.size()){
					selectJob = myjobids.get(position2);

					itemSelected=parent.getItemAtPosition(position2).toString();
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("Select an Action for jobID "+selectJob.getJid());
					builder.setItems(jodActionFailed, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {

							AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
							builder2.setMessage("Do you really want to "+jodActionFailed[item]+" jobID: "+selectJob.getJid()+" ?");
							builder2.setCancelable(true);
							//	JobID myjobid = jobList.get(position2);

							switch(item){
							case 0: 								
								builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {								
									public void onClick(DialogInterface dialog, int which) {	

										apiCall.performApiCall(Constants.API_RESCHEDULE+selectJob.getJid());
										Toast.makeText(getApplicationContext(), "Rescheduled", Toast.LENGTH_SHORT).show();
									}
								});
								builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();

										dialog.cancel();
									}
								});
								builder2.show();

							case 1: Toast.makeText(getApplicationContext(), jodActionFailed[item], Toast.LENGTH_SHORT).show();
							case 2: Toast.makeText(getApplicationContext(), jodActionFailed[item], Toast.LENGTH_SHORT).show();

							}
						}
					});

					builder.show();	


				}

				// TODO Auto-generated method stub
				return false;
			}


		});		

 
		datasource.close();


	}










}
