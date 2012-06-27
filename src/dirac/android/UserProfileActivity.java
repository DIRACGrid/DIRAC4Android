package dirac.android;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import org.apache.http.client.HttpClient;

import android.content.Context;
import android.util.Log;

import oauth.signpost.OAuth;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class UserProfileActivity  extends Activity {



	private final Context context = this;
	private Connectivity connect;
	private static final int REQUEST_PICK_FILE = 1;
	private InputStream keyStoreStream;
	private	KeyStore keyStore;
	private ListView lvListe;
	private HttpClient myHttpClient;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user);


		CacheHelper.writeBoolean(context, CacheHelper.CERTREADY, false); 

		setTitle("User Profile");

		connect = new Connectivity(context);
		Integer defValue = 0;
		Integer nbcert = CacheHelper.readInteger(context, CacheHelper.NBCERT, defValue);

		HashMap<String, String> maplist;
		maplist = new HashMap<String, String>();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(2);

		if(connect.isGranted() && (nbcert > 0)){



			maplist.put("line1", "Access has been granted using:");
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
				if(connect.isOnline())
					if(CacheHelper.readBoolean(context, CacheHelper.CERTREADY, false)){
						startActivity(new Intent().setClass(v.getContext(), PrepareRequestTokenActivity.class));




						//OAUTH 2 implemetantion....
						//	String formDataServiceUrl = Constants.REQUEST_URL;
						//	HttpPost post = new HttpPost(formDataServiceUrl);
						//	post.setHeader("ping", "test");

						//	try {
						//		HttpResponse result = myHttpClient.execute(post);
						//	} catch (ClientProtocolException e) {
						//	// TODO Auto-generated catch block
						//		e.printStackTrace();
						//	} catch (IOException e) {
						//	// TODO Auto-generated catch block
						//		e.printStackTrace();
						//	}









					}else{
						Toast.makeText(context, "Load your certificate first\nThe certificate keys are _never_ saved in the application.", Toast.LENGTH_LONG).show();

					}

			}
		});


		loadCert.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(context, FilePickerActivity.class);
				startActivityForResult(intent, REQUEST_PICK_FILE);

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
				if(data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {
					// Get the file path

					File file = new File(data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));

					if (!file.exists()) {
						throw new RuntimeException("File not found");
						// Set the file path text view
					}else{
						Toast.makeText(context, "File selected: "+file.getPath(), Toast.LENGTH_SHORT).show();

						KeyStore trustStore = null;
						try {
							trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
						} catch (KeyStoreException e1) {
							e1.printStackTrace();
						}
						try {
							trustStore.load(null, null);
						} catch (NoSuchAlgorithmException e1) {
							e1.printStackTrace();
						} catch (CertificateException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						// client certificate is stored in android's resource folder (raw)
						try {
							keyStoreStream = new BufferedInputStream(new FileInputStream(file));
						} catch (FileNotFoundException e2) {
							e2.printStackTrace();
						}
						try {
							keyStore = KeyStore.getInstance("PKCS12");
						} catch (KeyStoreException e1) {
							e1.printStackTrace();
						} 

						AlertDialog.Builder alert = new AlertDialog.Builder(this);

						alert.setMessage("your paraphrase:");

						// Set an EditText view to get user input 
						final EditText input = new EditText(this);
						input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

						alert.setView(input);

						alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								String value = input.getText().toString();
								try {
									try {
										keyStore.load(keyStoreStream, value.toCharArray());  



										lvListe = (ListView)findViewById(R.id.listCert);
										ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(2);
										Integer certi = 0;

										try {

											Enumeration<String> aliases = keyStore.aliases();

											HashMap<String, String> maplist;
											maplist = new HashMap<String, String>();

											maplist.put("line1", "The Grant Access will use:");
											list.add(maplist);

											while(aliases.hasMoreElements()){
												String alias = aliases.nextElement();

												if(keyStore.getCertificate(alias).getType().equals("X.509")){


													maplist = new HashMap<String, String>();
													maplist.put("line1", alias);

													String info = "expires " + ((X509Certificate) keyStore.getCertificate(alias)).getNotAfter()+"\n"
															+ "UserDN:\n" + ((X509Certificate) keyStore.getCertificate(alias)).getSubjectDN() +"\n"
															+ "Issuer:\n" + ((X509Certificate) keyStore.getCertificate(alias)).getIssuerDN();

													maplist.put("line2", info);
													list.add(maplist);

													CacheHelper.writeString(context, "cert_"+certi.toString()+"_line1" ,alias);	
													CacheHelper.writeString(context, "cert_"+certi.toString()+"_line2" , info);	



													certi++;

												}


											}
										} catch (Exception e) {
											e.printStackTrace();
										}




										CacheHelper.writeInteger(context, CacheHelper.NBCERT ,certi);	





										String[] from = { "line1", "line2" };

										int[] to = { android.R.id.text1, android.R.id.text2 };


										//	ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_2, android.R.id.text1, listeStrings);
										SimpleAdapter adapter = new SimpleAdapter(context, list, android.R.layout.simple_list_item_2, from, to);  

										lvListe.setAdapter(adapter);

										//cert ready
										CacheHelper.writeBoolean(context, CacheHelper.CERTREADY, true); 

										dialog.dismiss();
										KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
										trustStore.load(null, null);
										myHttpClient = CustomSSLSocketFactory.getNewHttpClient(context,keyStore,trustStore); 






									} catch (NoSuchAlgorithmException e) {
										e.printStackTrace();
									} catch (KeyStoreException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Log.i("here",keyStore.toString());

								} catch (CertificateException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}// Do something with value!
							}
						});

						alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.dismiss();

								// Canceled.
							}
						});

						alert.show();

					}
				}
			}
		}
	}



	private void clearCredentials() {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor edit = prefs.edit();
		edit.remove(OAuth.OAUTH_TOKEN);
		edit.remove(OAuth.OAUTH_TOKEN_SECRET);
		edit.commit();
		CacheHelper.writeInteger(context, CacheHelper.NBCERT ,0);
		CacheHelper.writeBoolean(context, CacheHelper.CERTREADY, false);


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
