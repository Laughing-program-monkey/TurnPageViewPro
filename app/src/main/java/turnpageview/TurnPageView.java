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
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Scroller;

import java.util.Timer;
import java.util.TimerTask;

import example.com.turnpageviewpro.R;

public class TurnPageView extends TurnPageParentView{
    private int mCornerX = 0; // 拖拽点对应的页脚
    private int mCornerY = 0;
    private Path mPath0;
    private Path mPath1;
    Bitmap mCurPageBitmap = null; // 当前页
    Bitmap mNextPageBitmap = null;
    PointF mTouch = new PointF(); // // 拖拽点
    PointF mBezierStart1 = new PointF(); // 贝塞尔曲线起始点
    PointF mBezierControl1 = new PointF(); // 贝塞尔曲线控制点
    PointF mBeziervertex1 = new PointF(); // 贝塞尔曲线顶点
    PointF mBezierEnd1 = new PointF(); // 贝塞尔曲线结束点

    PointF mBezierStart2 = new PointF(); // 另一条贝塞尔曲线
    PointF mBezierControl2 = new PointF();
    PointF mBeziervertex2 = new PointF();
    PointF mBezierEnd2 = new PointF();

    float mMiddleX;
    float mMiddleY;
    float mDegrees;
    float mTouchToCornerDis;
    ColorMatrixColorFilter mColorMatrixFilter;
    Matrix mMatrix;
    float[] mMatrixArray = {0, 0, 0, 0, 0, 0, 0, 0, 1.0f};

    boolean mIsRTandLB; // 是否属于右上左下
    float mMaxLength;
    int[] mBackShadowColors;
    int[] mFrontShadowColors;
    GradientDrawable mBackShadowDrawableLR;
    GradientDrawable mBackShadowDrawableRL;
    GradientDrawable mFolderShadowDrawableLR;
    GradientDrawable mFolderShadowDrawableRL;

    GradientDrawable mFrontShadowDrawableHBT;
    GradientDrawable mFrontShadowDrawableHTB;
    GradientDrawable mFrontShadowDrawableVLR;
    GradientDrawable mFrontShadowDrawableVRL;
    Paint mPaint;
    Scroller mScroller;
    private PageTimer pageTimer;//翻页的定时任务
    private Timer timer;//翻页的定时器
    private int turn_page_type;//翻页类型(从左往右，从左上角往右，从左下角往右，从右往左，从右上角往左，从右小角往左，从顶部往下，从底部往上)
   final int LEFT_RIGHT = 0;
   final int LEFT_TOP_RIGHT = 1;
   final int LEFT_BOTTOM_RIGHT = 2;
   final int RIGHT_LEFT = 3;
   final int RIGHT_TOP_LEFT = 4;
   final int RIGHT_BOTTOM_LEFT = 5;
    public TurnPageView(Context context) {
        super(context);
        init();
    }

    public TurnPageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
        init();
    }
    /*
     * 翻页类型
     * */
    enum TurnPageType {
        LEFT_RIGHT(0),
        LEFT_TOP_RIGHT(1),
        LEFT_BOTTOM_RIGHT(2),
        RIGHT_LEFT(3),
        RIGHT_TOP_LEFT(4),
        RIGHT_BOTTOM_LEFT(5);

        TurnPageType(int type) {
            TYPE = type;
        };
        final int TYPE;
        private int getType(){
            return TYPE;
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

    public void init() {
        mMaxLength = (float) Math.hypot(mWidth, mHeight);
        mPath0 = new Path();//Path路径对象
        mPath1 = new Path();
        createDrawable();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

        //颜色矩阵（ColorMatrix）和坐标变换矩阵（Matrix），对图片进行变换，以拉伸，扭曲等
        ColorMatrix cm = new ColorMatrix();

        //颜色矩阵，颜色矩阵是一个5x4 的矩阵，可以用来方便的修改图片中RGBA各分量的值，颜色矩阵以一维数组的方式存储如下：
        // [ a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t ]，他通过RGBA四个通道来直接操作对应颜色
        float array[] = {0.55f, 0, 0, 0,
                         80.0f, 0, 0.55f,0,
                         0, 80.0f, 0,0,
                         0.55f, 0, 80.0f, 0,
                         0, 0, 0.2f, 0};
        cm.set(array);
        //颜色滤镜，就像QQ的在线和离线图片，同一张图片通过颜色滤镜处理，显示不同的效果，可减少图片资源
        mColorMatrixFilter = new ColorMatrixColorFilter(cm);
        mMatrix = new Matrix();
        mScroller = new Scroller(getContext());
        mTouch.x = 0.01f; // 不让x,y为0,否则在点计算时会有问题
        mTouch.y = 0.01f;
    }

    /*
     * 初始化属性值
     *
     * */

    public void initAttr(AttributeSet attributeSet) {
        TypedArray array = mContext.obtainStyledAttributes(attributeSet, R.styleable.TurnPageView);
        turn_page_type = array.getInt(R.styleable.TurnPageView_direction, 0);
        auto = array.getBoolean(R.styleable.TurnPageView_turn_page_auto, true);
        turn_page_onTouch = array.getBoolean(R.styleable.TurnPageView_turn_page_onTouch, true);
        turn_page_time = array.getInteger(R.styleable.TurnPageView_turn_page_time, 3000);
        turn_page_duration = array.getInteger(R.styleable.TurnPageView_turn_page_duration, 5000);
        need_click=array.getBoolean(R.styleable.TurnPageView_need_click,false);
        bg_color=array.getColor(R.styleable.TurnPageView_bg_color,0xfddacab0);
        array.recycle();
    }

    /**
     * 计算拖拽点对应的拖拽角
     */
    public void calcCornerXY(float x, float y) {
        //将手机屏幕分为四个象限，判断手指落在哪个象限内
            if (x <= mWidth / 2)
                mCornerX = 0;
            else
                mCornerX = mWidth;
            if (y <= mHeight / 2)
                mCornerY = 0;
            else
                mCornerY = mHeight;
        //如果手指落在第一象限或第三象限，也就是右上角或左下角
        if ((mCornerX == 0 && mCornerY == mHeight)
                || (mCornerX == mWidth && mCornerY == 0))
            mIsRTandLB = true;
        else
            mIsRTandLB = false;
    }

    public boolean doTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mTouch.x = event.getX();
            mTouch.y = event.getY();
            /* Android提供了Invalidate和postInvalidate方法实现界面刷新，但是Invalidate不能直接在线程中调用，因为他是违背了单线程模型：
             * Android UI操作并不是线程安全的，并且这些操作必须在UI线程中调用。
             * invalidate()的调用是把之前的旧的view从主UI线程队列中pop掉
             * 而postInvalidate()在工作者线程中被调用
             */
            this.postInvalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mTouch.x = event.getX();
            mTouch.y = event.getY();
            // calcCornerXY(mTouch.x, mTouch.y);
            // this.postInvalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //是否触发翻页
            if (canDragOver()) {
                startAnimation(turn_page_time);
            } else {
                mTouch.x = mCornerX - 0.09f;//如果不能翻页就让mTouch返回没有静止时的状态
                mTouch.y = mCornerY - 0.09f;//- 0.09f是防止mTouch = 800 或mTouch= 0 ,在这些值时会出现BUG
            }

            this.postInvalidate();
        }
        return true;
    }

    /**
     * 求解直线P1P2和直线P3P4的交点坐标
     */
    public PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4) {
        PointF CrossP = new PointF();
        // 二元函数通式： y=ax+b
        float a1 = (P2.y - P1.y) / (P2.x - P1.x);
        float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

        float a2 = (P4.y - P3.y) / (P4.x - P3.x);
        float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
        CrossP.x = (b2 - b1) / (a1 - a2);
        CrossP.y = a1 * CrossP.x + b1;
        return CrossP;
    }

    private void calcPoints() {
        mMiddleX = (mTouch.x + mCornerX) / 2;
        mMiddleY = (mTouch.y + mCornerY) / 2;
        mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
                * (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
        mBezierControl1.y = mCornerY;
        mBezierControl2.x = mCornerX;
        mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
                * (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

        mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x)
                / 2;
        mBezierStart1.y = mCornerY;

        // 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
        // 如果继续翻页，会出现BUG故在此限制
            if (mTouch.x > 0 && mTouch.x < mWidth) {
                if (mBezierStart1.x < 0 || mBezierStart1.x > mWidth) {
                    if (mBezierStart1.x < 0)
                        mBezierStart1.x = mWidth - mBezierStart1.x;

                    float f1 = Math.abs(mCornerX - mTouch.x);
                    float f2 = mWidth * f1 / mBezierStart1.x;
                    mTouch.x = Math.abs(mCornerX - f2);

                    float f3 = Math.abs(mCornerX - mTouch.x)
                            * Math.abs(mCornerY - mTouch.y) / f1;
                    mTouch.y = Math.abs(mCornerY - f3);

                    mMiddleX = (mTouch.x + mCornerX) / 2;
                    mMiddleY = (mTouch.y + mCornerY) / 2;

                    mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
                            * (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
                    mBezierControl1.y = mCornerY;

                    mBezierControl2.x = mCornerX;
                    mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
                            * (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

                    mBezierStart1.x = mBezierControl1.x
                            - (mCornerX - mBezierControl1.x) / 2;
                }
            }
        mBezierStart2.x = mCornerX;
        mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y)
                / 2;

        mTouchToCornerDis = (float) Math.hypot((mTouch.x - mCornerX),
                (mTouch.y - mCornerY));

        mBezierEnd1 = getCross(mTouch, mBezierControl1, mBezierStart1,
                mBezierStart2);
        mBezierEnd2 = getCross(mTouch, mBezierControl2, mBezierStart1,
                mBezierStart2);

        /*
         * mBeziervertex1.x 推导
         * ((mBezierStart1.x+mBezierEnd1.x)/2+mBezierControl1.x)/2 化简等价于
         * (mBezierStart1.x+ 2*mBezierControl1.x+mBezierEnd1.x) / 4
         */
        mBeziervertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4;
        mBeziervertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4;
        mBeziervertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4;
        mBeziervertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4;
    }
    private void drawCurrentPageArea(Canvas canvas, Bitmap bitmap, Path path) {
        mPath0.reset();
        mPath0.moveTo(mBezierStart1.x, mBezierStart1.y);
        mPath0.quadTo(mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
                mBezierEnd1.y);
        mPath0.lineTo(mTouch.x, mTouch.y);
        mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y);
        mPath0.quadTo(mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
                mBezierStart2.y);
        mPath0.lineTo(mCornerX, mCornerY);
        mPath0.close();
        canvas.save();
        canvas.clipPath(path, Region.Op.XOR);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.restore();
    }

    private void drawNextPageAreaAndShadow(Canvas canvas, Bitmap bitmap) {
        mPath1.reset();
        mPath1.moveTo(mBezierStart1.x, mBezierStart1.y);
        mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
        mPath1.lineTo(mBeziervertex2.x, mBeziervertex2.y);
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
        mPath1.lineTo(mCornerX, mCornerY);
        mPath1.close();

        mDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl1.x
                - mCornerX, mBezierControl2.y - mCornerY));
        int leftx;
        int rightx;
        GradientDrawable mBackShadowDrawable;
        if (mIsRTandLB) {
            leftx = (int) (mBezierStart1.x);
            rightx = (int) (mBezierStart1.x + mTouchToCornerDis / 4);
            mBackShadowDrawable = mBackShadowDrawableLR;
        } else {
            leftx = (int) (mBezierStart1.x - mTouchToCornerDis / 4);
            rightx = (int) mBezierStart1.x;
            mBackShadowDrawable = mBackShadowDrawableRL;
        }
        canvas.save();
        canvas.clipPath(mPath0);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
        mBackShadowDrawable.setBounds(leftx, (int) mBezierStart1.y, rightx,
                (int) (mMaxLength + mBezierStart1.y));
        mBackShadowDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void setImageBitmap(Bitmap currentBitmap, Bitmap nextBitmap) {
        super.setImageBitmap(currentBitmap, nextBitmap);
        mCurPageBitmap = currentBitmap;
        mNextPageBitmap = nextBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(bg_color);
        calcPoints();
        drawCurrentPageArea(canvas, mCurPageBitmap, mPath0);
        drawNextPageAreaAndShadow(canvas, mNextPageBitmap);
        drawCurrentPageShadow(canvas);
        drawCurrentBackArea(canvas, mCurPageBitmap);
    }

    /**
     * 创建阴影的GradientDrawable
     */
    private void createDrawable() {

        /*
         * GradientDrawable 支持使用渐变色来绘制图形，通常可以用作Button或是背景图形。

         * GradientDrawable允许指定绘制图形的种类：LINE，OVAL，RECTANGLE或是RING ，颜色渐变支持LINEAR_GRADIENT，RADIAL_GRADIENT 和 SWEEP_GRADIENT。

         * 其中在使用RECTANGLE（矩形），还允许设置矩形四个角为圆角，每个圆角的半径可以分别设置：

         * public void setCornerRadii(float[] radii)

         * radii 数组分别指定四个圆角的半径，每个角可以指定[X_Radius,Y_Radius]，四个圆角的顺序为左上，右上，右下，左下。如果X_Radius,Y_Radius为0表示还是直角。

         * 颜色渐变的方向由GradientDrawable.Orientation定义,共八种

         * GradientDrawable的构造函数：public GradientDrawable(GradientDrawable.Orientation orientation, int[] colors)

         * orientation指定了渐变的方向，渐变的颜色由colors数组指定，数组中的每个值为一个颜色。

         * 本例定义一个渐变方向从组左上到右下，渐变颜色为红，绿，蓝三色：

         * mDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[] { 0xFFFF0000, 0xFF00FF00,0xFF0000FF });

         * 分别使用Liner,Radial 和Sweep三种渐变模式，并可配合指定矩形四个角圆角半径
         * */

        int[] color = {0x333333, 0x333333};

        //从右向左由颜色0x333333渐变为0x333333
        mFolderShadowDrawableRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, color);
        mFolderShadowDrawableRL
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);//线性渐变， "radial"：径向渐变，  "sweep" ：角度渐变

        mFolderShadowDrawableLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, color);
        mFolderShadowDrawableLR
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mBackShadowColors = new int[]{0xff111111, 0x111111};
        mBackShadowDrawableRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors);
        mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mBackShadowDrawableLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
        mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFrontShadowColors = new int[]{0x80111111, 0x111111};
        mFrontShadowDrawableVLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
        mFrontShadowDrawableVLR
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);
        mFrontShadowDrawableVRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors);
        mFrontShadowDrawableVRL
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFrontShadowDrawableHTB = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors);
        mFrontShadowDrawableHTB
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFrontShadowDrawableHBT = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors);
        mFrontShadowDrawableHBT
                .setGradientType(GradientDrawable.LINEAR_GRADIENT);
    }

    /**
     * 绘制翻起页的阴影
     */
    public void drawCurrentPageShadow(Canvas canvas) {
        double degree;
        //计算两点间连线的倾斜角.
        //还可旋转饼图
        if (mIsRTandLB) {
            degree = Math.PI
                    / 4
                    - Math.atan2(mBezierControl1.y - mTouch.y, mTouch.x
                    - mBezierControl1.x);
        } else {
            degree = Math.PI
                    / 4
                    - Math.atan2(mTouch.y - mBezierControl1.y, mTouch.x
                    - mBezierControl1.x);
        }
        // 翻起页阴影顶点与touch点的距离
        double d1 = (float) 25 * 1.414 * Math.cos(degree);
        double d2 = (float) 25 * 1.414 * Math.sin(degree);
        float x = (float) (mTouch.x + d1);
        float y;
        if (mIsRTandLB) {
            y = (float) (mTouch.y + d2);
        } else {
            y = (float) (mTouch.y - d2);
        }
        mPath1.reset();
        mPath1.moveTo(x, y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierControl1.x, mBezierControl1.y);
        mPath1.lineTo(mBezierStart1.x, mBezierStart1.y);
        mPath1.close();
        float rotateDegrees;
        canvas.save();

        canvas.clipPath(mPath0, Region.Op.XOR);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        int leftx;
        int rightx;
        GradientDrawable mCurrentPageShadow;
        if (mIsRTandLB) {
            leftx = (int) (mBezierControl1.x);
            rightx = (int) mBezierControl1.x + 25;
            mCurrentPageShadow = mFrontShadowDrawableVLR;
        } else {
            leftx = (int) (mBezierControl1.x - 25);
            rightx = (int) mBezierControl1.x + 1;
            mCurrentPageShadow = mFrontShadowDrawableVRL;
        }

        rotateDegrees = (float) Math.toDegrees(Math.atan2(mTouch.x
                - mBezierControl1.x, mBezierControl1.y - mTouch.y));
        canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y);
        mCurrentPageShadow.setBounds(leftx,
                (int) (mBezierControl1.y - mMaxLength), rightx,
                (int) (mBezierControl1.y));
        mCurrentPageShadow.draw(canvas);
        canvas.restore();

        mPath1.reset();
        mPath1.moveTo(x, y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierControl2.x, mBezierControl2.y);
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
        mPath1.close();
        canvas.save();
        canvas.clipPath(mPath0, Region.Op.XOR);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        if (mIsRTandLB) {
            leftx = (int) (mBezierControl2.y);
            rightx = (int) (mBezierControl2.y + 25);
            mCurrentPageShadow = mFrontShadowDrawableHTB;
        } else {
            leftx = (int) (mBezierControl2.y - 25);
            rightx = (int) (mBezierControl2.y + 1);
            mCurrentPageShadow = mFrontShadowDrawableHBT;
        }
        rotateDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl2.y
                - mTouch.y, mBezierControl2.x - mTouch.x));
        canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y);
        float temp;
        if (mBezierControl2.y < 0)
            temp = mBezierControl2.y - mHeight;
        else
            temp = mBezierControl2.y;

        int hmg = (int) Math.hypot(mBezierControl2.x, temp);
        if (hmg > mMaxLength)
            mCurrentPageShadow
                    .setBounds((int) (mBezierControl2.x - 25) - hmg, leftx,
                            (int) (mBezierControl2.x + mMaxLength) - hmg,
                            rightx);
        else
            mCurrentPageShadow.setBounds(
                    (int) (mBezierControl2.x - mMaxLength), leftx,
                    (int) (mBezierControl2.x), rightx);

        mCurrentPageShadow.draw(canvas);
        canvas.restore();
    }
    /**
     * 绘制翻起页背面
     */
    private void drawCurrentBackArea(Canvas canvas, Bitmap bitmap) {
        int i = (int) (mBezierStart1.x + mBezierControl1.x) / 2;
        float f1 = Math.abs(i - mBezierControl1.x);
        int i1 = (int) (mBezierStart2.y + mBezierControl2.y) / 2;
        float f2 = Math.abs(i1 - mBezierControl2.y);
        float f3 = Math.min(f1, f2);
        mPath1.reset();
        mPath1.moveTo(mBeziervertex2.x, mBeziervertex2.y);
        mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
        mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y);
        mPath1.close();
        GradientDrawable mFolderShadowDrawable;
        int left;
        int right;
        if (mIsRTandLB) {
            left = (int) (mBezierStart1.x - 1);
            right = (int) (mBezierStart1.x + f3 + 1);
            mFolderShadowDrawable = mFolderShadowDrawableLR;
        } else {
            left = (int) (mBezierStart1.x - f3 - 1);
            right = (int) (mBezierStart1.x + 1);
            mFolderShadowDrawable = mFolderShadowDrawableRL;
        }
        canvas.save();
        canvas.clipPath(mPath0);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);

        mPaint.setColorFilter(mColorMatrixFilter);

        float dis = (float) Math.hypot(mCornerX - mBezierControl1.x,
                mBezierControl2.y - mCornerY);
        float f8 = (mCornerX - mBezierControl1.x) / dis;
        float f9 = (mBezierControl2.y - mCornerY) / dis;
        mMatrixArray[0] = 1 - 2 * f9 * f9;
        mMatrixArray[1] = 2 * f8 * f9;
        mMatrixArray[3] = mMatrixArray[1];
        mMatrixArray[4] = 1 - 2 * f8 * f8;
        mMatrix.reset();
        mMatrix.setValues(mMatrixArray);
        mMatrix.preTranslate(-mBezierControl1.x, -mBezierControl1.y);
        mMatrix.postTranslate(mBezierControl1.x, mBezierControl1.y);
        canvas.drawBitmap(bitmap, mMatrix, mPaint);
        // canvas.drawBitmap(bitmap, mMatrix, null);
        mPaint.setColorFilter(null);
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
        mFolderShadowDrawable.setBounds(left, (int) mBezierStart1.y, right,
                (int) (mBezierStart1.y + mMaxLength));
        mFolderShadowDrawable.draw(canvas);
        canvas.restore();
    }

    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();
            mTouch.x = x;
            mTouch.y = y;
            postInvalidate();
        }
    }

    private void startAnimation(int delayMillis) {
        int dx=0, dy;
        // dx 水平方向滑动的距离，负值会使滚动向左滚动
        // dy 垂直方向滑动的距离，负值会使滚动向上滚动
        if (mCornerX > 0) {
            dx = -(int) (mWidth + mTouch.x);
        } else {
            dx = (int) (mWidth - mTouch.x + mWidth);
        }
        if (mCornerY > 0) {
            dy = (int) (1 - mTouch.y-mWidth/6);
        } else {
            dy = (int) (1 - mTouch.y); // // 防止mTouch.y最终变为0
        }

        //Start scrolling by providing a starting point and the distance to travel.
        mScroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy,
                delayMillis);
        first = false;
    }

    public void abortAnimation() {
        if (!mScroller.isFinished()) {
            //停止动画，与forceFinished(boolean)相反，Scroller滚动到最终x与y位置时中止动画。
            mScroller.abortAnimation();
        }
    }

    public boolean canDragOver() {
        //设置开始翻页的条件
//      if (mTouchToCornerDis > mWidth / 10)
        if (mTouchToCornerDis > 1)
            return true;
        return false;
    }



    @Override
    public void onTouchE( MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            mTouch.x = event.getX();
            mTouch.y = event.getY();
            //停止动画。与forceFinished(boolean)相反，Scroller滚动到最终x与y位置时中止动画。
            abortAnimation();
            //计算拖拽点对应的拖拽角
            calcCornerXY(event.getX(), event.getY());
            if (DragToRight(mCornerX)) {
                leftToRight();
            } else {
                rightToLeft();
            }

        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mTouch.x = event.getX();
            mTouch.y = event.getY();
            /* Android提供了Invalidate和postInvalidate方法实现界面刷新，但是Invalidate不能直接在线程中调用，因为他是违背了单线程模型：
             * Android UI操作并不是线程安全的，并且这些操作必须在UI线程中调用。
             * invalidate()的调用是把之前的旧的view从主UI线程队列中pop掉
             * 而postInvalidate()在工作者线程中被调用
             */
            this.postInvalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //是否触发翻页
            if (canDragOver()) {
                startAnimation(turn_page_time);
            } else {
                mTouch.x = mCornerX - 0.09f;//如果不能翻页就让mTouch返回没有静止时的状态
                mTouch.y = mCornerY - 0.09f;//- 0.09f是防止mTouch = 800 或mTouch= 0 ,在这些值时会出现BUG
            }
            this.postInvalidate();
        }
    }

    //设置一个自动翻页的定时器
    class PageTimer extends TimerTask {

        @Override
        public void run() {
            setTouchXYByType();
            calcCornerXY(mTouch.x, mTouch.y);
            if (DragToRight(mCornerX)) {
                leftToRight();
            } else {
                rightToLeft();
            }
            postInvalidate();
            if (canDragOver()) {
                startAnimation(turn_page_time);
            }
            first=false;
        }
    }

    //取消定时器
    @Override
    protected void cancel() {
        super.cancel();
        timer.cancel();
        pageTimer.cancel();

    }

    //根据翻页类型设定Touch.x.y
    public void setTouchXYByType() {
        switch (turn_page_type) {
            case LEFT_RIGHT:
                mTouch.x = 0;
                mTouch.y = 10;
                break;
            case LEFT_TOP_RIGHT:
                if (first) {
                    mTouch.x = 0;
                    mTouch.y = 10;
                } else {
                    mTouch.x = mWidth / (4 * 3);
                    mTouch.y = mWidth / 4;
                }
                break;
            case LEFT_BOTTOM_RIGHT:
                if (first) {
                    mTouch.x = 0;
                    mTouch.y = 10;
                } else {
                    mTouch.x = mWidth / (4 * 3);
                    mTouch.y = mHeight-mWidth / 4;
                }
                break;
            case RIGHT_LEFT:

                    mTouch.x = mWidth;
                    mTouch.y = 10;
                break;
            case RIGHT_TOP_LEFT:
                if (first) {
                    mTouch.x = mWidth;
                    mTouch.y = 10;
                } else {
                    mTouch.x = mWidth-mWidth / (4 * 3);
                    mTouch.y = mWidth / 4;
                }
                break;
            case RIGHT_BOTTOM_LEFT:
                if (first) {
                    mTouch.x = mWidth;
                    mTouch.y = 10;
                } else {
                    mTouch.x = mWidth-mWidth / (4 * 3);
                    mTouch.y = mHeight-mWidth / 4;
                }
                break;
        }
    }
    //动态的设置翻页类型
    public void setTurnPageType(TurnPageType turnPageType){
        turn_page_type=turnPageType.getType();
    }

}
