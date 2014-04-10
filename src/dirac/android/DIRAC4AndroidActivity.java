package dirac.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.ActionBar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;

public class DIRAC4AndroidActivity extends SherlockActivity implements
		ActionBar.OnNavigationListener {
	final String TAG = getClass().getName();

    private String BASE_URL;
	
	public static final String PREFS_NAME = "MyPrefsFile";
	Random r;
	private Intent StatsIntent;

//	private String itemSelected;
	public final Context context = this;
	//private final CharSequence[] jodActionFailed = { "Reschedule", "Delete","Kill" };
	ArrayAdapter<String> adapter2;

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private Connectivity connect;
	private JobsDataSource datasource;

	/** when the activity is first created. */
	private String[] mLocations;

	private PerformAPICall2 apiCall;

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		// Gson gson = new Gson();
		// String SSummary = "" ;
		// StatusSummary summary;
		TextView UserTV = (TextView) findViewById(R.id.userText1);

		switch (item.getItemId()) {
		case R.id.menu_mine:
			CacheHelper.writeString(this, CacheHelper.GETJOBSTYPE, "");
			UserTV.setText("Next Update: My Jobs");

			return true;
		case R.id.menu_all:
			CacheHelper.writeString(this, CacheHelper.GETJOBSTYPE,
					"allOwners=true");
			UserTV.setText("Next Update: All Onwers");

			return true;
		case R.id.menu_refresh:

			if (connect.isOnline()) {
				
				BASE_URL = "https://"+CacheHelper.readString(context, CacheHelper.DIRACSERVER, "");

				datasource.open();
				database = dbHelper.getWritableDatabase();

				// now done in PerformAPICall2
				// dbHelper.deleteTable(database, MySQLiteHelper.DIRAC_JOBS);

				String SdefValue = "";
				String JobType = CacheHelper.readString(context,
						CacheHelper.GETJOBSTYPE, SdefValue);

				final Status[] map = datasource.getLastUpdate();

				StateInfoArrayAdapter adapter = new StateInfoArrayAdapter(
						this.getApplicationContext(), R.layout.liststatus, map);

				// Get reference to ListView holder
				ListView lv = (ListView) this.findViewById(R.id.states);
				View footer = getLayoutInflater().inflate(R.layout.list_footer,
						null);

				TextView footerTV = (TextView) footer
						.findViewById(R.id.footer_text);

				if (CacheHelper.readBoolean(context,
						CacheHelper.SITESUMMARYBOOL, false))
					footerTV.setText("see per status <-> sties");
				else
					footerTV.setText("see per sites  <->  status");

				if (lv.getFooterViewsCount() == 0) {

					lv.addFooterView(footer);
				}

				apiCall.SetLV(lv, adapter);
				Activity activityContext = (Activity) this.context;
				apiCall.SetActivity(activityContext);
				Integer mymax = 1;
				mymax = CacheHelper.readInteger(getBaseContext(),
						CacheHelper.NMAXBJOBS, mymax);
				if (JobType == "") {
					apiCall.getSummaryandJobs(
							BASE_URL+Constants.REQUEST_SUMMARY,
							BASE_URL+Constants.REQUEST_SUMMARY + "?group=Site",
							BASE_URL+Constants.REQUEST_JOBS + "?maxJobs="
									+ mymax.toString());
				} else {
					apiCall.getSummaryandJobs(BASE_URL+Constants.REQUEST_SUMMARY + "?"
							+ JobType, BASE_URL+Constants.REQUEST_SUMMARY + "?"
							+ JobType + "&group=Site", BASE_URL+Constants.REQUEST_JOBS
							+ "?maxJobs=" + mymax.toString() + "&" + JobType);

				}

				// SSummary =
				// apiCall.performApiCall(Constants.API_SUMMARY+"?lastUpdateTime=360&"+JobType);

				// summary = gson.fromJson(SSummary, StatusSummary.class);

				// datasource.parseSummary(summary);

				CacheHelper.writeBoolean(this, CacheHelper.GETJOBS, true);

				// datasource.close();
				// database.close();

				// loadDataOnScreen();
			}
			return true;

		case R.id.manage_certs:
			startActivity(new Intent(context, UserProfileActivity.class));
			return true;

		case R.id.prefs:
			startActivity(new Intent(context, FilterSettingsActivity.class));
			return true;

		case R.id.history:
			BASE_URL = "https://"+CacheHelper.readString(context, CacheHelper.DIRACSERVER, "");
			apiCall = new PerformAPICall2(context);
	     	apiCall.getHistory(BASE_URL+Constants.REQUEST_HISTORY);
	     	return true;
	    	
	     	
		case R.id.about:
			startActivity(new Intent(context, AboutActivity.class));
			return true;
			


	
		

		}

		return false;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ActionBarSherlock
		// getSupportActionBar().setDisplayShowHomeEnabled(false);
		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.main);
		
		
		getSupportActionBar().setDisplayShowTitleEnabled(false);

	//TODO this need to be fixed	
		/*
		mLocations = getResources().getStringArray(R.array.locations);
		Context context = getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(
				context, R.array.locations, R.layout.spinner_item);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(list, this);
*/
		connect = new Connectivity(context);

		datasource = new JobsDataSource(this);
		datasource.open();
		dbHelper = new MySQLiteHelper(context);
		database = dbHelper.getWritableDatabase();

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		Boolean defValueb = false;
		CacheHelper.writeBoolean(context, CacheHelper.GETJOBS, defValueb);
		CacheHelper.writeString(context, CacheHelper.GETJOBSTYPE, "");

		// dbHelper.deleteTable(database, MySQLiteHelper.DIRAC_JOBS);

		database.close();
		datasource.close();

		try {
			loadDataOnScreen();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		apiCall = new PerformAPICall2(context);

		// loadDataOnScreen();

	}

	@SuppressWarnings("unused")
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {

		String SdefValue = "";
		String JobType = CacheHelper.readString(context,
				CacheHelper.GETJOBSTYPE, SdefValue);
		if (mLocations[itemPosition].compareTo("Jobs") == 0) {
			return true;
		} else if (mLocations[itemPosition].compareTo("History") == 0) {
			if (StatsIntent == null) {
				if (connect.isOnline())

					// Toast.makeText(context,
					// "Not available yet",Toast.LENGTH_LONG).show();
					// apiCall.getHistory(Constants.REQUEST_HISTORY+"?"+JobType)
					// ;
					BASE_URL = "https://"+CacheHelper.readString(context, CacheHelper.DIRACSERVER, "");

					
					apiCall.getHistory(BASE_URL+Constants.REQUEST_HISTORY);
			}
		}
		return true;
	}

	@SuppressLint("SimpleDateFormat")
	public void loadDataOnScreen() throws JSONException {

		ListView lv = (ListView) this.findViewById(R.id.states);
		// //// Create a customized ArrayAdapter
		datasource.open();
		database = dbHelper.getWritableDatabase();

		// final Status[] map = datasource.getLastUpdate2();

		Status[] map1 = datasource.getLastUpdate();
		Status[] map2 = map1;

		if (map1.length > 0) {
			if (map1[0] != null) {

				if (CacheHelper.readBoolean(context,
						CacheHelper.SITESUMMARYBOOL, false)) {

					JSONObject jObject = new JSONObject(CacheHelper.readString(
							context, CacheHelper.SITESUMMARY, ""));

					JSONArray jObjectN = jObject.names();

					map1 = new dirac.android.Status[jObjectN.length()];

					for (int k = 0; k < jObjectN.length(); k++) {

						String[] temp = new String[2];

						String tmp = jObjectN.getString(k);
						//String output = deCamelCasealize(tmp);
						temp[0] = tmp;
						temp[1] = jObject.getString(tmp);

						dirac.android.Status state = new dirac.android.Status(
								tmp, jObject.getString(tmp));
						map1[k] = state;
					}
				}

				final Status[] map = map1;
				StateInfoArrayAdapter adapter = new StateInfoArrayAdapter(
						this.getApplicationContext(), R.layout.liststatus, map);

				// Get reference to ListView holder
				View footer = getLayoutInflater().inflate(R.layout.list_footer,
						null);
				TextView footerTV = (TextView) footer
						.findViewById(R.id.footer_text);

				if (CacheHelper.readBoolean(context,
						CacheHelper.SITESUMMARYBOOL, false))
					footerTV.setText("see per status <-> sties");
				else
					footerTV.setText("see per sites  <->  status");

				if (lv.getFooterViewsCount() == 0) {

					lv.addFooterView(footer);
				} else {
					lv.removeFooterView(footer);
					lv.addFooterView(footer);
					lv.removeFooterView(footer);
				}
				// Set the ListView adapter
				lv.setAdapter(adapter);

				// database = dbHelper.getWritableDatabase();
				// datasource.open();
				// Boolean defValue = false;
				// Boolean test = CacheHelper.readBoolean(context,
				// CacheHelper.GETJOBS, defValue);
				String SdefValue = "";
				String JobType = CacheHelper.readString(context,
						CacheHelper.GETJOBSTYPE, SdefValue);
				TextView UserTV = (TextView) findViewById(R.id.userText1);

				if (JobType == "") {
					UserTV.setText("My last Jobs");
				} else {
					UserTV.setText("All Owners Jobs");

				}
	

			
				int[] TextPos = { R.id.tRunning, R.id.tWaiting,
						R.id.tCompleted, R.id.tDone, R.id.tFailed,
						R.id.tMatched, R.id.tReceived, R.id.tChecking,
						R.id.tStaging, R.id.tStalled, R.id.tKilled,
						R.id.tDeleted };

				int All = 0;

				for (int i = 0; i < TextPos.length; i++) {
					float F = 0;
					TextView T1 = (TextView) findViewById(TextPos[i]);
					T1.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT, F));
				}
				Integer totStatus = map2.length;

				for (int i = 0; i < totStatus; i++) {
					float F = Float.parseFloat(map2[i].number());

					TextView T1 = (TextView) findViewById(TextPos[map2[i]
							.get(map2[i].name())]);
					T1.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT, F));
					All = All + (int) F;
				}

				TextView Total = (TextView) findViewById(R.id.nbtotaljob);
				Total.setText("Jobs in Dirac: "
						+ CacheHelper.readString(context,
								CacheHelper.ALLENTRIES, "none"));

				TextView LU = (TextView) findViewById(R.id.lastup);

				String testtime = "never";
				Long exptime = CacheHelper.readLong(context,
						CacheHelper.LASTUPDATE, -1);
				if (exptime != -1) {
					Long ltime = exptime;
					SimpleDateFormat sdf = new SimpleDateFormat(
							"HH:mm:ss - dd/MM/yyyy");
					testtime = sdf.format(ltime);
				}

				LU.setText(testtime);
			}else{
				
				TextView LU = (TextView) findViewById(R.id.lastup);
				String testtime = "never" ;
				LU.setText(testtime);

				Status test = new Status("never","updated");
				Status[] mapr =  new Status[1];
				mapr[0] = test; 
				StateInfoArrayAdapter adapter = new StateInfoArrayAdapter(
						this.getApplicationContext(), R.layout.liststatus, mapr);
				lv.setAdapter(adapter);
				
				
				lv.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
					}
				});
				
				
				
				
				
				
				
				
				
				
			}
		}else{
			
			TextView LU = (TextView) findViewById(R.id.lastup);
			String testtime = "never" ;
			LU.setText(testtime);

			Status test = new Status("never","updated");
			Status[] mapr =  new Status[1];
			mapr[0] = test; 
			StateInfoArrayAdapter adapter = new StateInfoArrayAdapter(
					this.getApplicationContext(), R.layout.liststatus, mapr);
			lv.setAdapter(adapter);
			
		}
		
		
		
		
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				datasource.open();
				Status[] map1 = datasource.getLastUpdate();
				datasource.close();
				

				if (map1[0] == null){
					
					
					
				}else{
				
				
				if (CacheHelper.readBoolean(context,
						CacheHelper.SITESUMMARYBOOL, false)) {

					JSONObject jObject = null;
					try {
						jObject = new JSONObject(CacheHelper
								.readString(context,
										CacheHelper.SITESUMMARY, ""));

						JSONArray jObjectN = jObject.names();

						map1 = new dirac.android.Status[jObjectN
								.length()];

						for (int k = 0; k < jObjectN.length(); k++) {

							String[] temp = new String[2];

							String tmp = jObjectN.getString(k);
							String output = deCamelCasealize(tmp);
							temp[0] = output;
							temp[1] = jObject.getString(tmp);

							dirac.android.Status state = new dirac.android.Status(
									output, jObject.getString(tmp));
							map1[k] = state;
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				if (position == map1.length) {

					if (CacheHelper.readBoolean(context,
							CacheHelper.SITESUMMARYBOOL, false))
						CacheHelper.writeBoolean(context,
								CacheHelper.SITESUMMARYBOOL, false);
					else
						CacheHelper.writeBoolean(context,
								CacheHelper.SITESUMMARYBOOL, true);

					try {
						loadDataOnScreen();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {

					// When clicked, show a toast with the TextView text
					Intent myIntent = new Intent(view.getContext(),
							StateActivity.class);
					myIntent.putExtra("myState", ((Status) parent
							.getItemAtPosition(position)).name());
					startActivity(myIntent);
				}
			}
			}
		});
/*
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent,
					View arg1, final int position2, long arg3) {

				itemSelected = ((Status) parent
						.getItemAtPosition(position2)).name();
				// parent.getItemAtPosition(position2).toString();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						context);
				builder.setTitle("Select an Action for the batch of job with the state: "
						+ itemSelected);
				builder.setItems(jodActionFailed,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int item) {
								AlertDialog.Builder builder2 = new AlertDialog.Builder(
										context);
								builder2.setMessage("Do you really want to "
										+ jodActionFailed[item]
										+ " jobID: "
										+ itemSelected
										+ " ?");
								builder2.setCancelable(true);
								// JobID myjobid =
								// jobList.get(position2);

								if (item == 0) {
									builder2.setPositiveButton(
											"Yes",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {
													// adapter.remove(itemSelected);
													// adapter.notifyDataSetChanged();

													Toast.makeText(
															getApplicationContext(),
															"hola",
															Toast.LENGTH_SHORT)
															.show();
												}
											});
									builder2.setNegativeButton(
											"Nooo",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.cancel();
												}
											});
									builder2.show();
								} else if (item == 1) {
									Toast.makeText(
											getApplicationContext(),
											jodActionFailed[item],
											Toast.LENGTH_SHORT).show();
								}

								else if (item == 2) {
									Toast.makeText(
											getApplicationContext(),
											jodActionFailed[item],
											Toast.LENGTH_SHORT).show();
								}

							}

						});

				builder.show();

				return false;
			}
		});
*/
		
		
		
		
		
		
		
		
		
		

		database.close();
		datasource.close();

		/*
		 * if(test.booleanValue()){ Log.i(TAG,"here9");
		 * 
		 * datasource.open(); database = dbHelper.getWritableDatabase();
		 * 
		 * dbHelper.deleteTable(database, MySQLiteHelper.DIRAC_JOBS);
		 * 
		 * database.close(); datasource.close(); setSupportProgress(0);
		 * 
		 * 
		 * String myStrings = "";
		 * 
		 * for(int i = 0; i< map.length;i++){ if(i < (map.length - 1)) myStrings
		 * = myStrings+map[i].name()+","; else myStrings =
		 * myStrings+map[i].name() ;
		 * 
		 * } //dialog = ProgressDialog.show(context, "",
		 * "Downloading/Loading. Please wait...", true);
		 * 
		 * //apiCall.SetProgressDialog(dialog);
		 * 
		 * 
		 * Integer mymax = 10; mymax =
		 * CacheHelper.readInteger(getApplicationContext(),
		 * CacheHelper.NMAXBJOBS, mymax); if(connect.isOnline()) if
		 * (connect.isGranted()) apiCall.performApiCall(
		 * Constants.API_JOBS+"/groupby/status?lastUpdate=36&maxJobs="
		 * +mymax.toString()+"&status="+myStrings+"&flatten=true&"+JobType, "");
		 * 
		 * 
		 * }
		 * 
		 * CacheHelper.writeBoolean(this, CacheHelper.GETJOBS,false);
		 */

	}

	public static String deCamelCasealize(String camelCasedString) {
		if (camelCasedString == null || camelCasedString.isEmpty())
			return camelCasedString;

		StringBuilder result = new StringBuilder();
		result.append(camelCasedString.charAt(0));
		for (int i = 1; i < camelCasedString.length(); i++) {

			if (i + 1 < camelCasedString.length()) {
				if (Character.isUpperCase(camelCasedString.charAt(i))
						&& Character
								.isLowerCase(camelCasedString.charAt(i + 1))) {
					result.append(" ");
				} else if (Character.isUpperCase(camelCasedString.charAt(i))
						&& Character
								.isUpperCase(camelCasedString.charAt(i + 1))
						&& Character
								.isLowerCase(camelCasedString.charAt(i - 1))) {
					result.append(" ");
				}
			}
			result.append(camelCasedString.charAt(i));
		}
		return result.toString();
	}

}
