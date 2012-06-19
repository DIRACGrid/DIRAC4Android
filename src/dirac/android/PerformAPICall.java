package dirac.android;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

public class PerformAPICall {

	private final String TAG = this.getClass().getName();
	private SharedPreferences prefs;
	private Context context;
	private int myProgress;
	private ProgressBar PB;

	private JobsDataSource datasource;

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private Object Parser;


	public PerformAPICall(Context mycontext, SharedPreferences myprefs){
		this.context = mycontext;
		this.prefs = myprefs;	
	}
	public void SetProgressBar(ProgressBar myPB){
		this.PB = myPB;
	}

	public void SetClassParser(Object obj){
		this.Parser = obj;
	}

	public void performApiCall(String myUrl, String Parser) {

		performApiCall task = new performApiCall();
		task.execute(new String[] { myUrl });

	}




	public String performApiCall(String myUrl) {

		String jsonOutput = "";
		try {  	      	

			try{
				jsonOutput = doGet(myUrl,getConsumer(this.prefs));

			}catch (Exception e) {
				Toast.makeText(context, "ERROR CONNECTIUON", Toast.LENGTH_LONG).show();			//	textView.setText("Error retrieving contacts : " + jsonOutput);.show
			}

		} catch (Exception e) {
			Log.e(TAG, "Error executing request",e);
		}
		return jsonOutput;
	}



	private OAuthConsumer getConsumer(SharedPreferences prefs) {

		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");


		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		consumer.setTokenWithSecret(token, secret);
		//	Log.d("getConsumer",consumer.toString());
		return consumer;
	}

	private String doGet(String url,OAuthConsumer consumer) throws Exception {
		Log.i(TAG,"Requesting URL : " + url);

		try{
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			Log.i(TAG,"Requesting URL : " + url);
			consumer.sign(request);
			HttpResponse response = httpclient.execute(request);
			Log.i(TAG,"Statusline : " + response.getStatusLine());
			InputStream data = response.getEntity().getContent();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
			String responeLine;
			StringBuilder responseBuilder = new StringBuilder();
			while ((responeLine = bufferedReader.readLine()) != null) {
				responseBuilder.append(responeLine);
			}
			Log.i(TAG,"Response : " + responseBuilder.toString());
			return responseBuilder.toString();
		}catch (Exception e) {
			Log.e(TAG, "Error executing request",e);
			//	textView.setText("Error retrieving contacts : " + jsonOutput);
			return "";

		}
	}	



	public class performApiCall extends AsyncTask<String, Integer, String> {



		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {

				try {
					response = doGet(url,getConsumer(prefs));

					myProgress += 100;
					publishProgress(myProgress);
					myProgress += 100;
					publishProgress(myProgress);
					myProgress += 100;
					publishProgress(myProgress);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return response;

		}

		protected void onProgressUpdate(Integer... progress) {
			// setProgressPercent(progress[0]);
		PB.setProgress(progress[0]);

		}

		protected void onPostExecute(String result) {

			if(result != ""){
				datasource = new JobsDataSource(context);
				datasource.open();
				dbHelper = new MySQLiteHelper(context);
				database = dbHelper.getWritableDatabase(); 
				Gson gson = new Gson();
				Jobs  jobs = gson.fromJson(result, Jobs.class);
				datasource.parse(jobs);	
				database.close();	
			}
			publishProgress(2000);


		}	
	}


	public class performApiCallStats extends AsyncTask<String, Integer, String > {

		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {

				try {

					response = doGet(url,getConsumer(prefs));

					//Intent	StatsIntent = DIRACAndroidActivity.this.execute(context, response);



				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//startActivity(StatsIntent);

			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//dialog.dismiss();
			return response;

		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(String result) {


		}

	}




}
