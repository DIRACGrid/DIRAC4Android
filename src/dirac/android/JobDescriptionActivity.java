package dirac.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dirac.gsonconfig.Job;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class JobDescriptionActivity extends Activity {

    ArrayAdapter<String[]> adapter;
    private JobsDataSource datasource;
    List<String[]> job_infos;

    private Job myjob;
    private String result;
    private List<String[]> infos;

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);


	setContentView(R.layout.mainjobdescription);
	Intent i = getIntent();

	datasource = new JobsDataSource(this);
	datasource.open();
	myjob = datasource.getJobInfo(i.getStringExtra("jid"));	
	result = i.getStringExtra("description");	

	datasource.close();



	TextView tt = (TextView) this.findViewById(R.id.JOBDCOLOR);
	Status status = new Status();
	tt.setBackgroundColor(getResources().getColor(Status.ColorStatus[status.get(myjob.getStatus())]));
	tt = (TextView) this.findViewById(R.id.JOBDCOLOR2);
	tt.setBackgroundColor(getResources().getColor(Status.ColorStatus[status.get(myjob.getStatus())]));




	try {
	    JSONObject jObject = new JSONObject(result);					
	    JSONArray jObjectN = jObject.names();
	    infos = new ArrayList<String[]>();


	    for (int k = 0; k < jObjectN.length(); k++) {

		String[] temp = new String[2];

		String tmp = jObjectN.getString(k);
		String output = deCamelCasealize(tmp);
		temp[0] = output;
		temp[1] = jObject.getString(tmp);
		infos.add(temp);
	    }


	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}




	adapter = new JobInfoArrayAdapter(getApplicationContext(), R.layout.listjob, infos);

	// Get reference to ListView holder
	ListView lv = (ListView) this.findViewById(R.id.JOBD);


	// Set the ListView adapter
	lv.setAdapter(adapter);


	setTitle("dirac > "+ myjob.getStatus() +" > job id: "+myjob.getJid()+" > JDL");




    }
    public static String deCamelCasealize(String camelCasedString) {
	if (camelCasedString == null || camelCasedString.isEmpty()) 
	    return camelCasedString;  

	StringBuilder result = new StringBuilder();
	result.append(camelCasedString.charAt(0));
	for (int i = 1; i < camelCasedString.length(); i++) {

	    if(i+1 < camelCasedString.length()){
		if (Character.isUpperCase(camelCasedString.charAt(i)) && Character.isLowerCase(camelCasedString.charAt(i+1))){
		    result.append(" ");
		}else if (Character.isUpperCase(camelCasedString.charAt(i)) && Character.isUpperCase(camelCasedString.charAt(i+1)) && Character.isLowerCase(camelCasedString.charAt(i-1))){
		    result.append(" ");
		}
	    }
	    result.append(camelCasedString.charAt(i));
	}
	return result.toString();
    }


}
