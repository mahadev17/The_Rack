package com.tbldevelopment.therackapp;

import java.math.BigDecimal;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.tbldevelopment.rackapp.adapter.ItemCommentAdapter;
import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;
import com.tbldevelopment.therackapp.GetItemActivity.GetItems;

public class GetItemDetails extends Activity implements OnClickListener{
	private Context appContext;
	private Button btnBack;
	private Button btnComment,btnLikeCount;
	private Button btnShare;
	private Button btnBuyItem;
	String itemInfoData;
	JSONObject jsonObject;
	private String itemId;
	
	private int requestFor;
	private ArrayList<JSONObject> arrayJsonList;
	private ItemCommentAdapter itemCommentAdapter;
	private ListView listviewItemComment;
	private int commentCount;
	private TextView txtCommentCount;
	private String loginUserId;
	private String likeStatus;
	private int likeCount;
	public HashMap<String, Bitmap> hashImages;
	private Thread thread;
	private String itemTitle;
	
	//paypal variable
	 private static final String CONFIG_ENVIRONMENT = PaymentActivity.ENVIRONMENT_NO_NETWORK;
	    // note that these credentials will differ between live & sandbox environments.
	    private static final String CONFIG_CLIENT_ID = "credential from developer.paypal.com";
	    // when testing in sandbox, this is likely the -facilitator email address. 
	    private static final String CONFIG_RECEIVER_EMAIL = "matching paypal email address"; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_item_details);
		
		appContext=this;
		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
	
		btnBuyItem=(Button) findViewById(R.id.btnBuyNowItemDetails);
		Button btnEdit=(Button) findViewById(R.id.navigationRightBtn);
		loginUserId=Utility.getSharedPreferences(appContext, "loginUserId");
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			requestFor=bundle.getInt("requestFor");
			
			if(requestFor==Constant.GET_COMMENTS){
				itemInfoData=bundle.getString("itemInfoData");
			}else if(requestFor==Constant.ITEM_DETAIL){
				itemInfoData=bundle.getString("itemInfoData");
				String userId=bundle.getString("userId");
				System.out.println("User Id is   "+userId);
				if(userId.equals(loginUserId)){
					btnEdit.setVisibility(View.VISIBLE);
					btnBuyItem.setVisibility(View.GONE);
				}else if(!userId.equals(loginUserId)){
					btnEdit.setVisibility(View.GONE);
					btnBuyItem.setVisibility(View.VISIBLE);
				}
				btnEdit.setBackgroundResource(R.drawable.edit_btn);
				requestFor=Constant.GET_COMMENTS;
			}
				
		}
		
		Utility.setSharedPreferenceBoolean(appContext, "refreshComment", false);
		TextView txtHeading=(TextView) findViewById(R.id.txtViewTitle);
		TextView txtPrice=(TextView) findViewById(R.id.txtItemPriceItemDetails);
		TextView txtItemSize=(TextView) findViewById(R.id.txtItemSizeItemDetails);
		TextView txtItemTitle=(TextView) findViewById(R.id.txtItemTitleItemDetails);
		TextView txtFooterPrice=(TextView) findViewById(R.id.txtFooterItemPriceItemDetails);
		txtCommentCount=(TextView) findViewById(R.id.txtCommentCountItemDetails);
		TextView txtItemDetails=(TextView) findViewById(R.id.txtItemDetailsItemDetails);
		TextView txtItemTypeDetail=(TextView) findViewById(R.id.txtItemTypeItemDetails);
		listviewItemComment=(ListView) findViewById(R.id.listviewCommentsItemDetails);
		btnBack=(Button) findViewById(R.id.navigationLeftBtn);
		btnLikeCount=(Button) findViewById(R.id.btnPriceItemDetails);
		btnComment=(Button) findViewById(R.id.btnCommentItemDetails);
		btnShare=(Button) findViewById(R.id.btnShareItemDetails);
		
		
		
		btnBack.setVisibility(View.VISIBLE);
		
		txtHeading.setVisibility(View.VISIBLE);
		txtHeading.setText("Item Details");
		
		
		btnBack.setOnClickListener(this);
		btnEdit.setOnClickListener(this);
		btnComment.setOnClickListener(this);
		btnBuyItem.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		btnLikeCount.setOnClickListener(this);
		
		try {
			  
			jsonObject = new JSONObject(itemInfoData);
			itemId = jsonObject.getString("id");
			
			itemTitle=jsonObject.getString("title");
			txtItemTitle.setText(itemTitle);
			txtItemTypeDetail.setText(jsonObject.getString("title"));
			if (jsonObject.getString("content").equals("null")) {
				txtItemDetails.setText("");
			} else {
				txtItemDetails.setText(jsonObject.getString("content"));
			}
			txtPrice.setText("$" + jsonObject.getString("listing_price"));
			txtFooterPrice.setText("$" + jsonObject.getString("listing_price"));
			if (jsonObject.getString("size").equals("null")) {
				txtItemSize.setText("Size: 0");
			} else {
				txtItemSize.setText("Size: " + jsonObject.getString("size"));
			}
			
			likeStatus = jsonObject.getString("is_like");
			if(likeStatus.equals("null")){
				btnLikeCount.setText("Like: 0");
			}else{
				likeCount = Integer.parseInt(jsonObject.getString("like_count"));
				if (likeStatus.equals("0")) {
					btnLikeCount.setText("Like: " + likeCount);
				} else if (likeStatus.equals("1")) {
					btnLikeCount.setText("Unlike: " + likeCount);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		arrayJsonList = new ArrayList<JSONObject>();
		hashImages = new HashMap<String, Bitmap>();
		new GetComment().execute();
		
		itemCommentAdapter = new ItemCommentAdapter(appContext, requestFor,arrayJsonList,hashImages);
		listviewItemComment.setAdapter(itemCommentAdapter);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	
		if(v.getId()==R.id.navigationLeftBtn){
			Utility.setSharedPreferenceBoolean(appContext, "refreshItemList", true);
			finish();
		}else if(v.getId()==R.id.navigationRightBtn){
			Intent intent=new Intent(appContext,AddItemActivity.class);
			intent.putExtra("itemInfoData",itemInfoData);
			intent.putExtra("requestFor", Constant.EDIT_ITEM);
			startActivity(intent);
		}else if(v.getId()==R.id.btnCommentItemDetails){
			Intent intent =new Intent(appContext,AddCommentActivity.class);
			intent.putExtra("itemId", itemId);
			startActivity(intent);
		}else if(v.getId()==R.id.btnBuyNowItemDetails){
			
/*			PayPalPayment thingToBuy = new PayPalPayment(
					new BigDecimal("25"), "$",itemTitle);*/
			
				String amount="20";
			 PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(amount), "USD", itemTitle);

			Intent intent = new Intent(this, PaymentActivity.class);

			intent.putExtra(PaymentActivity.EXTRA_PAYPAL_ENVIRONMENT,
					CONFIG_ENVIRONMENT);
			intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID, CONFIG_CLIENT_ID);
			intent.putExtra(PaymentActivity.EXTRA_RECEIVER_EMAIL,
					CONFIG_RECEIVER_EMAIL);
			
			intent.putExtra(PaymentActivity.EXTRA_CLIENT_ID,
					"credential-from-developer.paypal.com");
			intent.putExtra(PaymentActivity.EXTRA_PAYER_ID,
					"your-customer-id-in-your-system");
			intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

			startActivityForResult(intent, 0);
			
		}else if(v.getId()==R.id.btnShareItemDetails){
			Toast.makeText(appContext, "Sharing...", Toast.LENGTH_SHORT).show();
		}else if(v.getId()==R.id.btnPriceItemDetails){
			if(likeStatus.equals("0")){
				likeStatus="1";
				requestFor=Constant.LIKE;
				new GetComment().execute();
			}else if(likeStatus.equals("1")){
				likeStatus="0";
				requestFor=Constant.UNLIKE;
				new GetComment().execute();
			}
			
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
		if (Utility.getSharedPreferencesBoolean(appContext, "refreshComment")) {
			requestFor=Constant.GET_COMMENTS;
			new GetComment().execute();
			
			itemCommentAdapter = new ItemCommentAdapter(appContext, requestFor,arrayJsonList,hashImages);
			listviewItemComment.setAdapter(itemCommentAdapter);
		}
		
	}
	
	public class GetComment extends AsyncTask<Void, Void, String> {

		ProgressDialog applicationDialog;
		protected void onPreExecute() {
			super.onPreExecute();
			applicationDialog = ProgressDialog.show(appContext, "","Please wait...", false, true, new OnCancelListener() {		
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					GetComment.this.cancel(true);
				}
			});
		}
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String result;
			String url = null;
			if(requestFor==Constant.GET_COMMENTS){
				url = Constant.serverUrl+"get_comment?item_id="+itemId;
			}else if(requestFor==Constant.LIKE){
				url = Constant.serverUrl+"add_like?item_id="+itemId+"&user_id="+loginUserId;
			}else if(requestFor==Constant.UNLIKE){
				url = Constant.serverUrl+"unlike?item_id="+itemId+"&user_id="+loginUserId;
			}
			
			result = Utility.findJSONFromUrl(url);
			if(result!=null){
				try {
					
					if(requestFor==Constant.GET_COMMENTS){
						JSONArray jsonArray=new JSONArray(result);
						if (jsonArray!= null) {
							arrayJsonList.clear();
							commentCount=0;
							for (int i = 0; i < jsonArray.length(); i++) {
								commentCount=commentCount+1;
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								arrayJsonList.add(jsonObject);
							}
						}
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
				if(requestFor==Constant.GET_COMMENTS){
					if(result.equals("0")){
						Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.failed_alert);
					}else{
						txtCommentCount.setText("Comments: "+commentCount);
						thread = new Thread(downloadImages);
						thread.start();
						itemCommentAdapter.notifyDataSetChanged();
					}
					
				}else if(requestFor==Constant.LIKE){
					if(result.equals("0")){
						Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.failed_alert);
					}else{
						likeCount=likeCount+1;
						btnLikeCount.setText("Unlike: "+likeCount);
					}
				}else if(requestFor==Constant.UNLIKE){
					
					if(result.equals("0")){
						Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.failed_alert);
					}else{
						likeCount=likeCount-1;
						btnLikeCount.setText("Like: "+likeCount);
					}
				}
			}
		}
	}
	
	public Runnable runOnMain = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			itemCommentAdapter.notifyDataSetChanged();
		}
	};
	
	Runnable downloadImages = new Runnable() {
		Bitmap bitmapImage;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			for (int i = 0; i < arrayJsonList.size(); i++) {
				
				try {
					bitmapImage = Utility.getBitmap(arrayJsonList.get(i).getString("profile_pic"));
					hashImages.put(arrayJsonList.get(i).getString("comment_id"), bitmapImage);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			PaymentConfirmation confirm = data
					.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
			if (confirm != null) {
				try {
					Log.i("paymentExample", confirm.toJSONObject().toString(4));

					// TODO: send 'confirm' to your server for verification.
					// see
					// https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
					// for more details.

				} catch (JSONException e) {
					Log.e("paymentExample",
							"an extremely unlikely failure occurred: ", e);
				}
			}
		} else if (resultCode == Activity.RESULT_CANCELED) {
			Log.i("paymentExample", "The user canceled.");
		} else if (resultCode == PaymentActivity.RESULT_PAYMENT_INVALID) {
			Log.i("paymentExample",
					"An invalid payment was submitted. Please see the docs.");
		}
	}

	@Override
	public void onDestroy() {
		stopService(new Intent(this, PayPalService.class));
		super.onDestroy();
	}

}
