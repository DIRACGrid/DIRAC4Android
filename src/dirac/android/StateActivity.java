package dirac.android;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class StateActivity extends Activity{


	Random r;


	private String itemSelected;
	private final Context context = this;
	private	final CharSequence[] jodActionFailed = {"Reschedule", "Delete", "Kill"};
	ArrayAdapter<String> adapter2;
	private OnItemLongClickListener listener;
	private JobsDataSource datasource;
	List<Job> myjobids;

	/** Ca	private CommentsDataSource datasource;
lled when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		datasource = new JobsDataSource(this);
		datasource.open();


		Bundle b = getIntent().getExtras();
		String state;
		JobArrayAdapter adapter;


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
		//T1.setBackgroundResource(R.color.completed);

		// Set the View layer
		setContentView(R.layout.mainstate);
		//	setTitle("TestIconizedListView");


		// Get reference to ListView holder
		ListView lv = (ListView) this.findViewById(R.id.STATELV);


		// Set the ListView adapter
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text

				itemSelected=parent.getItemAtPosition(position).toString();
				// Parse the inputstream
				Job myjobid = myjobids.get(position);

				Intent myIntent = new Intent(view.getContext(), JobActivity.class);			


				myIntent.putExtra("myJob", myjobid);
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




				// TODO Auto-generated method stub
				return false;
			}
		});		

	}











}
