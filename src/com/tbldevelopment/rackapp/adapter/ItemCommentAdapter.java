package com.tbldevelopment.rackapp.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.therackapp.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemCommentAdapter extends ArrayAdapter<JSONObject> {

	private LayoutInflater lflater;
	Context appContext;
	private ArrayList<JSONObject> jsonList;
	private int requestFor;
	HashMap<String, Bitmap> hmImages;

	public ItemCommentAdapter(Context context, int requested,
			ArrayList<JSONObject> list, HashMap<String, Bitmap> _hmImages) {
		super(context, requested, list);
		// TODO Auto-generated constructor stub

		jsonList = list;
		System.out.println("Json List is    in adapter   " + jsonList);
		appContext = context;
		hmImages = _hmImages;
		requestFor = requested;
		lflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View holder = convertView;
		if (holder == null) {

			switch (requestFor) {
			case Constant.GET_COMMENTS:
				holder = lflater.inflate(R.layout.item_list_item_details, null);
				break;
			case Constant.GET_MESSAGE_NOTIFICATION:
				holder = lflater.inflate(R.layout.show_notification_listitem,
						null);
				break;

			}
		} // fif ends

		try {
			JSONObject obj;
			obj = jsonList.get(position);
			if (obj != null) {

				switch (requestFor) {

				case Constant.GET_COMMENTS:
					ImageView profile_image = (ImageView) holder
							.findViewById(R.id.imageviewProfileComment);
					TextView txtComment = (TextView) holder
							.findViewById(R.id.txtComment);
					TextView txtCommentUsername = (TextView) holder
							.findViewById(R.id.txtCommentUserName);
					TextView txtCommentDate = (TextView) holder
							.findViewById(R.id.txtCommentDate);

					txtCommentUsername.setVisibility(View.VISIBLE);
					txtCommentDate.setVisibility(View.VISIBLE);
					txtComment.setText(obj.getString("comment"));
					txtCommentUsername.setText(obj.getString("name"));
					txtCommentDate.setText(obj.getString("created_at"));

					try {

						Bitmap bitmapPhoto = hmImages.get(obj
								.getString("comment_id"));
						if (bitmapPhoto == null) {
						} else {
							profile_image.setImageBitmap(bitmapPhoto);
						}
					} catch (OutOfMemoryError e) {

						e.printStackTrace();
					}

					break;

				case Constant.GET_MESSAGE_NOTIFICATION:

					ImageView profile_imageview = (ImageView) holder
							.findViewById(R.id.profile_pic_notification_item);
					
					ImageView item_imageview = (ImageView) holder
							.findViewById(R.id.item_pic_notification_item);
					
					TextView txtNotificationContent = (TextView) holder
							.findViewById(R.id.txtLike_notification_item);
					TextView txtSenderName = (TextView) holder
							.findViewById(R.id.txtSenderNameNotification);

					JSONObject jsonNotification = obj
							.getJSONObject("notifications");
					
					String notificationType=jsonNotification.getString("notify_type");
					
					if(notificationType.equals("comment")){
						txtNotificationContent.setText(obj.getString("sender_name")+" comment on your post");
					}else if(notificationType.equals("likes")){
						txtNotificationContent.setText(obj.getString("sender_name")+" liked on your post");
					}
					
					txtSenderName.setText(jsonNotification.getString("created_at"));

					try {

						Bitmap bitmapPhoto = hmImages.get(jsonNotification.getString("sender_id"));
						if (bitmapPhoto == null) {
						} else {
							profile_imageview.setImageBitmap(bitmapPhoto);
						}
					} catch (OutOfMemoryError e) {

						e.printStackTrace();
					}
					
					try {

						Bitmap bitmapPhoto = hmImages.get(jsonNotification.getString("id"));
						if (bitmapPhoto == null) {
						} else {
							item_imageview.setImageBitmap(bitmapPhoto);
						}
					} catch (OutOfMemoryError e) {

						e.printStackTrace();
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return holder;
	}

}