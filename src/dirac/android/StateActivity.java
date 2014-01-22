package dirac.android;

import java.util.List;
import java.util.Random;

import dirac.gsonconfig.Job;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StateActivity extends Activity{


    Random r;
    private 	JobArrayAdapter adapter;
    private View footer; 
    private ListView lv ;
    private final Context context = this;
    private	final CharSequence[] jodActionFailed = {"Delete", "Kill"};
    ArrayAdapter<String> adapter2;
    private JobsDataSource datasource;
    List<Job> myjobids;
    private String state;
    private Job selectJob;	
    Integer mymax = 10;
    protected ProgressBar PBar;
    private PerformAPICall2 apiCall;
    final String TAG = getClass().getName();
    private Connectivity connect;


    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);


	connect = new Connectivity(context);

	datasource = new JobsDataSource(this);
	datasource.open();


	Bundle b = getIntent().getExtras();

  //  Integer totNn = 0;
	if(b!=null){
	    state = b.getString("myState");
	    
	    if(CacheHelper.readBoolean(context, CacheHelper.SITESUMMARYBOOL, false))
		    myjobids = datasource.getAllJobIDsOfSites(state);
    	else   		
    	    myjobids = datasource.getAllJobIDsOfStatus(state);
	    

		final Status[] map = datasource.getLastUpdate(); 
		

		Integer totStatus = map.length;

		
	    
	    adapter = new JobArrayAdapter(
					  getApplicationContext(), R.layout.listjob, myjobids);	
	}else{		
	    myjobids = datasource.getAllJobIDs();

	    adapter = new JobArrayAdapter(
					  getApplicationContext(), R.layout.listjob, myjobids);

	}

	datasource.close();
	
	

	CacheHelper.writeInteger(context, CacheHelper.STARTJOBNB, myjobids.size());


	//T1.setBackgroundResource(R.color.completed);

	// Set the View layer
	setContentView(R.layout.mainstate);
   setTitle("dirac > "+state);


	// Get reference to ListView holder
	lv = (ListView) this.findViewById(R.id.STATELV);


	footer = getLayoutInflater().inflate(R.layout.list_footer, null);

	TextView footerTV = (TextView)footer.findViewById(R.id.footer_text);
	footerTV.setText("Download more"); 
	mymax = CacheHelper.readInteger(getApplicationContext(), CacheHelper.NMAXBJOBS, mymax);	

//	Log.i(state,totNn.toString());
//	if(myjobids.size()!= totNn)
	    lv.addFooterView(footer);

	// Set the ListView adapter
	lv.setAdapter(adapter);
	apiCall = new PerformAPICall2(context);
	apiCall.SetLV(lv, adapter, footer);
	lv.setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {


		    // When clicked, show a toast with the TextView text
		    if(position != myjobids.size()){
			// Parse the inputstream
			Job myjobid = myjobids.get(position);

			Intent myIntent = new Intent(view.getContext(), JobActivity.class);			


			myIntent.putExtra("jid", myjobid.getJid());
			startActivity(myIntent);	
		    }else{
			if(connect.isOnline()){
			    if(connect.isGranted()){
							
							


				String SdefValue = "";
				String JobType = CacheHelper.readString(context, CacheHelper.GETJOBSTYPE, SdefValue);
				Integer defInt	= 20;
				Integer nbJob = CacheHelper.readInteger(context, CacheHelper.STARTJOBNB, defInt);
				
				Log.i("addnew", JobType);
				if(CacheHelper.readBoolean(context, CacheHelper.SITESUMMARYBOOL, false))
			    	apiCall.getNewJobs(Constants.REQUEST_JOBS+"?site="+state+"&startJob="+nbJob.toString()+"&maxJobs="+mymax.toString()+"&"+JobType);
			    else   		
			    	apiCall.getNewJobs(Constants.REQUEST_JOBS+"?status="+state+"&startJob="+nbJob.toString()+"&maxJobs="+mymax.toString()+"&"+JobType);

							
				CacheHelper.writeInteger(context, CacheHelper.STARTJOBNB, (nbJob+mymax));

			    }

			}
		    }

		}
	    });
	
	
	
/*
	lv.setOnItemLongClickListener(new OnItemLongClickListener() {

		public boolean onItemLongClick(AdapterView<?> parent, View arg1,
					       final int position2, long arg3) {
		    if(position2 != myjobids.size()){
			selectJob = myjobids.get(position2);

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Select an Action for jobID "+selectJob.getJid());
			builder.setItems(jodActionFailed, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

				    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
				    builder2.setMessage("Do you really want to "+jodActionFailed[item]+" jobID: "+selectJob.getJid()+" ?");
				    builder2.setCancelable(true);
				    //	JobID myjobid = jobList.get(position2);

				    switch(item){
					
					//  case 0: 								
///					  builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {								
					  //public void onClick(DialogInterface dialog, int which) {	

					  //if(connect.isOnline()){
					  //apiCall.performApiCall(Constants.API_RESCHEDULE+selectJob.getJid());
					  //Toast.makeText(getApplicationContext(), "Rescheduled", Toast.LENGTH_SHORT).show();
					  //}else
					  //Toast.makeText(context, "no internet connectivity", Toast.LENGTH_SHORT).show();
					  //}
					  //});
					  ///builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
//					  public void onClick(DialogInterface dialog, int which) {
	//				  Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
//
	//				  dialog.cancel();
		//			  }
			//		  });
				//	  builder2.show();
					
									    case 0:
					builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {								
						public void onClick(DialogInterface dialog, int which) {	

						    if(connect.isOnline()){
							apiCall.deleteJobs(Constants.REQUEST_JOBS+"/"+selectJob.getJid());
							Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
						    }else
							Toast.makeText(context, "no internet connectivity", Toast.LENGTH_SHORT).show();
						}
					    });
					builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						    Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();

						    dialog.cancel();
						}
					    });
					builder2.show();
				    case 1: 
					Toast.makeText(getApplicationContext(), jodActionFailed[item], Toast.LENGTH_SHORT).show();

				    }
				}
			    });

			builder.show();	


		    }

		    return false;
		}


	    });		


*/

    }










}
