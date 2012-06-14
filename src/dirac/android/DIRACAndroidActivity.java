package dirac.android;

import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
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
import com.google.gson.Gson;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;

public class DIRACAndroidActivity extends Activity{
	private static final int MENU_NEW_GAME = 0;
	private static final int PICK_CONTACT = 0;
	final String TAG = getClass().getName();
	private SharedPreferences prefs;

	private static final int MENU_QUIT = 0;

	private static final int UpMenu1 = Menu.FIRST;
	private static final int UpMenu2 = Menu.FIRST+1;
	private static final int Stat_Menu1 = Menu.FIRST+2;
	private static final int Stat_Menu3 = Menu.FIRST+3;
	private static final int Filt_Menu1 = Menu.FIRST+4;
	private static final int Filt_Menu2 = Menu.FIRST+5;
	private static final int Filt_Menu3 = Menu.FIRST+6;
	private static final int UPDATE_MENU = 0;
	private static final int STATS_MENU = 1;
	private static final int FILTER_MENU = 2;
	public static final String PREFS_NAME = "MyPrefsFile";
	Random r;
	public static final String DIRAC_REQUEST_TOKEN_URL = "http://lhcb01.ecm.ub.es:9345/oauth/request_token";
	public static final String DIRAC_ACCESS_TOKEN_URL  = "http://lhcb01.ecm.ub.es:9345/oauth/access_token";
	public static final String DIRAC_AUTHORIZE_URL     = "http://lhcb01.ecm.ub.es:9345/oauth/authorize";


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

    protected ProgressBar PBar;
	private int myProgress;
	private int maxProgress  = 1100;


	@Override	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		PBar = (ProgressBar)findViewById(R.id.progressBar1);
		PBar.setVisibility(0);

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

		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);


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

			if(test.booleanValue()){

				String[] status = Status.PossibleStatus;

				


				datasource.open();
				database = dbHelper.getWritableDatabase(); 
				dbHelper.deleteTable(database, dbHelper.DIRAC_JOBS);
				
		
				PBar.setMax(1100);

				PBar.setVisibility(1);
				PBar.setProgress(0);
				
				myProgress = 0;

					for(String s: status){
				performApiCall task = new performApiCall();
				//task.execute(new String[] { Constants.API_JOBS+"/groupby/status?maxJobs=100&status=Waiting,Done,Completed,Running,Staging,Stalled,Failed,Killed&flatten=true" });
				task.execute(new String[] { Constants.API_JOBS+"?maxJobs=100&status="+s+JobType });	
					}





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





















	public Intent execute(Context context, JobsDataSource datasource){





		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(10);
		renderer.setLegendTextSize(30);
		renderer.setMargins(new int[] {20, 30, 15, 0});
		renderer.setAxesColor(Color.DKGRAY);
		renderer.setLabelsColor(Color.LTGRAY);
		renderer.setAntialiasing(true);
		renderer.setShowGridX(true);
		ArrayList<String[]> list = datasource.getAllJobIDsOfSatusTime();
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		//	double[] Range = {(double) (list.size()-10),(double) list.size()};
		//renderer.setRange(Range);

		String[] status = Status.PossibleStatus;
		int[] Colors = Status.ColorStatus;
		XYSeriesRenderer r;


		for (int i = 0; i < list.get(0).length - 1 ; i++) {
		//	System.out.println(i);
		//	System.out.println(status[i]);
		//	System.out.println(Colors[i]);
			XYSeries series = new XYSeries("");
			series.setTitle(status[i]);
			r = new XYSeriesRenderer();
			r.setColor(context.getResources().getColor(Colors[i]));
			r.setLineWidth(4);
			renderer.addSeriesRenderer(r);

			for (int k = 0; k < list.size(); k++) {

				String sdate = list.get(k)[list.get(0).length-1];
			//	System.out.println(sdate);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				Date d1 =null;
				try {
					d1 = dateFormat.parse(sdate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				series.add(d1.getTime(), Double.valueOf(list.get(k)[i]));					
			}	

			dataset.addSeries(series);
		}
		Intent intent = ChartFactory.getTimeChartIntent(this,dataset, renderer, null);

		return intent;
	}



	public boolean onCreateOptionsMenu(Menu menu){



		SubMenu UpdateMenu = menu.addSubMenu("Update");
		SubMenu fileMenu = menu.addSubMenu("Stats");
		SubMenu editMenu = menu.addSubMenu("Filters");
		UpdateMenu.add(UPDATE_MENU,UpMenu1,0,"Update All");
		UpdateMenu.add(UPDATE_MENU,UpMenu2,1,"Update Mine");
		fileMenu.add(STATS_MENU,Stat_Menu1,0,"Stats");
		fileMenu.add(STATS_MENU,Stat_Menu3,1,"Delete Stats");
		editMenu.add(FILTER_MENU,Filt_Menu1,0,"Add Filter");
		editMenu.add(FILTER_MENU,Filt_Menu2,1,"Apply Filter");
		editMenu.add(FILTER_MENU,Filt_Menu3,3,"Remove Filter");


		return true;

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Gson gson = new Gson();
		String SSummary ;
		StatusSummary summary;
		
		switch (item.getItemId()) {

		case UpMenu1:
			database = dbHelper.getWritableDatabase();
			datasource.open();				
			 SSummary = performApiCall(Constants.API_SUMMARY+"?allOwners=true");
			 summary = gson.fromJson(SSummary, StatusSummary.class);
			datasource.parseSummary(summary);	
			CacheHelper.writeBoolean(this, CacheHelper.GETJOBS,true);	
			CacheHelper.writeString(this, CacheHelper.GETJOBSTYPE,"&allOwners=true");
			loadDataOnScreen();
			database.close();		
			datasource.close();
			return true;
		case UpMenu2:
			database = dbHelper.getWritableDatabase();
			datasource.open();	
			SSummary = performApiCall(Constants.API_SUMMARY);
			summary = gson.fromJson(SSummary, StatusSummary.class);
			datasource.parseSummary(summary);	
			CacheHelper.writeBoolean(this, CacheHelper.GETJOBS,true);
			CacheHelper.writeString(this, CacheHelper.GETJOBSTYPE,"");	
			loadDataOnScreen();
			database.close();		
			datasource.close();
			return true;
		case Stat_Menu1:
			datasource.open();	
			startActivity(execute(context, datasource));
			datasource.close();
			return true;
		case Stat_Menu3:
			database = dbHelper.getWritableDatabase();
			datasource.open();	
			dbHelper.deleteStat(database, dbHelper.DIRAC_STATS);
			database.close();		
			datasource.close();
			return true;   
		case Filt_Menu1:
			Toast.makeText(context, "add filter", Toast.LENGTH_SHORT).show();
			Intent myIntent = new Intent(context, FilterSettingsActivity.class);				 
			startActivity(myIntent);
			return true;    
		case Filt_Menu2:
			Toast.makeText(context, "filter applied", Toast.LENGTH_SHORT).show();
			return true;     
		case Filt_Menu3:
			Toast.makeText(context, "filter removed", Toast.LENGTH_SHORT).show();
			return true;                   

		}
		return false;
	}



	public class performApiCall extends AsyncTask<String, Integer, String> {
		
	

		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {

				try {
					response = doGet(url,getConsumer(prefs));

					myProgress += 10;
		            publishProgress(myProgress);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return response;

		}

		protected void onProgressUpdate(Integer... progress) {
			// setProgressPercent(progress[0]);
			PBar.setProgress(progress[0]);

		}

		protected void onPostExecute(String result) {

			datasource.open();
			database = dbHelper.getWritableDatabase(); 
			Gson gson = new Gson();

			Jobs  jobs = gson.fromJson(result, Jobs.class);
			datasource.parse(jobs);	
			database.close();	

			myProgress+=90;
            publishProgress(myProgress);


		}
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





	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case (PICK_CONTACT) :
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c =  managedQuery(contactData, null, null, null, null);
				if (c.moveToFirst()) {
					//	          String name = c.getString(c.getColumnIndexOrThrow(People.NAME));
					//         Log.i(TAG,"Response : " + "Selected contact : " + name);
				}
			}
		break;
		}
	}	

	private void clearCredentials() {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor edit = prefs.edit();
		edit.remove(OAuth.OAUTH_TOKEN);
		edit.remove(OAuth.OAUTH_TOKEN_SECRET);
		edit.commit();
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
