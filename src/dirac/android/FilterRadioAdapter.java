package dirac.android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.BaseExpandableListAdapter;
import java.util.ArrayList;

public class FilterRadioAdapter extends BaseExpandableListAdapter {

    //  private Context context;
    private ArrayList<String> groups;
    private ArrayList<ArrayList<String>> sites;
    private LayoutInflater inflater;

    public FilterRadioAdapter(Context context, 
			      ArrayList<String> groups,
			      ArrayList<ArrayList<String>> sites ) { 
	//    this.context = context;
	this.groups = groups;
        this.sites = sites;
        inflater = LayoutInflater.from( context );
    }

    public Object getChild(int groupPosition, int childPosition) {
        return sites.get( groupPosition ).get( childPosition );
    }

    public long getChildId(int groupPosition, int childPosition) {
        return (long)( groupPosition*1024+childPosition );  // Max 1024 children per group
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = null;
        if( convertView != null )
            v = convertView;
        else
            v = inflater.inflate(R.layout.item_radiobutton, parent, false); 
        String c = (String)getChild( groupPosition, childPosition );
	RadioButton cb = (RadioButton)v.findViewById( R.id.radioButton1);
	cb.setText(c);
        cb.setChecked( false );
        return v;
    }

    public int getChildrenCount(int groupPosition) {
        return sites.get( groupPosition ).size();
    }

    public Object getGroup(int groupPosition) {
        return groups.get( groupPosition );        
    }

    public int getGroupCount() {
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        return (long)( groupPosition*1024 );  // To be consistent with getChildId
    } 

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = null;
        if( convertView != null )
            v = convertView;
        else
            v = inflater.inflate(R.layout.group_row, parent, false); 
        String gt = (String)getGroup( groupPosition );
	TextView colorGroup = (TextView)v.findViewById( R.id.childname );
	if( gt != null )
	    colorGroup.setText( gt );
        return v;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    } 

    public void onGroupCollapsed (int groupPosition) {} 
    public void onGroupExpanded(int groupPosition) {}


}
