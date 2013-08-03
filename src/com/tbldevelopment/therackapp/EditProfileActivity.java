package com.tbldevelopment.therackapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;
import com.tbldevelopment.therackapp.GetItemActivity.GetItems;

public class EditProfileActivity extends Activity implements OnClickListener{
	private Context appContext;
	private Button btnDone;
	private Button btnBack;
	private TextView txtHeading;
	private EditText edtname,edtCity,edtWebsite,edtEmail;
	private TextView txtShoeSize,txtDressSize,txtState,txtChangePassword;
	private ImageView imageviewProfile;
	
	private String[] shoeSizeList,dressSizeList,stateList;
	private Dialog dialogDresSizeList,dialogShoeSizeList,dialogStateList,imageDialog;
	private ArrayAdapter<String> dressSizeListAdaptor;
	private ArrayAdapter<String> shoeSizeListAdaptor;
	private ArrayAdapter<String> stateListAdaptor;
	
	private static final int MEMORYCARD = 3;
	private static final int CAMERA = 4;
	
	private Bitmap bitmap=null;
	private TextView txtEmail,txtName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		
		//initialize Context
		appContext=this;
		
		//Inflate navigation Bar
		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
		
		
		//View's Object
		btnDone= (Button)findViewById(R.id.navigationRightBtn);
		btnBack= (Button)findViewById(R.id.navigationLeftBtn);
		txtHeading= (TextView)findViewById(R.id.txtViewTitle);
		txtEmail=(TextView)findViewById(R.id.edtEmailEditProfile);
		txtDressSize=(TextView) findViewById(R.id.txtDressSizeEditProfile);
		txtShoeSize=(TextView) findViewById(R.id.txtShoeSizeEditProfile);
		txtState=(TextView) findViewById(R.id.txtStateEditProfile);
		txtName=(TextView) findViewById(R.id.edtNameEditProfile);
		edtCity=(EditText) findViewById(R.id.edtCityEditProfile);
		edtWebsite=(EditText) findViewById(R.id.edtWebsiteEditProfile);
		imageviewProfile=(ImageView) findViewById(R.id.imageview_profile_edt);
		txtChangePassword=(TextView) findViewById(R.id.txtPasswordEditProfile);
		
		//Set view's Visiblity
		txtHeading.setVisibility(View.VISIBLE);
		btnBack.setVisibility(View.VISIBLE);
		btnDone.setVisibility(View.VISIBLE);
				
		//Set navigation's right button background 
		btnBack.setBackgroundResource(R.drawable.account_page_btn);
		//set heading text
		txtHeading.setText("Profile");		
		
		if(Utility.getSharedPreferences(appContext, "UserProfileData")!=null){
		
			try {
				JSONObject jsonObject=new JSONObject(Utility.getSharedPreferences(appContext, "UserProfileData"));
				txtName.setText(jsonObject.getString("name"));
				txtEmail.setText(jsonObject.getString("email"));
				
				
				if(!jsonObject.getString("dress_size").equals("null")){
					txtDressSize.setText(jsonObject.getString("dress_size"));
				}else{
					txtDressSize.setText("");
				}
				if(!jsonObject.getString("shoe_size").equals("null")){
					txtShoeSize.setText(jsonObject.getString("shoe_size"));
				}else{
					txtShoeSize.setText("");
				}
				if(!jsonObject.getString("city").equals("null")){
					edtCity.setText(jsonObject.getString("city"));
				}else{
					edtCity.setText("");
				}
				if(!jsonObject.getString("website").equals("null")){
					edtWebsite.setText(jsonObject.getString("website"));
				}else{
					edtWebsite.setText("");
				}
				if(!jsonObject.getString("state").equals("null")){
					txtState.setText(jsonObject.getString("state"));
				}else{
					txtState.setText("");
				}
				final String strProfileImage=jsonObject.getString("profile_pic");
				Runnable runnImage = new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String imageUrl=strProfileImage;
						if (imageUrl!=null)
						{
							bitmap = Utility.DownloadImageDirect(imageUrl);
						}
						
						if(bitmap!=null)
						{
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									imageviewProfile.setImageBitmap(bitmap);
								}
							});
						}
					}
				};
				Thread thread = new Thread(runnImage);
				thread.start();
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Getting State,Dress & Shoe list from resource
		dressSizeList = getResources().getStringArray(R.array.DressSizeArray);
		shoeSizeList = getResources().getStringArray(R.array.ShoeSizeArray);
		stateList = getResources().getStringArray(R.array.StateArray);

		// getting diffrent dialog and its button objects
		imageDialog= new Dialog(appContext,R.style.CustomDialogTheme);
		imageDialog.setContentView(R.layout.dark_alert);
		Button sdButton = (Button) imageDialog.findViewById(R.id.btnSDImgDA);
		Button camButton = (Button) imageDialog.findViewById(R.id.btnCamImgDA);
		Button cancelButton = (Button) imageDialog.findViewById(R.id.btnCancelImgDA);
		
		
		
		//add dress size list in dialog
		dialogDresSizeList = new Dialog(appContext);
		dialogDresSizeList.setTitle(R.string.dress_title_txt);
		dialogDresSizeList.setContentView(R.layout.category_listview);
		ListView dressListView = (ListView) dialogDresSizeList
				.findViewById(R.id.listviewCategory);

		// making adaptor object and setting it to listview object
		dressSizeListAdaptor = new ArrayAdapter<String>(appContext,R.layout.listview_item, dressSizeList);
		dressListView.setAdapter(dressSizeListAdaptor);

		// add shoe size list in dialog
		dialogShoeSizeList = new Dialog(appContext);
		dialogShoeSizeList.setTitle(R.string.shoe_title_txt);
		dialogShoeSizeList.setContentView(R.layout.listview_subcategory);
		ListView shoeListView = (ListView) dialogShoeSizeList.findViewById(R.id.listviewSubCategory);
		// making adaptor object and setting it to listview object
		shoeSizeListAdaptor = new ArrayAdapter<String>(appContext,R.layout.listview_item, shoeSizeList);
		shoeListView.setAdapter(shoeSizeListAdaptor);

		// add state size list in dialog
		dialogStateList = new Dialog(appContext);
		dialogStateList.setTitle(R.string.state_title_txt);
		dialogStateList.setContentView(R.layout.listview_subcategory);
		ListView stateListView = (ListView) dialogStateList.findViewById(R.id.listviewSubCategory);
		// making adaptor object and setting it to listview object
		stateListAdaptor = new ArrayAdapter<String>(appContext,R.layout.listview_item, stateList);
		stateListView.setAdapter(stateListAdaptor);
		
		
		
		//set clicklistente
		btnDone.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		txtDressSize.setOnClickListener(this);
		txtShoeSize.setOnClickListener(this);
		txtState.setOnClickListener(this);
		txtChangePassword.setOnClickListener(this);
		sdButton.setOnClickListener(this);
		camButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		imageviewProfile.setOnClickListener(this);
		
		dressListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				dialogDresSizeList.dismiss();
				txtDressSize.setText(dressSizeList[position]);
			}
		});
		shoeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				dialogShoeSizeList.dismiss();
				txtShoeSize.setText(shoeSizeList[position]);
			}
		});
		stateListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				dialogStateList.dismiss();
				txtState.setText(stateList[position]);
			}
		});
		
	}//On create method end

	
	//On ClickListener Interface
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId()==R.id.navigationLeftBtn){
			//navigation left button clcik
			finish();
		}else if(v.getId()==R.id.txtDressSizeEditProfile){
			//show dress list dialog on dress textview click
			dialogDresSizeList.show();
		}else if(v.getId()==R.id.txtShoeSizeEditProfile){
			//show state list dialog on state textview click
			dialogShoeSizeList.show();
		}else if(v.getId()==R.id.txtStateEditProfile){
			//show state list dialog on state textview click
			dialogStateList.show();
		}else if(v.getId()==R.id.imageview_profile_edt){
			//profile image click for showing dialog to change picture 
			imageDialog.show();
		}else if(v.getId()==R.id.btnSDImgDA){
			//taking pic from sd card
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			startActivityForResult(intent, MEMORYCARD);
			imageDialog.dismiss();
		}else if(v.getId()==R.id.btnCamImgDA){
			//camera button click for capture image 
			imageDialog.dismiss();
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, CAMERA);
		}else if(v.getId()==R.id.btnCancelImgDA){
			//cancel button click in image dialog
			imageDialog.dismiss();
		}else if(v.getId()==R.id.txtPasswordEditProfile){
			//Click Change password Text
			Intent intent=new Intent(appContext,ChangePasswordActivity.class);
			startActivity(intent);
		}else if(v.getId()==R.id.navigationRightBtn){
			//navigation right button clcik
			//Get Value On click Button
			String strName=txtName.getText().toString().trim();
			String strEmail=txtEmail.getText().toString().trim();
			String strCity=edtCity.getText().toString().trim();
			String strWebsite=edtWebsite.getText().toString().trim();
			String strState=txtState.getText().toString().trim();
			String strShoeSize=txtShoeSize.getText().toString().trim();
			String strDressSize=txtDressSize.getText().toString().trim();
			
			String strBase64 = null;
			if(bitmap!=null){
				 strBase64 = Utility.encodeTobase64(bitmap);
			}
			
			bitmap=null;
			
			if(strShoeSize.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.shoe_title_txt);
			}else if(strDressSize.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.dress_title_txt);
			}else if(strCity.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.city_alert);
			}else if(strState.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.state_title_txt);
			}else if(strBase64==null){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.profile_image_alert);
			}else{
				new PostuserInfo().execute(strName,strEmail,strShoeSize,strDressSize,strCity,strState,strWebsite,strBase64);
			}
			
			
		}
	}
	
	// method for getting result returned on getting image
			@Override
			protected void onActivityResult(int requestCode, int resultCode,
					Intent mediaReturnedIntent) {
				super.onActivityResult(requestCode, resultCode, mediaReturnedIntent);
				if (resultCode != RESULT_OK) {
					return;
				}
				switch (requestCode) {
				case MEMORYCARD:
					if (mediaReturnedIntent.getData() != null) {
						bitmap=null;
						Uri _uri = mediaReturnedIntent.getData();
						if (_uri != null) {
							Cursor cursor = getContentResolver()
									.query(_uri,
											new String[] { android.provider.MediaStore.Images.ImageColumns.DATA },
											null, null, null);
							cursor.moveToFirst();
							InputStream stream;
							try {
								stream = getContentResolver().openInputStream(_uri);
								bitmap = BitmapFactory.decodeStream(stream);
								if (bitmap!=null) {
									imageviewProfile.setImageBitmap(bitmap);
								}
							} catch (OutOfMemoryError e) {
								// TODO: handle exception
								e.toString();
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NullPointerException e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}
					}

					break;

				case CAMERA:
					if (mediaReturnedIntent != null) {
						bitmap=null;
						bitmap = (Bitmap) mediaReturnedIntent.getExtras().get("data");
						Uri _uri = mediaReturnedIntent.getData();
						if (_uri != null) {
							Cursor cursor = getContentResolver()
									.query(_uri,
											new String[] { android.provider.MediaStore.Images.ImageColumns.DATA },
											null, null, null);
							cursor.moveToFirst();
							InputStream stream;
							try {
								stream = getContentResolver().openInputStream(_uri);
								bitmap = BitmapFactory.decodeStream(stream);
								bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, false);
								if (bitmap != null) {
										imageviewProfile.setImageBitmap(bitmap);
								}
								
							} catch (OutOfMemoryError e) {
								// TODO: handle exception
								e.toString();
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NullPointerException e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}
					}
					break;
				}
			}// onActivityResult function ends	
	
	public class PostuserInfo extends AsyncTask<String, Void, String> {


		ProgressDialog applicationDialog;
		String url;

		protected void onPreExecute() {
			super.onPreExecute();

			applicationDialog = ProgressDialog.show(appContext, "","Please wait...", false, true, new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					PostuserInfo.this.cancel(true);
				}
			});
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String userId=Utility.getSharedPreferences(appContext, "loginUserId");
			String result = null;
			ArrayList<NameValuePair> listNameValuePairs ;
			listNameValuePairs= new ArrayList<NameValuePair>();
			
			listNameValuePairs.add(new BasicNameValuePair("name",params[0]));
			listNameValuePairs.add(new BasicNameValuePair("email",params[1]));
			listNameValuePairs.add(new BasicNameValuePair("shoe_size",params[2]));
			listNameValuePairs.add(new BasicNameValuePair("dress_size",params[3]));
			listNameValuePairs.add(new BasicNameValuePair("city",params[4]));
			listNameValuePairs.add(new BasicNameValuePair("state",params[5]));
			listNameValuePairs.add(new BasicNameValuePair("website",params[6]));
			listNameValuePairs.add(new BasicNameValuePair("profile_pic",params[7]));
			String url=Constant.serverSignUpUrl+userId+"/"+"sign_up";
			result=Utility.postParamsAndfindJSON(url, listNameValuePairs);
			listNameValuePairs.clear();
			
			return result;
		}

		protected void onPostExecute(String result) {
			System.out.println("result is " + result);
			if (result == null) {
				applicationDialog.dismiss();
				Utility.ShowAlertWithMessage(appContext, R.string.alert,R.string.network_connection_alert);
			} else {
				applicationDialog.dismiss();
				Utility.setSharedPreference(appContext, "UserProfileData", result);
				Utility.setSharedPreferenceBoolean(appContext, "refreshUserDataInfo", true);
				Utility.setSharedPreferenceBoolean(appContext, "refreshUserInfoHome", true);
				finish();
			}
		}

		
	}

}
