package dirac.android;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListAdapter;
import android.widget.RadioButton;

public class FilterSettingsActivity  extends Activity {



	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filters);
		setTitle("Filter Settings");
		
		
		ExpandableListView lv = (ExpandableListView) this.findViewById(R.id.SiteListView);

        ArrayList<String> groupNames = new ArrayList<String>();
        groupNames.add( "" );
        ArrayList<ArrayList<String>> sites = new ArrayList<ArrayList<String>>(); 
        ArrayList<String> site = new ArrayList<String>();
        site.add( "LCG.CERN.CH" ); 
        site.add( "LCG.PIC.ES" ); 
        site.add( "LCG.IN2P3.FR" ); 
        sites.add( site ); 

        FilterCheckBoxAdapter	expListAdapter = new FilterCheckBoxAdapter( this,groupNames, sites );
		lv.setAdapter( expListAdapter );


		ExpandableListView lv2 = (ExpandableListView) this.findViewById(R.id.NameListView);

        ArrayList<String> groupNames2 = new ArrayList<String>();
        groupNames2.add( "" );
        ArrayList<ArrayList<String>> names = new ArrayList<ArrayList<String>>(); 
        ArrayList<String> name = new ArrayList<String>();
        name.add( "bjets" ); 
        name.add( "B2DK" ); 
        name.add( "BJPHSI" ); 
        names.add( name ); 

        FilterCheckBoxAdapter	expListAdapter2 = new FilterCheckBoxAdapter( this,groupNames2, names );
		lv2.setAdapter( expListAdapter2 );

		ExpandableListView lv3 = (ExpandableListView) this.findViewById(R.id.TimeListView);

        ArrayList<String> groupNames3 = new ArrayList<String>();
        groupNames3.add( "" );
        ArrayList<ArrayList<String>> times = new ArrayList<ArrayList<String>>(); 
        ArrayList<String> time = new ArrayList<String>();
        time.add( "12h" ); 
        time.add( "24h" ); 
        time.add( "48h" ); 
        time.add( "4d" ); 
        time.add( "1w" ); 
        time.add( "2w" ); 
        time.add( "1m" ); 
        time.add( "all" ); 
        times.add( time ); 

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,time);
        
        FilterRadioAdapter	expListAdapter3 = new FilterRadioAdapter( this,groupNames3, times );
		lv3.setAdapter( expListAdapter3);
		lv2.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> arg0, View parent, int position,
					long arg3) {
				
             AdapterView<?> rb =  arg0;
             Log.d("tatt", rb.toString());

				
			}
			
			
		});

		
		
		
	}
}