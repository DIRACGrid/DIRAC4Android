package dirac.android;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dirac.android.R.color;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
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

public class StateInfoArrayAdapter extends ArrayAdapter<Status> {
	private static final String tag = "StateInfoArrayAdapter";
//	private static final String ASSETS_DIR = "images/";
	private Context context;
	private Status[] map;
	private TextView jobIDName;
	private TextView jobIDID;
	private TextView jobIDColor;
	private TextView jobIDState;



	public StateInfoArrayAdapter(Context context, int textViewResourceId, Status[] map) {
		super(context, textViewResourceId, map);
		this.context = context;
		this.map = map;

	}

	public int getCount() {
		System.out.println("eel");

		System.out.println(this.map.length);
		return this.map.length;
		

	}

	public Status getItem(int index) {
		System.out.println("eeu");
		return this.map[index];
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("eeV");

		View row = convertView;

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.country_listitem, parent, false);
		}


		Status state = getItem(position);

		
        jobIDID = (TextView) row.findViewById(R.id.job_id);
        jobIDName = (TextView) row.findViewById(R.id.job_name);
		jobIDState = (TextView) row.findViewById(R.id.job_state);
		jobIDColor = (TextView) row.findViewById(R.id.job_color);
		jobIDID.setText(state.name());
		jobIDName.setText(state.number());
		jobIDState.setText("");
	    jobIDColor.setBackgroundColor(Color.rgb(255, 0, 0));

		int state_color = Color.rgb(170,102,204);

		if("running".equals(state.name())){
			 state_color = Color.rgb(153,204,0);
		}else if ("completed".equals(state.name())){
			state_color = Color.rgb(51,181,229);
		}else if ("failed".equals(state.name())){
			state_color = Color.rgb(255,68,68);
		}
		
		
	    jobIDColor.setBackgroundColor(state_color);
		return row;
	}

}
