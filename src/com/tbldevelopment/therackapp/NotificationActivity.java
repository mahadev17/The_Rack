package com.tbldevelopment.therackapp;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;
import com.tbldevelopment.therackapp.EditProfileActivity.PostuserInfo;

public class NotificationActivity extends Activity implements OnClickListener,OnCheckedChangeListener{
	
	private Context appContext;
	private Button btnBack;
	private Button btnDone;
	private int requestFor;
	private String loginUserId;
	private String strCommentSetting;
	private String strLikeSetting;
	private String strEventInviteSetting;
	private String strFollowSetting;
	private String strEventRemainderSetting;
	private ToggleButton tbtnLike,tbtnComment,tbtnFollow,tbtnEventinvite,tbtnEventRemainder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);
		
		appContext = this;
		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
		
		btnBack=(Button) findViewById(R.id.navigationLeftBtn);
		btnDone=(Button) findViewById(R.id.navigationRightBtn);
		
		loginUserId=Utility.getSharedPreferences(appContext, "loginUserId");
		
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			requestFor=bundle.getInt("requestFor");
		}
		
		TextView txtTitle=(TextView)findViewById(R.id.txtViewTitle);
		txtTitle.setVisibility(View.VISIBLE);
		txtTitle.setText("Notification");
		tbtnLike=(ToggleButton) findViewById(R.id.togglebtnLike);
		tbtnComment=(ToggleButton) findViewById(R.id.togglebtnComment);
		tbtnFollow=(ToggleButton) findViewById(R.id.togglebtnFollow);
		tbtnEventinvite=(ToggleButton) findViewById(R.id.togglebtnEventinvite);
		tbtnEventRemainder=(ToggleButton) findViewById(R.id.togglebtnEventRemainder);
		
		btnBack.setVisibility(View.VISIBLE);
		btnDone.setVisibility(View.VISIBLE);
		
		btnBack.setBackgroundResource(R.drawable.account_page_btn);
		
		
		btnBack.setOnClickListener(this);
		btnDone.setOnClickListener(this);
		tbtnLike.setOnCheckedChangeListener(this);
		tbtnComment.setOnCheckedChangeListener(this);
		tbtnFollow.setOnCheckedChangeListener(this);
		tbtnEventinvite.setOnCheckedChangeListener(this);
		tbtnEventRemainder.setOnCheckedChangeListener(this);
		
		new GetNotificationSetting().execute();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId()==R.id.navigationLeftBtn){
			finish();
		}else if(v.getId()==R.id.navigationRightBtn){
			requestFor=Constant.SET_SETTING;
			new GetNotificationSetting().execute();
		}
	}
	
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (buttonView.getId() == R.id.togglebtnLike) {
			if (isChecked) {
				strLikeSetting = "1";
			} else {
				strLikeSetting = "0";
			}
		} else if (buttonView.getId() == R.id.togglebtnComment) {
			if (isChecked) {
				strCommentSetting = "1";
			} else {
				strCommentSetting = "0";
			}
		} else if (buttonView.getId() == R.id.togglebtnFollow) {
			if (isChecked) {
				strFollowSetting = "1";
			} else {
				strFollowSetting = "0";
			}
		} else if (buttonView.getId() == R.id.togglebtnEventinvite) {
			if (isChecked) {
				strEventInviteSetting = "1";
			} else {
				strEventInviteSetting = "0";
			}
		} else if (buttonView.getId() == R.id.togglebtnEventRemainder) {
			if (isChecked) {
				strEventRemainderSetting = "1";
			} else {
				strEventRemainderSetting = "0";
			}
		}
		
		
	}
	
	public class GetNotificationSetting extends AsyncTask<Void, Void, String> {

		String result;
		ProgressDialog applicationDialog;
		String url;

		protected void onPreExecute() {
			super.onPreExecute();
			
			applicationDialog = ProgressDialog.show(appContext, "","Please wait...", false, true, new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					GetNotificationSetting.this.cancel(true);
				}
			});
			
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub

				if(requestFor==Constant.GET_SETTING){
					url=Constant.serverUrl+"get_settings?user_id="+loginUserId;
				}else if(requestFor==Constant.SET_SETTING){
					url=Constant.serverUrl+"save_settings?user_likes="+strLikeSetting+"&user_comments="+strCommentSetting+"&user_id="+loginUserId;
				}
				result = Utility.findJSONFromUrl(url);
				if(result!=null){
					try {
						JSONObject jsonObject=new JSONObject(result);
						if(jsonObject.getString("user_likes").equals("true")){
							strLikeSetting="1";
						}else if(jsonObject.getString("user_likes").equals("false")){
							strLikeSetting="0";
						}
						if(jsonObject.getString("user_comments").equals("true")){
							strCommentSetting="1";
						}else if(jsonObject.getString("user_comments").equals("false")){
							strCommentSetting="0";
						}
						
						
						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			
				return result;
		}

		protected void onPostExecute(String result) {
			System.out.println("result is " + result);
			if (result == null) {
				applicationDialog.dismiss();
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.network_connection_alert);
			}else if(result.equals("0")){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.failed_alert);
			}else{
				applicationDialog.dismiss();
				if(requestFor==Constant.GET_SETTING){
					if(strLikeSetting.equals("1")){
						tbtnLike.setChecked(true);
					}else if(strLikeSetting.equals("0")){
						tbtnLike.setChecked(false);
					}
					
					if(strCommentSetting.equals("1")){
						tbtnComment.setChecked(true);
					}else if(strCommentSetting.equals("0")){
						tbtnComment.setChecked(false);
					}
				}else if(requestFor==Constant.SET_SETTING){
					Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.setting_save_alert);
				}
			}
			
		}
	}

	
}
