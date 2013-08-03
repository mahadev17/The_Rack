package com.tbldevelopment.therackapp;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbldevelopment.rackapp.adapter.GetItemAdapter;
import com.tbldevelopment.rackapp.adapter.ItemAdapter;
import com.tbldevelopment.rackapp.adapter.ItemCommentAdapter;
import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;
import com.tbldevelopment.therackapp.EditProfileActivity.PostuserInfo;

public class FollowFollowingActivity extends Activity implements OnClickListener{
	private Context appContext;
	private Button btnBack;
	private int requestFor;
	private String loginUserId;
	
	private ArrayList<JSONObject> arrayJsonList;
	private GetItemAdapter getItemAdapter;
	private ListView listviewItem;
	private String userId;
	private TextView txtHeading;
	public HashMap<String, Bitmap> hashImages;
	private Thread thread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);
		
		appContext=this;
		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
		
		loginUserId=Utility.getSharedPreferences(appContext, "loginUserId");
		
		Bundle bundle=getIntent().getExtras();
		
		if(bundle!=null){
			userId=bundle.getString("userId");
			requestFor=bundle.getInt("requestFor");
		}
		
		btnBack=(Button) findViewById(R.id.navigationLeftBtn);
		txtHeading=(TextView) findViewById(R.id.txtViewTitle);
		listviewItem=(ListView) findViewById(R.id.listviewItem);
		txtHeading.setVisibility(View.VISIBLE);
		if(requestFor==Constant.FOLLOWER){
			txtHeading.setText("Follower");
		}else if(requestFor==Constant.FOLLOWING){
			txtHeading.setText("Following");
		}
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(this);
		
		new GetFollowerFollowing().execute();
		hashImages = new HashMap<String, Bitmap>();
		arrayJsonList = new ArrayList<JSONObject>();
		
		getItemAdapter = new GetItemAdapter(appContext, requestFor, arrayJsonList,this, "",hashImages);
		listviewItem.setAdapter(getItemAdapter);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.navigationLeftBtn){
			finish();
		}else if(v.getId()==R.id.btnFollowFollowList){
			
			int pos = Integer.parseInt(v.getTag().toString());
			
			JSONObject jsonObject=arrayJsonList.get(pos);
			JSONObject jsonObjectUser = null;
			try {
				jsonObjectUser= jsonObject.getJSONObject("user_inf");
				userId=jsonObjectUser.getString("id");
				
				if(jsonObject.getString("is_follow").equals("false")){
					requestFor=Constant.FOLLOWED_USER;
					new GetFollowerFollowing().execute();
				}else if(jsonObject.getString("is_follow").equals("true")){
					requestFor=Constant.UNFOLLOWED_USER;
					new GetFollowerFollowing().execute();
				} 
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public class GetFollowerFollowing extends AsyncTask<Void, Void, String> {
		ProgressDialog applicationDialog;
		protected void onPreExecute() {
			super.onPreExecute();
			
			applicationDialog = ProgressDialog.show(appContext, "","Please wait...", false, true, new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					GetFollowerFollowing.this.cancel(true);
				}
			});
			
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
				String url=null;
				String result=null;
				if(requestFor==Constant.FOLLOWER){
					url=Constant.serverUrl+"followers?user_id="+userId+"&curUser="+loginUserId;
				}else if(requestFor==Constant.FOLLOWING){
					url=Constant.serverUrl+"following?user_id="+userId+"&curUser="+loginUserId;
				}else if(requestFor==Constant.FOLLOWED_USER){
					url=Constant.serverUrl+"follow_user?user_id="+loginUserId+"&follower_id="+userId;
				}else if(requestFor==Constant.UNFOLLOWED_USER){
					url=Constant.serverUrl+"unfollow_user?user_id="+loginUserId+"&follower_id="+userId;
				}
				result = Utility.findJSONFromUrl(url);
			try {
				 if(requestFor==Constant.FOLLOWER){
					 arrayJsonList.clear();
					JSONArray jsonArray=new JSONArray(result);
					if(jsonArray!=null){
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject=jsonArray.getJSONObject(i);
							arrayJsonList.add(jsonObject);
						}
					}
				}else if(requestFor==Constant.FOLLOWING){
					arrayJsonList.clear();
					JSONArray jsonArray=new JSONArray(result);
					if(jsonArray!=null){
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject=jsonArray.getJSONObject(i);
							arrayJsonList.add(jsonObject);
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}

		protected void onPostExecute(String result) {
			System.out.println("result is " + result);
			if(applicationDialog!=null)
				applicationDialog.dismiss();
			if (result == null) {
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.network_connection_alert);
			}else{
				if(requestFor==Constant.FOLLOWER){
					thread = new Thread(downloadImages);
					thread.start();
					getItemAdapter.notifyDataSetChanged();
				}else if(requestFor==Constant.FOLLOWING){
					thread = new Thread(downloadImages);
					thread.start();
					getItemAdapter.notifyDataSetChanged();
				}else if(requestFor==Constant.FOLLOWED_USER){
					if(result.contains("1")){
						if (txtHeading.getText().toString().equals("Follower")) {
							requestFor=Constant.FOLLOWER;
							new GetFollowerFollowing().execute();
						}else if(txtHeading.getText().toString().equals("Following")){
							requestFor=Constant.FOLLOWING;
							new GetFollowerFollowing().execute();
						}	
					}	
				}	
			}	
		}
	}
	
	
	//stored image in hashmap
			public Runnable runOnMain = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					getItemAdapter.notifyDataSetChanged();
				}
			};

			Runnable downloadImages = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					for (int i = 0; i < arrayJsonList.size(); i++) {

						try {
							JSONObject jsonObject=arrayJsonList.get(i).getJSONObject("user_inf");
							Bitmap bitmapImage = Utility.getBitmap(jsonObject.getString("profile_pic"));
							hashImages.put(jsonObject.getString("id"),bitmapImage);
							runOnUiThread(runOnMain);

						} catch (OutOfMemoryError e) {
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							runOnUiThread(runOnMain);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				}
			};

	
}
