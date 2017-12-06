package com.example.test2017_2_8;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {

	private Button btn;
	private ImageView iv;

	private static final int RESULT_ALBUM_CROP_PATH = 302;
	private static final int RESULT_CAMERA_CROP_PATH_RESULT = 401;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(this);

		iv = (ImageView) findViewById(R.id.image_result);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn:
			// 通过找到图片的路径，指定裁剪
			takeAlbumCropPath();
			break;

		default:
			break;
		}

	}

	private void takeAlbumCropPath() {

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		startActivityForResult(intent, RESULT_ALBUM_CROP_PATH);

	}

	// 裁剪图片
	public void cropImg(Uri uri) {

		File tempFile = getTempFile();
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		//intent.putExtra("crop", "true");
		//intent.putExtra("aspectX", 1);
		//intent.putExtra("aspectY", 1);
		//intent.putExtra("outputX", 700);
		//intent.putExtra("outputY", 700);
		//intent.putExtra("return-data", false);
		intent.putExtra("scale",true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
		//intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, RESULT_CAMERA_CROP_PATH_RESULT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case RESULT_ALBUM_CROP_PATH:

			String picPath = parsePicturePath(MainActivity.this, data.getData());
			File file = new File(picPath);
			Uri uri = Uri.fromFile(file);
			cropImg(uri);

			break;

		case RESULT_CAMERA_CROP_PATH_RESULT:	
			Bundle extras=data.getExtras();
			
			if(extras!=null){
				Bitmap bitmap=BitmapFactory.decodeFile(getTempFile().getAbsolutePath(),null);
				if(bitmap!=null){
					Bitmap smallBmp=setScaleBitmap(bitmap, 2);
					iv.setImageBitmap(smallBmp);
				}
			}
			
			break;
			
		default:
			break;
		}

	}

	private Bitmap setScaleBitmap(Bitmap photo,int SCALE) {
        if (photo != null) {
            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
            //这里缩小了1/2,但图片过大时仍然会出现加载不了,但系统中一个BITMAP最大是在10M左右,我们可以根据BITMAP的大小
            //根据当前的比例缩小,即如果当前是15M,那如果定缩小后是6M,那么SCALE= 15/6
            Bitmap smallBitmap = zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
            //释放原始图片占用的内存，防止out of memory异常发生
            photo.recycle();
            return smallBitmap;
        }
        return null;
    }
	
	private File getTempFile() {
		try {
			return new File(getSDCardPath() + "/temp_crop.jpg");
		} catch (Exception e) {
			Log.d("harvic", e.getMessage());
		}
		return null;
	}

	 public Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
	        int w = bitmap.getWidth();
	        int h = bitmap.getHeight();
	        Matrix matrix = new Matrix();
	        float scaleWidth = ((float) width / w);
	        float scaleHeight = ((float) height / h);
	        matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
	        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	        return newbmp;
	    }
	
	// 解析获取图片库图片Uri物理路径
	public static String parsePicturePath(Context context, Uri uri) {

		if (null == context || uri == null)
			return null;

		boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		// DocumentUri
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageDocumentsUri
			if (isExternalStorageDocumentsUri(uri)) {
				String docId = DocumentsContract.getDocumentId(uri);
				String[] splits = docId.split(":");
				String type = splits[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory()
							+ File.separator + splits[1];
				}
			}
			// DownloadsDocumentsUri
			else if (isDownloadsDocumentsUri(uri)) {
				String docId = DocumentsContract.getDocumentId(uri);
				Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(docId));
				return getDataColumn(context, contentUri, null, null);
			}
			// MediaDocumentsUri
			else if (isMediaDocumentsUri(uri)) {
				String docId = DocumentsContract.getDocumentId(uri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = "_id=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			if (isGooglePhotosContentUri(uri))
				return uri.getLastPathSegment();
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;

	}

	private static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		String column = "_data";
		String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			try {
				if (cursor != null)
					cursor.close();
			} catch (Exception e) {
				Log.e("harvic", e.getMessage());
			}
		}
		return null;

	}

	private static boolean isExternalStorageDocumentsUri(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	private static boolean isDownloadsDocumentsUri(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	private static boolean isMediaDocumentsUri(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	private static boolean isGooglePhotosContentUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	public static String getSDCardPath() {
		String cmd = "cat /proc/mounts";
		Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
		try {
			Process p = run.exec(cmd);// 启动另一个进程来执行命令
			BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			BufferedReader inBr = new BufferedReader(new InputStreamReader(in));

			String lineStr;
			while ((lineStr = inBr.readLine()) != null) {
				// 获得命令执行后在控制台的输出信息
				if (lineStr.contains("sdcard")
						&& lineStr.contains(".android_secure")) {
					String[] strArray = lineStr.split(" ");
					if (strArray != null && strArray.length >= 5) {
						String result = strArray[1].replace("/.android_secure",
								"");
						return result;
					}
				}
				// 检查命令是否执行失败。
				if (p.waitFor() != 0 && p.exitValue() == 1) {
					// p.exitValue()==0表示正常结束，1：非正常结束
				}
			}
			inBr.close();
			in.close();
		} catch (Exception e) {

			return Environment.getExternalStorageDirectory().getPath();
		}

		return Environment.getExternalStorageDirectory().getPath();
	}

}
