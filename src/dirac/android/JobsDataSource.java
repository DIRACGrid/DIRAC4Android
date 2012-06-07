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

	private String[] allStats = { MySQLiteHelper.COMPLETED,MySQLiteHelper.RUNNING,MySQLiteHelper.FAILED,MySQLiteHelper.UNKNOWN,MySQLiteHelper.DATE_TIME};
	
		
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
		values.put(MySQLiteHelper.COLUMN_JOB_ID, myjob.id);

		values.put(MySQLiteHelper.COLUMN_JOB_NAME, myjob.name);
		values.put(MySQLiteHelper.COLUMN_JOB_STATE, myjob.state);
		values.put(MySQLiteHelper.COLUMN_JOB_SITE, myjob.site);
		values.put(MySQLiteHelper.COLUMN_JOB_SUB_TIME, myjob.time);
		values.put(MySQLiteHelper.COLUMN_JOB_HB_TIME, myjob.time);

		Cursor c = database.rawQuery("SELECT * FROM " + MySQLiteHelper.DIRAC_JOBS + " WHERE " + MySQLiteHelper.COLUMN_JOB_ID + "= '" + myjob.id.toString()+ "'",new String [] {});
		 if(!c.moveToFirst())
		 {				long insertId = database.insert(MySQLiteHelper.DIRAC_JOBS, null,
						values);
	 }
		
		



	}
	


	public void parse(InputStream inStream, int tot) {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

		try {
			String line;
		        	while ((line = reader.readLine()) != null) {
		        		String[] RowData = line.split(",");

		        		ContentValues values = new ContentValues();
		        		
		        		
		        		values.put(MySQLiteHelper.COLUMN_JOB_ID, Integer.parseInt(RowData[0]));
		        		values.put(MySQLiteHelper.COLUMN_JOB_STATE, RowData[1]);
		        		values.put(MySQLiteHelper.COLUMN_JOB_NAME, RowData[2]);
		        		values.put(MySQLiteHelper.COLUMN_JOB_SITE,RowData[3]);
		        		values.put(MySQLiteHelper.COLUMN_JOB_SUB_TIME, RowData[4]);
		        		values.put(MySQLiteHelper.COLUMN_JOB_HB_TIME, RowData[4]);
		        		
		        	//	Cursor c = database.rawQuery("SELECT * FROM " + MySQLiteHelper.DIRAC_JOBS + " WHERE " + MySQLiteHelper.COLUMN_JOB_ID + "= '" + RowData[0]+ "'",new String [] {});
		        	//	 if(!c.moveToFirst())
		        			 database.insert(MySQLiteHelper.DIRAC_JOBS, null,values);
		        		 
		        		
		        
		            // do something with "data" and "value"
		        	}
		    }
		    catch (IOException ex) {
		        // handle exception
		    }
		    finally {
		        try {
		        	inStream.close();
		        }
		        catch (IOException e) {
		            // handle exception
		        }
		    }
			
	
		String[][] statess = new String[][] {{ "running",  "failed", "completed" ,"running",  "unknown", "completed" },
				                            { "running",  "running", "completed","running",  "unknown", "completed"  },
				                            { "running",  "failed", "completed","completed",  "completed", "completed"  },
				                            { "running",  "failed", "completed","completed",  "completed", "completed"  },
				                            { "failed",  "failed", "failed","failed",  "unknown", "failed"  }	,
				                            { "running",  "failed", "running","running",  "running", "running"  }			};	
		String[] sites = new String[] { "CERN.CH", "RAL.UK", "PIC.ES"};	
		String[] times = new String[] { "10:12:34", "10:15:34", "10:16:34", "10:11:34" };	
		String[] names = new String[] { "bjets", "B2DX_beta", "Zmumu"};


		try{

			database.beginTransaction();
			DatabaseUtils.InsertHelper test = new DatabaseUtils.InsertHelper(database, MySQLiteHelper.DIRAC_JOBS);	
			

			int nextInt0 = new Random().nextInt(6);
			String[] states = statess[nextInt0];
			
			for(int i = 1; i<tot;i++){				
				ContentValues values = new ContentValues();
				int nextInt1 = new Random().nextInt(6);
				int nextInt2 = new Random().nextInt(3);
				int nextInt3 = new Random().nextInt(3);
				int nextInt4 = new Random().nextInt(4);
	    		values.put(MySQLiteHelper.COLUMN_JOB_ID, i);
	    		values.put(MySQLiteHelper.COLUMN_JOB_STATE, states[nextInt1]);
	    		values.put(MySQLiteHelper.COLUMN_JOB_NAME, names[nextInt2]);
	    		values.put(MySQLiteHelper.COLUMN_JOB_SITE,sites[nextInt3]);
	    		values.put(MySQLiteHelper.COLUMN_JOB_SUB_TIME, times[nextInt4]);
	    		values.put(MySQLiteHelper.COLUMN_JOB_HB_TIME, times[nextInt4]);
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
				allStats, null, null, null, null, null);


		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String[]	myInt = {cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),cursor.getString(4)};

			mysuperList.add(myInt);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return mysuperList;
	}	
	
	public void creatTableOfSatus() {		
		
		
		String status[] = {"running","completed","failed","unknown"};
		ContentValues values = new ContentValues();

	
		for(String state: status){
			Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.DIRAC_JOBS + " WHERE " + MySQLiteHelper.COLUMN_JOB_STATE + "= '" + state+ "'",new String [] {});
			Integer value = cursor.getCount();

			if(state.equals("running"))	
				values.put(MySQLiteHelper.RUNNING, value);
			if(state.equals("failed"))	
				values.put(MySQLiteHelper.FAILED, value);
			if(state.equals("completed"))	
				values.put(MySQLiteHelper.COMPLETED, value);
			if(state.equals("unknown"))	
				values.put(MySQLiteHelper.UNKNOWN, value);
		
			cursor.close();
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		values.put(MySQLiteHelper.DATE_TIME,dateFormat.format(date));
		
		long insertId = database.insert(MySQLiteHelper.DIRAC_STATS, null,
				values);
	}	
	
@SuppressWarnings("null")
public Status[] getLastUpdate() {	
	Cursor cursor = database.query(MySQLiteHelper.DIRAC_STATS,
			allStats, null, null, null, null, null);
	cursor.moveToLast();
	Status[] tmpmap = new Status[allStats.length-1] ;	
	int j = 0;

	if(!cursor.isAfterLast()){
		for(int i = 0; i < allStats.length-1;i++){
			Status state = new Status(allStats[i],cursor.getString(cursor.getColumnIndex(allStats[i])));
			if(Integer.parseInt(state.number()) == 0){
				j++;
				continue;
			}
			tmpmap[i-j]=state;
		}
	}
	
	Status[] map = new Status[allStats.length-1-j] ;
	for(int i = 0; i < allStats.length-1-j;i++)
		map[i]=tmpmap[i];
	
	return map;
}	


public String getLastUpdateTime() {	
	Cursor cursor = database.query(MySQLiteHelper.DIRAC_STATS,
			allStats, null, null, null, null, null);
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
		job.id = cursor.getInt(0);
		job.name = cursor.getString(1);
		job.state = cursor.getString(2);
		job.time = cursor.getString(3);
		job.site = cursor.getString(5);
		return job;
	}
}
