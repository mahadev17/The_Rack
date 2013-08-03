package com.tbldevelopment.therackapp;

import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.http.AccessToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbldevelopment.rackapp.twitter.TwitterApp;
import com.tbldevelopment.rackapp.twitter.TwitterApp.TwDialogListener;
import com.tbldevelopment.rackapp.twitter.TwitterSession;

public class SocialActivity extends Activity implements OnClickListener {
	private Context appContext;
	private Button btnBack;

	// Twitter
	public static final String twitter_consumer_key = "Mb7bncMfXJRVgQADWQWwQ";
	public static final String twitter_secret_key = "2nKX9m7sKADSlaBpxmdcc18nVUtZm1liO16Tggh28";
	private TwitterApp mTwitter;
	private SharedPreferences prefs;
	private final Handler mTwitterHandler = new Handler();
	final Runnable mUpdateTwitterNotification = new Runnable() {
		public void run() {
			Toast.makeText(getBaseContext(), "Tweet sent !", Toast.LENGTH_LONG)
					.show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social);

		appContext = this;
		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);

		TextView txtHeading = (TextView) findViewById(R.id.txtViewTitle);

		Button btnFb = (Button) findViewById(R.id.btnFb);
		Button btnTwitter = (Button) findViewById(R.id.btnTwitter);

		txtHeading.setText("Find Your Friends");
		txtHeading.setVisibility(View.VISIBLE);
		btnBack = (Button) findViewById(R.id.navigationLeftBtn);
		btnBack.setVisibility(View.VISIBLE);

		btnFb.setOnClickListener(this);
		btnTwitter.setOnClickListener(this);
		btnBack.setOnClickListener(this);

		mTwitter = new TwitterApp(appContext, twitter_consumer_key,
				twitter_secret_key);
		mTwitter.setListener(mTwLoginDialogListener);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == R.id.navigationLeftBtn) {
			finish();
		} else if (v.getId() == R.id.navigationRightBtn) {

		} else if (v.getId() == R.id.btnFb) {
			Intent i = new Intent(appContext, FacebookSharing.class);
			startActivity(i);
		}else if(v.getId()==R.id.btnTwitter){
			onTwitterClick();
		}
	}

	// Twitter Sharing

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		public void onComplete(String value) {
			System.out.println("value " + value);
			String username = mTwitter.getUsername();
			if (username != null)
				username = (username.equals("")) ? "No Name" : username;
			/*
			 * try { mTwitter.updateStatus(verse); Toast.makeText(appContext,
			 * "Prayer Posted to Twitter for " + username, handle exception
			 * Utility.ShowAlertWithMessage(appContext, "Failed",
			 * "Error Occur while posting this prayer"); e.printStackTrace(); }
			 */
			if (mTwitter.hasAccessToken()) {
				new TwitterSender().execute();
			} else {
				Toast.makeText(appContext, "Please Login First",Toast.LENGTH_SHORT).show();
			}
		}

		public void onError(String value) {

		}
	};

	private class TwitterSender extends AsyncTask<URL, Integer, Long> {
		private String url;
		ProgressDialog mProgressDialog;

		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(appContext, "","Wait...", true);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}

		protected Long doInBackground(URL... urls) {
			long result = 0;

			TwitterSession twitterSession = new TwitterSession(appContext);
			AccessToken accessToken = twitterSession.getAccessToken();

			twitter4j.conf.Configuration conf = new ConfigurationBuilder()
					.setOAuthConsumerKey(twitter_consumer_key)
					.setOAuthConsumerSecret(twitter_secret_key)
					.setOAuthAccessToken(accessToken.getToken())
					.setOAuthAccessTokenSecret(accessToken.getTokenSecret())
					.build();

			/*
			 * OAuthAuthorization auth = new OAuthAuthorization(conf,
			 * conf.getOAuthConsumerKey(), conf.getOAuthConsumerSecret(), new
			 * AccessToken(conf.getOAuthAccessToken(),
			 * conf.getOAuthAccessTokenSecret()));
			 * 
			 * ImageUpload upload = ImageUpload.getTwitpicUploader(
			 * twitpic_api_key, auth);
			 * 
			 * Log.d("", "Start sending image...");
			 */
			try {
				/*
				 * String path = Environment.getExternalStorageDirectory()
				 * .toString(); url = upload.upload(new File(path,
				 * "emoticon.png"));
				 */
				result = 1;
				Twitter tt = new TwitterFactory(conf).getInstance();
				String currentDateTimeString = DateFormat.getDateTimeInstance()
						.format(new Date());
				twitter4j.Status response = tt.updateStatus("Share with Rack App"+ currentDateTimeString);// posting
				// status
				// to
				// twitter
				System.out.println("status is : " + response);

			} catch (Exception e) {
				Log.e("", "Failed to send status");
				result = 0;
				e.printStackTrace();
			}

			return result;
		}

		protected void onPostExecute(Long result) {

			if (result == 1) {
				Toast.makeText(appContext, "Successfully invited to friends", Toast.LENGTH_SHORT).show();
			} else if (result == 0) {
				Toast.makeText(appContext, "Failed Post", Toast.LENGTH_SHORT).show();
			}
			if (mProgressDialog != null) {
				mProgressDialog.cancel();
				// finish();
			}
		}
	}
	
	private void onTwitterClick() {
		if (mTwitter.hasAccessToken()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(appContext);

			builder.setMessage("Delete current Twitter connection?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									mTwitter.resetAccessToken();
									/*
									 * twitterBtn
									 * .setBackgroundDrawable(getResources()
									 * .getDrawable(
									 * R.drawable.twitter_check_off));
									 */// mTwitterBtn.setChecked(false);
										// mTwitterBtn.setText("  Twitter (Not connected)");
										// mTwitterBtn.setTextColor(Color.GRAY);
									/*
									 * mText.setText("No Login");
									 * mUserPic.setImageDrawable(getResources()
									 * .getDrawable(
									 * android.R.drawable.alert_dark_frame));
									 */}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									// twitterBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.twitter_check_on));
									// mTwitterBtn.setChecked(true);
								}
							});
			final AlertDialog alert = builder.create();

			alert.show();
		} else {
			// tweetButton.setChecked(false);

			mTwitter.authorize();
		}
	}

}
