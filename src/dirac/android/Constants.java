package dirac.android;

import java.util.Arrays;
import java.util.List;

public class Constants{
    
    public final static  String BASE_URL 		= "https://"+"lhcb01.ecm.ub.es";
;
    public  final static String PORT 		    = "9354";

    public  static final String BASE_URL_PORT    = BASE_URL+":"+PORT;

    public static final String ENCODING 		= "UTF-8";


    public static final String REQUEST_GROUPS 		= "/oauth2/groups";
    public static final String REQUEST_SETUPS 		= "/oauth2/setups";
    public static final String REQUEST_TOKEN 		= "/oauth2/token";
    public static final String AUTHORIZE_URL 	    = "/oauth2/auth";
	
    public static final String REQUEST_JOBS 		= "/jobs";
    public static final String REQUEST_HISTORY		= REQUEST_JOBS+"/history";
    public static final String REQUEST_SUMMARY 		= REQUEST_JOBS+"/summary";
    public static final String REQUEST_RESCHEDULE 	= REQUEST_JOBS+"/reschedule/";
    public static final List<String> SERVERS 	    = Arrays.asList("lhcb01.ecm.ub.es:9354",
    																"test.dirac.org:4444");

}
