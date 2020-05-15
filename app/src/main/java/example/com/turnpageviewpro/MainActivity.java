package example.com.turnpageviewpro;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Interface.TurnPageViewClickInterface;
import adapter.ViewPagerAdapter;
import factory.PicturesPageFactory;
import turnpageview.TurnPageH;
import turnpageview.TurnPageV;
import turnpageview.TurnPageView;
import utils.ProcessImageUtil;
import utils.ScreenUtils;


public class MainActivity extends AppCompatActivity implements TurnPageViewClickInterface {
    private TurnPageView book_page;
	private TurnPageV page_widget;
	private TurnPageH page_widget_1;
	private List<View> views=new ArrayList<>();
	private  ArrayList<String> imagechooseBean=new ArrayList<>();
    private int imageDatas[]=new int[]{R.mipmap.screen_1,R.mipmap.screen_2,R.mipmap.screen_3,R.mipmap.screen_4};//R.mipmap.ic_launcher,R.drawable.ic_launcher_background,
	private String images[]=new String[]{"https://t00img.yangkeduo.com/goods/images/2020-05-12/448517bb270b7db64f84bbecb6f50149.jpeg",
            "https://t00img.yangkeduo.com/goods/images/2019-11-06/c53ec5c8-f01a-4247-a8c2-be9ba3062c75.jpg",
            "https://t00img.yangkeduo.com/goods/images/2019-07-22/2e178d43-c4e8-4c7e-8fb8-869c0dd5db31.jpg"};
	private ProcessImageUtil mImageUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mImageUtil = new ProcessImageUtil((FragmentActivity) this);
		book_page=(TurnPageView)findViewById(R.id.book_page);
		page_widget=(TurnPageV)findViewById(R.id.page_widget);
		page_widget_1=(TurnPageH) findViewById(R.id.page_widget_1);
		final PicturesPageFactory picturesPageFactory=new PicturesPageFactory(this,images);
		final PicturesPageFactory picturesPageFactory1=new PicturesPageFactory(this,imageDatas);
		page_widget_1.getFactory().initFactory(picturesPageFactory,2);
		page_widget.getFactory().initFactory(picturesPageFactory1,0);
		book_page.getFactory().initFactory(picturesPageFactory,2);
		page_widget_1.setTurnPageViewClickInterface(this);
		page_widget.setTurnPageViewClickInterface(this);
		book_page.setTurnPageViewClickInterface(this);
		for(int i=0;i<3;i++){
			ImageView imageView= (ImageView) LayoutInflater.from(this).inflate(R.layout.view_image,null);
			imageView.setImageResource(imageDatas[i]);
			views.add(imageView);
		}
		findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mImageUtil.requestPermissions(
						new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
								Manifest.permission.WRITE_EXTERNAL_STORAGE},
						new Runnable() {
							@Override
							public void run() {
								imagechooseBean.clear();
								MutiChooseImageActivity.forward(MainActivity.this,imagechooseBean);
							}
						});

			}
		});
	}
	@Override
	public void onPageClick(int position) {
		Log.e("positioncjasdcjsjcsjc",position+"______position");
	}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            imagechooseBean = data.getStringArrayListExtra("SELECT_IMAGES_PATH");
            String str[]=new String[imagechooseBean.size()];
            for(int i=0;i<imagechooseBean.size();i++){
                str[i]=imagechooseBean.get(i);
            }
            if(str.length>0){
                PicturesPageFactory picturesPageFactory=new PicturesPageFactory(this,str);
				book_page.getFactory().initFactory(picturesPageFactory,1);
				book_page.clearData();
            }

        }
    }
}
