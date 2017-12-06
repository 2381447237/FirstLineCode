package com.example.liutao.cardviewtest;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by liutao on 2017/5/11.
 */

public class HttpUtil {

    public static void sendHttpRequest( final String address, final HttpCallbackListener listener){

        Toast.makeText(MyApplication.getContext(),"当前没有可用网络",Toast.LENGTH_SHORT).show();

         new Thread(
                 new Runnable() {
                     @Override
                     public void run() {
                         HttpURLConnection connection=null;

                         try {
                             URL url=new URL(address);
                             connection= (HttpURLConnection) url.openConnection();
                             connection.setRequestMethod("GET");
                             connection.setConnectTimeout(8000);
                             connection.setReadTimeout(8000);
                             connection.setDoInput(true);
                             connection.setDoOutput(true);
                             InputStream in=connection.getInputStream();
                             BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                             StringBuilder response=new StringBuilder();
                             String line;
                             while ((line=reader.readLine())!=null){
                                 response.append(line);
                             }
                             if(listener !=null){
                                 listener.onFinish(response.toString());
                             }
                         } catch (Exception e) {
                             if(listener!=null){
                                 listener.onError(e);
                             }
                             e.printStackTrace();
                         }finally {
                             if(connection!=null){
                                 connection.disconnect();
                             }
                         }
                     }
                 }
         ).start();

    }

}
