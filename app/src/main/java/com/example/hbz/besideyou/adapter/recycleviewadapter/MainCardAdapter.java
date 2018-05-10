package com.example.hbz.besideyou.adapter.recycleviewadapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.adapter.recycleviewadapter.listener.OnItemClickListener;
import com.example.hbz.besideyou.bean.CardItemBean;

import java.util.List;

/**
 * @ClassName: com.example.besideyou.adapter
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/13 23:54
 */

public class MainCardAdapter extends RecyclerView.Adapter<MainCardAdapter.ViewHolder> {

    private List<CardItemBean> cardList;
    private OnItemClickListener onItemClickListener;

    public MainCardAdapter(List<CardItemBean> cardList) {
        this.cardList = cardList;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (cardList == null || position >= cardList.size()) {
            return;
        }
        CardItemBean cardItemBean = cardList.get(position);
        if (cardItemBean == null) {
            return;
        }
        // 设置图片
        holder.iv_img.setImageResource(cardItemBean.getImgId());
        // 设置标题
        if (!TextUtils.isEmpty(cardItemBean.getTitle())) {
            holder.tv_title.setText(cardItemBean.getTitle());
        }
        // 设置副标题
        if (!TextUtils.isEmpty(cardItemBean.getSubhead())) {
            holder.tv_subhead.setText(cardItemBean.getSubhead());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, (int) getItemId(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList != null ? cardList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_img;
        private TextView tv_title;
        private TextView tv_subhead;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_img = itemView.findViewById(R.id.iv_img);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_subhead = itemView.findViewById(R.id.tv_subhead);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
