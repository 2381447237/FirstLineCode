package com.example.test2017_1_243;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

public class Activity2 extends Activity{

	private ImageView iv;
	
	private Uri myUri;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity2);
		
		iv=(ImageView) findViewById(R.id.iv2);
		
		Intent intent=getIntent();
		
		myUri=intent.getParcelableExtra("123");
		
		try {
			Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(myUri));
			
			iv.setImageBitmap(bitmap);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
