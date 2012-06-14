package dirac.android;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class JobArrayAdapter extends ArrayAdapter<Job> {
	//	private static final String ASSETS_DIR = "images/";
	private Context context;
	private TextView jobIDName;
	private TextView jobIDID;
	private TextView jobIDColor;
	private TextView jobIDState;
	private List<Job> JobIDs = new ArrayList<Job>();

	public JobArrayAdapter(Context context, int textViewResourceId,
			List<Job> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.JobIDs = objects;
	}

	public int getCount() {
		return this.JobIDs.size();
	}

	public Job getItem(int index) {
		return this.JobIDs.get(index);
	}




	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			// ROW INFLATION
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listjob, parent, false);
		}

		Status s = new Status();
		Job job = getItem(position);
		int myposition = s.get(job.getStatus());
		jobIDID = (TextView) row.findViewById(R.id.job_id);
		jobIDName = (TextView) row.findViewById(R.id.job_name);
		jobIDColor = (TextView) row.findViewById(R.id.job_color);
		jobIDID.setText(job.getJid());
		jobIDName.setText(job.getName());
		jobIDColor.setBackgroundColor(Color.rgb(255, 0, 0));
		//  jobIDColor.setBackgroundResource(R.color.completed);
		//jobIDColor.setBackgroundResource(R.color.completed);

		jobIDColor.setBackgroundColor(context.getResources().getColor(Status.ColorStatus[myposition]));

		return row;
	}

}
