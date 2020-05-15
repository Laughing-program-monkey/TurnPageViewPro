package example.com.turnpageviewpro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Interface.CommonCallback;
import adapter.MutiChooseImageAdapter;
import bean.ChatChooseImageBean;
import utils.ImageUtil;

/**
 * Created by cxf on 2018/7/16.
 * 聊天时候选择图片
 */

public class MutiChooseImageActivity extends Activity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private MutiChooseImageAdapter mAdapter;
    private ImageUtil mImageUtil;
    private View mNoData;
    private ArrayList<String> chooseBean;

    public static void forward(Activity context, ArrayList<String> chooseBean) {
        Intent intent = new Intent(context, MutiChooseImageActivity.class);
        intent.putStringArrayListExtra("SELECT_IMAGES_PATH",  chooseBean);
        context.startActivityForResult(intent,0);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_choose_img);
        init();
    }

    public  void init() {
        chooseBean = getIntent().getStringArrayListExtra("SELECT_IMAGES_PATH");
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_send).setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        mNoData = findViewById(R.id.no_data);
        mImageUtil = new ImageUtil();
        mImageUtil.getLocalImageList(new CommonCallback<List<ChatChooseImageBean>>() {
            @Override
            public void callback(List<ChatChooseImageBean> list) {
                if (list.size() == 0) {
                    if (mNoData.getVisibility() != View.VISIBLE) {
                        mNoData.setVisibility(View.VISIBLE);
                    }
                } else {
                    mAdapter = new MutiChooseImageAdapter(MutiChooseImageActivity.this, list,chooseBean);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_cancel){
            onBackPressed();
            return;
        }
        if(v.getId()==R.id.btn_send){
            sendImage();
            return;
        }
    }

    private void sendImage() {
        if (mAdapter != null) {
            List<ChatChooseImageBean> file = mAdapter.getSelectedFile();
            if (file != null && file.size() > 0) {
                Intent intent = new Intent();
                ArrayList<String> tempList = new ArrayList<>();
                for (ChatChooseImageBean  path: file) {
                    tempList.add(path.getImageFile().getAbsolutePath());
                }
                intent.putStringArrayListExtra("SELECT_IMAGES_PATH",  tempList);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this,"请选择图片",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this,"未找到图片",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        mImageUtil.release();
        super.onDestroy();
    }


}
