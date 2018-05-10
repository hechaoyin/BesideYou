package com.example.hbz.besideyou.adapter.recycleviewadapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hbz.besideyou.R;
import com.example.hbz.besideyou.adapter.recycleviewadapter.listener.OnItemClickListener;
import com.example.hbz.besideyou.bean.CardItemBean;
import com.example.hbz.besideyou.bean.ContactsBean;
import com.example.hbz.besideyou.bean.MultipleItemBean;

import java.util.List;

/**
 * @ClassName: com.example.hbz.besideyou.adapter.recycleviewadapter
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/26 17:30
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    public static final int CONTACT_ITEM_STYLE_FRIEND = 1; //好友联系人
    public static final int CONTACT_ITEM_STYLE_TOP = 2; //新朋友，群聊

    List<MultipleItemBean> contacts;
    private OnItemClickListener onItemClickListener;

    public ContactsAdapter(List<MultipleItemBean> contacts) {
        this.contacts = contacts;
    }

    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case CONTACT_ITEM_STYLE_FRIEND:
            case CONTACT_ITEM_STYLE_TOP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
                break;
            default:
                break;
        }
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position) {

        if (holder == null || position >= contacts.size()) {
            return;
        }
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, (Integer) v.getTag());
            }
        });
        MultipleItemBean multipleItemBean = contacts.get(position);

        switch (multipleItemBean.getStyle()) {
            case CONTACT_ITEM_STYLE_FRIEND:
                if (multipleItemBean.getData() instanceof ContactsBean) {
                    ContactsBean contactsBean = (ContactsBean) multipleItemBean.getData();
                    // 设置头像
                    if (!TextUtils.isEmpty(contactsBean.getFaceUrl())) {
                        Glide.with(holder.itemView.getContext()).
                                load(contactsBean.getFaceUrl()).
                                apply((new RequestOptions()).placeholder(R.drawable.ic_login_face)).
                                into(holder.iv_user_face);
                    } else if (contactsBean.getIdentifier().contains("test")) {//如果是测试账号没有图片就加载测试图片
                        Glide.with(holder.itemView.getContext()).
                                load("http://hebizhi-1252028025.cosgz.myqcloud.com/face/" + contactsBean.getIdentifier() + ".jpg").
                                apply((new RequestOptions()).placeholder(R.drawable.ic_login_face)).
                                into(holder.iv_user_face);
                    } else {
                        holder.iv_user_face.setImageResource(R.drawable.ic_login_face);
                    }
                    // 设置名字
                    if (!TextUtils.isEmpty(contactsBean.getName())) {
                        holder.tv_user_name.setText(contactsBean.getName());
                    }
                    // 下划线
                }
                break;
            case CONTACT_ITEM_STYLE_TOP:
                if (multipleItemBean.getData() instanceof CardItemBean) {
                    CardItemBean cardItemBean = (CardItemBean) multipleItemBean.getData();
                    holder.iv_user_face.setImageResource(cardItemBean.getImgId());
                    holder.tv_user_name.setText(cardItemBean.getTitle());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (contacts == null || contacts.size() <= position) {
            return super.getItemViewType(position);
        }
        MultipleItemBean multipleItemBean = contacts.get(position);
        if (multipleItemBean == null) {
            return super.getItemViewType(position);
        }
        return multipleItemBean.getStyle();
    }

    @Override
    public int getItemCount() {
        return contacts != null ? contacts.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_user_face;
        TextView tv_user_name;
        View halving_line;

        ViewHolder(View itemView, int viewType) {
            super(itemView);
            iv_user_face = itemView.findViewById(R.id.iv_user_face);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            halving_line = itemView.findViewById(R.id.halving_line);
        }
    }

}
