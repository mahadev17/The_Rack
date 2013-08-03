package com.tbldevelopment.therackapp;

import com.facebook.android.Util;
import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;
import com.tbldevelopment.therackapp.FollowFollowingActivity.GetFollowerFollowing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

public class ChangePasswordActivity extends Activity implements OnClickListener{
	private Context appContext;
	private Button btnEdit;
	private Button btnBack;
	private TextView txtHeading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		
		// initialize Context
		appContext = this;

		// Inflate navigation Bar
		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);

		// View's Object
		btnEdit = (Button) findViewById(R.id.navigationRightBtn);
		btnBack = (Button) findViewById(R.id.navigationLeftBtn);
		txtHeading = (TextView) findViewById(R.id.txtViewTitle);

		// Set view's Visiblity
		txtHeading.setVisibility(View.VISIBLE);
		btnBack.setVisibility(View.VISIBLE);
		btnEdit.setVisibility(View.VISIBLE);

		//edit button clickable 
		btnEdit.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		
		// set heading text
		txtHeading.setText("Change Password");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.navigationLeftBtn){
			finish();
		}else if(v.getId()==R.id.navigationRightBtn){
			EditText edtOdlPassword=(EditText) findViewById(R.id.edtOldPassword);
			EditText edtNewPassword=(EditText) findViewById(R.id.edtNewPassword);
			EditText edtConfirmPassword=(EditText) findViewById(R.id.edtConfirmPassword);
			
			String strOldPass=edtOdlPassword.getText().toString().trim();
			String strNewPass=edtNewPassword.getText().toString().trim();
			String strConPass=edtConfirmPassword.getText().toString().trim();
			
			if(strOldPass.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.old_password_alert);
			}else if(strNewPass.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.new_password_alert);
			}else if(strConPass.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.con_password_alert);
			}else if(!strNewPass.equals(strConPass)){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.match_password_alert);
			}else{
				new PostChangePassword().execute(strOldPass,strNewPass,strConPass);
			}
		}
	}
	
	public class PostChangePassword extends AsyncTask<String, Void, String>{

		ProgressDialog applicationDialog;
		protected void onPreExecute() {
			super.onPreExecute();
			
			applicationDialog = ProgressDialog.show(appContext, "","Please wait...", false, true, new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					PostChangePassword.this.cancel(true);
				}
			});
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String result;
			String userId=Utility.getSharedPreferences(appContext, "loginUserId");
			String url=Constant.serverUrl+"reset_pass?user_id="+userId+"&old_password="+params[0]+"&password="+params[1]+"&password_confirmation="+params[2];
			result=Utility.findJSONFromUrl(url);		
			return result;
		}	
		
		protected void onPostExecute(String result) {
			System.out.println("result is " + result);
			applicationDialog.dismiss();
			if (result == null) {
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.network_connection_alert);
			}else if(result.equals("Authentication Failed")) {
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.failed_alert);
			}else {
				finish();
			}
			
		}
		
	}

}
