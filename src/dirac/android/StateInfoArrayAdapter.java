package dirac.android;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StateInfoArrayAdapter extends ArrayAdapter<Status> {
	private Context context;
	private Status[] map;
	private TextView StateNum;
	private TextView StateColor;
	private TextView StateName;



	public StateInfoArrayAdapter(Context context, int textViewResourceId, Status[] map) {
		super(context, textViewResourceId, map);
		this.context = context;
		this.map = map;

	}

	public int getCount() {

		return this.map.length;


	}

	public Status getItem(int index) {
		return this.map[index];
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.liststatus, parent, false);
		}


		Status state = getItem(position);


		StateNum = (TextView) row.findViewById(R.id.state_num);
		StateName = (TextView) row.findViewById(R.id.state_name);
		StateColor = (TextView) row.findViewById(R.id.state_color);
		StateName.setText(state.name());
		StateNum.setText(state.number());
		StateColor.setBackgroundColor(context.getResources().getColor(state.ColorStatus[state.get(state.name())]));


		return row;
	}

}
