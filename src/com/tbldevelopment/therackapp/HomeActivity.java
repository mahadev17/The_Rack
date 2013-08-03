package com.tbldevelopment.therackapp;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;

public class HomeActivity extends Activity implements OnClickListener{

	private Context appContext;
	private Bitmap bitmap = null;
	private String strProfileImage;
	private ImageView userImage;
	private TextView txtUserName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home_view);
		
		appContext=this;
		
		//Check user Already login or not
		Utility.setSharedPreferenceBoolean(appContext, "isLogin", true);
		
		//Accessing view through ids
		txtUserName=(TextView) findViewById(R.id.profile_name);
		userImage=(ImageView) findViewById(R.id.profile_pic_imageview);
		ImageButton btnMen=(ImageButton) findViewById(R.id.btnMen);
		ImageButton btnWomen=(ImageButton) findViewById(R.id.btnWomen);
		ImageButton btnMsg=(ImageButton) findViewById(R.id.btnMsg);
		ImageButton btnKids=(ImageButton) findViewById(R.id.btnKids);
		ImageButton btnCommunity=(ImageButton) findViewById(R.id.btnCommunity);
		ImageButton btnRack=(ImageButton) findViewById(R.id.btnRack);
		
		Utility.setSharedPreferenceBoolean(appContext, "refreshUserInfoHome", true);
		getUserInfoData();
		
		btnMen.setOnClickListener(this);
		btnWomen.setOnClickListener(this);
		btnKids.setOnClickListener(this);
		btnCommunity.setOnClickListener(this);
		btnRack.setOnClickListener(this);
		btnMsg.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.btnMen){
			Intent i=new Intent(getApplicationContext(),GetItemActivity.class);
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			i.putExtra("requestFor", Constant.GET_ITEM);
			i.putExtra("categoryId", "3");
			startActivity(i);
		}else if(v.getId()==R.id.btnWomen){
			Intent i=new Intent(getApplicationContext(),GetItemActivity.class);
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			i.putExtra("requestFor", Constant.GET_ITEM);
			i.putExtra("categoryId", "4");
			startActivity(i);
		}else if(v.getId()==R.id.btnKids){
			Intent i=new Intent(getApplicationContext(),GetItemActivity.class);
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			i.putExtra("requestFor", Constant.GET_ITEM);
			i.putExtra("categoryId", "5");
			startActivity(i);
		}else if(v.getId()==R.id.btnRack){
			Intent i=new Intent(getApplicationContext(),MyRackActivity.class);
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			startActivity(i);
		}else if(v.getId()==R.id.btnMsg){
			Intent i=new Intent(getApplicationContext(),MessageActivity.class);
			overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			startActivity(i);
		}
	}	
	
	public void getUserInfoData(){
		
		String userInfoData = Utility.getSharedPreferences(appContext,
				"UserProfileData");
		try {
			JSONObject jsonObject = new JSONObject(userInfoData);
			// new DownloadImageTask().execute();
			txtUserName.setText("Welcome " + jsonObject.getString("name"));

			strProfileImage = jsonObject.getString("profile_pic");
			Runnable runnImage = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub

					if (strProfileImage != null) {
						bitmap = Utility.DownloadImageDirect(strProfileImage);
					}
					if (bitmap != null) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								userImage.setImageBitmap(bitmap);
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
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(Utility.getSharedPreferencesBoolean(appContext, "refreshUserInfoHome")){
			getUserInfoData();
			Utility.setSharedPreferenceBoolean(appContext, "refreshUserInfoHome", false);
		}
	}
	
}
