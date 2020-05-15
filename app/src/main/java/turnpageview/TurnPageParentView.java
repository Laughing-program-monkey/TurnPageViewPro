package turnpageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import Interface.InitFactory;
import Interface.TurnPageViewClickInterface;
import factory.PicturesPageFactory;
import utils.BitmapUtils;
import utils.DownLoadImage;

public class TurnPageParentView extends View implements InitFactory {
    protected List<Bitmap> ImageBitmapDatas = new ArrayList<>();
    protected PicturesPageFactory pageFactory;
    protected int type;//图片类型 0代表工程目录下的图片，1代表拿手机本地里面的图片，2代表拿网络上的图片
    protected int mWidth=480;
    protected int mHeight=800;
    protected int lastIndex =1;//上一页的索引
    protected  int nextIndex;//下一页的索引
    protected  boolean auto;//是否自动播放
    protected  boolean first=true;//是否是第一次
    protected int turn_page_time; //翻页时间
    protected int turn_page_duration;//翻页间隔时间
    protected boolean turn_page_onTouch;//是否添加手指触摸响应事件
    protected  Context mContext;
    protected  boolean need_click;//判断是否有点击事件
    protected  int bg_color;//翻转的背面背景色
    private TurnPageViewClickInterface turnPageViewClickInterface;
    private  int currentPosition;
    private  Bitmap currentBitmap;
    private Bitmap nextBitmap;
    private int count;
    public TurnPageParentView(Context context) {
        super(context);
        mContext=context;
    }

    public TurnPageParentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
    }

    public void setTurnPageViewClickInterface(TurnPageViewClickInterface turnPageViewClickInterface) {
        this.turnPageViewClickInterface = turnPageViewClickInterface;
    }

    protected void setImageBitmap(Bitmap currentBitmap, Bitmap nextBitmap) {};

    protected  void onTouchE(MotionEvent event){};

    protected  void cancel(){
        System.gc();
    };
    @Override
    public void initFactory(PicturesPageFactory factory, int type) {
       this. pageFactory=factory;
       this.type=type;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initData();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(turn_page_onTouch && !need_click){
           onTouchE(event);
        }else if(need_click){
           if (event.getAction() == MotionEvent.ACTION_UP){
               if(turnPageViewClickInterface!=null){
                   turnPageViewClickInterface.onPageClick(currentPosition);
                   turnPageViewClickInterface.onPageClick(ImageBitmapDatas,currentPosition);
                   turnPageViewClickInterface.onPageClick(currentBitmap,nextBitmap,currentPosition);
               }
           }
        }
        //必须为true，否则无法获取ACTION_MOVE及ACTION_UP事件
        return true;

    }

    public  void setScreen(int width, int height){
        mWidth=width;
        mHeight=height;
    }
    //初始化数据
    public void initData() {
        count=0;
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        if (ImageBitmapDatas.size() == 0) {
            if (type == 0) {
                int size = pageFactory.getImages().length;
                for (int i = 0; i < size; i++) {
                    Bitmap bitmap = pageFactory.getBitmapByIndex(i, mWidth, mHeight);
                    ImageBitmapDatas.add(bitmap);
                }
            } else{
                //功能待定
                int size = pageFactory.getImagesUri().length;
                for (int i = 0; i < size; i++) {
                    //先用默认的图，防止下载缓慢导致程序的bug
                    Bitmap bitmap = pageFactory.getBitmapByIndex(i, mWidth, mHeight);
                    ImageBitmapDatas.add(bitmap);
                    if(pageFactory.getImagesUri().length>0)
                    new Task().execute(pageFactory.getImagesUri()[i]);
                }
            }
            setImageBitmap(ImageBitmapDatas.get(0),ImageBitmapDatas.get(0));
        }
    }


    //处理由左往右滑的数据
    public void leftToRight() {
        lastIndex--;
        if (lastIndex < 0) {
            lastIndex = ImageBitmapDatas.size() - 1;
        }
        if (nextIndex >= ImageBitmapDatas.size()) {
            nextIndex = 0;
        }
        setImageBitmap(ImageBitmapDatas.get(nextIndex), ImageBitmapDatas.get(lastIndex));
        nextIndex = lastIndex;
        currentPosition=nextIndex;
        currentBitmap=ImageBitmapDatas.get(nextIndex);
        nextBitmap=ImageBitmapDatas.get(lastIndex);
    }
    //处理由左往右滑的数据
    public void rightToLeft() {
        nextIndex++;
        if (lastIndex < 0) {
            lastIndex = ImageBitmapDatas.size() - 1;
        }
        if (nextIndex >= ImageBitmapDatas.size()) {
            nextIndex = 0;
        }
        setImageBitmap(ImageBitmapDatas.get(lastIndex), ImageBitmapDatas.get(nextIndex));
        lastIndex = nextIndex;
        currentPosition=lastIndex;
        currentBitmap=ImageBitmapDatas.get(lastIndex);
        nextBitmap=ImageBitmapDatas.get(nextIndex);
    }
    /**
     * 是否从左边翻向右边
     */
    public boolean DragToRight(int mCornerX) {
        if (mCornerX > 0){
            if(first){
                if(auto){
                    lastIndex=0;
                    nextIndex=-1;
                }else{
                    lastIndex=0;
                    nextIndex=0;
                }
            }
            return false;
        }else{
            if(first){
                if(!auto){
                    lastIndex=0;
                    nextIndex=0;
                }
            }
            return true;
        }
    }
    public InitFactory getFactory(){
        return this;
   }
    //采用异步实现图片的下载
    class Task extends AsyncTask<String, Integer, Void> {
          Bitmap bitmap=null;
        Bitmap bitmap1=null;
        protected Void doInBackground(String... params) {
            if(type==2) {
                bitmap = DownLoadImage.GetImageInputStream((String) params[0]);
            }else{
                bitmap=DownLoadImage.getImageBitmapFromSdcard((String)params[0]);
            }
            if(bitmap==null){
                bitmap1=pageFactory.getBitmapByIndex(count, mWidth, mHeight);
            }else {
                bitmap1= BitmapUtils.changeBitmapSize(bitmap, mWidth, mHeight);
            }
            //用下载好的图片替换原来默认的图片
            ImageBitmapDatas.remove(count);
            ImageBitmapDatas.add(count,bitmap1);
            count++;
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }
    //清除一下数据
    public void clearData(){
        ImageBitmapDatas.clear();
        initData();
    }
}
