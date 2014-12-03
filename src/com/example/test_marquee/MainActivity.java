package com.example.test_marquee;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.R.id;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView tv_marquee;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 0){
				
				tv_marquee.setText("简单实用的Android跑马灯");
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv_marquee = (TextView)findViewById(R.id.tv_marquee);
		new Thread(){

			@Override
			public void run() {
				super.run();
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
			}
			
		}.start();
	}
}
