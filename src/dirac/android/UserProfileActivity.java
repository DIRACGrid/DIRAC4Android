package dirac.android;


import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;

import dirac.android.R.color;
import dirac.gsonconfig.Groups;
import dirac.gsonconfig.Setups;
import android.content.Context;
import android.content.DialogInterface;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.security.KeyChainException;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
@SuppressLint("ResourceAsColor")
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class UserProfileActivity  extends Activity {


    private final Context context = this;
    private Connectivity connect;
    private static final int REQUEST_PICK_FILE = 1;
    private String myalias = "";
    private PerformAPICall2 apiCall;

	
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.user);
	Button loadCert = (Button) findViewById(R.id.loadCert);
	Button loadServ = (Button) findViewById(R.id.loadServ);
	Button loadRole = (Button) findViewById(R.id.loadRole);
	Button loadProd = (Button) findViewById(R.id.loadProd);
	Button clearCredentials = (Button) findViewById(R.id.clearGrant);
	Button launchOauth = (Button) findViewById(R.id.getGrant);
checkall();	
	CacheHelper.writeBoolean(context, CacheHelper.CERTREADY, false); 

	setTitle("dirac > user profile");




	
	
	launchOauth.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
		    if(connect.isOnline()){
		    	String DiracServer = CacheHelper.readString(context, CacheHelper.DIRACSERVER, "");
		    	String DiracGroup = CacheHelper.readString(context, CacheHelper.DIRACGROUP, "");
		    	String DiracSetup = CacheHelper.readString(context, CacheHelper.DIRACSETUP, "");
				Integer nbcert = CacheHelper.readInteger(context, CacheHelper.NBCERT, 0);

		    	
		    if(DiracServer!="" && DiracSetup!="" && DiracGroup!=""&& nbcert>0){
						

			apiCall = new PerformAPICall2(context);

			Activity activityContext = (Activity) context;
			apiCall.SetActivity(activityContext);

			apiCall.getAccess(Constants.REQUEST_TOKEN);

			
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage("Do you want to enable  ''auto - grant'' (this option could be (un)set in the preferences)");
			builder.setCancelable(true);
						
						
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {								
				public void onClick(DialogInterface dialog, int which) {	

				    CacheHelper.writeBoolean(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE,true);
				    dialog.cancel();
					Integer defValue = 0;
					Integer nbcert = CacheHelper.readInteger(context, CacheHelper.NBCERT, defValue);

					Log.i("def", nbcert.toString());
					if(connect.isGranted() && (nbcert > 0)){



						runOnUiThread(new Runnable() {
			    			
			    			public void run() {
			    				checkall();

			    		    }
			    		});



					}		
								
				}
			    });
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {			
				    CacheHelper.writeBoolean(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_AUTOCREATE,false);
				    dialog.cancel();
					Integer defValue = 0;
					Integer nbcert = CacheHelper.readInteger(context, CacheHelper.NBCERT, defValue);
					Log.i("def", nbcert.toString());
					if(connect.isGranted() && (nbcert > 0)){

						runOnUiThread(new Runnable() {
			    			
			    			public void run() {
			    				
			    				checkall();

			    		    }
			    		});

					}
				}
			});
			builder.show();

	

		    }else{
		    	
		    	Toast.makeText(context, "Select your Certificate, Server, Group and Setup", Toast.LENGTH_SHORT).show();
	
		    }
		       
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
							       
							       runOnUiThread(new Runnable() {
					    			
					    			public void run() {
					    				
					    				checkall();

					    		    }
					    		});
										
										
										
										
										

							   }
						            
						       }
						   },
						   new String[] {"RSA", "DSA","PKCS12"}, // List of acceptable key types. null for any
						   null,                        // issuer, null for any
						   Constants.BASE_URL,      // host name of server requesting the cert, null if unavailable
						   Integer.parseInt(Constants.PORT),                         // port of server requesting the cert, -1 if unavailable
						   myalias);   
				
				  
			
		    //cert ready
				  
			 checkall(); 

		}

	    });



	clearCredentials.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
		    clearCredentials();	
		}
	    });



	loadServ.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			
			
			Integer nbcert = CacheHelper.readInteger(context, CacheHelper.NBCERT, 0);

	    	
	    if(connect.isOnline() && nbcert>0){
					

			
			
			AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_list_item_single_choice);
            arrayAdapter.add("lhcb01.ecm.ub.es");
            arrayAdapter.add("add new server");
            builderSingle.setNeutralButton("cancel",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builderSingle.setNegativeButton("delete",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                        	
                       
                	    	String DiracServer = CacheHelper.readString(context, CacheHelper.DIRACSERVER_TMP, "");

                        	AlertDialog.Builder builder = new AlertDialog.Builder(context);
                			builder.setMessage("Are you sure you want to remove this server ("+DiracServer+") from the list");
                			builder.setCancelable(true);
                						
                						
                			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {								
                				public void onClick(DialogInterface dialog, int which) {	

                			
                                	arrayAdapter.notifyDataSetChanged();

                								
                				}
                			    });
                			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                				public void onClick(DialogInterface dialog, int which) {			
                				  

                					
                				}
                			});
                			builder.show();
                            dialog.dismiss();
                        }
                    });

            
            
            
            
            builderSingle.setPositiveButton("done",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                        	
                        	CacheHelper.writeString(context, CacheHelper.DIRACSERVER,CacheHelper.readString(context, CacheHelper.DIRACSERVER_TMP, ""));

                        	apiCall = new PerformAPICall2(context);

                			Activity activityContext = (Activity) context;
                			apiCall.SetActivity(activityContext);
                			apiCall.getGroupAndSetup(Constants.REQUEST_GROUPS,Constants.REQUEST_SETUPS);
                        	
                        	
                            dialog.dismiss();
                        }
                    });

            
            
            builderSingle.setSingleChoiceItems(arrayAdapter, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                	String strName = arrayAdapter.getItem(item);
                    
                    if(strName == "add new server"){

                    	arrayAdapter.add("test");
                    	arrayAdapter.notifyDataSetChanged();
                    	
                    }else{
                    
                    AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
                    builderInner.setMessage(strName);

                	CacheHelper.writeString(context, CacheHelper.DIRACSERVER_TMP, strName);
                    }
                
                }
            });
            
        

            AlertDialog alert = builderSingle.create();
            alert.show();    
            
            
		}else{
	    	Toast.makeText(context, "Select your Certificate", Toast.LENGTH_SHORT).show();

		}
		};
		
	
		
		});


	

	loadRole.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			
	    	String DiracServer = CacheHelper.readString(context, CacheHelper.DIRACSERVER, "");

		    if(connect.isOnline() && DiracServer!=""){
			
			
			AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    context,
                    android.R.layout.select_dialog_singlechoice);
        	
            
            	Gson gson = new Gson();
            	Groups mygroups = gson.fromJson(CacheHelper.readString(context, CacheHelper.SHPREF_GROUPS,"NONE"), Groups.class);

            	Log.i("rest",mygroups.getGroups().toString());
            	List<String> myGroups = mygroups.getGroups();	
            	Iterator<String> iterator = myGroups.iterator();
            	while (iterator.hasNext()) {
            		String tmp = iterator.next();
            		System.out.println(tmp);
                    arrayAdapter.add(tmp);
            	}
            
            
            builderSingle.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builderSingle.setAdapter(arrayAdapter,
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            String strName = arrayAdapter.getItem(which);
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
                            builderInner.setMessage(strName);

                        	CacheHelper.writeString(context, CacheHelper.DIRACGROUP, strName);
                        
                           checkall();
                        }
                    });
            builderSingle.show();
		}else{
	    	Toast.makeText(context, "Select a server", Toast.LENGTH_SHORT).show();

		}
		};
		});
	
	
	loadProd.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			

			
	    	String DiracServer = CacheHelper.readString(context, CacheHelper.DIRACSERVER, "");

		    if(connect.isOnline() && DiracServer!=""){
		    	
		    	
			AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    context,
                    android.R.layout.select_dialog_singlechoice);
        	
            
            	Gson gson = new Gson();
            	Setups mysetups = gson.fromJson(CacheHelper.readString(context, CacheHelper.SHPREF_SETUPS,"NONE"), Setups.class);

            	Log.i("rest",mysetups.getSetups().toString());
            	List<String> mySetups = mysetups.getSetups();	
            	Iterator<String> iterator = mySetups.iterator();
            	while (iterator.hasNext()) {
            		String tmp = iterator.next();
            		System.out.println(tmp);
                    arrayAdapter.add(tmp);
            	}
            
            
            builderSingle.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builderSingle.setAdapter(arrayAdapter,
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            String strName = arrayAdapter.getItem(which);
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
                            builderInner.setMessage(strName);

                        	CacheHelper.writeString(context, CacheHelper.DIRACSETUP, strName);
                        
                           checkall();
                        }
                    });
            builderSingle.show();
            }else{
    	    	Toast.makeText(context, "Select a Server", Toast.LENGTH_SHORT).show();

    		}
		};
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

    	CacheHelper.writeString(context, CacheHelper.DIRACSERVER, "");
 
	CacheHelper.writeInteger(context, CacheHelper.NBCERT ,0);
	CacheHelper.writeBoolean(context, CacheHelper.CERTREADY, false);
	CacheHelper.writeString(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN, "no");
	CacheHelper.writeString(context, CacheHelper.SHPREF_GROUPS, "");
	CacheHelper.writeString(context, CacheHelper.SHPREF_SETUPS, "");
	CacheHelper.writeString(context, CacheHelper.DIRACGROUP, "");
	CacheHelper.writeString(context, CacheHelper.DIRACSETUP, "");
	
	CacheHelper.writeLong(context, CacheHelper.SHPREF_KEY_ACCESS_TOKEN_EXPIRES_TIME, -1);
checkall();
	Toast.makeText(this.getApplicationContext(), "Your credentials have been cleared", Toast.LENGTH_SHORT).show();
    }



    void checkall(){
    	

    	connect = new Connectivity(context);
    	Integer defValue = 0;
    	Integer nbcert = CacheHelper.readInteger(context, CacheHelper.NBCERT, defValue);
    	
    	
    	Button loadCert = (Button) findViewById(R.id.loadCert);
    	
    	if((nbcert > 0)){

    		loadCert.setText("Using cert: "+CacheHelper.readString(context, CacheHelper.CERTALIAS, "ERROR"));
    		loadCert.setBackgroundColor(getResources().getColor(color.DarkGreen));

    	}else{

    		loadCert.setText("Select certificate");
    		loadCert.setBackgroundColor(getResources().getColor(color.DarkRed));
    	}

       	Button loadServ = (Button) findViewById(R.id.loadServ);
    	String DiracServer = CacheHelper.readString(context, CacheHelper.DIRACSERVER, "");

    	Log.i("here",DiracServer);
      	if((DiracServer != "")){

    		loadServ.setText("Using Server: "+DiracServer);
    		loadServ.setBackgroundColor(getResources().getColor(color.DarkGreen));

    	}else{

    		loadServ.setText("Select a DIRAC Server");
    		loadServ.setBackgroundColor(getResources().getColor(color.DarkRed));
    	}
      	

       	Button loadRole = (Button) findViewById(R.id.loadRole);
    	String DiracRole = CacheHelper.readString(context, CacheHelper.DIRACGROUP, "");

    	
      	if((DiracRole != "")){

    		loadRole.setText("Using Group: "+DiracRole);
    		loadRole.setBackgroundColor(getResources().getColor(color.DarkGreen));

    	}else{

    		loadRole.setText("Select a DIRAC Group");
    		loadRole.setBackgroundColor(getResources().getColor(color.DarkRed));
    	}
      	
      	
       	Button loadProd = (Button) findViewById(R.id.loadProd);
    	String DiracProd = CacheHelper.readString(context, CacheHelper.DIRACSETUP, "");

    	
      	if((DiracProd != "")){

    		loadProd.setText("Using Setup: "+DiracProd);
    		loadProd.setBackgroundColor(getResources().getColor(color.DarkGreen));

    	}else{

    		loadProd.setText("Select a DIRAC Setup");
    		loadProd.setBackgroundColor(getResources().getColor(color.DarkRed));
    	}
      	
    	
    	Button launchOauth = (Button) findViewById(R.id.getGrant);

    if(connect.isGranted() && nbcert > 0){
    	launchOauth.setText("You have Grant Access");
    	launchOauth.setBackgroundColor(getResources().getColor(color.DarkGreen));

    }else{
    	launchOauth.setText("Get Grant Access");
    	launchOauth.setBackgroundColor(getResources().getColor(color.DarkRed));
    }
    	Button clearCredentials = (Button) findViewById(R.id.clearGrant);
    	clearCredentials.setText("Reset Access");
    	
    }
    
    
}
