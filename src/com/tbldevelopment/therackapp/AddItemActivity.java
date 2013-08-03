package com.tbldevelopment.therackapp;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tbldevelopment.rackapp.adapter.ItemAdapter;
import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;

public class AddItemActivity extends Activity implements OnClickListener,TextWatcher{
	private Context appContext;
	private Button btnBack;
	private TextView txtHeading;
	private ImageView imageItem6,imageItem1,imageItem2,imageItem3,imageItem4,imageItem5;
	private static final int CAMERA = 1;
	
	private TextView txtCategory,txtSize,txtSubCategory;
	private EditText edtOtherSize;
	private Button btnDone;
	private String requestSize;
	private int requestFor; 
	private ArrayList<Bitmap> listBitmap;
	private ArrayAdapter<String> categoryAdaptor;
	private Dialog dialogCategoryList;
	
	private Dialog dialogSizeList;
	private ArrayAdapter<String> sizeAdaptor;
	private Dialog dialogSubCategoryList;
	private String userId;
	private String categoryId;
	
	private ItemAdapter itemAdapter;
	private String[] categotyArrayItem,dressSizeList,shoeSizeList;
	private ArrayList<JSONObject> arrayJsonList;
	private ListView listSize;
	private EditText edtListingPrice,edtEarning,edtTitle,edtOriginalPrice,edtDescription;
	private int requestImage;
	private String itemId;
	private int imageCount=0;
	ArrayList<NameValuePair> listNameValuePairs = new ArrayList<NameValuePair>();
	private ImageView imageCross1,imageCross2,imageCross3,imageCross4,imageCross5;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addrack);
		
		appContext=this;
		listBitmap = new ArrayList<Bitmap>();
		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
		
		userId=Utility.getSharedPreferences(appContext, "loginUserId");
		Bundle requestBundle=getIntent().getExtras();
		if(requestBundle!=null){
			requestFor=requestBundle.getInt("requestFor");
		}
		
		arrayJsonList = new ArrayList<JSONObject>();
		categotyArrayItem = getResources().getStringArray(R.array.CategoryArray);
		dressSizeList= getResources().getStringArray(R.array.DressSizeArray);
		shoeSizeList= getResources().getStringArray(R.array.ShoeSizeArray);
		
		btnBack= (Button)findViewById(R.id.navigationLeftBtn);
		txtHeading= (TextView)findViewById(R.id.txtViewTitle);
		imageItem1=(ImageView)this.findViewById(R.id.image1);
		imageItem2=(ImageView)this.findViewById(R.id.image2);
		imageItem3=(ImageView)this.findViewById(R.id.image3);
		imageItem4=(ImageView)this.findViewById(R.id.image4);
		imageItem5=(ImageView)this.findViewById(R.id.image5);
		imageItem6=(ImageView)this.findViewById(R.id.image6);
		imageCross1=(ImageView) findViewById(R.id.imageviewCross1);
		imageCross2=(ImageView) findViewById(R.id.imageviewCross2);
		imageCross3=(ImageView) findViewById(R.id.imageviewCross3);
		imageCross4=(ImageView) findViewById(R.id.imageviewCross4);
		imageCross5=(ImageView) findViewById(R.id.imageviewCross5);
		
		imageItem1.setTag("-1");
		imageItem2.setTag("-1");
		imageItem3.setTag("-1");
		imageItem4.setTag("-1");
		imageItem5.setTag("-1");
		imageItem6.setTag("-1");
		
		txtCategory=(TextView) findViewById(R.id.txtItemCategory);
		txtSubCategory=(TextView) findViewById(R.id.txtItemSubCategory);
		txtSize=(TextView) findViewById(R.id.txtItemSize);
		edtOtherSize=(EditText) findViewById(R.id.edtOtherSize);
		btnDone=(Button) findViewById(R.id.navigationRightBtn);
		btnDone.setBackgroundResource(R.drawable.done_btn);
		edtListingPrice=(EditText)findViewById(R.id.edtListingPrice);
		edtEarning=(EditText)findViewById(R.id.edtEarning);
		edtTitle=(EditText)findViewById(R.id.edtItemTitle);
		edtOriginalPrice=(EditText)findViewById(R.id.edtOriginalPrice);
		edtDescription=(EditText)findViewById(R.id.edtItemDescription);
		
		
		edtListingPrice.setOnClickListener(this);
		
		btnDone.setVisibility(View.VISIBLE);
		txtHeading.setVisibility(View.VISIBLE);
		
		btnBack.setVisibility(View.VISIBLE);
		
		
		btnBack.setOnClickListener(this);
		imageItem1.setOnClickListener(this);
		imageItem2.setOnClickListener(this);
		imageItem3.setOnClickListener(this);
		imageItem4.setOnClickListener(this);
		imageItem5.setOnClickListener(this);
		imageItem6.setOnClickListener(this);
		
		imageCross1.setOnClickListener(this);
		imageCross2.setOnClickListener(this);
		imageCross3.setOnClickListener(this);
		imageCross4.setOnClickListener(this);
		imageCross5.setOnClickListener(this);
		
		
		txtCategory.setOnClickListener(this);
		txtSubCategory.setOnClickListener(this);
		txtSize.setOnClickListener(this);
		btnDone.setOnClickListener(this);
		
		//set item's info for edit
		if(requestFor==Constant.EDIT_ITEM){
			txtHeading.setText("Edit Item");
			String itemInfo=requestBundle.getString("itemInfoData");
			try {
				//edit_items?item_id=
				final JSONObject jsonObject=new JSONObject(itemInfo);
				itemId = jsonObject.getString("id");
				edtTitle.setText(jsonObject.getString("title"));
				edtDescription.setText(jsonObject.getString("content"));
				if(jsonObject.getString("category_id").equals("3")){
					txtCategory.setText("Men");
				}else if(jsonObject.getString("category_id").equals("4")){
					txtCategory.setText("Women");
				}else if(jsonObject.getString("category_id").equals("5")){
					txtCategory.setText("Kids");
				}
				
				txtSubCategory.setText(jsonObject.getString("sub_category_name"));
				txtSize.setText(jsonObject.getString("size"));
				edtOriginalPrice.setText(jsonObject.getString("price"));
				edtListingPrice.setText(jsonObject.getString("listing_price"));
				float expectedPrice=Float.parseFloat(edtListingPrice.getText().toString());
				expectedPrice=expectedPrice-(expectedPrice*20)/100;
				edtEarning.setText(""+expectedPrice);
				
				Runnable runnImage = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					JSONArray jsonArray;
					Bitmap bitmap;
					try {
						jsonArray = jsonObject.getJSONArray("images");
						if(jsonArray!=null){
							if(jsonArray.length()>0){
								
								for (imageCount = 0; imageCount < jsonArray.length(); imageCount++) {
									JSONObject jsonObject1=jsonArray.getJSONObject(imageCount);
									final String imagePath = jsonObject1.getString("url");
									if (imagePath!=null) {
										bitmap = Utility.DownloadImageDirect(imagePath);
										listBitmap.add(bitmap);
									}
								}
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								setImage();
							}
						});
					}
			};
			Thread thread = new Thread(runnImage);
			thread.start();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			txtHeading.setText("Add Item");
		}
		
		
		// adding list into dialog to show category name
		dialogCategoryList= new Dialog(appContext);
		dialogCategoryList.setTitle(R.string.category_title_txt);
		dialogCategoryList.setContentView(R.layout.category_listview);
		ListView listCategoryName = (ListView)dialogCategoryList.findViewById(R.id.listviewCategory);
		
		// adding list into dialog to show category name
		dialogSizeList = new Dialog(appContext);
		dialogSizeList.setTitle(R.string.size_title_txt);
		dialogSizeList.setContentView(R.layout.category_listview);
		listSize = (ListView) dialogSizeList.findViewById(R.id.listviewCategory);
		
		
		// making adaptor object and setting it to listview object
		categoryAdaptor = new ArrayAdapter<String>(appContext,R.layout.listview_item, categotyArrayItem);
		listCategoryName.setAdapter(categoryAdaptor);
		
		// adding list into dialog to show sub category name
		dialogSubCategoryList = new Dialog(appContext);
		dialogSubCategoryList.setTitle(R.string.subcategory_title_txt);
		dialogSubCategoryList.setContentView(R.layout.listview_subcategory);
		ListView listSubCategoryName = (ListView)dialogSubCategoryList.findViewById(R.id.listviewSubCategory);

		// making adaptor object and setting it to listview object
		itemAdapter = new ItemAdapter(appContext, 2,arrayJsonList,this,"");
		listSubCategoryName.setAdapter(itemAdapter);
		
		listSize.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				// TODO Auto-generated method stub
				dialogSizeList.dismiss();
				if(requestSize.equals("DressPropertyList")){
					txtSize.setText(dressSizeList[pos]);
				}else if(requestSize.equals("ShoesPropertyList")){
					txtSize.setText(shoeSizeList[pos]);
				}
				
			}
		});
		
		listCategoryName.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				// TODO Auto-generated method stub
				dialogCategoryList.dismiss();
				txtCategory.setText(categotyArrayItem[pos]);
				if(categotyArrayItem[pos].equals("Men")){
					categoryId="3";
				}else if(categotyArrayItem[pos].equals("Women")){
					categoryId="4";
				}else if(categotyArrayItem[pos].equals("Kids")){
					categoryId="5";
				}
			}
		});
		
		edtListingPrice.addTextChangedListener(this);
		InputStream inputStream = getResources().openRawResource(R.raw.jsonlist);

	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	        int ctr;
	        try {
	            ctr = inputStream.read();
	            while (ctr != -1) {
	                byteArrayOutputStream.write(ctr);
	                ctr = inputStream.read();
	            }
	            inputStream.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        try {

	            // Parse the data into jsonobject to get original data in form of json.  
	        	JSONArray jsonArray = new JSONArray(byteArrayOutputStream.toString());
	            if(jsonArray!=null){
	            	for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject=jsonArray.getJSONObject(i);
						arrayJsonList.add(jsonObject);
	            	}
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.navigationLeftBtn){
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			finish();
		}else if(v.getId()==R.id.image1){
			requestImage=1;  
			String _path = Environment.getExternalStorageDirectory()
		                + File.separator + "camera_sample.jpg";
		        File file = new File(_path);
		        if (file.exists()) {
		            file.delete();
		        }
		        Uri outputFileUri = Uri.fromFile(file);
		        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		        startActivityForResult(intent, requestImage);
		        
		}else if(v.getId()==R.id.image2){
			requestImage=2;
			String _path = Environment.getExternalStorageDirectory()
	                + File.separator + "camera_sample.jpg";
	        File file = new File(_path);
	        if (file.exists()) {
	            file.delete();
	        }
	        Uri outputFileUri = Uri.fromFile(file);
	        Intent intent = new Intent(
	                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	        startActivityForResult(intent, requestImage);
		}else if(v.getId()==R.id.image3){
			requestImage=3;
			String _path = Environment.getExternalStorageDirectory()
	                + File.separator + "camera_sample.jpg";
	        File file = new File(_path);
	        if (file.exists()) {
	            file.delete();
	        }
	        Uri outputFileUri = Uri.fromFile(file);
	        Intent intent = new Intent(
	                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	        startActivityForResult(intent, requestImage);
		}else if(v.getId()==R.id.image4){
			requestImage=4;
			String _path = Environment.getExternalStorageDirectory()
	                + File.separator + "camera_sample.jpg";
	        File file = new File(_path);
	        if (file.exists()) {
	            file.delete();
	        }
	        Uri outputFileUri = Uri.fromFile(file);
	        Intent intent = new Intent(
	                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	        startActivityForResult(intent, CAMERA);
		}else if(v.getId()==R.id.image5){
			requestImage=5;
			String _path = Environment.getExternalStorageDirectory()
	                + File.separator + "camera_sample.jpg";
	        File file = new File(_path);
	        if (file.exists()) {
	            file.delete();
	        }
	        Uri outputFileUri = Uri.fromFile(file);
	        Intent intent = new Intent(
	                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	        startActivityForResult(intent, CAMERA);
		}else if(v.getId()==R.id.image6){
			requestImage=6;
			String _path = Environment.getExternalStorageDirectory()
	                + File.separator + "camera_sample.jpg";
	        File file = new File(_path);
	        if (file.exists()) {
	            file.delete();
	        }
	        Uri outputFileUri = Uri.fromFile(file);
	        Intent intent = new Intent(
	                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	        startActivityForResult(intent, CAMERA);
		}else if(v.getId()==R.id.txtItemCategory){
			dialogCategoryList.show();
		}else if(v.getId()==R.id.txtItemSubCategory){
			dialogSubCategoryList.show();
		}else if(v.getId()==R.id.imageviewCross1){
			imageItem2.setTag("-1");
			listBitmap.remove(1);
			imageCross1.setVisibility(View.GONE);
			imageItem2.setImageResource(R.drawable.take_camera_image);
		}else if(v.getId()==R.id.imageviewCross2){
			imageItem3.setTag("-1");
			listBitmap.remove(2);
			imageCross2.setVisibility(View.GONE);
			imageItem3.setImageResource(R.drawable.take_camera_image);
		}else if(v.getId()==R.id.imageviewCross3){
			imageItem4.setTag("-1");
			listBitmap.remove(3);
			imageItem4.setBackgroundResource(R.drawable.take_camera_image);
		}else if(v.getId()==R.id.imageviewCross4){
			imageItem5.setTag("-1");
			listBitmap.remove(4);
			imageItem5.setBackgroundResource(R.drawable.take_camera_image);
		}else if(v.getId()==R.id.imageviewCross5){
			imageItem6.setTag("-1");
			listBitmap.remove(5);
			imageItem6.setBackgroundResource(R.drawable.take_camera_image);
		}else if(v.getId()==R.id.txtItemSize){
			dialogSizeList.show();
		}else if(v.getId()==R.id.listitem_txt){
			dialogSubCategoryList.dismiss();
			int pos = Integer.parseInt(v.getTag().toString());
			
			try {
				JSONObject jsonObject=arrayJsonList.get(pos);
				txtSubCategory.setText(jsonObject.getString("title"));
				if(jsonObject.getString("sizetype").equals("DressPropertyList")){
					requestSize="DressPropertyList";
					txtSize.setText("");
					txtSize.setVisibility(View.VISIBLE);
					edtOtherSize.setVisibility(View.GONE);
					sizeAdaptor = new ArrayAdapter<String>(appContext,R.layout.listview_item, dressSizeList);
					listSize.setAdapter(sizeAdaptor);
				}else if(jsonObject.getString("sizetype").equals("ShoesPropertyList")){
					requestSize="ShoesPropertyList";
					txtSize.setText("");
					txtSize.setVisibility(View.VISIBLE);
					edtOtherSize.setVisibility(View.GONE);
					sizeAdaptor = new ArrayAdapter<String>(appContext,R.layout.listview_item, shoeSizeList);
					listSize.setAdapter(sizeAdaptor);
				}else if(jsonObject.getString("sizetype").equals("OS")){
					requestSize="OS";
					txtSize.setVisibility(View.GONE);
					edtOtherSize.setText("");
					edtOtherSize.setVisibility(View.VISIBLE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(v.getId()==R.id.navigationRightBtn){
			
			
			String itemTitle=edtTitle.getText().toString().trim();
			String itemOriginalPrice=edtOriginalPrice.getText().toString().trim();
			String itemListingPrice=edtListingPrice.getText().toString().trim();
			String itemDescription=edtDescription.getText().toString().trim();
			String subCategory=txtSubCategory.getText().toString().trim();
			String size=txtSize.getText().toString().trim();
			
			if(itemTitle.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.item_title_alert);
			}else if(itemOriginalPrice.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.item_o_price_alert);
			}else if(itemListingPrice.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.item_l_price_alert);
			}else if(itemDescription.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.item_desc_alert);
			}else if(imageItem1.getTag().toString().equals("-1")){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.item_image_alert);
			}else
			{
				listNameValuePairs.add(new BasicNameValuePair("name",itemTitle));
				listNameValuePairs.add(new BasicNameValuePair("description",itemDescription));
				listNameValuePairs.add(new BasicNameValuePair("category_id",categoryId));
				listNameValuePairs.add(new BasicNameValuePair("sub_category_name",subCategory));
				
				{
					Drawable d = imageItem1.getDrawable(); 
					Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
					if (bitmap!=null) {
						listNameValuePairs.add(new BasicNameValuePair("pic1",Utility.encodeTobase64(bitmap)));
					}
				}
				
				if (imageItem2.isShown() && imageItem2.getTag().equals("1"))
				{
					Drawable d = imageItem2.getDrawable(); 
					Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
					if (bitmap!=null) {
						listNameValuePairs.add(new BasicNameValuePair("pic2",Utility.encodeTobase64(bitmap)));
					}
				}
				if (imageItem3.isShown() && imageItem3.getTag().equals("1"))
				{
					Drawable d = imageItem3.getDrawable(); 
					Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
					if (bitmap!=null) {
						listNameValuePairs.add(new BasicNameValuePair("pic3",Utility.encodeTobase64(bitmap)));
					}
				}
				if (imageItem4.isShown() && imageItem4.getTag().equals("1"))
				{
					Drawable d = imageItem4.getDrawable(); 
					Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
					if (bitmap!=null) {
						listNameValuePairs.add(new BasicNameValuePair("pic4",Utility.encodeTobase64(bitmap)));
					}
				}
				if (imageItem5.isShown() && imageItem5.getTag().equals("1"))
				{
					Drawable d = imageItem5.getDrawable(); 
					Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
					if (bitmap!=null) {
						listNameValuePairs.add(new BasicNameValuePair("pic5",Utility.encodeTobase64(bitmap)));
					}
				}
				if (imageItem6.isShown() && imageItem6.getTag().equals("1"))
				{
					Drawable d = imageItem6.getDrawable(); 
					Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
					if (bitmap!=null) {
						listNameValuePairs.add(new BasicNameValuePair("pic6",Utility.encodeTobase64(bitmap)));
					}
				}
				listNameValuePairs.add(new BasicNameValuePair("user_id",userId));
				listNameValuePairs.add(new BasicNameValuePair("price",itemOriginalPrice));
				listNameValuePairs.add(new BasicNameValuePair("listing_price",itemListingPrice));
				listNameValuePairs.add(new BasicNameValuePair("ship_price","0"));
				listNameValuePairs.add(new BasicNameValuePair("size",size));
				requestFor=Constant.ADD_ITEM;
				new AddItem().execute();
			}
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(savedInstanceState);
	
		savedInstanceState.putInt("index", requestImage);
		savedInstanceState.putParcelableArrayList("list", listBitmap);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		requestImage = savedInstanceState.getInt("index");
		listBitmap = savedInstanceState.getParcelableArrayList("list");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent mediaReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, mediaReturnedIntent);
		  if (resultCode != RESULT_OK) 
		  {
			  return;
		  }else {
				if(requestCode==1){
					String _path = Environment.getExternalStorageDirectory()
							+ File.separator + "camera_sample.jpg";

					Bitmap bm = Utility.loadResizedImage(appContext, new File(
							_path));
					if(listBitmap.size()>=1)
					{
						listBitmap.set(0, bm);
					}else
					{
						listBitmap.add(bm);
					}
					bm=null;
				}else if(requestCode==2){
					String _path = Environment.getExternalStorageDirectory()
							+ File.separator + "camera_sample.jpg";

					Bitmap bm = Utility.loadResizedImage(appContext, new File(
							_path));
					if(listBitmap.size()>=2)
					{
						listBitmap.set(1, bm);
					}else
					{
						listBitmap.add(bm);
					}
					bm=null;
				}else if(requestCode==3){
					String _path = Environment.getExternalStorageDirectory()
							+ File.separator + "camera_sample.jpg";

					Bitmap bm = Utility.loadResizedImage(appContext, new File(
							_path));
					if(listBitmap.size()>=3)
					{
						listBitmap.set(2, bm);
					}else
					{
						listBitmap.add(bm);
					}					
					bm=null;
				}
				setImage();
			}
	}
	
	private void setImage()
	{
		for (int i = 0; i < listBitmap.size(); i++) {
			if (i==0) {
				imageItem1.setTag("1");
				imageItem2.setVisibility(View.VISIBLE);
				imageItem1.setImageBitmap(listBitmap.get(0));
			}
			if (i==1) {
				imageItem2.setTag("1");
				imageCross1.setVisibility(View.VISIBLE);
				imageItem3.setVisibility(View.VISIBLE);
				imageItem2.setImageBitmap(listBitmap.get(1));
			}
			if (i==2) {
				imageItem3.setTag("1");
				imageCross2.setVisibility(View.VISIBLE);
				imageItem3.setImageBitmap(listBitmap.get(2));
			}
		}
	}
	public class AddItem extends AsyncTask<Void, Void, String> {
		ProgressDialog applicationDialog;
		protected void onPreExecute() {
			super.onPreExecute();	
			applicationDialog = ProgressDialog.show(appContext, "","Please Wait...");
			applicationDialog.setCancelable(true);
		}
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String result = null;
			String url=null;
			if(requestFor==Constant.ADD_ITEM){
				url=Constant.serverUrl+"add_items";
			}else if(requestFor==Constant.EDIT_ITEM){
				url=Constant.serverUrl+"edit_items?item_id="+itemId;
			}
			result=Utility.postParamsAndfindJSON(url, listNameValuePairs);
			listNameValuePairs=null;
			return result;
		}
		protected void onPostExecute(String result) {
			System.out.println("result is " + result);
			applicationDialog.dismiss();
			if (result == null) {
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.network_connection_alert);
			}else if(result.equals("Successful")){
				if(requestFor==Constant.ADD_ITEM){
					Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.item_add_alert);
				}else if(requestFor==Constant.EDIT_ITEM){
					Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.item_edit_alert);
				}
			}else if(result.equals("failes")){
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.item_add_failed_alert);
			}
		}
	}
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if(edtListingPrice.getText().toString().length()!=0){
			float expectedPrice=Float.parseFloat(edtListingPrice.getText().toString());
			expectedPrice=expectedPrice-(expectedPrice*20)/100;
			edtEarning.setText(""+expectedPrice);
		}
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
}
