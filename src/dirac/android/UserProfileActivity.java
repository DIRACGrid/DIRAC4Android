package dirac.android;


import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.DialogInterface;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.security.KeyChainException;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class UserProfileActivity  extends Activity {


    private final Context context = this;
    private Connectivity connect;
    private static final int REQUEST_PICK_FILE = 1;
    private ListView lvListe;
    private String myalias = "";
    private PerformAPICall2 apiCall;

	
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.user);


	CacheHelper.writeBoolean(context, CacheHelper.CERTREADY, false); 

	setTitle("dirac > user profile");

	connect = new Connectivity(context);
	Integer defValue = 0;
	Integer nbcert = CacheHelper.readInteger(context, CacheHelper.NBCERT, defValue);

	HashMap<String, String> maplist;
	maplist = new HashMap<String, String>();
	final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(2);

	if(connect.isGranted() && (nbcert > 0)){



	    maplist.put("line1", "Access using:");
	    list.add(maplist);

	    for(Integer i = 0; i < nbcert; i++){

		maplist = new HashMap<String, String>();

		String alias = "";
		alias = CacheHelper.readString(context, "cert_"+i.toString()+"_line1" ,alias);	
		String info = "";
		info= CacheHelper.readString(context, "cert_"+i.toString()+"_line2" , info);	
		maplist.put("line1",alias);
		maplist.put("line2",info);
		list.add(maplist);


	    }


	}else{
	    maplist.put("line1", "Load your certificate and then ask for a grant access");
	    list.add(maplist);
	}

	lvListe = (ListView)findViewById(R.id.listCert);
	String[] from = { "line1", "line2" };

	int[] to = { android.R.id.text1, android.R.id.text2 };


	//	ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_2, android.R.id.text1, listeStrings);
	SimpleAdapter adapter = new SimpleAdapter(context, list, android.R.layout.simple_list_item_2, from, to);  

	lvListe.setAdapter(adapter);




	Button launchOauth = (Button) findViewById(R.id.getGrant);
	Button clearCredentials = (Button) findViewById(R.id.clearGrant);
	Button loadCert = (Button) findViewById(R.id.loadCert);
	launchOauth.setText("Get Grant Access");
	clearCredentials.setText("Reset Access");
	loadCert.setText("Load your Certificate");
	launchOauth.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
		    if(connect.isOnline()){
						
						

			apiCall = new PerformAPICall2(context);

			apiCall.getAccess(Constants.REQUEST_TOKEN);

						
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage("Do you want to enable  ''auto - grant'' (this option could be (un)set in the preferences)");
			builder.setCancelable(true);
						
						
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {								
				public void onClick(DialogInterface dialog, int which) {	

				    CacheHelper.writeBoolean(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE,true);
				    dialog.cancel();
							
								
								
						
								
				}
			    });
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {			
				    CacheHelper.writeBoolean(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE,false);
				    dialog.cancel();
				}
			    });
			builder.show();
						
						

		    }

		}
	    });


	loadCert.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
				
		    KeyChain.choosePrivateKeyAlias((Activity) context,
						   new KeyChainAliasCallback() {
					  
					  
						       public void alias(String alias) {
							   // Credential alias{ selected.  Remember the alias selection for future use.
							   if (alias != null) {
						            	
						            	
							       CacheHelper.writeString(context, CacheHelper.CERTALIAS, alias);

							       Integer certi = 0;



							       X509Certificate[] certificateChain = null;
							       try {
								   certificateChain = KeyChain.getCertificateChain(context, alias);
							       } catch (KeyChainException e3) {
								   // TODO Auto-generated catch block
								   e3.printStackTrace();
							       } catch (InterruptedException e3) {
								   // TODO Auto-generated catch block
								   e3.printStackTrace();
							       }
											
							       while(certi < certificateChain.length){
												
								   String Exp0 = certificateChain[certi].getNotAfter().toString();
								   String[] separated0 = Exp0.split("OID.");
								   String Exp1 = certificateChain[certi].getSubjectDN().toString();
								   String[] separated1 = Exp1.split("OID.");
								   String Exp2 = certificateChain[certi].getIssuerDN().toString();
								   String[] separated2 = Exp2.split("OID.");
								   String info = "expires " + separated0[0]+"\n"
								       + "UserDN:\n" + separated1[0] +"\n"
								       + "Issuer:\n" + separated2[0];

											

								   CacheHelper.writeString(context, "cert_"+certi.toString()+"_line1" ,alias);	
								   CacheHelper.writeString(context, "cert_"+certi.toString()+"_line2" , info);	



								   certi++;
							       }
													

										

										

							       CacheHelper.writeInteger(context, CacheHelper.NBCERT ,certi);										
							       CacheHelper.writeBoolean(context, CacheHelper.CERTREADY, true); 
										
										
					
										
										
										
										
										
										

							   }
						            
						       }
						   },
						   new String[] {"RSA", "DSA","PKCS12"}, // List of acceptable key types. null for any
						   null,                        // issuer, null for any
						   Constants.BASE_URL,      // host name of server requesting the cert, null if unavailable
						   Integer.parseInt(Constants.PORT),                         // port of server requesting the cert, -1 if unavailable
						   myalias);   
				
				  
			
		    //cert ready
				  
				  

		}
	    });



	clearCredentials.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
		    clearCredentials();	
		}
	    });


		




    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if(resultCode == RESULT_OK) {
	    switch(requestCode) {
	    case REQUEST_PICK_FILE:
			
	    }
	}
    }



    private void clearCredentials() {

	
	CacheHelper.writeInteger(context, CacheHelper.NBCERT ,0);
	CacheHelper.writeBoolean(context, CacheHelper.CERTREADY, false);
	CacheHelper.writeString(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN, "no");
	CacheHelper.writeLong(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_EXPIRES_TIME, -1);

	HashMap<String, String> maplist;
	maplist = new HashMap<String, String>();
	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(2);


	maplist.put("line1", "Load your certificate and then ask for a grant access");
	list.add(maplist);


	lvListe = (ListView)findViewById(R.id.listCert);
	String[] from = { "line1", "line2" };

	int[] to = { android.R.id.text1, android.R.id.text2 };


	//	ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_2, android.R.id.text1, listeStrings);
	SimpleAdapter adapter = new SimpleAdapter(context, list, android.R.layout.simple_list_item_2, from, to);  

	lvListe.setAdapter(adapter);



	Toast.makeText(this.getApplicationContext(), "Your credentials have been cleared", Toast.LENGTH_SHORT).show();
    }



}
