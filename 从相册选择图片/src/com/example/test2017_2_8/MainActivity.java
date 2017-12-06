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
			// ͨ���ҵ�ͼƬ��·����ָ���ü�
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

	// �ü�ͼƬ
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
            //Ϊ��ֹԭʼͼƬ�������ڴ��������������Сԭͼ��ʾ��Ȼ���ͷ�ԭʼBitmapռ�õ��ڴ�
            //������С��1/2,��ͼƬ����ʱ��Ȼ����ּ��ز���,��ϵͳ��һ��BITMAP�������10M����,���ǿ��Ը���BITMAP�Ĵ�С
            //���ݵ�ǰ�ı�����С,�������ǰ��15M,���������С����6M,��ôSCALE= 15/6
            Bitmap smallBitmap = zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
            //�ͷ�ԭʼͼƬռ�õ��ڴ棬��ֹout of memory�쳣����
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
	        matrix.postScale(scaleWidth, scaleHeight);// ���þ���������Ų�������ڴ����
	        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	        return newbmp;
	    }
	
	// ������ȡͼƬ��ͼƬUri����·��
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
		Runtime run = Runtime.getRuntime();// �����뵱ǰ Java Ӧ�ó�����ص�����ʱ����
		try {
			Process p = run.exec(cmd);// ������һ��������ִ������
			BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			BufferedReader inBr = new BufferedReader(new InputStreamReader(in));

			String lineStr;
			while ((lineStr = inBr.readLine()) != null) {
				// �������ִ�к��ڿ���̨�������Ϣ
				if (lineStr.contains("sdcard")
						&& lineStr.contains(".android_secure")) {
					String[] strArray = lineStr.split(" ");
					if (strArray != null && strArray.length >= 5) {
						String result = strArray[1].replace("/.android_secure",
								"");
						return result;
					}
				}
				// ��������Ƿ�ִ��ʧ�ܡ�
				if (p.waitFor() != 0 && p.exitValue() == 1) {
					// p.exitValue()==0��ʾ����������1������������
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
