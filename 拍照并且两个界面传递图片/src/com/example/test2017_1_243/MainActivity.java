package com.example.test2017_1_243;

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
import android.provider.MediaStore.Images.Media;
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
	private Button btn;
	private ImageView iv;
	private Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		btn = (Button) findViewById(R.id.btn);
		iv = (ImageView) findViewById(R.id.iv);
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent=new Intent(MainActivity.this,Activity2.class);
				intent.putExtra("123", imageUri);
				startActivity(intent);
				
			}
		});
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				takePhoto();

			}
		});
	}

	private void takePhoto() {

		// 创建File对象，用于存储拍照后的图片
		File outputImage = new File(Environment.getExternalStorageDirectory(),
				"hehe.jpg");

		try {
			if (outputImage.exists()) {
				outputImage.delete();
			}
			outputImage.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 将File转化为Uri
		imageUri = Uri.fromFile(outputImage);
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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
				startActivityForResult(intent, CROP_PHOTO);
			}
			
			break;
		case CROP_PHOTO:

			if(resultCode==RESULT_OK){
				
				try {
					Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
				
				    iv.setImageBitmap(bitmap);
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
