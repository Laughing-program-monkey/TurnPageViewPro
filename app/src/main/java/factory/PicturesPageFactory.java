package factory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import example.com.turnpageviewpro.R;
import utils.BitmapUtils;
import utils.ScreenUtils;

/**
 * 页面内容工厂类：制作图像集合型内容
 */
public class PicturesPageFactory extends PageFactory {
    private Context context;

    public int style;//集合类型
    public final static int STYLE_IDS = 1;//drawable目录图片集合类型
    public final static int STYLE_URIS = 2;//手机本地目录图片集合类型
    private int[] picturesIds;
    /**
     * 初始化drawable目录下的图片id集合
     * @param context
     * @param pictureIds
     */
    public PicturesPageFactory(Context context, int[] pictureIds){
        this.context = context;
        this.picturesIds = pictureIds;
        this.style = STYLE_IDS;
        if (pictureIds.length > 0){
            hasData = true;
            pageTotal = pictureIds.length;
        }
    }

    private String[] picturesUris;
    /**
     * 初始化本地目录下的图片uri集合
     * @param context
     * @param picturesUris
     */
    public PicturesPageFactory(Context context, String[] picturesUris){
        this.context = context;
        this.picturesUris = picturesUris;
            this.style = STYLE_URIS;
        if (picturesUris.length > 0){
            hasData = true;
            pageTotal = picturesUris.length;
        }
    }

    @Override
    public void drawPreviousBitmap(Bitmap bitmap, int pageNum) {
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(getBitmapByIndex(pageNum-2),0,0,null);
    }

    @Override
    public void drawCurrentBitmap(Bitmap bitmap, int pageNum) {
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(getBitmapByIndex(pageNum-1),0,0,null);
    }

    @Override
    public void drawNextBitmap(Bitmap bitmap, int pageNum) {
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(getBitmapByIndex(pageNum),0,0,null);
    }

    @Override
    public Bitmap getBitmapByIndex(int index) {
        if(hasData){
            switch (style){
                case STYLE_IDS:
                    return getBitmapFromIds(index);
                case STYLE_URIS:
                    return getBitmapFromUris();

                default:
                    return null;
            }
        }else {
            return null;
        }
    }
    public Bitmap getBitmapByIndex(int index,int width,int height) {
        if(hasData){
            switch (style){
                case STYLE_IDS:
                    return getBitmapFromIds(index,width,height);
                case STYLE_URIS:
                    return getBitmapFromUris(width,height);
                default:
                    return null;
            }
        }else {
            return null;
        }
    }

    /**
     * 从id集合获取bitmap
     * @param index
     * @return
     */
    private Bitmap getBitmapFromIds(int index){
        return BitmapUtils.drawableToBitmap(
                context.getResources().getDrawable(picturesIds[index]),
                ScreenUtils.getScreenWidth(context),
                ScreenUtils.getScreenHeight(context)
        );
    }

    /**
     * 从id集合获取bitmap
     * @param index
     * @return
     */
    private Bitmap getBitmapFromIds(int index,int width,int height){
        return BitmapUtils.drawableToBitmap(
                context.getResources().getDrawable(picturesIds[index]),
               width,
               height
        );
    }

    /**
     * 从uri集合获取bitmap
     * @param
     * @return
     */
    private Bitmap getBitmapFromUris(){
            return BitmapUtils.drawableToBitmap( context.getResources().getDrawable(R.mipmap.no_banner),
                    ScreenUtils.getScreenWidth(context),
                    ScreenUtils.getScreenHeight(context));
    }
    /**
     * 从uri集合获取bitmap
     * @param
     * @return
     */
    private Bitmap getBitmapFromUris(int width,int height){
      //先用默认的图片填充，以防下载缓慢出现bug
            return BitmapUtils.drawableToBitmap( context.getResources().getDrawable(R.mipmap.no_banner),
                    width,
                    height);
    }
    public int[] getImages(){
        return  picturesIds;
    }
    public String[] getImagesUri(){
        return  picturesUris;
    }

}
