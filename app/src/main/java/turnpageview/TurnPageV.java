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

public class TurnPageV extends TurnPageNormalParentView {
    private Bitmap foreImage;
    private Bitmap bgImage;
    private Bitmap topBit;
    private Bitmap bottomBit;
    private PointF touchPt;
    private PointF touchPt_1;
    private GradientDrawable shadowDrawableTB;
    private GradientDrawable shadowDrawableBT;
    private ColorMatrixColorFilter mColorMatrixFilter;
    private Scroller mScroller;//从一边翻页到另一边
    private Scroller mScroller_1;//从中间往上下翻页
    private int turn_page_type;
    private Timer timer;//不交由父类处理，方便子类变换
    private int mCornery;
    final int TOP_TO_BOTTOM = 0;
    final int BOTTOM_TO_TOP = 1;
    final int MIDDLE_TO_TOPANDBOTTOM = 2;
    private PageTimer pageTimer;
    private boolean Top_to_Bottom;
    public TurnPageV(Context context) {
        super(context);
        init();
    }

    public TurnPageV(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
        init();
    }

    /*
     * 翻页类型
     * */
    enum TurnPageType {
        TOP_TO_BOTTOM(0),
        BOTTOM_TO_TOP(1),
        MIDDLE_TO_TOPANDBOTTOM(2);

        TurnPageType(int type) {
            TYPE = type;
        }

        ;
        final int TYPE;

        private int getType() {
            return TYPE;
        }
    }

    public void init() {

        touchPt = new PointF(-1, -1);
        touchPt_1=new PointF(-1,-1);
        //ARGB A(0-透明,255-不透明)
        int[] color = {0xb0333333, 0x00333333};

        shadowDrawableBT = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, color);
        shadowDrawableBT.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        shadowDrawableTB = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, color);
        shadowDrawableTB.setGradientType(GradientDrawable.LINEAR_GRADIENT);

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
        TypedArray array = mContext.obtainStyledAttributes(attributeSet, R.styleable.TurnPageV);
        turn_page_type = array.getInt(R.styleable.TurnPageV_direction_v, 0);
        auto = array.getBoolean(R.styleable.TurnPageV_turn_page_auto_v, true);
        turn_page_onTouch = array.getBoolean(R.styleable.TurnPageV_turn_page_onTouch_v, true);
        turn_page_time = array.getInteger(R.styleable.TurnPageV_turn_page_time_v, 3000);
        turn_page_duration = array.getInteger(R.styleable.TurnPageV_turn_page_duration_v, 5000);
        need_click=array.getBoolean(R.styleable.TurnPageV_need_click_v,false);
        bg_color=array.getColor(R.styleable.TurnPageV_bg_color_v,0xfddacab0);
        if(auto){
            if(turn_page_type==TOP_TO_BOTTOM){
                Top_to_Bottom=true;
            }else{
                Top_to_Bottom=false;
            }
        }
        array.recycle();
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
    protected void setImageBitmap(Bitmap currentBitmap, Bitmap nextBitmap) {
        super.setImageBitmap(currentBitmap, nextBitmap);
        foreImage=currentBitmap;
        bgImage=nextBitmap;
        if (turn_page_type == 2) {
            topBit = Bitmap.createBitmap(foreImage, 0, 0, foreImage.getWidth(), foreImage.getHeight() / 2);
            bottomBit = Bitmap.createBitmap(foreImage, 0, foreImage.getHeight() / 2, foreImage.getWidth(), foreImage.getHeight()/2);
        }
    }

    @Override
    public void computeScroll() {
        if(turn_page_type!=MIDDLE_TO_TOPANDBOTTOM){
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

    /**
     * 画翻页效果
     *
     * @param canvas
     */
    @Override
    protected void drawPageEffect(Canvas canvas) {
        // TODO Auto-generated method stub
        drawForceImage(canvas);
        Paint mPaint = new Paint();
        if (touchPt.x != -1 && touchPt.y != -1) {
            switch (turn_page_type) {
                case MIDDLE_TO_TOPANDBOTTOM:
                    drawEffectMiddle(canvas, mPaint);
                    break;
                default:
                    drawEffectSingle(canvas, mPaint);
                    break;
            }
        }
    }


    @Override
    public void onTouchE( MotionEvent event) {
        if(turn_page_type!=MIDDLE_TO_TOPANDBOTTOM) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touchPt.x = event.getX();
                touchPt.y = event.getY();
                abortAnimation();
                calCorner_x();
                if (auto) {
                    if (DragToRight(mCornery) && Top_to_Bottom) {
                        leftToRight();
                    } else {
                        rightToLeft();
                    }
                } else {
                    if (DragToRight(mCornery)) {
                        leftToRight();
                        Top_to_Bottom = true;
                    } else {
                        rightToLeft();
                        Top_to_Bottom = false;
                    }
                }

            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                touchPt.x = event.getX();
                touchPt.y = event.getY();
                postInvalidate();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                startAnimation();
                postInvalidate();
            }
        }

    }

    //翻页动画
    public void startAnimation() {
        int dx=0, dy;

        //向下滑动
        if (Top_to_Bottom) {
            dy = (int) (2.5 * mHeight);
        } else {
            //向上滑动
            dy = -(int) (2.5 * mHeight);
        }
        mScroller.startScroll((int) touchPt.x, (int) touchPt.y, dx, dy, 3000);
        if(turn_page_type==MIDDLE_TO_TOPANDBOTTOM) {
            int dy1;
            dy1 = (int) (2.5 * mHeight);
            mScroller_1.startScroll((int) touchPt_1.x, (int) touchPt_1.y, dx, dy1, turn_page_time);
        }
        first = false;
    }

    //动态的设置翻页类型
    public void setTurnPageType(TurnPageType turnPageType) {
        turn_page_type = turnPageType.getType();
    }

    //暂停动画
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
        if (touchPt.y < mHeight / 2) {
            mCornery = 0;
        } else {
            mCornery = mHeight;
        }
    }

    //设置一个自动翻页的定时器
    class PageTimer extends TimerTask {

        @Override
        public void run() {
            setTouchXYByType();
            if (first) {
                lastIndex = 0;//解决刚开始状态下的图片二次翻页的问题
            }
            if (DragToRight(mCornery) && Top_to_Bottom) {
                leftToRight();//由上往下翻页
            } else {
                rightToLeft();//由下往上翻页
            }
            postInvalidate();
            startAnimation();
        }
    }

    //根据翻页类型设定Touch.x.y
    public void setTouchXYByType() {
        switch (turn_page_type) {
            case TOP_TO_BOTTOM:
                touchPt.y = mHeight/6;
                touchPt.x = mWidth / 2;
                break;
            case BOTTOM_TO_TOP:
                touchPt.y = mHeight -  mHeight/6;
                touchPt.x = mWidth / 2;
                break;
            case MIDDLE_TO_TOPANDBOTTOM:
                touchPt.y = mHeight / 2 - 50;
                touchPt.x = mWidth / 2;
                touchPt_1.x = mWidth / 2;
                touchPt_1.y = mHeight / 2 + 50;
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

    //画从一边翻页到另一边的效果
    @Override
    protected void drawEffectSingle(Canvas canvas,Paint mPaint){
        super.drawEffectSingle(canvas,mPaint);
        //翻页顶侧书边
        canvas.drawLine(0, touchPt.y, mWidth, touchPt.y, mPaint);
        //顶侧书边画阴影
        if (Top_to_Bottom) {
            shadowDrawableTB.setBounds(0, (int) touchPt.y, mWidth, (int) touchPt.y + 20);
            shadowDrawableTB.draw(canvas);
        } else {
            shadowDrawableBT.setBounds(0, (int) touchPt.y - 20, mWidth, (int) touchPt.y);
            shadowDrawableBT.draw(canvas);
        }
        //翻页对折处
        float halfCut;
        if (Top_to_Bottom) {
            halfCut = touchPt.y / 2;
        } else {
            halfCut = touchPt.y + (mHeight - touchPt.y) / 2;
        }

        canvas.drawLine(0, halfCut, mWidth, halfCut, mPaint);

        //对折处顶侧画翻页页图片背面
        Rect backArea = null;
        if (Top_to_Bottom) {
            backArea = new Rect(0, (int) halfCut, mWidth, (int) touchPt.y);
        } else {
            backArea = new Rect(0, (int) touchPt.y, mWidth, (int) halfCut);
        }

        Paint backPaint = new Paint();
        backPaint.setColor(bg_color);
        canvas.drawRect(backArea, backPaint);

        //将翻页图片正面进行处理垂直翻转并平移到touchPt.y点
        Paint fbPaint = new Paint();
        fbPaint.setColorFilter(mColorMatrixFilter);
        Matrix matrix = new Matrix();

        matrix.preScale(1, -1);//垂直翻转
        if (Top_to_Bottom) {
            matrix.postTranslate(0, touchPt.y);
        } else {
            matrix.postTranslate(0, foreImage.getHeight() + touchPt.y);
        }
        canvas.save();
        canvas.clipRect(backArea);
        canvas.drawBitmap(foreImage, matrix, fbPaint);
        canvas.restore();

        //对折处画上侧阴影
        if (Top_to_Bottom) {
            shadowDrawableTB.setBounds(0, (int) halfCut, mWidth, (int) halfCut + 50);
            shadowDrawableTB.draw(canvas);
        } else {
            shadowDrawableBT.setBounds(0, (int) halfCut - 50, mWidth, (int) halfCut);
            shadowDrawableBT.draw(canvas);
        }

        Path bgPath = new Path();

        //可以显示背景图的区域
        if (Top_to_Bottom) {
            bgPath.addRect(new RectF(0, 0, mWidth, halfCut), Path.Direction.CW);
        } else {
            bgPath.addRect(new RectF(0, halfCut, mWidth, mHeight), Path.Direction.CW);
        }
        //对折出下侧画背景
        drawBgImage(canvas, bgPath);
        //对折处画下侧阴影
        if (Top_to_Bottom) {
            shadowDrawableBT.setBounds(0, (int) halfCut - 50, mWidth, (int) halfCut);
            shadowDrawableBT.draw(canvas);
        } else {
            shadowDrawableTB.setBounds(0, (int) halfCut, mWidth, (int) halfCut + 50);
            shadowDrawableTB.draw(canvas);
        }
    }

    //画中间翻页效果
    @Override
    protected void drawEffectMiddle(Canvas canvas, Paint mPaint) {
        super.drawEffectMiddle(canvas, mPaint);
        //翻转上半页的书边
        canvas.drawLine(0, touchPt.y, mWidth, touchPt.y, mPaint);

        //翻转下半页的书边
        canvas.drawLine(0, touchPt_1.y, mWidth, touchPt_1.y, mPaint);

        //翻转上半页的书边阴影
        shadowDrawableBT.setBounds(0, (int) touchPt.y - 20, mWidth, (int) touchPt.y);
        shadowDrawableBT.draw(canvas);

        //翻转下半页的书边阴影
        shadowDrawableTB.setBounds(0, (int) touchPt_1.y, mWidth, (int) touchPt_1.y + 20);
        shadowDrawableTB.draw(canvas);

        float halfCut;//上半侧翻页对折处
        float halfCut_1;//下半侧翻页对折处

        halfCut = touchPt.y+ (mHeight / 2 - touchPt.y) / 2;
        halfCut_1 = mHeight / 2 + (touchPt_1.y - mHeight / 2) / 2;

        //上半侧翻页对折处画线
        canvas.drawLine(0, halfCut, mWidth, halfCut, mPaint);

        //下半侧翻页对折处画线
        canvas.drawLine(0, halfCut_1, mWidth, halfCut_1, mPaint);

        //翻转页的页背景
        Rect backArea;
        backArea = new Rect(0, (int) touchPt.y, mWidth, (int) halfCut);
        Paint backPaint = new Paint();
        backPaint.setColor(bg_color);
        canvas.drawRect(backArea, backPaint);

        //画上半侧翻转页的效果图
        Paint fbPaint = new Paint();
        fbPaint.setColorFilter(mColorMatrixFilter);
        //将翻页图片正面进行处理垂直翻转并平移到topBit.getHeight() / 2 + touchPt.y点
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);//垂直翻转
        matrix.postTranslate(0, topBit.getHeight() / 2 + touchPt.y);
        canvas.save();
        canvas.clipRect(backArea);
        canvas.drawBitmap(topBit, matrix, fbPaint);
        canvas.restore();

        //画下半侧翻转页的效果图
        backArea = new Rect(0, (int) halfCut_1, mWidth, (int) touchPt_1.y);
        canvas.drawRect(backArea, backPaint);

        //将翻页图片正面进行处理垂直翻转并平移到touchPt_1.y点
        Matrix matrix1 = new Matrix();
        matrix1.preScale(1, -1);//垂直翻转
        matrix1.postTranslate(0, touchPt_1.y);
        canvas.save();
        canvas.clipRect(backArea);
        canvas.drawBitmap(bottomBit, matrix1, fbPaint);
        canvas.restore();

        //画上半测对折处左侧阴影
        shadowDrawableBT.setBounds(0, (int) halfCut - 50, mWidth, (int) halfCut);
        shadowDrawableBT.draw(canvas);

        //画下半测对折处右侧阴影
        shadowDrawableTB.setBounds(0, (int) halfCut_1, mWidth, (int) halfCut_1 + 50);
        shadowDrawableTB.draw(canvas);

        //画上侧翻起页面之后的背景图的区域
        Path bgPath = new Path();
        bgPath.addRect(new RectF(0, halfCut, mWidth, mHeight / 2), Path.Direction.CW);
        drawBgImage(canvas, bgPath);

        //画下侧翻起页面之后的背景图的区域
        Path bgPath1 = new Path();
        bgPath1.addRect(new RectF(0, mHeight / 2, mWidth, halfCut_1), Path.Direction.CW);
        drawBgImage(canvas, bgPath1);

        //画上半侧对折处上侧阴影
        shadowDrawableTB.setBounds(0, (int) halfCut , mWidth, (int) halfCut+50);
        shadowDrawableTB.draw(canvas);

        //画下半侧对折处下侧阴影
        shadowDrawableBT.setBounds(0, (int) halfCut_1-50, mWidth, (int) halfCut_1);
        shadowDrawableBT.draw(canvas);
    }
}
