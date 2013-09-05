package dirac.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.security.KeyChain;
import android.security.KeyChainException;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import dirac.gsonconfig.Entries;
import dirac.gsonconfig.GToken;
import dirac.gsonconfig.Jobs;
import dirac.gsonconfig.StatusSummary;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class PerformAPICall2{

    private final String TAG = this.getClass().getName();
    private Context context;
    private Activity activity;
    ProgressDialog myProgress;

    private Connectivity connect;
    private JobsDataSource datasource;

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;


    private String extraInfo;

    private ListView mylv ;
    private JobArrayAdapter myJobArrayAdapter ;
    private StateInfoArrayAdapter myStateInfoArrayAdapter ;
    private View myFooter;

    public PerformAPICall2( Context myContext){

	this.context = myContext;
	this.myProgress = new ProgressDialog(context);
	this.connect = new Connectivity(context);
	datasource = new JobsDataSource(context);
	dbHelper = new MySQLiteHelper(context);
    }


    public void SetProgressDialog(ProgressDialog myProgressDialog){
	this.myProgress = myProgressDialog;
    }

    public void SetLV(ListView myLV, StateInfoArrayAdapter myStateInfoArrayAdapter){
	this.mylv = myLV;
	this.myStateInfoArrayAdapter = myStateInfoArrayAdapter;
    }

    public void SetLV(ListView myLV, JobArrayAdapter myJobArrayAdapter){
	this.mylv = myLV;
	this.myJobArrayAdapter = myJobArrayAdapter;
    }
    public void SetLV(ListView myLV, JobArrayAdapter myJobArrayAdapter, View myfooter){
	this.mylv = myLV;
	this.myJobArrayAdapter = myJobArrayAdapter;
	this.myFooter = myfooter;		
    }

    public void SetContext(Context myContext){
	this.context = myContext;
    }


    public void SetActivity(Activity activityContext) {
	this.activity = activityContext;
		
    }

    public void SetExtraInfo(String myExtra){
	this.extraInfo = myExtra;
    }

    public void getSummaryandJobs(String myUrlSum, String myUrlJobs) {
	performApiCallSummaryAndJobs task = new performApiCallSummaryAndJobs();
	task.execute(new String[] { myUrlSum, myUrlJobs});	
    }
    
    public void getSummary(String myUrl) {
	performApiCallSummary task = new performApiCallSummary();
	task.execute(new String[] { myUrl });	
    }
    
    
    public void getJobs(String myUrl) {
	performApiCall task = new performApiCall();
	task.execute(new String[] { myUrl });	
    }
    

    public void getHistory(String myUrl) {
	performApiCallStats task = new performApiCallStats();
	task.execute(new String[] { myUrl });
    }

    public void getJDL(String myUrl) {
	performApiCallJDL task = new performApiCallJDL();
	task.execute(new String[] { myUrl });	
    }
    public void getNewJobs(String myUrl) {
	performApiCallAddNew task = new performApiCallAddNew();
	task.execute(new String[] { myUrl });	
    }

    public void killJobs(String myUrl) {
	performApiCallKill task = new performApiCallKill();
	task.execute(new String[] { myUrl });
    }
    public void deleteJobs(String myUrl) {
	performApiCallDel task = new performApiCallDel();
	task.execute(new String[] { myUrl });
    }
    

    public void getAccess(String myUrl) {
	OAuth2RequestTokenTask task = new OAuth2RequestTokenTask();
	task.execute(new String[] { myUrl });
    }
    


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private HttpClient getMyHttpClient(Context mycontext)  {

		
	String alias = CacheHelper.readString(this.context, CacheHelper.CERTALIAS, "");	

	
	X509Certificate[] certificateChain = null;
	try {
	    certificateChain = KeyChain.getCertificateChain(this.context, alias);
	} catch (KeyChainException e3) {
	    // TODO Auto-generated catch block
	    e3.printStackTrace();

	} catch (InterruptedException e3) {
	    // TODO Auto-generated catch block
	    e3.printStackTrace();
	}
	// TODO Auto-generated catch block
	PrivateKey privateKey2 = null;
	try {
	    privateKey2 = KeyChain.getPrivateKey(this.context, alias);
	} catch (KeyChainException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} 
	//	Log.i("al 2 ", this.alias);	

	KeyStore pkcs12KeyStore = null;
	try {
	    pkcs12KeyStore = KeyStore.getInstance("PKCS12");
	} catch (KeyStoreException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} 
	try {
	    pkcs12KeyStore.load(null, null);
	} catch (NoSuchAlgorithmException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (CertificateException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} 
	try {
	    pkcs12KeyStore.setKeyEntry(alias, privateKey2, null, certificateChain);
	} catch (KeyStoreException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} 

		
	return CustomSSLSocketFactory.getNewHttpClient(context,pkcs12KeyStore,pkcs12KeyStore); 		
	//return consumer;
    }
    
    
    
    
    
    
    
    private String doGet(String url,HttpClient myHttpClient) throws Exception {

	try{

	    String mytoken = CacheHelper.readString(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN, "");
	    //CacheHelper.writeLong(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_EXPIRES_TIME, newtime);

			
	    HttpGet myjobs =	new HttpGet(url);
	    myjobs.setHeader("Authorization", "bearer "+mytoken);

	    HttpResponse result2jobs = null;
	    try {
	    	if(connect.isOnline()){
		    result2jobs = myHttpClient.execute(myjobs);
	    	}
	    } catch (ClientProtocolException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	    } catch (IOException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	    }					
	    InputStream data2jobs = null;
	    try {
		data2jobs = result2jobs.getEntity().getContent();
	    } catch (IllegalStateException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	    } catch (IOException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	    }		
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data2jobs));
		
	    String responeLine;
	    StringBuilder responseBuilder = new StringBuilder();
	    while ((responeLine = bufferedReader.readLine()) != null) {
		responseBuilder.append(responeLine);
	    }
	    

	    Calendar c = Calendar.getInstance(); 
	    long mseconds = c.getTimeInMillis();
	    CacheHelper.writeLong(context, CacheHelper.LASTUPDATE, mseconds);
	    
	    

	    return responseBuilder.toString();
	}catch (Exception e) {
	    Log.e(TAG, "Error executing request",e);
	    //	textView.setText("Error retrieving contacts : " + jsonOutput);
	    return "";

	}
    }	

    private String doPost(String url,HttpClient myHttpClient, Map<String,?> params) throws Exception {

	

	String mytoken = CacheHelper.readString(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN, "");
	//CacheHelper.writeLong(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_EXPIRES_TIME, newtime);

	Log.i(url, url);
			
	HttpPost post =	new HttpPost(url);

	if(params == null)  {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);    
	    nameValuePairs.add(new BasicNameValuePair("access_token",mytoken));	
	    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	}else{

	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(params.size()+1);    
	    nameValuePairs.add(new BasicNameValuePair("access_token",mytoken));	
	    for(Iterator<?> it = params.entrySet().iterator(); it.hasNext(); ) {
	        @SuppressWarnings("unchecked")
		    Map.Entry<String, ?> entry = (Entry<String, ?>) it.next();
	     	nameValuePairs.add(new BasicNameValuePair(entry.getKey(),(String) entry.getValue()));
	    }
	    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	} 

	    
	    
	HttpResponse result2jobs = null;
	try {
	    if(connect.isOnline()){
		result2jobs = myHttpClient.execute(post);
	    }
	} catch (ClientProtocolException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	} catch (IOException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	}					
	InputStream data2jobs = null;
	try {
	    data2jobs = result2jobs.getEntity().getContent();
	} catch (IllegalStateException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	} catch (IOException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	}		
	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data2jobs));
		
	String responeLine;
	StringBuilder responseBuilder = new StringBuilder();
	while ((responeLine = bufferedReader.readLine()) != null) {
	    responseBuilder.append(responeLine);
	}
	    

	Calendar c = Calendar.getInstance(); 
	long mseconds = c.getTimeInMillis();
	CacheHelper.writeLong(context, CacheHelper.LASTUPDATE, mseconds);
	    
	    
        Log.i("del", responseBuilder.toString());	
	return responseBuilder.toString();
	    
	    
	    

    }	


    
    
    

    
    
    private String getToken(String url,HttpClient myHttpClient) throws Exception {

	
		
	Log.d(TAG, "Retrieving token from DIRAC servers");
	Log.d(TAG, "url "+Constants.REQUEST_TOKEN);
	
	Log.d(TAG,	CacheHelper.readString(context,CacheHelper.SHPREF_GROUP, "No Groups"));


	//"""OAUTH 2 """" implemetantion....
	String formDataServiceUrl = Constants.REQUEST_TOKEN;
	HttpPost post = new HttpPost(formDataServiceUrl);
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
	nameValuePairs.add(new BasicNameValuePair("grant_type", "client_credentials"));
	nameValuePairs.add(new BasicNameValuePair("group", "lhcb_user"));
	nameValuePairs.add(new BasicNameValuePair("setup", "LHCb-Production"));
	try {
	    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	} catch (UnsupportedEncodingException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	}

		
	HttpResponse result22 = null;
	try {
	    result22 = myHttpClient.execute(post);
	} catch (ClientProtocolException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	} catch (IOException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	}	
	InputStream data23 = null;
	try {
	    data23 = result22.getEntity().getContent();
	} catch (IllegalStateException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	} catch (IOException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	}	
	BufferedReader token_ret = new BufferedReader(new InputStreamReader(data23));		
				
	StringBuilder sb3 = new StringBuilder();

	String line = null;
	try {
	    while ((line = token_ret.readLine()) != null) {
		sb3.append(line + "\n");
	    }
	} catch (IOException e2) {
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	}
				
				
	String myline = sb3.toString();
	GToken  mytoken;
	Gson gson = new Gson();

	mytoken = gson.fromJson(myline, GToken.class);
	Calendar c = Calendar.getInstance(); 
	long mseconds = c.getTimeInMillis();
	long newtime = mseconds+1000*(mytoken.getExpires_in() - 120); //remove 2 minutes to be sure to be granted on the server side
				
				
	CacheHelper.writeString(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN, mytoken.getToken());
	CacheHelper.writeLong(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_EXPIRES_TIME, newtime);
				

				
	return  mytoken.getToken();
	
    }
    
    
    
    
    
    
    
    
    
    
    
    public class OAuth2RequestTokenTask extends AsyncTask<String, String, String> {

	
	protected void onPreExecute() {
	    myProgress =	ProgressDialog.show(context, "", "Getting an access token. Please wait...", true);
		
	}	
	
	
		
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	    @Override
	    protected  String doInBackground(String... urls) {


	    for (String url : urls) {

		try {
		    getToken(url,getMyHttpClient(context));

		}  catch (Exception e) {
		    Log.e(TAG, "Error during OAUth retrieve request token", e);
		}
	    }
	    return "";
	}
	



	protected void onPostExecute(String result) {

	    if(myProgress.isShowing()){
	    	myProgress.dismiss();
	    }
	}

    }










    public class performApiCall extends AsyncTask<String, String, String> {


	protected void onPreExecute() {		
	    if(connect.isGranted()){
		myProgress =	ProgressDialog.show(context, "", "Downloading your jobs. Please wait...\nHow is going your analysis?", true);
	    }else{
		if(CacheHelper.readBoolean(context,  CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE, false)){
		    myProgress =	ProgressDialog.show(context, "", "Auto-Update: Getting an access token. Please wait...", true);			
				
		}else{
		    myProgress =	ProgressDialog.show(context, "", "Please set Auto-Update or grant again your app", true);
		}
	    }
		
	}	
		
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	    protected String doInBackground(String... urls) {
	    String response = "";
	    for (String url : urls) {

		try {
		    HttpClient myclient = getMyHttpClient(context);
		    if(CacheHelper.readBoolean(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE,false)){
			if(connect.isGranted() == false){
			    getToken(Constants.REQUEST_TOKEN,myclient);
			}	
		    }
		    if(connect.isGranted()){
			publishProgress("Downloading your jobs. Please wait...\nHow is going your analysis?");
			response = doGet(url,myclient);
		    }

		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }

	    return response;

	}

	protected void onProgressUpdate(String... ui) {
	    myProgress.setMessage(ui[0]);

	}

	@SuppressLint("SimpleDateFormat")
	    protected void onPostExecute(String result) {

	    if(result != ""){
		datasource.open();
		database = dbHelper.getWritableDatabase(); 
		dbHelper.deleteTable(database, MySQLiteHelper.DIRAC_JOBS);
		Gson gson = new Gson();

		Jobs  jobs = gson.fromJson(result, Jobs.class);
		datasource.parse(jobs);	
	
		Entries entries = gson.fromJson(result, Entries.class);
		CacheHelper.writeString(context, CacheHelper.ALLENTRIES, entries.getEntries());

			
		final dirac.android.Status[] map = datasource.getLastUpdate2(); 
		if( map.length != 0){		
		    if(map[0]!= null){
			myStateInfoArrayAdapter.setMap(map);
			mylv.setAdapter(myStateInfoArrayAdapter);
		
	    

			int[] TextPos = {R.id.tChecking,R.id.tCompleted,R.id.tDone,R.id.tFailed,R.id.tKilled,R.id.tMatched,R.id.tReceived,R.id.tRunning,R.id.tStaging,R.id.tStalled,R.id.tWaiting,R.id.tDeleted};
			int All = 0;

			for(int i = 0; i<TextPos.length; i++){
			    float F = 0;
			    TextView T1 = (TextView)activity.findViewById(TextPos[i]);
			    T1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, F));
			}
			Integer totStatus = map.length;

			for(int i = 0; i<totStatus; i++){
			    float F = Float.parseFloat(map[i].number());	

			    TextView T1 = (TextView)activity.findViewById(TextPos[map[i].get(map[i].name())]);
			    T1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, F));
			    All = All + (int) F;
			}


			TextView Total = (TextView)activity.findViewById(R.id.nbtotaljob);
			Total.setText("Jobs in Dirac: "+entries.getEntries());


			TextView LU = (TextView)activity.findViewById(R.id.lastup);

			String testtime = "never";
			Long exptime = CacheHelper.readLong(context, CacheHelper.LASTUPDATE, -1);
			if(exptime != -1){
			    Long ltime = exptime;
			    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy");
			    testtime = sdf.format(ltime);
			}
		
			LU.setText(testtime);	
			database.close();	
			datasource.close();
		    }
	
		}
	    }
	    if(myProgress.isShowing())
		myProgress.dismiss();
	}	
    }










    public class performApiCallSummary extends AsyncTask<String, String, String> {


	protected void onPreExecute() {		
	    if(connect.isGranted()){
		myProgress =	ProgressDialog.show(context, "", "Downloading the summary of your jobs. Please wait...\nHow is going your analysis?", true);
	    }else{
		if(CacheHelper.readBoolean(context,  CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE, false)){
		    myProgress =	ProgressDialog.show(context, "", "Auto-Update: Getting an access token. Please wait...", true);			
				
		}else{
		    myProgress =	ProgressDialog.show(context, "", "Please set Auto-Update or grant again your app", true);
		}
	    }
		
	}	
		
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	    protected String doInBackground(String... urls) {
	    String response = "";
	    for (String url : urls) {

		try {
		    HttpClient myclient = getMyHttpClient(context);
		    if(CacheHelper.readBoolean(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE,false)){
			if(connect.isGranted() == false){
			    getToken(Constants.REQUEST_TOKEN,myclient);
			}	
		    }
		    if(connect.isGranted()){
			publishProgress("Downloading the summary of your jobs. Please wait...\nHow is going your analysis?");
			response = doGet(url,myclient);
		    }

		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }

	    return response;

	}

	protected void onProgressUpdate(String... ui) {
	    myProgress.setMessage(ui[0]);

	}

	@SuppressLint("SimpleDateFormat")
	    protected void onPostExecute(String result) {

	    if(result != ""){
	    	
	    	

		Gson gson = new Gson();
		datasource.open();
		database = dbHelper.getWritableDatabase();					

				


		StatusSummary		summary = gson.fromJson(result, StatusSummary.class);


		dbHelper.deleteTable(database, MySQLiteHelper.DIRAC_JOBS);

		datasource.parseSummary(summary);	

						
		final dirac.android.Status[] map = datasource.getLastUpdate(); 
		if( map.length != 0){		
		    if(map[0]!= null){
			myStateInfoArrayAdapter.setMap(map);
			mylv.setAdapter(myStateInfoArrayAdapter);
						
					    

			int[] TextPos = {R.id.tChecking,R.id.tCompleted,R.id.tDone,R.id.tFailed,R.id.tKilled,R.id.tMatched,R.id.tReceived,R.id.tRunning,R.id.tStaging,R.id.tStalled,R.id.tWaiting,R.id.tDeleted};
			Integer All = 0;

			for(int i = 0; i<TextPos.length; i++){
			    float F = 0;
			    TextView T1 = (TextView)activity.findViewById(TextPos[i]);
			    T1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, F));
			}
			Integer totStatus = map.length;

			for(int i = 0; i<totStatus; i++){
			    float F = Float.parseFloat(map[i].number());	

			    TextView T1 = (TextView)activity.findViewById(TextPos[map[i].get(map[i].name())]);
			    T1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, F));
			    All = All + (int) F;
			}


			TextView Total = (TextView)activity.findViewById(R.id.nbtotaljob);
			Total.setText("Jobs in Dirac: "+All.toString());


			TextView LU = (TextView)activity.findViewById(R.id.lastup);

			String testtime = "never";
			Long exptime = CacheHelper.readLong(context, CacheHelper.LASTUPDATE, -1);
			if(exptime != -1){
			    Long ltime = exptime;
			    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy");
			    testtime = sdf.format(ltime);
			}
						
			LU.setText(testtime);	
			database.close();	
			datasource.close();
		    }
				
		}
	    }
	    if(myProgress.isShowing())
		myProgress.dismiss();
	}	
    }

 









    public class performApiCallSummaryAndJobs extends AsyncTask<String, String, ArrayList<String>> {


	protected void onPreExecute() {		
	    if(connect.isGranted()){
		myProgress =	ProgressDialog.show(context, "", "Downloading the summary and your last jobs. Please wait...", true);
	    }else{
		if(CacheHelper.readBoolean(context,  CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE, false)){
		    myProgress =	ProgressDialog.show(context, "", "Auto-Update: Getting an access token. Please wait...", true);			
				
		}else{
		    myProgress =	ProgressDialog.show(context, "", "Please set Auto-Update or grant again your app", true);
		}
	    }
		
	}	
		
	    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	    protected ArrayList<String> doInBackground(String... urls) {
	    ArrayList<String> response = new ArrayList<String>(2);
	    String r1 = "";
	    String r2 = "";
	    try {
		HttpClient myclient = getMyHttpClient(context);
		if(CacheHelper.readBoolean(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE,false)){
		    if(connect.isGranted() == false){
			getToken(Constants.REQUEST_TOKEN,myclient);
		    }	
		}
		if(connect.isGranted()){
		    publishProgress("Downloading the summary of your jobs. Please wait...");
		    r1= doGet(urls[0],myclient);
		    publishProgress("Downloading your last jobs. Please wait...\nHow is going your analysis?");
		    r2= doGet(urls[1],myclient);
		}

	    } catch (Exception e) {
		e.printStackTrace();
		
	    }

	    response.add(0,r1);
	    response.add(1,r2);
	    return response;

	}

	protected void onProgressUpdate(String... ui) {
	    myProgress.setMessage(ui[0]);

	}

	@SuppressLint("SimpleDateFormat")
	    protected void onPostExecute(ArrayList<String> result) {

	    datasource.open();
	    database = dbHelper.getWritableDatabase();					
	    Gson gson = new Gson();

	    
	    
	    if(result.get(1)!= ""){
	    
		dbHelper.deleteTable(database, MySQLiteHelper.DIRAC_JOBS);
		Jobs  jobs = gson.fromJson(result.get(1), Jobs.class);
		datasource.parse(jobs);						
										
	    }   
	    
	    
	    if(result.get(0) != ""){

		StatusSummary		summary = gson.fromJson(result.get(0), StatusSummary.class);


		dbHelper.deleteTable(database, MySQLiteHelper.DIRAC_STATS);

		datasource.parseSummary(summary);	
	
		final dirac.android.Status[] map = datasource.getLastUpdate(); 
		if( map.length != 0){		
		    if(map[0]!= null){
			myStateInfoArrayAdapter.setMap(map);
			mylv.setAdapter(myStateInfoArrayAdapter);
						
					   
			int[] TextPos = {R.id.tRunning, 
					R.id.tWaiting,
					R.id.tCompleted,
					R.id.tDone,
					R.id.tFailed,
					R.id.tMatched,
					R.id.tReceived,
					R.id.tChecking,
					R.id.tStaging,
					R.id.tStalled,
					R.id.tKilled,
					R.id.tDeleted};
			Integer All = 0;

			for(int i = 0; i<TextPos.length; i++){
			    float F = 0;
			    TextView T1 = (TextView)activity.findViewById(TextPos[i]);
			    T1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, F));
			}
			Integer totStatus = map.length;

			for(int i = 0; i<totStatus; i++){
			    float F = Float.parseFloat(map[i].number());	

			    TextView T1 = (TextView)activity.findViewById(TextPos[map[i].get(map[i].name())]);
			    T1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, F));
			    All = All + (int) F;
			}


			TextView Total = (TextView)activity.findViewById(R.id.nbtotaljob);
			Total.setText("Jobs in Dirac: "+All.toString());


			TextView LU = (TextView)activity.findViewById(R.id.lastup);

			String testtime = "never";
			Long exptime = CacheHelper.readLong(context, CacheHelper.LASTUPDATE, -1);
			if(exptime != -1){
			    Long ltime = exptime;
			    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy");
			    testtime = sdf.format(ltime);
			}
						
			LU.setText(testtime);	
		    }
						
		}
	    }


	    database.close();	
	    datasource.close();
				
	    if(myProgress.isShowing())
		myProgress.dismiss();
	}	
    }

 
 
 
 
  

    public class performApiCallStats extends AsyncTask<String, String, String > {

	protected void onPreExecute() {
	    if(connect.isGranted()){
		myProgress =	ProgressDialog.show(context, "", "Downloading the history. Please wait...", true);
	    }else{
		if(CacheHelper.readBoolean(context, CacheHelper.AUTOUPDATE, false)){
		    myProgress =	ProgressDialog.show(context, "", "Auto-Update: Getting an access token. Please wait...", true);			
					
		}else{
		    myProgress =	ProgressDialog.show(context, "", "Please set Auto-Update or grant again your app", true);
		}
	    }
	}
		
	protected String doInBackground(String... urls) {
	    String response = "";
	    Intent	StatsIntent = null;
	    for (String url : urls) {

		try {

			
			
		    HttpClient myclient = getMyHttpClient(context);
		    if(CacheHelper.readBoolean(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE,false)){
			if(connect.isGranted() == false){
			    getToken(Constants.REQUEST_TOKEN,myclient);
			}	
		    }
		    if(connect.isGranted()){
			publishProgress("Downloading the history (currenty only for allOwner). Please wait...");
			response = doGet(url,myclient);
			StatsIntent = PerformAPICall2.this.execute(context, response);

		    }

			


		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	    if(StatsIntent!=null)
		context.startActivity(StatsIntent);

	    try {
		Thread.sleep(2);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    return response;

	}

	protected void onProgressUpdate(String... ui) {
	    myProgress.setMessage(ui[0]);

	}

	

	

	protected void onPostExecute(String result) {

	    if(myProgress.isShowing())
		myProgress.dismiss();

	}

    }
	
    
    
    
    
    
    
    
    
    
    public class performApiCallDel extends AsyncTask<String, String, String > {

    	protected void onPreExecute() {
	    if(connect.isGranted()){
		myProgress =	ProgressDialog.show(context, "", "Deleting jobs. Please wait...", true);
	    }else{
		if(CacheHelper.readBoolean(context, CacheHelper.AUTOUPDATE, false)){
		    myProgress =	ProgressDialog.show(context, "", "Auto-Update: Getting an access token. Please wait...", true);			
    					
		}else{
		    myProgress =	ProgressDialog.show(context, "", "Please set Auto-Update or grant again your app", true);
		}
	    }
    	}
    			
    		
    	protected String doInBackground(String... urls) {
    	    String response = "";

	    try {
    			
    			
		HttpClient myclient = getMyHttpClient(context);
		if(CacheHelper.readBoolean(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE,false)){
		    if(connect.isGranted() == false){
			getToken(Constants.REQUEST_TOKEN,myclient);
		    }	
		}
		if(connect.isGranted()){
		    publishProgress("Deleting jobs. Please wait...");
		    Map<String,?> params = null;

    	    	    for (String url : urls) {
			response = doPost(url,myclient,params);
		    }
		}

	    } catch (Exception e) {
		e.printStackTrace();
	    }
    	    
    	    return response;

    	}
    	
    	
    	protected void onProgressUpdate(String... ui) {
    	    myProgress.setMessage(ui[0]);

    	}

    	

    	protected void onPostExecute(String result) {

    	    if(myProgress.isShowing())
    		myProgress.dismiss();

    	}

    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public class performApiCallKill extends AsyncTask<String, String, String > {

    	protected void onPreExecute() {
	    if(connect.isGranted()){
		myProgress =	ProgressDialog.show(context, "", "Killing jobs. Please wait...", true);
	    }else{
		if(CacheHelper.readBoolean(context, CacheHelper.AUTOUPDATE, false)){
		    myProgress =	ProgressDialog.show(context, "", "Auto-Update: Getting an access token. Please wait...", true);			
    					
		}else{
		    myProgress =	ProgressDialog.show(context, "", "Please set Auto-Update or grant again your app", true);
		}
	    }
    	}
    			
    		
    	protected String doInBackground(String... urls) {
    	    String response = "";

	    try {
    			
    			
		HttpClient myclient = getMyHttpClient(context);
		if(CacheHelper.readBoolean(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE,false)){
		    if(connect.isGranted() == false){
			getToken(Constants.REQUEST_TOKEN,myclient);
		    }	
		}
		if(connect.isGranted()){
		    publishProgress("Killing jobs. Please wait...");
		    Map<String,?> params = null;

    	    	    for (String url : urls) {
			response = doPost(url,myclient,params);
    	    	    }
		}


	    } catch (Exception e) {
		e.printStackTrace();
	    }
    	    
    	    return response;

    	}
    	
    	
    	protected void onProgressUpdate(String... ui) {
    	    myProgress.setMessage(ui[0]);

    	}

    	

    	protected void onPostExecute(String result) {

    	    if(myProgress.isShowing())
    		myProgress.dismiss();

    	}

    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    public class performApiCallAddNew extends AsyncTask<String, String, String > {

	protected void onPreExecute() {
	    if(connect.isGranted()){
		myProgress =	ProgressDialog.show(context, "", "Downloading new jobs. Please wait...", true);
	    }else{
		if(CacheHelper.readBoolean(context, CacheHelper.AUTOUPDATE, false)){
		    myProgress =	ProgressDialog.show(context, "", "Auto-Update: Getting an access token. Please wait...", true);			
					
		}else{
		    myProgress =	ProgressDialog.show(context, "", "Please set Auto-Update or grant again your app", true);
		}
	    }
	}
		
	protected String doInBackground(String... urls) {
	    String response = "";
	    for (String url : urls) {

		try {
			
		    HttpClient myclient = getMyHttpClient(context);
		    if(CacheHelper.readBoolean(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE,false)){
			if(connect.isGranted() == false){
			    getToken(Constants.REQUEST_TOKEN,myclient);
			}	
		    }
		    if(connect.isGranted()){
			publishProgress("Downloading new jobs, Please wait...");
			response = doGet(url,myclient);

		    }

		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	    return response;

	}

	protected void onProgressUpdate(String... ui) {
	    myProgress.setMessage(ui[0]);

	}

	

	protected void onPostExecute(String result) {

	    datasource.open();

	    database = dbHelper.getWritableDatabase(); 
	    Gson gson = new Gson();

	    Jobs  jobs = gson.fromJson(result, Jobs.class);
	    datasource.parse(jobs);	
	    datasource.close();
	    database.close();	
	    Collections.reverse(jobs.getJobs());

	    //	for(int k = 0; k < jobs.getJobs().size(); k++)
	    //  adapter.addAll(jobs.getJobs());
	    for(int k = 0; k < jobs.getJobs().size(); k++)
		myJobArrayAdapter.add(jobs.getJobs().get(k));
			

	    int mymax = 10;
	    mymax = CacheHelper.readInteger(context, CacheHelper.NMAXBJOBS, mymax);	



	    if(jobs.getJobs().size() < mymax)
		mylv.removeFooterView(myFooter);



	    if(myProgress.isShowing())
		myProgress.dismiss();
			
	}

    }


	
    public class performApiCallJDL extends AsyncTask<String, String, String > {


	
	protected void onPreExecute() {			
	    if(connect.isGranted()){
		myProgress =	ProgressDialog.show(context, "", "Downloading the JDL. Please wait...", true);
	    }else{
		if(CacheHelper.readBoolean(context, CacheHelper.AUTOUPDATE, false)){
		    myProgress =	ProgressDialog.show(context, "", "Auto-Update: Getting an access token. Please wait...", true);			
					
		}else{
		    myProgress =	ProgressDialog.show(context, "", "Please set Auto-Update or grant again your app", true);
		}
	    }
	}
		

     
		
	protected String doInBackground(String... urls) {
	    String response = "";
	    for (String url : urls) {
		try {

			
			
		    HttpClient myclient = getMyHttpClient(context);
		    if(CacheHelper.readBoolean(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE,false)){
			if(connect.isGranted() == false){
			    getToken(Constants.REQUEST_TOKEN,myclient);
			}	
		    }
		    if(connect.isGranted()){
			publishProgress("Downloading the JDL, Please wait...");
			response = doGet(url,myclient);

		    }

		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	    return response;

	}

	protected void onProgressUpdate(String... ui) {
	    myProgress.setMessage(ui[0]);

	}

	

	protected void onPostExecute(String result) {

	    if(myProgress.isShowing())
		myProgress.dismiss();
			
	    Intent myIntent = new Intent(context, JobDescriptionActivity.class);			


	    myIntent.putExtra("description",result);
	    myIntent.putExtra("jid", extraInfo);
	    context.startActivity(myIntent);
			

	}

    }






    public Intent execute(Context context, String result){

	XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

	JSONObject jObject1;

	try {
	    jObject1 = new JSONObject(result);
	    JSONObject menuObject = jObject1.getJSONObject("data");
	    renderer.setAxisTitleTextSize(30);
	    renderer.setChartTitleTextSize(30);
	    renderer.setLabelsTextSize(24);
	    renderer.setLegendTextSize(40);
	    renderer.setMargins(new int[] {70, 70, 70,70});
	    renderer.setAxesColor(Color.DKGRAY);
	    renderer.setLabelsColor(Color.LTGRAY);
	    renderer.setAntialiasing(true);
	    renderer.setShowGridX(true);
	    renderer.setYAxisMin(0);
			
			
	    //	ArrayList<String[]> list = datasource.getAllJobIDsOfSatusTime();
	    //	double[] Range = {(double) (list.size()-10),(double) list.size()};
	    //renderer.setRange(Range);

	    String[] status = Status.PossibleStatus;
	    int[] Colors = Status.ColorStatus;
	    XYSeriesRenderer r;


	    for (int i = 0; i < status.length - 1 ; i++) {

		JSONObject Status = null;
		try{
		    Status = menuObject.getJSONObject(status[i]);
		}catch (Exception e1) {
		    continue;
		}


		JSONArray StatusN = Status.names();



		//	System.out.println(i);
		//	System.out.println(status[i]);
		//	System.out.println(Colors[i]);
		XYSeries series = new XYSeries("");
		series.setTitle(status[i]);
		r = new XYSeriesRenderer();
		r.setColor(context.getResources().getColor(Colors[i]));
		r.setLineWidth(4);
		renderer.addSeriesRenderer(r);

		for (int k = 0; k < StatusN.length(); k++) {

		    String sdate = StatusN.getString(k);
		    java.util.Date time=new java.util.Date(Long.parseLong(sdate)*1000);
		    double  nor = Math.log10(Float.parseFloat((Status.getString(sdate))));

		    series.add(time.getTime(), nor);					
		}	

		dataset.addSeries(series);
	    }


	    //renderer.setPanLimits(new double[]{0,0,-1,100});


	} catch (JSONException e1) {
	    e1.printStackTrace();
	} 
		
	return ChartFactory.getTimeChartIntent(context,dataset, renderer,  null );
	//	return ChartFactory.getBarChartIntent(context,dataset, renderer,  Type.STACKED );
    }





}
