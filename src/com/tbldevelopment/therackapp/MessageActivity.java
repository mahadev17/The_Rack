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

import com.tbldevelopment.rackapp.adapter.ItemCommentAdapter;
import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;
import com.tbldevelopment.therackapp.GetItemActivity.GetItems;

public class MessageActivity extends Activity implements OnClickListener{
	
	private Context appContext;
	private Button btnBack;
	private Button btnDone;
	private ArrayList<JSONObject> arrayList;
	public HashMap<String, Bitmap> hashImages;
	private Thread thread;
	private ItemCommentAdapter commentItemAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_notification);
		
		appContext=this;
		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
		
		TextView txtTitle=(TextView) findViewById(R.id.txtViewTitle);
		btnBack=(Button) findViewById(R.id.navigationLeftBtn);
		btnDone=(Button) findViewById(R.id.navigationRightBtn);
		
		btnBack.setVisibility(View.VISIBLE);
		ListView listview=(ListView) findViewById(R.id.listviewMsg);
		txtTitle.setVisibility(View.VISIBLE);
		txtTitle.setText("Messages");
		
		btnDone.setBackgroundResource(R.drawable.refresh_icon);
		
		btnBack.setOnClickListener(this);
		btnDone.setOnClickListener(this);
		
		hashImages = new HashMap<String, Bitmap>();
		arrayList=new ArrayList<JSONObject>();
		int requestFor=Constant.GET_MESSAGE_NOTIFICATION;
		commentItemAdapter = new ItemCommentAdapter(appContext, requestFor,arrayList,hashImages);
		listview.setAdapter(commentItemAdapter);
		
		new GetNotification().execute();
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.navigationLeftBtn){
			finish();
		}
	}

	
	public class GetNotification extends AsyncTask<Void, Void, String>{
		ProgressDialog applicationDialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			applicationDialog = ProgressDialog.show(appContext, "",
					"Please wait...", false, true, new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub
							GetNotification.this.cancel(true);
						}
			});
		}
		
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String result;
			String userId=Utility.getSharedPreferences(appContext, "loginUserId");
			String url = Constant.serverUrl+"show_notifications?user_id=3";//+userId;
			result = Utility.findJSONFromUrl(url);
			
			if(result!=null){
				try {
					JSONArray jsonArray=new JSONArray(result);
					if(jsonArray!=null){
						arrayList.clear();
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject=jsonArray.getJSONObject(i);
							arrayList.add(jsonObject);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			applicationDialog.dismiss();
			if(result==null){
				Utility.ShowAlertWithMessage(appContext, R.string.alert,R.string.network_connection_alert);
			}else if(result.equals("[]\n")){
				Utility.ShowAlertWithMessage(appContext, R.string.alert,R.string.data_available_alert);
			}else{
				thread = new Thread(downloadImages);
				thread.start();
				commentItemAdapter.notifyDataSetChanged();
			}
			
		}
	}
	
	//stored image in hashmap
		public Runnable runOnMain = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				commentItemAdapter.notifyDataSetChanged();
			}
		};

		Runnable downloadImages = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < arrayList.size(); i++) {

					try {			
						Bitmap bitmapImage = Utility.getBitmap(arrayList.get(i).getString("sender_pic"));
						JSONObject jsonObjectNotification=arrayList.get(i).getJSONObject("notifications");
						hashImages.put(jsonObjectNotification.getString("sender_id"),bitmapImage);
						bitmapImage=null;
						bitmapImage = Utility.getBitmap(arrayList.get(i).getString("item_pic"));
						hashImages.put(jsonObjectNotification.getString("id"),bitmapImage);
						bitmapImage=null;
						runOnUiThread(runOnMain);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

}
