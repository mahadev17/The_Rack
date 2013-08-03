package com.tbldevelopment.therackapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;
import com.tbldevelopment.therackapp.GetItemActivity.GetItems;

public class AddUserInfo extends Activity implements OnClickListener{
	private Context appContext;
	private EditText edtEmail,edtUserName,edtPassword;
	private String username,password,email,userId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		appContext=this;
		
		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
		
		
		TextView txtHeading=(TextView) findViewById(R.id.txtViewTitle);
		Button btnNext=(Button) findViewById(R.id.btnNextSignUp);
		edtEmail=(EditText) findViewById(R.id.edtEmail);
		edtUserName=(EditText) findViewById(R.id.edtUserName);
		edtPassword=(EditText) findViewById(R.id.edtPassword);
		
		edtEmail.setText(Utility.getSharedPreferences(appContext, "UserEmail"));
		userId=Utility.getSharedPreferences(appContext, "loginUserId");
		
		txtHeading.setVisibility(View.VISIBLE);
		txtHeading.setText("Add Information");
		btnNext.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId()==R.id.btnNextSignUp){
			
			username=edtUserName.getText().toString().trim();
			password =edtPassword.getText().toString().trim();
			email=edtEmail.getText().toString().trim();
			
			if(email.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.email_alert);
			}else if(username.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.username_alert);
			}else if(password.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.password_alert);
			}else{
				new CheckUserName().execute();
			}
		}
	}
	
	public class CheckUserName extends AsyncTask<Void, Void, String> {
		
		ProgressDialog applicationDialog;
		protected void onPreExecute() {
			super.onPreExecute();
			
			applicationDialog = ProgressDialog.show(appContext, "",
					"Please wait...", false, true, new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub
							CheckUserName.this.cancel(true);
						}
					});
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String result=null;
			String url = Constant.serverSignUpUrl+userId+"/"+"check_name?name="+username;
			result = Utility.findJSONFromUrl(url);
			return result;
		}

		protected void onPostExecute(String result) {
			System.out.println("result is " + result);
			if (result == null) {
				applicationDialog.dismiss();
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.network_connection_alert);
			}else if(result.equals("1")){
				applicationDialog.dismiss();
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.username_alert);
			}else if(result.contains("-1")){
				applicationDialog.dismiss();
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.acc_alert);
			}else if(result.contains("0")){
				applicationDialog.dismiss();
				Utility.setSharedPreference(appContext, "UserName", username);
				Utility.setSharedPreference(appContext, "UserEmail", email);
				Intent intent =new Intent(appContext,AddUserProfile.class);
				intent.putExtra("userName", username);
				intent.putExtra("Password", password);
				intent.putExtra("userEmail", email);
				startActivity(intent);
				finish();
			}
		}
	
	}

}
