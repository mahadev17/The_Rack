package com.tbldevelopment.therackapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CardPaymentActivty extends Activity implements OnClickListener{
	private Context appContext;
	private Button btnBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_pament);
		
		appContext=this;
		LayoutInflater inflater = LayoutInflater.from(appContext);
		View navigationView = inflater.inflate(R.layout.navigation_bar, null);
		((LinearLayout) findViewById(R.id.topbar)).addView(navigationView);
		
		TextView txtHeading=(TextView) findViewById(R.id.txtViewTitle);
		txtHeading.setVisibility(View.VISIBLE);
		txtHeading.setText("Card Number");
		
		btnBack=(Button) findViewById(R.id.navigationLeftBtn);
		btnBack.setVisibility(View.VISIBLE);
		btnBack.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.navigationLeftBtn){
			finish();
		}
	}

}
