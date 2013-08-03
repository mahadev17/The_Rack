package com.tbldevelopment.therackapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;
import com.tbldevelopment.therackapp.EditProfileActivity.PostuserInfo;

public class SignInActivity extends Activity implements OnClickListener{
	private Context appContext;
	private String userName;
	private String password;
	private Button btnDone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		
		appContext = this;

		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);

		TextView txtTitle=(TextView) findViewById(R.id.txtViewTitle);
		txtTitle.setText("Login");
		txtTitle.setVisibility(View.VISIBLE);
		
		btnDone=(Button) findViewById(R.id.btnDoneSignIn);
		btnDone.setBackgroundResource(R.drawable.login_btn);
		btnDone.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.btnDoneSignIn){
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(btnDone.getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
			EditText edtUserName=(EditText) findViewById(R.id.edtEmail);
			EditText edtPassword=(EditText) findViewById(R.id.edtPassword);
			userName=edtUserName.getText().toString().trim();
			password=edtPassword.getText().toString().trim();
			if(userName.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.email_alert);
			}else if(password.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.password_alert);
			}else{
				Utility.setSharedPreference(appContext, "UserName", userName);
				Utility.setSharedPreference(appContext, "UserImage", "");
				new SignIn().execute();
			}	
		}
	}
	
	public class SignIn extends AsyncTask<Void, Void, String> {
		ProgressDialog applicationDialog;
		protected void onPreExecute() {
			super.onPreExecute();
			applicationDialog = ProgressDialog.show(appContext, "","Please wait...", false, true, new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					SignIn.this.cancel(true);
				}
			});
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String result;
			String url = Constant.serverUrl+"sign_in?name="+userName+"&password="+password;
			result = Utility.findJSONFromUrl(url);
			if(result!=null){
				try {
					JSONArray jsonArray=new JSONArray(result);
					JSONObject jsonObject=jsonArray.getJSONObject(0);
					Utility.setSharedPreference(appContext, "UserProfileData", jsonObject.toString());
					String userId=jsonObject.getString("id"); 
					Utility.setSharedPreference(appContext, "loginUserId", userId);
					jsonArray=null;
					jsonObject=null;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return result;
		}
		protected void onPostExecute(String result) {
			System.out.println("result is " + result);
			applicationDialog.dismiss();
			if (result == null) {
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.network_connection_alert);
			}else if(result.contains("Authentication Failed")){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.sign_in_alert);
			}else{
				Intent intent =new Intent(appContext,HomeActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}
}
