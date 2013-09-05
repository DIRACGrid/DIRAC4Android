package dirac.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class JobInfoArrayAdapter extends ArrayAdapter<String[]> {

    private Context context;
    private TextView InfoName;
    private TextView InfoValue;
    private List<String[]> infos = new ArrayList<String[]>();
	
    public JobInfoArrayAdapter(Context context, int textViewResourceId, List<String[]> infos) {
	super(context, textViewResourceId, infos);
	this.context = context;
	this.infos = infos;
    }

    public int getCount() {
	return this.infos.size();
    }

    public String[] getItem(int index) {
	return this.infos.get(index);
    }



    public View getView(int position, View convertView, ViewGroup parent) {
	View row = convertView;
	if (row == null) {
	    // ROW INFLATION
	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    row = inflater.inflate(R.layout.listjobinfo, parent, false);
	}

	String[] info  = getItem(position);
	InfoName = (TextView) row.findViewById(R.id.info_job);
	InfoValue= (TextView) row.findViewById(R.id.info_job_value);
	InfoName.setText(info[0]+":");
	InfoValue.setText(info[1]);
		

	return row;
    }

}
