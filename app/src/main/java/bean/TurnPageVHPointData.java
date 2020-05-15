package bean;

import android.graphics.drawable.GradientDrawable;

public class TurnPageVHPointData {
    private GradientDrawable shadowDrawableRL;
    private GradientDrawable shadowDrawableLR;
    private GradientDrawable shadowDrawableTB;
    private GradientDrawable shadowDrawableBT;
    public  boolean direction;//翻页方向
    public float touch_x;//触摸点x坐标
    public float touch_y;//触摸点y坐标
    public GradientDrawable shadowDrawable_1;
    public GradientDrawable shadowDrawable_2;
   final  int LEFT_TO_RIGHT=1;
   final  int RIGHT_TO_LEFT=2;
   final  int TOP_TO_BOTTOM=3;
    final  int BOTTOM_TO_TOP=4;
    public int mWidth;
    public int mHeight;
    public  int bitmapWH;
    public  Line_1 line_1;
    public Line_2 line_2;
    public  GradientDrawable_1 gradientDrawable_1;
    public  GradientDrawable_2 gradientDrawable_2;
    public  GradientDrawable_3 gradientDrawable_3;
    public  Rect_1 rect_1;
    public Rect_2 rect_2;
   //线条1的属性
   public  static class Line_1 {
     public  float start_x;
     public  float start_y;
     public  float end_x;
     public  float end_y;
    }
//线条2的属性
public static  class Line_2 {
     public float start_x;
     public float start_y;
     public float end_x;
     public float end_y;
    }
  //渐变drawable1的属性
  public static class GradientDrawable_1 {
     public float bound_start_x;
     public float bound_start_y;
     public float bound_end_x;
     public float bound_end_y;
    }
//渐变drawable2的属性
public static class GradientDrawable_2 {
     public  float bound_start_x;
     public  float bound_start_y;
     public  float bound_end_x;
     public  float bound_end_y;
    }
//渐变drawable3的属性
public static  class GradientDrawable_3 {
    public  float bound_start_x;
    public  float bound_start_y;
    public  float bound_end_x;
    public  float bound_end_y;
    }

    public static class Rect_1{
        public  float rect_start_x;
        public  float rect_start_y;
        public  float rect_end_x;
        public  float rect_end_y;
    }

    public static class  Rect_2{
        public  float rect_start_x;
        public  float rect_start_y;
        public  float rect_end_x;
        public  float rect_end_y;
    }
    public float half_cut;//对折点
    private float matrix_x;//图像坐标x平移点
    private float matrix_y;//图像坐标y平移点

    //基础数据的初始化
    public void init(int mWidth,int mHeight,int bitmapWH){
        this.mWidth=mWidth;
        this.mHeight=mHeight;
        this.bitmapWH=bitmapWH;
        //ARGB A(0-透明,255-不透明)
        int[] color = {0xb0333333, 0x00333333};
        shadowDrawableRL = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, color);
        shadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        shadowDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, color);
        shadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        shadowDrawableBT = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, color);
        shadowDrawableBT.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        shadowDrawableTB = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, color);
        shadowDrawableTB.setGradientType(GradientDrawable.LINEAR_GRADIENT);
    }
    //设置一个计算的方法
    public void cal_point(float touch_x,float touch_y,int direction){
        this.touch_x=touch_x;
        this.touch_y=touch_y;
      switch (direction){
          case LEFT_TO_RIGHT:
              cal_left_right();
              break;
          case RIGHT_TO_LEFT:
              cal_right_left();
              break;
          case TOP_TO_BOTTOM:
              cal_top_bottom();
              break;
          case BOTTOM_TO_TOP:
              cal_bottom_top();
              break;
      }
    }
    //计算从左到右的数据
    public void cal_left_right(){
        line_1.start_x=touch_x;
        line_1.start_y=0;
        line_1.end_x=touch_x;
        line_1.end_y=mHeight;

        half_cut=touch_x/2;

        line_2.start_x=half_cut;
        line_2.start_y=0;
        line_2.end_x=half_cut;
        line_2.end_y=mHeight;

        gradientDrawable_1.bound_start_x=(int) touch_x;
        gradientDrawable_1.bound_start_y=0;
        gradientDrawable_1.bound_end_x=(int) touch_x+20;
        gradientDrawable_1.bound_end_y=mHeight;

        gradientDrawable_2.bound_start_x=half_cut;
        gradientDrawable_2.bound_start_y=0;
        gradientDrawable_2.bound_end_x=half_cut+50;
        gradientDrawable_2.bound_end_y=mHeight;

        gradientDrawable_3.bound_start_x=half_cut-50;
        gradientDrawable_3.bound_start_y=0;
        gradientDrawable_3.bound_end_x=half_cut;
        gradientDrawable_3.bound_end_y=mHeight;

        matrix_x=touch_x;
        matrix_y=0;

        rect_1.rect_start_x=half_cut;
        rect_1.rect_start_y=0;
        rect_1.rect_end_x=touch_x;
        rect_1.rect_end_y=mHeight;

        rect_1.rect_start_x=0;
        rect_1.rect_start_y=0;
        rect_1.rect_end_x=half_cut;
        rect_1.rect_end_y=mHeight;
    }
    //计算从右到左的数据
    public void cal_right_left(){


    }
    //计算从上到下的数据
    public void cal_top_bottom(){

    }//计算从下到上的数据
    public void cal_bottom_top(){

    }

}
