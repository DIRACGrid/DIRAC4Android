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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StateActivity extends Activity{


	Random r;
	private 	JobArrayAdapter adapter;

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String itemSelected;
	private final Context context = this;
	private	final CharSequence[] jodActionFailed = {"Reschedule", "Delete", "Kill"};
	ArrayAdapter<String> adapter2;
	private OnItemLongClickListener listener;
	private JobsDataSource datasource;
	List<Job> myjobids;
	private String state;

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


		CacheHelper.writeInteger(context, CacheHelper.GETJOBSTYPE2, myjobids.size());


		//T1.setBackgroundResource(R.color.completed);

		// Set the View layer
		setContentView(R.layout.mainstate);
		//	setTitle("TestIconizedListView");


		// Get reference to ListView holder
		ListView lv = (ListView) this.findViewById(R.id.STATELV);


		View footer = getLayoutInflater().inflate(R.layout.list_footer, null);

		TextView footerTV = (TextView)footer.findViewById(R.id.footer_text);
		footerTV.setText("download more");

		lv.addFooterView(footer);

		// Set the ListView adapter
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
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
					Integer nbJob = CacheHelper.readInteger(context, CacheHelper.GETJOBSTYPE2, defInt);
					SJobs = performApiCall(Constants.API_JOBS+"?status="+state+"&startJob="+nbJob.toString()+"&maxJobs=10&"+JobType);
					CacheHelper.writeInteger(context, CacheHelper.GETJOBSTYPE2, (nbJob+10));


					datasource.open();
					dbHelper = new MySQLiteHelper(context);
					database = dbHelper.getWritableDatabase(); 
					database = dbHelper.getWritableDatabase(); 
					Gson gson = new Gson();

					Jobs  jobs = gson.fromJson(SJobs, Jobs.class);
					datasource.parse(jobs);	
					database.close();	
					Log.e(TAG,SJobs);    
				//	Collections.reverse(jobs.getJobs());
					for(int k = 0; k < jobs.getJobs().size(); k++)
						adapter.add(jobs.getJobs().get(k));
					dialog.dismiss();


				}

			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View arg1,
					final int position2, long arg3) {
				if(position2 != myjobids.size()){

					itemSelected=parent.getItemAtPosition(position2).toString();
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("Select an Action for jobID "+itemSelected);
					builder.setItems(jodActionFailed, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
							builder2.setMessage("Do you really want to "+jodActionFailed[item]+" jobID: "+itemSelected+" ?");
							builder2.setCancelable(true);
							//	JobID myjobid = jobList.get(position2);

							switch(item){
							case 0: 								
								builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {								
									public void onClick(DialogInterface dialog, int which) {	
										//		adapter.remove(itemSelected);
										//		adapter.notifyDataSetChanged();
										Toast.makeText(getApplicationContext(), "ddddasda", Toast.LENGTH_SHORT).show();
									}
								});
								builder2.setNegativeButton("Nooo", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();
									}
								});
								builder2.show();

							case 1: Toast.makeText(getApplicationContext(), jodActionFailed[item]+"ddddasda", Toast.LENGTH_SHORT).show();
							case 2: Toast.makeText(getApplicationContext(), jodActionFailed[item]+"ddasdadassdasdasdddd", Toast.LENGTH_SHORT).show();

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






	public String performApiCall(String myUrl) {

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

		//String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		//	String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");

		String token = Constants.ACCESS_TOKEN;
		String secret = Constants.ACCESS_TOKEN_SECRET;
		//("getConsumer",token);
		//Log.d("getConsumer",secret);
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
