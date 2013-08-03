package com.tbldevelopment.therackapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;

public class ShareOnFacebook extends Activity {

	private static final String APP_ID = "296725640459209";
	private static final String[] PERMISSIONS = new String[] { "publish_stream","email","user_photos"};
	String response;
	Context appContext;
	RelativeLayout facebook_layout;

	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";

	private com.facebook.android.AsyncFacebookRunner mAsyncRunner;;
	
	private Facebook facebook;
	//private ShowDetailsActivity sd;
	//private PostRewards sr;
	private String username;
	private String email;
	private String isSignUp;
	private String userId;
	ProgressDialog applicationDialog;
	public boolean saveCredentials(Facebook facebook) {
		Editor editor = this.getSharedPreferences(KEY, Context.MODE_PRIVATE)
				.edit();
		editor.putString(TOKEN, facebook.getAccessToken());
		editor.putLong(EXPIRES, facebook.getAccessExpires());
		return editor.commit();
	}

	public boolean restoreCredentials(Facebook facebook) {
		SharedPreferences facebookSession = this.getPreferences(MODE_PRIVATE); // this.getSharedPreferences(KEY,
																				// Context.MODE_PRIVATE);
		facebook.setAccessToken(facebookSession.getString(TOKEN, null));
		facebook.setAccessExpires(facebookSession.getLong(EXPIRES, 0));
		return facebook.isSessionValid();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		facebook = new Facebook(APP_ID);
		restoreCredentials(facebook);

		mAsyncRunner = new com.facebook.android.AsyncFacebookRunner(facebook);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.facebook_dialog);
		facebook_layout=(RelativeLayout) findViewById(R.id.facebook_mail_layout);

		appContext = this;	
	}

	public void doNotShare(View button) {
		finish();
	}

	public void share(View button) {

		try {
			if (!facebook.isSessionValid()) {
				facebook_layout.setVisibility(View.GONE);
				loginAndPostToWall();
				
			} else {
				//postToWall(messageToPost);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	

	public void loginAndPostToWall() {
		// facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new
		// LoginDialogListener();
		facebook.authorize(this, PERMISSIONS, new LoginDialogListener());
	}

	
	class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			saveCredentials(facebook);
			applicationDialog = ProgressDialog.show(appContext, "","Please Wait...");
			applicationDialog.setCancelable(true);
			getProfileInformation();
		}

		public void onFacebookError(FacebookError error) {
			showToast("Authentication with Facebook failed!");
			finish();
		}

		public void onError(DialogError error) {
			showToast("Authentication with Facebook failed!");
			finish();
		}

		public void onCancel() {
			showToast("Authentication with Facebook cancelled!");
			finish();
		}
	}

	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	
	public void getProfileInformation() {
		mAsyncRunner.request("me", new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				Log.d("Profile", response);
				String json = response;
				try {
					// Facebook Profile JSON data
					JSONObject profile = new JSONObject(json);
					System.out.println("json responce is  "+response);
					
					// getting name of the user
					username= profile.getString("name");
					email=profile.getString("email");
					Utility.setSharedPreference(appContext, "UserEmail", email); 
					
					// getting email of the user
					final String username = profile.getString("username");
					String imageUrl="https://graph.facebook.com/"+username+"/picture";
					Utility.setSharedPreference(appContext, "UserProfileImage", imageUrl);					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							new SocialLogin().execute();
						}
					});

					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}
	
	public class SocialLogin extends AsyncTask<Void, Void, String> {

		protected void onPreExecute() {
			super.onPreExecute();
			
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String result=null;
			String	url = Constant.serverUrl +"social_sign_in?email="+email;
			result = Utility.findJSONFromUrl(url);
			if (result != null) {	
				try {

					JSONObject jsonObject = new JSONObject(result);
					userId = jsonObject.getString("id");
					Utility.setSharedPreference(appContext, "UserProfileData", jsonObject.toString());
					isSignUp = jsonObject.getString("is_sign_up");
					Utility.setSharedPreference(appContext, "loginUserId", userId);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return result;
		}

		protected void onPostExecute(String result) {
			applicationDialog.dismiss();
			if (result == null) {
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.network_connection_alert);
			}else if(result.contains("Sign Up Failed")){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.failed_alert);
			}else{
				if(isSignUp.equalsIgnoreCase("true")){
					Intent intent =new Intent(appContext,AddUserInfo.class);
					startActivity(intent);
					finish();
				}else if(isSignUp.equalsIgnoreCase("false")){
					Intent intent =new Intent(appContext,HomeActivity.class);
					startActivity(intent);
					finish();
				}	
			}
		}
	}
}
