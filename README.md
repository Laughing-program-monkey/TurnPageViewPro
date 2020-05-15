## 简介 
### 自定义可以有多种翻页样式的view
> 1：支持从左上角，右上角，左下角，右下角开始翻页；  
> 2：支持水平从左往右，从右往左，从中间往两边开始翻页；  
> 3：支持垂直从上到下，从下到上，从中间往两端开始翻页；   
> 4：支持手动，自动翻页；  
> 5: 翻页用的图片资源可以来自项目目录下的资源文件夹，也支持根据网络路径异步下载，同时还可以获取本地图库资源；
### HOW TO USE ?
#### first（添加maven）
` allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  `  
#### next(添加依赖)
`  dependencies {
	        implementation 'com.github.Laughing-program-monkey:TurnPageViewPro:版本号'
	}
  ` 
  
### Introducing attributes
Attribute  | Describe  | Type | Default value | Must
---- | ----- |  --- | ---- | -----
direction  | 翻页的方式 |  enum  | ---- | NO
turn_page_time  | 翻页所要的时间 |  integer  | 3000 | NO
turn_page_duration  | 前一次翻页和后一次翻页的时间间隔 |  integer  | 5000 | NO
turn_page_auto  | 是否自动翻页 |  boolean  | true | NO
turn_page_onTouch  | 是否允许触摸翻页 |  boolean  | false | NO
need_click  | 是否需要添加点击事件 |  boolean  | false | NO
bg_color  | 绘制翻页背景颜色 |  color  | 0xfddacab0 | NO

### TurnPageView direction values
Attribute  | Describe  | Type | value 
---- | ----- |  --- | ---- 
LeftToRight  | 从左往右翻页 |  integer  | 0
LeftTopToRight  | 从左上角往右翻页 |  integer  | 1
LeftBottomToRight  | 从左下角往右翻页 |  integer  | 2 
RightToLeft  | 从右往左翻页 |  integer  | 3 
RightTopToLeft  | 从右上角往左翻页 |  integer  | 4 
RightBottomToLeft  | 从右下角往左翻页 |  integer  | 5 

### TurnPageH direction values
Attribute  | Describe  | Type | value 
---- | ----- |  --- | ---- 
LeftToRight_h  | 从左往右翻页 |  integer  | 0
RightToLeft_h  | 从右往左翻页 |  integer  | 1
MiddleToLeftAndRight_h  | 从中间往两边翻页 |  integer  | 2 

### TurnPageV direction values
Attribute  | Describe  | Type | value 
---- | ----- |  --- | ---- 
TopToBottom_v  | 从上往下翻页 |  integer  | 0
BottomToTop_v  | 从下往上翻页 |  integer  | 1
MiddleToTopAndBottom_v  | 从中间往两端翻页 |  integer  | 2 

### Layout(以从左往右翻页为例)
```
 <turnpageview.TurnPageView
        android:id="@+id/book_page"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        app:direction="LeftToRight"
        app:turn_page_auto="true"
        app:need_click="false"
        />

```
### Code
``` 
 PicturesPageFactory picturesPageFactory=new PicturesPageFactory(this,images);
// initFactory()方法里面的第二个参数值有三种 0：表示资源来自项目资源文件夹，1：表示来自本地图库资源，2：表示根据网络路径异步下载
 page_widget_1.getFactory().initFactory(picturesPageFactory,2);
//用户若需要加点击事件，则实现TurnPageViewClickInterface接口
 book_page.setTurnPageViewClickInterface(this);
```
