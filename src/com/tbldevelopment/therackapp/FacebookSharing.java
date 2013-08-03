package com.tbldevelopment.therackapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.tbldevelopment.rackapp.constant.Constant;

public class FacebookSharing extends Activity {

	private static final String APP_ID = "296725640459209";
	private static final String[] PERMISSIONS = new String[] { "publish_stream" };
	String response;
	Context appContext;

	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";

	private Facebook facebook;
	private String messageToPost;
	
	
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

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.facebook_dialog);

		appContext = this;
		String facebookMessage = getIntent().getStringExtra("facebookMessage");
		
		TextView txtSharing=(TextView) findViewById(R.id.txtSharingHeading);
		txtSharing.setText("Do you sharing with your friends.");
		
		/*byte[] videoShare=getIntent().getByteArrayExtra("Video");
		byte[] imageShare=getIntent().getByteArrayExtra("Image");*/
		if (facebookMessage == null) {
			facebookMessage = Constant.MESSAGE;
		}
		messageToPost = facebookMessage;
	}

	public void doNotShare(View button) {
		finish();
	}

	public void share(View button) {

		try {
			if (!facebook.isSessionValid()) {
				loginAndPostToWall();
			} else {
				postToWall(messageToPost);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void loginAndPostToWall() {
		// facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new
		// LoginDialogListener());
		facebook.authorize(this, PERMISSIONS, new LoginDialogListener());
	}

	@SuppressWarnings("deprecation")
	public void postToWall(String message) {

		new PostStatus(message).execute();
		finish();
		final Bundle parameters = new Bundle();
		parameters.putString("message", message);
		
		parameters.putString("description", "topic share");
		Thread postStatusThread = new Thread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub

				try {

					facebook.request("me");

					response = facebook.request("me/feed", parameters, "POST");
					Log.d("Tests", "got response: " + response);
					finish();

				} catch (Exception e) {
					response = "1";
					e.printStackTrace();
					finish();
				}
			}
		});
		/*
		 * if (response == null || response.equals("") ||
		 * response.equals("false")) {
		 * System.out.println("response is : "+response);
		 * showToast("Blank response."); } else if (response == "1") {
		 * System.out.println("response is : "+response);
		 * showToast("Failed to post to wall"); } else {
		 * System.out.println("response is : "+response);
		 * showToast("Message posted to your facebook wall!"); }
		 */
		// postStatusThread.start();

	}

	class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			saveCredentials(facebook);
			//if (messageToPost != null && videoToPost!=null && imageToPost!=null) {
				postToWall(messageToPost);
			//}
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

	public class PostStatus extends AsyncTask<Void, Void, String> {
		String message;

		public PostStatus(String msg) {
			message = msg;
		}

		public void onPrexecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			final Bundle parameters = new Bundle();
			parameters.putString("message", message);
//			parameters.putString("message", "");
			parameters.putString("description", "topic share");
			//parameters.putByteArray("video", data);

			try {

				facebook.request("me");

				response = facebook.request("me/feed", parameters, "POST");
				
				Log.d("Tests", "got response: " + response);
			} catch (Exception e) {
				response = "1";
				e.printStackTrace();
			}
			return response;
		}

		public void onPostExecute(String response) {

			if (response == null || response.equals("")
					|| response.equals("false")) {
				System.out.println("response is : " + response);
				showToast("Blank response.");
			} else if (response == "1") {
				System.out.println("response is : " + response);
				showToast("Failed to post to wall");
			} else {
				System.out.println("response is : " + response);
				showToast("Message posted to your facebook wall!");
			}

		}

	}
}
