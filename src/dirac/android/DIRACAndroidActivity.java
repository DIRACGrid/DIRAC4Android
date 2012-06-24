package dirac.android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.ActionBar;
import com.google.gson.Gson;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;

public class DIRACAndroidActivity extends SherlockActivity implements ActionBar.OnNavigationListener {

	final String TAG = getClass().getName();
	private SharedPreferences prefs;
	ProgressDialog dialog;

	public static final String PREFS_NAME = "MyPrefsFile";
	Random r;
	private Intent StatsIntent;


	private String itemSelected;
	public final Context context = this;
	private	final CharSequence[] jodActionFailed = {"Reschedule", "Delete", "Kill"};
	ArrayAdapter<String> adapter2;	

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private Connectivity connect;
	private JobsDataSource datasource;

	/**when the activity is first created. */
	private String[] mLocations;


	private PerformAPICall apiCall;

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {


		Gson gson = new Gson();
		String SSummary = "" ;
		StatusSummary summary;
		TextView UserTV = (TextView)findViewById(R.id.userText1);


		switch (item.getItemId()) {
		case R.id.menu_mine:
			CacheHelper.writeString(this, CacheHelper.GETJOBSTYPE,"");	
			UserTV.setText("Next Update: My Jobs");			

			return true;
		case R.id.menu_all:
			CacheHelper.writeString(this, CacheHelper.GETJOBSTYPE,"allOwners=true");	
			UserTV.setText("Next Update: All Onwers");			

			return true;
		case R.id.menu_refresh:

			if(connect.isOnline()){
				if(connect.isGranted()){


					database = dbHelper.getWritableDatabase();					
					datasource.open();	
					String SdefValue = "";



					String JobType = CacheHelper.readString(context, CacheHelper.GETJOBSTYPE, SdefValue);


					if (JobType == "")
						SSummary = apiCall.performApiCall(Constants.API_SUMMARY);
					else	
						SSummary = apiCall.performApiCall(Constants.API_SUMMARY+"?"+JobType);

					int idefValue = 0;
					Integer gg = CacheHelper.readInteger(context, CacheHelper.NMAXBJOBS, idefValue);
					summary = gson.fromJson(SSummary, StatusSummary.class);
					Log.d("test",gg.toString());


					datasource.parseSummary(summary);	
					CacheHelper.writeBoolean(this, CacheHelper.GETJOBS,true);	
					loadDataOnScreen();
					database.close();	
					datasource.close();	
				}
			}

			return true;
		case R.id.manage_filters:
			//Intent myIntent = new Intent(context, FilterSettingsActivity.class);				 
			//startActivity(myIntent);
			return true;  
		case R.id.manage_certs:
			startActivity(new Intent(context, UserProfileActivity.class));
			return true;  

		case R.id.prefs:
			startActivity(new Intent(context, FilterSettingsActivity.class));
			return true;  

		}



		return false;

	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActionBarSherlock
		//getSupportActionBar().setDisplayShowHomeEnabled(false);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.main);
		getSupportActionBar().setDisplayShowTitleEnabled(false);


		mLocations = getResources().getStringArray(R.array.locations);
		Context context = getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.locations, R.layout.spinner_item);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);

		connect = new Connectivity(context);

		datasource = new JobsDataSource(this);
		datasource.open();
		dbHelper = new MySQLiteHelper(context);
		database = dbHelper.getWritableDatabase(); 


		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		Boolean defValueb = false;
		CacheHelper.writeBoolean(context,CacheHelper.GETJOBS, defValueb);
		CacheHelper.writeString(context,CacheHelper.GETJOBSTYPE, "");

		loadDataOnScreen();

		connect.isGranted();
		database.close();	
		datasource.close();

		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		apiCall = new PerformAPICall(context,prefs);


	}

	public boolean onNavigationItemSelected(int itemPosition, long itemId) {

		String SdefValue = "";
		String JobType = CacheHelper.readString(context, CacheHelper.GETJOBSTYPE, SdefValue);
		if (mLocations[itemPosition].compareTo("Jobs") == 0) {
			return true;
		} else if (mLocations[itemPosition].compareTo("Stats") == 0){
			if (StatsIntent== null){
				if(connect.isOnline())
					apiCall.performApiCall(Constants.API_HISTORY+"?"+JobType, "Stats")	;
			}
		}
		return true;
	}



	public void loadDataOnScreen(){

		////// Create a customized ArrayAdapter

		final Status[] map = datasource.getLastUpdate(); 
		if(map[0]!=null){

			StateInfoArrayAdapter adapter = new StateInfoArrayAdapter(
					this.getApplicationContext(), R.layout.liststatus, map);

			// Get reference to ListView holder
			ListView lv = (ListView) this.findViewById(R.id.states);

			// Set the ListView adapter
			lv.setAdapter(adapter);


			//	database = dbHelper.getWritableDatabase();
			//		datasource.open();	
			Boolean defValue = false;
			Boolean test = CacheHelper.readBoolean(context, CacheHelper.GETJOBS, defValue);
			String SdefValue = "";
			String JobType = CacheHelper.readString(context, CacheHelper.GETJOBSTYPE, SdefValue);
			TextView UserTV = (TextView)findViewById(R.id.userText1);

			if(JobType==""){
				UserTV.setText("My Jobs");			
			}else{
				UserTV.setText("All Owners Jobs");	

			}

			if(test.booleanValue()){

				String[] status = Status.PossibleStatus;




				datasource.open();
				database = dbHelper.getWritableDatabase(); 
				dbHelper.deleteTable(database, dbHelper.DIRAC_JOBS);


				setSupportProgress(0);


				String myStrings = "";

				for(int i = 0; i< map.length;i++){
					if(i < (map.length - 1))
						myStrings = myStrings+map[i].name()+",";
					else
						myStrings = myStrings+map[i].name()
						;

				}
				//dialog = 	ProgressDialog.show(context, "", "Downloading/Loading. Please wait...", true);

				//apiCall.SetProgressDialog(dialog);


				Integer mymax = 10;
				mymax = CacheHelper.readInteger(getApplicationContext(), CacheHelper.NMAXBJOBS, mymax);	
				if(connect.isOnline())
					if (connect.isGranted())
						apiCall.performApiCall( Constants.API_JOBS+"/groupby/status?maxJobs="+mymax.toString()+"&status="+myStrings+"&flatten=true&"+JobType, "");



				//	performApiCall task2 = new performApiCall();
				//	task2.execute(new String[] { Constants.API_JOBS+"/groupby/status?maxJobs=20&status="+s+"&flatten=true" });
				//Log.i("jobs",myStrings);
				//task2.execute(new String[] { });

				//	task2.execute(new String[] { Constants.API_JOBS+"?maxJobs=20&status="+myStrings+"&"+JobType });	
				//}





			}else{				
				Log.d("cache test","false");		
			}
			CacheHelper.writeBoolean(this, CacheHelper.GETJOBS,false);	



			//	Gson gson = new Gson();
			//	Jobs jobs = gson.fromJson(Sjobs, Jobs.class);
			//	dbHelper.deleteTable(database, dbHelper.DIRAC_JOBS);
			//	datasource.parse(jobs);		
			//	database.close();		
			//	datasource.close();

			String[] status = Status.PossibleStatus;
			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// When clicked, show a toast with the TextView text
					Intent myIntent = new Intent(view.getContext(), StateActivity.class);				 
					myIntent.putExtra("myState", ((Status) parent.getItemAtPosition(position)).name());
					startActivity(myIntent);
				}
			});

			lv.setOnItemLongClickListener(new OnItemLongClickListener() {

				public boolean onItemLongClick(AdapterView<?> parent, View arg1,
						final int position2, long arg3) {

					itemSelected=((Status) parent.getItemAtPosition(position2)).name();
					//parent.getItemAtPosition(position2).toString();
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("Select an Action for the batch of job with the state: "+itemSelected);
					builder.setItems(jodActionFailed, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
							builder2.setMessage("Do you really want to "+jodActionFailed[item]+" jobID: "+itemSelected+" ?");
							builder2.setCancelable(true);
							//	JobID myjobid = jobList.get(position2);

							if(item == 0){		
								builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {								
									public void onClick(DialogInterface dialog, int which) {	
										//		adapter.remove(itemSelected);
										//		adapter.notifyDataSetChanged();

										Toast.makeText(getApplicationContext(), "hola", Toast.LENGTH_SHORT).show();
									}
								});
								builder2.setNegativeButton("Nooo", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();
									}
								});
								builder2.show();
							}
							else if(item == 1){ Toast.makeText(getApplicationContext(), jodActionFailed[item], Toast.LENGTH_SHORT).show();		}		

							else if(item ==2){Toast.makeText(getApplicationContext(), jodActionFailed[item], Toast.LENGTH_SHORT).show();	}			

						}

					});

					builder.show();	



					return false;
				}
			});		





			int[] TextPos = {R.id.tChecking,R.id.tCompleted,R.id.tDone,R.id.tFailed,R.id.tKilled,R.id.tMatched,R.id.tReceived,R.id.tRunning,R.id.tStaging,R.id.tStalled,R.id.tWaiting};
			int All = 0;

			for(int i = 0; i<TextPos.length; i++){
				float F = 0;
				TextView T1 = (TextView)findViewById(TextPos[i]);
				T1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, F));
			}

			for(int i = 0; i<map.length; i++){
				float F = Float.parseFloat(map[i].number());	
				TextView T1 = (TextView)findViewById(TextPos[map[i].get(map[i].name())]);
				T1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, F));
				All = All + (int) F;
			}


			TextView Total = (TextView)findViewById(R.id.nbtotaljob);
			Total.setText("Jobs in Dirac: "+All);


			TextView LU = (TextView)findViewById(R.id.lastup);



			LU.setText(datasource.getLastUpdateTime());	
		}





	}


}
