package dirac.android;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
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

import java.io.InputStream;
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


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;

public class DIRACAndroidActivity extends Activity{
	/** Called when the activity is first created. */

	Random r;


	private String itemSelected;
	private final Context context = this;
	private	final CharSequence[] jodActionFailed = {"Reschedule", "Delete", "Kill"};
	ArrayAdapter<String> adapter2;
	
	private String[] status;

 

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;



	private List<Job> countryList= new  ArrayList<Job>();
   
	private OnItemLongClickListener listener;
	private JobsDataSource datasource;

	/**when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		datasource = new JobsDataSource(this);
		datasource.open();
		dbHelper = new MySQLiteHelper(context);
		database = dbHelper.getWritableDatabase(); 
		setContentView(R.layout.main);

		
		
		
		loadDataOnScreen();


		Button NewB = (Button)findViewById(R.id.button1);

		NewB.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {


				database = dbHelper.getWritableDatabase();
                datasource.open();	

				EditText NumB = (EditText)findViewById(R.id.numofjob);
				dbHelper.deleteTable(database, dbHelper.DIRAC_JOBS);
				InputStream inputStream = getResources().openRawResource(R.raw.jobs2);
				datasource.parse(inputStream,Integer.parseInt(NumB.getText().toString()));		
				datasource.creatTableOfSatus();
				loadDataOnScreen();

				database.close();		
				datasource.close();


			}
		});
		Button Stats = (Button)findViewById(R.id.button2);

		Stats.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

                datasource.open();	
				startActivity(execute(context, datasource));
				datasource.close();

			}
		});

		

database.close();	
datasource.close();
		

	}


	
	
	
	public void loadDataOnScreen(){


		////// Create a customized ArrayAdapter
		
		final Status[] map = datasource.getLastUpdate(); 
		if(map[0]!=null){
			
		StateInfoArrayAdapter adapter = new StateInfoArrayAdapter(
				this.getApplicationContext(), R.layout.listitem, map);

		// Get reference to ListView holder
		ListView lv = (ListView) this.findViewById(R.id.states);

		// Set the ListView adapter
		lv.setAdapter(adapter);
		
		
		
		Status s = new Status();
		status = s.PossibleStatus;
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				Intent myIntent = new Intent(view.getContext(), StateActivity.class);				 
				myIntent.putExtra("myState", map[position].name());
				startActivity(myIntent);
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View arg1,
					final int position2, long arg3) {

				itemSelected=parent.getItemAtPosition(position2).toString();
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Select an Action for jobID "+itemSelected);
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

		Status s = new Status();
		String[] status = s.PossibleStatus;
		int[] Colors = s.ColorStatus;
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












}
