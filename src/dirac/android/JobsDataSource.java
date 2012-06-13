package dirac.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class JobsDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_JOB_ID, 
			MySQLiteHelper.COLUMN_JOB_NAME, 
			MySQLiteHelper.COLUMN_JOB_STATE, 
			MySQLiteHelper.COLUMN_JOB_SITE, 
			MySQLiteHelper.COLUMN_JOB_SUB_TIME, 
			MySQLiteHelper.COLUMN_JOB_HB_TIME};



	private String[] MID = {MySQLiteHelper.COLUMN_JOB_ID};

	public JobsDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void createJob (Job myjob) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_JOB_ID, Integer.getInteger(myjob.getJid()));

		values.put(MySQLiteHelper.COLUMN_JOB_NAME, myjob.getName());
		values.put(MySQLiteHelper.COLUMN_JOB_STATE, myjob.getStatus());
		values.put(MySQLiteHelper.COLUMN_JOB_SITE, myjob.getSite());
		values.put(MySQLiteHelper.COLUMN_JOB_SUB_TIME, myjob.getCpuTime());
		values.put(MySQLiteHelper.COLUMN_JOB_HB_TIME, myjob.getCpuTime());

		Cursor c = database.rawQuery("SELECT * FROM " + MySQLiteHelper.DIRAC_JOBS + " WHERE " + MySQLiteHelper.COLUMN_JOB_ID + "= '" + myjob.getJid()+ "'",new String [] {});
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
				values.put(MySQLiteHelper.COLUMN_JOB_ID, i.getJid());
				values.put(MySQLiteHelper.COLUMN_JOB_STATE, i.getStatus());
				values.put(MySQLiteHelper.COLUMN_JOB_NAME, i.getName());
				values.put(MySQLiteHelper.COLUMN_JOB_SITE,i.getSite());
				values.put(MySQLiteHelper.COLUMN_JOB_SUB_TIME, i.getCpuTime());
				values.put(MySQLiteHelper.COLUMN_JOB_HB_TIME, i.getCpuTime());
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




		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Job job = cursorToComment(cursor);
			jobids.add(job);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return jobids;
	}
	
	
	public Job getJobInfo(String jid) {


		Log.d("JA",jid) ;

		Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.DIRAC_JOBS + " WHERE " + MySQLiteHelper.COLUMN_JOB_ID + "= '" + jid+ "'",new String [] {});
		Job job = null;
		cursor.moveToFirst();
		job = cursorToComment(cursor);
		cursor.close();
		return job;
	}	
	
	
	
	public List<Job> getAllJobIDsOfSatus(String status) {
		List<Job> jobids = new ArrayList<Job>();



		Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.DIRAC_JOBS + " WHERE " + MySQLiteHelper.COLUMN_JOB_STATE + "= '" + status+ "'",new String [] {});

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Job job = cursorToComment(cursor);
			jobids.add(job);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return jobids;
	}	

	@SuppressWarnings("null")
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

	public void creatTableOfSatus() {		


		ContentValues values = new ContentValues();
		String[] status = Status.PossibleStatus;

		for(String state: status){
			Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.DIRAC_JOBS + " WHERE " + MySQLiteHelper.COLUMN_JOB_STATE + "= '" + state+ "'",new String [] {});
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

	@SuppressWarnings("null")
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
		//+ COLUMN_JOB_ID + " INTEGER PRIMARY KEY,"
	//	+ COLUMN_JOB_NAME + " TEXT,"
	//	+ COLUMN_JOB_STATE + " TEXT,"
	//	+ COLUMN_JOB_SUB_TIME + " TEXT,"
	//	+ COLUMN_JOB_HB_TIME + " TEXT,"
	//	+ COLUMN_JOB_SITE + " TEXT " + ");";
		job.setJid(cursor.getString(0));
		job.setName(cursor.getString(1));
		job.setStatus(cursor.getString(2));
		job.setCpuTime(cursor.getString(3));
		job.setCpuTime(cursor.getString(4));
		job.setSite(cursor.getString(5));
		return job;
	}
}
