package dirac.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CacheHelper {

    public static final String PREF_NAME = "FILTER_PREFERENCES";
    public static final int MODE = Context.MODE_PRIVATE;
	
    public static final String GETJOBS = "GETJOBS";
    public static final String GETJOBSTYPE = "GETJOBSTYPE";
    public static final String STARTJOBNB = "STARTJOBNB";
    public static final String GETDESCRITPION = "GETDESCRITPION";
    public static final String NBJOBSDB = "NBJOBSDB";
    public static final String NMAXBJOBS = "MAXNBJOBSDB";
    public static final String NBCERT = "NBCERT";
    public static final String CERTREADY = "CERTREADY";

    public static final String JOBNAME = "JOBNAME";
    public static final String TIME = "TIME";
    public static final String FTIME = "FINAL_TIME";
    public static final String APPNAME = "APPNAME";
    public static final String SITE = "SITE";
    public static final String USEIT  = "USEIT";
    public static final String SHPREF_GROUP  = "SHPREF_GROUP";
    public static final String SHPREF_SETUP  = "SHPREF_SETUP";
    public static final String SHPREF_GROUPS  = "SHPREF_GROUPS";
    public static final String SHPREF_SETUPS  = "SHPREF_SETUPS";
    public static final String SHPREF_KEY_ACCESS_TOKEN  = "SHPREF_KEY_ACCESS_TOKEN";
    public static final String SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE  = "SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE";
    public static final String SHPREF_KEY_ACCESS_TOKEN_EXPIRES_TIME =  "SHPREF_KEY_ACCESS_TOKEN_EXPIRES_TIME";
    public static final String CERTALIAS =  "CERTALIAS";
    public static final String LASTUPDATE =  "LASTUPDATE";
    public static final String ALLENTRIES =  "ALLENTRIES";
    public static final String AUTOUPDATE =  "AUTOUPDATE";
    public static final String SITESUMMARY =  "SITESUMMARY";
    public static final String SITESUMMARYBOOL =  "SITESUMMARYBOOL";
    public static final String DIRACSERVER =  "DIRACESERVER";
    public static final String DIRACSERVER_TMP =  "DIRACESERVER_TMP";
    public static final String DIRACGROUP =  "DIRACGROUP";
    public static final String DIRACSETUP =  "DIRACSETUP";

    public static void writeBoolean(Context context, String key, boolean value) {
	getEditor(context).putBoolean(key, value).commit();
    }

    public static boolean readBoolean(Context context, String key, boolean defValue) {
	return getPreferences(context).getBoolean(key, defValue);
    }

    public static void writeInteger(Context context, String key, int value) {
	getEditor(context).putInt(key, value).commit();

    }

    public static int readInteger(Context context, String key, int defValue) {
	return getPreferences(context).getInt(key, defValue);
    }


	
    public static void writeString(Context context, String key, String value) {
	getEditor(context).putString(key, value).commit();

    }
	
    public static String readString(Context context, String key, String defValue) {
	return getPreferences(context).getString(key, defValue);
    }
	
    public static void writeFloat(Context context, String key, float value) {
	getEditor(context).putFloat(key, value).commit();
    }

    public static float readFloat(Context context, String key, float defValue) {
	return getPreferences(context).getFloat(key, defValue);
    }
	
    public static void writeLong(Context context, String key, long value) {
	getEditor(context).putLong(key, value).commit();
    }

    public static long readLong(Context context, String key, long defValue) {
	return getPreferences(context).getLong(key, defValue);
    }

    public static SharedPreferences getPreferences(Context context) {
	return context.getSharedPreferences(PREF_NAME, MODE);
    }
	
    public static Editor getEditor(Context context) {
	return getPreferences(context).edit();
    }

}
