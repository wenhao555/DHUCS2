package com.example.dhucs.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dhucs.listeners.OnItemClickListener;
import com.example.dhucs.listeners.OnItemLongClickListener;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder> {

    private OnItemClickListener onItemClickListener;
    private com.example.dhucs.listeners.OnItemLongClickListener onItemLongClickListener;

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
        if (onItemClickListener != null) {
            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }
        if (onItemLongClickListener != null) {
            holder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.onLongItemClick(position);
                    return true;
                }
            });
        }
        onBindView(holder, position);
    }

    /**
     * 为itemview绑定数据
     *
     * @param holder   viewholder
     * @param position itemview的位置
     */
    protected abstract void onBindView(@NonNull BaseViewHolder holder, @NonNull int position);

    @Override
    public int getItemViewType(int position) {
        return getLayoutResId(position);
    }

    /**
     * 获取recyclerview itemview布局资源文件的id
     *
     * @param position itemview的位置
     * @return itemview对应的布局资源文件
     */
    protected abstract int getLayoutResId(int position);

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }


    public class BaseViewHolder extends RecyclerView.ViewHolder {
        protected final SparseArray<View> mViews;
        protected View mConvertView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            mViews = new SparseArray<>();
            mConvertView = itemView;
        }

        /**
         * 通过控件的Id获取对应的控件，如果没有则加入mViews，则从item根控件中查找并保存到mViews中
         *
         * @param viewId
         * @return
         */
        public <T extends View> T getView(@IdRes int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = mConvertView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }

        public View getConvertView() {
            return mConvertView;
        }
    }
}
