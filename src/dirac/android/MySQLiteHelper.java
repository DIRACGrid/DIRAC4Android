package dirac.android;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String DIRAC_JOBS = "DIRAC_JOBS";
	public static final String COLUMN_JOB_ID = "COLUMN_JOB_ID";
	public static final String COLUMN_JOB_NAME = "COLUMN_JOB_NAME";
	public static final String COLUMN_JOB_STATE = "COLUMN_JOB_STATE";
	public static final String COLUMN_JOB_SUB_TIME = "COLUMN_JOB_SUB_TIME";
	public static final String COLUMN_JOB_HB_TIME = "COLUMN_JOB_HB_TIME";
	public static final String COLUMN_JOB_SITE = "COLUMN_JOB_SITE";


	public static final String DIRAC_STATS = "DIRAC_STATS";
	public static final String COMPLETED = "completed";
	public static final String FAILED = "failed";
	public static final String RUNNING = "running";
	public static final String UNKNOWN = "unknown";
	public static final String DATE_TIME = "DATE_TIME";

	

	private static final String DATABASE_NAME = "dirac.db";
	private static final int DATABASE_VERSION = 22;

	// Database creation sql statement
	private static final String DATABASE_CREATE2 = "CREATE TABLE "
			+ DIRAC_STATS + "( "
			+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ COMPLETED + " INTEGER,"
			+ FAILED + " INTEGER,"
			+ RUNNING + " INTEGER,"
			+ UNKNOWN + " INTEGER,"
			+ DATE_TIME + " DATETIME " + ");";
	
	

	
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ DIRAC_JOBS + "( "
			+ COLUMN_JOB_ID + " INTEGER PRIMARY KEY,"
			+ COLUMN_JOB_NAME + " TEXT,"
			+ COLUMN_JOB_STATE + " TEXT,"
			+ COLUMN_JOB_SUB_TIME + " TEXT,"
			+ COLUMN_JOB_HB_TIME + " TEXT,"
			+ COLUMN_JOB_SITE + " TEXT " + ");";
	

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		
	
		
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_CREATE2);
	}

	public void deleteTable(SQLiteDatabase db, String TABLE) {
		db.delete(TABLE, null,null);
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + DIRAC_JOBS);
		db.execSQL("DROP TABLE IF EXISTS " + DIRAC_STATS);
		onCreate(db);
	}

}


