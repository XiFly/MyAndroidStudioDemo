package com.jucai.urltest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ImageView mImageView;
    private Bitmap mBitmap;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                //使用ImageView显示该图片
                mImageView.setImageBitmap(mBitmap);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.imageView);
        new Thread() {
            @Override
            public void run() {
                try {
                    //定义一个URL对象
                    URL url = new URL("http://pic26.nipic.com/20121217/5955207_202254574000_2.jpg");
                    //打开该URL对应的资源的输入流
                    InputStream is = url.openStream();
                    // 从InputStream中解析图片
                    mBitmap = BitmapFactory.decodeStream(is);
                    // 发送消息，通知UI组件显示该图片
                    handler.sendEmptyMessage(0x123);
                    is.close();
                    // 再次打开URL对应的资源的输入流
                    is = url.openStream();
                    // 打开手机文件对应的输出流
                    OutputStream os = openFileOutput("xifly.jpg", MODE_PRIVATE);
                    byte[] buff = new byte[1024];
                    int hasRed = 0;
                    // 将URL对应的资源下载到本地
                    while ((hasRed = is.read(buff)) > 0) {
                        os.write(buff,0,hasRed);
                    }
                    is.close();
                    os.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
