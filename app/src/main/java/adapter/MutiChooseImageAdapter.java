package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bean.ChatChooseImageBean;
import example.com.turnpageviewpro.AppContext;
import example.com.turnpageviewpro.R;

/**
 * Created by cxf on 2018/6/20.
 * 聊天时候选择图片的Adapter
 */

public class MutiChooseImageAdapter extends RecyclerView.Adapter<MutiChooseImageAdapter.Vh> {

    private List<ChatChooseImageBean> mList;
    private LayoutInflater mInflater;
    private List<ChatChooseImageBean> curSelects;
    public MutiChooseImageAdapter(Context context, List<ChatChooseImageBean> list, List<String> beforeSelList) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        curSelects = new ArrayList<>();
        if (beforeSelList != null && beforeSelList.size() > 0) {
            for (String bean: beforeSelList) {
                curSelects.add(new ChatChooseImageBean(new File(bean),true));
            }
        }
    }

    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_chat_choose_img, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh vh, int position) {

    }

    @Override
    public void onBindViewHolder(Vh vh, int position, List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    public List<ChatChooseImageBean> getSelectedFile() {
        return curSelects;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mCover;
        ImageView mImg;
        ChatChooseImageBean mBean;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            mCover = (ImageView) itemView.findViewById(R.id.cover);
            mImg = (ImageView) itemView.findViewById(R.id.img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSame(mList.get(mPosition),true)){   //点击的是已经选中的
//                        curSelects.remove(mList.get(mPosition));
                        notifyItemChanged(mPosition,  "payload");
                    }else{
                            curSelects.add(mList.get(mPosition));
                            notifyItemChanged(mPosition, "payload");
                    }
                }
            });
        }

        void setData(ChatChooseImageBean bean, int position, Object payload) {
            mBean = bean;
            mPosition = position;
            if (payload == null) {
                Glide.with(AppContext.Instance).load(bean.getImageFile()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mCover);
            }
            if (isSame(bean,false)) {
                mImg.setImageResource(R.mipmap.icon_checked);
            } else {
                mImg.setImageResource(R.mipmap.icon_checked_none);
            }
        }
    }

    private boolean isSame(ChatChooseImageBean bean, boolean isDeleteSame){
        for (ChatChooseImageBean listBean: curSelects) {
            if(listBean.getImageFile() == null || bean.getImageFile() == null)return false;
            if(listBean.getImageFile().getAbsolutePath().equals(bean.getImageFile().getAbsolutePath())) {
                if(isDeleteSame){
                    curSelects.remove(listBean);
                }
                return true;
            }
        }
        return false;
    }

}
