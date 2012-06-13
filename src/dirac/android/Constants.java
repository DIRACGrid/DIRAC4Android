package dirac.android;

public class Constants {

	public static final String CONSUMER_KEY 	= "be99ba61cbe5fcba8d631d0135ea6294";
	public static final String CONSUMER_SECRET 	= "7d86c156217881b4dae0d046ade697c4";
	
	public static final String ACCESS_TOKEN = "26b16fc514e0d34b2a7e495e38ad2a58";
	public static final String ACCESS_TOKEN_SECRET = "367df4178459665747eeb2b3e2096b64";
	public static final String OAUTH_TOKEN 	= "5c45983c77638a1ec4d1e0e718e32c4c";
	public static final String OAUTH_SIGN 	= "7wga%2B9BZAYJ58jpDlfus78ysook%3D";
	public static final String OAUTH_VERIF	= "4e7a14bf95319159c0073f338933ffb1";

//	public static final String SCOPE 			= "https://www.google.com/m8/feeds/";
	public static final String REQUEST_URL 		= "http://lhcb01.ecm.ub.es:9354/oauth/request_token";
	public static final String ACCESS_URL 		= "http://lhcb01.ecm.ub.es:9354/oauth/access_token";  
	public static final String AUTHORIZE_URL 	= "http://lhcb01.ecm.ub.es:9354/oauth/authorize";
	public static final String API_REQUEST 		= "http://lhcb01.ecm.ub.es:9354/jobs?allOwners=true&maxJobs=10000";

	public static final String ENCODING 		= "UTF-8";

	public static final String	OAUTH_CALLBACK_SCHEME	= "x-oauthflow";
	public static final String	OAUTH_CALLBACK_HOST		= "callback";
	public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

}