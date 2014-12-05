package com.example.test_marquee;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends Activity {

	private MarqueeView marqueeView;

	private String[] strings={"跑马灯测试","赤橙黄绿青蓝紫","猜","这是一串很长很长很长很长的数字11233333666699999"};
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {

				marqueeView.setText(strings[(int) Math.round(Math.random() * 10)%strings.length]);
			} else if (msg.what == 1) {
				System.out.println("=================================");

				LinearLayout.LayoutParams lp = (LayoutParams) marqueeView
						.getLayoutParams();
				lp.width = 800;
				lp.height = 50;
				marqueeView.setLayoutParams(lp);
			}else if(msg.what==2){
				System.out.println("=================================");

				LinearLayout.LayoutParams lp = (LayoutParams) marqueeView
						.getLayoutParams();
				lp.width = 1000;
				lp.height = 200;
				marqueeView.setLayoutParams(lp);
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		marqueeView = (MarqueeView) findViewById(R.id.marqueeView);
		/*new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					sleep(3000);
					handler.sendEmptyMessage(0);
					sleep(5000);
					handler.sendEmptyMessage(1);
					sleep(5000);
					handler.sendEmptyMessage(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}.start();*/
	}
	
	public void onBtn1Click(View v){
		handler.sendEmptyMessage(0);
	}
	public void onBtn2Click(View v){
		handler.sendEmptyMessage(1);
	}
	public void onBtn3Click(View v){
		handler.sendEmptyMessage(2);
	}
	
	
}
