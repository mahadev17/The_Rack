package com.tbldevelopment.therackapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;
import com.tbldevelopment.therackapp.EditProfileActivity.PostuserInfo;

public class AddUserProfile extends Activity implements OnClickListener{
	
	private Context appContext;
	private String userEmail,userName,userId;
	private String password;
	private Dialog dialogDresSizeList,dialogShoeSizeList,dialogStateList;
	private TextView txtDressSize,txtShoeSize,txtState;
	private String[] shoeSizeList,dressSizeList,stateList;
	private Bitmap bitmap = null;
	String strBase64=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_info);
		
		appContext = this;
		
		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
		
		//Make bundle object for getting value
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			userEmail=bundle.getString("userEmail");
			userName=bundle.getString("userName");
			password=bundle.getString("Password");
		}
		//Get Array list for dress size,shoe size and state name
		dressSizeList = getResources().getStringArray(R.array.DressSizeArray);
		shoeSizeList = getResources().getStringArray(R.array.ShoeSizeArray);
		stateList = getResources().getStringArray(R.array.StateArray);
		
		//Get view's Object
		TextView txtHeading =(TextView) findViewById(R.id.txtViewTitle);
		Button btnBack=(Button) findViewById(R.id.navigationLeftBtn);
		Button btnDone=(Button)findViewById(R.id.navigationRightBtn);
		txtDressSize=(TextView) findViewById(R.id.txtDressSize);
		txtShoeSize=(TextView) findViewById(R.id.txtShoeSize);
		txtState=(TextView) findViewById(R.id.txtState);
		
		//login user id
		userId=Utility.getSharedPreferences(appContext, "loginUserId");
		
		//set visiblity
		txtHeading.setVisibility(View.VISIBLE);
		btnDone.setVisibility(View.VISIBLE);
		btnBack.setVisibility(View.VISIBLE);
		
		//set heading text
		txtHeading.setText("Add Profile");
		
		//Set Click listener 
		btnDone.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		txtDressSize.setOnClickListener(this);
		txtShoeSize.setOnClickListener(this);
		txtState.setOnClickListener(this);
		
		
		//add dress size list in dialog
		dialogDresSizeList= new Dialog(appContext);
		dialogDresSizeList.setTitle(R.string.dress_title_txt);
		dialogDresSizeList.setContentView(R.layout.category_listview);
		ListView dressListView = (ListView)dialogDresSizeList.findViewById(R.id.listviewCategory);
		
		// making adaptor object and setting it to listview object
		ArrayAdapter<String> dressSizeListAdaptor = new ArrayAdapter<String>(appContext,R.layout.listview_item, dressSizeList);
		dressListView.setAdapter(dressSizeListAdaptor);
		
		// add shoe size list in dialog
		dialogShoeSizeList = new Dialog(appContext);
		dialogShoeSizeList.setTitle(R.string.shoe_title_txt);
		dialogShoeSizeList.setContentView(R.layout.listview_subcategory);
		ListView shoeListView = (ListView)dialogShoeSizeList.findViewById(R.id.listviewSubCategory);
		
		// making adaptor object and setting it to listview object
		ArrayAdapter<String> shoeSizeListAdaptor = new ArrayAdapter<String>(appContext,R.layout.listview_item, shoeSizeList);
		shoeListView.setAdapter(shoeSizeListAdaptor);
		
		
		// add state size list in dialog
		dialogStateList = new Dialog(appContext);
		dialogStateList.setTitle(R.string.state_title_txt);
		dialogStateList.setContentView(R.layout.listview_subcategory);
		ListView stateListView = (ListView) dialogStateList.findViewById(R.id.listviewSubCategory);
		// making adaptor object and setting it to listview object
		ArrayAdapter<String> stateListAdaptor = new ArrayAdapter<String>(appContext,R.layout.listview_item, stateList);
		stateListView.setAdapter(stateListAdaptor);
		
		dressListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				// TODO Auto-generated method stub
				dialogDresSizeList.dismiss();
				txtDressSize.setText(dressSizeList[pos]);
			}
		});
		
		shoeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				// TODO Auto-generated method stub
				dialogShoeSizeList.dismiss();
				txtShoeSize.setText(shoeSizeList[pos]);		
			}
		});
		stateListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				// TODO Auto-generated method stub
				dialogStateList.dismiss();
				txtState.setText(stateList[pos]);
				
			}
		});
		
	
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.navigationRightBtn){
			
			EditText edtMyCity=(EditText) findViewById(R.id.edtMyCity);
			EditText edtWebsite=(EditText) findViewById(R.id.edtWebsite);
			
			final String strShoeSize=txtShoeSize.getText().toString().trim();
			final String strDressSize=txtDressSize.getText().toString().trim();
			final String strCity=edtMyCity.getText().toString().trim();
			final String strState=txtState.getText().toString().trim();
			final String strWebsite=edtWebsite.getText().toString().trim();
			final String profileImage=Utility.getSharedPreferences(appContext, "UserProfileImage");
			
			Runnable runnImage = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					if (profileImage!=null)
					{
						bitmap = Utility.DownloadImageDirect(profileImage);
					}					
					if(bitmap!=null)
					{
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								strBase64 = Utility.encodeTobase64(bitmap);
								new SignUp().execute(userName,userEmail,password,strShoeSize,strDressSize,strCity,strState,strWebsite,strBase64);
							}
						});
					}
				}
			};
			Thread thread = new Thread(runnImage);
			thread.start();
			
		} else if (v.getId() == R.id.txtDressSize) {
			dialogDresSizeList.show();
		} else if (v.getId() == R.id.txtShoeSize) {
			dialogShoeSizeList.show();
		} else if (v.getId() == R.id.txtState) {
			dialogStateList.show();
		} else if (v.getId() == R.id.navigationLeftBtn) {
			finish();
		}
	}
	

	public class SignUp extends AsyncTask<String, Void, String> {
		ProgressDialog applicationDialog;
		protected void onPreExecute() {
			super.onPreExecute();
			
			applicationDialog = ProgressDialog.show(appContext, "","Please wait...", false, true, new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					SignUp.this.cancel(true);
				}
			});
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
		
			String result = null;
			ArrayList<NameValuePair> listNameValuePairs ;
			listNameValuePairs= new ArrayList<NameValuePair>();
			
			
			listNameValuePairs.add(new BasicNameValuePair("name",params[0]));
			listNameValuePairs.add(new BasicNameValuePair("email",params[1]));
			listNameValuePairs.add(new BasicNameValuePair("password",params[2]));
			if(params[3]!="")
			{
				listNameValuePairs.add(new BasicNameValuePair("shoe_size",params[3]));
			}
			if(params[4]!="")
			{
				listNameValuePairs.add(new BasicNameValuePair("dress_size",params[4]));
			}
			if(params[5]!="")
			{
				listNameValuePairs.add(new BasicNameValuePair("city",params[5]));
			}
			if(params[6]!="")
			{
				listNameValuePairs.add(new BasicNameValuePair("state",params[6]));
			}
			if(params[7]!="")
			{
				listNameValuePairs.add(new BasicNameValuePair("website",params[7]));
			}
			if(params[8]!="null"){
				listNameValuePairs.add(new BasicNameValuePair("profile_pic",params[8]));
			}
			String url=Constant.serverSignUpUrl+userId+"/"+"sign_up";
			result=Utility.postParamsAndfindJSON(url, listNameValuePairs);
			listNameValuePairs.clear();
			return result;
		}

		protected void onPostExecute(String result) {
			System.out.println("result is " + result);
			applicationDialog.dismiss();
			if (result == null) {
				Utility.ShowAlertWithMessage(appContext, R.string.alert,R.string.network_connection_alert);
			} else if(result.contains("Sign Up Failed")){
				Utility.ShowAlertWithMessage(appContext, R.string.alert,R.string.failed_alert);
			}else {
				Utility.setSharedPreference(appContext, "UserProfileData",result);
				Intent intent = new Intent(appContext, HomeActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}
}
