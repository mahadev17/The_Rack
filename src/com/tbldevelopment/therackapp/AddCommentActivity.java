package com.tbldevelopment.therackapp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;
import com.tbldevelopment.therackapp.EditProfileActivity.PostuserInfo;

public class AddCommentActivity extends Activity implements OnClickListener{
	private Context appContext;
	private Button btnBack;
	private Button btnCommentDone;
	private String strComment;
	private String itemId;
	private String userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_comment);
		
		appContext=this;
		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
		
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			itemId=bundle.getString("itemId");
			userId=Utility.getSharedPreferences(appContext, "loginUserId");
			System.out.println("Item id is   "+itemId);
			System.out.println("Item id is   "+userId);
		}
		
		TextView txtHeading=(TextView) findViewById(R.id.txtViewTitle);
		btnBack=(Button) findViewById(R.id.navigationLeftBtn);
		btnCommentDone=(Button) findViewById(R.id.navigationRightBtn);
		
		txtHeading.setVisibility(View.VISIBLE);
		btnBack.setVisibility(View.VISIBLE);
		btnCommentDone.setVisibility(View.VISIBLE);
		
		txtHeading.setText("Add Comment");
		
		btnBack.setOnClickListener(this);
		btnCommentDone.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId()==R.id.navigationLeftBtn){
			finish();
		}else if(v.getId()==R.id.navigationRightBtn){
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(btnCommentDone.getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
			EditText edtComment=(EditText) findViewById(R.id.edtCommentAdd);
			strComment=edtComment.getText().toString().trim();
			try {
				strComment = URLEncoder.encode(strComment, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(strComment.length()==0){
				Utility.ShowAlertWithMessage(appContext, R.string.alert,R.string.comment_txt);
			}else{
				new PostComment().execute();
			}
			
		}
	}

	public class PostComment extends AsyncTask<Void, Void, String> {

		String result;
		ProgressDialog applicationDialog;
		String url;

		protected void onPreExecute() {
			super.onPreExecute();
			
			applicationDialog = ProgressDialog.show(appContext, "","Please wait...", false, true, new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					PostComment.this.cancel(true);
				}
			});
			
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub

			url = Constant.serverUrl+"add_comment?item_id="+itemId+"&user_id="+userId+"&comment="+strComment;
			result = Utility.findJSONFromUrl(url);
			return result;
		}

		protected void onPostExecute(String result) {
			System.out.println("result is " + result);
			if (result == null) {
				applicationDialog.dismiss();
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.network_connection_alert);
			}else if(result.equals("-1")){
				applicationDialog.dismiss();
				Utility.ShowAlertWithMessage(appContext, R.string.alert, R.string.comment_alert);
			}else{
				applicationDialog.dismiss();
				Utility.setSharedPreferenceBoolean(appContext, "refreshComment", true);
				finish();
				Toast.makeText(appContext, "Comment Add Succesfully", Toast.LENGTH_SHORT).show();
			}
		}
	
	}
	
}
