package com.tbldevelopment.therackapp;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;

public class MyRackActivity extends Activity implements OnClickListener{
	
	private Context appContext;
	private Button btnAddRack;
	private TextView txtHeading;
	private ImageView profileImage;
	private String strProfileImage;
	private Bitmap bitmap = null;
	
	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myrack);
		
		//set Context
		appContext=this;
		
		//Inflate Navigation Bar
		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
		
		//View's Object
		btnAddRack= (Button)findViewById(R.id.navigationRightBtn);
		txtHeading= (TextView)findViewById(R.id.txtViewTitle);
		profileImage=(ImageView) findViewById(R.id.profileImageNavBar);
		TextView txtMyProfile=(TextView) findViewById(R.id.txtMyProfile);
		TextView txtMyWardrobe=(TextView) findViewById(R.id.txtMyWardrobe);
		TextView txtLogout=(TextView) findViewById(R.id.txtLogout);
		TextView txtMyNotification=(TextView) findViewById(R.id.txtNotification);
		TextView txtMyLikes=(TextView) findViewById(R.id.txtMyLikes);
		TextView txtMySell=(TextView) findViewById(R.id.txtMySell);
		TextView txtMyPurchase=(TextView) findViewById(R.id.txtMyPurchase);
		LinearLayout linearLayInviteFrnd=(LinearLayout) findViewById(R.id.linearLayoutInviteFriend);
		
		//Visible View
		txtHeading.setVisibility(View.VISIBLE);
		profileImage.setVisibility(View.VISIBLE);
		btnAddRack.setVisibility(View.VISIBLE);
		Utility.setSharedPreferenceBoolean(appContext, "refreshUserDataInfo", true);
		getUserInfo();
		
		//Set Button Background in navigation right button 
		btnAddRack.setBackgroundResource(R.drawable.add_rack_btn);
		
		//Set ClickListener
		btnAddRack.setOnClickListener(this);
		linearLayInviteFrnd.setOnClickListener(this);
		txtMyProfile.setOnClickListener(this);
		txtMyNotification.setOnClickListener(this);
		txtLogout.setOnClickListener(this);
		txtMyWardrobe.setOnClickListener(this);
		txtMyLikes.setOnClickListener(this);
		txtMyPurchase.setOnClickListener(this);
		txtMySell.setOnClickListener(this);
		
	}//OnCrate Finish
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.navigationRightBtn){
			//Add Rack Item Textview Click
			Intent intentaddrack=new Intent(appContext,AddItemActivity.class);
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			intentaddrack.putExtra("requestFor", Constant.GET_CATEGORY);
			startActivity(intentaddrack);
		}else if(v.getId()==R.id.linearLayoutInviteFriend){
			//Invite Friend Textview Click
			Intent i=new Intent(appContext,SocialActivity.class);
			startActivity(i);
		}else if(v.getId()==R.id.txtMyProfile){
			//Profile Textview Click
			Intent i=new Intent(appContext,EditProfileActivity.class);
			startActivity(i);
		}else if(v.getId()==R.id.txtNotification){
			//Notification Textview Click
			Intent i=new Intent(appContext,NotificationActivity.class);
			i.putExtra("requestFor", Constant.GET_SETTING);
			startActivity(i);
		}else if(v.getId()==R.id.txtLogout){
			// Logout Textview Click
			ShowLogoutAlert(appContext,R.string.alert,R.string.logout_alert);
		}else if(v.getId()==R.id.txtMyWardrobe){
			//My wardrobe Textview Click
			Intent i=new Intent(appContext,GetUserInfo.class);
			i.putExtra("userInfoData", Utility.getSharedPreferences(appContext, "UserProfileData"));
			i.putExtra("userID", Utility.getSharedPreferences(appContext, "loginUserId"));
			i.putExtra("requestFor",Constant.USER_INFO);
			startActivity(i);
		}else if(v.getId()==R.id.txtMyLikes){
			//My likes Textview Click
			Intent i=new Intent(appContext,GetItemActivity.class);
			i.putExtra("requestFor", Constant.MY_LIKES);
			i.putExtra("userID", Utility.getSharedPreferences(appContext, "loginUserId"));
			startActivity(i);
		}else if(v.getId()==R.id.txtMySell){
			//My Sell Textview Click
			Intent i=new Intent(appContext,GetUserInfo.class);
			i.putExtra("requestFor", Constant.MY_SALE);
			i.putExtra("userInfoData", Utility.getSharedPreferences(appContext, "UserProfileData"));
			i.putExtra("userID", Utility.getSharedPreferences(appContext, "loginUserId"));
			startActivity(i);
		}else if(v.getId()==R.id.txtMyPurchase){
			//My Purchase Textview Click
			Intent i=new Intent(appContext,GetUserInfo.class);
			i.putExtra("requestFor", Constant.MY_PURCHASE);
			i.putExtra("userInfoData", Utility.getSharedPreferences(appContext, "UserProfileData"));
			i.putExtra("userID", Utility.getSharedPreferences(appContext, "loginUserId"));
			startActivity(i);
		}
		
	}
	
    private void ShowLogoutAlert(Context context, int alerttitle,int locationvalidation) {
		// Assign the alert builder , this can not be assign in Click events
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		builder.setMessage(locationvalidation);
		builder.setTitle(alerttitle);
		// Set behavior of negative button
		builder.setNegativeButton(" No ", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// Cancel the dialog
				dialog.dismiss();
			}
		});
		
		builder.setPositiveButton(" Yes ", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// Cancel the dialog
				dialog.dismiss();
				Editor editor = appContext.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
				editor.putString(TOKEN, null);
				editor.putLong(EXPIRES, 0);
				editor.commit();
				
				Intent i=new Intent(appContext,SplashScreen.class);
				i.addCategory(Intent.CATEGORY_LAUNCHER);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
				Utility.setSharedPreferenceBoolean(appContext, "isLogin", false);
				startActivity(i);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void getUserInfo() {
		if (Utility.getSharedPreferences(appContext, "UserProfileData") != null) {

			try {
				JSONObject jsonObject = new JSONObject(
						Utility.getSharedPreferences(appContext,
								"UserProfileData"));
				txtHeading.setText("@ " + jsonObject.getString("name"));
				strProfileImage = jsonObject.getString("profile_pic");
				Runnable runnImage = new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String ImgURL = "";

						if (strProfileImage == null) {
						} else {
							bitmap = Utility
									.DownloadImageDirect(strProfileImage);
						}

						if (bitmap != null) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									profileImage.setImageBitmap(bitmap);
								}
							});
						}
					}
				};
				Thread thread = new Thread(runnImage);
				thread.start();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(Utility.getSharedPreferencesBoolean(appContext, "refreshUserDataInfo")){
			getUserInfo();
			Utility.setSharedPreferenceBoolean(appContext, "refreshUserDataInfo", false);
		}
	}
}//Main Class Finish
