package dirac.android;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String DIRAC_JOBS = "DIRAC_JOBS";
	public static final String COLUMN_JOB_ID = "COLUMN_JOB_ID";
	public static final String COLUMN_JOB_NAME = "COLUMN_JOB_NAME";
	public static final String COLUMN_JOB_STATUS = "COLUMN_JOB_STATUS";
	public static final String COLUMN_JOB_SITE = "COLUMN_JOB_SITE";
	public static final String COLUMN_JOB_OWNER_GROUP = "COLUMN_JOB_OWNER_GROUP";
	public static final String COLUMN_JOB_APP_STATUS = "COLUMN_JOB_APP_STATUS";
	public static final String COLUMN_JOB_MINOR_STATUS	="COLUMN_JOB_MINOR_STATUS";
	public static final String COLUMN_JOB_CPU_TIME = "COLUMN_JOB_CPU_TIME";
	public static final String COLUMN_JOB_TIME_START_EXECUTION="COLUMN_JOB_TIME_START_EXECUTION";
	public static final String COLUMN_JOB_TIME_LAST_UPDATE="COLUMN_JOB_TIME_LAST_UPDATE";
	public static final String COLUMN_JOB_TIME_SUBMISSION="COLUMN_JOB_TIME_SUBMISSION";
	public static final String COLUMN_JOB_TIME_LAST_SQL="COLUMN_JOB_TIME_LAST_SQL";
	public static final String COLUMN_JOB_TIME_END_EXECUTION="COLUMN_JOB_TIME_END_EXECUTION";
	public static final String COLUMN_JOB_TIME_HEART_BEAT="COLUMN_JOB_TIME_HEART_BEAT";	
	public static final String COLUMN_JOB_PRIORITY = "COLUMN_JOB_PRIORITY";
	public static final String COLUMN_JOB_FLAG_DELETED = "COLUMN_JOB_FLAG_DELETED";
	public static final String COLUMN_JOB_FLAG_RETREIVED = "COLUMN_JOB_FLAG_RETREIVED";
	public static final String COLUMN_JOB_FLAG_OUTPUT_SANDBOX_READY="COLUMN_JOB_FLAG_OUTPUT_SANDBOX_READY";
	public static final String COLUMN_JOB_FLAG_INPUT_SANDBOX_READY="COLUMN_JOB_FLAG_INPUT_SANDBOX_READY";
	public static final String COLUMN_JOB_FLAG_ACCOUNTED="COLUMN_JOB_FLAG_ACCOUNTED";
	public static final String COLUMN_JOB_FLAG_KILLED="COLUMN_JOB_FLAG_KILLED";	
	public static final String COLUMN_JOB_FLAG_VERIFIED="COLUMN_JOB_FLAG_VERIFIED";	
	public static final String COLUMN_JOB_JOB_GROUP="COLUMN_JOB_JOB_GROUP";
	public static final String COLUMN_JOB_RESCHEDULES = "COLUMN_JOB_RESCHEDULES";
	public static final String COLUMN_JOB_OWNER ="COLUMN_JOB_OWNER";
	public static final String COLUMN_JOB_OWNER_DN = "COLUMN_JOB_OWNER_DN";
	public static final String COLUMN_JOB_SETUP = "COLUMN_JOB_SETUP";
	public static final String COLUMN_JOB_CHANGE_STATUS_ACTION = "COLUMN_JOB_CHANGE_STATUS_ACTION";

	public static final String DIRAC_STATS = "DIRAC_STATS";
	public static final String DATE_TIME = "DATE_TIME";



	private static final String DATABASE_NAME = "dirac.db";
	private static final int DATABASE_VERSION = 102	;

	// Database creation sql statement
	private static String DATABASE_CREATE2;





	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ DIRAC_JOBS + "( "
			+ COLUMN_JOB_ID  + " INTEGER PRIMARY KEY,"
			+ COLUMN_JOB_NAME + " TEXT,"
			+ COLUMN_JOB_STATUS + " TEXT,"
			+ COLUMN_JOB_SITE + " TEXT,"
			+ COLUMN_JOB_OWNER_GROUP + " TEXT,"
			+ COLUMN_JOB_APP_STATUS + " TEXT,"
			+ COLUMN_JOB_MINOR_STATUS + " TEXT,"
			+ COLUMN_JOB_CPU_TIME + " DATETIME,"
			+ COLUMN_JOB_TIME_START_EXECUTION + " DATETIME,"
			+ COLUMN_JOB_TIME_LAST_UPDATE + " DATETIME,"
			+ COLUMN_JOB_TIME_SUBMISSION + " DATETIME,"
			+ COLUMN_JOB_TIME_LAST_SQL + " DATETIME,"
			+ COLUMN_JOB_TIME_END_EXECUTION + " DATETIME,"
			+ COLUMN_JOB_TIME_HEART_BEAT + " DATETIME,"
			+ COLUMN_JOB_PRIORITY + " TEXT,"
			+ COLUMN_JOB_FLAG_DELETED + " TEXT,"
			+ COLUMN_JOB_FLAG_RETREIVED + " TEXT,"
			+ COLUMN_JOB_FLAG_OUTPUT_SANDBOX_READY + " TEXT,"
			+ COLUMN_JOB_FLAG_INPUT_SANDBOX_READY + " TEXT,"
			+ COLUMN_JOB_FLAG_ACCOUNTED + " TEXT,"
			+ COLUMN_JOB_FLAG_KILLED + " TEXT,"
			+ COLUMN_JOB_FLAG_VERIFIED + " TEXT,"
			+ COLUMN_JOB_JOB_GROUP + " TEXT,"
			+ COLUMN_JOB_RESCHEDULES + " TEXT,"
			+ COLUMN_JOB_OWNER + " TEXT,"
			+ COLUMN_JOB_OWNER_DN + " TEXT,"
			+ COLUMN_JOB_SETUP + " TEXT,"
			+ COLUMN_JOB_CHANGE_STATUS_ACTION + " TEXT"
			+ ");";


	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {


		String[] status = Status.PossibleStatus;
		String column = "";
		for(int i = 0; i<status.length; i++)
			column = column+status[i] + " TEXT, ";

		DATABASE_CREATE2 =  "CREATE TABLE "
				+ DIRAC_STATS + "( "
				+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ column 
				+ DATE_TIME + " DATETIME " + ");";

		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_CREATE2);
	}

	public void deleteTable(SQLiteDatabase db, String TABLE) {
		db.delete(TABLE, null,null);

	}
	public void deleteStat(SQLiteDatabase db, String TABLE) {
		//test if exist !
		db.execSQL("DELETE FROM "+ TABLE +" WHERE _ID NOT IN (SELECT _ID FROM ( SELECT _ID FROM "+ TABLE +" ORDER BY _ID DESC  LIMIT 1))");
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


