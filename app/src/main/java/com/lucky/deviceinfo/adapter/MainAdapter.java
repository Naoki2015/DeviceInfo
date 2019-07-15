package com.lucky.deviceinfo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lucky.deviceinfo.R;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private Context context;

    private String[] allItems = {"系统信息", "CPU信息", "电池信息", "存储信息", "SIM卡信息", "网络信息",
            "传感器信息", "其他信息","危险信息"};

//    private int[] allIcons = {R.mipmap.ic_device, R.mipmap.ic_cpu, R.mipmap.ic_battery,
//            R.mipmap.ic_memory, R.mipmap.ic_memory, R.mipmap.ic_memory, R.mipmap.ic_memory,
//            R.mipmap.ic_memory, R.mipmap.ic_memory, R.mipmap.ic_memory};

    public MainAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_item, null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        //viewHolder.iv.setImageResource(menu.getIcon());
        //holder.textView.setText(menu.getName());
        viewHolder.textView.setText(allItems[i]);
        //viewHolder.iv_icon.setImageResource(allIcons[i]);

        //单击
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //触发自定义监听的单击事件
                onItemClickListener.onItemClick(viewHolder.itemView,i);
            }
        });
    }

    public void setOnItemClickListener(MainAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    /**
     * 自定义监听回调，RecyclerView 的 单击和长按事件
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        //void onItemLongClick(View view, int position);
    }

    @Override
    public long getItemId(int position) {
        return allItems.length;
    }

    @Override
    public int getItemCount() {
        return allItems.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        ImageView iv_icon;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_name_tv);
            //imageView = itemView.findViewById(R.id.image);
            //iv_icon = itemView.findViewById(R.id.item_trend_flag);
        }
    }
}
