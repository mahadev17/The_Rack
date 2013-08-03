package com.tbldevelopment.rackapp.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;
import com.tbldevelopment.therackapp.R;

public class GetItemAdapter extends ArrayAdapter<JSONObject> {
	private LayoutInflater lflater;
	Context appContext;
	private ArrayList<JSONObject> jsonList;
	private int requestFor;
	private String username;
	OnClickListener clickListener;
	HashMap<String, Bitmap> hmImages;

	public GetItemAdapter(Context context, int requested,
			ArrayList<JSONObject> list, OnClickListener clickListener,
			String username, HashMap<String, Bitmap> _hmImages) {
		super(context, requested, list);
		// TODO Auto-generated constructor stub

		jsonList = list;
		System.out.println("Json List is    in adapter   " + jsonList);
		appContext = context;
		requestFor = requested;
		this.username = username;
		hmImages = _hmImages;
		this.clickListener = clickListener;
		lflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View holder = convertView;
		if (holder == null) {

			switch (requestFor) {
			case Constant.GET_ITEM:
				holder = lflater.inflate(R.layout.rack_itemlist, null);
				break;
				
			case Constant.MY_LIKES:
				holder = lflater.inflate(R.layout.rack_itemlist, null);
				break;	
				
			case Constant.USER_INFO:
				holder = lflater.inflate(R.layout.custome_gridview, null);
				break;
			case Constant.MY_SALE:
				holder = lflater.inflate(R.layout.custome_gridview, null);
				break;	
				
			case Constant.FOLLOWER:
				holder = lflater.inflate(R.layout.item_list_item_details, null);
				break;
			case Constant.FOLLOWING:
				holder = lflater.inflate(R.layout.item_list_item_details, null);
				break;	
			}
		} // fif ends

		try {
			JSONObject obj;
			obj = jsonList.get(position);
			if (obj != null) {

				switch (requestFor) {

				case Constant.GET_ITEM:
					LinearLayout linearLayoutUserInfo = (LinearLayout) holder
							.findViewById(R.id.linearLayoutUserInfo);
					LinearLayout linearLayoutItemInfo = (LinearLayout) holder
							.findViewById(R.id.linearLayoutItemInfo);
					TextView txtUserName = (TextView) holder
							.findViewById(R.id.txtUsernameItemList);
					TextView txtPostDate = (TextView) holder
							.findViewById(R.id.txtPostDateItemList);
					TextView txtSize = (TextView) holder
							.findViewById(R.id.txtSizeItemList);
					TextView txtOriginalPrice = (TextView) holder
							.findViewById(R.id.txtOriginalPriceItemList);
					TextView txtExpectedPrice = (TextView) holder
							.findViewById(R.id.txtExpectedPriceItemList);
					TextView txtImageTitle = (TextView) holder
							.findViewById(R.id.txtImageTitleItemList);
					ImageView profileImage = (ImageView) holder
							.findViewById(R.id.imageviewProfilePicItemList);
					ImageView itemImage = (ImageView) holder
							.findViewById(R.id.imageviewItemList);

					linearLayoutUserInfo.setTag(String.valueOf(position));
					linearLayoutItemInfo.setTag(String.valueOf(position));

					JSONObject jsonObjectUser = obj.getJSONObject("user");

					txtImageTitle.setText(obj.getString("title"));
					txtOriginalPrice.setText("$" + obj.getString("price"));
					txtExpectedPrice.setText("$"+ obj.getString("listing_price"));
					txtUserName.setText(jsonObjectUser.getString("name"));
					
					txtPostDate.setText(obj.getString("created_at"));
					

					txtOriginalPrice.setPaintFlags(txtOriginalPrice
							.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

					
					if (obj.getString("size") != "null") {
						txtSize.setText("Size : "+obj.getString("size"));

					} else {
						txtSize.setText("Size : 0");
					}

					try {
						Bitmap bitmapPhoto = hmImages.get(jsonObjectUser
								.getString("id"));
						if (bitmapPhoto == null) {
							profileImage
									.setBackgroundResource(R.drawable.profile_default);
						} else {
							profileImage.setImageBitmap(bitmapPhoto);
						}
						bitmapPhoto = null;
					} catch (OutOfMemoryError e) {

						e.printStackTrace();
					}

					try {
						Bitmap bitmapPhoto = hmImages.get(obj.getString("id"));
						if (bitmapPhoto == null) {
							itemImage
									.setBackgroundResource(R.drawable.profile_default);
						} else {
							itemImage.setImageBitmap(bitmapPhoto);
						}
						bitmapPhoto = null;
					} catch (OutOfMemoryError e) {

						e.printStackTrace();
					}

					linearLayoutUserInfo.setOnClickListener(clickListener);
					linearLayoutItemInfo.setOnClickListener(clickListener);
					break;

				case Constant.MY_LIKES:
					LinearLayout linearLayoutUserInfo1 = (LinearLayout) holder
							.findViewById(R.id.linearLayoutUserInfo);
					LinearLayout linearLayoutItemInfo1 = (LinearLayout) holder
							.findViewById(R.id.linearLayoutItemInfo);
					TextView txtUserName1 = (TextView) holder
							.findViewById(R.id.txtUsernameItemList);
					TextView txtPostDate1 = (TextView) holder
							.findViewById(R.id.txtPostDateItemList);
					TextView txtSize1 = (TextView) holder
							.findViewById(R.id.txtSizeItemList);
					TextView txtOriginalPrice1 = (TextView) holder
							.findViewById(R.id.txtOriginalPriceItemList);
					TextView txtExpectedPrice1 = (TextView) holder
							.findViewById(R.id.txtExpectedPriceItemList);
					TextView txtImageTitle1 = (TextView) holder
							.findViewById(R.id.txtImageTitleItemList);
					ImageView profileImage1 = (ImageView) holder
							.findViewById(R.id.imageviewProfilePicItemList);
					ImageView itemImage1 = (ImageView) holder
							.findViewById(R.id.imageviewItemList);

					linearLayoutUserInfo1.setTag(String.valueOf(position));
					linearLayoutItemInfo1.setTag(String.valueOf(position));

					JSONObject jsonObjectUser1 = obj.getJSONObject("user");

					txtImageTitle1.setText(obj.getString("title"));
					txtOriginalPrice1.setText("$" + obj.getString("price"));
					txtExpectedPrice1.setText("$"+ obj.getString("listing_price"));
					txtUserName1.setText(jsonObjectUser1.getString("name"));
					
					txtPostDate1.setText(obj.getString("created_at"));
					

					txtOriginalPrice1.setPaintFlags(txtOriginalPrice1
							.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

					
					if (obj.getString("size") != "null") {
						txtSize1.setText("Size : "+obj.getString("size"));

					} else {
						txtSize1.setText("Size : 0");
					}

					try {
						Bitmap bitmapPhoto = hmImages.get(jsonObjectUser1
								.getString("id"));
						if (bitmapPhoto == null) {
							profileImage1
									.setBackgroundResource(R.drawable.profile_default);
						} else {
							profileImage1.setImageBitmap(bitmapPhoto);
						}
						bitmapPhoto = null;
					} catch (OutOfMemoryError e) {

						e.printStackTrace();
					}

					try {
						Bitmap bitmapPhoto = hmImages.get(obj.getString("id"));
						if (bitmapPhoto == null) {
							itemImage1
									.setBackgroundResource(R.drawable.profile_default);
						} else {
							itemImage1.setImageBitmap(bitmapPhoto);
						}
						bitmapPhoto = null;
					} catch (OutOfMemoryError e) {

						e.printStackTrace();
					}

					linearLayoutUserInfo1.setOnClickListener(clickListener);
					linearLayoutItemInfo1.setOnClickListener(clickListener);
					break;

					
				case Constant.USER_INFO:
					TextView txtTitleGrid = (TextView) holder.findViewById(R.id.txtTitleGridItem);
					TextView txtPriceGrid = (TextView) holder.findViewById(R.id.txtPriceGridItem);
					TextView txtSizeGrid = (TextView) holder.findViewById(R.id.txtSizeGridItem);
					ImageView imageItem=(ImageView) holder.findViewById(R.id.imageviewGridItem);
					
					txtTitleGrid.setText(obj.getString("title"));
					txtPriceGrid.setText("$"+obj.getString("listing_price"));
					if(obj.getString("size").equals("null")){
						txtSizeGrid.setText("Size: 0");
					}else{
						txtSizeGrid.setText("Size: "+obj.getString("size"));
					}
					
					try {
						Bitmap bitmapPhoto = hmImages.get(obj.getString("id"));
						if (bitmapPhoto == null) {
						} else {
							imageItem.setImageBitmap(bitmapPhoto);
						}
						bitmapPhoto = null;
					} catch (OutOfMemoryError e) {

						e.printStackTrace();
					}
					break;		
					
				case Constant.MY_SALE:
					TextView txtTitleGrid1 = (TextView) holder.findViewById(R.id.txtTitleGridItem);
					TextView txtPriceGrid1 = (TextView) holder.findViewById(R.id.txtPriceGridItem);
					TextView txtSizeGrid1 = (TextView) holder.findViewById(R.id.txtSizeGridItem);
					ImageView imageItem1=(ImageView) holder.findViewById(R.id.imageviewGridItem);
					
					txtTitleGrid1.setText(obj.getString("title"));
					txtPriceGrid1.setText("$"+obj.getString("listing_price"));
					if(obj.getString("size").equals("null")){
						txtSizeGrid1.setText("Size: 0");
					}else{
						txtSizeGrid1.setText("Size: "+obj.getString("size"));
					}
					
					try {
						Bitmap bitmapPhoto = hmImages.get(obj.getString("id"));
						if (bitmapPhoto == null) {
						} else {
							imageItem1.setImageBitmap(bitmapPhoto);
						}
						bitmapPhoto = null;
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					}
					break;
					
				case Constant.MY_PURCHASE:
					TextView txtTitleGrid2 = (TextView) holder.findViewById(R.id.txtTitleGridItem);
					TextView txtPriceGrid2 = (TextView) holder.findViewById(R.id.txtPriceGridItem);
					TextView txtSizeGrid2 = (TextView) holder.findViewById(R.id.txtSizeGridItem);
					ImageView imageItem2=(ImageView) holder.findViewById(R.id.imageviewGridItem);
					
					txtTitleGrid2.setText(obj.getString("title"));
					txtPriceGrid2.setText("$"+obj.getString("listing_price"));
					if(obj.getString("size").equals("null")){
						txtSizeGrid2.setText("Size: 0");
					}else{
						txtSizeGrid2.setText("Size: "+obj.getString("size"));
					}
					
					try {
						Bitmap bitmapPhoto = hmImages.get(obj.getString("id"));
						if (bitmapPhoto == null) {
						} else {
							imageItem2.setImageBitmap(bitmapPhoto);
						}
						bitmapPhoto = null;
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					}
					break;
					
					
				case Constant.FOLLOWER:
					ImageView profileImage2=(ImageView) holder.findViewById(R.id.imageviewProfileComment);
					TextView txtUsername=(TextView)holder.findViewById(R.id.txtComment);
					Button btnFollow=(Button) holder.findViewById(R.id.btnFollowFollowList);
					JSONObject jsonObject=obj.getJSONObject("user_inf");
					txtUsername.setText(jsonObject.getString("name"));
					
					btnFollow.setTag(String.valueOf(position));
					
					if(obj.getString("is_follow").equals("true")){
						btnFollow.setText("Unfollow");
					}else if(obj.getString("is_follow").equals("false")){
						btnFollow.setText("Follow");
					}
					
					String loginUserId=Utility.getSharedPreferences(appContext, "loginUserId");
					String reqUser=jsonObject.getString("id");
					if(loginUserId.equals(reqUser)){
						btnFollow.setVisibility(View.GONE);
					}else{
						btnFollow.setVisibility(View.VISIBLE);
					}
					
					try {
						Bitmap bitmapPhoto = hmImages.get(jsonObject.getString("id"));
						if (bitmapPhoto == null) {
						} else {
							profileImage2.setImageBitmap(bitmapPhoto);
						}
						bitmapPhoto = null;
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					}
					
					btnFollow.setOnClickListener(clickListener);
					
				break;
				
				case Constant.FOLLOWING:
					ImageView profileImage3=(ImageView) holder.findViewById(R.id.imageviewProfileComment);
					TextView txtUsername1=(TextView)holder.findViewById(R.id.txtComment);
					Button btnFollow1=(Button) holder.findViewById(R.id.btnFollowFollowList);
					JSONObject jsonObject1=obj.getJSONObject("user_inf");
					txtUsername1.setText(jsonObject1.getString("name"));
					
					btnFollow1.setTag(String.valueOf(position));
					
					if(obj.getString("is_follow").equals("true")){
						btnFollow1.setText("Unfollow");
					}else if(obj.getString("is_follow").equals("false")){
						btnFollow1.setText("Follow");
					}
					try {
						Bitmap bitmapPhoto = hmImages.get(jsonObject1.getString("id"));
						if (bitmapPhoto == null) {
						} else {
							profileImage3.setImageBitmap(bitmapPhoto);
						}
						bitmapPhoto = null;
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					}
					btnFollow1.setVisibility(View.VISIBLE);
					btnFollow1.setOnClickListener(clickListener);
				break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return holder;
	}
}
