package com.lucky.deviceinfo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lucky.deviceinfo.R;

import java.util.Map;

public class ShowInfoAdapter extends RecyclerView.Adapter<ShowInfoAdapter.holder> {


    private Context context;
    private Map<String, String> map;
    private int layout;
    private String[] mKeys;

    /**
     * @param context 上下文对象
     * @param map     展示数据
     * @param layout  布局
     */
    public ShowInfoAdapter(Context context, Map<String, String> map, int layout) {
        this.context = context;
        this.map = map;
        this.layout = layout;
        mKeys = map.keySet().toArray(new String[map.size()]);
    }

    public ShowInfoAdapter(Context context, Map<String, String> map) {
        this(context, map, 0);
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 0) {
            return new ShowInfoAdapter.holder(LayoutInflater.from(context).inflate(R.layout.showinfo_item_default, null));
        } else {
            return new ShowInfoAdapter.holder(LayoutInflater.from(context).inflate(layout, null));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int i) {
        String key = mKeys[i];
        String Value = map.get(key);
        holder.tv_key.setText(key);
        holder.tv_value.setText(Value);
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    static class holder extends RecyclerView.ViewHolder {
        public holder(View itemView) {
            super(itemView);
            tv_key = itemView.findViewById(R.id.item_key);
            tv_value = itemView.findViewById(R.id.item_value);
        }

        TextView tv_value;
        TextView tv_key;

    }
}
