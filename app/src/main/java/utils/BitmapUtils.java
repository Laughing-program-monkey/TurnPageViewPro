package utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by anlia on 2017/12/11.
 */

public class BitmapUtils {
    /**
     * drawable图片资源转bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * drawable图片资源转bitmap并重置宽高
     * @param drawable
     * @param newW
     * @param newH
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable, int newW, int newH) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return changeBitmapSize(bitmap,newW,newH);
    }
    /*
     * 根据图片的uri生成BitMap
     *
     * */
    public static Bitmap drawableToBitmap(String uri, int newW, int newH) {
       BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap=BitmapFactory.decodeFile(uri,options);
        if(bitmap != null){
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight);
        }
        return changeBitmapSize(bitmap,newW,newH);
    }

    /**
     * 改变bitmap的大小
     * @param bitmap 目标bitmap
     * @param newW 目标宽度
     * @param newH 目标高度
     * @return
     */
    public static Bitmap changeBitmapSize(Bitmap bitmap, int newW, int newH) {
        int oldW = bitmap.getWidth();
        int oldH = bitmap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newW) / oldW;
        float scaleHeight = ((float) newH) / oldH;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, oldW, oldH, matrix, true);
        return bitmap;
    }


}
