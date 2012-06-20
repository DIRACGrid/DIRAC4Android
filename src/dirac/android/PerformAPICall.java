package dirac.android;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
	private Class<Jobs> JobsParser;
	private Class<Status> StautsParser;


	public PerformAPICall(Context myContext, SharedPreferences myPrefs){
		this.context = myContext;
		this.prefs = myPrefs;	
	}
	public void SetProgressBar(ProgressBar myPB){
		this.PB = myPB;
	}


	public void SetPrefs(SharedPreferences myPrefs){
		this.prefs = myPrefs;
	}

	public void SetContext(Context myContext){
		this.context = myContext;
	}

	public void performApiCall(String myUrl, String type) {

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
				Jobs  jobs = gson.fromJson(result, JobsParser);
				datasource.parse(jobs);	
				database.close();	
			}
			publishProgress(2000);


		}	
	}


	public class performApiCallStats extends AsyncTask<String, Integer, String > {

		protected String doInBackground(String... urls) {
			String response = "";Intent	StatsIntent;
			for (String url : urls) {

				try {

					response = doGet(url,getConsumer(prefs));

					StatsIntent = PerformAPICall.this.execute(context, response);



				} catch (Exception e) {
					e.printStackTrace();
				}
			}
//			startActivity(StatsIntent);

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
					Log.i("",status[i]);
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
					double  log10 = java.lang.Math.log10(Float.parseFloat((Status.getString(sdate))));

					series.add(time.getTime(), log10);					
				}	

				dataset.addSeries(series);
			}




		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		Intent intent = new Intent() ;//= ChartFactory.getTimeChartIntent(this,dataset, renderer, null);
		return intent;
	}





}