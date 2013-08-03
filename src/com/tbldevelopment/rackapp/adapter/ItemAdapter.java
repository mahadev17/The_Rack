package com.tbldevelopment.rackapp.adapter;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
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

public class ItemAdapter extends ArrayAdapter<JSONObject> {
	private LayoutInflater lflater;
	Context appContext;
	private ArrayList<JSONObject> jsonList;
	private int requestFor;
	private String username;
	OnClickListener clickListener;
	public ItemAdapter(Context context, int requested,
			ArrayList<JSONObject> list,OnClickListener clickListener,String username) {
		super(context,requested, list);
		// TODO Auto-generated constructor stub
		
		jsonList = list;
		System.out.println("Json List is    in adapter   "+jsonList);
		appContext = context;
		requestFor = requested;
		this.username=username;
		this.clickListener=clickListener;
		lflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	

	public View getView(final int position, View convertView, ViewGroup parent) {
		View holder = convertView;
		if (holder == null) {
			
			switch(requestFor){
				case Constant.GET_ITEM:
					holder = lflater.inflate(R.layout.rack_itemlist, null);
					break;
					
				case Constant.GET_SUB_CATEGORY:
					holder = lflater.inflate(R.layout.listview_item, null);
					break;		
			
			}
		} // fif ends
		
		try {
			JSONObject obj;
			obj = jsonList.get(position);
			if (obj != null) {
			
				switch(requestFor){
				
				case Constant.GET_ITEM:
					LinearLayout linearLayoutUserInfo=(LinearLayout)holder.findViewById(R.id.linearLayoutUserInfo);
					LinearLayout linearLayoutItemInfo=(LinearLayout)holder.findViewById(R.id.linearLayoutItemInfo);
					TextView txtUserName = (TextView) holder.findViewById(R.id.txtUsernameItemList);
					TextView txtPostDate = (TextView) holder.findViewById(R.id.txtPostDateItemList);
					//TextView txtLocation = (TextView) holder.findViewById(R.id.txtLocationItemList);
					TextView txtSize = (TextView) holder.findViewById(R.id.txtSizeItemList);
					TextView txtOriginalPrice = (TextView) holder.findViewById(R.id.txtOriginalPriceItemList);
					TextView txtExpectedPrice = (TextView) holder.findViewById(R.id.txtExpectedPriceItemList);
					TextView txtImageTitle = (TextView) holder.findViewById(R.id.txtImageTitleItemList);
					ImageView profileImage=(ImageView) holder.findViewById(R.id.imageviewProfilePicItemList);
					ImageView itemImage=(ImageView) holder.findViewById(R.id.imageviewItemList);
					
					linearLayoutUserInfo.setTag(String.valueOf(position));
					linearLayoutItemInfo.setTag(String.valueOf(position));
					
				
					JSONObject jsonObjectUser= obj.getJSONObject("user");
					//JSONObject jsonObjectItemImage= obj.getJSONObject("images");
					 
					 
					 	txtImageTitle.setText(obj.getString("title"));
						txtOriginalPrice.setText("$"+obj.getString("price"));
						txtExpectedPrice.setText("$"+obj.getString("listing_price"));
						txtPostDate.setText(obj.getString("created_at"));
						txtUserName.setText(jsonObjectUser.getString("name"));
						
						txtOriginalPrice.setPaintFlags(txtOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
						
						/*if(jsonObjectUser.getString("city")!="null"){
							txtLocation.setText(jsonObjectUser.getString("city"));
							
						}else{
								txtLocation.setVisibility(View.GONE);
						}*/
						
						if(obj.getString("size")!="null"){
							txtSize.setText(obj.getString("size"));
							
						}else{
							txtSize.setVisibility(View.GONE);
						}
					linearLayoutUserInfo.setOnClickListener(clickListener);
					linearLayoutItemInfo.setOnClickListener(clickListener);
					break;
					
				
				
				case Constant.GET_SUB_CATEGORY:
					TextView txtList = (TextView) holder.findViewById(R.id.listitem_txt);
					txtList.setTag(String.valueOf(position));
					
					txtList.setText(obj.getString("title"));
					txtList.setOnClickListener(clickListener);
					
					break;
				
				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return holder;
	}
}
