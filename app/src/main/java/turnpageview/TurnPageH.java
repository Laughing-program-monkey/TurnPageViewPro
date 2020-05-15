package turnpageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Scroller;

import java.util.Timer;
import java.util.TimerTask;

import example.com.turnpageviewpro.R;
import factory.PicturesPageFactory;

public class TurnPageH extends TurnPageNormalParentView {
    public TurnPageH(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
        init();
    }
    private Bitmap foreImage;
    private Bitmap bgImage;
    private Bitmap leftBitmap;//左半图
    private Bitmap rightBitmap;//右半图
    private PointF touchPt;
    private PointF touchPt_1;
    private GradientDrawable shadowDrawableRL;
    private GradientDrawable shadowDrawableLR;
    private ColorMatrixColorFilter mColorMatrixFilter;
    private Scroller mScroller;//从一边翻页到另一边
    private Scroller mScroller_1;//从中间往两边翻页
    private Timer timer;
    private PageTimer pageTimer;
    private int mCornerx;
    private int turn_page_type;
    final int LEFT_TOP_RIGHT = 0;
    final int RIGHT_TOP_LEFT = 1;
    final int MIDDLE_TO_LEFTANDRIGHT = 2;
    private boolean Left_to_Right;
    public TurnPageH(Context context) {
        super(context);
        init();
    }

    /*
     * 翻页类型
     * */
    enum TurnPageType {
        LEFT_TO_RIGHT(0),
        RIGHT_TO_LEFT(1),
        MIDDLE_TO_LEFTANDRIGHT(2);

        TurnPageType(int type) {
            TYPE = type;
        };
        final int TYPE;

        private int getType() {
            return TYPE;
        }
    }
    public void init() {
        // TODO Auto-generated constructor stub
        touchPt = new PointF(-1, -1);
        touchPt_1=new PointF(-1,-1);
//ARGB A(0-透明,255-不透明)
        int[] color = {0xb0333333, 0x00333333};
        shadowDrawableRL = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, color);
        shadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        shadowDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, color);
        shadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);


        float array[] = {0.55f, 0, 0, 0,
                80.0f, 0, 0.55f, 0,
                0, 80.0f, 0, 0,
                0.55f, 0, 80.0f, 0,
                0, 0, 0.2f, 0};
        ColorMatrix cm = new ColorMatrix();
        cm.set(array);
        mColorMatrixFilter = new ColorMatrixColorFilter(cm);

//利用滚动条来实现接触点放开后的动画效果
        mScroller = new Scroller(mContext);
        mScroller_1=new Scroller(mContext);
    }

    /*
     * 初始化属性值
     *
     * */
    public void initAttr(AttributeSet attributeSet) {
        TypedArray array = mContext.obtainStyledAttributes(attributeSet, R.styleable.TurnPageH);
        turn_page_type = array.getInt(R.styleable.TurnPageH_direction_h, 0);
        auto = array.getBoolean(R.styleable.TurnPageH_turn_page_auto_h, true);
        turn_page_onTouch = array.getBoolean(R.styleable.TurnPageH_turn_page_onTouch_h, true);
        turn_page_time = array.getInteger(R.styleable.TurnPageH_turn_page_time_h, 3000);
        turn_page_duration = array.getInteger(R.styleable.TurnPageH_turn_page_duration_h, 5000);
        need_click=array.getBoolean(R.styleable.TurnPageH_need_click_h,false);
        bg_color=array.getColor(R.styleable.TurnPageH_bg_color_h,0xfddacab0);
        if(auto) {
            if (turn_page_type == LEFT_TOP_RIGHT) {
                Left_to_Right = true;
            } else {
                Left_to_Right = false;
            }
        }
        array.recycle();
    }

    @Override
    protected void setImageBitmap(Bitmap currentBitmap, Bitmap nextBitmap) {
        super.setImageBitmap(currentBitmap, nextBitmap);
        foreImage = currentBitmap;
        bgImage = nextBitmap;
        if (turn_page_type == 2) {
            leftBitmap = Bitmap.createBitmap(foreImage, 0, 0, foreImage.getWidth() / 2, foreImage.getHeight());
            rightBitmap = Bitmap.createBitmap(foreImage, foreImage.getWidth() / 2, 0, foreImage.getWidth() / 2, foreImage.getHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (auto && first) {
            timer = new Timer();
            pageTimer = new PageTimer();
            timer.schedule(pageTimer, 0, turn_page_duration);
        }

    }

    @Override
    public void computeScroll() {
        if(turn_page_type!=MIDDLE_TO_LEFTANDRIGHT){
            if (mScroller.computeScrollOffset()) {
                touchPt.x = mScroller.getCurrX();
                touchPt.y = mScroller.getCurrY();
                postInvalidate();
            }
        }else{
            if (mScroller.computeScrollOffset() && mScroller_1.computeScrollOffset()) {
                touchPt.x = mScroller.getCurrX();
                touchPt.y = mScroller.getCurrY();
                touchPt_1.x=mScroller_1.getCurrX();
                touchPt_1.y=mScroller_1.getCurrY();
                postInvalidate();
            }
        }
        super.computeScroll();
    }


    public Bitmap getForeImage() {
        return foreImage;
    }

    public void setForeImage(Bitmap foreImage) {
        this.foreImage = foreImage;
    }

    public Bitmap getBgImage() {
        return bgImage;
    }


    public void setBgImage(Bitmap bgImage) {
        this.bgImage = bgImage;
    }

    /**
     * 画前景图片
     *
     * @param canvas
     */
    private void drawForceImage(Canvas canvas) {
        // TODO Auto-generated method stub
        Paint mPaint = new Paint();

        if (foreImage != null) {
            canvas.drawBitmap(foreImage, 0, 0, mPaint);
        }
    }

    /**
     * 画背景图片
     *
     * @param canvas
     */
    private void drawBgImage(Canvas canvas, Path path) {
        // TODO Auto-generated method stub
        Paint mPaint = new Paint();

        if (bgImage != null) {
            canvas.save();

//只在与路径相交处画图
            canvas.clipPath(path, Region.Op.INTERSECT);
            canvas.drawBitmap(bgImage, 0, 0, mPaint);
            canvas.restore();
        }
    }

    @Override
    protected void drawPageEffect(Canvas canvas) {
        drawForceImage(canvas);
        Paint mPaint = new Paint();
        if (touchPt.x != -1 && touchPt.y != -1) {
            switch (turn_page_type) {
                case MIDDLE_TO_LEFTANDRIGHT:
                    drawEffectMiddle(canvas, mPaint);
                    break;
                default:
                    drawEffectSingle(canvas, mPaint);
                    break;
            }
        }
            super.drawPageEffect(canvas);
    }

    /**
     * 画翻页效果
     *
     * @param
     */

    @Override
    protected void onTouchE(MotionEvent event) {
           if(turn_page_type!=MIDDLE_TO_LEFTANDRIGHT) {
               if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   touchPt.x = event.getX();
                   touchPt.y = event.getY();
                   abortAnimation();
                   calCorner_x();
                   if (auto) {
                       if (DragToRight(mCornerx) && Left_to_Right) {
                           leftToRight();
                       } else {
                           rightToLeft();
                       }
                   } else {
                       if (DragToRight(mCornerx)) {
                           leftToRight();
                           Left_to_Right = true;
                       } else {
                           rightToLeft();
                           Left_to_Right = false;
                       }
                   }
               } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                   touchPt.x = event.getX();
                   touchPt.y = event.getY();
                   postInvalidate();
               } else if (event.getAction() == MotionEvent.ACTION_UP) {
                   startAnimation();
                   postInvalidate();
                   //向右滑动
               }

           }
    }

    @Override
    public void initFactory(PicturesPageFactory factory, int type) {
        this.pageFactory = factory;
        this.type = type;
    }

    //设置一个自动翻页的定时器
    class PageTimer extends TimerTask {

        @Override
        public void run() {
            setTouchXYByType();
            if (first) {
                lastIndex = 0;//解决刚开始状态下的图片二次翻页的问题
            }
            if (DragToRight(mCornerx) && Left_to_Right) {
                leftToRight();
            } else {
                rightToLeft();
            }
            postInvalidate();
            startAnimation();
        }
    }

    //根据翻页类型设定Touch.x.y
    public void setTouchXYByType() {
        switch (turn_page_type) {
            case LEFT_TOP_RIGHT:
                touchPt.x = 10;
                touchPt.y = mHeight / 2;
                break;
            case RIGHT_TOP_LEFT:
                touchPt.x = mWidth - 10;
                touchPt.y = mHeight / 2;
                break;
            case MIDDLE_TO_LEFTANDRIGHT:
                touchPt.x = mWidth / 2 - 50;
                touchPt.y = mHeight / 2;
                touchPt_1.x = mWidth / 2 + 50;
                touchPt_1.y = mHeight / 2;
                //待处理
                break;
        }
    }

    //取消定时器
    @Override
    protected void cancel() {
        super.cancel();
        timer.cancel();
        pageTimer.cancel();
    }

    private void startAnimation() {
        int dx = 0, dy = 0;
        if (Left_to_Right) {
            dx = (int) (2.5 * mWidth);
        } else {
            //向左滑动
            dx = -(int) (2.5 * mWidth);
        }
        mScroller.startScroll((int) touchPt.x, (int) touchPt.y, dx, dy, turn_page_time);
        if(turn_page_type==MIDDLE_TO_LEFTANDRIGHT) {
            int dx1 = 0;
            dx1 = (int) (2.5 * mWidth);
            mScroller_1.startScroll((int) touchPt_1.x, (int) touchPt_1.y, dx1, dy, turn_page_time);
        }
        first = false;
    }

    //动态的设置翻页类型
    public void setTurnPageType(TurnPageType turnPageType) {
        turn_page_type = turnPageType.getType();
    }

    public void abortAnimation() {
        if (!mScroller.isFinished()) {
            //停止动画，与forceFinished(boolean)相反，Scroller滚动到最终x与y位置时中止动画。
            mScroller.abortAnimation();
        }
        if (!mScroller_1.isFinished()) {
            //停止动画，与forceFinished(boolean)相反，Scroller滚动到最终x与y位置时中止动画。
            mScroller_1.abortAnimation();
        }
    }

    //计算corner_x
    public void calCorner_x() {
        if (touchPt.x < mWidth / 2) {
            mCornerx = 0;
        } else {
            mCornerx = mWidth;
        }
    }
    //画从一边翻页到另一边的效果

    @Override
    protected void drawEffectSingle(Canvas canvas, Paint mPaint) {
        super.drawEffectSingle(canvas, mPaint);
        //翻页左侧书边
        canvas.drawLine(touchPt.x, 0, touchPt.x, mHeight, mPaint);

//左侧书边画阴影
        if (Left_to_Right) {
            shadowDrawableLR.setBounds((int) touchPt.x, 0, (int) touchPt.x + 20, mHeight);
            shadowDrawableLR.draw(canvas);
        } else {
            shadowDrawableRL.setBounds((int) touchPt.x - 20, 0, (int) touchPt.x, mHeight);
            shadowDrawableRL.draw(canvas);
        }

//翻页对折处
        float halfCut;
        if (Left_to_Right) {
            halfCut = touchPt.x / 2;
        } else {
            halfCut = touchPt.x + (mWidth - touchPt.x) / 2;
        }

        canvas.drawLine(halfCut, 0, halfCut, mHeight, mPaint);

//对折处左侧画翻页页图片背面
        Rect backArea;
        if (Left_to_Right) {
            backArea = new Rect((int) halfCut, 0, (int) touchPt.x, mHeight);
        } else {
            backArea = new Rect((int) touchPt.x, 0, (int) halfCut, mHeight);
        }
        Paint backPaint = new Paint();
        backPaint.setColor(bg_color);
        canvas.drawRect(backArea, backPaint);

//将翻页图片正面进行处理水平翻转并平移到touchPt.x点
        Paint fbPaint = new Paint();
        fbPaint.setColorFilter(mColorMatrixFilter);
        Matrix matrix = new Matrix();

        matrix.preScale(-1, 1);
        if (Left_to_Right) {
            matrix.postTranslate(touchPt.x, 0);
        } else {
            matrix.postTranslate(foreImage.getWidth() + touchPt.x, 0);
        }


        canvas.save();
        canvas.clipRect(backArea);
        canvas.drawBitmap(foreImage, matrix, fbPaint);
        canvas.restore();

//对折处画左侧阴影
        if (Left_to_Right) {
            shadowDrawableLR.setBounds((int) halfCut, 0, (int) halfCut + 50, mHeight);
            shadowDrawableLR.draw(canvas);
        } else {
            shadowDrawableRL.setBounds((int) halfCut - 50, 0, (int) halfCut, mHeight);
            shadowDrawableRL.draw(canvas);
        }
        Path bgPath = new Path();

//可以显示背景图的区域
        if (Left_to_Right) {
            bgPath.addRect(new RectF(0, 0, halfCut, mHeight), Path.Direction.CW);
        } else {
            bgPath.addRect(new RectF(halfCut, 0, mWidth, mHeight), Path.Direction.CW);
        }


//对折出右侧画背景
        drawBgImage(canvas, bgPath);

//对折处画右侧阴影
        if (Left_to_Right) {
            shadowDrawableRL.setBounds((int) halfCut - 50, 0, (int) halfCut, mHeight);
            shadowDrawableRL.draw(canvas);
        } else {
            shadowDrawableLR.setBounds((int) halfCut, 0, (int) halfCut + 50, mHeight);
            shadowDrawableLR.draw(canvas);
        }
    }
    //画中间翻页效果
    @Override
    protected void drawEffectMiddle(Canvas canvas, Paint mPaint) {
        super.drawEffectMiddle(canvas, mPaint);
        //翻转左半页的书边
        canvas.drawLine(touchPt.x, 0, touchPt.x, mHeight, mPaint);

        //翻转右半页的书边
        canvas.drawLine(touchPt_1.x, 0, touchPt_1.x, mHeight, mPaint);

        //翻转左半页的书边阴影
        shadowDrawableRL.setBounds((int) touchPt.x - 20, 0, (int) touchPt.x, mHeight);
        shadowDrawableRL.draw(canvas);

        //翻转右半页的书边阴影
        shadowDrawableLR.setBounds((int) touchPt_1.x, 0, (int) touchPt_1.x + 20, mHeight);
        shadowDrawableLR.draw(canvas);

        float halfCut;//左半侧翻页对折处
        float halfCut_1;//右半侧翻页对折处
        halfCut = touchPt.x + (mWidth / 2 - touchPt.x) / 2;
        halfCut_1 = mWidth / 2 + (touchPt_1.x - mWidth / 2) / 2;

        //左半侧翻页对折处画线
        canvas.drawLine(halfCut, 0, halfCut, mHeight, mPaint);

        //右半侧翻页对折处画线
        canvas.drawLine(halfCut_1, 0, halfCut_1, mHeight, mPaint);

        //翻转页的页背景
        Rect backArea;
        backArea = new Rect((int) touchPt.x, 0, (int) halfCut, mHeight);
        Paint backPaint = new Paint();
        backPaint.setColor(bg_color);
        canvas.drawRect(backArea, backPaint);

        //画左半侧翻转页的效果图
        Paint fbPaint = new Paint();
        fbPaint.setColorFilter(mColorMatrixFilter);
        //将翻页图片正面进行处理水平翻转并平移到leftBitmap.getWidth()+touchPt.x点
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        matrix.postTranslate(leftBitmap.getWidth()+touchPt.x, 0);
        canvas.save();
        canvas.clipRect(backArea);
        canvas.drawBitmap(leftBitmap, matrix, fbPaint);
        canvas.restore();

        //画右半侧翻转页的效果图
        backArea = new Rect((int) halfCut_1, 0, (int) touchPt_1.x, mHeight);
        canvas.drawRect(backArea, backPaint);
        //将翻页图片正面进行处理水平翻转并平移到touchPt.x点
        Matrix matrix1 = new Matrix();
        matrix1.preScale(-1, 1);
        matrix1.postTranslate(touchPt_1.x, 0);
        canvas.save();
        canvas.clipRect(backArea);
        canvas.drawBitmap(rightBitmap, matrix1, fbPaint);
        canvas.restore();

        //画右半测对折处左侧阴影
        shadowDrawableRL.setBounds((int) halfCut_1 - 50, 0, (int) halfCut_1, mHeight);
        shadowDrawableRL.draw(canvas);

        //画左半测对折处右侧阴影
        shadowDrawableLR.setBounds((int) halfCut, 0, (int) halfCut + 50, mHeight);
        shadowDrawableLR.draw(canvas);

        //画左侧翻起页面之后的背景图的区域
        Path bgPath = new Path();
        bgPath.addRect(new RectF(halfCut, 0, mWidth / 2, mHeight), Path.Direction.CW);
        drawBgImage(canvas, bgPath);

        //画右侧翻起页面之后的背景图的区域
        Path bgPath1 = new Path();
        bgPath1.addRect(new RectF(mWidth / 2, 0, halfCut_1, mHeight), Path.Direction.CW);
        drawBgImage(canvas, bgPath1);

        //画左半侧对折处左侧阴影
        shadowDrawableRL.setBounds((int) halfCut - 50, 0, (int) halfCut, mHeight);
        shadowDrawableRL.draw(canvas);

        //画右半侧对折处右侧阴影
        shadowDrawableLR.setBounds((int) halfCut_1, 0, (int) halfCut_1 + 50, mHeight);
        shadowDrawableLR.draw(canvas);
    }
}
