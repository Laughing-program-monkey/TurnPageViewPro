package utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownLoadImage {
    private  Bitmap bitmap;
    private static DownLoadImage downLoadImage;
    public static DownLoadImage getInstance(){
        if(downLoadImage==null){
            synchronized (DownLoadImage.class){
                if(downLoadImage==null){
                    downLoadImage=new DownLoadImage();
                }
            }
        }
        return downLoadImage;
    }
    //根据路径获取图片的BitMap
    public static Bitmap GetImageInputStream(String imageurl) {
        URL url;
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(imageurl);
            connection=(HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e)
        { e.printStackTrace(); }
        return bitmap;
    }
   //读取手机本地图片
    public static Bitmap getImageBitmapFromSdcard(String imageUrl){
        Bitmap bitmap=null;
        try {
            FileInputStream fileInputStream=new FileInputStream(imageUrl);
            bitmap=BitmapFactory.decodeStream(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
