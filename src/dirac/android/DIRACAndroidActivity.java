package dirac.android;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;

public class DIRACAndroidActivity extends Activity{
	private static final int MENU_NEW_GAME = 0;


	private static final int MENU_QUIT = 0;

private static final int Stat_Menu1 = Menu.FIRST;
private static final int Stat_Menu2 = Menu.FIRST+1;
private static final int Stat_Menu3 = Menu.FIRST+2;
private static final int Filt_Menu1 = Menu.FIRST+3;
private static final int Filt_Menu2 = Menu.FIRST+4;
private static final int Filt_Menu3 = Menu.FIRST+5;
private static final int STATS_MENU = 0;
private static final int FILTER_MENU = 1;
public static final String PREFS_NAME = "MyPrefsFile";

	Random r;


	private String itemSelected;
	private final Context context = this;
	private	final CharSequence[] jodActionFailed = {"Reschedule", "Delete", "Kill"};
	ArrayAdapter<String> adapter2;


	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public InputStream getJSONData(String url){

		DefaultHttpClient httpClient = new DefaultHttpClient();
		URI uri;
		InputStream data = null;
		try {
			uri = new URI(url);
			HttpGet method = new HttpGet(uri);
			HttpResponse response = httpClient.execute(method);
			data = response.getEntity().getContent();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	public void runJSONParser(){
		try{
			Log.i("MY INFO", "Json Parser started..");
			Gson gson = new Gson();
			Reader r = new InputStreamReader(getJSONData("https://api.twitter.com/1/trends/1.json"));
			Log.i("MY INFO", r.toString());
			TwitterTrends[] objs = gson.fromJson(r, TwitterTrends[].class);
			Log.i("MY INFO", ""+objs[0].getTrends().size());
			for(TwitterTrend tr : objs[0].getTrends()){
				Log.i("TRENDS", tr.getName() + " - " + tr.getUrl());
			} 
			for(location tr : objs[0].getLocations()){
				Log.i("TRENDS", tr.getName() + " - " + tr.getWoeid());
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}















	private List<Job> countryList= new  ArrayList<Job>();

	private OnItemLongClickListener listener;
	private JobsDataSource datasource;

	/**when the activity is first created. */
	@SuppressWarnings({ "null", "unused" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		datasource = new JobsDataSource(this);
		datasource.open();
		dbHelper = new MySQLiteHelper(context);
		database = dbHelper.getWritableDatabase(); 
		setContentView(R.layout.main);



		loadDataOnScreen();


		database.close();	
		datasource.close();
		
		

		
		
		String defValue = null;
		String test = CacheHelper.readString(context, CacheHelper.JOBNAME, defValue);
		if(test == null) {
			CacheHelper.writeString(this, CacheHelper.JOBNAME,"");		
			CacheHelper.writeBoolean(this, CacheHelper.USEIT,true);		
			CacheHelper.writeString(this, CacheHelper.TIME,"");		
			CacheHelper.writeString(this, CacheHelper.FTIME,"");		
			CacheHelper.writeString(this, CacheHelper.APPNAME,"");		
			CacheHelper.writeString(this, CacheHelper.SITE,"");	
			Log.d("main", "yeah");
		}else{
			
			String defValue1 = "";

			Log.d("main", CacheHelper.readString(this, CacheHelper.JOBNAME,defValue1));
			Log.d("main", CacheHelper.readString(this, CacheHelper.SITE,defValue1));
			Log.d("main", CacheHelper.readString(this, CacheHelper.TIME,defValue1));
			Log.d("main", CacheHelper.readString(this, CacheHelper.APPNAME,defValue1));

			CacheHelper.writeString(this, CacheHelper.JOBNAME,"test");	
			CacheHelper.writeString(this, CacheHelper.SITE,"LCG.CERN.CH");	
			CacheHelper.writeString(this, CacheHelper.TIME,"test");	
			CacheHelper.writeString(this, CacheHelper.APPNAME,"DaVinci");	
			
		}
	}





	public void loadDataOnScreen(){

		runJSONParser();
		////// Create a customized ArrayAdapter

		final Status[] map = datasource.getLastUpdate(); 
		if(map[0]!=null){

			StateInfoArrayAdapter adapter = new StateInfoArrayAdapter(
					this.getApplicationContext(), R.layout.liststatus, map);

			// Get reference to ListView holder
			ListView lv = (ListView) this.findViewById(R.id.states);

			// Set the ListView adapter
			lv.setAdapter(adapter);



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


		}
		TextView LU = (TextView)findViewById(R.id.lastup);
		LU.setText(datasource.getLastUpdateTime());	


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
		final int nr = 10;
		//	double[] Range = {(double) (list.size()-10),(double) list.size()};
		//renderer.setRange(Range);

		String[] status = Status.PossibleStatus;
		int[] Colors = Status.ColorStatus;
		XYSeriesRenderer r;


		for (int i = 0; i < list.get(0).length - 1 ; i++) {
			System.out.println(i);
			System.out.println(status[i]);
			System.out.println(Colors[i]);
			XYSeries series = new XYSeries("");
			series.setTitle(status[i]);
			r = new XYSeriesRenderer();
			r.setColor(context.getResources().getColor(Colors[i]));
			r.setLineWidth(4);
			renderer.addSeriesRenderer(r);

			for (int k = 0; k < list.size(); k++) {

				String sdate = list.get(k)[list.get(0).length-1];
				System.out.println(sdate);
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


		SubMenu fileMenu = menu.addSubMenu("Stats");
		SubMenu editMenu = menu.addSubMenu("Filters");
		
		fileMenu.add(STATS_MENU,Stat_Menu1,0,"Stats");
		fileMenu.add(STATS_MENU,Stat_Menu2,1,"Add Dummy data");
		fileMenu.add(STATS_MENU,Stat_Menu3,2,"Delete Stats");
		editMenu.add(FILTER_MENU,Filt_Menu1,0,"Add Filter");
		editMenu.add(FILTER_MENU,Filt_Menu2,1,"Apply Filter");
		editMenu.add(FILTER_MENU,Filt_Menu3,3,"Remove Filter");


		return true;

		}

	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case Stat_Menu1:
	    	datasource.open();	
			startActivity(execute(context, datasource));
			datasource.close();
	        return true;
	    case Stat_Menu2:
			database = dbHelper.getWritableDatabase();
			datasource.open();	

     		int nextInt2 = new Random().nextInt(3000);

			dbHelper.deleteTable(database, dbHelper.DIRAC_JOBS);
			InputStream inputStream = getResources().openRawResource(R.raw.jobs2);
			datasource.parse(inputStream,nextInt2);		
			datasource.creatTableOfSatus();
			loadDataOnScreen();
			database.close();		
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






}
