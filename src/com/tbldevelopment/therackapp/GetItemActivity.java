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
import android.content.Intent;
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

import com.facebook.android.Util;
import com.tbldevelopment.rackapp.adapter.GetItemAdapter;
import com.tbldevelopment.rackapp.adapter.ItemAdapter;
import com.tbldevelopment.rackapp.adapter.ItemCommentAdapter;
import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;
import com.tbldevelopment.therackapp.GetItemDetails.GetComment;

public class GetItemActivity extends Activity implements OnClickListener{
	private Context appContext;
	private int requestFor;
	private ArrayList<JSONObject> arrayJsonList;
	private GetItemAdapter getItemAdapter;
	private ListView listviewItem;
	private Button btnBack;
	private String categoryId;
	private String loginUserId;
	public HashMap<String, Bitmap> hashImages=null;
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
		listviewItem=(ListView) findViewById(R.id.listviewItem);
		btnBack=(Button) findViewById(R.id.navigationLeftBtn);
		btnBack.setVisibility(View.VISIBLE);
		
		loginUserId=Utility.getSharedPreferences(appContext, "loginUserId");
		Utility.setSharedPreferenceBoolean(appContext, "refreshItemList", false);
		
		TextView txtHeading=(TextView) findViewById(R.id.txtViewTitle);
		txtHeading.setVisibility(View.VISIBLE);
		
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			requestFor=bundle.getInt("requestFor");
			if(requestFor==Constant.GET_ITEM){
				categoryId=bundle.getString("categoryId");
			}else if(requestFor==Constant.MY_LIKES){
				loginUserId=bundle.getString("userID");
			}
			
		}
		if(requestFor==Constant.GET_ITEM){
			if(categoryId.equals("3")){
				txtHeading.setText("Men");
			}else if(categoryId.equals("4")){
				txtHeading.setText("Women");
			}else if(categoryId.equals("5")){
				txtHeading.setText("Kids");
			}
		}else if(requestFor==Constant.MY_LIKES){
			txtHeading.setText("My Likes");
		}
		hashImages = new HashMap<String, Bitmap>();
		arrayJsonList = new ArrayList<JSONObject>();
		new GetItems().execute();
		
		getItemAdapter = new GetItemAdapter(appContext, requestFor,arrayJsonList,clickListener,"",hashImages);
		listviewItem.setAdapter(getItemAdapter);
		btnBack.setOnClickListener(this);
	}
	OnClickListener clickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			int pos = Integer.parseInt(v.getTag().toString());
			JSONObject jsonObject=arrayJsonList.get(pos);
			if(v.getId()==R.id.linearLayoutUserInfo){
			String userId=null;
			JSONObject jsonObjectUser = null;
			try {
				jsonObjectUser= jsonObject.getJSONObject("user");
				userId=jsonObjectUser.getString("id");
				System.out.println("user Id is  "+userId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(v.getId()==R.id.linearLayoutUserInfo){
				Intent intentUser=new Intent(appContext,GetUserInfo.class);
				intentUser.putExtra("userInfoData", jsonObjectUser.toString());
				intentUser.putExtra("userID", userId);
				intentUser.putExtra("requestFor",Constant.USER_INFO);
				startActivity(intentUser);
			}
			}else if(v.getId()==R.id.linearLayoutItemInfo){
				Intent intentItem=new Intent(appContext,GetItemDetails.class);
				intentItem.putExtra("requestFor", Constant.GET_COMMENTS);
				intentItem.putExtra("itemInfoData", jsonObject.toString());
				startActivity(intentItem);
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.navigationLeftBtn){
			finish();
		}
	}
	
	public class GetItems extends AsyncTask<Void, Void, String> {
		ProgressDialog applicationDialog;
		protected void onPreExecute() {
			super.onPreExecute();
			
			applicationDialog = ProgressDialog.show(appContext, "","Please wait...", false, true, new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					GetItems.this.cancel(true);
				}
			});
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String result=null;String url=null;
			
			if(requestFor==Constant.GET_ITEM){
				url=Constant.serverUrl+"items?category_id="+categoryId+"&user_id="+loginUserId;
			}else if(requestFor==Constant.MY_LIKES){
				url=Constant.serverUrl+"my_likes?user_id="+loginUserId;
			}
			
			result = Utility.findJSONFromUrl(url);
			
			if (result != null) {
				if (requestFor == Constant.GET_ITEM) {
					try {
						arrayJsonList.clear();
						JSONObject jsonObject = new JSONObject(result);
						JSONArray jsonArrayItem = jsonObject
								.getJSONArray("items");
						if (jsonArrayItem != null) {
							for (int i = 0; i < jsonArrayItem.length(); i++) {
								JSONObject jsonObjectItem = jsonArrayItem.getJSONObject(i);
								arrayJsonList.add(jsonObjectItem);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if(requestFor==Constant.MY_LIKES){
					arrayJsonList.clear();
					try {
						JSONArray jsonArray=new JSONArray(result);
						if(jsonArray.length()>0){
							for (int j = 0; j < jsonArray.length(); j++) {
								JSONObject jsonObject=jsonArray.getJSONObject(j);
								arrayJsonList.add(jsonObject);
							}
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}

			return result;
		}

		protected void onPostExecute(String result) {
			System.out.println("result is " + result);
			if(applicationDialog!=null)
				applicationDialog.dismiss();
			
			if (result == null) {
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.network_connection_alert);
			}else if (arrayJsonList.size() == 0) {
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.data_available_alert);
			}else{
				thread = new Thread(downloadImages);
				thread.start();
				getItemAdapter.notifyDataSetChanged();
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
					JSONObject jsonUserData=arrayJsonList.get(i).getJSONObject("user");
					Bitmap bitmapImage = Utility.getBitmap(jsonUserData.getString("profile_pic"));
					hashImages.put(jsonUserData.getString("id"),bitmapImage);
					bitmapImage=null;
					JSONArray jsonArrayItemImage=arrayJsonList.get(i).getJSONArray("images");
					if(jsonArrayItemImage!=null){
						if(jsonArrayItemImage.length()>0){
							JSONObject jsonObjectItemImage=jsonArrayItemImage.getJSONObject(0);
							bitmapImage = Utility.getBitmap(jsonObjectItemImage.getString("url"));
							hashImages.put(arrayJsonList.get(i).getString("id"),bitmapImage);
						}
					}
					bitmapImage = null;
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
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (Utility.getSharedPreferencesBoolean(appContext, "refreshItemList")) {
			Utility.setSharedPreferenceBoolean(appContext, "refreshItemList", false);
			requestFor=Constant.GET_ITEM;
			new GetItems().execute();
			getItemAdapter = new GetItemAdapter(appContext, requestFor,arrayJsonList,clickListener,"",hashImages);
			listviewItem.setAdapter(getItemAdapter);
		}
	}
}
