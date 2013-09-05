package dirac.android;

public class Constants {

 
	
    public static final String BASE_URL 		= "https://lhcb01.ecm.ub.es";
    public static final String PORT 		    = "9354";

    public static final String BASE_URL_PORT    = BASE_URL+":"+PORT;

    public static final String ENCODING 		= "UTF-8";


    public static final String REQUEST_GROUPS 		= BASE_URL_PORT+"/oauth2/groups";
    public static final String REQUEST_SETUPS 		= BASE_URL_PORT+"/oauth2/setups";
    public static final String REQUEST_TOKEN 		= BASE_URL_PORT+"/oauth2/token";
    public static final String AUTHORIZE_URL 	    = BASE_URL_PORT+"/oauth2/auth";
	
    public static final String REQUEST_JOBS 		= BASE_URL_PORT+"/jobs";
    public static final String REQUEST_HISTORY		= REQUEST_JOBS+"/history";
    public static final String REQUEST_SUMMARY 		= REQUEST_JOBS+"/summary";
    public static final String REQUEST_RESCHEDULE 	= REQUEST_JOBS+"/reschedule/";

}
