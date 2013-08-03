package com.tbldevelopment.therackapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tbldevelopment.rackapp.constant.Constant;
import com.tbldevelopment.rackapp.util.Utility;


public class SplashScreen extends Activity implements OnClickListener{
	protected boolean _active = true;
	protected int _splashTime = 1850; // Splash screen time
	Context appContext;
	boolean signin_status;

	LinearLayout layout;
	public static final int REQUEST_CODE_PLUS_CLIENT_FRAGMENT = 0;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);
		
		//initialize  context 
		appContext=this;
		
		//View's object
		layout=(LinearLayout) findViewById(R.id.view);
		Button fbLoginBtn=(Button) findViewById(R.id.fbLoginBtn);
		Button emailLoginBtn=(Button) findViewById(R.id.emailLoginBtn);
		
		//Button Click Listener Object
		fbLoginBtn.setOnClickListener(this);
		emailLoginBtn.setOnClickListener(this);
		/*final ImageView splashImageView = (ImageView) findViewById(R.id.imageAnimFrames);
		try
		{
		//Start Animation for open door
	    splashImageView.setBackgroundResource(R.anim.flag);
		}catch(OutOfMemoryError e)
		{
			e.printStackTrace();
		}
		
	    final AnimationDrawable frameAnimation= (AnimationDrawable)splashImageView.getBackground();*/
	    Handler handler = new Handler();
	    handler.postDelayed(new Runnable(){

	    public void run(){

/*	    if(frameAnimation!=null)
	      	frameAnimation.start();*/
	      //thread for timer
	    	Thread splashTread = new Thread() {
				@Override
				public void run() {
					try {
						int waited = 70;
						while (_active && (waited < _splashTime)) {
							sleep(180);
							if (_active) {
								waited += 70;
							}
						}
					} catch (InterruptedException e) {
						// do nothing
					} finally {

					}
					runOnUiThread(endSplashThread);
				}
			};
			splashTread.start();
	    }
	    }, 3000);
	    
  
    
 

    	 
		 //start thread
	}//end oncreate method 
	
    
	private Runnable endSplashThread = new Runnable() {
		public void run() {
			
			//Check User is login or not
			if(Utility.getSharedPreferencesBoolean(appContext, "isLogin")==true){
				Intent i=new Intent(appContext,HomeActivity.class);
				startActivity(i);
				finish();
			}else{
				layout.setVisibility(View.VISIBLE);
			}
		}
	};
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.fbLoginBtn){
			//facebook click listener
			Intent i=new Intent(appContext,ShareOnFacebook.class);
			i.putExtra("requestFor", Constant.FACEBOOK_LOGIN);
			startActivity(i);
		}else if(v.getId()==R.id.emailLoginBtn){
			//email click listener
			Intent i=new Intent(appContext,SignInActivity.class);
			startActivity(i);
		}
	}
}//end main class 
