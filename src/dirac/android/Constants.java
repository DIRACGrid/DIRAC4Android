package dirac.android;

public class Constants {

	public static final String CONSUMER_KEY 	= "be99ba61cbe5fcba8d631d0135ea6294";
	public static final String CONSUMER_SECRET 	= "7d86c156217881b4dae0d046ade697c4";
	
	public static final String ACCESS_TOKEN = "0949d59bb3785cf626fbb11b3dde4ea2";
	public static final String ACCESS_TOKEN_SECRET = "aef6202bf4e6ba26a0db7735a213e75d";
	public static final String OAUTH_TOKEN 	= "5c45983c77638a1ec4d1e0e718e32c4c";
	public static final String OAUTH_SIGN 	= "7wga%2B9BZAYJ58jpDlfus78ysook%3D";
	public static final String OAUTH_VERIF	= "4e7a14bf95319159c0073f338933ffb1";

//	public static final String SCOPE 			= "https://www.google.com/m8/feeds/";
	public static final String BASE_URL 		= "http://lhcb01.ecm.ub.es:9354";

	public static final String REQUEST_URL 		= BASE_URL+"/oauth/request_token";
	public static final String ACCESS_URL 		= BASE_URL+"/oauth/access_token";  
	public static final String AUTHORIZE_URL 	= BASE_URL+"/oauth/authorize";
	public static final String API_ALLUSERS 	= BASE_URL+"/jobs?maxJobs=2000&status=Done,Waiting";
			//"allOwners=true&maxJobs=11122";
	public static final String API_MYJOBS 		= BASE_URL+"/jobs?maxJobs=10000&status=Done";
	public static final String API_JOBS		    = BASE_URL+"/jobs";
	public static final String API_HISTORY		= API_JOBS+"/history";
	public static final String API_SUMMARY 		= API_JOBS+"/summary";

	public static final String ENCODING 		= "UTF-8";

	public static final String	OAUTH_CALLBACK_SCHEME	= "test";
	public static final String	OAUTH_CALLBACK_HOST		= "yeah";
	public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

}