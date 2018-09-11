package com.lcg.comment.adapter;

import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lcg.comment.BR;
import com.lcg.comment.R;
import com.lcg.comment.bean.ListTitle;
import com.lcg.comment.model.ItemListTitle;
import com.xuebaedu.teacher.adapter.FooterViewHolder;

import java.util.ArrayList;

/**
 * 有标题的RecyclerView.Adapter
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.1
 * @since 2017/10/30 12:00
 */
public class HaveTitleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList data;
    private int mLayoutId, mVariableId;
    private FooterViewHolder mFooterViewHolder;

    public HaveTitleAdapter(ArrayList data, int layoutId, int variableId) {
        this(data, layoutId, variableId, null);
    }

    public HaveTitleAdapter(ArrayList data, int layoutId, int variableId, FooterViewHolder holder) {
        this.data = data;
        mLayoutId = layoutId;
        mVariableId = variableId;
        mFooterViewHolder = holder;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 2) {
            return mFooterViewHolder;
        } else if (viewType == 0 || viewType == 3) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater
                    .from(parent.getContext()), R.layout.item_list_title, parent, false);
            ContentHolder holder = new ContentHolder(binding.getRoot());
            holder.setBinding(binding);
            return holder;
        } else {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater
                    .from(parent.getContext()), mLayoutId, parent, false);
            ContentHolder holder = new ContentHolder(binding.getRoot());
            holder.setBinding(binding);
            return holder;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (data != null) {
            count = data.size();
        }
        if (mFooterViewHolder != null) {
            count += 1;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < data.size()) {
            Object o = data.get(position);
            if (o instanceof ItemListTitle) {
                return 3;
            } else if (o instanceof BaseObservable) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 2;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 1:
                ((ContentHolder) holder).getBinding().setVariable(mVariableId, data.get
                        (position));
                break;
            case 0:
                ItemListTitle title = new ItemListTitle(null);
                Object o = data.get(position);
                if (o instanceof ListTitle) {
                    title.setPrimary(((ListTitle) o).getPrimary());
                    String secondary = ((ListTitle) o).getSecondary();
                    if (TextUtils.isEmpty(secondary)) {
                        title.setSecondary("");
                    } else {
                        title.setSecondary(secondary);
                    }
                } else {
                    title.setPrimary(o.toString());
                    title.setSecondary("");
                }
                ((ContentHolder) holder).getBinding().setVariable(BR.item, title);
                break;
            default:
                ((ContentHolder) holder).getBinding().setVariable(BR.item, data.get
                        (position));
                break;
        }
    }

    public static class ContentHolder extends RecyclerView.ViewHolder {
        ViewDataBinding binding;

        ContentHolder(View itemView) {
            super(itemView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }
    }
}
