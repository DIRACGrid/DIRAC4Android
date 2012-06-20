package dirac.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.app.AlertDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.achartengine.ChartFactory;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.ActionBar;
import com.google.gson.Gson;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;

public class DIRACAndroidActivity extends SherlockActivity implements ActionBar.OnNavigationListener {
	private static final int MENU_NEW_GAME = 0;
	private static final int PICK_CONTACT = 0;
	final String TAG = getClass().getName();
	private SharedPreferences prefs;
	ProgressDialog dialog;

	private static final int UpMenu1 = Menu.FIRST;
	private static final int Filt_Menu1 = Menu.FIRST+1;
	private static final int User_Menu1 = Menu.FIRST+2;
	private static final int Stat_Menu1 = Menu.FIRST+3;
	public static final String PREFS_NAME = "MyPrefsFile";
	Random r;
	private Intent StatsIntent;


	private String itemSelected;
	private final Context context = this;
	private	final CharSequence[] jodActionFailed = {"Reschedule", "Delete", "Kill"};
	ArrayAdapter<String> adapter2;	

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	private List<Job> countryList= new  ArrayList<Job>();

	private OnItemLongClickListener listener;
	private JobsDataSource datasource;

	/**when the activity is first created. */
    private String[] mLocations;

	private int myProgress;
	private int maxProgress  = 1100;
	private PerformAPICall apiCall;

    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getSupportMenuInflater();
    	inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
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
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.locations, R.layout.sherlock_spinner_item);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
        
        
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


		database.close();	
		datasource.close();

		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);


		//  performApiCall();





		String defValue = null;
		String test = CacheHelper.readString(context, CacheHelper.JOBNAME, defValue);
		if(test == null) {
			CacheHelper.writeString(this, CacheHelper.JOBNAME,"");		
			CacheHelper.writeBoolean(this, CacheHelper.USEIT,true);		
			CacheHelper.writeString(this, CacheHelper.TIME,"");		
			CacheHelper.writeString(this, CacheHelper.FTIME,"");		
			CacheHelper.writeString(this, CacheHelper.APPNAME,"");		
			CacheHelper.writeString(this, CacheHelper.SITE,"");	
		}else{

			String defValue1 = "";

			CacheHelper.writeString(this, CacheHelper.JOBNAME,"test");	
			CacheHelper.writeString(this, CacheHelper.SITE,"LCG.CERN.CH");	
			CacheHelper.writeString(this, CacheHelper.TIME,"test");	
			CacheHelper.writeString(this, CacheHelper.APPNAME,"DaVinci");	

		}

	}

    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
    	if (mLocations[itemPosition].compareTo("Jobs") == 0) {
    		return true;
    	} else if (mLocations[itemPosition].compareTo("Stats") == 0){
    		if (StatsIntent== null){
		//		performApiCallStats  task = new performApiCallStats();
		//		task.execute(new String[] {Constants.API_HISTORY});
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

				myProgress = 0;

				//			performApiCallStats  task = new performApiCallStats();
				//task.execute(new String[] { Constants.API_JOBS+"/groupby/status?maxJobs=20&status=Waiting,Done,Completed,Running,Staging,Stalled,Failed,Killed&flatten=true" });
				//			task.execute(new String[] { Constants.API_HISTORY});

				
//	            int progress = (Window.PROGRESS_END - Window.PROGRESS_START) / 100 * 20;
//	            setSupportProgress(progress);
	            
				String myStrings = "";

				for(int i = 0; i< map.length;i++){
					if(i < (map.length - 1))
						myStrings = myStrings+map[i].name()+",";
					else
						myStrings = myStrings+map[i].name()
						;

				}
				apiCall.performApiCall( Constants.API_JOBS+"/groupby/status?maxJobs=10&status="+myStrings+"&flatten=true&"+JobType, "");
				
				
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


	public Intent execute(Context context, String result){

		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		JSONObject jObject1;

		try {
			jObject1 = new JSONObject(result);
			JSONObject menuObject = jObject1.getJSONObject("data");
			renderer.setAxisTitleTextSize(30);
			renderer.setChartTitleTextSize(30);
			renderer.setLabelsTextSize(24);
			renderer.setLegendTextSize(40);
			renderer.setMargins(new int[] {70, 70, 70,70});
			renderer.setAxesColor(Color.DKGRAY);
			renderer.setLabelsColor(Color.LTGRAY);
			renderer.setAntialiasing(true);
			renderer.setShowGridX(true);
			//	ArrayList<String[]> list = datasource.getAllJobIDsOfSatusTime();
			//	double[] Range = {(double) (list.size()-10),(double) list.size()};
			//renderer.setRange(Range);

			String[] status = Status.PossibleStatus;
			int[] Colors = Status.ColorStatus;
			XYSeriesRenderer r;


			for (int i = 0; i < status.length - 1 ; i++) {

				JSONObject Status = null;
				try{
					Status = menuObject.getJSONObject(status[i]);
					Log.i("",status[i]);
				}catch (Exception e1) {
					continue;
				}


				JSONArray StatusN = Status.names();



				//	System.out.println(i);
				//	System.out.println(status[i]);
				//	System.out.println(Colors[i]);
				XYSeries series = new XYSeries("");
				series.setTitle(status[i]);
				r = new XYSeriesRenderer();
				r.setColor(context.getResources().getColor(Colors[i]));
				r.setLineWidth(4);
				renderer.addSeriesRenderer(r);

				for (int k = 0; k < StatusN.length(); k++) {

					String sdate = StatusN.getString(k);
					java.util.Date time=new java.util.Date(Long.parseLong(sdate)*1000);
					double  log10 = java.lang.Math.log10(Float.parseFloat((Status.getString(sdate))));

					series.add(time.getTime(), log10);					
				}	

				dataset.addSeries(series);
			}




		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		Intent intent = new Intent() ;//= ChartFactory.getTimeChartIntent(this,dataset, renderer, null);
		return intent;
	}



//	public boolean onOptionsItemSelected(MenuItem item) {
//		dialog = ProgressDialog.show(this, "",                         "Downloading/Loading. Please wait...", true);
//
//		Gson gson = new Gson();
//		String SSummary ;
//		StatusSummary summary;
//
//		apiCall = new PerformAPICall(context,prefs);
//		switch (item.getItemId()) {
//
//		case UpMenu1:
//			database = dbHelper.getWritableDatabase();
//			datasource.open();		
//			String SdefValue = "";
//			String JobType = CacheHelper.readString(context, CacheHelper.GETJOBSTYPE, SdefValue);
//			if (JobType == "")
//				SSummary = apiCall.performApiCall(Constants.API_SUMMARY);
//			else
//				SSummary = apiCall.performApiCall(Constants.API_SUMMARY+"?"+JobType);
//
//			summary = gson.fromJson(SSummary, StatusSummary.class);
//			datasource.parseSummary(summary);	
//			CacheHelper.writeBoolean(this, CacheHelper.GETJOBS,true);	
//			loadDataOnScreen();
//			database.close();		
//			datasource.close();
//			dialog.dismiss();
//			return true;
//		case Filt_Menu1:
//			Intent myIntent = new Intent(context, FilterSettingsActivity.class);				 
//			startActivity(myIntent);
//			dialog.dismiss();
//			return true;  
//		case User_Menu1:
//			Intent myIntent2 = new Intent(context, UserProfileActivity.class);				 
//			startActivity(myIntent2);
//			dialog.dismiss();
//
//			return true;  
//		case Stat_Menu1:
//
//			if (StatsIntent== null){
//
//				performApiCallStats  task = new performApiCallStats();
//				//task.execute(new String[] { Constants.API_JOBS+"/groupby/status?maxJobs=100&status=Waiting,Done,Completed,Running,Staging,Stalled,Failed,Killed&flatten=true" });
//				task.execute(new String[] { Constants.API_HISTORY});
//			}
//
//			return true;
//		}	
//
//		return false;
//	}

}
