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
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import com.tbldevelopment.rackapp.adapter.GetItemAdapter;
import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;
import com.tbldevelopment.therackapp.GetItemActivity.GetItems;

public class GetUserInfo extends Activity implements OnClickListener {
	private Context appContext;
	private Button btnBack;
	private String userId;

	private String userInfoData;
	private String userName, location;
	private String userFollower, userFollowing, userListing;
	TextView txtFollower, txtFollowing, txtListing;
	private String loginUserId;
	private LinearLayout followerlayout, followinglayout;
	private int requestFor;
	private Button btnUserFollow;

	private ArrayList<JSONObject> arrayJsonList;
	private GetItemAdapter getItemAdapter;
	private GridView gridviewItem;
	private String isFollowed;
	public HashMap<String, Bitmap> hashImages;
	private Thread thread;
	private int countItem;
	private TextView txtCount;
	private String strProfileImage;
	private Bitmap bitmap=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_getuserinfo);

		appContext = this;
		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);

		final ImageView imageProfile=(ImageView) findViewById(R.id.imageviewProfileUserInfoList);
		btnUserFollow = (Button) findViewById(R.id.btnUserFollow);
		btnBack = (Button) findViewById(R.id.navigationLeftBtn);
		TextView txtHeading = (TextView) findViewById(R.id.txtViewTitle);
		TextView txtUserName = (TextView) findViewById(R.id.txtUserNameUserInfoList);
		TextView txtLocation = (TextView) findViewById(R.id.txtLocationUserInfoList);
		txtFollower = (TextView) findViewById(R.id.txtFollowersUserInfoList);
		txtFollowing = (TextView) findViewById(R.id.txtFollowingUserInfoList);
		txtListing = (TextView) findViewById(R.id.txtListingUserInfoList);
		followerlayout = (LinearLayout) findViewById(R.id.linearLayoutFollower);
		followinglayout = (LinearLayout) findViewById(R.id.linearLayoutFollowing);
		gridviewItem = (GridView) findViewById(R.id.gridView);
		
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			userId = bundle.getString("userID");
			userInfoData = bundle.getString("userInfoData");
			requestFor = bundle.getInt("requestFor");
		}
		LinearLayout listingLayout=(LinearLayout) findViewById(R.id.listingLayout);
		View view=findViewById(R.id.viewLayout);
		

		try {
			JSONObject jsonObj = new JSONObject(userInfoData);
			userName = jsonObj.getString("name");
			location = jsonObj.getString("city");
			strProfileImage=jsonObj.getString("profile_pic");
			Runnable runnImage = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					if (strProfileImage==null)
					{
					}else{
						bitmap = Utility.DownloadImageDirect(strProfileImage);
					}
					
					if(bitmap!=null)
					{
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								imageProfile.setImageBitmap(bitmap);
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
		txtHeading.setVisibility(View.VISIBLE);
		loginUserId = Utility.getSharedPreferences(appContext, "loginUserId");
		txtCount=(TextView) findViewById(R.id.txtPurchaseCount);
		if(requestFor==Constant.USER_INFO){
			listingLayout.setVisibility(View.VISIBLE);
			view.setVisibility(View.GONE);
			txtCount.setVisibility(View.GONE);
			txtHeading.setText(userName);
		}else if(requestFor==Constant.MY_SALE || requestFor==Constant.MY_PURCHASE){
			view.setVisibility(View.VISIBLE);
			txtCount.setVisibility(View.VISIBLE);
			listingLayout.setVisibility(View.GONE);
			if(requestFor==Constant.MY_SALE){
				txtHeading.setText(userName+" Sale");
			}else if(requestFor==Constant.MY_PURCHASE){
				txtHeading.setText(userName+" Purchase");
			}
			
		}
		if (location.equals("null")) {
			txtLocation.setText("");
		} else {
			txtLocation.setText(location);
		}
		txtUserName.setText(userName + "'s Wardrobe");

		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(this);
		followerlayout.setOnClickListener(this);
		followinglayout.setOnClickListener(this);
		btnUserFollow.setOnClickListener(this);

		hashImages = new HashMap<String, Bitmap>();
		arrayJsonList = new ArrayList<JSONObject>();
		new GetUSerInfo().execute();

		getItemAdapter = new GetItemAdapter(appContext, requestFor, arrayJsonList,this, userName,hashImages);
		gridviewItem.setAdapter(getItemAdapter);

		gridviewItem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				JSONObject jsonObjectItem = arrayJsonList.get(position);
				System.out.println("Object Item is  " + jsonObjectItem);

				Intent intent = new Intent(appContext, GetItemDetails.class);
				intent.putExtra("itemInfoData", jsonObjectItem.toString());
				intent.putExtra("requestFor", Constant.ITEM_DETAIL);
				intent.putExtra("userId", userId);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == R.id.navigationLeftBtn) {
			finish();
		} else if (v.getId() == R.id.linearLayoutFollower) {
			requestFor = Constant.FOLLOWER;
			Intent i = new Intent(appContext, FollowFollowingActivity.class);
			i.putExtra("userId", userId);
			i.putExtra("requestFor", Constant.FOLLOWER);
			startActivity(i);
		} else if (v.getId() == R.id.linearLayoutFollowing) {
			Intent i = new Intent(appContext, FollowFollowingActivity.class);
			i.putExtra("userId", userId);
			i.putExtra("requestFor", Constant.FOLLOWING);
			startActivity(i);
		} else if (v.getId() == R.id.btnUserFollow) {

			if (btnUserFollow.getText().toString().equals("Follow")) {
				requestFor = Constant.FOLLOWED_USER;
				new GetUSerInfo().execute();
			} else if (btnUserFollow.getText().toString().equals("Unfollow")) {
				requestFor = Constant.UNFOLLOWED_USER;
				new GetUSerInfo().execute();
			}

		}
	}

	public class GetUSerInfo extends AsyncTask<Void, Void, String> {
		ProgressDialog applicationDialog;
		protected void onPreExecute() {
			super.onPreExecute();

			applicationDialog = ProgressDialog.show(appContext, "","Please wait...", false, true, new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					GetUSerInfo.this.cancel(true);
				}
			});

		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String url=null;
			String result=null;
			if (requestFor == Constant.USER_INFO) {
				url = Constant.serverUrl + "user_items?user_id=" + userId
						+ "&curUser="+loginUserId;
			}else if(requestFor==Constant.MY_SALE){
				url = Constant.serverUrl+"my_sales?user_id="+loginUserId;
			}else if(requestFor==Constant.MY_PURCHASE){
				url = Constant.serverUrl+"my_purchase?user_id="+loginUserId;
			}else if (requestFor == Constant.FOLLOWED_USER) {
				url = Constant.serverUrl + "follow_user?&user_id="+ loginUserId + "&follower_id=" + userId;
			} else if (requestFor == Constant.UNFOLLOWED_USER) {
				url = Constant.serverUrl + "unfollow_user?&user_id="+ loginUserId + "&follower_id=" + userId;
			}
			result = Utility.findJSONFromUrl(url);
			try {
				
				if (requestFor == Constant.USER_INFO) {
					arrayJsonList.clear();
					if (result != null)
					{
						JSONArray jsonArray = new JSONArray(result);
						if(jsonArray!=null)
						{
							if(jsonArray.length()>0)
							{
								JSONObject jsonObject = jsonArray.getJSONObject(0);
								JSONArray jsonUserArray = jsonObject.getJSONArray("user");
								JSONObject jsonObjectUser = jsonUserArray.getJSONObject(0);
								JSONArray jsonItemArray = jsonObject.getJSONArray("items");
							if (jsonItemArray != null) 
							{
								for (int i = 0; i < jsonItemArray.length(); i++)
								{
									JSONObject jsonObjectItem = jsonItemArray.getJSONObject(i);
									arrayJsonList.add(jsonObjectItem);
								}
							}
							isFollowed = jsonObjectUser.getString("is_follow");
							userListing = jsonObject.getString("count");
							userFollower = jsonObjectUser.getString("followers");
							userFollowing = jsonObjectUser.getString("following");
						  }
					 }
				  }
				}
				if (requestFor == Constant.MY_SALE || requestFor==Constant.MY_PURCHASE) {
					arrayJsonList.clear();
					if (result != null) {
						countItem=0;
						JSONArray jsonArray = new JSONArray(result);
						if(jsonArray!=null){
							if (jsonArray .length()>0) {
								for (int i = 0; i < jsonArray.length(); i++) {
									countItem=countItem+1;
									JSONObject jsonObjectItem = jsonArray.getJSONObject(i);
									arrayJsonList.add(jsonObjectItem);
								}
							}
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
			applicationDialog.dismiss();
			if (result == null) {
				Utility.ShowAlertWithMessage(appContext, R.string.alert,
						R.string.network_connection_alert);
			} else {
				if (requestFor == Constant.USER_INFO) {
					if(userFollower==null){
						txtFollower.setText("0");
					}else{
						txtFollower.setText(userFollower);
					}
					if(userFollowing==null){
						txtFollowing.setText("0");
					}else{
						txtFollowing.setText(userFollowing);
					}
					if(userListing==null){
						txtListing.setText("0");
					}else{
						txtListing.setText(userListing);
					}
					if(!userId.equals(loginUserId)){
						if (isFollowed.equals("false")) {
							btnUserFollow.setVisibility(View.VISIBLE);
							btnUserFollow.setText("Follow"); 
						}else if(isFollowed.equals("true")){
							btnUserFollow.setVisibility(View.VISIBLE);
							btnUserFollow.setText("Unfollow");
						}
					}
					thread = new Thread(downloadImages);
					thread.start();
					getItemAdapter.notifyDataSetChanged();
				} else if(requestFor==Constant.MY_SALE || requestFor==Constant.MY_PURCHASE){
					if(requestFor==Constant.MY_SALE){
						txtCount.setText("Total Sale : "+countItem);
					}else if(requestFor==Constant.MY_PURCHASE){
						txtCount.setText("Total Purchase : "+countItem);
					}
					thread = new Thread(downloadImages);
					thread.start();
					getItemAdapter.notifyDataSetChanged();
				}else if (requestFor == Constant.FOLLOWED_USER) {
					if(result.contains("User Not Found")){
						Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.failed_alert);
					}else if(result.contains("1")){
						btnUserFollow.setText("Unfollow");
						int countFollowUser=Integer.parseInt(txtFollower.getText().toString());
						countFollowUser=countFollowUser+1;
						txtFollower.setText(String.valueOf(countFollowUser));	
					}
				} else if (requestFor == Constant.UNFOLLOWED_USER) {
					if (result.contains("User Not Found")) {
						Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.failed_alert);
					} else if(result.contains("1")) {
						btnUserFollow.setText("Follow");
						int countFollowUser=Integer.parseInt(txtFollower.getText().toString());
						countFollowUser=countFollowUser-1;
						txtFollower.setText(String.valueOf(countFollowUser));
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
						JSONArray jsonArrayItemImage=arrayJsonList.get(i).getJSONArray("images");
						if(jsonArrayItemImage!=null){
							if(jsonArrayItemImage.length()>0){
								JSONObject jsonObjectItemImage=jsonArrayItemImage.getJSONObject(0);
								Bitmap bitmapImage = Utility.getBitmap(jsonObjectItemImage.getString("url"));
								hashImages.put(arrayJsonList.get(i).getString("id"),bitmapImage);
							}
						}
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
