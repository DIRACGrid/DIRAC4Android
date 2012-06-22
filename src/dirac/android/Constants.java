package dirac.android;

public class Constants {

	public static final String CONSUMER_KEY 	= "be99ba61cbe5fcba8d631d0135ea6294";
	public static final String CONSUMER_SECRET 	= "7d86c156217881b4dae0d046ade697c4";
	
	public static final String BASE_URL 		= "http://lhcb01.ecm.ub.es:9354";

	public static final String REQUEST_URL 		= BASE_URL+"/oauth/request_token";
	public static final String ACCESS_URL 		= BASE_URL+"/oauth/access_token";  
	public static final String AUTHORIZE_URL 	= BASE_URL+"/oauth/authorize";

	public static final String API_JOBS		    = BASE_URL+"/jobs";
	public static final String API_HISTORY		= API_JOBS+"/history";
	public static final String API_SUMMARY 		= API_JOBS+"/summary";
	public static final String API_RESCHEDULE 	= API_JOBS+"/reschedule/";

	public static final String ENCODING 		= "UTF-8";

	
	//SCHEME SHOULD BE: x-dirac-android-activity
	public static final String	OAUTH_CALLBACK_SCHEME	= "http";
	public static final String	OAUTH_CALLBACK_HOST		= "callback";
	public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME+"://"+OAUTH_CALLBACK_HOST;

}