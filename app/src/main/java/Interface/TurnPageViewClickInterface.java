package Interface;

import android.graphics.Bitmap;

import java.util.List;

public interface TurnPageViewClickInterface {
    void onPageClick(int position);//点击事件的回调
    default void onPageClick(List<Bitmap> bitmaps, int position){};//返回Bitmap集合
    default  void onPageClick(Bitmap currentBitmap, Bitmap nextBitmap, int position){};//返回当前的bitmap和下一页的bitmap
}
