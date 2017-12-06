package com.example.choosepictest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

	public static final int TAKE_PHOTO = 10000;
	public static final int CROP_PHOTO = 20000;
	private Button takePhoto;
	private ImageView picture;
	private Uri imageUri;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		takePhoto=(Button) findViewById(R.id.take_photo);
		picture=(ImageView) findViewById(R.id.picture);
		takePhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				takePhoto();
				
			}
		});
	}

	private void takePhoto(){
		
		// 创建File对象，用于存储拍照后的图片
		File outputImage=new File(Environment.getExternalStorageDirectory(),"huluwa.jpg");
		
		try {
		if(outputImage.exists()){
			outputImage.delete();
		}
		
			outputImage.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//将File转化为Uri
		imageUri=Uri.fromFile(outputImage);
		Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
		// 启动相机程序
		startActivityForResult(intent, TAKE_PHOTO);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
		case TAKE_PHOTO:
			
			if(resultCode==RESULT_OK){
				
				Intent intent=new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(imageUri, "image/*");
				intent.putExtra("scale",true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
				// 启动裁剪程序
				startActivityForResult(intent,CROP_PHOTO);
			}
			
			break;

		case CROP_PHOTO:
			
			if(resultCode==RESULT_OK){
				
				try {
					Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
					// 将裁剪后的照片显示出来
				     picture.setImageBitmap(bitmap);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
			break;
			
		default:
			break;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
