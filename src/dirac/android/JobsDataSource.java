package dirac.android;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import dirac.gsonconfig.Job;
import dirac.gsonconfig.Jobs;
import dirac.gsonconfig.StatusSummary;
import dirac.gsonconfig.flag;
import dirac.gsonconfig.time;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class JobsDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { 
	MySQLiteHelper.COLUMN_JOB_ID,  
	MySQLiteHelper.COLUMN_JOB_NAME, 
	MySQLiteHelper.COLUMN_JOB_STATUS, 
	MySQLiteHelper.COLUMN_JOB_SITE ,
	MySQLiteHelper.COLUMN_JOB_OWNER_GROUP, 
	MySQLiteHelper.COLUMN_JOB_APP_STATUS ,
	MySQLiteHelper.COLUMN_JOB_MINOR_STATUS , 
	MySQLiteHelper.COLUMN_JOB_CPU_TIME ,
	MySQLiteHelper.COLUMN_JOB_TIME_START_EXECUTION, 
	MySQLiteHelper.COLUMN_JOB_TIME_LAST_UPDATE, 
	MySQLiteHelper.COLUMN_JOB_TIME_SUBMISSION,
	MySQLiteHelper.COLUMN_JOB_TIME_LAST_SQL, 
	MySQLiteHelper.COLUMN_JOB_TIME_END_EXECUTION, 
	MySQLiteHelper.COLUMN_JOB_TIME_HEART_BEAT, 
	MySQLiteHelper.COLUMN_JOB_PRIORITY ,
	MySQLiteHelper.COLUMN_JOB_FLAG_DELETED, 
	MySQLiteHelper.COLUMN_JOB_FLAG_RETREIVED,
	MySQLiteHelper.COLUMN_JOB_FLAG_OUTPUT_SANDBOX_READY, 
	MySQLiteHelper.COLUMN_JOB_FLAG_INPUT_SANDBOX_READY , 
	MySQLiteHelper.COLUMN_JOB_FLAG_ACCOUNTED, 
	MySQLiteHelper.COLUMN_JOB_FLAG_KILLED,
	MySQLiteHelper.COLUMN_JOB_FLAG_VERIFIED,
	MySQLiteHelper.COLUMN_JOB_JOB_GROUP , 
	MySQLiteHelper.COLUMN_JOB_RESCHEDULES ,
	MySQLiteHelper.COLUMN_JOB_OWNER,
	MySQLiteHelper.COLUMN_JOB_OWNER_DN,
	MySQLiteHelper.COLUMN_JOB_SETUP//,
	//MySQLiteHelper.COLUMN_JOB_CHANGE_STATUS_ACTION,
    };




    public JobsDataSource(Context context) {
	dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
	database = dbHelper.getWritableDatabase();
    }

    public void close() {
	dbHelper.close();
    }

    public void createJob (Job i) {
	ContentValues values = new ContentValues();	
	values.put(MySQLiteHelper.COLUMN_JOB_ID,  Integer.getInteger(i.getJid()));
	values.put(MySQLiteHelper.COLUMN_JOB_NAME, i.getName());
	values.put(MySQLiteHelper.COLUMN_JOB_STATUS, i.getStatus());
	values.put(MySQLiteHelper.COLUMN_JOB_SITE , i.getSite());
	values.put(MySQLiteHelper.COLUMN_JOB_OWNER_GROUP, i.getOwnerGroup());
	values.put(MySQLiteHelper.COLUMN_JOB_APP_STATUS , i.getAppStatus());
	values.put(MySQLiteHelper.COLUMN_JOB_MINOR_STATUS , i.getMinorStatus());
	values.put(MySQLiteHelper.COLUMN_JOB_CPU_TIME , i.getCpuTime());
	values.put(MySQLiteHelper.COLUMN_JOB_TIME_START_EXECUTION, i.getTimes().getStartExecution());
	values.put(MySQLiteHelper.COLUMN_JOB_TIME_LAST_UPDATE, i.getTimes().getLastUpdate());
	values.put(MySQLiteHelper.COLUMN_JOB_TIME_SUBMISSION, i.getTimes().getSubmission());
	values.put(MySQLiteHelper.COLUMN_JOB_TIME_LAST_SQL, i.getTimes().getLastSOL());
	values.put(MySQLiteHelper.COLUMN_JOB_TIME_END_EXECUTION, i.getTimes().getEndExecution());
	values.put(MySQLiteHelper.COLUMN_JOB_TIME_HEART_BEAT, i.getTimes().getHeartBeat());
	values.put(MySQLiteHelper.COLUMN_JOB_PRIORITY , i.getPriority());
	values.put(MySQLiteHelper.COLUMN_JOB_FLAG_DELETED, i.getFlags().getDeleted());
	values.put(MySQLiteHelper.COLUMN_JOB_FLAG_RETREIVED, i.getFlags().getRetrieved());
	values.put(MySQLiteHelper.COLUMN_JOB_FLAG_OUTPUT_SANDBOX_READY, i.getFlags().getOutputSandboxReady());
	values.put(MySQLiteHelper.COLUMN_JOB_FLAG_INPUT_SANDBOX_READY , i.getFlags().getInputSandboxReady());
	values.put(MySQLiteHelper.COLUMN_JOB_FLAG_ACCOUNTED, i.getFlags().getAccounted());
	values.put(MySQLiteHelper.COLUMN_JOB_FLAG_KILLED, i.getFlags().getKilled());
	values.put(MySQLiteHelper.COLUMN_JOB_FLAG_VERIFIED, i.getFlags().getVerified());
	values.put(MySQLiteHelper.COLUMN_JOB_JOB_GROUP , i.getJobGroup());
	values.put(MySQLiteHelper.COLUMN_JOB_RESCHEDULES , i.getReschedules());
	values.put(MySQLiteHelper.COLUMN_JOB_OWNER , i.getOwner());
	values.put(MySQLiteHelper.COLUMN_JOB_OWNER_DN , i.getOwnerDN());
	values.put(MySQLiteHelper.COLUMN_JOB_SETUP , i.getSetup());
	//values.put(MySQLiteHelper.COLUMN_JOB_CHANGE_STATUS_ACTION , i.getChangeStatusAction());

	Cursor c = database.rawQuery("SELECT * FROM " + MySQLiteHelper.DIRAC_JOBS + " WHERE " + MySQLiteHelper.COLUMN_JOB_ID + "= '" + i.getJid()+ "'",new String [] {});
	if(!c.moveToFirst())
	    {				database.insert(MySQLiteHelper.DIRAC_JOBS, null,values);
	    }





    }



    public void parse(Jobs jobs) {
	try{

	    database.beginTransaction();
	    DatabaseUtils.InsertHelper test = new DatabaseUtils.InsertHelper(database, MySQLiteHelper.DIRAC_JOBS);	


	    for(Job i: jobs.getJobs()){				
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_JOB_ID,  i.getJid());
		values.put(MySQLiteHelper.COLUMN_JOB_NAME, i.getName());
		values.put(MySQLiteHelper.COLUMN_JOB_STATUS, i.getStatus());
		values.put(MySQLiteHelper.COLUMN_JOB_SITE , i.getSite());
		values.put(MySQLiteHelper.COLUMN_JOB_OWNER_GROUP, i.getOwnerGroup());
		values.put(MySQLiteHelper.COLUMN_JOB_APP_STATUS , i.getAppStatus());
		values.put(MySQLiteHelper.COLUMN_JOB_MINOR_STATUS , i.getMinorStatus());
		values.put(MySQLiteHelper.COLUMN_JOB_CPU_TIME , i.getCpuTime());
		values.put(MySQLiteHelper.COLUMN_JOB_TIME_START_EXECUTION, i.getTimes().getStartExecution());
		values.put(MySQLiteHelper.COLUMN_JOB_TIME_LAST_UPDATE, i.getTimes().getLastUpdate());
		values.put(MySQLiteHelper.COLUMN_JOB_TIME_SUBMISSION, i.getTimes().getSubmission());
		values.put(MySQLiteHelper.COLUMN_JOB_TIME_LAST_SQL, i.getTimes().getLastSOL());
		values.put(MySQLiteHelper.COLUMN_JOB_TIME_END_EXECUTION, i.getTimes().getEndExecution());
		values.put(MySQLiteHelper.COLUMN_JOB_TIME_HEART_BEAT, i.getTimes().getHeartBeat());
		values.put(MySQLiteHelper.COLUMN_JOB_PRIORITY , i.getPriority());
		values.put(MySQLiteHelper.COLUMN_JOB_FLAG_DELETED, i.getFlags().getDeleted());
		values.put(MySQLiteHelper.COLUMN_JOB_FLAG_RETREIVED, i.getFlags().getRetrieved());
		values.put(MySQLiteHelper.COLUMN_JOB_FLAG_OUTPUT_SANDBOX_READY, i.getFlags().getOutputSandboxReady());
		values.put(MySQLiteHelper.COLUMN_JOB_FLAG_INPUT_SANDBOX_READY , i.getFlags().getInputSandboxReady());
		values.put(MySQLiteHelper.COLUMN_JOB_FLAG_ACCOUNTED, i.getFlags().getAccounted());
		values.put(MySQLiteHelper.COLUMN_JOB_FLAG_KILLED, i.getFlags().getKilled());
		values.put(MySQLiteHelper.COLUMN_JOB_FLAG_VERIFIED, i.getFlags().getVerified());
		values.put(MySQLiteHelper.COLUMN_JOB_JOB_GROUP , i.getJobGroup());
		values.put(MySQLiteHelper.COLUMN_JOB_RESCHEDULES , i.getReschedules());
		values.put(MySQLiteHelper.COLUMN_JOB_OWNER , i.getOwner());
		values.put(MySQLiteHelper.COLUMN_JOB_OWNER_DN , i.getOwnerDN());
		values.put(MySQLiteHelper.COLUMN_JOB_SETUP , i.getSetup());
		//	values.put(MySQLiteHelper.COLUMN_JOB_CHANGE_STATUS_ACTION , i.getChangeStatusAction());
		test.insert(values);
	    }			  
	    database.setTransactionSuccessful();
	} catch (SQLException e) {
	} finally {
	    database.endTransaction();
	}
    }	






    public List<Job> getAllJobIDs() {
	List<Job> jobids = new ArrayList<Job>();

	Cursor cursor = database.query(MySQLiteHelper.DIRAC_JOBS,
				       allColumns, null, null, null, null, null);




	cursor.moveToLast();
	while (!cursor.isBeforeFirst()) {
	    Job job = cursorToComment(cursor);
	    jobids.add(job);
	    cursor.moveToPrevious();
	}
	// Make sure to close the cursor
	cursor.close();
	return jobids;
    }


    public Job getJobInfo(String jid) {



	Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.DIRAC_JOBS + " WHERE " + MySQLiteHelper.COLUMN_JOB_ID + "= '" + jid+ "'",new String [] {});
	Job job = null;
	cursor.moveToFirst();
	job = cursorToComment(cursor);
	cursor.close();
	return job;
    }	



    public List<Job> getAllJobIDsOfStatus(String status) {
    	List<Job> jobids = new ArrayList<Job>();



    	Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.DIRAC_JOBS + " WHERE " + MySQLiteHelper.COLUMN_JOB_STATUS + "= '" + status+ "'",new String [] {});

    	//cursor.moveToLast();
    	//while (!cursor.isBeforeFirst()) {
    	//		Job job = cursorToComment(cursor);
    	//		jobids.add(job);
    	//		cursor.moveToPrevious();
    	//	}


    	cursor.moveToLast();
    	while (!cursor.isBeforeFirst()) {
    	    Job job = cursorToComment(cursor);
    	    jobids.add(job);
    	    cursor.moveToPrevious();
    	}


    	// Make sure to close the cursor
    	cursor.close();
    	return jobids;
        }	
    
    
    
    
    
    
    
    
    
    
    public List<Job> getAllJobIDsOfSites(String status) {
    	List<Job> jobids = new ArrayList<Job>();



    	Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.DIRAC_JOBS + " WHERE " + MySQLiteHelper.COLUMN_JOB_SITE + "= '" + status+ "'",new String [] {});

    	//cursor.moveToLast();
    	//while (!cursor.isBeforeFirst()) {
    	//		Job job = cursorToComment(cursor);
    	//		jobids.add(job);
    	//		cursor.moveToPrevious();
    	//	}


    	cursor.moveToLast();
    	while (!cursor.isBeforeFirst()) {
    	    Job job = cursorToComment(cursor);
    	    jobids.add(job);
    	    cursor.moveToPrevious();
    	}


    	// Make sure to close the cursor
    	cursor.close();
    	return jobids;
        }	
    
    
    
    
    
    
    
    
    

    public ArrayList<String[]> getAllJobIDsOfSatusTime() {

	ArrayList<String[]> mysuperList = new ArrayList<String[]>() ;

	Cursor cursor = database.query(MySQLiteHelper.DIRAC_STATS,
				       null, null, null, null, null, null);

	cursor.moveToFirst();
	while (!cursor.isAfterLast()) {			
	    String[] status = Status.PossibleStatus;
	    String[] myInt = new String[status.length+1];
	    for(int i = 0; i<status.length; i++)
		myInt[i] = cursor.getString(cursor.getColumnIndex(status[i]));


	    System.out.println("UUZ");
	    myInt[status.length] = cursor.getString(cursor.getColumnIndex("DATE_TIME"));

	    System.out.println("UUZ");
	    mysuperList.add(myInt);
	    cursor.moveToNext();
	}
	// Make sure to close the cursor
	cursor.close();
	return mysuperList;
    }	

    public void parseSummary(StatusSummary Summary) {		


	ContentValues values = new ContentValues();

	String defVal = "0";
	if(Summary.getChecking() != "Checking")
	    defVal = Summary.getChecking();
	values.put("Checking", defVal);	
	defVal = "0";
	if(Summary.getCompleted() != "Completed")
	    defVal = Summary.getCompleted();
	values.put("Completed", defVal);
	defVal = "0";
	if(Summary.getDone() != "Done")
	    defVal = Summary.getDone();
	values.put("Done", defVal);	
	defVal = "0";
	if(Summary.getFailed() != "Failed")
	    defVal = Summary.getFailed();
	values.put("Failed", defVal);	
	defVal = "0";
	if(Summary.getKilled() != "Killed")
	    defVal = Summary.getKilled();
	values.put("Killed", defVal);	
	defVal = "0";
	if(Summary.getMatched() != "Matched")
	    defVal = Summary.getMatched();
	values.put("Matched", defVal);	
	defVal = "0";
	if(Summary.getReceived() != "Received")
	    defVal = Summary.getReceived();
	values.put("Received", defVal);	
	defVal = "0";
	if(Summary.getRunning() != "Running")
	    defVal = Summary.getRunning();
	values.put("Running", defVal);	
	defVal = "0";
	if(Summary.getStaging() != "Staging")
	    defVal = Summary.getStaging();
	values.put("Staging", defVal);	
	defVal = "0";
	if(Summary.getStalled() != "Stalled")
	    defVal = Summary.getStalled();
	values.put("Stalled", defVal);	
	defVal = "0";
	if(Summary.getWaiting() != "Waiting")
	    defVal = Summary.getWaiting();
	values.put("Waiting", defVal);
	defVal = "0";
	if(Summary.getDeleted() != "Deleted")
	    defVal = Summary.getDeleted();
	values.put("Deleted", defVal);	



	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	Date date = new Date();
	//	System.out.println(dateFormat.format(date));
	values.put(MySQLiteHelper.DATE_TIME,dateFormat.format(date));

	database.insert(MySQLiteHelper.DIRAC_STATS, null,values);
	//	System.out.println(dateFormat.format(date));

    }	

    public void creatTableOfStatus() {		


	ContentValues values = new ContentValues();
	String[] status = Status.PossibleStatus;

	for(String state: status){
	    Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.DIRAC_JOBS + " WHERE " + MySQLiteHelper.COLUMN_JOB_STATUS + "= '" + state+ "'",new String [] {});
	    Integer value = cursor.getCount();

	    values.put(state, value);	 

	    cursor.close();
	}
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	Date date = new Date();
	System.out.println(dateFormat.format(date));
	values.put(MySQLiteHelper.DATE_TIME,dateFormat.format(date));

	database.insert(MySQLiteHelper.DIRAC_STATS, null,values);
    }	



    
    
    
    
    public Status[] getLastUpdate() {	



	Cursor cursor = database.query(MySQLiteHelper.DIRAC_STATS,
				       null, null, null, null, null, null);
	cursor.moveToLast();


	String[] status = Status.PossibleStatus;


	Status[] tmpmap = new Status[status.length] ;	
	int j = 0;

	if(!cursor.isAfterLast()){

	    for(int i = 0; i < status.length;i++){

		Status state = new Status(status[i],cursor.getString(cursor.getColumnIndex(status[i])));


		if(Integer.parseInt(state.number()) == 0){

		    j++;
		    continue;
		}
		tmpmap[i-j]=state;
	    }
	}

	Status[] map = new Status[status.length-j] ;

	for(int i = 0; i < status.length-j;i++)
	    map[i]=tmpmap[i];



	return map;
    }	

	
	
	
	
    public Status[] getLastUpdate2() {	



	String[] status = Status.PossibleStatus;


	Status[] tmpmap = new Status[status.length] ;	
	int j = 0;


	for(int i = 0; i < status.length;i++){


	    Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.DIRAC_JOBS + " WHERE " + MySQLiteHelper.COLUMN_JOB_STATUS + "= '" + status[i]+ "'",new String [] {});
				
	    if(cursor.getCount() == 0){

		j++;
		continue;
	    }else{
		Integer tot = cursor.getCount();
		tmpmap[i-j] = new Status(status[i],tot.toString());

		Log.i(status[i],tot.toString());

	    }
	}



	Status[] map = new Status[status.length-j] ;
	for(int i = 0; i < status.length-j;i++)
	    map[i]=tmpmap[i];


	return map;

    }	

	

    public String getLastUpdateTime() {	
	Cursor cursor = database.query(MySQLiteHelper.DIRAC_STATS,
				       null, null, null, null, null, null);
	cursor.moveToLast();
	String time =null;
	if(!cursor.moveToLast()){
	    time = "never";
	}else{
	    time = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.DATE_TIME));
	}
	return time;
    }	





    private Job cursorToComment(Cursor cursor) {

	Job job = new Job();		
	job.setJid(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_ID)));
	job.setName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_NAME)));
	job.setStatus(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_STATUS)));
	job.setSite(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_SITE)));
	job.setOwnerGroup(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_OWNER_GROUP)));
	job.setAppStatus(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_APP_STATUS)));
	job.setMinorStatus(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_MINOR_STATUS)));	
	job.setCpuTime(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_CPU_TIME)));
	time times = new time();
	times.setStartExecution(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_TIME_START_EXECUTION)));
	times.setLastUpdate(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_TIME_LAST_UPDATE)));
	times.setSubmission(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_TIME_SUBMISSION)));
	times.setLastSOL(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_TIME_LAST_SQL)));
	times.setEndExecution(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_TIME_END_EXECUTION)));
	times.setHeartBeat(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_TIME_HEART_BEAT)));
	job.setTimes(times);
	job.setPriority(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_PRIORITY)));
	flag flags = new flag();
	flags.setDeleted(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_FLAG_DELETED)));
	flags.setRetrieved(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_FLAG_RETREIVED)));
	flags.setOutputSandboxReady(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_FLAG_OUTPUT_SANDBOX_READY)));
	flags.setInputSandboxReady(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_FLAG_INPUT_SANDBOX_READY)));
	flags.setAccounted(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_FLAG_ACCOUNTED)));
	flags.setKilled(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_FLAG_KILLED)));
	flags.setVerified(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_FLAG_VERIFIED)));
	job.setFlags(flags);
	job.setJobGroup(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_JOB_GROUP)));
	job.setReschedules(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_RESCHEDULES)));
	job.setOwner(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_OWNER)));
	job.setOwnerDN(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_OWNER_DN)));
	job.setSetup(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_SETUP)));
	//	job.setChangeStatusAction(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_JOB_CHANGE_STATUS_ACTION)));

	return job;
    }

    public void parseJobToState(Jobs jobs) {
	// TODO Auto-generated method stub
		
    }
}
