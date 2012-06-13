package dirac.android;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dirac.android.R.color;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class JobArrayAdapter extends ArrayAdapter<Job> {
	private static final String tag = "JobArrayAdapter";
	//	private static final String ASSETS_DIR = "images/";
	private Context context;
	private TextView jobIDName;
	private TextView jobIDID;
	private TextView jobIDColor;
	private TextView jobIDState;
	private List<Job> JobIDs = new ArrayList<Job>();
	private	final CharSequence[] jodActionFailed = {"Reschedule", "Delete", "Kill"};

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
			row = inflater.inflate(R.layout.listitem, parent, false);
		}

		Status s = new Status();
		Job job = getItem(position);
		int myposition = s.get(job.state);
		jobIDID = (TextView) row.findViewById(R.id.job_id);
		jobIDName = (TextView) row.findViewById(R.id.job_name);
		jobIDState = (TextView) row.findViewById(R.id.job_state);
		jobIDColor = (TextView) row.findViewById(R.id.job_color);
		jobIDID.setText(job.id.toString());
		jobIDName.setText(job.name);
		jobIDState.setText(job.state);
		jobIDColor.setBackgroundColor(Color.rgb(255, 0, 0));
		//  jobIDColor.setBackgroundResource(R.color.completed);
		//jobIDColor.setBackgroundResource(R.color.completed);

		jobIDColor.setBackgroundColor(context.getResources().getColor(Status.ColorStatus[myposition]));

		return row;
	}

}
